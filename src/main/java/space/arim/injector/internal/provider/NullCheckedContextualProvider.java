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

import space.arim.injector.error.InjectionInvocationException;
import space.arim.injector.internal.DependencyRepository;

public class NullCheckedContextualProvider<T> implements ContextualProvider<T> {

	private final ContextualProvider<T> delegate;

	public NullCheckedContextualProvider(ContextualProvider<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public T provideUsing(DependencyRepository repository) {
		T instance = delegate.provideUsing(repository);
		if (instance == null) {
			throw new InjectionInvocationException("provider " + delegate + " returned null");
		}
		return instance;
	}

	@Override
	public boolean permitsMultiBinding() {
		return delegate.permitsMultiBinding();
	}

	@Override
	public String toString() {
		return "NullCheckedContextualProvider [delegate=" + delegate + "]";
	}

}
