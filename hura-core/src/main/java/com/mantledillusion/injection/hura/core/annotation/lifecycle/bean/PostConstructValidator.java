package com.mantledillusion.injection.hura.core.annotation.lifecycle.bean;

import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;

class PostConstructValidator extends AbstractLifecycleAnnotationValidator<PostConstruct> {

    @Construct
    PostConstructValidator() {
        super(PostConstruct.class, false, true, true);
    }

    @Override
    protected Class<? extends BeanProcessor<?>>[] getProcessors(PostConstruct annotationInstance) {
        return annotationInstance.value();
    }
}
