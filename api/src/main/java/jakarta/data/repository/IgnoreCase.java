/*
 * Copyright (c) 2024,2025 Contributors to the Eclipse Foundation
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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.data.metamodel.restrict.Operator;

/**
 * <p>Annotates a parameter of a repository {@link Find} or {@link Delete} method,
 * indicating that a persistent field should be compared ignoring case.
 * The {@link By} annotation can be used on the same parameter to identify the
 * persistent field. Otherwise, if the {@code -parameters} compile option is
 * enabled, the the persistent field is inferred by matching the name of the
 * method parameter. The {@link Operator#EQUAL EQUAL} comparison is assumed
 * unless the {@link Is} annotation is used on the same parameter to choose a
 * different type of comparison.</p>
 *
 * <p>For example,</p>
 *
 * <pre>
 * &#64;Repository
 * public interface People extends BasicRepository&lt;Person, Long&gt; {
 *
 *     // Find all Person entities where the lastName matches the respective value
 *     // ignoring case.
 *     &#64;Find
 *     List&lt;Person&gt; withSurname(&#64;By(_Person.LASTNAME) &#64;IgnoreCase String surname);
 *
 *     // Find a page of Person entities where the lastName field begins with the
 *     // supplied prefix, ignoring case.
 *     &#64;Find
 *     Page&lt;Person&gt; withSurnamePrefix(&#64;By(_Product.LASTNAME) &#64;Is(PREFIXED) &#64;IgnoreCase String prefix,
 *                                    PageRequest pagination,
 *                                    Order&lt;Person&gt; order);
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface IgnoreCase {
}
