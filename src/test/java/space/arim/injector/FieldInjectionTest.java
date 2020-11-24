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
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import space.arim.injector.example.Wing;

public class FieldInjectionTest {

	@Test
	public void testPublicFieldInjection() {
		Yuck yuck = new InjectorBuilder().build().request(Yuck.class);
		assertNotNull(yuck.publicWing);

		assertTrue(yuck.dontInjectFieldsRespected());
	}

	@Test
	public void testPrivateFieldInjection() {
		Yuck yuck = new InjectorBuilder().privateInjection(true).build().request(Yuck.class);
		assertNotNull(yuck.publicWing);
		assertNotNull(yuck.privateWing());

		assertTrue(yuck.dontInjectFieldsRespected());
	}

	@Test
	public void testPrivateStaticFieldInjection() {
		Yuck yuck = new InjectorBuilder().privateInjection(true).staticInjection(true).build().request(Yuck.class);
		assertNotNull(yuck.publicWing);
		assertNotNull(Yuck.disgustingWing);
		assertNotNull(yuck.privateWing());
		assertNotNull(Yuck.privateStaticWing);
		assertTrue(yuck.dontInjectFieldsRespected());
	}

	public static class Yuck {

		public static Wing dontInject1;
		private static Wing dontInject2;
		private Wing dontInject3;
		public Wing dontInject4;

		@Inject
		public static Wing disgustingWing;
		@Inject
		private static Wing privateStaticWing;
		@Inject
		private Wing privateWing;
		@Inject
		public Wing publicWing;

		@Override
		public String toString() {
			return "Yuck [disguistingWing=" + disgustingWing + ", privateStaticWing=" + privateStaticWing
					+ ", privateWing=" + privateWing() + ", publicWing=" + publicWing + "]";
		}

		boolean dontInjectFieldsRespected() {
			return dontInject1 == null && dontInject2 == null && dontInject3 == null && dontInject4 == null;
		}

		Wing privateWing() {
			return privateWing;
		}
	}

}
