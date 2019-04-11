package com.mantledillusion.injection.hura.annotation.lifecycle.bean;

import com.mantledillusion.injection.hura.annotation.instruction.Construct;

class PreConstructValidator extends AbstractLifecycleAnnotationValidator<PreConstruct> {

    @Construct
    PreConstructValidator() {
        super(PreConstruct.class);
    }

    @Override
    protected Class<? extends BeanProcessor<?>>[] getProcessors(PreConstruct annotationInstance) {
        return annotationInstance.value();
    }
}
