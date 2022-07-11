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
import space.arim.injector.error.InjectorInternalFailureException;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class GenericType {

	private final AnnotatedType annotatedType;

	public GenericType(AnnotatedType annotatedType) {
		this.annotatedType = annotatedType;
	}

	public Class<?> getRawType() {
		Type type = annotatedType.getType();
		if (type instanceof Class) {
			return (Class<?>) type;
		}
		if (type instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) type).getRawType();
		}
		throw new InjectorException("Generic types are not supported, except for specific features such as injecting Provider or multi-binding and optional binding.");
	}

	private InjectorInternalFailureException failedAnnotatedGenerics(String reason) {
		return new InjectorInternalFailureException(
				"Unable to determine details of parameterized type. Reason: " + reason + '.');
	}

	public Annotation[] getAnnotations() {
		return annotatedType.getAnnotations();
	}

	public GenericType getTypeArgument() {
		if (!(annotatedType instanceof AnnotatedParameterizedType)) {
			throw failedAnnotatedGenerics("Annotated type is not AnnotatedParameterizedType but rather " + annotatedType.getClass() + " with Type " + annotatedType.getType().getClass());
		}
		AnnotatedType[] annotatedTypeArguments = ((AnnotatedParameterizedType) annotatedType)
				.getAnnotatedActualTypeArguments();
		if (annotatedTypeArguments.length != 1) {
			throw failedAnnotatedGenerics("AnnotatedParameterizedType annotated type arguments are not of length 1");
		}
		return new GenericType(annotatedTypeArguments[0]);
	}
}
