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
package space.arim.injector.internal.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import space.arim.injector.error.MisannotatedInjecteeException;
import space.arim.injector.internal.IdentifierInternal;
import space.arim.injector.internal.InjectionSettings;

public class ConstructorScan<U> {

	private final InjectionSettings settings;
	private final IdentifierInternal<U> identifier;

	public ConstructorScan(InjectionSettings settings, IdentifierInternal<U> identifier) {
		this.settings = settings;
		this.identifier = identifier;
	}

	public Constructor<U> findInjectableConstructor() {
		/*
		 * Requirements:
		 * 1. Detect duplicate @Inject constructors
		 * 2. Accept default public constructor only when no other constructors are present
		 */
		Constructor<U> injectAnnotatedConstructor = null;
		Constructor<U> defaultConstructor = null;
		boolean foundNonDefault = false;

		@SuppressWarnings("unchecked")
		Constructor<U>[] declaredConstructors = (Constructor<U>[]) identifier.getType().getDeclaredConstructors();
		for (Constructor<U> constructor : declaredConstructors) {

			boolean isPublic = Modifier.isPublic(constructor.getModifiers());
			if (!settings.privateInjection() && !isPublic) {
				continue;
			}
			if (settings.spec().hasInjectAnnotation(constructor)) {
				if (injectAnnotatedConstructor != null) {
					throw new MisannotatedInjecteeException(
							"Multiple constructors with @Inject present on " + identifier.getType().getName());
				}
				injectAnnotatedConstructor = constructor;
			}
			if (isPublic && constructor.getParameterCount() == 0) {
				defaultConstructor = constructor;
			} else {
				foundNonDefault = true;
			}
		}
		if (injectAnnotatedConstructor != null) {
			return injectAnnotatedConstructor;
		}
		if (!foundNonDefault && defaultConstructor != null) {
			return defaultConstructor;
		}
		throw new MisannotatedInjecteeException(
				"No injectable constructors found for concrete type " + identifier.getType().getName());
	}

}
