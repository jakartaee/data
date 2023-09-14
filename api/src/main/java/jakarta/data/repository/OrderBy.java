/*
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
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

import jakarta.data.Sort;
import jakarta.data.page.Pageable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotates a repository method to request sorting of results.</p>
 *
 * <p>When multiple <code>OrderBy</code> annotations are specified on a
 * repository method, the precedence for sorting follows the order
 * in which the <code>OrderBy</code> annotations are specified,
 * and after that follows any sort criteria that is supplied
 * dynamically by {@link Sort} parameters or by a
 * {@link Pageable} parameter with {@link Pageable#sorts()}.</p>
 *
 * <p>For example, the following sorts first by the
 * <code>lastName</code> attribute in ascending order,
 * and secondly, for entities with the same <code>lastName</code>,
 * it then sorts by the <code>firstName</code> attribute,
 * also in ascending order. For entities with the same
 * <code>lastName</code> and <code>firstName</code>,
 * it then sorts by criteria that is specified in the
 * {@link Pageable#sorts()}.</p>
 *
 * <pre>
 * &#64;OrderBy("lastName")
 * &#64;OrderBy("firstName")
 * Person[] findByZipCode(int zipCode, Pageable pagination);
 * </pre>
 *
 * <p>The precise meaning of ascending and descending order is
 * defined by the database, but generally ascending order for
 * numeric values means smaller numbers before larger numbers and for
 * string values means <code>A</code> before <code>Z</code>.</p>
 *
 * <p>A repository method will fail if an <code>OrderBy</code> annotation is
 * specified in combination with any of:</p>
 * <ul>
 * <li>an <code>OrderBy</code> keyword</li>
 * <li>a {@link Query} annotation that contains an <code>ORDER BY</code> clause.</li>
 * </ul>
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
     * <p>The default value of <code>false</code> means ascending sort.</p>
     *
     * @return whether to use descending (versus ascending) order.
     */
    boolean descending() default false;

    /**
     * <p>Indicates whether or not to request case insensitive ordering
     * from a database with case sensitive collation.
     * A database with case insensitive collation performs case insensitive
     * ordering regardless of the requested <code>ignoreCase</code> value.</p>
     *
     * <p>The default value is <code>false</code>.</p>
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
     * Enables multiple <code>OrderBy</code> annotations on the same type.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface List {
        OrderBy[] value();
    }
}
