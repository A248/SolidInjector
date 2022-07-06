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

package space.arim.injector;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Enables use of the multibinding feature. This annotation has two usages. <br>
 * <br>
 * <b>1. On bind methods</b> <br>
 * Identifies a method as providing a binding of an interface, allowing for multiplicity.
 * That is, the same interface may be bound multiple times using this annotation. E.g.: <br>
 * <pre>
 * {@code
 * public class BindModule1 {
 *     @MultiBinding
 *     public MyService provider1(MyServiceImpl1 impl1) {
 *         return impl1;
 *     }
 * }
 *
 * public class BindModule2 {
 *     @MultiBinding
 *     public MyService provider2(MyServiceIimpl2 impl2) {
 *         return impl2;
 *     }
 * }
 * }
 * </pre>
 * <br>
 * <b>2. At injection points</b> <br>
 * On constructor parameters, injectable method parameters, and injectable fields.
 * Since multibinding is outside of the formal injection specification, to enforce
 * good practice it is required to annotate all requests of multiply binded interfaces
 * to use this annotation. E.g.: <br>
 * <pre>
 * {@code
 * public class MyDependent {
 *     private final Set<MyService> myServices;
 *
 *     public MyDependent(@MultiBinding Set<MyService> myServices) {
 *         this.myServices = myServices;
 *     }
 * }
 * }
 * </pre>
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER})
public @interface MultiBinding {
}
