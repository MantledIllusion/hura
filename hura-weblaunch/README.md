# Hura WebLaunch

Hura's WebLaunch setup combines the web application injection functionality of Hura Web with an embedded application server.

## 1. Running a Hura injected web application server

### 1.1 Launching the application

The embedded application server allows launching a Hura WebLaunch application from a simple static Java _main()_ method.

The class **_HuraWeblaunchApplication_** allows building and launching a web application using the _**HuraWeblaunchApplication**.build()_ method by providing an implementation of Hura Web's **_HuraWebApplicationInitializer_**.

```java
public class SomeApplication {

    public static void main(String... args) {
        HuraWeblaunchApplication.build(SomeInitializer.class).startup(args);
    }
}
```

### 1.2 Configuring the web server

The _**HuraWeblaunchApplication**.build()_ method returns an instance of **_HuraWeblaunchApplicationBuilder_**, which allows configuring the application server using factory methods.