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

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

public class ConstructorInjectionTest {

	@Test
	public void testPublicExplicitCtor() {
		assertNotNull(Injector.newInjector().request(PublicExplicitCtor.class));
	}

	@Test
	public void testPublicDefaultCtor() {
		assertNotNull(Injector.newInjector().request(PublicDefaultCtor.class));
	}

	@Test
	public void testPrivateCtor() {
		Injector injector = new InjectorBuilder().privateInjection(true).build();
		assertNotNull(injector.request(PrivateCtor.class));
	}

	public static class PublicExplicitCtor {

		@Inject
		public PublicExplicitCtor() {}
	}

	public static class PublicDefaultCtor {}

	public static class PrivateCtor {

		@Inject
		private PrivateCtor() {}
	}

}
