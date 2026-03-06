/*
 * Copyright (c) 2025,2026 Contributors to the Eclipse Foundation
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
package jakarta.data.spi.expression.function;

import jakarta.data.expression.NumericExpression;
import jakarta.data.messages.Messages;
import jakarta.data.spi.expression.literal.NumericLiteral;

/**
 * <p>A {@linkplain NumericExpression numeric expression} that represents
 * an operation, such as the {@linkplain Operator#PLUS addition} or
 * {@linkplain Operator#TIMES multiplication} of two numeric expressions.</p>
 *
 * @param <T> entity type.
 * @param <N> result type of the numeric expression.
 * @since 1.1
 */
public interface NumericOperatorExpression<T, N extends Number & Comparable<N>>
        extends NumericExpression<T, N> {
    /**
     * <p>Arithmetic operations that can be performed by a
     * {@link NumericOperatorExpression}.</p>
     */
    enum Operator {
        /**
         * <p>The {@code PLUS} operator computes the sum of the
         * {@link NumericOperatorExpression#left()} and
         * {@link NumericOperatorExpression#right()} expressions.</p>
         */
        PLUS,
        /**
         * <p>The {@code MINUS} operator computes the difference of the
         * {@link NumericOperatorExpression#left()} expression minus the
         * {@link NumericOperatorExpression#right()} expression.
         * </p>
         */
        MINUS,
        /**
         * <p>The {@code TIMES} operater computes the product of the
         * {@link NumericOperatorExpression#left()} and
         * {@link NumericOperatorExpression#right()} expressions.</p>
         */
        TIMES,
        /**
         * <p>The {@code DIVIDE} operator computes the quotient of the
         * {@link NumericOperatorExpression#left()} expression divided by the
         * {@link NumericOperatorExpression#right()} expression.</p>
         */
        DIVIDE
    }

    /**
     * <p>The numeric expression to which the arithmetic {@link #operator()}
     * is applied.</p>
     *
     * <p>For example, the left expression is the <b>minuend</b> when the
     * operation is {@linkplain Operator#MINUS subtraction},</p>
     *
     * <pre>difference = left() - right()</pre>
     *
     * <p>The left expression is the <b>dividend</b> when the operation is
     * {@linkplain Operator#DIVIDE division},</p>
     *
     * <pre>quotient = left() / right()</pre>
     *
     * @return the numeric expression to which the operator is applied.
     */
    NumericExpression<? super T, N> left();

    /**
     * <p>The type of arithmetic {@linkplain Operator operation} represnted by
     * this expression.</p>
     *
     * @return one of the enumerated {@link Operator Operator} values.
     */
    Operator operator();

    /**
     * <p>The numeric expression applied to the {@link left()} expression by
     * the arithmetic {@link #operator()}.</p>
     *
     * <p>For example, the right expression is the <b>subtrahend</b> when the
     * operation is {@linkplain Operator#MINUS subtraction},</p>
     *
     * <pre>difference = left() - right()</pre>
     *
     * <p>The right expression is the <b>divisor</b> when the operation is
     * {@linkplain Operator#DIVIDE division},</p>
     *
     * <pre>quotient = left() / right()</pre>
     *
     * @return the numeric expression applied to the {@code left()} expression
     *         by the arithmetic operator.
     */
    NumericExpression<? super T, N> right();

    /**
     * <p>Creates a {@code NumericOperatorExpression} that represents the given
     * arithmetic {@code operator} applying the value represented by the
     * {@code right} expression to the given {@code left} value.</p>
     *
     * @param <T>      entity type.
     * @param <N>      type of the {@code left} value, {@code right}
     *                 expression, and result expression.
     * @param operator one of the enumerated {@link Operator Operator} values.
     * @param left     value to which the {@code operator} is applied.
     * @param right    expression that represents the value applied by the
     *                 {@code operator} to the {@code left} value.
     * @return a numeric expression representing the result of the operation.
     * @throws NullPointerException if any of the method arguments are
     *                              {@code null}.
     */
    static <T, N extends Number & Comparable<N>> NumericOperatorExpression<T, N> of(
            Operator operator,
            N left,
            NumericExpression<T, N> right) {
        if (left == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "left"));
        }

        return new NumericOperatorExpressionRecord<>(operator, NumericLiteral.of(right.type(), left), right);
    }

    /**
     * <p>Creates a {@code NumericOperatorExpression} that represents the given
     * arithmetic {@code operator} applying the given {@code right} value to
     * the value represented by the {@code left} expression.</p>
     *
     * @param <T>      entity type.
     * @param <N>      type of the {@code left} expression, {@code right}
     *                 value, and result expression.
     * @param operator one of the enumerated {@link Operator Operator} values.
     * @param left     expression to which the {@code operator} is applied.
     * @param right    value applied by the {@code operator} to the
     *                 {@code left} expression.
     * @return a numeric expression representing the result of the operation.
     * @throws NullPointerException if any of the method arguments are
     *                              {@code null}.
     */
    static <T, N extends Number & Comparable<N>> NumericOperatorExpression<T, N> of(
            Operator operator,
            NumericExpression<T, N> left,
            N right) {
        if (right == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "right"));
        }

        return new NumericOperatorExpressionRecord<>(operator, left, NumericLiteral.of(left.type(), right));
    }

    /**
     * <p>Creates a {@code NumericOperatorExpression} that represents the given
     * arithmetic {@code operator} applying the value represented by the
     * {@code right} expression to the value represented by the {@code left}
     * expression.</p>
     *
     * @param <T>      entity type.
     * @param <N>      type of the {@code left} expression, {@code right}
     *                 expression, and result expression.
     * @param operator one of the enumerated {@link Operator Operator} values.
     * @param left     expression to which the {@code operator} is applied.
     * @param right    expression that represents the value applied by the
     *                 {@code operator} to the {@code left} expression.
     * @return a numeric expression representing the result of the operation.
     * @throws NullPointerException if any of the method arguments are
     *                              {@code null}.
     */
    static <T, N extends Number & Comparable<N>> NumericOperatorExpression<T, N> of(
            Operator operator,
            NumericExpression<T, N> left,
            NumericExpression<? super T, N> right) {
        return new NumericOperatorExpressionRecord<>(operator, left, right);
    }
}
