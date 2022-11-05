/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
 * <p><code>Sort</code> implements the pairing of a {@link Direction} and a property.</p>
 *
 * <p>Dynamic <code>Sort</code> criteria are optionally specified as
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
 * <p>It is preferable to use static sorting criteria
 * (<code>OrderBy</code> keyword or {@link Query} or {@link OrderBy} annotation)
 * where possible to better allow for optimizations by the provider.</p>
 *
 * <p>A repository method will fail if a <code>Sort</code> parameter is
 * specified in combination with any of:</p>
 * <ul>
 * <li>an <code>OrderBy</code> keyword</li>
 * <li>an {@link OrderBy} annotation</li>
 * <li>a {@link Query} annotation that contains an <code>ORDER BY</code> clause.</li>
 * </ul>
 */
public final class Sort {

    private final String property;

    private final Direction direction;

    private Sort(String property, Direction direction) {
        this.property = property;
        this.direction = direction;
    }

    /**
     * @return The property name to order by; will never be {@literal null}.
     */
    public String getProperty() {
        return this.property;
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
        return Objects.equals(property, sort.property) && direction == sort.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(property, direction);
    }

    @Override
    public String toString() {
        return "Sort{" +
                "property='" + property + '\'' +
                ", direction=" + direction +
                '}';
    }

    /**
     * Create a {@link Sort} instance
     *
     * @param property  the property name to order by
     * @param direction The direction order by
     * @return an {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when there is a null parameter
     */
    public static Sort of(String property, Direction direction) {
        Objects.requireNonNull(property, "property is required");
        Objects.requireNonNull(direction, "direction is required");
        return new Sort(property, direction);
    }

    /**
     * Create a {@link Sort} instance with ascending direction {@link  Direction#ASC}
     *
     * @param property the property name to order by
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null
     */
    public static Sort asc(String property) {
        return of(property, Direction.ASC);
    }

    /**
     * Create a {@link Sort} instance on descending direction {@link  Direction#DESC}
     *
     * @param property the property name to order by
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null
     */
    public static Sort desc(String property) {
        return of(property, Direction.DESC);
    }

}