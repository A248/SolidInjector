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

import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import space.arim.injector.error.MisannotatedInjecteeException;
import space.arim.injector.example.Plane;

public class BrokenConstructorsTest {

	private Injector injector = Injector.newInjector();

	@Test
	public void testNoInjectCtors() {
		assertThrows(MisannotatedInjecteeException.class, () -> {
			injector.request(NoInjectCtors.class);
		});	
	}

	@Test
	public void testMultipleInjectCtors() {
		assertThrows(MisannotatedInjecteeException.class, () -> {
			injector.request(MultipleInjectCtors.class);
		});
	}

	public static class NoInjectCtors {

		public NoInjectCtors(@SuppressWarnings("unused") Void sig) {}
	}

	public static class MultipleInjectCtors {

		@Inject
		public MultipleInjectCtors() {}

		@Inject
		public MultipleInjectCtors(@SuppressWarnings("unused") Plane plane) {}
	}
}
