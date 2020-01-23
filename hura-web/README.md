# Hura Web

Hura's web combines the injection functionality of the core with the Java Servlet 4 specification.

## 1. Running Hura injected web applications

### 1.1 Launching the application

To launch a web application injected by Hura, implement the interface **_HuraWebApplicationInitializer_**, which...
- is marked as a **_ServletContainerInitializer_**, so it will be picked up by servlet container providers
- extends Hura's **_Blueprint_** to allow defining the web environment of the application using dependency injection

```java
public class SomeWebApplication implements HuraWebApplicationInitializer {
    // define stuff here
}
```

### 1.2 Running the application

The **_HuraWebApplicationInitializer_** implementation is injected by a **_RootInjector_** by Hura Web, allowing the beans defined by the **_Blueprint_** to retrieve an **_Injector_** instance injected by the applications root injection sequence.

From there, the beans can build up and tear down the application arbitrarily in as many sub sequences as desired when requests are retrieved by components of the application's entities.

### 1.3 Shutting the application down

As mentioned before, the **_HuraWebApplicationInitializer_** is being injected by a **_RootInjector_** on application startup.

When the servlet container provider shuts down, that **_RootInjector_** is shut down by Hura Web. As a result, the initializer and all beans originating from it will be destroyed automatically when the application shuts down, effectively cleanly ending their natural bean life cycle.

## 2. Defining the Web Environment

The environment of a web application consists of servlet specification entities registered as the container provider, with the main entity being the **_Servlet_**.

Hura web allows such entities to be defined by creating singleton beans implementing **_ServletContextConfiguration_**. The interface can be implemented freely, but the **_WebEnvironmentFactory_** provides implementations for standard use cases.

**_SingletonAllocation_** instances defined by a **_HuraWebApplicationInitializer_** implementation holding a bean implementing **_ServletContextConfiguration_** will be automatically registered with the servlet container provider:

```java
public class SomeWebApplication implements HuraWebApplicationInitializer {
    
    @Define
    public Blueprint.SingletonAllocation defineServlet() {
        return WebEnvironmentFactory.registerServlet(SomeServlet.class).build();
    }
}
```