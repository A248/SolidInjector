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
package space.arim.injector.internal.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

/**
 * Used to eliminate parent methods which have been overridden
 *
 */
class DistinctMethod {

	private final String name;
	private final Class<?>[] parameters;
	/**
	 * If package private, defines the enclosing package
	 */
	private final String enclosingPackage;

	private DistinctMethod(String name, Class<?>[] parameters, String enclosingPackage) {
		this.name = name;
		this.parameters = parameters;
		this.enclosingPackage = enclosingPackage;
	}

	static DistinctMethod of(Method method) {
		int modifiers = method.getModifiers();
		if (Modifier.isStatic(modifiers) || Modifier.isPrivate(modifiers)) {
			// Static and private methods cannot be overridden
			return null;
		}
		String enclosingPackage;
		if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) {
			enclosingPackage = null;
		} else {
			// Must be package private, determine package
			String className = method.getDeclaringClass().getName();
			int indexOfPeriod = className.lastIndexOf('.');
			String packageName = (indexOfPeriod == -1) ? "" : className.substring(0, indexOfPeriod);
			enclosingPackage = packageName;
		}
		return new DistinctMethod(method.getName(), method.getParameterTypes(), enclosingPackage);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + name.hashCode();
		result = prime * result + Arrays.hashCode(parameters);
		result = prime * result + Objects.hashCode(enclosingPackage);
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof DistinctMethod)) {
			return false;
		}
		DistinctMethod other = (DistinctMethod) object;
		return name.equals(other.name) && Arrays.equals(parameters, other.parameters)
				&& Objects.equals(enclosingPackage, other.enclosingPackage);
	}
}
