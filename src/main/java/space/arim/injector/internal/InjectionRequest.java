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

import java.util.HashSet;
import java.util.Set;

import space.arim.injector.Identifier;
import space.arim.injector.error.CircularDependencyException;
import space.arim.injector.internal.provider.ContextualProvider;

class InjectionRequest implements DependencyRepository {

	private final InjectorImpl injector;
	private final Set<Identifier<?>> identifiersInProgress = new HashSet<>();

	InjectionRequest(InjectorImpl injector) {
		this.injector = injector;
	}

	@Override
	public DependencyRepository getRoot() {
		return injector;
	}

	@Override
	public <U> ContextualProvider<U> requestProvider(Identifier<U> identifier) {
		return injector.lookupProvider(identifier);
	}

	@Override
	public <U> U requestInstance(Identifier<U> identifier) {
		boolean added = identifiersInProgress.add(identifier);
		if (!added) {
			throw new CircularDependencyException("Circular dependency detected while serving instance for " + identifier);
		}
		U instance = requestProvider(identifier).provideUsing(this);
		identifiersInProgress.remove(identifier);
		return instance;
	}

}
