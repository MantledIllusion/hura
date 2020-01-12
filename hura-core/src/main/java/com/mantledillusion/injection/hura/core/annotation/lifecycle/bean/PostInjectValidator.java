package com.mantledillusion.injection.hura.core.annotation.lifecycle.bean;

import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;

class PostInjectValidator extends AbstractLifecycleAnnotationValidator<PostInject> {

    @Construct
    PostInjectValidator() {
        super(PostInject.class, true, true, true);
    }

    @Override
    protected Class<? extends BeanProcessor<?>>[] getProcessors(PostInject annotationInstance) {
        return annotationInstance.value();
    }
}
