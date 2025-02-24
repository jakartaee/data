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

import jakarta.data.metamodel.constraint.Between;
import jakarta.data.metamodel.constraint.Constraint;
import jakarta.data.metamodel.constraint.EqualTo;
import jakarta.data.metamodel.constraint.GreaterThan;
import jakarta.data.metamodel.constraint.GreaterThanOrEqual;
import jakarta.data.metamodel.constraint.LessThan;
import jakarta.data.metamodel.constraint.LessThanOrEqual;
import jakarta.data.metamodel.constraint.Like;
import jakarta.data.metamodel.constraint.NotBetween;
import jakarta.data.metamodel.constraint.NotEqualTo;
import jakarta.data.metamodel.constraint.NotLike;

/**
 * <p>Annotates a parameter of a repository {@link Find} or {@link Delete} method,
 * indicating that a persistent field should be compared ignoring case.
 * The {@link By} annotation can be used on the same parameter to identify the
 * persistent field. Otherwise, if the {@code -parameters} compile option is
 * enabled, the the persistent field is inferred by matching the name of the
 * method parameter.</p>
 *
 * <p>The {@code IgnoreCase} annotation can be used on repository method parameters
 * that are of type {@link String} - in which case the equality comparison is
 * assumed - and on parameters of the following types:
 * </p>
 * <ul>
 * <li>{@link Between Between&lt;String&gt;}</li>
 * <li>{@link EqualTo EqualTo&lt;String&gt;}</li>
 * <li>{@link GreaterThan GreaterThan&lt;String&gt;}</li>
 * <li>{@link GreaterThanOrEqual GreaterThanEqual&lt;String&gt;}</li>
 * <li>{@link LessThan LessThan&lt;String&gt;}</li>
 * <li>{@link LessThanOrEqual LessThanEqual&lt;String&gt;}</li>
 * <li>{@link Like Like}</li>
 * <li>{@link NotBetween NotBetween&lt;String&gt;}</li>
 * <li>{@link NotEqualTo NotEqualTo&lt;String&gt;}</li>
 * <li>{@link NotLike NotLike}</li>
 * <li>{@link Constraint Constraint<String>}, which must be one of the above.</li>
 * </ul>
 *
 * <p>For example,</p>
 *
 * <pre>
 * &#64;Repository
 * public interface People extends BasicRepository&lt;Person, Long&gt; {
 *
 *     // List of Person entities where the lastName matches ignoring case.
 *     // Requires the -parameters compile option.
 *     &#64;Find
 *     List&lt;Person&gt; ofSurname(&#64;IgnoreCase String lastName);
 *
 *     // List of Person entities where the lastName field, ignoring case,
 *     // is alphabetized within the first parameter up to but not including
 *     // the second parameter.
 *     &#64;Find
 *     &#64;OrderBy(_Person.LASTNAME)
 *     List&lt;Person&gt; ofSurnameWithin(
 *         &#64;By(_Person.LASTNAME) &#64;IgnoreCase GreaterThanEqual&lt;String&gt; beginAt,
 *         &#64;By(_Person.LASTNAME) &#64;IgnoreCase LessThan&lt;String&gt; endBefore);
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface IgnoreCase {
}
