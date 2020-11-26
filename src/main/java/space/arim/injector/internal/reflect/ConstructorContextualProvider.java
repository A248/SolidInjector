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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import space.arim.injector.error.InjectionInvocationException;
import space.arim.injector.error.InjectorException;
import space.arim.injector.internal.DependencyRepository;
import space.arim.injector.internal.ExceptionContext;
import space.arim.injector.internal.dependency.InstantiableDependencyBunch;
import space.arim.injector.internal.provider.ContextualProvider;

class ConstructorContextualProvider<T> implements ContextualProvider<T> {

	private final Constructor<T> constructor;
	private final InstantiableDependencyBunch parameterDependencies;

	public ConstructorContextualProvider(Constructor<T> constructor, InstantiableDependencyBunch parameterDependencies) {
		this.constructor = constructor;
		this.parameterDependencies = parameterDependencies;
	}

	@Override
	public T provideUsing(DependencyRepository repository) {
		Object[] arguments;
		try {
			arguments = parameterDependencies.instantiateDependencies(repository);
		} catch (InjectorException ex) {
			throw new ExceptionContext().rethrow(ex,
					"Invoking constructor " + QualifiedNames.forConstructor(constructor));
		}
		try {
			return constructor.newInstance(arguments);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| InstantiationException ex) {
			throw new InjectionInvocationException(
					"Failed to invoke constructor " + QualifiedNames.forConstructor(constructor), ex);
		}
	}

	@Override
	public String toString() {
		return "ConstructorContextualProvider [constructor=" + constructor + ", parameterDependencies="
				+ parameterDependencies + "]";
	}

}
