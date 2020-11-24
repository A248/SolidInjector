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

abstract class SingleSpecSupport<P> implements SpecSupport {

	abstract Class<? extends Annotation> getInjectAnnotationClass();

	abstract Class<? extends Annotation> getSingletonAnnotationClass();

	@Override
	public final boolean hasInjectAnnotation(AnnotatedElement element) {
		return element.getAnnotation(getInjectAnnotationClass()) != null;
	}

	@Override
	public final boolean hasSingletonAnnotation(AnnotatedElement element) {
		return element.getAnnotation(getSingletonAnnotationClass()) != null;
	}

	abstract Class<?> getUncheckedProviderClass();

	@Override
	public final boolean isAnyProvider(Class<?> clazz) {
		return clazz.equals(getUncheckedProviderClass());
	}

	abstract P externalize0(ContextlessProvider<?> provider);

	@Override
	public final <T> T externalize(ContextlessProvider<?> provider, Class<T> extProviderClass) {
		if (!extProviderClass.equals(getUncheckedProviderClass())) {
			throw new IllegalArgumentException("Provider type " + extProviderClass + " not recognised");
		}
		@SuppressWarnings("unchecked")
		T casted = (T) externalize0(provider);
		return casted;
	}

	abstract Class<? extends Annotation> getQualifierClass();

	@Override
	public final boolean isQualifier(Class<? extends Annotation> annotation) {
		return annotation.isAnnotationPresent(getQualifierClass());
	}

}
