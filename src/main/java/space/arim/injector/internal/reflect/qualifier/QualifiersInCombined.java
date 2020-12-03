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
package space.arim.injector.internal.reflect.qualifier;

import java.lang.annotation.Annotation;

import space.arim.injector.error.MisannotatedInjecteeException;
import space.arim.injector.internal.spec.SpecSupport;

public class QualifiersInCombined implements QualifiersIn {

	private final QualifiersIn one;
	private final QualifiersIn two;

	public QualifiersInCombined(QualifiersIn one, QualifiersIn two) {
		this.one = one;
		this.two = two;
	}

	@Override
	public Annotation getQualifier(SpecSupport spec) {
		Annotation firstQualifier = one.getQualifier(spec);
		Annotation secondQualifier = two.getQualifier(spec);
		if (firstQualifier != null && secondQualifier != null) {
			throw new MisannotatedInjecteeException(
					"Duplicate @Qualifier " + secondQualifier + ", " + firstQualifier + " is already present");
		}
		if (firstQualifier != null) {
			return firstQualifier;
		}
		if (secondQualifier != null) {
			return secondQualifier;
		}
		return null;
	}

}