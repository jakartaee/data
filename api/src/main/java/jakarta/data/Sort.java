/*
 * Copyright (c) 2022,2026 Contributors to the Eclipse Foundation
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

import jakarta.data.messages.Messages;
import jakarta.data.metamodel.StaticMetamodel;
import jakarta.data.repository.OrderBy;

/**
 * <p>Requests sorting on a given entity attribute.</p>
 *
 * <p>An instance of {@code Sort} specifies a sorting criterion based
 * on an entity attribute, with a sorting {@linkplain Direction direction} and
 * well-defined case sensitivity.</p>
 *
 * <p>A query method of a repository may have a parameter or parameters
 * of type {@code Sort} if its return type indicates that it may return multiple
 * entities. Parameters of type {@code Sort} must occur after the method
 * parameters representing regular parameters of the query itself.</p>
 *
 * <p>A repository method parameter of type {@link Order} allows a variable
 * number of {@code Sort} criteria. For example,</p>
 *
 * <pre>{@code
 * Employee[] findByYearHired(int yearHired, Limit maxResults, Order<Employee> sortBy);
 *
 * ...
 * highestPaidNewHires = employees.findByYearHired(Year.now().getValue(),
 *                                                 Limit.of(10),
 *                                                 Order.by(Sort.desc("salary"),
 *                                                          Sort.asc("lastName"),
 *                                                          Sort.asc("firstName")));
 * }</pre>
 *
 * <p>Alternatively, {@link Order} may be used in combination with
 * the {@linkplain StaticMetamodel static metamodel} to allow a variable number
 * of typed {@code Sort} criteria. For example,</p>
 *
 * <pre>{@code
 * highestPaidNewHires = employees.findByYearHired(Year.now().getValue(),
 *                                                 Limit.of(10),
 *                                                 Order.by(_Employee.salary.desc(),
 *                                                          _Employee.lastName.asc(),
 *                                                          _Employee.firstName.asc()));
 * }</pre>
 *
 * <p>When multiple sorting criteria are provided, sorting is
 * lexicographic, with the precedence of a criterion depending on its position
 * with the list of criteria.</p>
 *
 * <p>A repository method may declare static sorting criteria using
 * the {@code OrderBy} keyword or {@link OrderBy @OrderBy} annotation, and also
 * accept dynamic sorting criteria via its parameters. In this situation, the
 * static sorting criteria are applied first, followed by any dynamic sorting
 * criteria specified by instances of {@code Sort}.</p>
 *
 * <p>In the example above, the matching employees are sorted first by
 * salary from highest to lowest. Employees with the same salary are then sorted
 * alphabetically by last name. Employees with the same salary and last name are
 * then sorted alphabetically by first name.</p>
 *
 * <p>A repository method throws {@link jakarta.data.exceptions.DataException}
 * if the database is incapable of ordering the query results using the given
 * sort criteria.</p>
 *
 * @param <T>             entity class of the entity attribute upon which to
 *                        sort.
 * @param property        name of the entity attribute to order by.
 * @param isAscending     whether ordering for this attribute is ascending
 *                        ({@code true}) or descending ({@code false}).
 * @param ignoreCase      whether or not to request case insensitive ordering
 *                        from a database with case sensitive collation.
 * @param orderNullsFirst whether {@code null} values are ordered first
 *                        ({@code TRUE}), last ({@code FALSE}), or according
 *                        to the capability and behavior of the underlying
 *                        data store ({@code null}).
 */
public record Sort<T>(String property,
                      boolean isAscending,
                      boolean ignoreCase,
                      Boolean orderNullsFirst) {
    /**
     * <p>Defines sort criteria for an entity attribute. For more descriptive
     * code, use:</p>
     * <ul>
     * <li>{@link #asc(String) Sort.asc(attributeName)}
     *     for ascending sort on an entity attribute.</li>
     * <li>{@link #asc(String) Sort.asc(attributeName)}.{@link #nullsFirst()}
     *     for ascending sort on an entity attribute,
     *     where {@code null} values are ordered first.</li>
     * <li>{@link #ascIgnoreCase(String) Sort.ascIgnoreCase(attributeName)}
     *     for case insensitive ascending sort on an entity attribute.</li>
     * <li>{@link #ascIgnoreCase(String) Sort.ascIgnoreCase(attributeName)}.{@link #nullsFirst()}
     *     for case insensitive ascending sort on an entity attribute,
     *     where {@code null} values are ordered first.</li>
     * <li>{@link #desc(String) Sort.desc(attributeName)}
     *     for descending sort on an entity attribute.</li>
     * <li>{@link #desc(String) Sort.desc(attributeName)}.{@link #nullsLast()}
     *     for descending sort on an entity attribute,
     *     where {@code null} values are ordered last.</li>
     * <li>{@link #descIgnoreCase(String) Sort.descIgnoreCase(attributeName)}
     *     for case insensitive descending sort on an entity attribute.</li>
     * <li>{@link #descIgnoreCase(String) Sort.descIgnoreCase(attributeName)}.{@link #nullsLast()}
     *     for case insensitive descending sort on an entity attribute,
     *     where {@code null} values are ordered last.</li>
     * </ul>
     *
     * @param property    name of the entity attribute to order by.
     * @param isAscending whether ordering for this attribute is ascending
     *                    (true) or descending (false).
     * @param ignoreCase  whether or not to request case insensitive ordering
     *                    from a database with case sensitive collation.
     */
    public Sort {
        if (property == null) {
            throw new NullPointerException(
                Messages.get("001.arg.required", "attribute"));
        }
    }

    /**
     * <p>Constructor for compatibility with Jakarta Data 1.0. Use the
     * {@link #of(String, Direction, boolean)} method instead.</p>
     *
     * @param property    name of the entity attribute to order by.
     * @param isAscending whether ordering for this attribute is ascending
     *                    ({@code true}) or descending ({@code false}).
     * @param ignoreCase  whether or not to request case insensitive ordering
     *                    from a database with case sensitive collation.
     */
    public Sort(String property, boolean isAscending, boolean ignoreCase) {
        this(property, isAscending, ignoreCase, null);
    }

    // Override to provide method documentation:

    /**
     * Name of the entity attribute to order by.
     *
     * @return The attribute name to order by; will never be {@code null}.
     */
    public String property() {
        return property;
    }

    // Override to provide method documentation:

    /**
     * <p>Indicates whether or not to request case insensitive ordering
     * from a database with case sensitive collation. A database with case
     * insensitive collation performs case insensitive ordering regardless of
     * the requested {@code ignoreCase} value.</p>
     *
     * @return Returns whether or not to request case insensitive sorting for
     * the entity attribute.
     */
    public boolean ignoreCase() {
        return ignoreCase;
    }

    // Override to provide method documentation:

    /**
     * Indicates whether to sort the entity attribute in ascending order (true)
     * or descending order (false).
     *
     * @return Returns whether sorting for this attribute shall be ascending.
     */
    public boolean isAscending() {
        return isAscending;
    }

    /**
     * Indicates whether to sort the entity attribute in descending order (true)
     * or ascending order (false).
     *
     * @return Returns whether sorting for this attribute shall be descending.
     */
    public boolean isDescending() {
        return !isAscending;
    }

    /**
     * Indicates whether {@code null} values are ordered first ({@code TRUE}),
     * last ({@code FALSE}), or according to the capability and behavior of the
     * underlying data store ({@code null}).
     *
     * @return indication of how {@code null} values are ordered.
     */
    public Boolean orderNullsFirst() {
        return orderNullsFirst;
    }

    /**
     * Create a {@link Sort} instance.
     *
     * @param <T>        entity class of the sortable entity attribute.
     * @param attribute  name of the entity attribute to order by
     * @param direction  the direction in which to order.
     * @param ignoreCase whether to request a case insensitive ordering.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when there is a null parameter
     */
    public static <T> Sort<T> of(String attribute,
                                 Direction direction,
                                 boolean ignoreCase) {
        if (direction == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "direction"));
        }

        return new Sort<>(attribute,
                          Direction.ASC.equals(direction),
                          ignoreCase,
                          null);
    }

    /**
     * <p>Create a {@link Sort} instance, indicating how {@code null} values
     * are ordered.</p>
     *
     * <p>If the data store is a non-relational database that is not capable
     * of ordering {@code null} values according to the given
     * {@code nullsFirst} argument, then repository methods to which this
     * instance is supplied must raise {@link IllegalArgumentException}.</p>
     *
     * @param <T>        entity class of the sortable entity attribute.
     * @param attribute  name of the entity attribute to order by
     * @param direction  the direction in which to order.
     * @param ignoreCase whether to request a case insensitive ordering.
     * @param nullsFirst whether {@code null} values are ordered first
     *                   ({@code true}) or last ({@code false}).
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when there is a {@code null} parameter.
     */
    public static <T> Sort<T> of(String attribute,
                                 Direction direction,
                                 boolean ignoreCase,
                                 boolean nullsFirst) {
        if (direction == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "direction"));
        }

        return new Sort<>(attribute,
                          Direction.ASC.equals(direction),
                          ignoreCase,
                          nullsFirst);
    }

    /**
     * Create a {@link Sort} instance with
     * {@link Direction#ASC ascending direction} that does not request case
     * insensitive ordering.
     *
     * @param <T>       entity class of the sortable entity attribute.
     * @param attribute name of the entity attribute to order by
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the attribute name is null
     */
    public static <T> Sort<T> asc(String attribute) {
        return new Sort<>(attribute, true, false, null);
    }

    /**
     * Create a {@link Sort} instance with
     * {@link Direction#ASC ascending direction} and case insensitive ordering.
     *
     * @param <T>       entity class of the sortable entity attribute.
     * @param attribute name of the entity attribute to order by.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the attribute name is null.
     */
    public static <T> Sort<T> ascIgnoreCase(String attribute) {
        return new Sort<>(attribute, true, true, null);
    }

    /**
     * Create a {@link Sort} instance with
     * {@link Direction#DESC descending direction} that does not request case
     * insensitive ordering.
     *
     * @param <T>       entity class of the sortable entity attribute.
     * @param attribute name of the entity attribute to order by
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the attribute name is null
     */
    public static <T> Sort<T> desc(String attribute) {
        return new Sort<>(attribute, false, false, null);
    }

    /**
     * Create a {@link Sort} instance with
     * {@link Direction#DESC descending direction} and case insensitive
     * ordering.
     *
     * @param <T>       entity class of the sortable entity attribute.
     * @param attribute name of the entity attribute to order by.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the attribute name is null.
     */
    public static <T> Sort<T> descIgnoreCase(String attribute) {
        return new Sort<>(attribute, false, true, null);
    }

    /**
     * <p>Returns an otherwise-equivalent sort that orders {@code null} values
     * first. For example,</p>
     *
     * <pre>{@code
     * page1 = customers.livingIn(country,
     *                            PageRequest.ofSize(50),
     *                            Order.by(_Customer.stateName.asc().nullsFirst(),
     *                                     _Customer.cityName.asc().nullsFirst(),
     *                                     _Customer.id.asc()));
     * }</pre>
     *
     * <p>If the data store is a non-relational database that is not capable
     * of ordering {@code null} values first, then repository methods to which
     * this instance is supplied must raise {@link IllegalArgumentException}.
     * </p>
     *
     * @return a sort with {@link #orderNullsFirst} set to {@code TRUE}.
     */
    public Sort<T> nullsFirst() {
        return new Sort<>(property, isAscending, ignoreCase, true);
    }

    /**
     * <p>Returns an otherwise-equivalent sort that orders {@code null} values
     * last. For example,</p>
     *
     * <pre>{@code
     * page1 = products.namedLike(namePattern,
     *                            PageRequest.ofSize(25),
     *                            Order.by(_Product.price.desc().nullsLast(),
     *                                     _Product.name.asc(),
     *                                     _Product.id.asc()));
     * }</pre>
     *
     * <p>If the data store is a non-relational database that is not capable
     * of ordering {@code null} values last, then repository methods to which
     * this instance is supplied must raise {@link IllegalArgumentException}.
     * </p>
     *
     * @return a sort with {@link #orderNullsFirst} set to {@code FALSE}.
     */
    public Sort<T> nullsLast() {
        return new Sort<>(property, isAscending, ignoreCase, false);
    }
}