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

import space.arim.injector.Identifier;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public interface ProviderMap {

	// Fails by returning error message
	String installProvider(Identifier<?> identifier, ContextualProvider<?> provider);

	<U> ContextualProvider<U> requestSingleProvider(Identifier<U> identifier,
													Function<Identifier<U>, ContextualProvider<U>> creator);

	<U> ContextualProvider<Set<U>> requestMultipleProviders(Identifier<U> identifier);

	<U> Optional<ContextualProvider<U>> requestProviderOptionally(Identifier<U> identifier);

	ProviderMap makeConcurrent();

	boolean permitsMultiBindings();

}
