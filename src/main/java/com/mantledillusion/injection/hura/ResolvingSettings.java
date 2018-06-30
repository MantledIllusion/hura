package com.mantledillusion.injection.hura;

import com.mantledillusion.injection.hura.annotation.DefaultValue;
import com.mantledillusion.injection.hura.annotation.Matches;
import com.mantledillusion.injection.hura.annotation.Optional;
import com.mantledillusion.injection.hura.annotation.Property;

final class ResolvingSettings {

	final String propertyKey;
	final String matcher;
	final boolean forced;
	final String defaultValue;
	
	private ResolvingSettings(String propertyKey, String matcher, boolean forced, String defaultValue) {
		super();
		this.propertyKey = propertyKey;
		this.matcher = matcher;
		this.forced = forced;
		this.defaultValue = defaultValue;
	}

	static ResolvingSettings of(Property property, Matches matches, DefaultValue defaultValue, Optional optional) {
		boolean forced = true;
		String matcher = Matches.DEFAULT_MATCHER;
		if (matches != null) {
			matcher = matches.value();
		}
		String defaultVal = null;
		if (defaultValue != null) {
			forced = false;
			defaultVal = defaultValue.value();
		}
		if (optional != null) {
			forced = false;
		}
		return new ResolvingSettings(property.value(), matcher, forced, defaultVal);
	}
	
	static ResolvingSettings of(String propertyKey, String matcher, boolean forced) {
		return new ResolvingSettings(propertyKey, matcher, forced, null);
	}
	
	static ResolvingSettings of(String propertyKey, String matcher, String defaultValue) {
		return new ResolvingSettings(propertyKey, matcher, false, defaultValue);
	}
}
