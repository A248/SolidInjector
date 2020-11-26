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

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Type;

import space.arim.injector.error.InjectorException;
import space.arim.injector.internal.ExceptionContext;
import space.arim.injector.internal.dependency.AnnotatedTypeAsDependency;
import space.arim.injector.internal.dependency.InstantiableDependency;
import space.arim.injector.internal.dependency.InstantiableDependencyBunch;
import space.arim.injector.internal.spec.SpecSupport;

public class ExecutableDependencies {

	private final SpecSupport spec;
	private final Executable executable;

	public ExecutableDependencies(SpecSupport spec, Executable executable) {
		this.spec = spec;
		this.executable = executable;
	}

	public InstantiableDependencyBunch collectDependencies() {
		Class<?>[] parameterTypes = executable.getParameterTypes();
		Type[] genericParameterTypes = executable.getGenericParameterTypes();
		Annotation[][] parameterAnnotations = executable.getParameterAnnotations();
		InstantiableDependency[] instantiableDependencies = new InstantiableDependency[parameterTypes.length];

		for (int n = 0; n < parameterTypes.length; n++) {
			Class<?> type = parameterTypes[n];
			Type genericType = genericParameterTypes[n];
			Annotation[] annotations = parameterAnnotations[n];
			InstantiableDependency dependency;
			try {
				dependency = new AnnotatedTypeAsDependency(spec, type, genericType, annotations).createDependency();
			} catch (InjectorException ex) {
				throw new ExceptionContext().rethrow(ex, "On executable " + QualifiedNames.forExecutable(executable));
			}
			instantiableDependencies[n] = dependency;
		}
		return new InstantiableDependencyBunch(instantiableDependencies);
	}

}
