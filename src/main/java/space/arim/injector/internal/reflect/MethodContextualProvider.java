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
import space.arim.injector.internal.DependencyRepository;
import space.arim.injector.internal.dependency.InstantiableDependencyBunch;
import space.arim.injector.internal.provider.ContextualProvider;

public class MethodContextualProvider<T> implements ContextualProvider<T> {

	private final Object bindModule;
	private final Method method;
	private final InstantiableDependencyBunch parameterDependencies;

	public MethodContextualProvider(Object bindModule, Method method, InstantiableDependencyBunch parameterDependencies) {
		this.bindModule = bindModule;
		this.method = method;
		this.parameterDependencies = parameterDependencies;
	}

	@Override
	public T provideUsing(DependencyRepository repository) {
		Object[] arguments = parameterDependencies.instantiateDependencies(repository);
		try {
			@SuppressWarnings("unchecked")
			T instance = (T) method.invoke(bindModule, arguments);
			return instance;

		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new InjectionInvocationException("Failed to invoke method " + QualifiedNames.forMethod(method), ex);
		}
	}

	@Override
	public String toString() {
		return "MethodContextualProvider [bindModule=" + bindModule + ", method=" + method + ", parameterDependencies="
				+ parameterDependencies + "]";
	}

}
