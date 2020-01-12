package com.mantledillusion.injection.hura.core.annotation.lifecycle.bean;

import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;

class PreConstructValidator extends AbstractLifecycleAnnotationValidator<PreConstruct> {

    @Construct
    PreConstructValidator() {
        super(PreConstruct.class, true,false, false);
    }

    @Override
    protected Class<? extends BeanProcessor<?>>[] getProcessors(PreConstruct annotationInstance) {
        return annotationInstance.value();
    }
}
