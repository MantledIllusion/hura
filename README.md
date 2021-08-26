# Hura
Hura is a dependency injection framework focused on sequential injection and bean lifecycle management.

Hura Crepitans, also called 'Sandbox Tree' and nicknamed 'Dynamite Tree', is a plant mainly living in South America. Its fruits are pumpkin-shaped capsules that undergo huge amounts of tension when drying out during dry season. On the first rain, when the surface of the fruit gets wet and softens up, the dry seeds inside break through the shell with a loud bang, being sling-shotted over a distance for more than 300 feet.

```xml
<dependency>
    <groupId>com.mantledillusion.injection</groupId>
    <artifactId>hura-parent</artifactId>
    <type>pom</type>
</dependency>
```

Get the newest version at [mvnrepository.com/hura](https://mvnrepository.com/artifact/com.mantledillusion.injection/hura-parent)

## Advantages 

As mentioned above, Hura as an injection framework specializes on a couple of advanced aspects of dependency injection:

### Automatic Scoping using Injection Sequences
Hura defines a set of referencing beans as a scope and the single injection injecting that set as an injection sequence. 

Using an instance of the class **_Injector_** injected into any bean of a sequence's scope, Hura allows triggering child injection sequences programatically, which automatically will be recognized as sub-sequences to the parent sequence that injected the **_Injector_** and therefor defining a sub-scope of beans. 

By trigging further injections from sub-sequences, the scopes will form the so called injection tree, with the allowed depth of the tree being unlimited. Inside the injection tree, beans of any scope have access to all beans of their parent scope (upstream) and naturally will have access to beans of all child scopes (downstream), but will not have any access to beans of scopes in different branches of the tree.

While not effecting basic one-scope injections like application level singleton meshes, this visibility principle is extremely versatile for injecting any type of structure that requires on the fly injection/destruction with bean pool separation like stateful request handling, MVP view assembly or contexted testing.

### Bean Lifecycle Management
In addition to plainly allowing code execution on a bean after injecting its scope has been injected, Hura also provides:
- Executing code before a bean's class is even instantiated
- Executing code after a bean has been instantiated and injected
- Executing code after a bean's scope has been injected completely
- Executing code before a bean's scope is destroyed
- Executing code after the bean's scope is destroyed

Hura allows hooking to any of these lifecycle milestones by:
- Hura Annotations on a bean's methods, either on the bean's direct class, its super class or any of its implemented interfaces
- Hura Annotations on a bean's class referencing an external processor, either on the bean's direct class, its super class or any of its implemented interfaces
- Hura Annotations on custom method annotations referencing an external processor, with the custom annotation then used on the bean's direct class, its super class, any of its implemented interfaces
- Hura Annotations on custom class annotations referencing an external processor, with the custom annotation then used on the bean's direct class, its super class, any of its implemented interfaces
- Independent processor registrations for a specific bean type broadly defined for the injection sequence

### Leightweight

Hura has barely any dependencies to other frameworks or libraries, keeping it extremely lightweight.

### Transparency

Instead of autonomously doing a whole bunch of intransparent magic trying to desperately interpret any possible use case the developer might have, Hura only executes what is explicitly expressed.

Developer convenience in Hura is not archived by performing brute-force style what-if checks under the hood, but by a simple, intuitive and supportive Java API that requires the littlest configuration for standard use.

This also favors all non-standard scenarios, as the developer can expect complex cases to be handled in the exact same place of the API with only a few more expressions, instead of trying to fumble around with loose configurations in order to get the under the hood magic to behave differently.

## Packages

### Hura Core

Contains the generic core of the injection framework and most of its functionality around injection, property resolving and bean life cycle handling.

See the [documentation](https://github.com/MantledIllusion/hura/blob/master/hura-core/README.md) for more details.

### Hura Web

Combines Hura Core with Java's Servlet API 4 for building deployable web application WARs.

See the [documentation](https://github.com/MantledIllusion/hura/blob/master/hura-web/README.md) for more details.

### Hura WebLaunch

Combines Hura Web with an Undertow embedded web server for building runnable lightweight web application JARs.

See the [documentation](https://github.com/MantledIllusion/hura/blob/master/hura-weblaunch/README.md) for more details.
