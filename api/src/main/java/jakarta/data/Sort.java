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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data;


import java.util.Objects;

/**
 * <p>Requests sorting on a given entity attribute.</p>
 *
 * <p><code>Sort</code> allows the application to dynamically provide
 * sort criteria which includes a case sensitivity request,
 * a {@link Direction} and a property.</p>
 *
 * <p>Dynamic <code>Sort</code> criteria can be specified when
 * {@link jakarta.data.page.Pageable#sortBy(Sort[]) requesting a page of results}
 * or can be optionally specified as
 * parameters to a repository method in any of the positions that are after
 * the query parameters. You can use <code>Sort...</code> to allow a variable
 * number of <code>Sort</code> criteria. For example,</p>
 *
 * <pre>
 * Employee[] findByYearHired(int yearYired, Limit maxResults, Sort... sortBy);
 * ...
 * highestPaidNewHires = employees.findByYearHired(Year.now(),
 *                                                 Limit.of(10),
 *                                                 Sort.desc("salary"),
 *                                                 Sort.asc("lastName"),
 *                                                 Sort.asc("firstName"));
 * </pre>
 *
 * <p>When combined on a method with static sort criteria
 * (<code>OrderBy</code> keyword or {@link jakarta.data.repository.OrderBy} annotation or
 * {@link jakarta.data.repository.Query} with an <code>ORDER BY</code> clause), the static
 * sort criteria is applied first, followed by the dynamic sort criteria
 * that is defined by <code>Sort</code> instances in the order listed.</p>
 *
 * <p>In the example above, the matching employees are sorted first by salary
 * from highest to lowest. Employees with the same salary are then sorted
 * alphabetically by last name. Employees with the same salary and last name
 * are then sorted alphabetically by first name.</p>
 *
 * <p>A repository method will fail with a
 * {@link jakarta.data.exceptions.DataException DataException}
 * or a more specific subclass if</p>
 * <ul>
 * <li>a <code>Sort</code> parameter is
 *     specified in combination with a {@link jakarta.data.page.Pageable} parameter with
 *     {@link jakarta.data.page.Pageable#sorts()}.</li>
 * <li>the database is incapable of ordering with the requested
 *     sort criteria.</li>
 * </ul>
 *
 */
public interface Sort {
    /**
     * Create a {@link Sort} instance
     *
     * @param property   the property name to order by
     * @param direction  the direction in which to order.
     * @param ignoreCase whether to request a case insensitive ordering.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when there is a null parameter
     */
    static Sort of(String property, Direction direction, boolean ignoreCase) {
        Objects.requireNonNull(direction, "direction is required");
        return new DefaultSort(property, Direction.ASC.equals(direction), ignoreCase);
    }

    /**
     * Create a {@link Sort} instance with {@link Direction#ASC ascending direction}
     * that does not request case insensitive ordering.
     *
     * @param property the property name to order by
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null
     */
    static Sort asc(String property) {
        return new DefaultSort(property, true, false);
    }

    /**
     * Create a {@link Sort} instance with {@link Direction#ASC ascending direction}
     * and case insensitive ordering.
     *
     * @param property the property name to order by.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null.
     */
    static Sort ascIgnoreCase(String property) {
        return new DefaultSort(property, true, true);
    }

    /**
     * Create a {@link Sort} instance with {@link Direction#DESC descending direction}
     * that does not request case insensitive ordering.
     *
     * @param property the property name to order by
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null
     */
    static Sort desc(String property) {
        return new DefaultSort(property, false, false);
    }

    /**
     * Create a {@link Sort} instance with {@link Direction#DESC descending direction}
     * and case insensitive ordering.
     *
     * @param property the property name to order by.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null.
     */
    static Sort descIgnoreCase(String property) {
        return new DefaultSort(property, false, true);
    }

    /**
     * Name of the property to order by.
     *
     * @return The property name to order by; will never be {@code null}.
     */
    String property();

    /**
     * <p>Indicates whether or not to request case insensitive ordering
     * from a database with case sensitive collation.
     * A database with case insensitive collation performs case insensitive
     * ordering regardless of the requested <code>ignoreCase</code> value.</p>
     *
     * @return Returns whether or not to request case insensitive sorting for the property.
     */
    boolean ignoreCase();

    /**
     * Indicates whether to sort the property in ascending order (true) or descending order (false).
     *
     * @return Returns whether sorting for this property shall be ascending.
     */
    boolean isAscending();

    /**
     * Indicates whether to sort the property in descending order (true) or ascending order (false).
     *
     * @return Returns whether sorting for this property shall be descending.
     */
    boolean isDescending();
}
