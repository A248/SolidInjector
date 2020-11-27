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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import space.arim.injector.Identifier;
import space.arim.injector.error.InjectorException;
import space.arim.injector.error.MisconfiguredBindingsException;
import space.arim.injector.internal.provider.ContextualProvider;
import space.arim.injector.internal.provider.FixedContextualProvider;
import space.arim.injector.internal.provider.NullCheckedContextualProvider;
import space.arim.injector.internal.provider.SingletonContextualProvider;
import space.arim.injector.internal.reflect.ExecutableDependencies;
import space.arim.injector.internal.reflect.MethodContextualProvider;
import space.arim.injector.internal.reflect.QualifiedNames;
import space.arim.injector.internal.spec.SpecSupport;

public class InjectorConfiguration {

	private final SpecSupport spec;
	private final Collection<Object> bindModules;

	public InjectorConfiguration(SpecSupport spec, Collection<Object> bindModules) {
		this.spec = spec;
		this.bindModules = bindModules;
	}

	public Map<Identifier<?>, ContextualProvider<?>> configure() {
		Map<Identifier<?>, ContextualProvider<?>> providers = new HashMap<>();
		for (Object bindModule : bindModules) {
			Objects.requireNonNull(bindModule, "bind module");
			addBindModule(providers, bindModule);
		}
		return providers;
	}

	public Map<Identifier<?>, ContextualProvider<?>> configure(Map<Identifier<?>, Object> boundInstances) {
		Map<Identifier<?>, ContextualProvider<?>> providers = configure();

		boundInstances.forEach((identifier, boundInstance) -> {
			ContextualProvider<?> previous = providers.put(identifier, new FixedContextualProvider<>(boundInstance));
			if (previous != null) {
				throw duplicateBinding(identifier, "instance " + boundInstance);
			}
		});
		return providers;
	}

	private MisconfiguredBindingsException duplicateBinding(Identifier<?> identifier, String binding) {
		return new MisconfiguredBindingsException(
				"Failed to bind " + binding + ". Duplicate binding exists for identifier " + identifier);
	}

	private void addBindModule(Map<Identifier<?>, ContextualProvider<?>> providers, Object bindModule) {
		for (Method method : bindModule.getClass().getMethods()) {
			if (method.getDeclaringClass().equals(Object.class) || Modifier.isStatic(method.getModifiers())) {
				continue;
			}
			ContextualProvider<?> provider = createPossiblySingletonMethodProvider(bindModule, method);
			Identifier<?> identifier = createIdentifier(method);
			ContextualProvider<?> previous = providers.put(identifier, provider);
			if (previous != null) {
				throw duplicateBinding(identifier, "method " + QualifiedNames.forMethod(method));
			}
		}
	}

	private Identifier<?> createIdentifier(Method method) {
		IdentifierCreation<?> idCreation = new IdentifierCreation<>(spec, method.getReturnType(), method.getAnnotations());
		try {
			return idCreation.createIdentifier();
		} catch (InjectorException ex) {
			throw new ExceptionContext().rethrow(ex, "On method " + QualifiedNames.forMethod(method));
		}
	}

	private ContextualProvider<?> createPossiblySingletonMethodProvider(Object bindModule, Method method) {
		ContextualProvider<?> provider = createMethodProvider(bindModule, method);
		if (spec.hasSingletonAnnotation(method)) {
			return new SingletonContextualProvider<>(provider);
		}
		return provider;
	}

	private ContextualProvider<?> createMethodProvider(Object bindModule, Method method) {
		return new NullCheckedContextualProvider<>(
				new MethodContextualProvider<>(bindModule, method,
				new ExecutableDependencies(spec, method).collectDependencies()));
	}

}
