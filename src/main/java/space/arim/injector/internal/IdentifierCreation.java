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

import space.arim.injector.Identifier;
import space.arim.injector.internal.reflect.GenericType;
import space.arim.injector.internal.reflect.qualifier.QualifiersIn;
import space.arim.injector.internal.spec.SpecSupport;

public class IdentifierCreation<U> {

	private final Class<U> type;
	private final QualifiersIn qualifiersIn;

	public IdentifierCreation(Class<U> type, QualifiersIn qualifiersIn) {
		this.type = type;
		this.qualifiersIn = qualifiersIn;
	}

	@SuppressWarnings("unchecked")
	public IdentifierCreation(GenericType type, QualifiersIn qualifiersIn) {
		this((Class<U>) type.getRawType(), qualifiersIn);
	}

	public Identifier<U> createIdentifier(SpecSupport spec) {
		Annotation qualifier = qualifiersIn.getQualifier(spec);
		if (qualifier == null) {
			return Identifier.ofType(type);
		}
		String name = spec.getNamedQualifier(qualifier);
		if (name != null) {
			return Identifier.ofTypeAndNamed(type, name);
		}
		return Identifier.ofTypeAndQualifier(type, qualifier.annotationType());
	}

}
