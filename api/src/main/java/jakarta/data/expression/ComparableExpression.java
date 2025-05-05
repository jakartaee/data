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
package jakarta.data.expression;

import jakarta.data.constraint.Between;
import jakarta.data.constraint.GreaterThan;
import jakarta.data.constraint.GreaterThanOrEqual;
import jakarta.data.constraint.LessThan;
import jakarta.data.constraint.LessThanOrEqual;
import jakarta.data.constraint.NotBetween;
import jakarta.data.metamodel.Attribute;
import jakarta.data.restrict.BasicRestriction;
import jakarta.data.restrict.Restriction;

/**
 * <p>An {@linkplain Expression expression} that evaluates to a comparable
 * result that can be sorted.</p>
 *
 * <p>A more specific subtype, such as {@link NumericExpression},
 * {@link TemporalExpression}, or {@link TextExpression}, should be used
 * instead wherever it is possible.</p>
 *
 * <p>The {@linkplain Attribute entity and static metamodel} for the code
 * examples within this class are shown in the {@link Attribute} Javadoc.
 * </p>
 *
 * @param <T> entity type.
 * @param <V> entity attribute type.
 * @since 1.1
 */
public interface ComparableExpression<T, V extends Comparable<?>>
        extends Expression<T, V> {

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value falling within the range between (and inclusive of) 
     * the specified values.
     * </p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(make, model, _Car.year.between(2022, 2025));
     * </pre>
     *
     * @param min minimum value against which to compare. Must not be
     *            {@code null}.
     * @param max maximum value against which to compare. Must not be
     *            {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the value is {@code null}.
     */
    default Restriction<T> between(V min, V max) {
        return BasicRestriction.of(this, Between.bounds(min, max));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value falling within the range between (and inclusive of) 
     * the values to which the given expressions evaluate.</p>
     *
     * <p>Example:</p>
     * <pre>
     * preOwnedButNotFirstOrSecondModelYear = cars.search(
     *         make,
     *         model,
     *         _Car.year.between(_Car.firstModelYear.plus(2),
     *                           NumericLiteral.of(Year.now().getValue() - 1)));
     * </pre>
     *
     * @param minExpression expression that evaluates to the lower bound
     *                      on the range. Must not be {@code null}.
     * @param maxExpression expression that evaluates to the upper bound
     *                      on the range. Must not be {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the expression is {@code null}.
     */
    default <U extends ComparableExpression<? super T, V>> Restriction<T> between(
            U minExpression,
            U maxExpression) {
        return BasicRestriction.of(
                this,
                Between.bounds(minExpression, maxExpression));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value greater than the given value.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(make, model, _Car.price.greaterThan(25000));
     * </pre>
     *
     * @param value value against which to compare. Must not be {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the value is {@code null}.
     */
    default Restriction<T> greaterThan(V value) {
        return BasicRestriction.of(this, GreaterThan.bound(value));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value greater than the value to which the given
     * expression evaluates.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(make,
     *                     model,
     *                     _Car.year.greaterThan(_Car.firstModelYear));
     * </pre>
     *
     * @param expression expression against which to compare. Must not be
     *                   {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the expression is {@code null}.
     */
    default Restriction<T> greaterThan(ComparableExpression<? super T, V> expression) {
        return BasicRestriction.of(this, GreaterThan.bound(expression));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value greater than or equal to the given
     * value.</p>
     *
     * <p>Example:</p>
     * <pre>
     * atLeast2024 = cars.search(make, model, _Car.year.greaterThanEqual(2024));
     * </pre>
     *
     * @param value value against which to compare. Must not be {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the value is {@code null}.
     */
    default Restriction<T> greaterThanEqual(V value) {
        return BasicRestriction.of(this, GreaterThanOrEqual.min(value));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value greater than or equal to the value to which the
     * given expression evaluates.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(make,
     *                     model,
     *                     _Car.year.greaterThanEqual(_Car.firstModelYear.plus(2)));
     * </pre>
     *
     * @param expression expression against which to compare. Must not be
     *                   {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the expression is {@code null}.
     */
    default Restriction<T> greaterThanEqual(ComparableExpression<? super T, V> expression) {
        return BasicRestriction.of(this, GreaterThanOrEqual.min(expression));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value smaller than the given value.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(make, model, _Car.price.lessThan(35000));
     * </pre>
     *
     * @param value value against which to compare. Must not be {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the value is {@code null}.
     */
    default Restriction<T> lessThan(V value) {
        return BasicRestriction.of(this, LessThan.bound(value));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value smaller than the value to which the given
     * expression evaluates.</p>
     *
     * <p>Example:</p>
     * <pre>
     * listedBeforeToday = cars.search(make,
     *                                 model,
     *                                 _Car.listed.lessThan(CurrentDate.now()));
     * </pre>
     *
     * @param expression expression against which to compare. Must not be
     *                   {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the expression is {@code null}.
     */
    default Restriction<T> lessThan(ComparableExpression<? super T, V> expression) {
        return BasicRestriction.of(this, LessThan.bound(expression));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value less than or equal to the given value.
     * </p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(make, model, _Car.price.lessThanEqual(25000));
     * </pre>
     *
     * @param value value against which to compare. Must not be {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the value is {@code null}.
     */
    default Restriction<T> lessThanEqual(V value) {
        return BasicRestriction.of(this, LessThanOrEqual.max(value));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value less than or equal to the value to which the
     * given expression evaluates.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(make,
     *                     model,
     *                     _Car.firstModelYear.lessThanEqual(_Car.year.minus(2)));
     * </pre>
     *
     * @param expression expression against which to compare. Must not be
     *                   {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the expression is {@code null}.
     */
    default Restriction<T> lessThanEqual(ComparableExpression<? super T, V> expression) {
        return BasicRestriction.of(this, LessThanOrEqual.max(expression));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value falling outside the range between given values.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(make, model, _Car.year.notBetween(2021, 2023));
     * </pre>
     *
     * @param min the lower bound on the range. Must not be {@code null}.
     * @param max the upper bound on the range. Must not be {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the value is {@code null}.
     */
    default Restriction<T> notBetween(V min, V max) {
        return BasicRestriction.of(this, NotBetween.bounds(min, max));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value falling outside the range between the values to 
     * which the given expressions evaluate.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(make,
     *                     model,
     *                     _Car.year.notBetween(_Car.firstModelYear,
     *                                          _Car.firstModelYear.plus(2)));
     * </pre>
     *
     * @param minExpression expression that evaluates to the lower bound
     *                      on the range. Must not be {@code null}.
     * @param maxExpression expression that evaluates to the lower bound
     *                      on the range. Must not be {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the expression is {@code null}.
     */
    default <U extends ComparableExpression<? super T, V>> Restriction<T> notBetween(
            U minExpression,
            U maxExpression) {
        return BasicRestriction.of(
                this,
                NotBetween.bounds(minExpression, maxExpression));
    }
}
