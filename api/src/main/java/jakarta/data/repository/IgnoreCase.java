/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.data.constraint.AtLeast;
import jakarta.data.constraint.AtMost;
import jakarta.data.constraint.Between;
import jakarta.data.constraint.EqualTo;
import jakarta.data.constraint.GreaterThan;
import jakarta.data.constraint.LessThan;
import jakarta.data.constraint.Like;
import jakarta.data.constraint.NotBetween;
import jakarta.data.constraint.NotEqualTo;
import jakarta.data.constraint.NotLike;

/**
 * <p>Annotates a parameter of a repository {@link Find} or {@link Delete}
 * method, to compare an entity attribute ignoring case.</p>
 *
 * <p>The parameter must correspond to a {@link String}-typed entity attribute
 * and must either be an implicit equality constraint or one of the following
 * constraints, indicated by the method parameter's type or its {@link Is @Is}
 * annotation.
 *
 * <ul>
 * <li>{@link AtLeast}</li>
 * <li>{@link AtMost}</li>
 * <li>{@link Between} (not available for {@code @Is})</li>
 * <li>{@link EqualTo}</li>
 * <li>{@link GreaterThan}</li>
 * <li>{@link LessThan}</li>
 * <li>{@link Like}</li>
 * <li>{@link NotBetween} (not available for {@code @Is})</li>
 * <li>{@link NotEqualTo}</li>
 * <li>{@link NotLike}</li>
 * </ul>
 *
 * <p>For example,</p>
 *
 * <pre>
 * &#64;Repository
 * public interface People extends BasicRepository&lt;Person, Long&gt; {
 *
 *     // List of Person entities where the lastName attribute matches a
 *     // literal value, ignoring case.
 *     // Requires the -parameters compile option.
 *     &#64;Find
 *     List&lt;Person&gt; ofSurname(&#64;IgnoreCase String lastName);
 *
 *     // List of Person entities where the lastName attribute matches a
 *     // pattern, ignoring case.
 *     &#64;Find
 *     &#64;OrderBy(_Person.LASTNAME)
 *     &#64;OrderBy(_Person.FIRSTNAME)
 *     List&lt;Person&gt; surnamed(&#64;By(_Person.LASTNAME) &#64;IgnoreCase Like pattern);
 *
 *     // List of Person entities where the lastName attribute, is alphabetized
 *     // within the first parameter up to but not including the second
 *     // parameter, ignoring case.
 *     &#64;Find
 *     &#64;OrderBy(_Person.LASTNAME)
 *     &#64;OrderBy(_Person.FIRSTNAME)
 *     List&lt;Person&gt; withinSurnameRange(
 *         &#64;By(_Person.LASTNAME) &#64;IgnoreCase &#64;Is(AtLeast.class) String beginAt,
 *         &#64;By(_Person.LASTNAME) &#64;IgnoreCase &#64;Is(LessThan.class) String endBefore);
 * }
 *
 * ...
 *
 * smiths = people.ofSurname("smith");
 * aNames = people.surnamed(Like.pattern("A%"));
 * surnamesAToE = people.withinSurnameRange("A", "F");
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface IgnoreCase {
}
