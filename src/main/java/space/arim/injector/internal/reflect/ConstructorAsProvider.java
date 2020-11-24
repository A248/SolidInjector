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
import java.util.List;

import space.arim.injector.internal.InjectionSettings;
import space.arim.injector.internal.provider.ContextualProvider;
import space.arim.injector.internal.provider.SingletonContextualProvider;

public class ConstructorAsProvider<U> {

	private final InjectionSettings settings;
	private final Constructor<U> constructor;

	public ConstructorAsProvider(InjectionSettings settings, Constructor<U> constructor) {
		this.settings = settings;
		this.constructor = constructor;
	}

	public ContextualProvider<U> createProvider() {
		ContextualProvider<U> constructorProvider = injectFieldsAndMethodsProvider(createConstructorProvider());

		if (settings.spec().hasSingletonAnnotation(constructor.getDeclaringClass())) {
			return new SingletonContextualProvider<>(constructorProvider);
		}
		return constructorProvider;
	}

	private ContextualProvider<U> injectFieldsAndMethodsProvider(ContextualProvider<U> delegateProvider) {
		InjectableMemberScan scan = new InjectableMemberScan(constructor.getDeclaringClass(), settings);
		List<PostConstructorInjection> injections = scan.scanInjections();
		if (injections.isEmpty()) {
			return delegateProvider;
		}
		PostConstructorInjection[] injectionsArray = injections.toArray(new PostConstructorInjection[] {});
		return new PostConstructorInjectionContextualProvider<>(delegateProvider, injectionsArray);
	}

	private ContextualProvider<U> createConstructorProvider() {
		return new ConstructorContextualProvider<>(constructor,
				new ExecutableDependencies(settings.spec(), constructor).collectDependencies());
	}
	
}
