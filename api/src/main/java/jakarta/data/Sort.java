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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data;

import jakarta.data.metamodel.StaticMetamodel;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;

import java.util.Objects;

/**
 * <p>Requests sorting on a given entity attribute.</p>
 *
 * <p>An instance of {@code Sort} specifies a sorting criterion based
 * on an entity field, with a sorting {@linkplain Direction direction}
 * and well-defined case sensitivity.</p>
 *
 * <p>A query method of a repository may have a parameter or parameters
 * of type {@code Sort} if its return type indicates that it may return
 * multiple entities. Parameters of type {@code Sort} must occur after
 * the method parameters representing regular parameters of the query
 * itself.</p>
 *
 * <p>Alternatively, dynamic {@code Sort} criteria may be specified when
 * requesting a {@link PageRequest#sortBy(Sort) page} of results.</p>
 *
 * <p>The parameter type {@code Sort<?>...} allows a variable number
 * of generic {@code Sort} criteria. For example,</p>
 *
 * <pre>
 * Employee[] findByYearHired(int yearYired, Limit maxResults, {@code Sort<?>...} sortBy);
 * ...
 * highestPaidNewHires = employees.findByYearHired(Year.now(),
 *                                                 Limit.of(10),
 *                                                 Sort.desc("salary"),
 *                                                 Sort.asc("lastName"),
 *                                                 Sort.asc("firstName"));
 * </pre>
 *
 * <p>Alternatively, {@link Order} may be used in combination with
 * the {@linkplain StaticMetamodel static metamodel} to allow a
 * variable number of typed {@code Sort} criteria. For example,</p>
 *
 * <pre>
 * Employee[] findByYearHired(int yearYired, Limit maxResults, {@code Order<Employee>} sortBy);
 * ...
 * highestPaidNewHires = employees.findByYearHired(Year.now(),
 *                                                 Limit.of(10),
 *                                                 Order.by(_Employee.salary.desc(),
 *                                                          _Employee.lastName.asc(),
 *                                                          _Employee.firstName.asc()));
 * </pre>
 *
 * <p>When multiple sorting criteria are provided, sorting is
 * lexicographic, with the precedence of a criterion depending
 * on its position with the list of criteria.</p>
 *
 * <p>A repository method may declare static sorting criteria using
 * the ({@code OrderBy} keyword or {@link OrderBy} annotation, or
 * using {@link Query} with an {@code ORDER BY} clause), and also
 * accept dynamic sorting criteria via its parameters. In this
 * situation, the static sorting criteria are applied first,
 * followed by any dynamic sorting criteria specified by instances
 * of {@code Sort} .</p>
 *
 * <p>In the example above, the matching employees are sorted first by
 * salary from highest to lowest. Employees with the same salary are
 * then sorted alphabetically by last name. Employees with the same
 * salary and last name are then sorted alphabetically by first name.</p>
 *
 * <p>A repository method throws {@link IllegalArgumentException} if it is
 * called with an argument or arguments of type {@link Sort} and a separate
 * argument of type {@code PageRequest} with nonempty sort criteria.</p>
 *
 * <p>A repository method throws {@link jakarta.data.exceptions.DataException}
 * if the database is incapable of ordering the query results using the given
 * sort criteria.</p>
 *
 *
 * @param <T>         entity class of the property upon which to sort.
 * @param property    name of the property to order by.
 * @param isAscending whether ordering for this property is ascending (true) or descending (false).
 * @param ignoreCase  whether or not to request case insensitive ordering from a database with case sensitive collation.
 */
public record Sort<T>(String property, boolean isAscending, boolean ignoreCase) {

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
     * @return The property name to order by; will never be {@code null}.
     */
    public String property() {
        return property;
    }

    // Override to provide method documentation:
    /**
     * <p>Indicates whether or not to request case insensitive ordering
     * from a database with case sensitive collation.
     * A database with case insensitive collation performs case insensitive
     * ordering regardless of the requested {@code ignoreCase} value.</p>
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
     * @param <T>        entity class of the sortable property.
     * @param property  the property name to order by
     * @param direction the direction in which to order.
     * @param ignoreCase whether to request a case insensitive ordering.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when there is a null parameter
     */
    public static <T> Sort<T> of(String property, Direction direction, boolean ignoreCase) {
        Objects.requireNonNull(direction, "direction is required");
        return new Sort<>(property, Direction.ASC.equals(direction), ignoreCase);
    }

    /**
     * Create a {@link Sort} instance with {@link Direction#ASC ascending direction}
     * that does not request case insensitive ordering.
     *
     * @param <T>      entity class of the sortable property.
     * @param property the property name to order by
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null
     */
    public static <T> Sort<T> asc(String property) {
        return new Sort<>(property, true, false);
    }

    /**
     * Create a {@link Sort} instance with {@link Direction#ASC ascending direction}
     * and case insensitive ordering.
     *
     * @param <T>      entity class of the sortable property.
     * @param property the property name to order by.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null.
     */
    public static <T> Sort<T> ascIgnoreCase(String property) {
        return new Sort<>(property, true, true);
    }

    /**
     * Create a {@link Sort} instance with {@link Direction#DESC descending direction}
     * that does not request case insensitive ordering.
     *
     * @param <T>      entity class of the sortable property.
     * @param property the property name to order by
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null
     */
    public static <T> Sort<T> desc(String property) {
        return new Sort<>(property, false, false);
    }

    /**
     * Create a {@link Sort} instance with {@link Direction#DESC descending direction}
     * and case insensitive ordering.
     *
     * @param <T>      entity class of the sortable property.
     * @param property the property name to order by.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the property is null.
     */
    public static <T> Sort<T> descIgnoreCase(String property) {
        return new Sort<>(property, false, true);
    }
}