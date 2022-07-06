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

import space.arim.injector.internal.DependencyRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class MultiContextualProvider<T> implements ContextualProvider<Set<T>> {

	private final ContextualProvider<T>[] providers;

	public MultiContextualProvider(ContextualProvider<T>[] providers) {
		this.providers = providers;
	}

	@Override
	public Set<T> provideUsing(DependencyRepository repository) {
		Set<T> values = new HashSet<>(providers.length, 1f);
		for (ContextualProvider<T> provider : providers) {
			values.add(provider.provideUsing(repository));
		}
		return Collections.unmodifiableSet(values);
	}
}
