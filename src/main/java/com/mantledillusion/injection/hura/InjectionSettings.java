package com.mantledillusion.injection.hura;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.mantledillusion.injection.hura.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.annotation.injection.Qualifier;
import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.injection.hura.Blueprint.BlueprintTemplate;
import com.mantledillusion.injection.hura.Blueprint.TypedBlueprint;
import com.mantledillusion.injection.hura.Predefinable.Property;
import com.mantledillusion.injection.hura.Predefinable.Mapping;
import com.mantledillusion.injection.hura.annotation.instruction.Adjust;
import com.mantledillusion.injection.hura.annotation.instruction.Adjust.MappingDef;
import com.mantledillusion.injection.hura.annotation.instruction.Adjust.PropertyDef;
import com.mantledillusion.injection.hura.annotation.instruction.Context;
import com.mantledillusion.injection.hura.annotation.injection.SingletonMode;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.annotation.instruction.Optional.InjectionMode;

final class InjectionSettings<T> {

	final Class<T> type;
	final boolean isIndependent;
	final String qualifier;
	final boolean isContext;
	final SingletonMode singletonMode;
	final InjectionMode injectionMode;
	final boolean overwriteWithNull;
	final Blueprint predefinitions;
	final List<Class<? extends BlueprintTemplate>> extensions;

	private InjectionSettings(Class<T> type, boolean isIndependent, String qualifier, boolean isContext,
			SingletonMode singletonMode, InjectionMode injectionMode, boolean overwriteWithNull,
			Blueprint predefinitions, List<Class<? extends BlueprintTemplate>> extensions) {
		this.type = type;
		this.isIndependent = isIndependent;
		this.qualifier = qualifier;
		this.isContext = isContext;
		this.singletonMode = singletonMode;
		this.injectionMode = injectionMode;
		this.overwriteWithNull = overwriteWithNull;
		this.predefinitions = predefinitions;
		this.extensions = extensions;
	}

	<T2 extends T> InjectionSettings<T2> refine(Class<T2> type) {
		return new InjectionSettings<>(type, this.isIndependent, this.qualifier, this.isContext,
				this.singletonMode, this.injectionMode, this.overwriteWithNull, this.predefinitions, this.extensions);
	}

	InjectionSettings<T> refine(String qualifier) {
		return new InjectionSettings<>(this.type, this.isIndependent, qualifier, this.isContext,
				this.singletonMode, this.injectionMode, this.overwriteWithNull, this.predefinitions, this.extensions);
	}

	static <T> InjectionSettings<T> of(TypedBlueprint<T> blueprint) {
		if (blueprint == null) {
			throw new IllegalArgumentException("Unable to inject using a null blueprint.");
		}
		return new InjectionSettings<>(blueprint.getRootType(), true, StringUtils.EMPTY,
				isContext(blueprint.getRootType()), SingletonMode.SEQUENCE, InjectionMode.EAGER, false,
				Blueprint.EMPTY, Collections.emptyList());
	}

	static <T> InjectionSettings<T> of(Class<T> type, Inject inject, Qualifier qualifier, Optional optional, Adjust adjust) {
		return of(type, qualifier, optional, adjust, inject.overwriteWithNull());
	}

	static <T> InjectionSettings<T> of(Class<T> type, Plugin plugin, Optional optional, Adjust adjust) {
		InjectionSettings<T> set = of(type, null, optional, adjust, false);

		set.predefinitions.getTypeAllocations().put(type, new Injector.PluginAllocator<T>(new File(plugin.directory()), plugin.pluginId(), InjectionProcessors.of()));

		return set;
	}

	private static <T> InjectionSettings<T> of(Class<T> type, Qualifier qualifier, Optional optional, Adjust adjust, boolean overwriteWithNull) {
		List<Predefinable> predefinables = new ArrayList<>();
		String singletonQualifier = StringUtils.EMPTY;
		SingletonMode singletonMode = SingletonMode.SEQUENCE;
		if (qualifier != null) {
			singletonQualifier = qualifier.value();
			singletonMode = qualifier.mode();
		}
		InjectionMode injectionMode = InjectionMode.EAGER;
		if (optional != null) {
			injectionMode = InjectionMode.EXPLICIT;
		}
		List<Class<? extends BlueprintTemplate>> extensions = Collections.emptyList();
		if (adjust != null) {
			for (PropertyDef property : adjust.properties()) {
				predefinables.add(Property.of(property.key(), property.value()));
			}
			for (MappingDef mapping : adjust.mappings()) {
				predefinables.add(Mapping.of(mapping.base(), mapping.target(), mapping.mode()));
			}
			extensions = Arrays.asList(adjust.extensions());
		}
		return new InjectionSettings<>(type, qualifier == null, singletonQualifier, isContext(type),
				singletonMode, injectionMode, overwriteWithNull, Blueprint.of(predefinables),
				extensions);
	}

	static InjectionSettings<Object> of(String qualifier, SingletonMode mode) {
		return new InjectionSettings<>(Object.class, false, qualifier, false, mode, InjectionMode.EAGER, false,
				Blueprint.EMPTY, Collections.emptyList());
	}

	private static boolean isContext(Class<?> type) {
		return !ReflectionCache.getSuperTypesAnnotatedWith(type, Context.class).isEmpty();
	}
}