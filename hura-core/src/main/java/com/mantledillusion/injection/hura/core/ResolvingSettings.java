package com.mantledillusion.injection.hura.core;

import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.core.annotation.property.Matches;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

final class ResolvingSettings {

	final String resolvableValue;
	final String matcher;
	final boolean forced;
	
	private ResolvingSettings(String resolvableValue, String matcher, boolean forced) {
		this.resolvableValue = resolvableValue;
		this.matcher = matcher;
		this.forced = forced;
	}

	static ResolvingSettings of(Resolve property, Matches matches, Optional optional) {
		boolean forced = true;
		String matcher = Matches.DEFAULT_MATCHER;
		if (matches != null) {
			matcher = matches.value();
		}
		if (optional != null) {
			forced = false;
		}
		return new ResolvingSettings(property.value(), matcher, forced);
	}
	
	static ResolvingSettings of(String propertyKey, String matcher) {
		return new ResolvingSettings(propertyKey, matcher, false);
	}

	static ResolvingSettings of(String propertyKey, boolean forced) {
		return new ResolvingSettings(propertyKey, Matches.DEFAULT_MATCHER, forced);
	}

	static ResolvingSettings of(String propertyKey, String matcher, boolean forced) {
		return new ResolvingSettings(propertyKey, matcher, forced);
	}
}
