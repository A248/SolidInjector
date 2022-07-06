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

package space.arim.injector.internal;

import space.arim.injector.Identifier;
import space.arim.injector.internal.provider.ContextualProvider;

import java.util.Set;

public interface DependencyRepository {

	DependencyRepository getRoot();

	<U> ContextualProvider<U> requestProvider(Identifier<U> identifier);

	<U> U requestInstance(Identifier<U> identifier);

	<U> ContextualProvider<Set<U>> requestMultipleProviders(Identifier<U> identifier);

	<U> Set<U> requestMultipleInstances(Identifier<U> identifier);

}
