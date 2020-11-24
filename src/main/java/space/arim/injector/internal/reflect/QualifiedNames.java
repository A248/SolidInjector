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
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public final class QualifiedNames {

	private QualifiedNames() {
	}

	public static String forMethod(Method method) {
		return method.getDeclaringClass().getName() + "#" + method.getName();
	}

	static String forConstructor(Constructor<?> constructor) {
		Class<?> declaringClass = constructor.getDeclaringClass();
		return declaringClass.getName() + "#" + declaringClass.getSimpleName();
	}

	public static String forExecutable(Executable executable) {
		if (executable instanceof Method) {
			return forMethod((Method) executable);
		}
		if (executable instanceof Constructor) {
			return forConstructor((Constructor<?>) executable);
		}
		return "Executable " + executable.getName();
	}

	static String forParameter(Executable executable, int index) {
		return forExecutable(executable) + " parameter " + index;
	}

	public static String forMember(Member member) {
		if (member instanceof Executable) {
			return forExecutable((Executable) member);
		}
		if (member instanceof Field) {
			return forField((Field) member);
		}
		if (member instanceof Constructor) {
			return forConstructor((Constructor<?>) member);
		}
		return member.toString();
	}

	public static String forField(Field field) {
		return field.getDeclaringClass().getName() + "#" + field.getName();
	}

}
