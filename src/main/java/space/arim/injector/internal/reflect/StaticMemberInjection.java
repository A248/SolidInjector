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

import java.lang.reflect.Member;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import space.arim.injector.internal.DependencyRepository;

/**
 * Static injection wrapper which ensures static methods and fields are injected at most once
 * 
 * @author A248
 *
 */
class StaticMemberInjection implements PostConstructorInjection {

	private final Member member;
	private final PostConstructorInjection delegate;

	StaticMemberInjection(Member member, PostConstructorInjection delegate) {
		this.member = member;
		this.delegate = delegate;
	}

	@Override
	public void injectInto(Object instance, DependencyRepository repository) {
		boolean added = StaticallyInjectedMembers.INJECTED.add(member);
		if (!added) {
			return;
		}
		delegate.injectInto(instance, repository);
	}

	@Override
	public String toString() {
		return "StaticMemberInjection [member=" + member + ", delegate=" + delegate + "]";
	}

	/**
	 * Uses static state to track static state. Please don't use static state in your program.
	 * 
	 */
	private static class StaticallyInjectedMembers {

		static final Set<Member> INJECTED = ConcurrentHashMap.newKeySet();
	}

}
