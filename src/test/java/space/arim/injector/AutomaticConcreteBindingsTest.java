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

import space.arim.injector.error.MisconfiguredBindingsException;

public class AutomaticConcreteBindingsTest {

	@Test
	public void testAutomaticBindingFromConcreteType() {
		Injector injector = Injector.newInjector();
		assertNotNull(injector.request(ConcreteType.class));
	}

	@Test
	public void testQualifiedIdentifiersRequireExplicitBindings() {
		Injector injector = Injector.newInjector();
		assertThrows(MisconfiguredBindingsException.class, () -> {
			injector.request(Identifier.ofTypeAndNamed(ConcreteType.class, QUALIFIER_VALUE));
		});
	}
	
	@Test
	public void testQualifiedIdentifiersWithExplicitBindings() {
		Injector injector = Injector.newInjector(new BinderOfQualifier());
		assertNotNull(injector.request(Identifier.ofTypeAndNamed(ConcreteType.class, QUALIFIER_VALUE)));
	}

	public static class ConcreteType {
		
	}

	private static final String QUALIFIER_VALUE = "qualification";

	public static class BinderOfQualifier {
		@Named(QUALIFIER_VALUE)
		public ConcreteType qualified(ConcreteType unqualified) {
			return unqualified;
		}
	}
}
