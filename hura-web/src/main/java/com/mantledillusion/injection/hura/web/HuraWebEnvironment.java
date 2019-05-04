package com.mantledillusion.injection.hura.web;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.annotation.instruction.Define;

class HuraWebEnvironment implements Blueprint {

    private final String applicationBasePackage;
    private final String applicationInitializerClass;

    HuraWebEnvironment(String applicationBasePackage, String applicationInitializerClass) {
        this.applicationBasePackage = applicationBasePackage;
        this.applicationInitializerClass = applicationInitializerClass;
    }

    @Define
    PropertyAllocation defineApplicationBasePackage() {
        return PropertyAllocation.of(HuraWebApplicationInitializer.PKEY_BASEPACKAGE, this.applicationBasePackage);
    }

    @Define
    PropertyAllocation defineApplicationInitializerClass() {
        return PropertyAllocation.of(HuraWebApplicationInitializer.PKEY_INITIALIZER, this.applicationInitializerClass);
    }
}
