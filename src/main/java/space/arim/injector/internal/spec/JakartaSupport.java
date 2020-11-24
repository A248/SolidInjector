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

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Qualifier;
import jakarta.inject.Singleton;

import space.arim.injector.internal.provider.ContextlessProvider;

import jakarta.inject.Provider;

public class JakartaSupport extends SingleSpecSupport<Provider<?>> {

	@Override
	Class<? extends Annotation> getInjectAnnotationClass() {
		return Inject.class;
	}

	@Override
	Class<? extends Annotation> getSingletonAnnotationClass() {
		return Singleton.class;
	}

	@Override
	Class<?> getUncheckedProviderClass() {
		return Provider.class;
	}

	@Override
	Class<? extends Annotation> getQualifierClass() {
		return Qualifier.class;
	}

	@Override
	public String getNamedQualifier(Annotation annotation) {
		return (annotation instanceof Named) ? ((Named) annotation).value() : null;
	}

	@Override
	Provider<?> externalize0(ContextlessProvider<?> provider) {
		return provider::provide;
	}

}
