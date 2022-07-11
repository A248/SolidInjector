/*
 * SolidInjector
 * Copyright © 2022 Anand Beh
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

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Qualifier;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiBindingTest {

	private Impl3 impl3;
	private Injector injector;

	private Set<MyService> impls;
	private Set<MyService> qualifiedImpls;

	@BeforeEach
	public void setInjector() {
		impl3 = new Impl3();
		injector = new InjectorBuilder()
				.addBindModules(new BindModule(impl3))
				.multiBindings(true)
				.build();

		impls = injector.requestMultipleInstances(MyService.class);
		qualifiedImpls = injector.requestMultipleInstances(
				Identifier.ofTypeAndQualifier(MyService.class, MyQualifier.class));
	}

	@Test
	public void requestByInjector() {
		assertTrue(impls.contains(impl3));
		assertEquals(
				Set.of(Impl1.class, Impl2.class, Impl3.class),
				impls.stream().map(Object::getClass).collect(Collectors.toUnmodifiableSet()));
		assertEquals(
				Set.of(Impl1.class, Impl2.class),
				qualifiedImpls.stream().map(Object::getClass).collect(Collectors.toUnmodifiableSet()));
	}

	@Test
	public void requestByConstructor() {
		ConstructorRequest constructorRequest = injector.request(ConstructorRequest.class);
		assertEquals(impls, constructorRequest.myServices);
		assertEquals(qualifiedImpls, constructorRequest.myQualifiedServices);
		assertEquals(impls, constructorRequest.myProvidedServices.get());
		assertEquals(qualifiedImpls, constructorRequest.myProvidedQualifiedServices.get());
	}

	@Test
	public void requestByField() {
		FieldRequest fieldRequest = injector.request(FieldRequest.class);
		assertEquals(impls, fieldRequest.myServices);
		assertEquals(qualifiedImpls, fieldRequest.myQualifiedServices);
		assertEquals(impls, fieldRequest.myProvidedServices.get());
		assertEquals(qualifiedImpls, fieldRequest.myProvidedQualifiedServices.get());
	}

	@Test
	public void requestByMethod() {
		MethodRequest methodRequest = injector.request(MethodRequest.class);
		assertEquals(impls, methodRequest.myServices);
		assertEquals(qualifiedImpls, methodRequest.myQualifiedServices);
		assertEquals(impls, methodRequest.myProvidedServices.get());
		assertEquals(qualifiedImpls, methodRequest.myProvidedQualifiedServices.get());
	}

	@Test
	public void noBindingsRegistered() {
		Identifier<MyService> alienIdentifier = Identifier.ofTypeAndNamed(MyService.class, "none bound");
		assertEquals(Set.of(), injector.requestMultipleInstances(alienIdentifier));
	}

	public static class ConstructorRequest {

		private final Set<MyService> myServices;
		private final Set<MyService> myQualifiedServices;
		private final Provider<Set<MyService>> myProvidedServices;
		private final Provider<Set<MyService>> myProvidedQualifiedServices;

		@Inject
		public ConstructorRequest(@MultiBinding Set<MyService> myServices,
								  @MultiBinding Set<@MyQualifier MyService> myQualifiedServices,
								  @MultiBinding Provider<Set<MyService>> myProvidedServices,
								  @MultiBinding Provider<Set<@MyQualifier MyService>> myProvidedQualifiedServices) {
			this.myServices = myServices;
			this.myQualifiedServices = myQualifiedServices;
			this.myProvidedServices = myProvidedServices;
			this.myProvidedQualifiedServices = myProvidedQualifiedServices;
		}
	}

	public static class FieldRequest {
		@Inject
		@MultiBinding
		public Set<MyService> myServices;
		@Inject
		@MultiBinding
		public Set<@MyQualifier MyService> myQualifiedServices;
		@Inject
		@MultiBinding
		public Provider<Set<MyService>> myProvidedServices;
		@Inject
		@MultiBinding
		public Provider<Set<@MyQualifier MyService>> myProvidedQualifiedServices;
	}

	public static class MethodRequest {

		private Set<MyService> myServices;
		private Set<MyService> myQualifiedServices;
		private Provider<Set<MyService>> myProvidedServices;
		private Provider<Set<MyService>> myProvidedQualifiedServices;

		@Inject
		public void injectMyServices(@MultiBinding Set<MyService> myServices,
									 @MultiBinding Set<@MyQualifier MyService> myQualifiedServices,
									 @MultiBinding Provider<Set<MyService>> myProvidedServices,
									 @MultiBinding Provider<Set<@MyQualifier MyService>> myProvidedQualifiedServices) {
			this.myServices = myServices;
			this.myQualifiedServices = myQualifiedServices;
			this.myProvidedServices = myProvidedServices;
			this.myProvidedQualifiedServices = myProvidedQualifiedServices;
		}
	}

	public class BindModule {

		private final Impl3 impl3;

		public BindModule(Impl3 impl3) {
			this.impl3 = impl3;
		}

		@MultiBinding
		public MyService impl1(Impl1 impl1) {
			return impl1;
		}

		@MultiBinding
		public MyService impl2(Impl2 impl2) {
			return impl2;
		}

		@MultiBinding
		public MyService impl3() {
			return impl3;
		}

		@MultiBinding
		@MyQualifier
		public MyService impl1Qualified(Impl1 impl1) {
			return impl1;
		}

		@MultiBinding
		@MyQualifier
		public MyService impl2Qualified(Impl2 impl2) {
			return impl2;
		}
	}

	public interface MyService {}
	@Singleton
	public static final class Impl1 implements MyService {}
	@Singleton
	public static final class Impl2 implements MyService {}
	public static final class Impl3 implements MyService {}

	@Qualifier
	@Retention(RUNTIME)
	@Target({METHOD, TYPE_USE})
	public @interface MyQualifier {}

}
