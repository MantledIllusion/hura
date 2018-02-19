package com.mantledillusion.injection.hura.annotation;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.reflect.TypeUtils;

import com.mantledillusion.injection.hura.AnnotationValidator;
import com.mantledillusion.injection.hura.BeanAllocation;
import com.mantledillusion.injection.hura.Predefinable;
import com.mantledillusion.injection.hura.exception.BlueprintException;

class DefineValidator implements AnnotationValidator<Define, Method> {

	@Override
	public void validate(Define annotationInstance, Method m) throws Exception {
		Map<TypeVariable<?>, Type> collectionGenericType = TypeUtils.getTypeArguments(m.getGenericReturnType(),
				Collection.class);
		if (!TypeUtils.isAssignable(m.getGenericReturnType(), BeanAllocation.class)
				&& !TypeUtils.isAssignable(m.getGenericReturnType(), Predefinable.class)
				&& !(TypeUtils.isAssignable(m.getGenericReturnType(), Collection.class)
						&& TypeUtils.isAssignable(TypeUtils.parameterize(Collection.class, collectionGenericType)
								.getActualTypeArguments()[0], Predefinable.class))) {
			throw new BlueprintException("The method '" + m + "' is annoated with '" + Define.class.getSimpleName()
					+ "', but does neither declare " + BeanAllocation.class.getSimpleName() + " nor a "
					+ Predefinable.class.getSimpleName() + " implementation as its return type.");
		} else if (m.getParameterCount() != 0) {
			throw new BlueprintException("The method '" + m + "' is annoated with '" + Define.class.getSimpleName()
					+ "', but is not parameterless as required.");
		}
	}
}
