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
import static org.junit.jupiter.api.Assertions.fail;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import space.arim.injector.example.Plane;

public class MethodInjectionAndSuperclassesFirstTest {

	@Test
	public void testPublicMethodInjection() {
		new InjectorBuilder().build().request(ExtendedYuck.class);
	}

	@Test
	public void testPrivateMethodInjection() {
		new InjectorBuilder().privateInjection(true).build().request(ExtendedYuck.class);
	}

	@Test
	public void testStaticMethodInjection() {
		new InjectorBuilder().staticInjection(true).build().request(ExtendedYuck.class);
	}

	@Test
	public void testPrivateStaticMethodInjection() {
		new InjectorBuilder().privateInjection(true).staticInjection(true).build().request(ExtendedYuck.class);
	}

	@SuppressWarnings("unused")
	public static class ExtendedYuck extends FieldInjectionTest.Yuck {

		@Inject
		public void inject(Plane plane) {
			assertNotNull(plane);

			assertNotNull(publicWing, "Superclasses injected first");
		}

		@Inject
		private void injectPrivate(Plane plane) {
			assertNotNull(plane);

			assertNotNull(publicWing, "Superclasses injected first");
			assertNotNull(privateWing(), "Superclasses injected first");
		}

		@Inject
		public static void injectStatic(Plane plane) {
			assertNotNull(plane);

			assertNotNull(disgustingWing, "Superclasses injected first");
		}

		@Inject
		private static void injectPrivateStatic(Plane plane) {
			assertNotNull(plane);

			assertNotNull(disgustingWing, "Superclasses injected first");
		}

		public void dontInject1(Plane plane) { fail(); }
		private void dontInject2(Plane plane) { fail(); }
		public static void dontInject3(Plane plane) { fail(); }
		private static void dontInject4(Plane plane) { fail();}
	}

}
