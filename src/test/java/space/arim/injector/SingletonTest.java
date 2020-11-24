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

import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.junit.jupiter.api.Test;

public class SingletonTest {

	@Test
	public void testInstantiatedOnce() {
		Injector injector = Injector.newInjector(new SingletonImplBinder());
		ISingleton singleton = injector.request(ISingleton.class);
		assertNotNull(singleton);
		ISingleton another = injector.request(ISingleton.class);
		assertTrue(singleton == another);
	}

	public interface ISingleton {}

	@Singleton
	public static class SingletonImpl implements ISingleton {

		private static final AtomicBoolean instantiated = new AtomicBoolean(false);

		@Inject
		public SingletonImpl() {
			boolean firstInstantiation = instantiated.compareAndSet(false, true);
			assertTrue(firstInstantiation);
		}
	}

	public static class SingletonImplBinder {

		public ISingleton singleton(SingletonImpl singleton) {
			return singleton;
		}
	}
}
