package com.mantledillusion.injection.hura.lifecycle.injectables;

import com.mantledillusion.injection.hura.annotation.lifecycle.bean.PostConstruct;
import com.mantledillusion.injection.hura.annotation.lifecycle.bean.PostInject;
import com.mantledillusion.injection.hura.annotation.lifecycle.bean.PreConstruct;
import com.mantledillusion.injection.hura.annotation.lifecycle.bean.PreDestroy;
import com.mantledillusion.injection.hura.lifecycle.misc.LifecycleBeanProcessor;

@PreConstruct(LifecycleBeanProcessor.class)
@PostInject(LifecycleBeanProcessor.class)
@PostConstruct(LifecycleBeanProcessor.class)
@PreDestroy(LifecycleBeanProcessor.class)
public class ClassProcessedLifecycleInjectable extends AbstractLifecycleInjectable {

}