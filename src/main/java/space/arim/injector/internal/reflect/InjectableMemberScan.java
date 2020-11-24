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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import space.arim.injector.error.MisannotatedInjecteeException;
import space.arim.injector.internal.InjectionSettings;
import space.arim.injector.internal.dependency.AnnotatedTypeAsDependency;

class InjectableMemberScan {

	private final Class<?> subject;
	private final InjectionSettings settings;

	InjectableMemberScan(Class<?> subject, InjectionSettings settings) {
		this.subject = subject;
		this.settings = settings;
	}

	private <M extends AccessibleObject & Member> boolean filter(M member) {
		int modifiers = member.getModifiers();
		if (!settings.privateInjection() && !Modifier.isPublic(modifiers)) {
			return false;
		}
		if (!settings.staticInjection() && Modifier.isStatic(modifiers)) {
			return false;
		}
		if (Modifier.isAbstract(modifiers)) {
			// Skip abstract methods
			return false;
		}
		if (!settings.spec().hasInjectAnnotation(member)) {
			return false;
		}
		member.setAccessible(true); // Check and/or set visibility
		return true;
	}

	final List<PostConstructorInjection> scanInjections() {
		Set<DistinctMethod> distinctInstanceMethods = new HashSet<>();
		List<PostConstructorInjection> injections = new ArrayList<>();
		Class<?> clazz = subject;
		while (!clazz.equals(Object.class)) {

			for (Method method : clazz.getDeclaredMethods()) {
				DistinctMethod distinctMethod = DistinctMethod.of(method);
				if (distinctMethod != null && !distinctInstanceMethods.add(distinctMethod)) {
					// Method overridden by subclass
					continue;
				}
				if (!filter(method)) {
					continue;
				}
				injections.add(wrapStaticInjections(method, injectionFor(method)));
			}
			for (Field field : clazz.getDeclaredFields()) {
				if (!filter(field)) {
					continue;
				}
				if (Modifier.isFinal(field.getModifiers())) {
					throw new MisannotatedInjecteeException("Cannot inject into final field " + QualifiedNames.forField(field));
				}
				injections.add(wrapStaticInjections(field, injectionFor(field)));
			}

			clazz = clazz.getSuperclass();
		}
		// Reverse because specification requires fields and methods are injected in superclasses first
		Collections.reverse(injections);
		return injections;
	}

	private PostConstructorInjection wrapStaticInjections(Member member, PostConstructorInjection delegate) {
		if (Modifier.isStatic(member.getModifiers())) {
			return new StaticMemberInjection(member, delegate);
		}
		return delegate;
	}

	private PostConstructorInjection injectionFor(Method method) {
		return new MethodInjection(method,
				new ExecutableDependencies(settings.spec(), method).collectDependencies());
	}

	private PostConstructorInjection injectionFor(Field field) {
		return new FieldInjection(field,
				new AnnotatedTypeAsDependency(settings.spec(), field.getType(),
				field.getGenericType(), field.getAnnotations()).createDependency());
	}

}
