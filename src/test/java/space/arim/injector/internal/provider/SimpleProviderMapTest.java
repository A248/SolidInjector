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
import space.arim.injector.Identifier;
import space.arim.injector.error.MultiBindingRelatedException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SimpleProviderMapTest {

	private ProviderMap providerMap;
	private final Identifier<MyService> identifier = Identifier.ofType(MyService.class);

	@BeforeEach
	public void setProviderMap() {
		providerMap = new SimpleProviderMap();
	}

	@Test
	public void installConflictingProvidersWhereMultiBindingPermitted() {
		ContextualProvider<MyService> provider = new MultiBindableContextualProvider<>(new MyService.Provider());
		ContextualProvider<MyService> providerTwo = new MultiBindableContextualProvider<>(new MyService.Provider());
		assertNull(providerMap.installProvider(identifier, provider));
		assertNotNull(providerMap.installProvider(identifier, providerTwo));
	}

	@Test
	public void requestMultipleProviders() {
		assertThrows(MultiBindingRelatedException.class, () -> providerMap.requestMultipleProviders(identifier));
	}

}
