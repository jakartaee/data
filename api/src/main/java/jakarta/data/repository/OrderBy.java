/*
 * Copyright (c) 2022,2024 Contributors to the Eclipse Foundation
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

import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.page.PageRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotates a repository method to request sorting of results.</p>
 *
 * <p>When multiple {@code OrderBy} annotations are specified on a
 * repository method, the precedence for sorting follows the order
 * in which the {@code OrderBy} annotations are specified,
 * and after that follows any sort criteria that are supplied
 * dynamically by {@link Sort} parameters, any {@link Order} parameter,
 * or by a {@link PageRequest} parameter with
 * {@linkplain PageRequest#sorts() sorting criteria}.</p>
 *
 * <p>For example, the following sorts first by the
 * {@code lastName} attribute in ascending order,
 * and secondly, for entities with the same {@code lastName},
 * it then sorts by the {@code firstName} attribute,
 * also in ascending order. For entities with the same
 * {@code lastName} and {@code firstName},
 * it then sorts by criteria that is specified in the
 * {@link PageRequest#sorts()}.</p>
 *
 * <pre>
 * &#64;OrderBy("lastName")
 * &#64;OrderBy("firstName")
 * Person[] findByZipCode(int zipCode, {@code PageRequest<?>} pageRequest);
 * </pre>
 *
 * <p>The precise meaning of ascending and descending order is
 * defined by the database, but generally ascending order for
 * numeric values means smaller numbers before larger numbers and for
 * string values means {@code A} before {@code Z}.</p>
 *
 * <p>A repository method with an {@code @OrderBy} annotation must not
 * have:</p>
 * <ul>
 * <li>the <em>Query by Method Name</em> {@code OrderBy} keyword in its
 *     name, nor</li>
 * <li>a {@link Query @Query} annotation specifying a JDQL or JPQL query
 *     with an {@code ORDER BY} clause.</li>
 * </ul>
 * <p>A Jakarta Data provider is permitted to reject such a repository
 * method declaration at compile time or to implement the method to
 * throw {@link UnsupportedOperationException}.</p>
 *
 * <p>A repository method will fail with a
 * {@link jakarta.data.exceptions.DataException DataException}
 * or a more specific subclass if the database is incapable of
 * ordering with the requested sort criteria.</p>
 */
@Repeatable(OrderBy.List.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OrderBy {
    /**
     * <p>Indicate whether to use descending order
     * when sorting by this attribute.</p>
     *
     * <p>The default value of {@code false} means ascending sort.</p>
     *
     * @return whether to use descending (versus ascending) order.
     */
    boolean descending() default false;

    /**
     * <p>Indicates whether or not to request case insensitive ordering
     * from a database with case sensitive collation.
     * A database with case insensitive collation performs case insensitive
     * ordering regardless of the requested {@code ignoreCase} value.</p>
     *
     * <p>The default value is {@code false}.</p>
     *
     * @return whether or not to request case insensitive sorting for the property.
     */
    boolean ignoreCase() default false;

    /**
     * <p>Entity attribute name to sort by.</p>
     *
     * <p>For example,</p>
     *
     * <pre>
     * &#64;OrderBy("age")
     * Stream&lt;Person&gt; findByLastName(String lastName);
     * </pre>
     *
     * @return entity attribute name.
     */
    String value();

    /**
     * Enables multiple {@code OrderBy} annotations on the method.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface List {
        /**
         * Returns a list of annotations with the first taking precedence,
         * followed by the second, and so forth.
         *
         * @return list of annotations.
         */
        OrderBy[] value();
    }
}
