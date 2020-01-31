package com.mantledillusion.injection.hura.core.annotation.lifecycle.bean;

import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;

class PostDestroyValidator extends AbstractLifecycleAnnotationValidator<PostDestroy> {

    @Construct
    PostDestroyValidator() {
        super(PostDestroy.class, false, false, false);
    }

    @Override
    protected Class<? extends BeanProcessor<?>>[] getProcessors(PostDestroy annotationInstance) {
        return annotationInstance.value();
    }
}