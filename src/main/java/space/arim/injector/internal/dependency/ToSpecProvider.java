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

import space.arim.injector.internal.DependencyRepository;
import space.arim.injector.internal.provider.ContextlessProvider;
import space.arim.injector.internal.provider.ContextualProvider;
import space.arim.injector.internal.spec.SpecSupport;

import java.util.Objects;

public final class ToSpecProvider<P> {

	private transient final SpecSupport spec;
	private final Class<P> providerType;

	public ToSpecProvider(SpecSupport spec, Class<P> providerType) {
		this.spec = Objects.requireNonNull(spec, "spec");
		this.providerType = Objects.requireNonNull(providerType, "providerType");
	}

	public Class<P> providerType() {
		return providerType;
	}

	public P externalizeProvider(ContextualProvider<?> contextualProvider, DependencyRepository repository) {
		ContextlessProvider<?> contextlessProvider = contextualProvider.attachTo(repository.getRoot());
		return spec.externalize(contextlessProvider, providerType);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ToSpecProvider<?> that = (ToSpecProvider<?>) o;
		return providerType.equals(that.providerType);
	}

	@Override
	public int hashCode() {
		return providerType.hashCode();
	}

	@Override
	public String toString() {
		return "ToSpecProvider{providerType=" + providerType + '}';
	}
}
