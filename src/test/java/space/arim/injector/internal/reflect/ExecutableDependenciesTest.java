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

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.inject.Qualifier;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import space.arim.injector.Identifier;
import space.arim.injector.example.Plane;
import space.arim.injector.example.TrafficControl;
import space.arim.injector.example.Wing;
import space.arim.injector.internal.dependency.InstantiableDependency;
import space.arim.injector.internal.dependency.InstantiableDependencyBunch;
import space.arim.injector.internal.dependency.InstantiableInstanceDependency;
import space.arim.injector.internal.dependency.InstantiableProviderDependency;
import space.arim.injector.internal.spec.JakartaSpecSupportProvider;
import space.arim.injector.internal.spec.SpecSupport;

public class ExecutableDependenciesTest {

	/*
	 * This test set does not use @EnumSource. If it did, qualifiers
	 * would need to be specified in jakarta.inject and javax.inject.
	 * However, that would lead to duplicate qualifier exceptions.
	 */

	private Method getMethod(String methodName) throws NoSuchMethodException {
		for (Method method : getClass().getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		throw new NoSuchMethodException();
	}

	private InstantiableDependencyBunch fromMethod(SpecSupport spec, String methodName)
			throws NoSuchMethodException, SecurityException {
		return new ExecutableDependencies(spec, getMethod(methodName)).collectDependencies();
	}

	// No dependencies

	@SuppressWarnings("unused")
	private void methodNoDependencies() {}

	@ParameterizedTest
	@ArgumentsSource(JakartaSpecSupportProvider.class)
	public void testMethodNoDependencies(SpecSupport spec) throws NoSuchMethodException, SecurityException {
		assertEquals(
				new InstantiableDependencyBunch(new InstantiableDependency[] {}),
				fromMethod(spec, "methodNoDependencies"));
	}

	// Unqualified dependencies

	@SuppressWarnings("unused")
	private void methodUnqualifiedDependencies(Plane plane, Wing wing) {}

	@ParameterizedTest
	@ArgumentsSource(JakartaSpecSupportProvider.class)
	public void testMethodUnqualifiedDependencies(SpecSupport spec) throws NoSuchMethodException, SecurityException {
		assertEquals(
				new InstantiableDependencyBunch(new InstantiableDependency[] {
						new InstantiableInstanceDependency<>(Identifier.ofType(Plane.class)),
						new InstantiableInstanceDependency<>(Identifier.ofType(Wing.class))
				}),
				fromMethod(spec, "methodUnqualifiedDependencies"));
	}

	// Qualified instance dependencies

	@Qualifier
	@Retention(RUNTIME)
	@Target({PARAMETER, TYPE_USE})
	private @interface BusyAirport {}

	@SuppressWarnings("unused")
	private void methodQualifiedDependencies(Plane pane, Wing wing, @Named("tail") Wing tail,
			@BusyAirport TrafficControl trafficControl) {}

	@ParameterizedTest
	@ArgumentsSource(JakartaSpecSupportProvider.class)
	public void testMethodQualifiedDependencies(SpecSupport spec) throws NoSuchMethodException, SecurityException {
		assertEquals(
				new InstantiableDependencyBunch(new InstantiableDependency[] {
						new InstantiableInstanceDependency<>(Identifier.ofType(Plane.class)),
						new InstantiableInstanceDependency<>(Identifier.ofType(Wing.class)),
						new InstantiableInstanceDependency<>(Identifier.ofTypeAndNamed(Wing.class, "tail")),
						new InstantiableInstanceDependency<>(
								Identifier.ofTypeAndQualifier(TrafficControl.class, BusyAirport.class))
				}),
				fromMethod(spec, "methodQualifiedDependencies"));
	}

	// Qualified instance and provider dependencies

	@SuppressWarnings("unused")
	private void methodQualifiedProviderDependencies(Plane pane, Provider<Wing> wing,
			@Named("tail") Wing tail, @BusyAirport Provider<TrafficControl> trafficControl) {}

	@ParameterizedTest
	@ArgumentsSource(JakartaSpecSupportProvider.class)
	public void testMethodQualifiedProviderDependencies(SpecSupport spec) throws NoSuchMethodException, SecurityException {
		assertEquals(
				new InstantiableDependencyBunch(new InstantiableDependency[] {
						new InstantiableInstanceDependency<>(Identifier.ofType(Plane.class)),
						new InstantiableProviderDependency<>(spec, Provider.class, Identifier.ofType(Wing.class)),
						new InstantiableInstanceDependency<>(Identifier.ofTypeAndNamed(Wing.class, "tail")),
						new InstantiableProviderDependency<>(
								spec, Provider.class,
								Identifier.ofTypeAndQualifier(TrafficControl.class, BusyAirport.class))
				}),
				fromMethod(spec, "methodQualifiedProviderDependencies"));
	}

	// Qualified provider using TYPE_USE

	@SuppressWarnings("unused")
	private void methodQualifiedProviderWithTypeUseDependency(Provider<@BusyAirport TrafficControl> trafficControl) {}

	@ParameterizedTest
	@ArgumentsSource(JakartaSpecSupportProvider.class)
	public void testMethodQualifiedProviderWithTypeUseDependency(SpecSupport spec)
			throws NoSuchMethodException, SecurityException {
		assertEquals(
				new InstantiableDependencyBunch(new InstantiableDependency[] {
						new InstantiableProviderDependency<>(spec, Provider.class,
								Identifier.ofTypeAndQualifier(TrafficControl.class, BusyAirport.class))
				}),
				fromMethod(spec, "methodQualifiedProviderWithTypeUseDependency"));
	}

}
