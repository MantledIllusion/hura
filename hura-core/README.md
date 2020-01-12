# Hura Core

Hura's core contains all the generic base functionality of the framework.

## 1. Dependency Injection Theory

### 1.1 Fundamentals

Dependency injection (DI) is a well defined basic theorem. There exist several reliable in-detail resources to read into it, so this documentation will only cover the very basic ideas behind it, especially when there are differences between the standard paradigms and the way Hura handles DI.

The base idea behind DI is that entity **A** needing entity **B** should not be obliged to create **B** itself. Instead, **A** should just declare being in need of [**B**] (an anonymous entity providing a certain set of functionality) and leave it to the DI framework to supply any type of entity that serves all of [**B**]'s requirements.

Using this simple idea, **A** limits itself to what it really requires (some entity providing the functionality of [**B**]), allowing the DI provider to loosely couple **A** to the rest of the application's entity environment.

Any change to the entity **B** will become completely unnoticeable to **A** as long as [**B**]'s feature set remains unchanged.

The entities administrated by the DI provider are usually called beans.

### 1.2 The Injection Sequence

Injecting a single entity in a DI environment means instantiating that entity, followed by resolving all of its dependencies, which might require more entities to be instantiated.

In Hura, this process is called an injection sequence. Such a sequence is triggered by instantiating a bean of any type. That bean becomes the sequence's injection root. Other beans that have to be instantiated in the process of instantiating the injection root will be registered as dependent to that root.

Any bean of an injection sequence can itself trigger another injection sequence by programmatically instantiating another bean. The bean instantiated with such a sub-sequence will become that sub-sequence's root. This process is repeatable arbitrarily, effectively forming what is called the injection tree; a tree structure of sequence's beans being the parent of sub-sequence's beans.

### 1.3 Bean Lifecycle

As described above, a bean's life starts with its own injection sequence instantiating it. A bean's life ends when its injection sequence's root is destroyed by the DI provider.

Those are the two events that frame every bean's lifecycle.

Depending on the DI provider, beans are able to react to different stages of their lifecycle, mostly at its begin and end. During the lifecycle, beans have acces to the DI provider's functionality.

When an injection sequence root is destroyed, the DI provider will also destroy all other beans injected in it. Because of the way injection sequences can have sub-sequences in Hura, this will also trigger the destruction of any sub-sequence (and its beans) that might have instantiated from beans of the parent sequence.

### 1.4 Singleton & Independent Beans

Singletons are single instances of a specific entity type that are able to serve all of an application's requirements to that type's functionality.

Hura breaks with the paradigm that every entity in a DI managed entity environment automatically is a singleton bean.

Instead, Hura only registers those beans as singletons who are explicitly named by a so called qualifier. All other beans are independent to Hura; they cannot be injected somewhere else than at the injection point were they are required. A sequence's injection root is an example for such an independent bean.

Hura handles bean visibility different than specified in the standard DI paradigm for a reason; it allows...
- multiple beans of the same type to coexist without any interference
- beans to only be referenced by exactly one other bean

Those premises are required for beans to carry state safely. While the standard DI paradigm also requires beans to be stateless, that requirement does not apply to Hura. 

The reason Hura also does not follow this paradigm is that visibility is not the only reason to manage an entity as a DI bean; the other reason is because the DI provider also manages each beans life cycle as described above. As a result, Hura allows stateful independent beans to react to being instantiated/destroyed or benefit from any other functionality Hura provides to beans managed by it.

## 2. Hura Basics

### 2.1 The Injector

The base for DI with Hura is die **_Injector_** class.

An **_Injector_** cannot be instantiated directly; it can only be injected itself, which ensures the context of an injection sequence to be passed on further down the injection tree. 

To start a new injection tree, the _**Injector**.of()_ methods can be used, which create a **_RootInjector_** instance instead. In addition to being a fully functional **_Injector_**, the **_RootInjector_** allows...
- to define a base injection context for the whole injection tree using the arguments of _**Injector**.of()_ upon the **_RootInjector_** being created
- to destroy the whole injection tree at once by calling _**RootInjector**.shutdown()_

All **_Injector_** instances, no matter the origin, allow to...
- aggregate existing instances of singleton beans of the **_Injector_**'s own injection sequence using the _**Injector**.aggregate()_ methods
- resolve properties of the **_Injector_**'s own injection sequence using the _**Injector**.resolve()_ methods
- inject new instances of beans in sub-sequences using one of the _**Injector**.instantiate()_ methods
- destroy beans instantiated by the _**Injector**_ by using either the _**Injector**.destroy()_ or _**Injector**.destroyAll()_ method

```java
RootInjector injector = Injector.of();
SomeType bean = injector.instantiate(SomeType.class);
Collection<SomeType> singletons = injector.aggregate(SomeType.class);
String property = injector.resolve("Hi, my name is ${property.key.name}");
injector.destroy(bean);
injector.shutdown();
```

Note that since the _**Injector**_ has a lifecycle as any other bean, it cannot be used anymore once it has been destroyed or shut down.

### 2.2 Blueprints and Allocations

The whole idea of DI is about loose coupling, but this requires injection points that actually allow for a loosely coupled injection; for example by requiring the injection of any class implementing an interface instead of requiring an instance of a concrete class type. Upon injecting such a loosely coupled environment though, the DI framework requires instructions on how to couple; in relation to the example from before, the interface implementing class has to be specified.

The no-op interface **_Blueprint_** allows implementing types that specify methods which may return any instance of the abstract class **_Allocation_** (or **_Collection_** thereof), which can be used to describe any kind of coupling definition Hura allows. Using this concept, a **_Blueprint_** can define an injection's context using an infinite amount of allocation returning methods.

For most situations, a Blueprint is required to define a context of couplings for an injection, but the **_Injector_** also allows supplying the allocations as a **_Collection_**, so implementing the **_Blueprint_** interface is not necessary.

##### 2.2.1 Property Allocations

The **_PropertyAllocation_** allows setting property keys to values, so Hura is able to resolve those keys during injection.

```java
public class SomeBlueprint implements Blueprint {

    @Define
    public PropertyAllocation define1() {
        return PropertyAllocation.of("property.key", "some value");
    }

    @Define
    public List<PropertyAllocation> define2() {
        return PropertyAllocation.of(new File(SomeType.class.getResource("/properties/some.properties").toURI()));
    }
}
```

##### 2.2.2 Type Allocations

Using the **_TypeAllocation_** a class type A can be bound to some kind of provider for the class B which has to be assignable to A.

When Hura needs to inject an instance of type A, an instance of type B will be injected instead. This makes the **_TypeAllocation_** the default approach for loose coupling by interfaces.

```java
public class SomeBlueprint implements Blueprint {

    @Define
    public TypeAllocation define1() {
        return TypeAllocation.allocateToType(A.class, B.class);
    }

    @Define
    public TypeAllocation define2() {
        return TypeAllocation.allocateToInstance(A.class, new B());
    }
}
```

##### 2.2.3 Singleton Allocations

The SingletonAllocation offers a way to bind beans to qualifiers in order to make them become singletons.

```java
import com.mantledillusion.injection.hura.core.Blueprint;public class SomeBlueprint implements Blueprint {
    @Define
    public SingletonAllocation define1() {
        return Blueprint.SingletonAllocation.allocateToType("SomeQualifier", SomeType.class);
    }

    @Define
    public SingletonAllocation define2() {
        return SingletonAllocation.allocateToInstance("SomeOtherQualifier", new SomeOtherType());
    }
}
```

##### 2.2.4 Alias Allocations

In rare cases it can be useful inject the singleton of a specific qualifier at an injection point where a singleton of a different qualifier is demanded.

In these cases, the AliasAllocation can help by assigning an alias to singleton's qualifier.

```java
public class SomeBlueprint implements Blueprint {

    @Define
    public AliasAllocation define() {
        return AliasAllocation.of("SomeQualifier", "SomeOtherQualifier");
    }
}
```

### 2.3 Injectability Of Classes

Before a class can be injected by Hura, it has to be instantiated in most cases.

Classes annotated with _@Context_ will not be instantiated by Hura; it is a flag for instances of those classes needing to be provided by an allocation.

```java
@Context
public class SomeContextualClass {
    
}
```

All other classes can be instantiated by Hura, as long as there is an injectable constructor available. A constructor is considered injectable if all of its parameters are injectable as well. If there are multiple constructors, use _@Construct_ to point Hura to the one that should be used for injection.

Hura is able utilize hidden constructors, as long as they are annotated with _@Construct_. This is a precautionary safety measure for private constructors. It allows developers to enforce...
- others to still not being able to instantiate a certain class (by not setting the annotation)
- instantiation of a class by injection only (by explicitly setting annotation)

```java
public class SomeSecretlyInjectableClass {

    private final String preset;

    @Construct
    private SomeSecretlyInjectableClass() {
        this.preset = "injected";
    }

    public SomeSecretlyInjectableClass(String preset) {
        this.preset = preset;
    }
}
```

## 3. Bean Injection

Instead of using the annotations provided by [JSR-330](https://jcp.org/en/jsr/detail?id=330), Hura uses its own set of injection annotations. The reason is that Hura has its own set of DI paradigms (chapter 1.) that differ from those of the JSR, so Hura would behave unexpectedly when injecting a class annotated with the JSR's annotations.

To inject an independent bean into another one, the Hura's _@Inject_ annotation can be used. It allowed to be used as either a constructor parameter or field annotation.

### 3.1 Injecting Independent Beans

To inject an independent bean, simply annotate a constructor parameter or field with _@Inject_ in a class that will be injected.

```java
public class SomeInjectableClass {

    @Inject
    private SomeOtherClass byField;
    private final SomeOtherClass byConstructor;

    public SomeSecretlyInjectableClass(@Inject SomeOtherClass byConstructor) {
        this.byConstructor = byConstructor;
    }
}
```

### 3.2 Injecting and Aggregating Singletons Beans

To inject a singleton, add _@Qualifier_ to a constructor parameter or field already annotated with _@Inject_ and provide a name for the singleton.

The same instance with automatically be injection on all injection points with the same qualifier in a sub-part of an injection tree.

```java
public class SomeInjectableClass {

    @Inject
    @Qualifier("byFieldSingleton")
    private SomeOtherClass byField;
    private final SomeOtherClass byConstructor;

    public SomeSecretlyInjectableClass(@Inject @Qualifier("byConstructorSingleton") SomeOtherClass byConstructor) {
        this.byConstructor = byConstructor;
    }
}
```

Note that **_Injector_** instances can never be singletons; since they contain information about their parent injection sequence, injecting it in multiple sequences would carry its injection context out of itself.

### 3.3 Optional Injections

By default, when annotating a constructor parameter or field with @Inject, the injection is meant to be **_InjectionMode_.EAGER**, which effectively means that the injection has to be fulfillable. If it is not (which might happen if an allocation is required but not available), an exception is thrown, because Hura is not able to inject the parameter/field.

If an injection should only be performed if it can be fulfilled, the annotation _@Optional_ can be added additionally to _@Inject_, which then equals to **_InjectionMode_.EXPLICIT**. If an optional injection cannot be fulfilled, Hura will leave the parameter/field null instead of throwing an exception.

```java
public class SomeInjectableClass {

    @Inject
    @Optional
    private SomeOtherClass byField;
    private final SomeOtherClass byConstructor;

    public SomeSecretlyInjectableClass(@Inject @Optional SomeOtherClass byConstructor) {
        this.byConstructor = byConstructor;
    }
}
```

### 3.4 Injection in Sub-Sequences

Sub-sequences are created by injecting an **_Injector_** with _@Inject_ instead of creating a **_RootInjector_** manually.

Every call of an _**Injector**.instantiate()_ method of an injected **_Injector_** will create a new sub-sequence to the injection sequence that has injected the **_Injector_**. This scheme is what creates an injection tree.

```java
public class SomeClassWithInjector {

    @Inject
    private Injector injector;

    public SomeSubSequenceBean create() {
        return this.injector.instantiate(SomeSubSequenceBean.class);
    }
}
```

### 3.5 Aggregating Singletons

Since singletons are named by their qualifier, they become addressable even after the injection sequence has finished because they reside in a singleton pool.

From that pool, singletons are retrievable using the singleton aggregation functionality.

Singleton aggregation works either through...
- the _@Aggregate_ annotation
- by calling the _**Injector**.aggregate()_ method of an injector that has been injected in the same or a downstream injection sequence as the singleton to aggregate

Both the annotation and the _**Injector**_ allow to supply **_Predicate_** implementations for filtering through the singleton pool.

 ```java
public class SomeAggregatingClass {

    @Inject
    private Injector injector; 
    @Aggregate(qualifierMatcher = "beanNumber//d+")
    private Collection<Object> byField;
    
    private void aggregateManually() {
        Collection<Serializable> byCall = this.injector.aggregate(SomeBeanSuperType.class, 
            (qualifier, bean) -> bean.hasSomeCharacteristic());
    }
}
 ```

Note that in order to provide access to <u>all</u> singletons assigned during an injection sequence, aggregation is done right before the **_Phase_.POST_CONSTRUCT** milestone of injection, so this will be the point from which on aggregations are possible.

## 4. Property Injection

Hura is able to resolve values from properties and inject them just like Hura injects beans using the _@Resolve_ annotation.

Properties in Hura are essentially String/String key/value pairs. Those pairs are pooled in a property context which is passed down the injection tree, so sub-sequences will essentially inherit all properties its parent sequence owned.

### 4.1 Resolving Values

To resolve a value from a property, simply annotate a constructor parameter or field with _@Resolve_ in a class that will be injected.

The standard type for value to be resolved is String.

Hura will parse values from any String contained by _@Resolve_ after resolving all placeholders in that String.

```java
public class SomeClassWithProperty {

    // If the property "property.key.value" is set to "4", this will contain "The value is 4"
    @Resolve("The value is ${property.key.value}")
    private String value; 
}
```

Hura allows placeholders to be nested; in those cases, the framework will resolve the placeholders from the deepest to the topmost, which allows placeholders to be assembled from resolved lower level placeholders.

```java
public class SomeClassWithDeepProperty {

    // There might be multiple properties like "property.key.myEnum.values.ONE" with translations to 
    // enum values and the value to translate is contained by the other property "property.key.enumValue", 
    // so this nested resolving will directly resolve to the correct translation
    @Resolve("${property.key.myEnum.values.${property.key.enumValue}}")
    private String value;
}
```

### 4.2 Optional Values

If Hura cannot resolve a placeholder from a property, an exception will be thrown.

In cases where a value is not required if its not resolvable, the constructor parameter / field annotated with @Resolve can additionally be annotated with _@Optional_, which will instruct Hura to just leave the value null.

```java
public class SomeClassWithOptionalProperty {

    @Optional
    @Resolve("${property.key.maybeUnsetProperty}")
    private String value; 
}
```

### 4.3 Default Values

Instead of declaring a value optional just in case at least one placeholder is not resolvable, each placeholder is allowed to have a default value after a colon.

Just as with the normal values, the default value can contain a placeholder as well.

```java
public class SomeClassWithDefaultedProperty {

    // If both the properties "property.key.maybeUnsetProperty" and "property.key.mayAlsoBeUnsetProperty" 
    // are not set, this will contain "default value"
    @Resolve("${property.key.maybeUnsetProperty:${property.key.mayAlsoBeUnsetProperty:defaultValue}}")
    private String value; 
}
```

### 4.4 Resolving Matchers

In some situations a resolved value is expected to be of a very specific pattern; for example, the value String is expected to contain a number or a loosely valid email address format.

For these cases the _@Matches_ annotation can be used, which allows the readily resolved value to be matched against a regex pattern.

```java
public class SomeClassWithProperty {

    @Resolve("${property.key.value}")
    @Matches("//d+")
    private String positiveNumericInteger; 
}
```

### 4.5 Propertied Injection Settings

Hura allows properties to be used in a lot String fields of the annotations the framework offers; in Hura, such fields are called resolvable values.

The most basic of these is _@Resolve_, which uses this mechanism to fill its resolved value into a constructor parameter or field.

But a lot more of Hura's framework annotations actually have one or more String fields that are resolvable values:
- _@Aggregate_
    - qualifierMatcher
- _@Matches_
    - value
- _@Plugin_
    - directory
    - pluginId
    - versionFrom
    - versionTo
- _@Qualifier_
    - value
- _@Resolve_
    - value

Using a propertied value instead of a static one offers a huge amount of configurability; for example, the plugin directory suddenly becomes easily configurable.

## 5. Plugin Injection

Hura is able to inject beans whose classes originate from plugins.

For Hura plugins have to be packed like specified in the Java SPI (Service Provider Interface) definition:
- packaged as JAR file
- a file under /META-INF/services/ for every service the plugin provides, with
    - the fully qualified class name of the allocated type (Service Provider Interface) as file name
    - the fully qualified class name of the implementing type (Service Provider) as text content

To inject a plugin the _@Plugin_ annotation is used instead of _@Inject_.

### 5.1 Injecting Plugins

A plugin can be injected by annotating the constructor parameter or field of the allocated type (Service Provider Interface) with _@Plugin_.

First the plugin JAR file needs to be placed in a valid location and requires at least the SPI service file to exist and contain the name of the implementing class:

<code>SomePlugin.jar/META-INF/services/com.mantledillusion.example.ServiceProviderInterface</code>
```text
com.mantledillusion.example.impl.ServiceProvider
```

In the Hura injected code, the _@Plugin_ annotation accepts the name of the plugin and a directory where to load the plugin from.

```java
public class SomeClassWithPlugin {

    @Plugin(directory = "src/main/resources/plugins", pluginId = "SomePlugin")
    public ServiceProviderInterface plugin;
}
```

### 5.2 Versioning Plugins

Hura allows the versioning of plugins.

It retrieves a plugin's version from its file name postfix that has to be formatted like SomePlugin_V1.jar for the version 1 or SomePlugin_V1.20.3.jar for the version 1.20.3; a plugins version is allowed to have as many sub versions as required. 

Note that even a plugin without such a postfix has a version for Hura: it is simply considered to be of the version 0.

Hura will parse a version from the plugin file's name and considers the prefix before the version to be the plugin ID the _@Plugin_ annotation requires.

If Hura encounters multiple plugins of the same plugin ID in a directory, all of the plugin's versions are parsed and the one with the highest version is injected. The _@Plugin_ annotation allows restricting the version range that found plugins are allowed to be in in order to be considered injectable at an injection point.

```java
public class SomeClassWithPlugin {

    @Plugin(directory = "src/main/resources/plugins", pluginId = "SomePlugin", versionFrom = "2", versionUntil = "3")
    public ServiceProviderInterface plugin;
}
```

<code>src/main/resources/plugins/SomePlugin_V1.7.4.jar</code> < ignored (out of range)<br>
<code>src/main/resources/plugins/SomePlugin_V2.2.10jar</code> < ignored (outdated)<br>
<code>src/main/resources/plugins/SomePlugin_V2.3.jar</code> < injected<br>
<code>src/main/resources/plugins/SomePlugin_V3.2.11.jar</code> < ignored (out of range)<br>

## 6. Bean Lifecycle

With beans being instantiated, injected and destroyed by DI frameworks, beans have a natural lifecycle during which they are allowed to be used.

The Java language provides the two method annotations _@PostConstruct_ and _@PreDestroy_ through [JSR-330](https://jcp.org/en/jsr/detail?id=330), which enable execution of code right after the start and right before the end of a beans life cycle.

Hura allows the same kind of life cycle bean code execution, but through more than just annotated methods and on a finer grained life cycle milestone level.

Depending on the milestone at which the code is executed, the bean to execute the code on might not yet be available. Also Hura will provide a **_TemporalInjectorCallback_** instance when executing code for some milestones; it allows performing manual injection operations in the milestone's currently running injection sequence.

### 6.1 Lifecycle Milestones

The lifecycle milestones at which Hura is able to execute code are defined by the **_Phase_** enumeration.

##### 6.1.1 PRE_CONSTRUCT
- Bean available: No
- **_TemporalInjectorCallback_** available: Yes

The milestone right before the bean is instantiated.

##### 6.1.2 POST_INJECT
- Bean available: Yes
- **_TemporalInjectorCallback_** available: Yes

The milestone right after the bean has been instantiated and all downstream beans have been injected.

The injection sequence <u>is not</u> completed at this point; for example, the bean's parent bean might still require other child beans to be injected into it.

##### 6.1.3 POST_CONSTRUCT
- Bean available: Yes
- **_TemporalInjectorCallback_** available: No

The milestone right after the bean has been instantiated and all downstream and upstream beans have been injected.

The injection sequence <u>is</u> completed at this point; the injection of all of the sequence's beans has been completed.

##### 6.1.4 PRE_DESTROY
- Bean available: Yes
- **_TemporalInjectorCallback_** available: No

The milestone right before the bean being destroyed.

##### 6.1.5 POST_DESTROY
- Bean available: Yes
- **_TemporalInjectorCallback_** available: No

The milestone right after the bean and all other beans of the same injection sequence have been destroyed.

### 6.2 Lifecycle Code Execution Options

While the Java language's specification only allows bean methods to be annotated to be executed during a lifecycle milestone, Hura provides way more flexible ways to execute code on a bean.

##### 6.2.1 Method & Class Annotations

The package _com.mantledillusion.injection.hura.core.lifecycle.bean_ contains one annotation for each injection milestone that can be used on both methods and classes.

The annotations allow an array of **_BeanProcessor_** implementing types to be defined; when their code has to be executed because of their milestone being reached, those types will be be injected, executed and destroyed.

When used on a class, the annotations will execute the code of the given processors when the milestone is reached.

When used on a method, the annotations will also execute the code of the given processors when the milestone is reached, but in addition to that the method will be called. Depending on the **_Phase_** of the annotation used to annotate the method, the method is allowed to have certain types of parameters or injection annotations on parameters.

```java
public class SomeProcessedClass {

    @PostInject
    private void processThroughMethodAnnotation(Phase phase, TemporalInjectorCallback callback, @Inject SomeBeanClass bean) {
        // do whatever
__    }
}
```

Note that _@PreConstruct_ can <u>not</u> be used on methods; since the bean instance is not available on that milestone, there will not be an instance whose method would be callable.

Using these annotations can be extremely useful by being placed on an interface's class, so all beans made from implementing types will be processed automatically.

```java
public class SomeBeanProcessor implements BeanProcessor<SomeProcessedInterface> {

    void process(Phase phase, SomeProcessedInterface bean, TemporalInjectorCallback callback) throws Exception {
        bean.processThroughClassAnnotation();
    }
}
```

```java
@PostConstruct(value = SomeInterfaceProcessor.class)
public interface SomeProcessedInterface {
    
    void processThroughClassAnnotation();
}
```

The **_BeanProcessor_** interface has parameters for the bean and a **_TemporalInjectorCallback_**, but these parameters will only be supplied when the processor is used in certain **_Phase_**'s annotations.

##### 6.2.2 Annotation Annotations

The package _com.mantledillusion.injection.hura.core.lifecycle.annotation_ contains one annotation for each injection milestone that can be used on other annotations.

The annotations allow an array of **_AnnotationProcessor_** implementing types to be defined; when their code has to be executed because of their milestone being reached, those types will be be injected, executed and destroyed.

When used on an annotation which can be used anywhere on a bean's class, the annotations will execute the code of the given processors when the milestone is reached. The bean, the annotated annotation's instance and the element annotated by that instance will be supplied to the processor.

```java
@Retention(RUNTIME)
@Target({TYPE})
@PostConstruct(LifecycleAnnotationProcessor.class)
public @interface SomeCustomAnnotation {

}
```

```java
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;

public class SomeAnnotationProcessor implements AnnotationProcessor<SomeCustomAnnotation, Class<?>> {

    void process(Phase phase, Object bean, SomeCustomAnnotation annotationInstance, Class<?> annotatedElement, TemporalInjectorCallback callback) throws Exception {
        // do whatever
    }
}
```

```java
@SomeCustomAnnotation
public class SomeProcessedClass {

}
```

Using these annotations can be extremely useful for creating own annotations, because they basically allow building custom framework functionality.

The **_AnnotationProcessor_** interface has parameters for the bean and a **_TemporalInjectorCallback_**, but these parameters will only be supplied when the processor is used in certain **_Phase_**'s annotations.

##### 6.2.3 BeanProcessors in Allocations

When creating either a **_TypeAllocation_** or a **_SingletonAllocation_** for a type, an array of **_PhasedBeanProcessor_** instances can be provided to be executed at a certain milestone.

```java
public class SomeBeanProcessor implements BeanProcessor<SomeUnchangeableType> {

    void process(Phase phase, SomeUnchangeableType bean, TemporalInjectorCallback callback) throws Exception {
        bean.someUnchangeableMethod();
    }
}
```

```java
public class SomeBlueprint implements Blueprint {

    @Define
    public Blueprint.TypeAllocation allocate() {
        return TypeAllocation.allocateToType(Serializable.class, SomeUnchangeableType.class, PhasedBeanProcessor.of(new SomeBeanProcessor(), Phase.POST_CONSTRUCT));
    }
}
```

```java
public final class SomeUnchangeableType {

    public final void someUnchangeableMethod() {
        // whatever
    }
}
```

This is especially useful when the allocated type is a class that cannot be modified to be annotated with a lifecycle annotation.

## 7. Events

Hura is able to automatically connect every injected bean over an event bus. The bus is able to distribute events over the complete injection tree.

### 7.1 Sending Events

An instance of the type **_Bus_** can be injected into any arbitrary bean. Using its _**Bus**.publish()_ methods any kind of event can be send into the event bus.

```java
public class SomeClassWithEventBus {

    @Inject
    private Bus bus;
}
```

By default, an event send by any bean in an injection tree can be received by any other bean.

By setting the property **_Bus_.PROPERTY_BUS_ISOLATION** to true, every **_Bus_** injected with this property set will limit receiving events send through this bus to beans of the same or downstream injection sequences. Additionally, this functionality can be enforced in the moment of sending events by switching isolation on manually when calling _**Bus**.publish()_.

### 7.2 Receiving Events

To receive events, any method can be annotated with _@Subscribe_.

```java
public class SomeClassWithEventBusSubscription {

    @Subscribe
    public void receive(Object o) {
        // do whatever
    }
}
```

The subscription to the event bus will be performed either...
- ...for the methods 1 allowed parameter's type
- ...for all of the types given to the _@Subscribe_ annotation

A registration for a type will result in all events of the same or sub any class to be delivered to the method.