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

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import space.arim.injector.error.InjectorException;
import space.arim.injector.internal.InjectionSettings;
import space.arim.injector.internal.InjectorConfiguration;
import space.arim.injector.internal.InjectorImpl;
import space.arim.injector.internal.spec.SpecDetector;
import space.arim.injector.internal.spec.SpecSupport;

/**
 * Entry point for creating instances of an object through dependency injection.
 * <br>
 * <br>
 * The convenience methods {@link #newInjector(Object...)} and
 * {@link #newInjector(Collection)} provide injector instances with the default
 * settings, which are suitable for most purposes. <br>
 * <br>
 * For greater configuration, {@link InjectorBuilder} may be used.
 * 
 * @author A248
 *
 */
public final class Injector {

	private final InjectorImpl impl;

	Injector(InjectorImpl impl) {
		this.impl = impl;
	}

	/**
	 * Requests an instance of an unqualified type
	 * 
	 * @param <U>   the type of the instance to retrieve
	 * @param clazz the class of the type
	 * @return the instance
	 * @throws InjectorException if resolving the type or a dependency somehow failed
	 */
	public <U> U request(Class<U> clazz) {
		return impl.requestInstance(Identifier.ofType(clazz));
	}

	/**
	 * Requests an instance according to an identifier
	 * 
	 * @param <U>       the type of the instance to retrieve
	 * @param identifier the identifier
	 * @return the instance
	 * @throws InjectorException if resolving the type or a dependency somehow failed
	 */
	public <U> U request(Identifier<U> identifier) {
		return impl.requestInstance(identifier);
	}

	/**
	 * Creates an injector from the given binding modules
	 * 
	 * @param bindModules the object modules to configure bindings
	 * @return the injector
	 * @throws InjectorException if the modules are misconfigured or misannotated
	 */
	public static Injector newInjector(Object... bindModules) {
		return newInjector(Arrays.asList(bindModules));
	}

	/**
	 * Creates an injector from the given binding modules
	 * 
	 * @param bindModules the object modules to configure bindings
	 * @return the injector
	 * @throws InjectorException if the modules are misconfigured or misannotated
	 */
	public static Injector newInjector(Collection<Object> bindModules) {
		SpecSupport spec = SpecDetector.detectedSpec();
		return new Injector(
				new InjectorImpl(
						new InjectionSettings(spec),
						new ConcurrentHashMap<>(
								new InjectorConfiguration(spec, bindModules).configure())));
	}

}
