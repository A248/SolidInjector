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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class MultiBindingProviderMap implements ProviderMap {

	private final Map<Identifier<?>, Object> providers;

	private MultiBindingProviderMap(Map<Identifier<?>, Object> providers) {
		this.providers = providers;
	}

	public MultiBindingProviderMap() {
		this(new HashMap<>());
	}

	private static String doesNotPermitMultiBinding(String what) {
		return what + " does not permit multi-binding. Use @MultiBinding on bind methods to allow multiplicity.";
	}

	@Override
	public String installProvider(Identifier<?> identifier, ContextualProvider<?> newProvider) {

		Object existingProviderOrMultiple = providers.get(identifier);
		if (existingProviderOrMultiple == null) {
			// Install single provider; retry on concurrent update
			Object previous = providers.putIfAbsent(identifier, newProvider);
			if (previous != null) return installProvider(identifier, newProvider);
		} else {
			// Check that multibinding is permitted
			if (!newProvider.permitsMultiBinding()) {
				return doesNotPermitMultiBinding("The binding to be installed at " + identifier);
			}
			if (existingProviderOrMultiple instanceof ContextualProvider[]) {
				// Found multiple existing providers: append the new one
				ContextualProvider<?>[] existingProviderArray = (ContextualProvider<?>[]) existingProviderOrMultiple;
				ContextualProvider<?>[] newProviderArray = Arrays.copyOf(existingProviderArray, existingProviderArray.length + 1);
				newProviderArray[newProviderArray.length - 1] = newProvider;
				boolean replaced = providers.replace(identifier, existingProviderArray, newProviderArray);
				if (!replaced) return installProvider(identifier, newProvider);
			} else {
				// Found one existing provider: make sure it permits multi-binding
				ContextualProvider<?> existingProvider = (ContextualProvider<?>) existingProviderOrMultiple;
				if (!existingProvider.permitsMultiBinding()) {
					return doesNotPermitMultiBinding("The existing binding at " + identifier);
				}
				// Append new provider; retry on concurrent update
				ContextualProvider<?>[] providerArray = new ContextualProvider[] {existingProvider, newProvider};
				boolean replaced = providers.replace(identifier, existingProvider, providerArray);
				if (!replaced) return installProvider(identifier, newProvider);
			}
		}
		// Success
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U> ContextualProvider<U> requestSingleProvider(Identifier<U> identifier, Function<Identifier<U>, ContextualProvider<U>> creator) {
		Object existingProviderOrMultiple = providers.get(identifier);
		if (existingProviderOrMultiple == null) {
			// No existing provider, create one if possible
			ContextualProvider<U> newProvider = creator.apply(identifier);
			// Optimistic update
			Object previous = providers.putIfAbsent(identifier, newProvider);
			if (previous != null) {
				// Retry due to concurrent update
				return requestSingleProvider(identifier, creator);
			}
			return newProvider;
		}
		if (existingProviderOrMultiple instanceof ContextualProvider[]) {
			throw new MultiBindingRelatedException("Multiple bindings are registered " +
					"for identifier " + identifier + ", but only one instance was requested.");
		}
		return (ContextualProvider<U>) existingProviderOrMultiple;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <U> ContextualProvider<Set<U>> requestMultipleProviders(Identifier<U> identifier) {
		Object existingProviderOrMultiple = providers.get(identifier);
		if (existingProviderOrMultiple == null) {
			return (repository) -> Collections.emptySet();
		}
		if (existingProviderOrMultiple instanceof ContextualProvider[]) {
			ContextualProvider<U>[] castedProviders = (ContextualProvider<U>[]) existingProviderOrMultiple;
			return new MultiContextualProvider<>(castedProviders);
		}
		ContextualProvider<U> provider = (ContextualProvider<U>) existingProviderOrMultiple;
		if (!provider.permitsMultiBinding()) {
			throw new MultiBindingRelatedException("Multiple bindings were requested " +
					"for identifier " + identifier + ", but " + doesNotPermitMultiBinding("the registered binding"));
		}
		ContextualProvider<U>[] providerAsArray = (ContextualProvider<U>[]) new ContextualProvider<?>[] {provider};
		return new MultiContextualProvider<>(providerAsArray);
	}

	@Override
	public <U> Optional<ContextualProvider<U>> requestProviderOptionally(Identifier<U> identifier) {
		Object existingProviderOrMultiple = providers.get(identifier);
		if (existingProviderOrMultiple == null) {
			return Optional.empty();
		}
		if (existingProviderOrMultiple instanceof ContextualProvider[]) {
			throw new MultiBindingRelatedException("Multiple bindings are registered " +
					"for identifier " + identifier + ", but only one instance was requested.");
		}
		@SuppressWarnings("unchecked")
		ContextualProvider<U> provider = (ContextualProvider<U>) existingProviderOrMultiple;
		return Optional.of(provider);
	}

	@Override
	public ProviderMap makeConcurrent() {
		return new MultiBindingProviderMap(new ConcurrentHashMap<>(providers));
	}

	@Override
	public boolean permitsMultiBindings() {
		return true;
	}
}
