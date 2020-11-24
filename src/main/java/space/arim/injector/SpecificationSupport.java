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
package space.arim.injector;

import java.util.function.Supplier;

import space.arim.injector.internal.spec.CombinedSpecSupport;
import space.arim.injector.internal.spec.JakartaSupport;
import space.arim.injector.internal.spec.JavaxSupport;
import space.arim.injector.internal.spec.SpecDetector;
import space.arim.injector.internal.spec.SpecSupport;

/**
 * Determines how the injector should accomodate compatibility with
 * {@code javax.inject} and {@code jakarta.inject}
 * 
 * @author A248
 *
 */
public enum SpecificationSupport {

	/**
	 * Uses the {@code javax.inject} specification
	 * 
	 */
	JAVAX(() -> new JavaxSupport()),
	/**
	 * Uses the {@code jakarta.inject} specification
	 * 
	 */
	JAKARTA(() -> new JakartaSupport()),
	/**
	 * Combined support for {@code javax.inject} and {@code jakarta.inject}. <br>
	 * <br>
	 * In this mode, equivalent annotations from different specifications are
	 * considered equal. For example, a {@code Named} annotation will be considered
	 * equal to a {@code Named} annotation made in the other spec with the same
	 * value.
	 * 
	 */
	COMBINED(() -> new CombinedSpecSupport()),
	/**
	 * Automatically detect whether {@code javax.inject} or {@code jakarta.inject}
	 * is present. <br>
	 * <br>
	 * If both are present, {@link COMBINED} is used
	 * 
	 */
	AUTO_DETECT(() -> SpecDetector.detectedSpec());

	/**
	 * Supplier of corresponding support instance. <br>
	 * <br>
	 * Note that method references are not used so as to avoid classloading.
	 */
	private final Supplier<SpecSupport> toInternal;

	private SpecificationSupport(Supplier<SpecSupport> toInternal) {
		this.toInternal = toInternal;
	}

	SpecSupport toInternal() {
		return toInternal.get();
	}

}
