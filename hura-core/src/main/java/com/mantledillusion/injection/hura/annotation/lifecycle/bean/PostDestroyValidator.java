package com.mantledillusion.injection.hura.annotation.lifecycle.bean;

import com.mantledillusion.injection.hura.annotation.instruction.Construct;

class PostDestroyValidator extends AbstractLifecycleAnnotationValidator<PostDestroy> {

    @Construct
    PostDestroyValidator() {
        super(PostDestroy.class);
    }

    @Override
    protected Class<? extends BeanProcessor<?>>[] getProcessors(PostDestroy annotationInstance) {
        return annotationInstance.value();
    }
}