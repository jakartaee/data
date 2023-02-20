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
 */
public final class Sort {

    private final String property;

    private final Direction direction;

    private final boolean ignoreCase;

    private Sort(String property, Direction direction, boolean ignoreCase) {
        this.property = property;
        this.direction = direction;
        this.ignoreCase = ignoreCase;
    }

    /**
     * @return The property name to order by; will never be {@literal null}.
     */
    public String property() {
        return this.property;
    }

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

    /**
     * @return Returns whether sorting for this property shall be ascending.
     */
    public boolean isAscending() {
        return Direction.ASC.equals(direction);
    }

    /**
     * @return Returns whether sorting for this property shall be descending.
     */
    public boolean isDescending() {
        return Direction.DESC.equals(direction);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sort sort = (Sort) o;
        return Objects.equals(property, sort.property) && direction == sort.direction && ignoreCase == sort.ignoreCase;
    }

    @Override
    public int hashCode() {
        return Objects.hash(property, direction, ignoreCase);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(property.length() + 32)
                .append("Sort{property='").append(property)
                .append("', direction=").append(direction);
        if (ignoreCase)
            s.append(", ignore case");
        s.append('}');
        return s.toString();
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
        Objects.requireNonNull(property, "property is required");
        Objects.requireNonNull(direction, "direction is required");
        return new Sort(property, direction, ignoreCase);
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
        return of(property, Direction.ASC, false);
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
        return of(property, Direction.ASC, true);
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
        return of(property, Direction.DESC, false);
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
        return of(property, Direction.DESC, true);
    }
}