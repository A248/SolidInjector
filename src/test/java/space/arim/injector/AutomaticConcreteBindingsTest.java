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
package space.arim.injector;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.inject.Named;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import space.arim.injector.error.MisconfiguredBindingsException;

public class AutomaticConcreteBindingsTest {

	@ParameterizedTest
	@EnumSource
	public void testAutomaticBindingFromConcreteType(SpecificationSupport specification) {
		Injector injector = InjectorCreator.newInjector(specification);

		assertNotNull(injector.request(ConcreteType.class));
	}

	@ParameterizedTest
	@EnumSource
	public void testQualifiedIdentifiersRequireExplicitBindings(SpecificationSupport specification) {
		Injector injector = InjectorCreator.newInjector(specification);

		assertThrows(MisconfiguredBindingsException.class, () -> {
			injector.request(Identifier.ofTypeAndNamed(ConcreteType.class, NAMED_VALUE));
		});
	}

	@Test // No use testing Javax - Javax support will not recognise jakarta.inject.Named
	public void testQualifiedIdentifiersWithExplicitBindings() {
		Injector injector = Injector.newInjector(new BinderOfQualifier());

		assertNotNull(injector.request(Identifier.ofTypeAndNamed(ConcreteType.class, NAMED_VALUE)));
	}

	public static class ConcreteType {

	}

	private static final String NAMED_VALUE = "qualification";

	public static class BinderOfQualifier {
		@Named(NAMED_VALUE)
		public ConcreteType qualified(ConcreteType unqualified) {
			return unqualified;
		}
	}
}
