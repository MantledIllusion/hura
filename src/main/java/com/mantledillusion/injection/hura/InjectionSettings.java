package com.mantledillusion.injection.hura;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.injection.hura.Blueprint.BlueprintTemplate;
import com.mantledillusion.injection.hura.Blueprint.TypedBlueprint;
import com.mantledillusion.injection.hura.Predefinable.Property;
import com.mantledillusion.injection.hura.Predefinable.Mapping;
import com.mantledillusion.injection.hura.annotation.Adjust;
import com.mantledillusion.injection.hura.annotation.Adjust.MappingDef;
import com.mantledillusion.injection.hura.annotation.Adjust.PropertyDef;
import com.mantledillusion.injection.hura.annotation.Context;
import com.mantledillusion.injection.hura.annotation.Global;
import com.mantledillusion.injection.hura.annotation.Global.SingletonMode;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Optional;
import com.mantledillusion.injection.hura.annotation.Optional.InjectionMode;

final class InjectionSettings<T> {

	@SuppressWarnings("rawtypes")
	static final InjectionSettings DEFAULTS = new InjectionSettings<>(null, true, StringUtils.EMPTY, false,
			SingletonMode.SEQUENCE, InjectionMode.EAGER, false, Blueprint.EMPTY, Collections.emptyList());

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
		return new InjectionSettings<>(type, this.isIndependent, this.qualifier, this.isContext, this.singletonMode,
				this.injectionMode, this.overwriteWithNull, this.predefinitions, this.extensions);
	}

	InjectionSettings<T> refine(String qualifier) {
		return new InjectionSettings<>(this.type, this.isIndependent, qualifier, this.isContext, this.singletonMode,
				this.injectionMode, this.overwriteWithNull, this.predefinitions, this.extensions);
	}

	static <T> InjectionSettings<T> of(TypedBlueprint<T> blueprint) {
		if (blueprint == null) {
			throw new IllegalArgumentException("Unable to inject using a null blueprint.");
		}
		return new InjectionSettings<>(blueprint.getRootType(), true, StringUtils.EMPTY,
				isContext(blueprint.getRootType()), SingletonMode.SEQUENCE, InjectionMode.EAGER, false, Blueprint.EMPTY,
				Collections.emptyList());
	}

	static <T> InjectionSettings<T> of(Class<T> type, Inject inject, Global global, Optional optional, Adjust adjust) {
		List<Predefinable> predefinables = new ArrayList<>();
		SingletonMode singletonMode = SingletonMode.SEQUENCE;
		if (global != null) {
			singletonMode = SingletonMode.GLOBAL;
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
		return new InjectionSettings<>(type, StringUtils.isBlank(inject.value()), inject.value(), isContext(type),
				singletonMode, injectionMode, inject.overwriteWithNull(), Blueprint.of(predefinables),
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