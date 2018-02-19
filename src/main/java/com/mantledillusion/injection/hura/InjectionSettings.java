package com.mantledillusion.injection.hura;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.injection.hura.Blueprint.BlueprintTemplate;
import com.mantledillusion.injection.hura.Blueprint.TypedBlueprint;
import com.mantledillusion.injection.hura.annotation.Context;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Inject.InjectionMode;
import com.mantledillusion.injection.hura.annotation.Inject.SingletonMode;

final class InjectionSettings<T> {

	@SuppressWarnings("rawtypes")
	static final InjectionSettings DEFAULTS = new InjectionSettings<>(null, true, StringUtils.EMPTY, false, SingletonMode.SEQUENCE, InjectionMode.EAGER, false, Collections.emptyList());;
	
	final Class<T> type;
	final boolean isIndependent;
	final String singletonId;
	final boolean isContext;
	final SingletonMode singletonMode;
	final InjectionMode injectionMode;
	final boolean overwriteWithNull;
	final List<Class<? extends BlueprintTemplate>> extensions;

	private InjectionSettings(Class<T> type, boolean isIndependent, String singletonId, boolean isContext,
			SingletonMode singletonMode, InjectionMode injectionMode, boolean overwriteWithNull, List<Class<? extends BlueprintTemplate>> extensions) {
		this.type = type;
		this.isIndependent = isIndependent;
		this.singletonId = singletonId;
		this.isContext = isContext;
		this.singletonMode = singletonMode;
		this.injectionMode = injectionMode;
		this.overwriteWithNull = overwriteWithNull;
		this.extensions = extensions;
	}

	<T2 extends T> InjectionSettings<T2> refine(Class<T2> type) {
		return new InjectionSettings<>(type, isIndependent, singletonId, isContext, singletonMode, injectionMode,
				overwriteWithNull, this.extensions);
	}

	static <T> InjectionSettings<T> of(TypedBlueprint<T> blueprint) {
		if (blueprint == null) {
			throw new IllegalArgumentException("Unable to inject using a null blueprint.");
		}
		return new InjectionSettings<>(blueprint.getRootType(), true, StringUtils.EMPTY,
				isContext(blueprint.getRootType()), SingletonMode.SEQUENCE, InjectionMode.EAGER, false, Collections.emptyList());
	}

	static <T> InjectionSettings<T> of(Class<T> type, Inject annotation) {
		return new InjectionSettings<>(type, StringUtils.isBlank(annotation.value()), annotation.value(),
				isContext(type), annotation.singletonMode(), annotation.injectionMode(),
				annotation.overwriteWithNull(), Arrays.asList(annotation.extensions()));
	}

	private static boolean isContext(Class<?> type) {
		return !ReflectionCache.getSuperTypesAnnotatedWith(type, Context.class).isEmpty();
	}
}