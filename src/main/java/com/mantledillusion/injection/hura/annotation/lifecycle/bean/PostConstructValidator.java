package com.mantledillusion.injection.hura.annotation.lifecycle.bean;

import com.mantledillusion.injection.hura.annotation.instruction.Construct;

class PostConstructValidator extends AbstractLifecycleAnnotationValidator<PostConstruct> {

    @Construct
    PostConstructValidator() {
        super(PostConstruct.class);
    }

    @Override
    protected Class<? extends BeanProcessor<?>>[] getProcessors(PostConstruct annotationInstance) {
        return annotationInstance.value();
    }
}