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

package space.arim.injector.internal.provider;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.junit.jupiter.MockitoExtension;
import space.arim.injector.Identifier;
import space.arim.injector.internal.DependencyRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ProviderMapTest {

	private final Identifier<MyService> identifier = Identifier.ofType(MyService.class);

	@ParameterizedTest
	@ArgumentsSource(ProviderMapFactory.class)
	public void installAndRequestInstance(ProviderMap providerMap) {
		DependencyRepository repository = mock(DependencyRepository.class);

		MyService instance = new MyService.Impl();
		assertNull(
				providerMap.installProvider(identifier, new FixedContextualProvider<>(instance)));
		assertEquals(
				instance,
				providerMap.requestSingleProvider(identifier, (i) -> {
					throw new AssertionError("Provider should already be installed");
				}).provideUsing(repository)
		);
	}

	@ParameterizedTest
	@ArgumentsSource(ProviderMapFactory.class)
	public void requestAndCreateInstance(ProviderMap providerMap) {
		DependencyRepository repository = mock(DependencyRepository.class);

		MyService instance = new MyService.Impl();
		assertEquals(
				instance,
				providerMap.requestSingleProvider(identifier, (ident) -> {
					return new FixedContextualProvider<>(instance);
				}).provideUsing(repository)
		);
	}

	@ParameterizedTest
	@ArgumentsSource(ProviderMapFactory.class)
	public void installProviderWithMultiBindingPermitted(ProviderMap providerMap) {
		assertNull(
				providerMap.installProvider(identifier,
						new MultiBindableContextualProvider<>(new MyService.Provider()))
		);
	}

	@ParameterizedTest
	@ArgumentsSource(ProviderMapFactory.class)
	public void installConflictingProvidersWhereMultiBindingProhibited(ProviderMap providerMap) {
		// Because multibinding is not permitted by the provider, this should always fail
		assertNull(providerMap.installProvider(identifier, new MyService.Provider()));
		assertNotNull(providerMap.installProvider(identifier, new MyService.Provider()));
	}

	@ParameterizedTest
	@ArgumentsSource(ProviderMapFactory.class)
	public void installDuplicateProvidersAtDifferentIdentifiers(ProviderMap providerMap) {
		ContextualProvider<MyService> provider = new MyService.Provider();
		Identifier<MyService> identifierTwo = Identifier.ofTypeAndNamed(MyService.class, "qualifier");
		assertNull(providerMap.installProvider(identifier, provider));
		assertNull(providerMap.installProvider(identifierTwo, provider));
	}
}
