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

package space.arim.injector.internal.reflect;

import space.arim.injector.error.InjectorException;
import space.arim.injector.internal.ExceptionContext;
import space.arim.injector.internal.dependency.AnnotatedTypeAsDependency;
import space.arim.injector.internal.dependency.InstantiableDependency;
import space.arim.injector.internal.dependency.InstantiableDependencyBunch;
import space.arim.injector.internal.spec.SpecSupport;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

public class ExecutableDependencies {

	private final SpecSupport spec;
	private final Executable executable;

	public ExecutableDependencies(SpecSupport spec, Executable executable) {
		this.spec = spec;
		this.executable = executable;
	}

	public InstantiableDependencyBunch collectDependencies() {
		Parameter[] parameters = executable.getParameters();
		InstantiableDependency[] instantiableDependencies = new InstantiableDependency[parameters.length];

		for (int n = 0; n < parameters.length; n++) {
			try {
				instantiableDependencies[n] = intoDependency(parameters[n]);
			} catch (InjectorException ex) {
				throw new ExceptionContext().rethrow(ex,
						"On executable " + QualifiedNames.forExecutable(executable) + ", parameter number " + n);
			}
		}
		return new InstantiableDependencyBunch(instantiableDependencies);
	}

	private InstantiableDependency intoDependency(Parameter parameter) {
		return new AnnotatedTypeAsDependency(
				spec, new GenericType(parameter.getAnnotatedType()), parameter.getAnnotations()
		).createDependency();
	}

}
