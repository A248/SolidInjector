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
package space.arim.injector.internal.provider;

import space.arim.injector.internal.DependencyRepository;

public class FixedContextualProvider<T> implements ContextualProvider<T> {

	private final T instance;

	public FixedContextualProvider(T instance) {
		this.instance = instance;
	}

	@Override
	public T provideUsing(DependencyRepository repository) {
		return instance;
	}

	@Override
	public ContextlessProvider<T> attachTo(DependencyRepository repository) {
		return new FixedContextlessProvider<>(instance);
	}

	@Override
	public String toString() {
		return "FixedContextualProvider [instance=" + instance + "]";
	}

}
