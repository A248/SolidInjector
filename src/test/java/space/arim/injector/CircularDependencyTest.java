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

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import org.junit.jupiter.api.Test;

import space.arim.injector.error.CircularDependencyException;

public class CircularDependencyTest {

	@Test
	public void testCircularDependencies() {
		Injector injector = Injector.newInjector(new CircularBinder());
		assertThrows(CircularDependencyException.class, () -> injector.request(Dependent.class));
	}

	@Test
	public void testBreakWithProviders() {
		Injector injector = Injector.newInjector(new CircleBreakingBinder());
		assertNotNull(injector.request(Dependent.class));
		assertNotNull(injector.request(Dependency.class));
	}

	public interface Dependent {}
	public static class HardDependent implements Dependent {
		@Inject
		public HardDependent(Dependency dependency) {
			assertNotNull(dependency);
		}
	}
	public static class FlexibleDependent implements Dependent {
		@Inject
		public FlexibleDependent(Provider<Dependency> provider) {
			assertNotNull(provider);
		}
	}

	public interface Dependency {}
	public static class DependencyImpl implements Dependency {
		@Inject
		public DependencyImpl(Dependent dependent) {
			assertNotNull(dependent);
		}
	}

	public static class CircularBinder {

		public Dependent dependent(HardDependent dependent) {
			return dependent;
		}

		public Dependency dependency(DependencyImpl dependency) {
			return dependency;
		}

	}

	public static class CircleBreakingBinder {

		public Dependent dependent(FlexibleDependent dependent) {
			return dependent;
		}

		public Dependency dependency(DependencyImpl dependency) {
			return dependency;
		}

	}

}
