package com.mantledillusion.injection.hura.lifecycle.injectables;

import com.mantledillusion.injection.hura.annotation.lifecycle.bean.*;
import com.mantledillusion.injection.hura.lifecycle.misc.LifecycleBeanProcessor;

@PreConstruct(LifecycleBeanProcessor.class)
@PostInject(LifecycleBeanProcessor.class)
@PostConstruct(LifecycleBeanProcessor.class)
@PreDestroy(LifecycleBeanProcessor.class)
@PostDestroy(LifecycleBeanProcessor.class)
public class ClassProcessedLifecycleInjectable extends AbstractLifecycleInjectable {

}