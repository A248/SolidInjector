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

import java.lang.annotation.Annotation;
import java.util.Objects;

public final class IdentifierInternal<T> {

	private final Class<T> type;
	private final Qualifier qualifier;

	private IdentifierInternal(Class<T> type, Qualifier qualifier) {
		this.type = Objects.requireNonNull(type, "type");
		this.qualifier = qualifier;
	}

	public Class<T> getType() {
		return type;
	}

	public static <T> IdentifierInternal<T> ofType(Class<T> type) {
		return new IdentifierInternal<>(type, null);
	}

	public static <T> IdentifierInternal<T> ofTypeAndQualifier(Class<T> type, Class<? extends Annotation> qualifier) {
		return new IdentifierInternal<>(type, new UserQualifier(qualifier));
	}

	static <T> IdentifierInternal<T> ofTypeAndQualifier(Class<T> type, Annotation qualifier) {
		return ofTypeAndQualifier(type, qualifier.annotationType());
	}

	public static <T> IdentifierInternal<T> ofTypeAndNamed(Class<T> type, String name) {
		return new IdentifierInternal<>(type, new NamedQualifier(name));
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
		if (!(object instanceof IdentifierInternal)) {
			return false;
		}
		IdentifierInternal<?> other = (IdentifierInternal<?>) object;
		return type.equals(other.type) && Objects.equals(qualifier, other.qualifier);
	}

	@Override
	public String toString() {
		return "IdentifierInternal [type=" + type + ", qualifier=" + qualifier + "]";
	}

	private interface Qualifier {}

	private static class UserQualifier implements Qualifier {

		private final Class<? extends Annotation> annotation;

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
			return "IdentifierInternal.UserQualifier [annotation=" + annotation + "]";
		}
	}

	private static class NamedQualifier implements Qualifier {

		private final String name;

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
			return "IdentifierInternal.NamedQualifier [name=" + name + "]";
		}
	}

}
