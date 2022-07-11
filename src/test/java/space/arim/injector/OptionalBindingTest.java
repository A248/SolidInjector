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
import jakarta.inject.Named;
import jakarta.inject.Provider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import space.arim.injector.error.InjectorException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OptionalBindingTest {

	private Impl impl;
	private Injector injector;

	// This test needs to parameterize across multi-bindings to cover different implementations of ProviderMap
	private void setInjector(boolean multiBindings) {
		impl = new Impl();
		injector = new InjectorBuilder()
				.bindInstance(Service.class, impl)
				.multiBindings(multiBindings)
				.optionalBindings(true)
				.build();
	}

	@Test
	public void featureNotEnabled() {
		assertThrows(InjectorException.class, () -> Injector.newInjector().requestOptionalInstance(Impl.class));
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	public void requestByInjector(boolean multiBindings) {
		setInjector(multiBindings);

		assertEquals(Optional.of(impl),
				injector.requestOptionalInstance(Service.class));
		assertEquals(Optional.empty(),
				injector.requestOptionalInstance(Identifier.ofTypeAndNamed(Service.class, "empty")));
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	public void requestByConstructor(boolean multiBindings) {
		setInjector(multiBindings);

		ConstructorRequest constructorRequest = injector.request(ConstructorRequest.class);
		assertEquals(Optional.of(impl), constructorRequest.presentOptionalService);
		assertEquals(Optional.empty(), constructorRequest.emptyOptionalService);
		assertEquals(Optional.of(impl), constructorRequest.providedPresentOptionalService.map(Provider::get));
		assertEquals(Optional.empty(), constructorRequest.providedEmptyOptionalService);
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	public void requestByField(boolean multiBindings) {
		setInjector(multiBindings);

		FieldRequest fieldRequest = injector.request(FieldRequest.class);
		assertEquals(Optional.of(impl), fieldRequest.presentOptionalService);
		assertEquals(Optional.empty(), fieldRequest.emptyOptionalService);
		assertEquals(Optional.of(impl), fieldRequest.providedPresentOptionalService.map(Provider::get));
		assertEquals(Optional.empty(), fieldRequest.providedEmptyOptionalService);
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	public void requestByMethod(boolean multiBindings) {
		setInjector(multiBindings);

		MethodRequest methodRequest = injector.request(MethodRequest.class);
		assertEquals(Optional.of(impl), methodRequest.presentOptionalService);
		assertEquals(Optional.empty(), methodRequest.emptyOptionalService);
		assertEquals(Optional.of(impl), methodRequest.providedPresentOptionalService.map(Provider::get));
		assertEquals(Optional.empty(), methodRequest.providedEmptyOptionalService);
	}

	public interface Service {}
	public static class Impl implements Service {}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static class ConstructorRequest {

		private final Optional<Service> presentOptionalService;
		private final Optional<Service> emptyOptionalService;
		private final Optional<Provider<Service>> providedPresentOptionalService;
		private final Optional<Provider<Service>> providedEmptyOptionalService;

		@Inject
		public ConstructorRequest(Optional<Service> presentOptionalService,
								  @Named("empty") Optional<Service> emptyOptionalService,
								  Optional<Provider<Service>> providedPresentOptionalService,
								  @Named("empty") Optional<Provider<Service>> providedEmptyOptionalService) {
			this.presentOptionalService = presentOptionalService;
			this.emptyOptionalService = emptyOptionalService;
			this.providedPresentOptionalService = providedPresentOptionalService;
			this.providedEmptyOptionalService = providedEmptyOptionalService;
		}
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static class FieldRequest {
		@Inject
		public Optional<Service> presentOptionalService;
		@Inject
		@Named("empty")
		public Optional<Service> emptyOptionalService;
		@Inject
		public Optional<Provider<Service>> providedPresentOptionalService;
		@Inject
		@Named("empty")
		public Optional<Provider<Service>> providedEmptyOptionalService;
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static class MethodRequest {

		private Optional<Service> presentOptionalService;
		private Optional<Service> emptyOptionalService;
		private Optional<Provider<Service>> providedPresentOptionalService;
		private Optional<Provider<Service>> providedEmptyOptionalService;

		@Inject
		public void injectOptionalServices(Optional<Service> presentOptionalService,
										   @Named("empty") Optional<Service> emptyOptionalService,
										   Optional<Provider<Service>> providedPresentOptionalService,
										   @Named("empty") Optional<Provider<Service>> providedEmptyOptionalService) {
			this.presentOptionalService = presentOptionalService;
			this.emptyOptionalService = emptyOptionalService;
			this.providedPresentOptionalService = providedPresentOptionalService;
			this.providedEmptyOptionalService = providedEmptyOptionalService;
		}
	}
}
