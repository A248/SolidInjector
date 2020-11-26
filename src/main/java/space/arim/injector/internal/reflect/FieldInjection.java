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

import java.lang.reflect.Field;

import space.arim.injector.error.InjectionInvocationException;
import space.arim.injector.error.InjectorException;
import space.arim.injector.internal.DependencyRepository;
import space.arim.injector.internal.ExceptionContext;
import space.arim.injector.internal.dependency.InstantiableDependency;

class FieldInjection implements PostConstructorInjection {

	private final Field field;
	private final InstantiableDependency dependency;

	FieldInjection(Field field, InstantiableDependency dependency) {
		this.field = field;
		this.dependency = dependency;
	}

	@Override
	public void injectInto(Object instance, DependencyRepository repository) {
		Object value;
		try {
			value = dependency.instantiate(repository);
		} catch (InjectorException ex) {
			throw new ExceptionContext().rethrow(ex, "Injecting field " + QualifiedNames.forField(field));
		}
		try {
			field.set(instance, value);
		} catch (IllegalArgumentException | IllegalAccessException ex) {
			throw new InjectionInvocationException(
					"Unable to set field " + QualifiedNames.forField(field) + " on " + instance, ex);
		}
	}

	@Override
	public String toString() {
		return "FieldInjection [field=" + field + ", dependency=" + dependency + "]";
	}

}
