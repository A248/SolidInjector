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
package space.arim.injector.internal.reflect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import space.arim.injector.error.MisannotatedInjecteeException;
import space.arim.injector.example.Plane;
import space.arim.injector.internal.InjectionSettings;
import space.arim.injector.internal.spec.SpecDetector;

public class ConstructorScanTest {

	private final InjectionSettings settings = new InjectionSettings(SpecDetector.detectedSpec());

	@Test
	public void testNoInjectConstructors() {
		assertThrows(MisannotatedInjecteeException.class, () -> {
			new ConstructorScan<>(settings, NoInjectConstructors.class).findInjectableConstructor();
		});	
	}


	public static class NoInjectConstructors {

		public NoInjectConstructors(Void sig) {}
	}

	@Test
	public void testMultipleInjectConstructors() {
		assertThrows(MisannotatedInjecteeException.class, () -> {
			new ConstructorScan<>(settings, MultipleInjectConstructors.class).findInjectableConstructor();
		});
	}

	public static class MultipleInjectConstructors {

		@Inject
		public MultipleInjectConstructors() {}

		@Inject
		public MultipleInjectConstructors(Plane plane) {}
	}

	@Test
	public void testWellMadeConstructor() throws NoSuchMethodException {
		assertEquals(
				WellMadeConstructor.class.getDeclaredConstructor(Plane.class),
				new ConstructorScan<>(settings, WellMadeConstructor.class).findInjectableConstructor());
	}

	public static class WellMadeConstructor {
		@Inject
		public WellMadeConstructor(Plane plane) {}
	}

}
