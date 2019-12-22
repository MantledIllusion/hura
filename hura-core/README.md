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