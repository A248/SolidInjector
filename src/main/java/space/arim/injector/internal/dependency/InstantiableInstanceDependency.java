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
package space.arim.injector.internal.dependency;

import space.arim.injector.Identifier;
import space.arim.injector.internal.DependencyRepository;

class InstantiableInstanceDependency<T> implements InstantiableDependency {

	private final Identifier<T> identifier;

	public InstantiableInstanceDependency(Identifier<T> identifier) {
		this.identifier = identifier;
	}

	@Override
	public T instantiate(DependencyRepository repository) {
		return repository.requestInstance(identifier);
	}

	@Override
	public String toString() {
		return "instance dependency [identifier=" + identifier + "]";
	}

}
