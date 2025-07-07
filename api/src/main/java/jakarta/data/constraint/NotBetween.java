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
 * <p>A constraint that excludes values within a range.</p>
 *
 * <p>A parameter-based repository method can impose a constraint on an
 * entity attribute by defining a method parameter that is of type
 * {@code NotBetween} and is of the same type or a subtype of the entity
 * attribute. For example,</p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; byModelYear(&#64;By(_Car.YEAR) NotBetween&lt;Integer&gt; yearsToExclude);
 *
 * ...
 *
 * found = cars.byModelYear(NotBetween.bounds(2020, 2022));
 * </pre>
 *
 * <p>Repository methods can also accept {@code NotBetween} constraints at
 * run time in the form of a {@link Restriction} on a
 * {@link ComparableExpression}. For example,</p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; searchAll(Restriction&lt;Car&gt; restrict, Order&lt;Car&gt; sorts);
 *
 * ...
 *
 * found = cars.searchAll(_Car.year.notBetween(2019, 2021),
 *                        Order.by(_Car.year.desc(),
 *                                 _Car.price.desc(),
 *                                 _Car.vin.asc()));
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
public interface NotBetween<V extends Comparable<?>> extends Constraint<V> {

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * less than the given {@code lower} bound or greater than the given
     * {@code upper} bound. For example,</p>
     *
     * <pre>
     * found = cars.byModelYear(NotBetween.bounds(2022, 2024));
     * </pre>
     *
     * @param <V>   type of the entity attribute or a subtype or primitive
     *              wrapper type for the entity attribute.
     * @param lower the lower bound of the range to exclude.
     * @param upper the upper bound of the range to exclude.
     * @return a {@code NotBetween} constraint.
     * @throws NullPointerException if the lower or upper bound is {@code null}.
     */
    static <V extends Comparable<?>> NotBetween<V> bounds(V lower, V upper) {
        return new NotBetweenRecord<>(ComparableLiteral.of(lower),
                ComparableLiteral.of(upper));
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * less than the given {@code lower} bound or greater than the value to
     * which the given {@code upper} expression evaluates. For example,</p>
     *
     * <pre>
     * found = cars.byModelYear(NotBetween.bounds(2015,
     *                                            _Car.firstModelYear.plus(5)));
     * </pre>
     *
     * @param <V>   type of the entity attribute or a subtype or primitive
     *              wrapper type for the entity attribute.
     * @param lower the lower bound of the range to exclude.
     * @param upper an expression that evaluates to the upper bound of the
     *              range to exclude.
     * @return a {@code NotBetween} constraint.
     * @throws NullPointerException if lower or upper is {@code null}.
     */
    static <V extends Comparable<?>> NotBetween<V> bounds(
            V lower,
            ComparableExpression<?, V> upper) {
        return new NotBetweenRecord<>(ComparableLiteral.of(lower),
                upper);
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * less than the value to which the given {@code lower} expression
     * evaluates or greater than the given {@code upper} bound. For example,
     * </p>
     *
     * <pre>
     * found = cars.byModelYear(NotBetween.bounds(_Car.firstModelYear, 2022));
     * </pre>
     *
     * @param <V>   type of the entity attribute or a subtype or primitive
     *              wrapper type for the entity attribute.
     * @param lower an expression that evaluates to the lower bound of the
     *              range to exclude.
     * @param upper the upper bound of the range to exclude.
     * @return a {@code NotBetween} constraint.
     * @throws NullPointerException if lower or upper is {@code null}.
     */
    static <V extends Comparable<?>> NotBetween<V> bounds(
            ComparableExpression<?, V> lower,
            V upper) {
        return new NotBetweenRecord<>(lower,
                ComparableLiteral.of(upper));
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * less than the value to which the given {@code lower} expression
     * evaluates or greater than the value to which the given {@code upper}
     * expression evaluates. For example,</p>
     *
     * <pre>
     * found = cars.byModelYear(NotBetween.bounds(_Car.firstModelYear,
     *                                            _Car.firstModelYear.plus(2)));
     * </pre>
     *
     * @param <V>   type of the entity attribute or a subtype or primitive
     *              wrapper type for the entity attribute.
     * @param lower an expression that evaluates to the lower bound of the
     *              range to exclude.
     * @param upper an expression that evaluates to the upper bound of the
     *              range to exclude.
     * @return a {@code NotBetween} constraint.
     * @throws NullPointerException if lower or upper is {@code null}.
     */
    static <V extends Comparable<?>> NotBetween<V> bounds(
            ComparableExpression<?, V> lower,
            ComparableExpression<?, V> upper) {
        return new NotBetweenRecord<>(lower,
                upper);
    }

    /**
     * <p>An expression that evaluates to the minimum value excluded for the
     * constraint target.</p>
     *
     * @return an expression representing the minimum value excluded.
     */
    ComparableExpression<?, V> lowerBound();

    /**
     * <p>An expression that evaluates to the maximum value excluded for the
     * constraint target.</p>
     *
     * @return an expression representing the maximum value excluded.
     */
    ComparableExpression<?, V> upperBound();
}
