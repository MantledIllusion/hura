package com.mantledillusion.injection.hura.core;

import com.mantledillusion.injection.hura.core.Injector.AbstractAllocator;
import com.mantledillusion.injection.hura.core.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.core.annotation.instruction.Define;
import com.mantledillusion.injection.hura.core.exception.BlueprintException;
import com.mantledillusion.injection.hura.core.exception.MappingException;
import com.mantledillusion.injection.hura.core.exception.ValidatorException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

class InjectionAllocations {

	static final InjectionAllocations EMPTY = new InjectionAllocations();

	private final Map<Type, AbstractAllocator<?>> typeAllocations = new HashMap<>();
	private final Map<String, AbstractAllocator<?>> singletonAllocations = new HashMap<>();
	private final Map<String, String> propertyAllocations = new HashMap<>();
	private final Map<String, String> mappingAllocations = new HashMap<>();

	private InjectionAllocations() {
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

	Map<String, String> getMappingAllocations() {
		return mappingAllocations;
	}

	static InjectionAllocations ofBlueprints(Collection<Blueprint> blueprints) {
		return ofBlueprintsAndAllocations(blueprints, Collections.emptyList());
	}

	static InjectionAllocations ofAllocations(Collection<Blueprint.Allocation> allocations) {
		return ofBlueprintsAndAllocations(Collections.emptyList(), allocations);
	}

	static InjectionAllocations ofBlueprintsAndAllocations(Collection<Blueprint> blueprints, Collection<Blueprint.Allocation> allocations) {
		InjectionAllocations injectionAllocations = new InjectionAllocations();
		if (blueprints != null) {
			for (Blueprint blueprint: blueprints) {
				if (blueprint != null) {
					findDefinitions(blueprint, injectionAllocations);
				}
			}
		}
		if (allocations != null) {
			for (Blueprint.Allocation allocation : allocations) {
				if (allocation != null) {
					define(allocation, injectionAllocations);
				}
			}
		}
		return injectionAllocations;
	}

	private static void findDefinitions(Blueprint blueprint, InjectionAllocations injectionAllocations) {
		for (Method m : ReflectionCache.getMethodsAnnotatedWith(blueprint.getClass(), Define.class)) {
			if (!TypeUtils.isAssignable(m.getGenericReturnType(), Blueprint.Allocation.class)
					&& !(TypeUtils.isAssignable(m.getGenericReturnType(), Collection.class)
					&& TypeUtils.isAssignable(InjectionUtils.findCollectionType(m.getGenericReturnType()), Blueprint.Allocation.class))) {
				throw new ValidatorException(
						"The " + ValidatorUtils.getDescription(m) + " is annotated with '" + Define.class.getSimpleName()
								+ "', but does neither declare " + Blueprint.TypeAllocation.class.getSimpleName() + " nor a "
								+ Blueprint.Allocation.class.getSimpleName() + " implementation as its return type.");
			} else if (m.getParameterCount() != 0) {
				throw new ValidatorException("The " + ValidatorUtils.getDescription(m) + " is annotated with '"
						+ Define.class.getSimpleName() + "', but is not parameterless as required.");
			}

			Object definable;
			try {
				definable = m.invoke(blueprint);
			} catch (IllegalAccessException e) {
				throw new BlueprintException("Unable to call method '" + m + "' annotated with @"
						+ Define.class.getSimpleName() + " to retrieve its definition", e);
			} catch (InvocationTargetException e) {
				throw new BlueprintException("Unable to call method '" + m + "' annotated with @"
						+ Define.class.getSimpleName() + " to retrieve its definition", e.getTargetException());
			}

			if (definable == null) {
				throw new BlueprintException("Method '" + m + "' annotated with @"
						+ Define.class.getSimpleName() + " defined a null allocation");
			}

			if (definable instanceof Collection) {
				@SuppressWarnings("unchecked")
				Collection<Blueprint.Allocation> allocations = (Collection<Blueprint.Allocation>) definable;
				for (Blueprint.Allocation allocation : allocations) {
					define(allocation, injectionAllocations);
				}
			} else if (definable instanceof Blueprint.Allocation) {
				define((Blueprint.Allocation) definable, injectionAllocations);
			}
		}
	}

	private static void define(Blueprint.Allocation allocation, InjectionAllocations injectionAllocations) {
		if (allocation != null) {
			if (allocation instanceof Blueprint.SingletonAllocation) {
				Blueprint.SingletonAllocation singleton = (Blueprint.SingletonAllocation) allocation;
				String qualifier = singleton.getQualifier();
				if (injectionAllocations.singletonAllocations.containsKey(qualifier)) {
					throw new IllegalArgumentException(
							"There were 2 or more singletons defined for the qualifier '" + qualifier + "'");
				}
				injectionAllocations.singletonAllocations.put(singleton.getQualifier(), singleton.getAllocator());
			} else if (allocation instanceof Blueprint.PropertyAllocation) {
				Blueprint.PropertyAllocation property = (Blueprint.PropertyAllocation) allocation;
				String propertyKey = property.getKey();
				if (injectionAllocations.propertyAllocations.containsKey(propertyKey)) {
					throw new IllegalArgumentException(
							"There were 2 or more property values defined for the key '" + propertyKey + "'; '"
									+ injectionAllocations.propertyAllocations.get(propertyKey) + "' and '"
									+ property.getValue() + "'");
				}
				injectionAllocations.propertyAllocations.put(propertyKey, property.getValue());
			} else if (allocation instanceof Blueprint.MappingAllocation) {
				Blueprint.MappingAllocation mapping = (Blueprint.MappingAllocation) allocation;
				String mappingBase = mapping.getBase();
				String mappingTarget = mapping.getTarget();
				if (injectionAllocations.mappingAllocations.containsKey(mappingBase)) {
					throw new IllegalArgumentException("There were 2 or more singleton mapping " +
							"targets defined for the mapping base '" + mappingBase + "'; '"
							+ injectionAllocations.propertyAllocations.get(mappingBase) + "' and '"
							+ mappingTarget + "'");
				}
				checkForCircularMapping(mappingBase, mappingTarget, injectionAllocations.mappingAllocations);
				injectionAllocations.mappingAllocations.put(mappingBase, mappingTarget);
			} else if (allocation instanceof Blueprint.TypeAllocation) {
				Blueprint.TypeAllocation typeAllocation = (Blueprint.TypeAllocation) allocation;
				Class<?> type = typeAllocation.getType();
				if (injectionAllocations.typeAllocations.containsKey(type)) {
					throw new IllegalArgumentException(
							"There were 2 or more allocators defined for the type '" + type.getSimpleName() + "'");
				}
				injectionAllocations.typeAllocations.put(type, typeAllocation.getAllocator());
			}
		}
	}

	private static void checkForCircularMapping(String mappingBase, String mappingTarget, Map<String, String> existingMappings) {
		List<String> qualifiers = new ArrayList<>();
		qualifiers.add(mappingBase);
		Map<String, String> mappings = new HashMap<>(existingMappings);
		mappings.put(mappingBase, mappingTarget);

		String target = mappingTarget;
		while (mappings.containsKey(target)) {
			target = mappings.get(target);
			if (mappingBase.equals(target)) {
				throw new MappingException("qualifier mapping loop detected! Adding a mapping from '" + mappingBase
						+ "' to '" + mappingTarget + "' closes the mapping loop '"
						+ StringUtils.join(qualifiers, "' -> '") + "'");
			}
		}
	}
}