/*
 * SolidInjector
 * Copyright © 2023 Anand Beh
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

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;
import space.arim.injector.error.MisconfiguredBindingsException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ManualBindingTest {

	public interface Service {}
	public static class Impl implements Service { }
	public static class ExtendedImpl extends Impl {}

	@Test
	public void testInstanceBinding() {
		Impl impl = new Impl();
		Injector injector = new InjectorBuilder().bindInstance(Service.class, impl).build();
		assertEquals(impl, injector.request(Service.class));
	}

	@Test
	public void testIdentifierBinding() {
		Injector injector = new InjectorBuilder().bindIdentifier(Service.class, Impl.class).build();
		assertDoesNotThrow(() -> injector.request(Service.class));
	}

	@Test
	public void testIdentifierBindingTransitivity() {
		Injector injector = new InjectorBuilder()
				.bindIdentifier(Service.class, Impl.class)
				.bindIdentifier(Impl.class, ExtendedImpl.class)
				.build();
		assertTrue(injector.request(Impl.class) instanceof ExtendedImpl);
	}

	public static class ExtendedImplAsSingleton {
		@Singleton
		public @Named("as-singleton") ExtendedImpl extendedImpl() {
			return new ExtendedImpl();
		}
	}
	@Test
	public void testIdentifierBindingTransitivityToSingleton() {
		Injector injector = new InjectorBuilder()
				.bindIdentifier(Service.class, Impl.class)
				.bindIdentifier(Identifier.ofType(Impl.class), Identifier.ofTypeAndNamed(ExtendedImpl.class, "as-singleton"))
				.addBindModules(new ExtendedImplAsSingleton())
				.build();
		Service impl = injector.request(Service.class);
		assertSame(impl, injector.request(Impl.class));
		assertTrue(impl instanceof ExtendedImpl);
	}

	@Test
	public void testIdentifierAlreadyBoundInstance() {
		InjectorBuilder builder = new InjectorBuilder().bindInstance(Service.class, new Impl());
		assertThrows(MisconfiguredBindingsException.class, () -> builder.bindInstance(Service.class, new Impl()));
		assertThrows(MisconfiguredBindingsException.class, () -> builder.bindIdentifier(Service.class, Impl.class));
	}

	@Test
	public void testIdentifierAlreadyBoundIdentifier() {
		InjectorBuilder builder = new InjectorBuilder().bindIdentifier(Service.class, Impl.class);
		assertThrows(MisconfiguredBindingsException.class, () -> builder.bindInstance(Service.class, new Impl()));
		assertThrows(MisconfiguredBindingsException.class, () -> builder.bindIdentifier(Service.class, Impl.class));
	}

	@Test
	public void testIdentifierBoundToItself() {
		InjectorBuilder builder = new InjectorBuilder();
		assertThrows(IllegalArgumentException.class, () -> builder.bindIdentifier(Impl.class, Impl.class));
	}

}
