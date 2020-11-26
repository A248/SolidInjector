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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import space.arim.injector.error.InjectionInvocationException;
import space.arim.injector.error.InjectorException;
import space.arim.injector.internal.DependencyRepository;
import space.arim.injector.internal.ExceptionContext;
import space.arim.injector.internal.dependency.InstantiableDependencyBunch;

class MethodInjection implements PostConstructorInjection {

	private final Method method;
	private final InstantiableDependencyBunch dependencies;

	MethodInjection(Method method, InstantiableDependencyBunch dependencies) {
		this.method = method;
		this.dependencies = dependencies;
	}

	@Override
	public void injectInto(Object instance, DependencyRepository repository) {
		Object[] arguments;
		try {
			arguments = dependencies.instantiateDependencies(repository);
		} catch (InjectorException ex) {
			throw new ExceptionContext().rethrow(ex, "Injecting method " + QualifiedNames.forMethod(method));
		}
		try {
			method.invoke(instance, arguments);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new InjectionInvocationException(
					"Unable to invoke " + QualifiedNames.forMethod(method) + " on " + instance, ex);
		}
	}

	@Override
	public String toString() {
		return "MethodInjection [method=" + method + ", dependencies=" + dependencies + "]";
	}

}
