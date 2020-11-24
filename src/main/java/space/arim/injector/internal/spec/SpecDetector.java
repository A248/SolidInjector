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
package space.arim.injector.internal.spec;

public final class SpecDetector {

	private static final SpecSupport SPEC;

	private SpecDetector() {
	}

	public static SpecSupport detectedSpec() {
		return SPEC;
	}

	static {
		boolean javax = false;
		try {
			Class.forName("javax.inject.Inject");
			javax = true;
		} catch (ClassNotFoundException ignored) {
		}

		boolean jakarta = false;
		try {
			Class.forName("jakarta.inject.Inject");
			jakarta = true;
		} catch (ClassNotFoundException ignored) {
		}

		SpecSupport spec;
		if (jakarta && javax) {
			spec = new CombinedSpecSupport();
		} else if (javax) {
			spec = new JavaxSupport();
		} else {
			spec = new JakartaSupport();
		}
		SPEC = spec;
	}

}
