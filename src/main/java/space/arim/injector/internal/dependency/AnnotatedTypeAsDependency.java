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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import space.arim.injector.error.InjectorInternalFailureException;
import space.arim.injector.internal.IdentifierCreation;
import space.arim.injector.internal.reflect.qualifier.QualifiersInAnnotations;
import space.arim.injector.internal.reflect.qualifier.QualifiersInCombined;
import space.arim.injector.internal.spec.SpecSupport;

public class AnnotatedTypeAsDependency {

	private final SpecSupport spec;
	private final Class<?> type;
	/**
	 * All annotations. <br>
	 * Not equal to annotatedType.getAnnotations(). Annotation[] covers all annotations,
	 * whereas annotatedType.getAnnotations() includes only TYPE_USE annotations
	 */
	private final Annotation[] annotations;
	private final AnnotatedType annotatedType;

	public AnnotatedTypeAsDependency(SpecSupport spec, Class<?> type, Annotation[] annotations,
			AnnotatedType annotatedType) {
		this.spec = spec;
		this.type = type;
		this.annotations = annotations;
		this.annotatedType = annotatedType;
	}

	public AnnotatedTypeAsDependency(SpecSupport spec, Parameter parameter) {
		this(spec, parameter.getType(), parameter.getAnnotations(), parameter.getAnnotatedType());
	}

	public InstantiableDependency createDependency() {
		if (spec.isAnyProvider(type)) {
			return fromProviderType();
		}
		return new InstantiableInstanceDependency<>(
				new IdentifierCreation<>(type,
						new QualifiersInAnnotations(annotations)
				).createIdentifier(spec));
	}

	private InjectorInternalFailureException failedAnnotatedGenerics(String reason) {
		return new InjectorInternalFailureException(
				"Unable to determine annotated generic type of Provider. Reason: " + reason);
	}

	private Annotation[] getProviderTypeUseAnnotations() {
		if (!(annotatedType instanceof AnnotatedParameterizedType)) {
			throw failedAnnotatedGenerics("Annotated type is not AnnotatedParameterizedType");
		}
		AnnotatedType[] annotatedTypeArguments = ((AnnotatedParameterizedType) annotatedType)
				.getAnnotatedActualTypeArguments();
		if (annotatedTypeArguments.length != 1) {
			throw failedAnnotatedGenerics("AnnotatedParameterizedType annotated type arguments are not of length 1");
		}
		return annotatedTypeArguments[0].getAnnotations();
	}

	private Class<?> getProviderTypeArgument() {
		Type genericType = annotatedType.getType();
		if (!(genericType instanceof ParameterizedType)) {
			throw failedAnnotatedGenerics("Generic type is not a ParameterizedType");
		}
		Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
		if (actualTypeArguments.length != 1) {
			throw failedAnnotatedGenerics("ParameterizedType type arguments are not of length 1");
		}
		Type actualTypeArgument = actualTypeArguments[0];
		if (!(actualTypeArgument instanceof Class)) {
			throw failedAnnotatedGenerics("ParameterizedType's actual type argument not a Class");
		}
		return (Class<?>) actualTypeArgument;
	}

	private InstantiableDependency fromProviderType() {
		Annotation[] typeUseAnnotations = getProviderTypeUseAnnotations();
		Class<?> providerTypeArgument = getProviderTypeArgument();
		Class<?> providerType = this.type;
		return new InstantiableProviderDependency<>(spec, providerType,
				new IdentifierCreation<>(providerTypeArgument,
						new QualifiersInCombined(
								new QualifiersInAnnotations(annotations),
								new QualifiersInAnnotations(typeUseAnnotations))
						).createIdentifier(spec));
	}

}
