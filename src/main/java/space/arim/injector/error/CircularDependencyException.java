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
package space.arim.injector.error;

/**
 * Thrown when a circular dependency is detected
 * 
 * @author A248
 *
 */
public class CircularDependencyException extends InjectorException {

	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = 341896330358452922L;

	/**
	 * Creates the exception
	 * 
	 */
	public CircularDependencyException() {

	}

	/**
	 * Creates the exception with the specified message
	 * 
	 * @param message the message
	 */
	public CircularDependencyException(String message) {
		super(message);
	}

	/**
	 * Creates the exception with the specified cause
	 * 
	 * @param cause the cause
	 */
	public CircularDependencyException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates the exception with the specified message and cause
	 * 
	 * @param message the message
	 * @param cause   the cause
	 */
	public CircularDependencyException(String message, Throwable cause) {
		super(message, cause);
	}
}
