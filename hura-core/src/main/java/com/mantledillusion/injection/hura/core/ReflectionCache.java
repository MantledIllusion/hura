package com.mantledillusion.injection.hura.core;

import com.mantledillusion.cache.hydnora.HydnoraCache;
import com.mantledillusion.essentials.concurrency.locks.LockIdentifier;
import com.mantledillusion.essentials.reflection.AnnotationEssentials;
import com.mantledillusion.essentials.reflection.AnnotationEssentials.AnnotationOccurrence;
import com.mantledillusion.essentials.reflection.ConstructorEssentials;
import com.mantledillusion.essentials.reflection.MethodEssentials;
import com.mantledillusion.essentials.reflection.TypeEssentials;
import com.mantledillusion.injection.hura.core.annotation.injection.Aggregate;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.instruction.Adjust;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.core.annotation.property.Matches;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;
import com.mantledillusion.injection.hura.core.exception.InjectionException;
import com.mantledillusion.injection.hura.core.exception.ValidatorException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

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

	private static abstract class NonWrappingCache<Identifier extends LockIdentifier, EntryType>
			extends HydnoraCache<Identifier, EntryType> {

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
		private final Map<Integer, ResolvingSettings<?>> resolvableParams;
		private final Map<Integer, InjectionSettings<?>> injectableParams;

		private InjectableConstructor(Constructor<T> constructor, Map<Integer, ResolvingSettings<?>> resolvableParams,
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

		ResolvingSettings<?> getResolvingSettings(int i) {
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

	private static final class ConstructorCache extends NonWrappingCache<TypeIdentifier<?>, InjectableConstructor<?>> {

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
							throw new InjectionException("The only injectable constructor in the type "
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

		private Map<Integer, ResolvingSettings<?>> extractResolvingSettings(Constructor<?> c) throws Exception {
			Map<Integer, ResolvingSettings<?>> settings = new HashMap<>();
			Parameter[] parameters = c.getParameters();
			for (int i = 0; i < c.getParameterCount(); i++) {
				if (InjectionUtils.isResolvable(parameters[i])) {
					settings.put(i, retrieveResolvingSettings(parameters[i].getType(), parameters[i]));
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
		private final ResolvingSettings<?> settings;

		private ResolvableField(Field field, ResolvingSettings<?> settings) {
			this.field = field;
			this.settings = settings;
		}

		Field getField() {
			return field;
		}

		ResolvingSettings<?> getSettings() {
			return settings;
		}
	}

	private static final class ResolvableFieldCache extends NonWrappingCache<TypeIdentifier<?>, List<ResolvableField>> {

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

					ResolvingSettings fieldSet = retrieveResolvingSettings(field.getType(), field);

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

		Field getField() {
			return field;
		}

		InjectionSettings<?> getSettings() {
			return settings;
		}
	}

	private static final class InjectableFieldCache extends NonWrappingCache<TypeIdentifier<?>, List<InjectableField>> {

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
	// ########################################### FIELD AGGREGATION #################################################
	// ###############################################################################################################

	static final class AggregateableField {

		private final Field field;
		private final AggregationSettings<?> settings;

		private AggregateableField(Field field, AggregationSettings<?> settings) {
			this.field = field;
			this.settings = settings;
		}

		Field getField() {
			return field;
		}

		AggregationSettings<?> getSettings() {
			return settings;
		}
	}

	private static final class AggregateableFieldCache extends NonWrappingCache<TypeIdentifier<?>, List<AggregateableField>> {

		@Override
		protected List<AggregateableField> load(TypeIdentifier<?> id) throws Exception {
			return find(id.type);
		}

		private <T> List<AggregateableField> find(Class<T> type) {
			List<AggregateableField> fields = new ArrayList<>();
			Class<? super T> superType = type.getSuperclass();
			while (superType != null && superType != Object.class) {
				fields.addAll(get(new TypeIdentifier<>(superType)));
				superType = superType.getSuperclass();
			}

			for (Field field : type.getDeclaredFields()) {
				if (InjectionUtils.isAggregateable(field)) {
					if (!field.isAccessible()) {
						try {
							field.setAccessible(true);
						} catch (SecurityException e) {
							throw new InjectionException("Unable to gain access to the field '" + field.getName()
									+ "' of the type " + superType.getSimpleName() + " which is inaccessible.", e);
						}
					}

					AggregationSettings<?> fieldSet = retrieveAggregationSettings(field.getType(), field.getGenericType(), field);

					fields.add(new AggregateableField(field, fieldSet));
				}
			}

			return fields;
		}

		private List<AggregateableField> retrieve(TypeIdentifier<?> id) {
			return get(id);
		}
	}

	private final AggregateableFieldCache aggregateableFieldCache = new AggregateableFieldCache();

	static <T> List<AggregateableField> getAggregateableFields(Class<T> type) {
		return determineCacheFor(type).aggregateableFieldCache.retrieve(new TypeIdentifier<>(type));
	}

	// ###############################################################################################################
	// ############################################# ANNOTATED TYPE ##################################################
	// ###############################################################################################################

	private static final class AnnotatedTypeCache extends NonWrappingCache<AnnotatedTypeIdentifier, List<Class<?>>> {

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

	private static final class AnnotatedMethodCache extends NonWrappingCache<AnnotatedTypeIdentifier, List<Method>> {

		@Override
		protected List<Method> load(AnnotatedTypeIdentifier id) throws Exception {
			List<Method> methods = MethodEssentials.getDeclaredMethodsAnnotatedWith(id.type, id.annotationType);
			methods.stream().filter(m -> !m.isAccessible()).forEach(m -> {
				try {
					m.setAccessible(true);
				} catch (SecurityException e) {
					throw new ValidatorException("Unable to gain access to the method '" + m + "' of the type '"
							+ m.getDeclaringClass().getSimpleName() + "'", e);
				}
			});
			return methods;
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
	// ########################################## ANNOTATED ANNOTATION ###############################################
	// ###############################################################################################################

	private static final class AnnotatedAnnotationCache
			extends NonWrappingCache<AnnotatedTypeIdentifier, List<AnnotationOccurrence>> {

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

	private static <T> ResolvingSettings<T> retrieveResolvingSettings(Class<T> type, AnnotatedElement e) {
		return ResolvingSettings.of(type, e.getAnnotation(Resolve.class), e.getAnnotation(Matches.class),
				e.getAnnotation(com.mantledillusion.injection.hura.core.annotation.instruction.Optional.class));
	}

	private static <T> InjectionSettings<T> retrieveInjectionSettings(Class<T> type, AnnotatedElement e) {
		if (e.isAnnotationPresent(Inject.class)) {
			return InjectionSettings.of(type, e.getAnnotation(Inject.class), e.getAnnotation(Qualifier.class),
					e.getAnnotation(com.mantledillusion.injection.hura.core.annotation.instruction.Optional.class), e.getAnnotation(Adjust.class));
		} else {
			return InjectionSettings.of(type, e.getAnnotation(Plugin.class),
					e.getAnnotation(com.mantledillusion.injection.hura.core.annotation.instruction.Optional.class), e.getAnnotation(Adjust.class));
		}
	}

	private static <T> AggregationSettings<T> retrieveAggregationSettings(Class<T> type, Type genericType, AnnotatedElement e) {
		return AggregationSettings.of(type, genericType, e.getAnnotation(Aggregate.class), e.getAnnotation(Optional.class));
	}
}
