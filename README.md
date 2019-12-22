# hura
Hura is a dependency injection framework focused on sequential injection and bean lifecycle management.

Hura Crepitans, also called 'Sandbox Tree' and nicknamed 'Dynamite Tree', 
is a plant mainly living in South America. Its fruits are pumpkin-shaped capsules 
that undergo huge amounts of tension when drying out during dry season. 
On the first rain, when the surface of the fruit gets wet and softens up, 
the dry seeds inside break through the shell with a loud bang, 
being sling shotted over a distance for more than 300 feet.

## Packages

Hura is separated into different packages to make it possible using only certain parts of it.

**See the REAME.md in each package for detailed documentation**.

### Hura Core

Contains the generic part of the injection framework and most of its functionality.

### Hura Web

Combines Hura Core with the Servlet API 4 for building deployable web application WARs.

### Hura WebLaunch

Combines Hura Web with the embedded web server Undertow for building runnable web application JARs.