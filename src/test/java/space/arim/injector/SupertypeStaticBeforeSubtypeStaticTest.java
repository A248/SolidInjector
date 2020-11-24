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
import org.junit.jupiter.api.Test;

import space.arim.injector.example.Wing;

public class SupertypeStaticBeforeSubtypeStaticTest {

	@Test
	public void testSupertypeStaticMethodBeforeSubtypeStaticField() {
		Injector injector = new InjectorBuilder().staticInjection(true).build();
		Super superObj = injector.request(Sub.class);
		assertNotNull(superObj);
		assertNull(Super.witnessedSubclassStaticFieldInjection);
	}

	@Test
	public void testSupertypeStaticMethodBeforeSubtypeStaticMethod() {
		Injector injector = new InjectorBuilder().staticInjection(true).build();
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
		@Inject
		public static void superStaticMethodInjection() {
			assertNull(witnessedSubclassStaticFieldInjection);
			witnessedSubclassStaticFieldInjection = Sub.staticFieldInjection;
			assertNull(witnessedSubclassStaticMethodInjection);
			witnessedSubclassStaticMethodInjection = Sub.staticMethodInjection;
		}
	}

	public static class Sub extends Super {
		@Inject
		public static Wing staticFieldInjection;
		static Wing staticMethodInjection;

		@Inject
		public static void injectSubMethod(Wing wing) {
			Sub.staticMethodInjection = wing;
		}
	}
}
