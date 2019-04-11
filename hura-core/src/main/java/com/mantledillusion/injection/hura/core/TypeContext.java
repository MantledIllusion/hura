package com.mantledillusion.injection.hura.core;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

class TypeContext {

	static final String TYPE_CONTEXT_SINGLETON_ID = "_typeContext";

	private final Map<Type, Injector.AbstractAllocator<?>> typeAllocations = new HashMap<>();
	
	TypeContext() {
	}

	TypeContext(TypeContext base) {
		this.typeAllocations.putAll(base.typeAllocations);
	}
	
	boolean hasTypeAllocator(Type type) {
		return this.typeAllocations.containsKey(type);
	}

	@SuppressWarnings("unchecked")
	<T> Injector.AbstractAllocator<T> getTypeAllocator(Type type) {
		return (Injector.AbstractAllocator<T>) this.typeAllocations.get(type);
	}
	
	TypeContext merge(Map<Type, Injector.AbstractAllocator<?>> typeAllocations) {
		TypeContext newContext = new TypeContext(this);
		newContext.typeAllocations.putAll(typeAllocations);
		return newContext;
	}
}
