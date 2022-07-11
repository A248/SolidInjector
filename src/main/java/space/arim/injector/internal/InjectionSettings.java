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

import space.arim.injector.internal.spec.SpecSupport;

public final class InjectionSettings {

	private final SpecSupport spec;
	private final boolean privateInjection;
	private final boolean staticInjection;
	private final boolean optionalBindings;

	public InjectionSettings(SpecSupport spec,
							 boolean privateInjection, boolean staticInjection, boolean optionalBindings) {
		this.spec = spec;
		this.privateInjection = privateInjection;
		this.staticInjection = staticInjection;
		this.optionalBindings = optionalBindings;
	}

	public InjectionSettings(SpecSupport spec) {
		this(spec, false, false, false);
	}

	public SpecSupport spec() {
		return spec;
	}

	public boolean privateInjection() {
		return privateInjection;
	}

	public boolean staticInjection() {
		return staticInjection;
	}

	public boolean optionalBindings() {
		return optionalBindings;
	}

	@Override
	public String toString() {
		return "InjectionSettings [spec=" + spec + ", privateInjection=" + privateInjection + ", staticInjection="
				+ staticInjection + ", optionalBindings=" + optionalBindings + "]";
	}

}
