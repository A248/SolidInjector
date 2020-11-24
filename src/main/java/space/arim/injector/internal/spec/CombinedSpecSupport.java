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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import space.arim.injector.internal.provider.ContextlessProvider;

public class CombinedSpecSupport implements SpecSupport {

	private final JakartaSupport jakarta = new JakartaSupport();
	private final JavaxSupport javax = new JavaxSupport();

	@Override
	public boolean hasInjectAnnotation(AnnotatedElement element) {
		return jakarta.hasInjectAnnotation(element) || javax.hasInjectAnnotation(element);
	}

	@Override
	public boolean hasSingletonAnnotation(AnnotatedElement element) {
		return jakarta.hasSingletonAnnotation(element) || javax.hasSingletonAnnotation(element);
	}

	@Override
	public boolean isAnyProvider(Class<?> clazz) {
		return jakarta.isAnyProvider(clazz) || javax.isAnyProvider(clazz);
	}

	@Override
	public boolean isQualifier(Class<? extends Annotation> annotation) {
		return jakarta.isQualifier(annotation) || javax.isQualifier(annotation);
	}

	@Override
	public String getNamedQualifier(Annotation annotation) {
		String jakartaName = jakarta.getNamedQualifier(annotation);
		return (jakartaName != null) ? jakartaName : javax.getNamedQualifier(annotation);
	}

	@Override
	public <T> T externalize(ContextlessProvider<?> provider, Class<T> extProviderClass) {
		return (extProviderClass.equals(jakarta.inject.Provider.class)) ?
				jakarta.externalize(provider, extProviderClass) : javax.externalize(provider, extProviderClass);
	}

}
