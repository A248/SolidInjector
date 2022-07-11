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

public final class ProviderDependency<P> implements InstantiableDependency {

	private final ToSpecProvider<P> toSpecProvider;
	private final Identifier<?> identifier;

	public ProviderDependency(ToSpecProvider<P> toSpecProvider, Identifier<?> identifier) {
		this.toSpecProvider = Objects.requireNonNull(toSpecProvider, "toSpecProvider");
		this.identifier = Objects.requireNonNull(identifier, "identifier");
	}

	@Override
	public P instantiate(DependencyRepository repository) {
		ContextualProvider<?> contextualProvider = repository.requestProvider(identifier);
		return toSpecProvider.externalizeProvider(contextualProvider, repository);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + identifier.hashCode();
		result = prime * result + toSpecProvider.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof ProviderDependency)) {
			return false;
		}
		ProviderDependency<?> other = (ProviderDependency<?>) object;
		return identifier.equals(other.identifier) && toSpecProvider.equals(other.toSpecProvider);
	}

	@Override
	public String toString() {
		return "provider dependency [providerType=" + toSpecProvider.providerType() + ", identifier=" + identifier + "]";
	}

}
