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

package space.arim.injector.internal.provider;

import space.arim.injector.Identifier;
import space.arim.injector.error.MultiBindingRelatedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class SimpleProviderMap implements ProviderMap {

	private final Map<Identifier<?>, ContextualProvider<?>> providers;

	private SimpleProviderMap(Map<Identifier<?>, ContextualProvider<?>> providers) {
		this.providers = providers;
	}

	public SimpleProviderMap() {
		this(new HashMap<>());
	}

	@Override
	public String installProvider(Identifier<?> identifier, ContextualProvider<?> provider) {
		ContextualProvider<?> previous = providers.putIfAbsent(identifier, provider);
		if (previous != null) {
			return "Duplicate binding exists for identifier " + identifier;
		}
		return null; // Success
	}

	@Override
	public <U> ContextualProvider<U> requestSingleProvider(Identifier<U> identifier,
														   Function<Identifier<U>, ContextualProvider<U>> creator) {
		@SuppressWarnings("unchecked")
		ContextualProvider<U> existingProvider = (ContextualProvider<U>) providers.get(identifier);
		if (existingProvider == null) {
			// No existing provider, create one if possible
			ContextualProvider<U> newProvider = creator.apply(identifier);
			// Optimistic update
			@SuppressWarnings("unchecked")
			ContextualProvider<U> previousProvider = (ContextualProvider<U>) providers.putIfAbsent(identifier, newProvider);
			return (previousProvider != null) ? previousProvider : newProvider;
		}
		return existingProvider;
	}

	@Override
	public <U> ContextualProvider<Set<U>> requestMultipleProviders(Identifier<U> identifier) {
		throw new MultiBindingRelatedException(
				"The multi-binding feature must be explicitly enabled with injectorBuilder.multiBindings(true)");
	}

	@Override
	public ProviderMap makeConcurrent() {
		return new SimpleProviderMap(new ConcurrentHashMap<>(providers));
	}

	@Override
	public boolean permitsMultiBindings() {
		return false;
	}
}
