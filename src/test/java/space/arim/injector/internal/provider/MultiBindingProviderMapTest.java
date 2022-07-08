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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.arim.injector.Identifier;
import space.arim.injector.error.MisconfiguredBindingsException;
import space.arim.injector.error.MultiBindingRelatedException;
import space.arim.injector.internal.DependencyRepository;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class MultiBindingProviderMapTest {

	private ProviderMap providerMap;
	private final Identifier<MyService> identifier = Identifier.ofType(MyService.class);

	@BeforeEach
	public void setProviderMap() {
		providerMap = new MultiBindingProviderMap();
	}

	@Test
	public void multiBindingFeature(@Mock DependencyRepository repository) {
		MyService instanceOne = new MyService.Impl();
		ContextualProvider<MyService> providerOne = new MultiBindableContextualProvider<>(new FixedContextualProvider<>(instanceOne));
		assertNull(providerMap.installProvider(identifier, providerOne));

		assertEquals(
				Set.of(instanceOne),
				providerMap.requestMultipleProviders(identifier).provideUsing(repository));

		MyService instanceTwo = new MyService.Impl();
		ContextualProvider<MyService> providerTwo = new MultiBindableContextualProvider<>(new FixedContextualProvider<>(instanceTwo));
		assertNull(providerMap.installProvider(identifier, providerTwo));

		assertEquals(
				Set.of(instanceOne, instanceTwo),
				providerMap.requestMultipleProviders(identifier).provideUsing(repository));
		assertThrows(MultiBindingRelatedException.class,
				() -> providerMap.requestSingleProvider(identifier, (ident) -> { throw new AssertionError(); }));

		MyService instanceThree = new MyService.Impl();
		ContextualProvider<MyService> providerThree = new MultiBindableContextualProvider<>(new FixedContextualProvider<>(instanceThree));
		assertNull(providerMap.installProvider(identifier, providerThree));

		assertEquals(
				Set.of(instanceOne, instanceTwo, instanceThree),
				providerMap.requestMultipleProviders(identifier).provideUsing(repository));
	}

	@Test
	public void multiBindingNotPermittedByExistingProvider() {
		assertNull(providerMap.installProvider(identifier, new MyService.Provider()));
		assertNotNull(providerMap.installProvider(identifier, new MultiBindableContextualProvider<>(new MyService.Provider())));
	}

	@Test
	public void multiBindingNotPermittedByNewProvider() {
		assertNull(providerMap.installProvider(identifier, new MultiBindableContextualProvider<>(new MyService.Provider())));
		assertNotNull(providerMap.installProvider(identifier, new MyService.Provider()));
	}

	@Test
	public void multiBindingNotPermittedByThirdProvider() {
		assertNull(providerMap.installProvider(identifier, new MultiBindableContextualProvider<>(new MyService.Provider())));
		assertNull(providerMap.installProvider(identifier, new MultiBindableContextualProvider<>(new MyService.Provider())));
		assertNotNull(providerMap.installProvider(identifier, new MyService.Provider()));
	}

	@Test
	public void noBindingsRegistered(@Mock DependencyRepository repository) {
		assertEquals(Set.of(), providerMap.requestMultipleProviders(identifier).provideUsing(repository));
	}

	@Test
	public void multiBindingNotPermittedButWasRequested() {
		assertNull(providerMap.installProvider(identifier, new MyService.Provider()));
		assertThrows(MultiBindingRelatedException.class, () -> providerMap.requestMultipleProviders(identifier));
	}
}
