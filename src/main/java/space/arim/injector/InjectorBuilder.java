/* 
 * SolidInjector
 * Copyright © 2020 Anand Beh <https://www.arim.space>
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import space.arim.injector.error.InjectorException;
import space.arim.injector.error.MisconfiguredBindingsException;
import space.arim.injector.internal.IdentifierInternal;
import space.arim.injector.internal.InjectionSettings;
import space.arim.injector.internal.InjectorConfiguration;
import space.arim.injector.internal.InjectorImpl;
import space.arim.injector.internal.spec.SpecSupport;

/**
 * Builder of {@link Injector}s. Not thread safe.
 * 
 * @author A248
 *
 */
public final class InjectorBuilder {

	private SpecificationSupport specification = SpecificationSupport.AUTO_DETECT;
	private final Set<Object> bindModules = new HashSet<>();
	private final Map<IdentifierInternal<?>, Object> boundInstances = new HashMap<>();
	private boolean privateInjection;
	private boolean staticInjection;

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
	 * Adds the specified bind modules to this injector builder
	 * 
	 * @param bindModules the bind modules
	 * @return this builder
	 */
	public InjectorBuilder addBindModules(Object... bindModules) {
		for (Object bindModule : bindModules) {
			Objects.requireNonNull(bindModule, "bind module");
			this.bindModules.add(bindModule);
		}
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
		bindInstance0(IdentifierInternal.ofType(clazz), instance);
		return this;
	}

	/**
	 * Binds an identifier to an instance
	 * 
	 * @param <U>        the type to bind
	 * @param identifier the identifier of the type to bind
	 * @param instance   the instance
	 * @return this builder
	 * @throws MisconfiguredBindingsException if the identifier is known to already
	 *                                        be bound
	 */
	public <U> InjectorBuilder bindInstance(Identifier<U> identifier, U instance) {
		bindInstance0(identifier.toInternal(), instance);
		return this;
	}

	private <U> void bindInstance0(IdentifierInternal<U> identifier, U instance) {
		Object previous = boundInstances.put(identifier, instance);
		if (previous != null) {
			throw new MisconfiguredBindingsException("Binding already exists for identifier " + identifier
					+ " (previous binding is instance " + previous + ")");
		}
	}

	/**
	 * Builds into an injector. May be used repeatedly without side effects
	 * 
	 * @return the injector
	 * @throws InjectorException if the modules are misconfigured or misannotated
	 */
	public Injector build() {
		SpecSupport specification = this.specification.toInternal();
		return new Injector(
				new InjectorImpl(
						new InjectionSettings(specification, privateInjection, staticInjection),
						new ConcurrentHashMap<>(
								new InjectorConfiguration(specification, bindModules).configure(boundInstances))));
	}

}
