/*
 * SolidInjector
 * Copyright © 2022 Anand Beh
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

package space.arim.injector.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Set;

import space.arim.injector.Identifier;
import space.arim.injector.error.MisconfiguredBindingsException;
import space.arim.injector.internal.provider.ContextualProvider;
import space.arim.injector.internal.provider.ProviderMap;
import space.arim.injector.internal.reflect.ConstructorAsProvider;
import space.arim.injector.internal.reflect.ConstructorScan;

public final class InjectorImpl implements DependencyRepository {

	private final InjectionSettings settings;
	private final ProviderMap providerMap;

	public InjectorImpl(InjectionSettings settings, ProviderMap providerMap) {
		this.settings = settings;
		this.providerMap = providerMap;
	}

	<U> ContextualProvider<U> lookupProvider(Identifier<U> ident) {
		return providerMap.requestSingleProvider(ident, (identifier) -> {
			if (identifier.isQualified()) {
				throw new MisconfiguredBindingsException("No binding found for qualified identifier " + identifier);
			}
			Class<U> type = identifier.getType();
			if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
				throw new MisconfiguredBindingsException(
						"No binding found for abstract identifier " + identifier);
			}
			return createConcreteProvider(type);
		});
	}

	private <U> ContextualProvider<U> createConcreteProvider(Class<U> type) {
		Constructor<U> constructor = new ConstructorScan<>(settings, type).findInjectableConstructor();
		constructor.setAccessible(true); // Check and/or set visibility
		return new ConstructorAsProvider<>(settings, constructor).createProvider();
	}

	@Override
	public DependencyRepository getRoot() {
		return this;
	}

	@Override
	public <U> ContextualProvider<U> requestProvider(Identifier<U> identifier) {
		return lookupProvider(identifier);
	}

	@Override
	public <U> U requestInstance(Identifier<U> identifier) {
		return new InjectionRequest(this).requestInstance(identifier);
	}

	@Override
	public <U> ContextualProvider<Set<U>> requestMultipleProviders(Identifier<U> identifier) {
		return providerMap.requestMultipleProviders(identifier);
	}

	@Override
	public <U> Set<U> requestMultipleInstances(Identifier<U> identifier) {
		return new InjectionRequest(this).requestMultipleInstances(identifier);
	}

}
