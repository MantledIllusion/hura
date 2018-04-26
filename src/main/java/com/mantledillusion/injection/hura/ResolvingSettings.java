package com.mantledillusion.injection.hura;

import com.mantledillusion.injection.hura.annotation.Property;

final class ResolvingSettings {

	final String propertyKey;
	final String matcher;
	final boolean forced;
	final boolean useDefault;
	final String defaultValue;
	
	private ResolvingSettings(String propertyKey, String matcher, boolean forced, boolean useDefault,
			String defaultValue) {
		super();
		this.propertyKey = propertyKey;
		this.matcher = matcher;
		this.forced = forced;
		this.useDefault = useDefault;
		this.defaultValue = defaultValue;
	}

	static ResolvingSettings of(Property property) {
		return new ResolvingSettings(property.value(), property.matcher(), property.forced(), property.useDefault(), property.defaultValue());
	}
	
	static ResolvingSettings of(String propertyKey, String matcher, boolean forced) {
		return new ResolvingSettings(propertyKey, matcher, forced, false, "");
	}
	
	static ResolvingSettings of(String propertyKey, String matcher, String defaultValue) {
		return new ResolvingSettings(propertyKey, matcher, false, true, defaultValue);
	}
}
