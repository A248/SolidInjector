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
 * Thrown when a reflective invocation by the injector to user defined code somehow failed. <br>
 * <ul>
 * <li> An injection method or constructor threw an exception
 * <li> A binding method threw an exception or returned {@code null}
 * </ul>
 * 
 * @author A248
 *
 */
public class InjectionInvocationException extends InjectorException {

	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = 7263818844332292242L;

	/**
	 * Creates the exception
	 * 
	 */
	public InjectionInvocationException() {
		
	}

	/**
	 * Creates the exception with the specified message
	 * 
	 * @param message the message
	 */
	public InjectionInvocationException(String message) {
		super(message);
	}

	/**
	 * Creates the exception with the specified cause
	 * 
	 * @param cause the cause, cannot be null
	 */
	public InjectionInvocationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates the exception with the specified message and cause
	 * 
	 * @param message the message
	 * @param cause the cause, cannot be null
	 */
	public InjectionInvocationException(String message, Throwable cause) {
		super(message, cause);
	}

}
