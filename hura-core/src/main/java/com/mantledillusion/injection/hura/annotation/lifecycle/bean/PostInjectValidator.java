package com.mantledillusion.injection.hura.annotation.lifecycle.bean;

import com.mantledillusion.injection.hura.annotation.instruction.Construct;

class PostInjectValidator extends AbstractLifecycleAnnotationValidator<PostInject> {

    @Construct
    PostInjectValidator() {
        super(PostInject.class);
    }

    @Override
    protected Class<? extends BeanProcessor<?>>[] getProcessors(PostInject annotationInstance) {
        return annotationInstance.value();
    }
}
