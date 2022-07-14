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

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodCallUsabilityTest {

	@Test
	public void addBindModulesWithListCallsCorrectMethod() {
		Path path = Path.of(".");
		Injector injector = new InjectorBuilder()
				.addBindModules(List.of(new PathBindModule(path)))
				.build();
		assertEquals(path, injector.request(Path.class));
	}

	@Test
	public void newInjectorWithListCallsCorrectMethod() {
		Path path = Path.of(".");
		Injector injector = Injector.newInjector(
				List.of(new PathBindModule(path))
		);
		assertEquals(path, injector.request(Path.class));
	}

	public static final class PathBindModule {

		private final Path path;

		private PathBindModule(Path path) {
			this.path = path;
		}

		public Path thePath() {
			return path;
		}
	}
}
