package com.mantledillusion.injection.hura.core.lifecycle.injectables;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.*;
import com.mantledillusion.injection.hura.core.lifecycle.misc.LifecycleBeanProcessor;

@PreConstruct(LifecycleBeanProcessor.class)
@PostInject(LifecycleBeanProcessor.class)
@PostConstruct(LifecycleBeanProcessor.class)
@PreDestroy(LifecycleBeanProcessor.class)
@PostDestroy(LifecycleBeanProcessor.class)
public class ClassProcessedLifecycleInjectable extends AbstractLifecycleInjectable {

}