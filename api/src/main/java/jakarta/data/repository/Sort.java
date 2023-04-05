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
package jakarta.data.repository;

import java.util.Objects;

/**
 * <p><code>Sort</code> allows the application to dynamically provide
 * sort criteria which includes a case sensitivity request,
 * a {@link Direction} and a property.</p>
 *
 * <p>Dynamic <code>Sort</code> criteria can be specified when
 * {@link Pageable#sortBy(Sort[]) requesting a page of results}
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
 * (<code>OrderBy</code> keyword or {@link OrderBy} annotation or
 * {@link Query} with an <code>ORDER BY</code> clause), the static
 * sort criteria is applied first, followed by the dynamic sort criteria
 * that is defined by <code>Sort</code> instances in the order listed.</p>
 *
 * <p>A repository method will fail with a
 * {@link jakarta.data.exceptions.DataException DataException}
 * or a more specific subclass if</p>
 * <ul>
 * <li>a <code>Sort</code> parameter is
 *     specified in combination with a {@link Pageable} parameter with
 *     {@link Pageable#sorts()}.</li>
 * <li>the database is incapable of ordering with the requested
 *     sort criteria.</li>
 * </ul>
 *
 * @param property    name of the property to order by.
 * @param isAscending whether ordering for this property is ascending (true) or descending (false).
 * @param ignoreCase  whether or not to request case insensitive ordering from a database with case sensitive collation.
 */
public record Sort(String property, boolean isAscending, boolean ignoreCase) {

    /**
     * <p>Defines sort criteria for an entity property. For more descriptive code, use:</p>
     * <ul>
     * <li>{@link #asc(String) Sort.asc(propertyName)} for ascending sort on a property.</li>
     * <li>{@link #ascIgnoreCase(String) Sort.ascIgnoreCase(propertyName)} for case insensitive ascending sort on a property.</li>
     * <li>{@link #desc(String) Sort.desc(propertyName)} for descending sort on a property.</li>
     * <li>{@link #descIgnoreCase(String) Sort.descIgnoreCase(propertyName)} for case insensitive descending sort on a property.</li>
     * </ul>
     *
     * @param property    name of the property to order by.
     * @param isAscending whether ordering for this property is ascending (true) or descending (false).
     * @param ignoreCase  whether or not to request case insensitive ordering from a database with case sensitive collation.
     */
    public Sort {
        Objects.requireNonNull(property, "property is required");
    }

    // Override to provide method documentation:
    /**
     * Name of the property to order by.
     *
     * @return The property name to order by; will never be {@literal null}.
     */
    public String property() {
        return property;
    }

    // Override to provide method documentation:
    /**
     * <p>Indicates whether or not to request case insensitive ordering
     * from a database with case sensitive collation.
     * A database with case insensitive collation performs case insensitive
     * ordering regardless of the requested <code>ignoreCase</code> value.</p>
     *
     * @return Returns whether or not to request case insensitive sorting for the property.
     */
    public boolean ignoreCase() {
        return ignoreCase;
    }

    // Override to provide method documentation:
    /**
     * Indicates whether to sort the property in ascending order (true) or descending order (false).
     *
     * @return Returns whether sorting for this property shall be ascending.
     */
    public boolean isAscending() {
        return isAscending;
    }

    /**
     * Indicates whether to sort the property in descending order (true) or ascending order (false).
     *
     * @return Returns whether sorting for this property shall be descending.
     */
    public boolean isDescending() {
        return !isAscending;
    }

    /**
     * Create a {@link Sort} instance
     *
     * @param property  the property name to order by
     * @param direction the direction in which to order.
     * @param ignoreCase whether to request a case insensitive ordering.
     * @return an {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when there is a null parameter
     */
    public static Sort of(String property, Direction direction, boolean ignoreCase) {
        Objects.requireNonNull(direction, "direction is required");
        return new Sort(property, Direction.ASC.equals(direction), ignoreCase);
    }

    /**
     * Create a {@link Sort} instance with ascending direction {@link  Direction#ASC}
     * that does not request case insensitive ordering.
     *
     * @param property the property name to order by
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null
     */
    public static Sort asc(String property) {
        return new Sort(property, true, false);
    }

    /**
     * Create a {@link Sort} instance with ascending direction {@link  Direction#ASC}
     * and case insensitive ordering.
     *
     * @param property the property name to order by.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null.
     */
    public static Sort ascIgnoreCase(String property) {
        return new Sort(property, true, true);
    }

    /**
     * Create a {@link Sort} instance with descending direction {@link  Direction#DESC}
     * that does not request case insensitive ordering.
     *
     * @param property the property name to order by
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null
     */
    public static Sort desc(String property) {
        return new Sort(property, false, false);
    }

    /**
     * Create a {@link Sort} instance with descending direction {@link  Direction#DESC}
     * and case insensitive ordering.
     *
     * @param property the property name to order by.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null.
     */
    public static Sort descIgnoreCase(String property) {
        return new Sort(property, false, true);
    }
}