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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class SingletonTest {

	public interface IFace {}

	@ParameterizedTest
	@EnumSource
	public void testSingletonsInstantiatedOnce(SpecificationSupport specification) {
		Injector injector = InjectorCreator.newInjector(specification, new SingletonImplBinder());

		IFace singleton = injector.request(IFace.class);
		assertNotNull(singleton);
		assertEquals(SingletonImpl.class, singleton.getClass(), "Binding failure");
		assertTrue(singleton == injector.request(IFace.class), "Re-requested singleton must be same instance");
	}

	@ParameterizedTest
	@EnumSource
	public void testNonSingletonsInstantiatedMultipleTimes(SpecificationSupport specification) {
		Injector injector = InjectorCreator.newInjector(specification, new NonSingletonImplBinder());

		IFace nonSingleton = injector.request(IFace.class);
		assertNotNull(nonSingleton);
		assertEquals(NonSingletonImpl.class, nonSingleton.getClass(), "Binding failure");
		assertFalse(nonSingleton == injector.request(IFace.class), "Re-requested non-singleton must be different instance");
	}

	@AfterEach
	public void tearDown() {
		SingletonImpl.instantiated.set(false);
	}

	@javax.inject.Singleton
	@Singleton
	public static class SingletonImpl implements IFace {

		static final AtomicBoolean instantiated = new AtomicBoolean(false);

		@Inject
		public SingletonImpl() {
			assertTrue(instantiated.compareAndSet(false, true), "Instantiated multiple times");
		}
	}

	public static class SingletonImplBinder {

		public IFace singleton(SingletonImpl singleton) {
			return singleton;
		}
	}

	public static class NonSingletonImpl implements IFace {}

	public static class NonSingletonImplBinder {
		public IFace singleton(NonSingletonImpl nonSingleton) {
			return nonSingleton;
		}
	}

}
