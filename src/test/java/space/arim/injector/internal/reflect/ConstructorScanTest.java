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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import space.arim.injector.error.MisannotatedInjecteeException;
import space.arim.injector.example.Plane;
import space.arim.injector.internal.InjectionSettings;
import space.arim.injector.internal.spec.AllSpecSupportProvider;
import space.arim.injector.internal.spec.SpecSupport;

public class ConstructorScanTest {

	@ParameterizedTest
	@ArgumentsSource(AllSpecSupportProvider.class)
	public void testNoInjectConstructors(SpecSupport spec) {
		InjectionSettings settings = new InjectionSettings(spec);

		assertThrows(MisannotatedInjecteeException.class, () -> {
			new ConstructorScan<>(settings, NoInjectConstructors.class).findInjectableConstructor();
		});	
	}

	public static class NoInjectConstructors {

		public NoInjectConstructors(Void sig) {}
	}

	@ParameterizedTest
	@ArgumentsSource(AllSpecSupportProvider.class)
	public void testMultipleInjectConstructors(SpecSupport spec) {
		InjectionSettings settings = new InjectionSettings(spec);

		assertThrows(MisannotatedInjecteeException.class, () -> {
			new ConstructorScan<>(settings, MultipleInjectConstructors.class).findInjectableConstructor();
		});
	}

	public static class MultipleInjectConstructors {

		@javax.inject.Inject
		@Inject
		public MultipleInjectConstructors() {}

		@javax.inject.Inject
		@Inject
		public MultipleInjectConstructors(Plane plane) {}
	}

	@ParameterizedTest
	@ArgumentsSource(AllSpecSupportProvider.class)
	public void testWellMadeConstructor(SpecSupport spec) throws NoSuchMethodException {
		InjectionSettings settings = new InjectionSettings(spec);

		assertEquals(
				WellMadeConstructor.class.getDeclaredConstructor(Plane.class),
				new ConstructorScan<>(settings, WellMadeConstructor.class).findInjectableConstructor());
	}

	public static class WellMadeConstructor {

		@javax.inject.Inject
		@Inject
		public WellMadeConstructor(Plane plane) {}
	}

}
