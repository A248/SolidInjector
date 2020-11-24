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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import space.arim.injector.error.InjectorInternalFailureException;
import space.arim.injector.internal.IdentifierCreation;
import space.arim.injector.internal.IdentifierInternal;
import space.arim.injector.internal.spec.SpecSupport;

public class AnnotatedTypeAsDependency {

	private final SpecSupport spec;
	private final Class<?> type;
	private final Type genericType;
	private final Annotation[] annotations;

	public AnnotatedTypeAsDependency(SpecSupport spec, Class<?> type, Type genericType, Annotation[] annotations) {
		this.spec = spec;
		this.type = type;
		this.genericType = genericType;
		this.annotations = annotations;
	}

	public InstantiableDependency createDependency() {
		if (spec.isAnyProvider(type)) {
			return fromProviderType();
		}
		IdentifierCreation<?> identifierCreation = new IdentifierCreation<>(spec, type, annotations);
		IdentifierInternal<?> identifier = identifierCreation.createIdentifier();
		return new InstantiableInstanceDependency<>(identifier);
	}

	private InjectorInternalFailureException failedGenerics(String reason) {
		return new InjectorInternalFailureException("Unable to determine generic type of " + genericType + ". Reason: " + reason);
	}

	private InstantiableDependency fromProviderType() {
		if (!(genericType instanceof ParameterizedType)) {
			throw failedGenerics("type is not a ParameterizedType");
		}
		Type actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
		if (!(actualTypeArgument instanceof Class)) {
			throw failedGenerics("actual type argument not a Class");
		}
		IdentifierCreation<?> identifierCreation = new IdentifierCreation<>(spec, (Class<?>) actualTypeArgument, annotations);
		IdentifierInternal<?> identifier = identifierCreation.createIdentifier();
		Class<?> providerType = this.type;
		return new InstantiableProviderDependency<>(spec, providerType, identifier);
	}

}
