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

package space.arim.injector.internal.dependency;

import space.arim.injector.Identifier;
import space.arim.injector.internal.DependencyRepository;
import space.arim.injector.internal.provider.ContextualProvider;

import java.util.Objects;

public final class MultiProviderDependency<P> implements InstantiableDependency {

	private final ToSpecProvider<P> toSpecProvider;
	private final Identifier<?> identifier;

	public MultiProviderDependency(ToSpecProvider<P> toSpecProvider, Identifier<?> identifier) {
		this.toSpecProvider = Objects.requireNonNull(toSpecProvider, "toSpecProvider");
		this.identifier = Objects.requireNonNull(identifier, "identifier");
	}

	@Override
	public P instantiate(DependencyRepository repository) {
		ContextualProvider<?> contextualProvider = repository.requestMultipleProviders(identifier);
		return toSpecProvider.externalizeProvider(contextualProvider, repository);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MultiProviderDependency<?> that = (MultiProviderDependency<?>) o;
		return toSpecProvider.equals(that.toSpecProvider) && identifier.equals(that.identifier);
	}

	@Override
	public int hashCode() {
		int result = toSpecProvider.hashCode();
		result = 31 * result + identifier.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "multi-provider dependency [providerType=" + toSpecProvider.providerType() + ", identifier=" + identifier + "]";
	}
}
