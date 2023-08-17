/*
 * SolidInjector
 * Copyright © 2023 Anand Beh
 *
 * SolidInjector is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SolidInjector is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SolidInjector. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */

package space.arim.injector;

import space.arim.injector.error.InjectorException;
import space.arim.injector.error.MisconfiguredBindingsException;
import space.arim.injector.internal.InjectionSettings;
import space.arim.injector.internal.InjectorConfiguration;
import space.arim.injector.internal.InjectorImpl;
import space.arim.injector.internal.provider.MultiBindingProviderMap;
import space.arim.injector.internal.provider.ProviderMap;
import space.arim.injector.internal.provider.SimpleProviderMap;
import space.arim.injector.internal.spec.SpecSupport;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Builder of {@link Injector}s. Not thread safe.
 * 
 * @author A248
 *
 */
public final class InjectorBuilder {

	private SpecificationSupport specification = SpecificationSupport.AUTO_DETECT;
	private final Set<Object> bindModules = new HashSet<>();
	private final Map<Identifier<?>, Identifier<?>> boundImplementors = new HashMap<>();
	private final Map<Identifier<?>, Object> boundInstances = new HashMap<>();
	private boolean privateInjection;
	private boolean staticInjection;
	private boolean multiBindings;
	private boolean optionalBindings;

	/**
	 * Sets the specification to support ({@code javax.inject} or
	 * {@code jakarta.inject}). By default, it is auto detected.
	 * 
	 * @param specification the specification
	 * @return this builder
	 */
	public InjectorBuilder specification(SpecificationSupport specification) {
		this.specification = specification;
		return this;
	}

	/**
	 * Sets whether to enable <i>deep reflection</i> to inject into
	 * non{@literal -}public members. Disabled by default.
	 * 
	 * @param privateInjection whether to to inject into non{@literal -}public
	 *                         members
	 * @return this builder
	 */
	public InjectorBuilder privateInjection(boolean privateInjection) {
		this.privateInjection = privateInjection;
		return this;
	}

	/**
	 * Sets whether to enable injection of static methods and fields. Disabled by
	 * default. <br>
	 * <br>
	 * <b>Use of static injection is heavily discouraged, as it defeats much of the
	 * purpose of dependency injection. This feature is provided solely for
	 * compatibility with legacy programs.</b> <br>
	 * <br>
	 * Note: static members are injected at most once per VM.
	 * 
	 * @param staticInjection whether to enable static injection
	 * @return this builder
	 */
	public InjectorBuilder staticInjection(boolean staticInjection) {
		this.staticInjection = staticInjection;
		return this;
	}

	/**
	 * Sets whether to enable the multibinding feature. Disabled by default. <br>
	 * <br>
	 * Note that the multibinding feature is outside the specification. It is suggested
	 * to leave disabled unless otherwise necessary.
	 *
	 * @param multiBindings whether to enable multiple bindings
	 * @return this builder
	 */
	public InjectorBuilder multiBindings(boolean multiBindings) {
		this.multiBindings = multiBindings;
		return this;
	}

	/**
	 * Sets whether to enable the optional bindings feature. Disabled by default. <br>
	 * <br>
	 * Note that the optional bindings feature is outside the specification. It is suggested
	 * to leave disabled unless otherwise necessary.
	 *
	 * @param optionalBindings whether to enable optional bindings
	 * @return this builder
	 */
	public InjectorBuilder optionalBindings(boolean optionalBindings) {
		this.optionalBindings = optionalBindings;
		return this;
	}

	/**
	 * Adds the specified bind modules to this injector builder
	 * 
	 * @param bindModules the bind modules
	 * @return this builder
	 */
	public InjectorBuilder addBindModules(Object... bindModules) {
		return addBindModules(Arrays.asList(bindModules));
	}

	/**
	 * Adds the specified bind modules to this injector builder
	 *
	 * @param bindModules the bind modules
	 * @return this builder
	 */
	public InjectorBuilder addBindModules(Collection<?> bindModules) {
		for (Object bindModule : bindModules) {
			Objects.requireNonNull(bindModule, "bind module");
			this.bindModules.add(bindModule);
		}
		return this;
	}

	private void checkUnbound(Identifier<?> identifier) {
		Identifier<?> previousImpl = boundImplementors.get(identifier);
		if (previousImpl != null) {
			throw new MisconfiguredBindingsException("Binding already exists for identifier " + identifier
					+ " (previous binding is implementor " + previousImpl + ")");
		}
		Object previousInstance = boundInstances.get(identifier);
		if (previousInstance != null) {
			throw new MisconfiguredBindingsException("Binding already exists for identifier " + identifier
					+ " (previous binding is instance " + previousInstance + ")");
		}
	}

	private <U> void bindImplementor0(Identifier<U> identifier, Identifier<? extends U> implementor) {
		if (identifier.equals(implementor)) {
			throw new IllegalArgumentException("Implementor must not be the same");
		}
		if (!identifier.getType().isAssignableFrom(implementor.getType())) {
			throw new ClassCastException("Implementor identifier type must be compatible");
		}
		checkUnbound(identifier);
		boundImplementors.put(identifier, implementor);
	}

	private <U> void bindInstance0(Identifier<U> identifier, U instance) {
		identifier.getType().cast(instance);
		checkUnbound(identifier);
		boundInstances.put(identifier, instance);
	}

	/**
	 * Binds an identifier to another. The types must be compatible.
	 *
	 * @param <U>        the type to bind
	 * @param identifier the identifier of the type
	 * @param implementor the implementation identifier
	 * @return this builder
	 * @throws MisconfiguredBindingsException if the identifier is known to already be bound
	 * @throws IllegalArgumentException if the implementor is the same as the identifier
	 */
	public <U> InjectorBuilder bindIdentifier(Identifier<U> identifier, Identifier<? extends U> implementor) {
		bindImplementor0(identifier, implementor);
		return this;
	}

	/**
	 * Binds an identifier to an unqualified type. The types must be compatible.
	 *
	 * @param <U>        the type to bind
	 * @param identifier the identifier of the type
	 * @param implementor the unqualified implementation class
	 * @return this builder
	 * @throws MisconfiguredBindingsException if the identifier is known to already be bound
	 * @throws IllegalArgumentException if the implementor is the same as the identifier
	 */
	public <U> InjectorBuilder bindIdentifier(Identifier<U> identifier, Class<? extends U> implementor) {
		bindImplementor0(identifier, Identifier.ofType(implementor));
		return this;
	}

	/**
	 * Binds an unqualified type to another. The types must be compatible.
	 *
	 * @param <U>        the type to bind
	 * @param clazz the class of the type
	 * @param implementor the unqualified implementation class
	 * @return this builder
	 * @throws MisconfiguredBindingsException if the identifier is known to already be bound
	 * @throws IllegalArgumentException if the implementor is the same as the identifier
	 */
	public <U> InjectorBuilder bindIdentifier(Class<U> clazz, Class<? extends U> implementor) {
		bindImplementor0(Identifier.ofType(clazz), Identifier.ofType(implementor));
		return this;
	}

	/**
	 * Binds an unqualified type to an instance
	 * 
	 * @param <U>      the type to bind
	 * @param clazz    the class of the type
	 * @param instance the instance
	 * @return this builder
	 * @throws MisconfiguredBindingsException if the unqualified type is known to
	 *                                        already be bound
	 */
	public <U> InjectorBuilder bindInstance(Class<U> clazz, U instance) {
		bindInstance0(Identifier.ofType(clazz), instance);
		return this;
	}

	/**
	 * Binds an identifier to an instance
	 * 
	 * @param <U>        the type to bind
	 * @param identifier the identifier of the type
	 * @param instance   the instance
	 * @return this builder
	 * @throws MisconfiguredBindingsException if the identifier is known to already
	 *                                        be bound
	 */
	public <U> InjectorBuilder bindInstance(Identifier<U> identifier, U instance) {
		bindInstance0(identifier, instance);
		return this;
	}

	/**
	 * Builds into an injector. May be used repeatedly without side effects
	 * 
	 * @return the injector
	 * @throws InjectorException if the modules are misconfigured or misannotated
	 */
	public Injector build() {
		ProviderMap providerMap = (multiBindings) ? new MultiBindingProviderMap() : new SimpleProviderMap();
		SpecSupport specification = this.specification.toInternal();
		return new Injector(
				new InjectorImpl(
						new InjectionSettings(specification, privateInjection, staticInjection, optionalBindings),
						new InjectorConfiguration(specification, bindModules, providerMap).configure(
								boundImplementors, boundInstances
						)
				));
	}

}
