package com.mantledillusion.injection.hura.annotation.lifecycle.bean;

import com.mantledillusion.injection.hura.annotation.instruction.Construct;

class PreDestroyValidator extends AbstractLifecycleAnnotationValidator<PreDestroy> {

    @Construct
    PreDestroyValidator() {
        super(PreDestroy.class);
    }

    @Override
    protected Class<? extends BeanProcessor<?>>[] getProcessors(PreDestroy annotationInstance) {
        return annotationInstance.value();
    }
}