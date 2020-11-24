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

public interface SpecSupport {

	/**
	 * Determines whether {@literal @Inject} is present
	 * 
	 * @param element the annotated element
	 * @return true if the inject annotation is present
	 */
	boolean hasInjectAnnotation(AnnotatedElement element);

	/**
	 * Determines whether {@literal @Singleton} is present
	 * 
	 * @param element the annotated element
	 * @return true if the singleton annotation is present
	 */
	boolean hasSingletonAnnotation(AnnotatedElement element);

	/**
	 * Determines whether the specified class is any kind of {@code Provider.class}.
	 * If the implementation supports multiple provider classes, this may return
	 * {@code true} for distinct classes.
	 * 
	 * @param clazz the class to test
	 * @return true if a provider class
	 */
	boolean isAnyProvider(Class<?> clazz);

	/**
	 * Externalises an internal provider to a {@code Provider}
	 * 
	 * @param <T>              the type of the external provider
	 * @param provider         the internal contextless provider
	 * @param extProviderClass the external provider class
	 * @return the external provider
	 */
	<T> T externalize(ContextlessProvider<?> provider, Class<T> extProviderClass);

	/**
	 * Determines whether the specified annotation is a {@literal Qualifier}
	 * 
	 * @param annotation the annotation class
	 * @return true if the annotation is a qualifier annotation, false otherwise
	 */
	boolean isQualifier(Class<? extends Annotation> annotation);

	/**
	 * If the specified annotation is a {@literal Named} qualifier, gets its value.
	 * 
	 * @param annotation the annotation
	 * @return the value of the {@literal Named} annotation or {@code null} if not a
	 *         named qualifier
	 */
	String getNamedQualifier(Annotation annotation);

}
