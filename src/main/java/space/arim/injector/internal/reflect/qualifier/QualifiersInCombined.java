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

package space.arim.injector.internal.reflect.qualifier;

import java.lang.annotation.Annotation;
import java.util.Objects;

import space.arim.injector.error.MisannotatedInjecteeException;
import space.arim.injector.internal.spec.SpecSupport;

public final class QualifiersInCombined implements QualifiersIn {

	private final QualifiersIn[] combined;

	public QualifiersInCombined(QualifiersIn...combined) {
		this.combined = Objects.requireNonNull(combined);
	}

	@Override
	public Annotation getQualifier(SpecSupport spec) {
		Annotation currentQualifier = null;
		for (QualifiersIn qualifiersIn : combined) {
			Annotation thisQualifier = qualifiersIn.getQualifier(spec);
			if (thisQualifier == null) {
				continue;
			}
			if (currentQualifier != null) {
				throw new MisannotatedInjecteeException(
						"Duplicate @Qualifier " + thisQualifier + ", " + currentQualifier + " is already present");
			}
			currentQualifier = thisQualifier;
		}
		return currentQualifier;
	}

}
