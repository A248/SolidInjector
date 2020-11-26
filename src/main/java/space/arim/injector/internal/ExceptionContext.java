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
package space.arim.injector.internal;

import java.lang.reflect.InvocationTargetException;

import space.arim.injector.error.InjectorException;

public class ExceptionContext {

	public <T extends InjectorException> InjectorException rethrow(T original, String addedContext) {
		Class<? extends InjectorException> clazz = original.getClass();
		try {
			return clazz.getDeclaredConstructor(String.class, Throwable.class)
					.newInstance("Additional context: " + addedContext, original);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException ex) {
			original.addSuppressed(ex);
			return original;
		}
	}

}
