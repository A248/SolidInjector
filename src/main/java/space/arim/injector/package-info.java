/**
 * The injector API. <br>
 * <br>
 * {@link Injector} is used to retrieve instances via dependency injection. {@link InjectorBuilder}
 * allows creating an {@code Injector} with all available options. <br>
 * <br>
 * <b>Nulls</b> <br>
 * Unless otherwise stated, {@code null} is not permitted as an input. {@code NullPointerException}
 * will be thrown otherwise. <br>
 * <br>
 * <b>Thread Safety</b> <br>
 * Objects are thread safe unless stated otherwise. Builder objects are usually not thread safe.
 * 
 */
package space.arim.injector;