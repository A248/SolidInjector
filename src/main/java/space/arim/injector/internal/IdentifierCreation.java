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
package space.arim.injector.internal;

import java.lang.annotation.Annotation;

import space.arim.injector.error.MisannotatedInjecteeException;
import space.arim.injector.internal.spec.SpecSupport;

public class IdentifierCreation<U> {

	private final SpecSupport spec;
	private final Class<U> type;
	private final Annotation[] annotations;

	public IdentifierCreation(SpecSupport spec, Class<U> type, Annotation[] annotations) {
		this.spec = spec;
		this.type = type;
		this.annotations = annotations;
	}

	public IdentifierInternal<U> createIdentifier() {
		Annotation qualifier = getQualifier();
		if (qualifier == null) {
			return IdentifierInternal.ofType(type);
		}
		String name = spec.getNamedQualifier(qualifier);
		if (name != null) {
			return IdentifierInternal.ofTypeAndNamed(type, name);
		}
		return IdentifierInternal.ofTypeAndQualifier(type, qualifier);
	}

	private Annotation getQualifier() {
		Annotation qualifier = null;
		for (Annotation annotation : annotations) {
			if (!spec.isQualifier(annotation.annotationType())) {
				continue;
			}
			if (qualifier != null) {
				throw new MisannotatedInjecteeException(
						"Duplicate @Qualifier " + annotation + ", " + qualifier + " is already present");
			}
			qualifier = annotation;
		}
		return qualifier;
	}

}
