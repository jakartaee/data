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

import jakarta.data.expression.ComparableExpression;
import jakarta.data.expression.TextExpression;
import jakarta.data.messages.Messages;
import jakarta.data.metamodel.Attribute;
import jakarta.data.metamodel.ComparableAttribute;
import jakarta.data.metamodel.StaticMetamodel;
import jakarta.data.metamodel.TextAttribute;
import jakarta.data.repository.OrderBy;

/**
 * <p>Requests sorting on a given entity attribute or expression.</p>
 *
 * <p>An instance of {@code Sort} specifies a sorting criterion based
 * on an entity attribute or an expression involving entity attributes,
 * with a sorting {@linkplain Direction direction} and well-defined
 * case sensitivity.</p>
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
 * @param <T>          type of entity from which query results are obtained.
 * @param expression   an expression that computes a value by which to
 *                         order results. Alternatively, {@code null} if
 *                         {@code property} is supplied instead.
 * @param property     name of an entity attribute to order by.
 *                         Alternatively, {@code null} if {@code expression}
 *                         is supplied instead.
 * @param isAscending  whether ordering for this attribute is ascending
 *                     ({@code true}) or descending ({@code false}).
 * @param ignoreCase   whether or not to request case insensitive ordering
 *                     from a database with case sensitive collation.
 * @param nullOrdering whether {@code null} values are ordered
 *                     {@link Nulls#FIRST FIRST}, {@link Nulls#LAST LAST}, or
 *                     {@linkplain Nulls#UNSPECIFIED by the data store}.
 */
public record Sort<T>(ComparableExpression<T, ? extends Comparable<?>> expression,
                      String property,
                      boolean isAscending,
                      boolean ignoreCase,
                      Nulls nullOrdering) {

    /**
     * Indicates how {@code null} values are ordered.
     *
     * @since 1.1
     */
    public enum Nulls {
        /**
         * Values that are {@code null} are ordered before values that are
         * non-{@code null}. If the data store is a non-relational database
         * that is not capable of ordering {@code null} values, then
         * repository methods to which this value is supplied must raise
         * {@link IllegalArgumentException}.
         */
        FIRST,
        /**
         * Values that are not {@code null} are ordered before values that
         * are {@code null}. If the data store is a non-relational database
         * that is not capable of ordering {@code null} values, then
         * repository methods to which this value is supplied must raise
         * {@link IllegalArgumentException}.
         */
        LAST,
        /**
         * Ordering of {@code null} values is not indicated by the application
         * and is left the capability and behavior of the data store.
         */
        UNSPECIFIED
    }

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
     * @param property     name of the entity attribute to order by.
     * @param isAscending  whether ordering for this attribute is ascending
     *                     (true) or descending (false).
     * @param ignoreCase   whether or not to request case insensitive ordering
     *                     from a database with case sensitive collation.
     * @param nullOrdering whether {@code null} values are ordered
     *                     {@link Nulls#FIRST FIRST}, {@link Nulls#LAST LAST},
     *                     or {@linkplain Nulls#UNSPECIFIED by the data store}.
     * @since 1.1
     */
    public Sort {
        if (expression == null && property == null) {
            throw new NullPointerException(
                Messages.get("001.arg.required", "expression"));
        }

        if (nullOrdering == null) {
            throw new NullPointerException(
                Messages.get("001.arg.required", "nullOrdering"));
        }

        if (property != null && expression != null) {
            // TODO add a message
            throw new IllegalArgumentException(
                    "property: " + property + ", expression: " + expression);
        }

        property = expression instanceof Attribute<?> attr
                ? attr.name()
                : property;
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
        this(null,
             property,
             isAscending,
             ignoreCase,
             Nulls.UNSPECIFIED);
    }

    // Override to provide method documentation:

    /**
     * An expression to order by. The presence of a sort expression is
     * mutually exclusive with the presence of a
     * {@linkplain #property() sort attribute name}.
     *
     * @return The attribute name to order by, or {@code null} if this
     *         {@code Sort} instance pertains to a {@link #property()}
     */
    public ComparableExpression<T, ? extends Comparable<?>> expression() {
        return expression;
    }

    /**
     * Name of the entity attribute to order by The presence of a
     * sort attribute name is mutually exclusive with the presence of a
     * sort {@link #expression()}.
     *
     * @return The attribute name to order by, or {@code null} if this
     *         {@code Sort} instance pertains to an {@link #expression()}
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
     * Indicates whether {@code null} values are ordered
     * {@link Nulls#FIRST FIRST}, {@link Nulls#LAST LAST}, or
     * {@linkplain Nulls#UNSPECIFIED by the data store}.
     *
     * @return indication of how {@code null} values are ordered.
     * @since 1.1
     */
    public Nulls nullOrdering() {
        return nullOrdering;
    }

    /**
     * Create a {@link Sort} instance.
     *
     * @param <T>        entity class of the sortable entity attribute.
     * @param attribute  name of the entity attribute to order by.
     * @param direction  the direction in which to order.
     * @param ignoreCase whether to request a case insensitive ordering.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when there is a null parameter.
     */
    public static <T> Sort<T> of(String attribute,
                                 Direction direction,
                                 boolean ignoreCase) {
        if (direction == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "direction"));
        }

        return new Sort<>(null,
                          attribute,
                          Direction.ASC.equals(direction),
                          ignoreCase,
                          Nulls.UNSPECIFIED);
    }

    /**
     * <p>Create a {@link Sort} instance, indicating how {@code null} values
     * are ordered.</p>
     *
     * @param <T>          entity class of the sortable entity attribute.
     * @param attribute    name of the entity attribute to order by.
     * @param direction    the direction in which to order.
     * @param ignoreCase   whether to request a case insensitive ordering.
     * @param nullOrdering whether {@code null} values are ordered
     *                     {@link Nulls#FIRST FIRST}, {@link Nulls#LAST LAST},
     *                     or {@linkplain Nulls#UNSPECIFIED by the data store}.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when there is a {@code null} parameter.
     * @since 1.1
     */
    public static <T> Sort<T> of(String attribute,
                                 Direction direction,
                                 boolean ignoreCase,
                                 Nulls nullOrdering) {
        if (direction == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "direction"));
        }

        return new Sort<>(null,
                          attribute,
                          Direction.ASC.equals(direction),
                          ignoreCase,
                          nullOrdering);
    }

    /**
     * Create a {@link Sort} instance with {@link Direction#ASC ascending
     * direction} that does not request case-insensitive ordering.
     * <p>
     * The {@code expression} can be obtained from the
     * {@linkplain StaticMetamodel static metamodel}, which also allows a
     * more concise way to sort on an attribute or expression,
     *
     * <pre>{@code
     * _Person.ssn.asc()
     * }</pre>
     *
     * @param <T>        entity class of the expression
     * @param <V>        type of the expression
     * @param expression the {@linkplain ComparableAttribute entity attribute}
     *                   or {@linkplain ComparableExpression expression} by
     *                   which to order
     * @return a {@link Sort} instance. Never {@code null}
     * @throws NullPointerException if the {@code expression} is {@code null}
     * @since 1.1
     */
    public static <T, V extends Comparable<?>> Sort<T> asc(
            ComparableExpression<T, V> expression) {
        return new Sort<>(expression, null, true, false, Nulls.UNSPECIFIED);
    }

    /**
     * Create a {@link Sort} instance with
     * {@linkplain Direction#ASC ascending direction} that does not
     * request case-insensitive ordering.
     *
     * @param <T>       entity class of the sortable entity attribute.
     * @param attribute name of the entity attribute to order by.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the attribute name is null.
     */
    public static <T> Sort<T> asc(String attribute) {
        return new Sort<>(null, attribute, true, false, Nulls.UNSPECIFIED);
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
        return new Sort<>(null, attribute, true, true, Nulls.UNSPECIFIED);
    }

    /**
     * Create a {@link Sort} instance with {@link Direction#ASC ascending
     * direction} and case insensitive ordering.
     * <p>
     * The {@code expression} can be obtained from the
     * {@linkplain StaticMetamodel static metamodel}, which also allows a
     * more concise way to sort on an attribute or expression,
     *
     * <pre>{@code
     * _Product.name.ascIgnoreCase()
     * }</pre>
     *
     * @param <T>        entity class of the expression
     * @param expression the {@linkplain TextAttribute entity attribute}
     *                   or {@linkplain TextExpression expression} by
     *                   which to order
     * @return a {@link Sort} instance. Never {@code null}
     * @throws NullPointerException if the {@code expression} is {@code null}
     * @since 1.1
     */
    public static <T> Sort<T> ascIgnoreCase(TextExpression<T> expression) {
        return new Sort<>(expression, null, true, true, Nulls.UNSPECIFIED);
    }

    /**
     * Create a {@link Sort} instance with {@link Direction#DESC descending
     * direction} that does not request case-insensitive ordering.
     * <p>
     * The {@code expression} can be obtained from the
     * {@linkplain StaticMetamodel static metamodel}, which also allows a
     * more concise way to sort on an attribute or expression,
     *
     * <pre>{@code
     * _Person.ssn.desc()
     * }</pre>
     *
     * @param <T>        entity class of the expression
     * @param <V>        type of the expression
     * @param expression the {@linkplain ComparableAttribute entity attribute}
     *                   or {@linkplain ComparableExpression expression} by
     *                   which to order
     * @return a {@link Sort} instance. Never {@code null}
     * @throws NullPointerException if the {@code expression} is {@code null}
     * @since 1.1
     */
    public static <T, V extends Comparable<?>> Sort<T> desc(
            ComparableExpression<T, V> expression) {
        return new Sort<>(expression, null, false, false, Nulls.UNSPECIFIED);
    }

    /**
     * Create a {@link Sort} instance with
     * {@linkplain Direction#DESC descending direction} that does not
     * request case-insensitive ordering.
     *
     * @param <T>       entity class of the sortable entity attribute.
     * @param attribute name of the entity attribute to order by.
     * @return a {@link Sort} instance. Never {@code null}.
     * @throws NullPointerException when the attribute name is null.
     */
    public static <T> Sort<T> desc(String attribute) {
        return new Sort<>(null, attribute, false, false, Nulls.UNSPECIFIED);
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
        return new Sort<>(null, attribute, false, true, Nulls.UNSPECIFIED);
    }

    /**
     * Create a {@link Sort} instance with {@link Direction#DESC descending
     * direction} and case insensitive ordering.
     * <p>
     * The {@code expression} can be obtained from the
     * {@linkplain StaticMetamodel static metamodel}, which also allows a
     * more concise way to sort on an attribute or expression,
     *
     * <pre>{@code
     * _Product.name.descIgnoreCase()
     * }</pre>
     *
     * @param <T>        entity class of the expression
     * @param expression the {@linkplain TextAttribute entity attribute}
     *                   or {@linkplain TextExpression expression} by
     *                   which to order
     * @return a {@link Sort} instance. Never {@code null}
     * @throws NullPointerException if the {@code expression} is {@code null}
     * @since 1.1
     */
    public static <T> Sort<T> descIgnoreCase(TextExpression<T> expression) {
        return new Sort<>(expression, null, false, true, Nulls.UNSPECIFIED);
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
     * @return a sort with {@link #nullOrdering} set to {@link Nulls#FIRST}.
     * @since 1.1
     */
    public Sort<T> nullsFirst() {
        return new Sort<>(expression,
                          expression == null ? property : null,
                          isAscending,
                          ignoreCase,
                          Nulls.FIRST);
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
     * @return a sort with {@link #nullOrdering} set to {@link Nulls#LAST}.
     * @since 1.1
     */
    public Sort<T> nullsLast() {
        return new Sort<>(expression,
                          expression == null ? property : null,
                          isAscending,
                          ignoreCase,
                          Nulls.LAST);
    }
}