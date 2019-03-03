package com.mantledillusion.injection.hura;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.mantledillusion.essentials.reflection.TypeEssentials;
import com.mantledillusion.injection.hura.Injector.AbstractAllocator;
import com.mantledillusion.injection.hura.Predefinable.Property;
import com.mantledillusion.injection.hura.Predefinable.Singleton;
import com.mantledillusion.injection.hura.Predefinable.Mapping;
import com.mantledillusion.injection.hura.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.annotation.instruction.Define;
import com.mantledillusion.injection.hura.annotation.injection.Global.SingletonMode;
import com.mantledillusion.injection.hura.exception.BlueprintException;
import com.mantledillusion.injection.hura.exception.ValidatorException;
import org.apache.commons.lang3.reflect.TypeUtils;

/**
 * A {@link Blueprint} is an injection instruction for an {@link Injector}.
 * <p>
 * It may contain information on how the injection has to be done when the
 * {@link Blueprint} is used by an {@link Injector}.
 * <p>
 * For example, the {@link Blueprint} may pre-define singletons or divert the
 * injection of an interface-typed injection to the implementing {@link Class}
 * that has to be used for the injection; these kind of injection instructions
 * are called allocations.
 * <p>
 * The static {@link Method}s...
 * <ul>
 * <li>{@link #from(BlueprintTemplate)} (For injection instruction only)</li>
 * <li>{@link #from(TypedBlueprintTemplate)} (For injection instruction on
 * instantiating a specific type)</li>
 * <li>{@link #of(Predefinable...)} (For simple injection instruction only)</li>
 * <li>{@link #of(Class, Predefinable...)} (For simple injection of instantiating
 * a specific type)</li>
 * </ul>
 * ...can be used to create {@link Blueprint} instances; refer to the
 * documentation of these {@link Method}s for information on how allocations can
 * be created.
 */
public class Blueprint {

	static final Blueprint EMPTY = new Blueprint();

	/**
	 * A {@link TypedBlueprint} is an extension to the {@link Blueprint}.
	 * <p>
	 * In addition to defining allocations, a {@link TypedBlueprint} also determines
	 * a specific {@link Class} that can be instantiated and injected using it.
	 *
	 * @param <T>
	 *            The root type to instantiate, inject and return as a new bean when
	 *            using this {@link TypedBlueprint} on an {@link Injector}.
	 */
	public static final class TypedBlueprint<T> extends Blueprint {

		private final Class<T> rootType;

		private TypedBlueprint(Class<T> rootType) {
			this.rootType = rootType;
		}

		public Class<T> getRootType() {
			return rootType;
		}
	}

	/**
	 * Interface for self implemented {@link Blueprint}s that can be processed into
	 * an actual {@link Blueprint} instance by
	 * {@link Blueprint#from(BlueprintTemplate)}.
	 * <p>
	 * An implementation of this interface may define an arbitrary amount of
	 * {@link BeanAllocation} returning {@link Method}s that are annotated with
	 * {@link Define}.
	 * <p>
	 * During processing, these {@link Method}s will be invoked; their returned
	 * {@link BeanAllocation} instances will be turned into allocations the
	 * {@link Injector} can use during injection of the processed {@link Blueprint};
	 * this is how {@link BlueprintTemplate} implementations can influence the way
	 * an {@link Injector} injects its beans.
	 */
	public interface BlueprintTemplate {

	}

	/**
	 * A {@link TypedBlueprintTemplate} is an extension to a
	 * {@link BlueprintTemplate}.
	 * <p>
	 * In addition to defining allocations, a {@link TypedBlueprintTemplate} also
	 * has to determine a specific {@link Class} that can be instantiated and
	 * injected using it after it has been processed by
	 * {@link Blueprint#from(TypedBlueprintTemplate)}.
	 *
	 * @param <T>
	 *            The root type to instantiate, inject and return as a new bean when
	 *            using a processed {@link TypedBlueprint} instance of this
	 *            {@link TypedBlueprintTemplate} on an {@link Injector}.
	 */
	public interface TypedBlueprintTemplate<T> extends BlueprintTemplate {

		/**
		 * Returns the root {@link Class} this {@link TypedBlueprintTemplate} can be
		 * used to instantiate and inject when being used on an {@link Injector} after
		 * being processed to a {@link TypedBlueprint}.
		 * 
		 * @return The {@link Class} to instantiate and inject; may <b>not</b> return
		 *         null.
		 */
		Class<T> getRootType();
	}

	private final Map<Type, AbstractAllocator<?>> typeAllocations = new HashMap<>();
	private final Map<String, AbstractAllocator<?>> singletonAllocations = new HashMap<>();
	private final Map<String, String> propertyAllocations = new HashMap<>();
	private final Map<SingletonMode, Map<String, String>> mappingAllocations = new HashMap<>();

	private Blueprint() {
		this.mappingAllocations.put(SingletonMode.GLOBAL, new HashMap<>());
		this.mappingAllocations.put(SingletonMode.SEQUENCE, new HashMap<>());
	}

	Map<Type, AbstractAllocator<?>> getTypeAllocations() {
		return typeAllocations;
	}

	Map<String, AbstractAllocator<?>> getSingletonAllocations() {
		return singletonAllocations;
	}

	Map<String, String> getPropertyAllocations() {
		return propertyAllocations;
	}

	Map<SingletonMode, Map<String, String>> getMappingAllocations() {
		return mappingAllocations;
	}

	/**
	 * Convenience {@link Method} for not having to implement
	 * {@link BlueprintTemplate} and passing it to {@link #from(BlueprintTemplate)}
	 * when the injection scenario is rather basic, so the extended allocation
	 * features of the {@link BlueprintTemplate} are not needed.
	 * 
	 * @param predefinables
	 *            The {@link Predefinable}s to be used during injection, such as
	 *            {@link SingletonMode#SEQUENCE} {@link Singleton}s or
	 *            {@link Property}s; might be null or contain nulls, both is
	 *            ignored.
	 * @return A new {@link Blueprint} instance; never null
	 */
	public static Blueprint of(Predefinable... predefinables) {
		return of(Arrays.asList(predefinables));
	}

	/**
	 * Convenience {@link Method} for not having to implement
	 * {@link BlueprintTemplate} and passing it to {@link #from(BlueprintTemplate)}
	 * when the injection scenario is rather basic, so the extended allocation
	 * features of the {@link BlueprintTemplate} are not needed.
	 * 
	 * @param predefinables
	 *            The {@link Predefinable}s to be used during injection, such as
	 *            {@link SingletonMode#SEQUENCE} {@link Singleton}s or
	 *            {@link Property}s; might be null or contain nulls, both is
	 *            ignored.
	 * @return A new {@link Blueprint} instance; never null
	 */
	public static Blueprint of(Collection<Predefinable> predefinables) {
		Blueprint blueprint = new Blueprint();
		addPredefinables(blueprint, predefinables);
		return blueprint;
	}

	/**
	 * Convenience {@link Method} for not having to implement
	 * {@link TypedBlueprintTemplate} and passing it to
	 * {@link #from(TypedBlueprintTemplate)} when the injection scenario is rather
	 * basic, so the extended allocation features of the
	 * {@link TypedBlueprintTemplate} are not needed.
	 * 
	 * @param <T>
	 *            The root type of the bean to instantiate and inject by the
	 *            {@link Injector}.
	 * @param rootType
	 *            The {@link Class} of the bean to instantiate and inject by the
	 *            {@link Injector}; may <b>not</b> return null.
	 * @param predefinables
	 *            The {@link Predefinable}s to be used during injection, such as
	 *            {@link SingletonMode#SEQUENCE} {@link Singleton}s or
	 *            {@link Property}s; might be null or contain nulls, both is
	 *            ignored.
	 * @return A new {@link TypedBlueprint} instance; never null
	 */
	public static <T> TypedBlueprint<T> of(Class<T> rootType, Predefinable... predefinables) {
		return of(rootType, Arrays.asList(predefinables));
	}

	/**
	 * Convenience {@link Method} for not having to implement
	 * {@link TypedBlueprintTemplate} and passing it to
	 * {@link #from(TypedBlueprintTemplate)} when the injection scenario is rather
	 * basic, so the extended allocation features of the
	 * {@link TypedBlueprintTemplate} are not needed.
	 * 
	 * @param <T>
	 *            The root type of the bean to instantiate and inject by the
	 *            {@link Injector}.
	 * @param rootType
	 *            The {@link Class} of the bean to instantiate and inject by the
	 *            {@link Injector}; may <b>not</b> return null.
	 * @param predefinables
	 *            The {@link Predefinable}s to be used during injection, such as
	 *            {@link SingletonMode#SEQUENCE} {@link Singleton}s or
	 *            {@link Property}s; might be null or contain nulls, both is
	 *            ignored.
	 * @return A new {@link TypedBlueprint} instance; never null
	 */
	public static <T> TypedBlueprint<T> of(Class<T> rootType, Collection<Predefinable> predefinables) {
		if (rootType == null) {
			throw new IllegalArgumentException("Cannot create a blue print for a null root type.");
		}

		TypedBlueprint<T> blueprint = new TypedBlueprint<>(rootType);
		addPredefinables(blueprint, predefinables);
		return blueprint;
	}

	private static void addPredefinables(Blueprint blueprint, Collection<Predefinable> predefinables) {
		if (predefinables != null) {
			for (Predefinable predefinable : predefinables) {
				if (predefinable != null) {
					if (predefinable instanceof Property) {
						Property property = (Property) predefinable;
						String propertyKey = property.getKey();
						if (blueprint.propertyAllocations.containsKey(propertyKey)) {
							throw new IllegalArgumentException(
									"There were 2 or more property values defined for the key '" + propertyKey + "'; '"
											+ blueprint.propertyAllocations.get(propertyKey) + "' and '"
											+ property.getValue() + "'");
						}
						blueprint.propertyAllocations.put(propertyKey, property.getValue());
					} else if (predefinable instanceof Singleton) {
						Singleton singleton = (Singleton) predefinable;
						String qualifier = singleton.getQualifier();
						if (blueprint.singletonAllocations.containsKey(qualifier)) {
							throw new IllegalArgumentException(
									"There were 2 or more beans defined for the qualifier '" + qualifier + "'");
						}
						blueprint.singletonAllocations.put(singleton.getQualifier(), singleton.getAllocator());
					}
					if (predefinable instanceof Mapping) {
						Mapping mapping = (Mapping) predefinable;
						SingletonMode mappingMode = mapping.getMode();
						String mappingBase = mapping.getBase();
						if (blueprint.mappingAllocations.get(mappingMode).containsKey(mappingBase)) {
							throw new IllegalArgumentException("There were 2 or more '" + mappingMode.name()
									+ "' singleton mapping targets defined for the mapping base '" + mappingBase
									+ "'; '" + blueprint.propertyAllocations.get(mappingBase) + "' and '"
									+ mapping.getTarget() + "'");
						}
						blueprint.mappingAllocations.get(mappingMode).put(mappingBase, mapping.getTarget());
					}
				}
			}
		}
	}

	/**
	 * Processes the given {@link BlueprintTemplate} into an actual
	 * {@link Blueprint} that can be used on {@link Injector}s.
	 * 
	 * @param template
	 *            The {@link BlueprintTemplate} to process; may <b>not</b> be null.
	 * @return A new {@link Blueprint} instance; never null
	 */
	public static Blueprint from(BlueprintTemplate template) {
		if (template == null) {
			throw new IllegalArgumentException("Cannot process a null template");
		}

		Blueprint blueprint = new Blueprint();
		buildAllocations(template, blueprint);
		return blueprint;
	}

	/**
	 * Processes the given {@link TypedBlueprintTemplate} into an actual
	 * {@link TypedBlueprint} that can be used on {@link Injector}s.
	 * 
	 * @param <T>
	 *            The bean type.
	 * @param template
	 *            The {@link TypedBlueprintTemplate} to process; may <b>not</b> be
	 *            null.
	 * @return A new {@link TypedBlueprint} instance; never null
	 */
	public static <T> TypedBlueprint<T> from(TypedBlueprintTemplate<T> template) {
		if (template == null) {
			throw new IllegalArgumentException("Cannot process a null template");
		}

		Class<T> rootType = template.getRootType();
		if (rootType == null) {
			throw new BlueprintException("The root type of a type blueprint template may not be null.");
		}

		TypedBlueprint<T> blueprint = new TypedBlueprint<>(rootType);
		buildAllocations(template, blueprint);
		return blueprint;
	}

	private static void buildAllocations(BlueprintTemplate template, Blueprint blueprint) {
		for (Method m : ReflectionCache.getMethodsAnnotatedWith(template.getClass(), Define.class)) {
			Map<TypeVariable<?>, Type> collectionGenericType = TypeUtils.getTypeArguments(m.getGenericReturnType(),
					Collection.class);
			if (!TypeUtils.isAssignable(m.getGenericReturnType(), BeanAllocation.class)
					&& !TypeUtils.isAssignable(m.getGenericReturnType(), Predefinable.class)
					&& !(TypeUtils.isAssignable(m.getGenericReturnType(), Collection.class) && TypeUtils.isAssignable(
					TypeUtils.parameterize(Collection.class, collectionGenericType).getActualTypeArguments()[0],
					Predefinable.class))) {
				throw new ValidatorException(
						"The " + ValidatorUtils.getDescription(m) + " is annotated with '" + Define.class.getSimpleName()
								+ "', but does neither declare " + BeanAllocation.class.getSimpleName() + " nor a "
								+ Predefinable.class.getSimpleName() + " implementation as its return type.");
			} else if (m.getParameterCount() != 0) {
				throw new ValidatorException("The " + ValidatorUtils.getDescription(m) + " is annotated with '"
						+ Define.class.getSimpleName() + "', but is not parameterless as required.");
			}

			if (!m.isAccessible()) {
				try {
					m.setAccessible(true);
				} catch (SecurityException e) {
					throw new BlueprintException("Unable to gain access to the method '" + m + "' of the type '"
							+ m.getDeclaringClass().getSimpleName() + "'", e);
				}
			}

			Object definable;
			try {
				definable = m.invoke(template);
			} catch (IllegalAccessException e) {
				throw new BlueprintException("Unable to call method '" + m + "' annotated with @"
						+ Define.class.getSimpleName() + " to retrieve its definition", e);
			} catch (InvocationTargetException e) {
				throw new BlueprintException("Unable to call method '" + m + "' annotated with @"
						+ Define.class.getSimpleName() + " to retrieve its definition", e.getTargetException());
			}

			if (definable instanceof Collection) {
				@SuppressWarnings("unchecked")
				Collection<Predefinable> predefinables = (Collection<Predefinable>) definable;
				for (Predefinable predefinable : predefinables) {
					define(predefinable, blueprint);
				}
			} else if (definable instanceof Predefinable) {
				define((Predefinable) definable, blueprint);
			} else {
				BeanAllocation<?> alloc = (BeanAllocation<?>) definable;
				if (alloc == null) {
					throw new BlueprintException("The allocation returned by the method '" + m
							+ "' was null; cannot allocate a null allocation.");
				}

				ParameterizedType parameterizedBeanAllocation = TypeEssentials
						.getParameterizedBound(BeanAllocation.class, m.getGenericReturnType());
				blueprint.typeAllocations.put(parameterizedBeanAllocation.getActualTypeArguments()[0],
						alloc.getAllocator());
			}
		}
	}

	private static void define(Predefinable predefinable, Blueprint blueprint) {
		if (predefinable instanceof Property) {
			Property property = (Property) predefinable;
			blueprint.propertyAllocations.put(property.getKey(), property.getValue());
		} else if (predefinable instanceof Singleton) {
			Singleton singleton = (Singleton) predefinable;
			blueprint.singletonAllocations.put(singleton.getQualifier(), singleton.getAllocator());
		} else if (predefinable instanceof Mapping) {
			Mapping mapping = (Mapping) predefinable;
			blueprint.mappingAllocations.get(mapping.getMode()).put(mapping.getBase(),
					mapping.getTarget());
		}
	}
}