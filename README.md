
# SolidInjector [![Maven Central](https://img.shields.io/maven-central/v/space.arim.injector/injector?color=brightgreen&label=maven%20central)](https://mvnrepository.com/artifact/space.arim.injector/injector) [![Javadoc](https://javadoc.io/badge2/space.arim.injector/injector/javadoc.svg)](https://javadoc.io/doc/space.arim.injector/injector)

Modern and lightweight injector supporting javax and jarkarta.

## Introduction

I was looking for an implementation of the javax.inject or jakarta.inject specification for a small project I was making. However, the ones I found were either too bulky and over-the-top, or abandoned by the author.

### Features

* Supports javax.inject and jakarta.inject.
* Easy to configure.
* Provides `module-info` while remaining Java 8 compatible.
* Compliant with the specification.
* Permits constructor, field, and method injection.

There are no dependencies except JDK 8, and a choice of javax.inject or jakarta.inject.

### Dependency

The dependency is `space.arim.injector:injector`. It is deployed to Central.

For those using JPMS, the module name is `space.arim.injector`

Though the public API is small, the javadocs may be browsed using [here](https://javadoc.io/doc/space.arim.injector/injector) or in your IDE.

## Injection Usage

### Basic Use

Concrete classes are instantiated directly, either using the default constructor, or through the public constructor annotated with `@Inject`.

```java
public class Plane {

	private final Wing wing;

	@Inject
	public Plane(Wing wing) { // Wing is automatically instantiated
		this.wing = wing;
	}
}

public class Wing {
	
	/*
	 * Define further dependencies in constructor, fields, or methods
	@Inject
	public Wing(Foil someOtherDependency) {
		
	}
	*/
	
	// This class can be instantiated whether or not the constructor is commented out
	
}
```

There can be at most 1 constructor annotated with `@Inject`.

To instantiate a Plane:

```java
Injector injector = Injector.newInjector();
Plane plane = injector.request(Plane.class);
```

### Private and Static Injection

By default, only public instance members (constructors, fields, and methods) are injected. To enable private injection, use the InjectorBuilder:

```java
Injector injector = new InjectorBuilder().privateInjection(true).build();
Plane plane = injector.request(Plane.class);
```

* When using JPMS, private injection requires modules to be *open* to SolidInjector.
* Static injection can also be enabled using InjectorBuilder, but static injection is highly discouraged.

### Bindings

Abstract classes and interfaces require bindings. This is best demonstrated with an example.

Take the following `TrafficControl` interface, and an implementation `NoOpTrafficControl`:

```java
public interface TrafficControl {

	boolean canLand(Plane plane);
	
}

public class NoOpTrafficControl implements TrafficControl {

	@Override
	public boolean canLand(Plane plane) {
		// Uh-oh!
		return true;
	}

}
```

This would be idiomatically bound with:

```java
public class AirportModule {

	public TrafficControl trafficControl(NoOpTrafficControl trafficControl) {
		return trafficControl;
	}
	
}
```

You may add as many modules to the injector as desired. Ordering is unimportant:

```java
Injector injector = Injector.newInjector(new AirportModule(), new TrainStationModule());

Plane plane = injector.request(Plane.class);
Train train = injector.request(Train.class);
```

**Binding Method Details**

* Method parameters are treated as further dependencies.
* All public instance methods, declared and inherited, will be used.

### Provider

javax.inject.Provider or jakarta.inject.Provider may be used to break circular dependencies or for deferred retrieval. 

For any type `T`, you can also inject `Provider<T>` whose get() method will return an instance of T.

### Singleton

Per the javax/jakarta spec:
* Concrete classes annotated with @Singleton will be instantiated at most once.

With regards to SolidInjector:
* Binding methods annotated with @Singleton will be called at most once.

### Circular Dependencies

Circular dependencies are detected, and an exception is thrown. `Provider` can be used to break circular dependencies. Alternatively, consider refactoring.

### JPMS

jakarta.inject has an 'Automatic-Module-Name' and is therefore safe to use on the modular classpath.

javax.inject is in somewhat of a predicament. The original JSR-330 does not define its module name. However, the unofficial continuation of javax.inject under Jakarta EE 8 does define `java.inject` as the module name, see [here](https://github.com/eclipse-ee4j/injection-api/issues/14). This is also the logical module name which was discussed for JSR-330.

Therefore, when using javax.inject in a modular environment, use the 'java.inject' module name and the Jakarta EE 8 continuation of javax.inject

### Extra Features outside the specification

These features fall outside the official specification. They must be explicitly enabled by configuring the InjectorBuilder:
* `injectorBuilder.multiBindings(true)` for multi-binding
* `injectorBuilder.optionalBindings(true)` for optional binding

**Multiple bindings**

Sometimes you may want to bind multiple implementations, and inject a `Set` of such instances.

To use this feature, use `@MultiBinding` on bind methods: multiple such bind methods may exist associated to the same interface.

```java
public class MultiBindingModule {
	@MultiBinding
	public MyService provider1(MyServiceImpl1 impl1) {
		return impl1;
	}

	@MultiBinding
	public MyService provider2(MyServiceImpl2 impl2) {
		return impl2;
    }
}
```

Request all implementations as a `Set`, again specifying `@MultiBinding`:

```java
public class MyDependent { 
	private final Set<MyService> myServices;

	public MyDependent(@MultiBinding Set<MyService> myServices) {
		this.myServices = myServices;
	}
}
```

You must use `@MultiBinding` on bind methods *and* at injection points.

**Optional Bindings**

An intuitive feature. If the binding is present, the optional is present. Otherwise the optional is empty.

```java
public class MyDependent {
    @Inject
    public void injectOptionalService(Optional<MyService> optionalService) {
        if (optionalService.isPresent()) {
            MyService impl = optionalService.get();
            System.out.println("MyService is bound to " + impl);
        } else {
            System.out.println("MyService is not bound");
        }
    }
}
```

For use with `Provider`, inject a `Optional<Provider<T>>`. Note that `Provider<Optional>` is *not* supported.

*Warning*: Do not mix Optional requests with non-optional requests for concrete classes:
  * That is, your dependency graph should not contain both `MyConcreteClass` and `Optional<MyConcreteClass>` if `MyConcreteClass` is instantiable.
  * If you do this, you subject yourself to the arbitrary and unpredictable instantiation order of your dependency graph. When `Optional<MyConcreteClass` is requested, if `MyConcreteClass` was requested first, you will receive the instance. However, if `MyConcreteClass` was not previously requested, you will receive an empty optional.
  * This happens because `MyConcreteClass` is instantiated automatically when it is requested, but optional requests do not cause automatic instantiation.
