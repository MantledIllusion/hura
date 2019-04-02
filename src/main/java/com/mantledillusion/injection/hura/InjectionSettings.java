package com.mantledillusion.injection.hura;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.mantledillusion.injection.hura.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.annotation.injection.Qualifier;
import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.injection.hura.annotation.instruction.Adjust;
import com.mantledillusion.injection.hura.annotation.instruction.Adjust.MappingDef;
import com.mantledillusion.injection.hura.annotation.instruction.Adjust.PropertyDef;
import com.mantledillusion.injection.hura.annotation.instruction.Context;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.annotation.instruction.Optional.InjectionMode;

final class InjectionSettings<T> {

	final Class<T> type;
	final boolean isIndependent;
	final String qualifier;
	final boolean isContext;
	final InjectionMode injectionMode;
	final boolean overwriteWithNull;
	final List<Blueprint.Allocation> allocations;
	final List<Class<? extends Blueprint>> extensions;

	private InjectionSettings(Class<T> type, boolean isIndependent, String qualifier, boolean isContext,
							  InjectionMode injectionMode, boolean overwriteWithNull, List<Blueprint.Allocation> allocations,
							  List<Class<? extends Blueprint>> extensions) {
		this.type = type;
		this.isIndependent = isIndependent;
		this.qualifier = qualifier;
		this.isContext = isContext;
		this.injectionMode = injectionMode;
		this.overwriteWithNull = overwriteWithNull;
		this.allocations = allocations;
		this.extensions = extensions;
	}

	<T2 extends T> InjectionSettings<T2> refine(Class<T2> type) {
		return new InjectionSettings<>(type, this.isIndependent, this.qualifier, this.isContext,
				this.injectionMode, this.overwriteWithNull, this.allocations, this.extensions);
	}

	InjectionSettings<T> refine(String qualifier) {
		return new InjectionSettings<>(this.type, this.isIndependent, qualifier, this.isContext,
				this.injectionMode, this.overwriteWithNull, this.allocations, this.extensions);
	}

	static <T> InjectionSettings<T> of(Class<T> type) {
		if (type == null) {
			throw new IllegalArgumentException("Unable to inject using a null root type.");
		}
		return new InjectionSettings<>(type, true, StringUtils.EMPTY,
				isContext(type), InjectionMode.EAGER, false,
				Collections.emptyList(), Collections.emptyList());
	}

	static <T> InjectionSettings<T> of(Class<T> type, Inject inject, Qualifier qualifier, Optional optional, Adjust adjust) {
		return of(type, qualifier, optional, adjust, inject.overwriteWithNull());
	}

	static <T> InjectionSettings<T> of(Class<T> type, Plugin plugin, Optional optional, Adjust adjust) {
		InjectionSettings<T> set = of(type, null, optional, adjust, false);

		set.allocations.add(Blueprint.TypeAllocation.allocateToPlugin(type, new File(plugin.directory()), plugin.pluginId()));

		return set;
	}

	private static <T> InjectionSettings<T> of(Class<T> type, Qualifier qualifier, Optional optional, Adjust adjust, boolean overwriteWithNull) {
		List<Blueprint.Allocation> allocations = new ArrayList<>();
		String singletonQualifier = StringUtils.EMPTY;
		if (qualifier != null) {
			singletonQualifier = qualifier.value();
		}
		InjectionMode injectionMode = InjectionMode.EAGER;
		if (optional != null) {
			injectionMode = InjectionMode.EXPLICIT;
		}
		List<Class<? extends Blueprint>> extensions = Collections.emptyList();
		if (adjust != null) {
			for (PropertyDef property : adjust.properties()) {
				allocations.add(Blueprint.PropertyAllocation.of(property.key(), property.value()));
			}
			for (MappingDef mapping : adjust.mappings()) {
				allocations.add(Blueprint.MappingAllocation.of(mapping.base(), mapping.target()));
			}
			extensions = Arrays.asList(adjust.extensions());
		}
		return new InjectionSettings<>(type, qualifier == null, singletonQualifier, isContext(type),
				injectionMode, overwriteWithNull, allocations, extensions);
	}

	static InjectionSettings<Object> of(String qualifier) {
		return new InjectionSettings<>(Object.class, false, qualifier, false, InjectionMode.EAGER,
				false, Collections.emptyList(), Collections.emptyList());
	}

	private static boolean isContext(Class<?> type) {
		return !ReflectionCache.getSuperTypesAnnotatedWith(type, Context.class).isEmpty();
	}
}