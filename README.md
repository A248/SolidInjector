
# SolidInjector

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

The JPMS Module name is `space.arim.injector`.

The dependency is

```
space.arim.injector:injector:{VERSION}
```

The repository is

```
https://mvn-repo.arim.space/lesser-gpl3/
```

A thanks to Cloudsmith for providing free repositories for FOSS.

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
Plane plane = injector.create(Plane.class);
```

### Private and Static Injection

By default, only public instance members (constructors, fields, and methods) are injected. To enable private injection, use the InjectorBuilder:

```java
Injector injector = new InjectorBuilder().privateInjection(true).build();
Plane plane = injector.create(Plane.class);
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

Plane plane = injector.create(Plane.class);
Train train = injector.create(Train.class);
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

