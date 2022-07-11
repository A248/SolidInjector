/*
 * SolidInjector
 * Copyright © 2022 Anand Beh
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

package space.arim.injector.internal.dependency;

import space.arim.injector.MultiBinding;
import space.arim.injector.error.MultiBindingRelatedException;
import space.arim.injector.internal.IdentifierCreation;
import space.arim.injector.internal.reflect.GenericType;
import space.arim.injector.internal.reflect.qualifier.QualifiersInAnnotations;
import space.arim.injector.internal.reflect.qualifier.QualifiersInCombined;
import space.arim.injector.internal.spec.SpecSupport;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

public final class AnnotatedTypeAsDependency {

	private final SpecSupport spec;

	private final GenericType type;
	private final Annotation[] extraAnnotations;

	public AnnotatedTypeAsDependency(SpecSupport spec, GenericType type, Annotation[] extraAnnotations) {
		this.spec = spec;
		this.type = type;
		this.extraAnnotations = extraAnnotations;
	}

	private static void multiBindingRequireSet(Class<?> rawTypeWhichMustBeSet) {
		if (!rawTypeWhichMustBeSet.equals(Set.class)) {
			throw new MultiBindingRelatedException(
					"To use multi-binding, you must inject a Set of requested instances");
		}
	}

	public InstantiableDependency createDependency() {
		Class<?> rawType = type.getRawType();
		if (spec.isAnyProvider(rawType)) {
			Class<?> providerType = rawType;
			GenericType providerArgument = type.getTypeArgument();
			Annotation[] providerArgumentTypeUseAnnotations = providerArgument.getAnnotations();
			if (hasMultiBindingAnnotation()) {
				// Inject a Provider<Set<T>> using multi-binding
				multiBindingRequireSet(providerArgument.getRawType());
				GenericType setArgument = providerArgument.getTypeArgument();
				Annotation[] setArgumentTypeUseAnnotations = setArgument.getAnnotations();
				Class<?> multiBoundType = setArgument.getRawType();
				return new MultiProviderDependency<>(
						new ToSpecProvider<>(spec, providerType),
						new IdentifierCreation<>(multiBoundType,
								new QualifiersInCombined(
										new QualifiersInAnnotations(extraAnnotations),
										new QualifiersInAnnotations(providerArgumentTypeUseAnnotations),
										new QualifiersInAnnotations(setArgumentTypeUseAnnotations))
						).createIdentifier(spec));
			}
			// Inject a Provider<T>
			return new ProviderDependency<>(
					new ToSpecProvider<>(spec, providerType),
					new IdentifierCreation<>(providerArgument,
							new QualifiersInCombined(
									new QualifiersInAnnotations(extraAnnotations),
									new QualifiersInAnnotations(providerArgumentTypeUseAnnotations))
					).createIdentifier(spec));
		}
		if (hasMultiBindingAnnotation()) {
			multiBindingRequireSet(rawType);
			// Inject a Set<T> using multi-binding
			GenericType setArgument = type.getTypeArgument();
			Annotation[] typeUseAnnotations = setArgument.getAnnotations();
			Class<?> multiBoundType = setArgument.getRawType();
			return new MultiInstanceDependency<>(
					new IdentifierCreation<>(multiBoundType,
							new QualifiersInCombined(
									new QualifiersInAnnotations(extraAnnotations),
									new QualifiersInAnnotations(typeUseAnnotations))
					).createIdentifier(spec));
		}
		if (rawType.equals(Optional.class)) {
			GenericType optionalArgument = type.getTypeArgument();
			Annotation[] optionalArgumentTypeUseAnnotations = optionalArgument.getAnnotations();
			if (spec.isAnyProvider(optionalArgument.getRawType())) {
				// Inject a Optional<Provider<T>>
				Class<?> providerType = optionalArgument.getRawType();
				GenericType providerArgument = optionalArgument.getTypeArgument();
				Annotation[] providerArgumentTypeUseAnnotations = providerArgument.getAnnotations();
				return new OptionalProviderDependency<>(
						new ToSpecProvider<>(spec, providerType),
						new IdentifierCreation<>(providerArgument,
								new QualifiersInCombined(
										new QualifiersInAnnotations(extraAnnotations),
										new QualifiersInAnnotations(optionalArgumentTypeUseAnnotations),
										new QualifiersInAnnotations(providerArgumentTypeUseAnnotations))
						).createIdentifier(spec));
			}
			// Inject a Optional<T>
			return new OptionalInstanceDependency<>(
					new IdentifierCreation<>(optionalArgument,
							new QualifiersInCombined(
									new QualifiersInAnnotations(extraAnnotations),
									new QualifiersInAnnotations(optionalArgumentTypeUseAnnotations))
					).createIdentifier(spec));
		}
		// Inject a T
		return new InstanceDependency<>(
				new IdentifierCreation<>(rawType,
						new QualifiersInAnnotations(extraAnnotations)
				).createIdentifier(spec));
	}

	private boolean hasMultiBindingAnnotation() {
		for (Annotation annotation : extraAnnotations) {
			if (annotation.annotationType().equals(MultiBinding.class)) {
				return true;
			}
		}
		return false;
	}

}
