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
package space.arim.injector;

import java.lang.annotation.Annotation;
import java.util.Objects;

import space.arim.injector.internal.IdentifierInternal;

/**
 * An identifier for a dependency. Includes optional qualifier.
 * 
 * @author A248
 *
 * @param <T> the type
 */
public final class Identifier<T> {

	private final Class<T> type;
	private final Qualifier qualifier;

	private Identifier(Class<T> type, Qualifier qualifier) {
		this.type = Objects.requireNonNull(type, "type");
		this.qualifier = qualifier;
	}

	/**
	 * Creates an identifier from a simple type
	 * 
	 * @param <T> the type
	 * @param type the type class
	 * @return the identifier
	 */
	public static <T> Identifier<T> ofType(Class<T> type) {
		return new Identifier<>(type, null);
	}

	/**
	 * Creates an identifier from a type and qualifier. <br>
	 * <br>
	 * Thsi identifier will match types annoted with the same qualifier annotation. <br>
	 * <br>
	 * The qualifier annotation should be itself annotated with {@code Qualifier}. However,
	 * this requirement is not enforced by preconditions.
	 * 
	 * @param <T> the type
	 * @param type the type class
	 * @param qualifier the qualifier annotation
	 * @return the identifier
	 */
	public static <T> Identifier<T> ofTypeAndQualifier(Class<T> type, Class<? extends Annotation> qualifier) {
		return new Identifier<>(type, new UserQualifier(qualifier));
	}

	/**
	 * Creates an identifier from a type and named qualifier. <br>
	 * <br>
	 * This identifier will match types annotated with {@code Named} where the value of such
	 * annotation matches the given name.
	 * 
	 * @param <T> the type
	 * @param type the type class
	 * @param name the name, i.e. the value of the {@code Named} annotation
	 * @return the identifier
	 */
	public static <T> Identifier<T> ofTypeAndNamed(Class<T> type, String name) {
		return new Identifier<>(type, new NamedQualifier(name));
	}

	IdentifierInternal<T> toInternal() {
		if (qualifier == null) {
			return IdentifierInternal.ofType(type);
		}
		if (qualifier instanceof UserQualifier) {
			return IdentifierInternal.ofTypeAndQualifier(type, ((UserQualifier) qualifier).annotation);
		} else {
			return IdentifierInternal.ofTypeAndNamed(type, ((NamedQualifier) qualifier).name);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + type.hashCode();
		result = prime * result + Objects.hashCode(qualifier);
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Identifier)) {
			return false;
		}
		Identifier<?> other = (Identifier<?>) object;
		return type.equals(other.type) && Objects.equals(qualifier, other.qualifier);
	}

	@Override
	public String toString() {
		return "Identifier [type=" + type + ", qualifier=" + qualifier + "]";
	}

	private interface Qualifier {}

	private static class UserQualifier implements Qualifier {

		final Class<? extends Annotation> annotation;

		UserQualifier(Class<? extends Annotation> annotation) {
			this.annotation = Objects.requireNonNull(annotation, "annotation");
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + annotation.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object object) {
			return this == object
					|| object instanceof UserQualifier && annotation.equals(((UserQualifier) object).annotation);
		}

		@Override
		public String toString() {
			return "Identifier.UserQualifier [annotation=" + annotation + "]";
		}
	}

	private static class NamedQualifier implements Qualifier {

		final String name;

		NamedQualifier(String name) {
			this.name = Objects.requireNonNull(name, "name");
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + name.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object object) {
			return this == object || object instanceof NamedQualifier && name.equals(((NamedQualifier) object).name);
		}

		@Override
		public String toString() {
			return "Identifier.NamedQualifier [name=" + name + "]";
		}
	}

}
