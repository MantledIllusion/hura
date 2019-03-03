package com.mantledillusion.injection.hura;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mantledillusion.cache.hydnora.HydnoraCache;
import com.mantledillusion.essentials.concurrency.locks.LockIdentifier;
import com.mantledillusion.essentials.reflection.AnnotationEssentials;
import com.mantledillusion.essentials.reflection.ConstructorEssentials;
import com.mantledillusion.essentials.reflection.MethodEssentials;
import com.mantledillusion.essentials.reflection.TypeEssentials;
import com.mantledillusion.essentials.reflection.AnnotationEssentials.AnnotationOccurrence;
import com.mantledillusion.injection.hura.annotation.instruction.Adjust;
import com.mantledillusion.injection.hura.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.annotation.instruction.Context;
import com.mantledillusion.injection.hura.annotation.property.DefaultValue;
import com.mantledillusion.injection.hura.annotation.injection.Global;
import com.mantledillusion.injection.hura.annotation.injection.Global.SingletonMode;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.property.Matches;
import com.mantledillusion.injection.hura.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.annotation.property.Property;
import com.mantledillusion.injection.hura.exception.InjectionException;

final class ReflectionCache {

	private static final class TypeIdentifier<T> extends LockIdentifier {

		private final Class<T> type;

		public TypeIdentifier(Class<T> type) {
			super(type);
			this.type = type;
		}
	}

	private static final class AnnotatedTypeIdentifier extends LockIdentifier {

		private final Class<?> type;
		private final Class<? extends Annotation> annotationType;

		public AnnotatedTypeIdentifier(Class<?> type, Class<? extends Annotation> annotationType) {
			super(type, annotationType);
			this.type = type;
			this.annotationType = annotationType;
		}
	}

	private static abstract class NonWrappingCache<EntryType, Identifier extends LockIdentifier>
			extends HydnoraCache<EntryType, Identifier> {

		private NonWrappingCache() {
			setWrapRuntimeExceptions(false);
		}
	}
	
	ReflectionCache() {}

	// ###############################################################################################################
	// ######################################### CONSTRUCTOR INJECTION ###############################################
	// ###############################################################################################################

	static final class InjectableConstructor<T> {

		static enum ParamSettingType {
			RESOLVABLE, INJECTABLE, BOTH;
		}

		private final Constructor<T> constructor;
		private final Map<Integer, ResolvingSettings> resolvableParams;
		private final Map<Integer, InjectionSettings<?>> injectableParams;

		private InjectableConstructor(Constructor<T> constructor, Map<Integer, ResolvingSettings> resolvableParams,
				Map<Integer, InjectionSettings<?>> injectableParams) {
			this.constructor = constructor;
			this.resolvableParams = resolvableParams;
			this.injectableParams = injectableParams;
		}

		Constructor<T> getConstructor() {
			return this.constructor;
		}

		int getParamCount() {
			return this.resolvableParams.size() + this.injectableParams.size();
		}

		ParamSettingType getSettingTypeOfParam(int i) {
			return this.resolvableParams.containsKey(i)
					? (this.injectableParams.containsKey(i) ? ParamSettingType.BOTH : ParamSettingType.RESOLVABLE)
					: ParamSettingType.INJECTABLE;
		}

		ResolvingSettings getResolvingSettings(int i) {
			return this.resolvableParams.get(i);
		}

		InjectionSettings<?> getInjectionSettings(int i) {
			return this.injectableParams.get(i);
		}

		T instantiate(Object[] params) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
				InvocationTargetException {
			return this.constructor.newInstance(params);
		}
	}

	private static final class ConstructorCache extends NonWrappingCache<InjectableConstructor<?>, TypeIdentifier<?>> {

		@Override
		protected InjectableConstructor<?> load(TypeIdentifier<?> id) throws Exception {
			Constructor<?> c = find(id.type);
			return new InjectableConstructor<>(c, extractResolvingSettings(c), extractInjectionSettings(c));
		}

		private <T> Constructor<T> find(Class<T> type) {
			Constructor<T> c = null;

			try {
				List<Constructor<T>> constructors = ConstructorEssentials.getDeclaredConstructorsAnnotatedWith(type,
						Construct.class);

				if (constructors.isEmpty()) {
					constructors = ConstructorEssentials.getDeclaredConstructors(type);
					if (constructors.isEmpty()) {
						throw new InjectionException(
								"There is no constructor available to use on the type " + type.getSimpleName());
					} else {
						for (Constructor<T> constructor : constructors) {
							if (InjectionUtils.hasAllParametersDefinable(constructor)) {
								if (c == null) {
									c = constructor;
								} else {
									throw new InjectionException("Multiple constructors found in the type "
											+ type.getSimpleName()
											+ "who either have no parameters or whose parameters are all injectables; use the "
											+ Construct.class.getSimpleName()
											+ " annotation on the one meant to be used for injection.");
								}
							}
						}

						if (c == null) {
							throw new InjectionException("There is no constructor in the type " + type.getSimpleName()
									+ " who either has no parameters or whose parameters are fully annotated; no "
									+ " constructor suitable for injection could be found.");
						} else if (c.getParameterCount() == 0 && !Modifier.isPublic(c.getModifiers())) {
							throw new InjectionException("The only injectables constructor in the type "
									+ type.getSimpleName()
									+ " is a non-public no-args constructor. If this constructor should be used for"
									+ " instantiation during injection, it has to be annotated with the @"
									+ Construct.class.getSimpleName() + " annotation.");
						}
					}
				} else if (constructors.size() == 1) {
					c = constructors.get(0);
				} else {
					throw new InjectionException("Multiple constructors found in the type " + type.getSimpleName()
							+ " being annotated with the " + Construct.class.getSimpleName()
							+ " annotation; it can be only used once per type.");
				}
			} catch (SecurityException e) {
				throw new InjectionException(
						"Unable to retrieve constructor to instantiate " + type.getSimpleName() + " for injection.", e);
			}

			if (!c.isAccessible()) {
				try {
					c.setAccessible(true);
				} catch (SecurityException e) {
					throw new InjectionException("Unable to make constructor " + c.toString() + " of type "
							+ type.getSimpleName() + " accessible.", e);
				}
			}

			return c;
		}

		private Map<Integer, ResolvingSettings> extractResolvingSettings(Constructor<?> c) throws Exception {
			Map<Integer, ResolvingSettings> settings = new HashMap<>();
			Parameter[] parameters = c.getParameters();
			for (int i = 0; i < c.getParameterCount(); i++) {
				if (InjectionUtils.isResolvable(parameters[i])) {
					settings.put(i, retrieveResolvingSettings(parameters[i]));
				}
			}
			return settings;
		}

		private Map<Integer, InjectionSettings<?>> extractInjectionSettings(Constructor<?> c) throws Exception {
			Map<Integer, InjectionSettings<?>> settings = new HashMap<>();
			Parameter[] parameters = c.getParameters();
			for (int i = 0; i < c.getParameterCount(); i++) {
				if (InjectionUtils.isInjectable(parameters[i])) {
					settings.put(i, retrieveInjectionSettings(parameters[i].getType(), parameters[i]));
				}
			}
			return settings;
		}

		@SuppressWarnings("unchecked")
		private <T> InjectableConstructor<T> retrieve(TypeIdentifier<T> id) {
			return (InjectableConstructor<T>) get(id);
		}
	}

	private final ConstructorCache constructorCache = new ConstructorCache();

	static <T> InjectableConstructor<T> getInjectableConstructor(Class<T> type) {
		return determineCacheFor(type).constructorCache.retrieve(new TypeIdentifier<>(type));
	}

	// ###############################################################################################################
	// ############################################ FIELD RESOLVING ##################################################
	// ###############################################################################################################

	static final class ResolvableField {

		private final Field field;
		private final ResolvingSettings settings;

		private ResolvableField(Field field, ResolvingSettings settings) {
			this.field = field;
			this.settings = settings;
		}

		public Field getField() {
			return field;
		}

		public ResolvingSettings getSettings() {
			return settings;
		}
	}

	private static final class ResolvableFieldCache extends NonWrappingCache<List<ResolvableField>, TypeIdentifier<?>> {

		@Override
		protected List<ResolvableField> load(TypeIdentifier<?> id) throws Exception {
			return find(id.type);
		}

		private <T> List<ResolvableField> find(Class<T> type) {
			List<ResolvableField> fields = new ArrayList<>();
			Class<? super T> superType = type.getSuperclass();
			while (superType != null && superType != Object.class) {
				fields.addAll(get(new TypeIdentifier<>(superType)));
				superType = superType.getSuperclass();
			}

			for (Field field : type.getDeclaredFields()) {
				if (InjectionUtils.isResolvable(field)) {
					if (!field.isAccessible()) {
						try {
							field.setAccessible(true);
						} catch (SecurityException e) {
							throw new InjectionException("Unable to gain access to the field '" + field.getName()
									+ "' of the type " + superType.getSimpleName() + " which is inaccessible.", e);
						}
					}

					ResolvingSettings fieldSet = retrieveResolvingSettings(field);

					fields.add(new ResolvableField(field, fieldSet));
				}
			}

			return fields;
		}

		private List<ResolvableField> retrieve(TypeIdentifier<?> id) {
			return get(id);
		}
	}

	private final ResolvableFieldCache resolvableFieldCache = new ResolvableFieldCache();

	static <T> List<ResolvableField> getResolvableFields(Class<T> type) {
		return determineCacheFor(type).resolvableFieldCache.retrieve(new TypeIdentifier<>(type));
	}

	// ###############################################################################################################
	// ############################################ FIELD INJECTION ##################################################
	// ###############################################################################################################

	static final class InjectableField {

		private final Field field;
		private final InjectionSettings<?> settings;

		private InjectableField(Field field, InjectionSettings<?> settings) {
			this.field = field;
			this.settings = settings;
		}

		public Field getField() {
			return field;
		}

		public InjectionSettings<?> getSettings() {
			return settings;
		}
	}

	private static final class InjectableFieldCache extends NonWrappingCache<List<InjectableField>, TypeIdentifier<?>> {

		@Override
		protected List<InjectableField> load(TypeIdentifier<?> id) throws Exception {
			return find(id.type);
		}

		private <T> List<InjectableField> find(Class<T> type) {
			List<InjectableField> fields = new ArrayList<>();
			Class<? super T> superType = type.getSuperclass();
			while (superType != null && superType != Object.class) {
				fields.addAll(get(new TypeIdentifier<>(superType)));
				superType = superType.getSuperclass();
			}

			for (Field field : type.getDeclaredFields()) {
				if (InjectionUtils.isInjectable(field)) {
					if (!field.isAccessible()) {
						try {
							field.setAccessible(true);
						} catch (SecurityException e) {
							throw new InjectionException("Unable to gain access to the field '" + field.getName()
									+ "' of the type " + superType.getSimpleName() + " which is inaccessible.", e);
						}
					}

					InjectionSettings<?> fieldSet = retrieveInjectionSettings(field.getType(), field);

					if (fieldSet.isContext && !fieldSet.isIndependent
							&& fieldSet.singletonMode == SingletonMode.GLOBAL) {
						throw new InjectionException("The type " + field.getType().getSimpleName() + " is "
								+ Context.class.getSimpleName()
								+ " annotated, which makes it a context sensitive type that cannot be injected as "
								+ "a global singleton itself. Nevertheless, it is used as such at the field '"
								+ field.getName() + "' in the type '" + field.getDeclaringClass().getSimpleName()
								+ "'.");
					}

					fields.add(new InjectableField(field, fieldSet));
				}
			}

			return fields;
		}

		private List<InjectableField> retrieve(TypeIdentifier<?> id) {
			return get(id);
		}
	}

	private final InjectableFieldCache injectableFieldCache = new InjectableFieldCache();

	static <T> List<InjectableField> getInjectableFields(Class<T> type) {
		return determineCacheFor(type).injectableFieldCache.retrieve(new TypeIdentifier<>(type));
	}

	// ###############################################################################################################
	// ############################################# ANNOTATED TYPE ##################################################
	// ###############################################################################################################

	private static final class AnnotatedTypeCache extends NonWrappingCache<List<Class<?>>, AnnotatedTypeIdentifier> {

		@Override
		protected List<Class<?>> load(AnnotatedTypeIdentifier id) throws Exception {
			return TypeEssentials.getSuperClassesAnnotatedWith(id.type, id.annotationType);
		}

		private List<Class<?>> retrieve(AnnotatedTypeIdentifier id) {
			return get(id);
		}
	}

	private final AnnotatedTypeCache annotatedTypeCache = new AnnotatedTypeCache();

	static List<Class<?>> getSuperTypesAnnotatedWith(Class<?> type, Class<? extends Annotation> annotationType) {
		return determineCacheFor(type).annotatedTypeCache.retrieve(new AnnotatedTypeIdentifier(type, annotationType));
	}

	// ###############################################################################################################
	// ############################################ ANNOTATED METHOD #################################################
	// ###############################################################################################################

	private static final class AnnotatedMethodCache extends NonWrappingCache<List<Method>, AnnotatedTypeIdentifier> {

		@Override
		protected List<Method> load(AnnotatedTypeIdentifier id) throws Exception {
			return MethodEssentials.getDeclaredMethodsAnnotatedWith(id.type, id.annotationType);
		}

		private List<Method> retrieve(AnnotatedTypeIdentifier id) {
			return get(id);
		}
	}

	private final AnnotatedMethodCache annotatedMethodCache = new AnnotatedMethodCache();

	static List<Method> getMethodsAnnotatedWith(Class<?> type, Class<? extends Annotation> annotationType) {
		return determineCacheFor(type).annotatedMethodCache.retrieve(new AnnotatedTypeIdentifier(type, annotationType));
	}

	// ###############################################################################################################
	// ############################################ ANNOTATED METHOD #################################################
	// ###############################################################################################################

	private static final class AnnotatedAnnotationCache
			extends NonWrappingCache<List<AnnotationOccurrence>, AnnotatedTypeIdentifier> {

		@Override
		protected List<AnnotationOccurrence> load(AnnotatedTypeIdentifier id) throws Exception {
			return AnnotationEssentials.getAnnotationsAnnotatedWith(id.type, id.annotationType);
		}

		private List<AnnotationOccurrence> retrieve(AnnotatedTypeIdentifier id) {
			return get(id);
		}
	}

	private final AnnotatedAnnotationCache annotatedAnnotationCache = new AnnotatedAnnotationCache();

	static List<AnnotationOccurrence> getAnnotationsAnnotatedWith(Class<?> type,
			Class<? extends Annotation> annotationType) {
		return determineCacheFor(type).annotatedAnnotationCache.retrieve(new AnnotatedTypeIdentifier(type, annotationType));
	}

	// ###############################################################################################################
	// ################################################## MISC #######################################################
	// ###############################################################################################################

	private static ReflectionCache DEFAULT = new ReflectionCache();

	private static ReflectionCache determineCacheFor(Class<?> type) {
		if (type.getClassLoader() instanceof PluginCache.PluginClassLoader) {
			return ((PluginCache.PluginClassLoader) type.getClassLoader()).getPluginReflectionCache();
		} else {
			return DEFAULT;
		}
	}

	private static ResolvingSettings retrieveResolvingSettings(AnnotatedElement e) {
		return ResolvingSettings.of(e.getAnnotation(Property.class), e.getAnnotation(Matches.class), 
				e.getAnnotation(DefaultValue.class), e.getAnnotation(Optional.class));
	}

	private static <T> InjectionSettings<T> retrieveInjectionSettings(Class<T> type, AnnotatedElement e) {
		return InjectionSettings.of(type, e.getAnnotation(Inject.class), e.getAnnotation(Global.class), 
				e.getAnnotation(Optional.class), e.getAnnotation(Adjust.class));
	}
}
