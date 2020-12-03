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

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;

import jakarta.inject.Named;
import jakarta.inject.Qualifier;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import space.arim.injector.Identifier;
import space.arim.injector.error.MisannotatedInjecteeException;
import space.arim.injector.internal.reflect.qualifier.QualifiersInAnnotations;
import space.arim.injector.internal.spec.JakartaSpecSupportProvider;
import space.arim.injector.internal.spec.SpecSupport;

public class IdentifierCreationTest {

	/*
	 * This test set does not use @EnumSource. If it did, qualifiers
	 * would need to be specified in jakarta.inject and javax.inject.
	 * However, that would lead to duplicate qualifier exceptions.
	 */

	private Identifier<IdentifierCreationTest> createFromMethod(SpecSupport spec, String methodName)
			throws NoSuchMethodException, SecurityException {
		Annotation[] annotations = getClass().getDeclaredMethod(methodName).getAnnotations();
		return new IdentifierCreation<>(IdentifierCreationTest.class,
				new QualifiersInAnnotations(annotations)).createIdentifier(spec);
	}

	// Unqualified

	@SuppressWarnings("unused")
	private void unqualifiedMethod() {}

	@ParameterizedTest
	@ArgumentsSource(JakartaSpecSupportProvider.class)
	public void testUnqualifiedType(SpecSupport spec) throws NoSuchMethodException, SecurityException {
		assertEquals(
				Identifier.ofType(IdentifierCreationTest.class),
				createFromMethod(spec, "unqualifiedMethod"));
	}

	// User qualifier

	@Qualifier
	@Retention(RUNTIME)
	private @interface TestQualifier {}

	@TestQualifier
	private void qualifiedMethod() {}

	@ParameterizedTest
	@ArgumentsSource(JakartaSpecSupportProvider.class)
	public void testUserQualifier(SpecSupport spec) throws NoSuchMethodException, SecurityException {
		assertEquals(
				Identifier.ofTypeAndQualifier(IdentifierCreationTest.class, TestQualifier.class),
				createFromMethod(spec, "qualifiedMethod"));
	}

	// Named qualifier

	private static final String NAMED_VALUE = "qual";
	@Named(NAMED_VALUE)
	private void namedQualifiedMethod() {}

	@ParameterizedTest
	@ArgumentsSource(JakartaSpecSupportProvider.class)
	public void testNamedQualifier(SpecSupport spec) throws NoSuchMethodException, SecurityException {
		assertEquals(
				Identifier.ofTypeAndNamed(IdentifierCreationTest.class, NAMED_VALUE),
				createFromMethod(spec, "namedQualifiedMethod"));
	}

	// Too many qualifiers

	@Qualifier
	@Retention(RUNTIME)
	private @interface AnotherQualifier {}

	@TestQualifier
	@AnotherQualifier
	private void doublyQualifiedMethod() {}

	@ParameterizedTest
	@ArgumentsSource(JakartaSpecSupportProvider.class)
	public void testDuplicateQualifiers(SpecSupport spec) {
		assertThrows(MisannotatedInjecteeException.class, () -> createFromMethod(spec, "doublyQualifiedMethod"));
	}

	@TestQualifier
	@Named(NAMED_VALUE)
	private void doublyQualifiedMethodWithNamed() {}

	@ParameterizedTest
	@ArgumentsSource(JakartaSpecSupportProvider.class)
	public void testDuplicateQualifiersWithNamed(SpecSupport spec) {
		assertThrows(MisannotatedInjecteeException.class, () -> createFromMethod(spec, "doublyQualifiedMethodWithNamed"));
	}

}
