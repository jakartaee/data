/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
package jakarta.data.constraint;

import jakarta.data.expression.ComparableExpression;
import jakarta.data.metamodel.Attribute;
import jakarta.data.restrict.Restriction;
import jakarta.data.spi.expression.literal.ComparableLiteral;

/**
 * <p>A constraint that imposes minimum and maximum values.</p>
 *
 * <p>A parameter-based repository method can impose a constraint on an
 * entity attribute by defining a method parameter that is of type
 * {@code Between} and is of the same type or a subtype of the entity
 * attribute. For example,</p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; byModelYear(&#64;By(_Car.YEAR) Between&lt;Integer&gt; yearRange,
 *                       Order&lt;Car&gt; sorts);
 *
 * ...
 *
 * found = cars.byModelYear(Between.bounds(2021, 2024));
 * </pre>
 *
 * <p>Repository methods can also accept {@code Between} constraints at
 * run time in the form of a {@link Restriction} on a
 * {@link ComparableExpression}. For example,</p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; searchAll(Restriction&lt;Car&gt; restrict, Order&lt;Car&gt; sorts);
 *
 * ...
 *
 * found = cars.searchAll(_Car.price.between(25000, 35000),
 *                        Order.by(_Car.price.desc(),
 *                                 _Car.year.desc());
 * </pre>
 *
 * <p>The {@linkplain Attribute entity and static metamodel} for the code
 * examples within this class are shown in the {@link Attribute} Javadoc.
 * </p>
 *
 * @param <V> type of the entity attribute or a subtype or primitive wrapper
 *            type for the entity attribute.
 * @since 1.1
 */
public interface Between<V extends Comparable<?>> extends Constraint<V> {

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * greater than or equal to the given {@code minimum} and less than or
     * equals to the given {@code maximum}. For example,</p>
     *
     * <pre>
     * found = cars.byPrice(Between.bounds(27000, 37000),
     *                      Order.by(_Car.price.desc()));
     * </pre>
     *
     * @param <V>     type of the entity attribute or a subtype or primitive
     *                wrapper type for the entity attribute.
     * @param minimum the minimum value.
     * @param maximum the maximum value.
     * @return a {@code Between} constraint.
     * @throws NullPointerException if the minimum or maximum is {@code null}.
     */
    static <V extends Comparable<?>> Between<V> bounds(
            V minimum,
            V maximum) {
        return new BetweenRecord<>(ComparableLiteral.of(minimum),
                                   ComparableLiteral.of(maximum));
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * greater than or equal to the given {@code minimum} and less than or
     * equal to the value to which the given {@code maximum} expression
     * evaluates. For example,</p>
     *
     * <pre>
     * found = cars.byModelYear(Between.bounds(2020, _Car.firstModelYear.plus(5)),
     *                          Order.by(_Car.price.desc()));
     * </pre>
     *
     * @param <V>     type of the entity attribute or a subtype or primitive
     *                wrapper type for the entity attribute.
     * @param minimum a minimum value.
     * @param maximum an expression that evaluates to a maximum value.
     * @return a {@code Between} constraint.
     * @throws NullPointerException if the minimum or maximum is {@code null}.
     */
    static <V extends Comparable<?>> Between<V> bounds(
            V minimum,
            ComparableExpression<?, V> maximum) {
        return new BetweenRecord<>(ComparableLiteral.of(minimum),
                                   maximum);
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * greater than or equal to the value to which the given {@code minimum}
     * expression evaluates and less than or equal to the given
     * {@code maximum}. For example,</p>
     *
     * <pre>
     * found = cars.byModelYear(Between.bounds(_Car.firstModelYear.plus(2), 2024),
     *                          Order.by(_Car.year.desc()));
     * </pre>
     *
     * @param <V>     type of the entity attribute or a subtype or primitive
     *                wrapper type for the entity attribute.
     * @param minimum an expression that evaluates to a minimum value.
     * @param maximum a maximum value.
     * @return a {@code Between} constraint.
     * @throws NullPointerException if the minimum or maximum is {@code null}.
     */
    static <V extends Comparable<?>> Between<V> bounds(
            ComparableExpression<?, V> minimum,
            V maximum) {
        return new BetweenRecord<>(minimum,
                                   ComparableLiteral.of(maximum));
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * greater than or equal to the value to which the given {@code minimum}
     * expression evaluates and less than or equal to the value to which the
     * given {@code maximum} expression evaluates. For example,</p>
     *
     * <pre>
     * found = cars.byModelYear(Between.bounds(_Car.firstModelYear.plus(1),
     *                                         _Car.firstModelYear.plus(4)),
     *                          Order.by(_Car.price.desc(),
     *                                   _Car.year.desc(),
     *                                   _Car.vin.asc()));
     * </pre>
     *
     * @param <V>     type of the entity attribute or a subtype or primitive
     *                wrapper type for the entity attribute.
     * @param minimum an expression that evaluates to a minimum value.
     * @param maximum an expression that evaluates to a maximum value.
     * @return a {@code Between} constraint.
     * @throws NullPointerException if the minimum or maximum is {@code null}.
     */
    static <V extends Comparable<?>> Between<V> bounds(
            ComparableExpression<?, V> minimum,
            ComparableExpression<?, V> maximum) {
        return new BetweenRecord<>(minimum,
                                   maximum);
    }

    /**
     * <p>An expression that evaluates to the minimum value allowed for the
     * constraint target.</p>
     *
     * @return an expression representing the minimum value.
     */
    ComparableExpression<?, V> lowerBound();

    /**
     * <p>An expression that evaluates to the maximum value allowed for the
     * constraint target.</p>
     *
     * @return an expression representing the maximum value.
     */
    ComparableExpression<?, V> upperBound();
}
