/*
 * SolidInjector
 * Copyright © 2023 Anand Beh
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

import space.arim.injector.Identifier;
import space.arim.injector.MultiBinding;
import space.arim.injector.error.InjectorException;
import space.arim.injector.error.MisconfiguredBindingsException;
import space.arim.injector.internal.provider.ContextualProvider;
import space.arim.injector.internal.provider.FixedContextualProvider;
import space.arim.injector.internal.provider.MultiBindableContextualProvider;
import space.arim.injector.internal.provider.NullCheckedContextualProvider;
import space.arim.injector.internal.provider.IdentifierContextualProvider;
import space.arim.injector.internal.provider.ProviderMap;
import space.arim.injector.internal.provider.SingletonContextualProvider;
import space.arim.injector.internal.reflect.ExecutableDependencies;
import space.arim.injector.internal.reflect.MethodContextualProvider;
import space.arim.injector.internal.reflect.QualifiedNames;
import space.arim.injector.internal.reflect.qualifier.QualifiersInAnnotations;
import space.arim.injector.internal.spec.SpecSupport;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public final class InjectorConfiguration {

	private final SpecSupport spec;
	private final Collection<Object> bindModules;
	private final ProviderMap providerMap;

	public InjectorConfiguration(SpecSupport spec, Collection<Object> bindModules, ProviderMap providerMap) {
		this.spec = spec;
		this.bindModules = bindModules;
		this.providerMap = providerMap;
	}

	public ProviderMap configure() {
		return configure(Collections.emptyMap(), Collections.emptyMap());
	}

	public ProviderMap configure(Map<Identifier<?>, Identifier<?>> boundImplementors,
								 Map<Identifier<?>, Object> boundInstances) {

		for (Object bindModule : bindModules) {
			Objects.requireNonNull(bindModule, "bind module");
			addBindModule(bindModule);
		}
		boundImplementors.forEach((identifier, boundImplementor) -> {
			String failureReason = providerMap.installProvider(identifier, new IdentifierContextualProvider<>(boundImplementor));
			if (failureReason != null) {
				throw failedToBind("implementor " + boundImplementor, failureReason);
			}
		});
		boundInstances.forEach((identifier, boundInstance) -> {
			String failureReason = providerMap.installProvider(identifier, new FixedContextualProvider<>(boundInstance));
			if (failureReason != null) {
				throw failedToBind("instance " + boundInstance, failureReason);
			}
		});
		return providerMap.makeConcurrent();
	}

	private MisconfiguredBindingsException failedToBind(String binding, String reason) {
		return new MisconfiguredBindingsException(
				"Failed to bind " + binding + ". " + reason);
	}

	private static boolean isBindMethod(Method method) {
		if (method.getDeclaringClass().equals(Object.class) || Modifier.isStatic(method.getModifiers())) {
			return false;
		}
		try {
			// Make sure this method is not overridden from Object (e.g. equals or hashCode)
			Object.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
			return false;
		} catch (NoSuchMethodException ignored) {}
		return true;
	}

	private void addBindModule(Object bindModule) {
		for (Method method : bindModule.getClass().getMethods()) {
			if (isBindMethod(method)) {
				ContextualProvider<?> provider = createMethodProvider(bindModule, method);
				Identifier<?> identifier = createIdentifier(method);
				String failureReason = providerMap.installProvider(identifier, provider);
				if (failureReason != null) {
					throw failedToBind("method " + QualifiedNames.forMethod(method), failureReason);
				}
			}
		}
	}

	private Identifier<?> createIdentifier(Method method) {
		try {
			return new IdentifierCreation<>(method.getReturnType(),
					new QualifiersInAnnotations(method.getAnnotations())).createIdentifier(spec);
		} catch (InjectorException ex) {
			throw new ExceptionContext().rethrow(ex, "On method " + QualifiedNames.forMethod(method));
		}
	}

	private ContextualProvider<?> createMethodProvider(Object bindModule, Method method) {
		ContextualProvider<?> provider =
				new NullCheckedContextualProvider<>(
						new MethodContextualProvider<>(
								bindModule, method,
								new ExecutableDependencies(spec, method).collectDependencies()
						));
		if (spec.hasSingletonAnnotation(method)) {
			provider = new SingletonContextualProvider<>(provider);
		}
		if (providerMap.permitsMultiBindings() && method.isAnnotationPresent(MultiBinding.class)) {
			provider = new MultiBindableContextualProvider<>(provider);
		}
		return provider;
	}

}
