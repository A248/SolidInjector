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
package space.arim.injector.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import space.arim.injector.error.CircularDependencyException;
import space.arim.injector.error.InjectionInvocationException;
import space.arim.injector.error.InjectorException;
import space.arim.injector.error.InjectorInternalFailureException;
import space.arim.injector.error.MisannotatedInjecteeException;
import space.arim.injector.error.MisconfiguredBindingsException;

public class ExceptionContextTest {

	private final ExceptionContext context = new ExceptionContext();

	private void throwAndRewrapMisannotatedInjecteeException() {
		try {
			// Simulate naturally thrown exception
			throw new MisannotatedInjecteeException();
		} catch (InjectorException ex) {
			throw context.rethrow(ex, "some added context");
		}
	}

	@Test
	public void testHardcodedSameException() {
		assertThrows(MisannotatedInjecteeException.class, this::throwAndRewrapMisannotatedInjecteeException);
	}

	@Test
	public void testHardcodedPreserveCause() {
		try {
			throwAndRewrapMisannotatedInjecteeException();
		} catch (InjectorException ex) {
			Throwable cause = ex.getCause();
			assertNotNull(cause, "Cause should be preserved");
			assertEquals(MisannotatedInjecteeException.class, cause.getClass(), "Cause should have same runtime class");
		}
	}

	@SuppressWarnings("unchecked")
	private static final Class<? extends InjectorException>[] exClasses = (Class<? extends InjectorException>[]) new Class<?>[] {
		CircularDependencyException.class,
		InjectionInvocationException.class,
		InjectorException.class,
		InjectorInternalFailureException.class,
		MisannotatedInjecteeException.class,
		MisconfiguredBindingsException.class
	};

	private void throwAndRewrap(Class<? extends InjectorException> exClass) {
		try {
			reflectivelyThrowException(exClass);
		} catch (InjectorException ex) {
			throw context.rethrow(ex, "Exception " + exClass.getName());
		}
	}

	@Test
	public void testComprehensiveSameException() {
		for (Class<? extends InjectorException> exClass : exClasses) {
			assertThrows(exClass, () -> throwAndRewrap(exClass));
		}
	}

	@Test
	public void testComprehensivePreserveCause() {
		for (Class<? extends InjectorException> exClass : exClasses) {
			try {
				throwAndRewrap(exClass);
			} catch (InjectorException ex) {
				Throwable cause = ex.getCause();
				assertNotNull(cause, "Cause should be preserved");
				assertEquals(exClass, cause.getClass(), "Cause should have same runtime class");
			}
		}
	}

	private static void reflectivelyThrowException(Class<? extends InjectorException> exClass) {
		try {
			throw exClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException ex) {
			fail("Cannot instantiate exception", ex);
		}
	}
}
