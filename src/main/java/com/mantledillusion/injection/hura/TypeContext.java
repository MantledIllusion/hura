package com.mantledillusion.injection.hura;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.mantledillusion.injection.hura.Injector.AbstractAllocator;

class TypeContext {

	static final String TYPE_CONTEXT_SINGLETON_ID = "_typeContext";

	private final Map<Type, AbstractAllocator<?>> typeAllocations = new HashMap<>();
	
	TypeContext() {
	}
	
	boolean hasTypeAllocator(Type type) {
		return this.typeAllocations.containsKey(type);
	}
	
	TypeContext(TypeContext base) {
		this.typeAllocations.putAll(base.typeAllocations);
	}

	@SuppressWarnings("unchecked")
	<T> AbstractAllocator<T> getTypeAllocator(Type type) {
		return (AbstractAllocator<T>) this.typeAllocations.get(type);
	}
	
	TypeContext merge(Map<Type, AbstractAllocator<?>> typeAllocations) {
		TypeContext newContext = new TypeContext(this);
		newContext.typeAllocations.putAll(typeAllocations);
		return newContext;
	}
	
	TypeContext merge(TypeContext other) {
		TypeContext newContext = new TypeContext(this);
		newContext.typeAllocations.putAll(other.typeAllocations);
		return newContext;
	}
}
