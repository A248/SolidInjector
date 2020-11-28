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
package space.arim.injector.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentMap;

import space.arim.injector.Identifier;
import space.arim.injector.error.MisconfiguredBindingsException;
import space.arim.injector.internal.provider.ContextualProvider;
import space.arim.injector.internal.reflect.ConstructorAsProvider;
import space.arim.injector.internal.reflect.ConstructorScan;

public class InjectorImpl implements DependencyRepository {

	private final InjectionSettings settings;
	private final ConcurrentMap<Identifier<?>, ContextualProvider<?>> providers;

	public InjectorImpl(InjectionSettings settings,
			ConcurrentMap<Identifier<?>, ContextualProvider<?>> providers) {
		this.settings = settings;
		this.providers = providers;
	}

	<U> ContextualProvider<U> lookupProvider(Identifier<U> identifier) {
		@SuppressWarnings("unchecked")
		ContextualProvider<U> existingProvider = (ContextualProvider<U>) providers.get(identifier);
		if (existingProvider == null) {
			// No existing provider, create one if possible
			if (identifier.isQualified()) {
				throw new MisconfiguredBindingsException("No binding found for qualified identifier " + identifier);
			}
			Class<U> type = identifier.getType();
			if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
				throw new MisconfiguredBindingsException(
						"No binding found for abstract identifier " + identifier);
			}
			ContextualProvider<U> newProvider = createConcreteProvider(type);
			// Optimistic update
			@SuppressWarnings("unchecked")
			ContextualProvider<U> previousProvider = (ContextualProvider<U>) providers.putIfAbsent(identifier, newProvider);
			return (previousProvider != null) ? previousProvider : newProvider;
		}
		return existingProvider;
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

}
