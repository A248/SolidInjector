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
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import space.arim.injector.example.Wing;

public class SupertypeStaticBeforeSubtypeStaticTest {

	@ParameterizedTest
	@EnumSource
	public void testSupertypeStaticMethodBeforeSubtypeStaticField(SpecificationSupport specification) {
		Injector injector = new InjectorBuilder()
				.specification(specification)
				.staticInjection(true)
				.build();

		Super superObj = injector.request(Sub.class);
		assertNotNull(superObj);
		assertNull(Super.witnessedSubclassStaticFieldInjection);
	}

	@ParameterizedTest
	@EnumSource
	public void testSupertypeStaticMethodBeforeSubtypeStaticMethod(SpecificationSupport specification) {
		Injector injector = new InjectorBuilder()
				.specification(specification)
				.staticInjection(true)
				.build();

		Super superObj = injector.request(Sub.class);
		assertNotNull(superObj);
		assertNull(Super.witnessedSubclassStaticMethodInjection);
	}

	@AfterEach
	public void tearDown() {
		Super.witnessedSubclassStaticFieldInjection = null;
		Super.witnessedSubclassStaticMethodInjection = null;
		Sub.staticFieldInjection = null;
		Sub.staticMethodInjection = null;
	}

	public static class Super {

		static Wing witnessedSubclassStaticFieldInjection;
		static Wing witnessedSubclassStaticMethodInjection;

		@javax.inject.Inject
		@Inject
		public static void superStaticMethodInjection() {
			assertNull(witnessedSubclassStaticFieldInjection);
			witnessedSubclassStaticFieldInjection = Sub.staticFieldInjection;
			assertNull(witnessedSubclassStaticMethodInjection);
			witnessedSubclassStaticMethodInjection = Sub.staticMethodInjection;
		}
	}

	public static class Sub extends Super {

		@javax.inject.Inject
		@Inject
		public static Wing staticFieldInjection;
		static Wing staticMethodInjection;

		@javax.inject.Inject
		@Inject
		public static void injectSubMethod(Wing wing) {
			Sub.staticMethodInjection = wing;
		}
	}
}
