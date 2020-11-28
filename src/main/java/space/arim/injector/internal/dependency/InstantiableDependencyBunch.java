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

import java.util.Arrays;

import space.arim.injector.internal.DependencyRepository;

public class InstantiableDependencyBunch {

	private final InstantiableDependency[] instantiableDependencies;

	public InstantiableDependencyBunch(InstantiableDependency[] instantiableDependencies) {
		this.instantiableDependencies = instantiableDependencies;
	}

	public Object[] instantiateDependencies(DependencyRepository repository) {
		Object[] dependencies = new Object[instantiableDependencies.length];
		for (int n = 0; n < instantiableDependencies.length; n++) {
			dependencies[n] = instantiableDependencies[n].instantiate(repository);
		}
		return dependencies;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(instantiableDependencies);
		return result;
	}

	@Override
	public boolean equals(Object object) {
		return this == object ||
				object instanceof InstantiableDependencyBunch &&
				Arrays.equals(instantiableDependencies, ((InstantiableDependencyBunch) object).instantiableDependencies);
	}

	@Override
	public String toString() {
		return "dependencies " + Arrays.toString(instantiableDependencies);
	}

}
