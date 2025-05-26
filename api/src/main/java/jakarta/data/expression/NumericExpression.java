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

import static jakarta.data.expression.function.NumericFunctionExpression.ABS;
import static jakarta.data.expression.function.NumericFunctionExpression.NEG;
import static jakarta.data.expression.function.NumericOperatorExpression.Operator.DIVIDE;
import static jakarta.data.expression.function.NumericOperatorExpression.Operator.MINUS;
import static jakarta.data.expression.function.NumericOperatorExpression.Operator.PLUS;
import static jakarta.data.expression.function.NumericOperatorExpression.Operator.TIMES;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.data.expression.function.NumericCast;
import jakarta.data.expression.function.NumericFunctionExpression;
import jakarta.data.expression.function.NumericOperatorExpression;
import jakarta.data.metamodel.Attribute;
import jakarta.data.metamodel.NumericAttribute;

/**
 * <p>An {@linkplain Expression expression} that evaluates to a
 * {@linkplain NumericAttribute numeric} typed value.</p>
 *
 * <p>The {@linkplain Attribute entity and static metamodel} for the code
 * examples within this class are shown in the {@link Attribute} Javadoc.
 * </p>
 *
 * @param <T> entity type.
 * @param <N> entity attribute type.
 * @since 1.1
 */
public interface NumericExpression<T, N extends Number & Comparable<N>>
        extends ComparableExpression<T, N> {

    /**
     * <p>Represents the absolute value function applied to the value to which
     * the current expression evaluates.</p>
     *
     * <p>Example:</p>
     * <pre>
     * within2YearsOf2024 = cars.search(make,
     *                                  model,
     *                                  _Car.year.minus(2024).abs().lessThanEqual(2));
     * </pre>
     *
     * @return an expression for the function that computes the absolute value.
     */
    default NumericExpression<T, N> abs() {
        return NumericFunctionExpression.of(ABS, this);
    }

    /**
     * <p>Represents the sign negation function applied to the value to which
     * the current expression evaluates. The sign negation function reverse the
     * sign (positive to negative or negative to positive) of the value. It has
     * no effect on the value {@code 0}.</p>
     *
     * <p>Example:</p>
     * <pre>
     * atLeast2YearsBeyondOriginalModelYear = cars.search(
     *         make,
     *         model,
     *         _Car.firstModelYear.minus(_Car.year).negated().greaterThanEqual(2));
     * </pre>
     *
     * @return an expression for the function that computes negation of value.
     */
    default NumericExpression<T, N> negated() {
        return NumericFunctionExpression.of(NEG, this);
    }

    /**
     * <p>Represents the addition function that computes the sum of the value
     * to which the current expression evaluates plus the given value.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(make,
     *                     model,
     *                     _Car.price.plus(fees).lessThan(30000));
     * </pre>
     *
     * @param value the value to add. Must not be {@code null}.
     * @return an expression for the function that computes the sum.
     * @throws NullPointerException if the supplied value is null.
     */
    default NumericExpression<T, N> plus(N value) {
        return NumericOperatorExpression.of(PLUS, this, value);
    }

    /**
     * <p>Represents the subtraction function that computes the difference of
     * the value to which the current expression evaluates minus the given
     * value.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(make,
     *                     model,
     *                     _Car.price.minus(discount).lessThanEqual(25000));
     * </pre>
     *
     * @param value the value to subtract. Must not be {@code null}.
     * @return an expression for the function that computes the difference.
     * @throws NullPointerException if the supplied value is null.
     */
    default NumericExpression<T, N> minus(N value) {
        return NumericOperatorExpression.of(MINUS, this, value);
    }

    /**
     * <p>Represents the multiplication function that computes the product of
     * the value to which the current expression evaluates times the given
     * factor.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(
     *         make,
     *         model,
     *         _Car.price.asDouble().times(1.0 + taxRate).lessThan(35000.0));
     * </pre>
     *
     * @param factor the value times which to multiply. Must not be
     *               {@code null}.
     * @return an expression for the function that computes the product.
     * @throws NullPointerException if the supplied factor value is null.
     */
    default NumericExpression<T, N> times(N factor) {
        return NumericOperatorExpression.of(TIMES, this, factor);
    }

    /**
     * <p>Represents the division function that computes the quotient of the
     * value to which the current expression evaluates divided by the given
     * divisor value.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(
     *         make,
     *         model,
     *         _Car.price.asDouble().divide(1.0 + discountRate).lessThan(27000.0));
     * </pre>
     *
     * @param divisor the value by which to divide. Must not be {@code 0} or
     *                {@code null}.
     * @return an expression for the function that computes the quotient.
     * @throws NullPointerException if the supplied divisor value is null.
     */
    default NumericExpression<T, N> divide(N divisor) {
        return NumericOperatorExpression.of(DIVIDE, this, divisor);
    }

    /**
     * <p>Represents the addition function that computes the sum of the values
     * to which the current expression and the given expression evaluate.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(make,
     *                     model,
     *                     NumericLiteral.of(fees).plus(_Car.price).lessThan(32000));
     * </pre>
     *
     * @param expression expression that evaluates to the value to add. Must
     *                   not be {@code null}.
     * @return an expression for the function that computes the sum.
     * @throws NullPointerException if the supplied value is null.
     */
    default NumericExpression<T, N> plus(
            NumericExpression<T, N> expression) {
        return NumericOperatorExpression.of(PLUS, this, expression);
    }

    /**
     * <p>Represents the subtraction function that computes the difference of
     * the value to which the current expression evaluates minus the value to
     * which the given expression evaluates.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(make,
     *                     model,
     *                     _Car.year.minus(_Car.firstModelYear).greaterThan(1));
     * </pre>
     *
     * @param expression expression that evaluates to the value to subtract.
     *                   Must not be {@code null}.
     * @return an expression for the function that computes the difference.
     * @throws NullPointerException if the supplied expression is null.
     */
    default NumericExpression<T, N> minus(
            NumericExpression<T, N> expression) {
        return NumericOperatorExpression.of(MINUS, this, expression);
    }

    /**
     * <p>Represents the multiplication function that computes the product of
     * the values to which the current expression and the given factor
     * expression evaluate.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(
     *         make,
     *         model,
     *         NumericLiteral.of(1.0 + taxRate).times(_Car.price).lessThan(40000.0));
     * </pre>
     *
     * @param factorExpression expression that evaluates to the value by which
     *                         to multiply. Must not be {@code null}.
     * @return an expression for the function that computes the product.
     * @throws NullPointerException if the supplied factor expression is null.
     */
    default NumericExpression<T, N> times(
            NumericExpression<T, N> factorExpression) {
        return NumericOperatorExpression.of(TIMES, this, factorExpression);
    }

    /**
     * <p>Represents the division function that computes the quotient of the
     * value to which the current expression evaluates divided by the value to
     * which the divisor expression evaluates.</p>
     *
     * <p>Example:</p>
     * <pre>
     * discountedByMoreThan10Percent = cars.search(
     *         make,
     *         model,
     *         NumericLiteral.of(discount).divide(_Car.price.asDouble()).greaterThan(0.1));
     * </pre>
     *
     * @param divisorExpression expression that evaluates to the value by which
     *                          to divide. Must not be {@code null}.
     * @return an expression for the function that computes the quotient.
     * @throws NullPointerException if the supplied divisor expression is null.
     */
    default NumericExpression<T, N> divide(
            NumericExpression<T, N> divisorExpression) {
        return NumericOperatorExpression.of(DIVIDE, this, divisorExpression);
    }

    /**
     * <p>Represents the cast function that converts the value to which the
     * current expression evaluates to {@link Long}.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(make,
     *                     model,
     *                     _Car.price.asLong().lessThan(36000L));
     * </pre>
     *
     * @return an expression for the function that casts to {@code Long}.
     */
    default NumericExpression<T, Long> asLong() {
        return NumericCast.of(this, Long.class);
    }

    /**
     * <p>Represents the cast function that converts the value to which the
     * current expression evaluates to {@link Double}.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(make,
     *                     model,
     *                     _Car.price.asDouble().lessThan(34000.0));
     * </pre>
     *
     * @return an expression for the function that casts to {@code Double}.
     */
    default NumericExpression<T, Double> asDouble() {
        return NumericCast.of(this, Double.class);
    }

    /**
     * <p>Represents the cast function that converts the value to which the
     * current expression evaluates to {@link BigInteger}.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(make,
     *                     model,
     *                     _Car.price.asBigInteger().lessThan(BigInteger.valueOf(50000L)));
     * </pre>
     *
     * @return an expression for the function that casts to {@code BigInteger}.
     */
    default NumericExpression<T, BigInteger> asBigInteger() {
        return NumericCast.of(this, BigInteger.class);
    }

    /**
     * <p>Represents the cast function that converts the value to which the
     * current expression evaluates to {@link BigDecimal}.</p>
     *
     * <p>Example:</p>
     * <pre>
     * found = cars.search(make,
     *                     model,
     *                     _Car.price.asBigDecimal().lessThan(BigDecimal.valueOf(45000L)));
     * </pre>
     *
     * @return an expression for the function that casts to {@code BigDecimal}.
     */
    default NumericExpression<T, BigDecimal> asBigDecimal() {
        return NumericCast.of(this, BigDecimal.class);
    }
}
