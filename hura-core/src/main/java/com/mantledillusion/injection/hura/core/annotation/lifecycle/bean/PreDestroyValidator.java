package com.mantledillusion.injection.hura.core.annotation.lifecycle.bean;

import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;

class PreDestroyValidator extends AbstractLifecycleAnnotationValidator<PreDestroy> {

    @Construct
    PreDestroyValidator() {
        super(PreDestroy.class, false, true);
    }

    @Override
    protected Class<? extends BeanProcessor<?>>[] getProcessors(PreDestroy annotationInstance) {
        return annotationInstance.value();
    }
}