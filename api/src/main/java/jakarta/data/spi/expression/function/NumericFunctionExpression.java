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

import jakarta.data.expression.ComparableExpression;
import jakarta.data.expression.Expression;
import jakarta.data.expression.NumericExpression;
import jakarta.data.expression.TextExpression;
import jakarta.data.messages.Messages;

import java.util.List;

/**
 * <p>An {@linkplain Expression expression} that represents applying a function
 * to one or more expressions that are supplied as {@link #arguments()} to
 * compute a {@linkplain NumericExpression numeric} result.</p>
 *
 * @param <T> entity type.
 * @param <N> result type of the function.
 */
public interface NumericFunctionExpression<T, N extends Number & Comparable<N>>
        extends FunctionExpression<T, N>, NumericExpression<T, N> {

    /**
     * <p>Name of the function that computes the
     * {@linkplain NumericExpression#abs() absolute value} of a
     * {@linkplain NumericExpression numeric expression}.</p>
     *
     * <p>This function accepts a numeric expression as its only argument.
     * </p>
     *
     * <p>The result of the {@code ABS} function is a numeric expression
     * of the same type as the argument. The resulting expression represents
     * a positive numeric value or 0.</p>
     */
    String ABS = "ABS"; // exactly matches function name from JCQL

    /**
     * <p>Name of the function that computes the
     * {@linkplain TextExpression#length() length} of a
     * {@linkplain TextExpression textual expression}.</p>
     *
     * <p>This function accepts a textual expression as its only argument.</p>
     *
     * <p>The result of the {@code LENGTH} function is a
     * {@linkplain NumericExpression numeric expression} in which the
     * expression type is {@link Integer} and the entity type is the same as
     * the entity type of the textual expression. The resulting expression
     * represents a positive numeric value or {@code 0}, indicating the number
     * of characters in the {@link String} value to which the textual
     * expression evaluates.</p>
     */
    String LENGTH = "LENGTH"; // exactly matches function name from JCQL

    /**
     * <p>Name of the function that computes the
     * {@linkplain NumericExpression#negated() negation} of a
     * {@linkplain NumericExpression numeric expression}. This is equivalent
     * to multiplication by {@code -1}, causing reversal of the sign on
     * positive numbers to become negative and on negative numbers to become
     * positive. The value {@code 0} remains {@code 0}.</p>
     *
     * <p>This function accepts a numeric expression as its only argument.
     * </p>
     *
     * <p>The result of the {@code NEG} function is a numeric expression
     * of the same type as the argument.</p>
     */
    String NEG = "-";

    /**
     * Creates a {@code NumericFunctionExpression} to represent a function with
     * the given {@code name} that accepts the given numeric {@code expression}
     * as input.
     *
     * @param <T> entity type.
     * @param <N> result type of the function.
     * @param name       a function name constant (such as {@link #ABS})
     *                   defined in this class or a function name constant
     *                   from a vendor extension.
     * @param returnType the type of the result to which the expression
     *                   evaluates.
     * @param expression an expression that evaluates to a numeric value.
     * @return a {@code NumericFunctionExpression} representing the function.
     */
    static <T, N extends Number & Comparable<N>>
        NumericFunctionExpression<T, N> of(
                String name,
                Class<? extends N> returnType,
                NumericExpression<? super T, N> expression) {

        Messages.requireNonNull(expression, "expression");

        return new NumericFunctionExpressionRecord<>(name,
                                                     returnType,
                                                     List.of(expression));
    }

    /**
     * Creates a {@code NumericFunctionExpression} to represent a function with
     * the given {@code name} that accepts the given textual {@code expression}
     * as input.
     *
     * @param <T> entity type.
     * @param <N> result type of the function.
     * @param name       a function name constant (such as {@link #LENGTH})
     *                   defined in this class or a function name constant
     *                   from a vendor extension.
     * @param returnType the type of the result to which the expression
     *                   evaluates.
     * @param expression an expression that evaluates to a {@link String}
     *                   value.
     * @return a {@code NumericFunctionExpression} representing the function.
     */
    static <T, N extends Number & Comparable<N>>
        NumericFunctionExpression<T, N> of(
                String name,
                Class<N> returnType,
                TextExpression<? super T> expression) {

        Messages.requireNonNull(expression, "expression");

        return new NumericFunctionExpressionRecord<>(name,
                                                     returnType,
                                                     List.of(expression));
    }

    /**
     * <p>An ordered list of inputs to the function.</p>
     *
     * <p>A constant (such as {@link #ABS} or {@link #LENGTH} defined in this
     * class or in a vendor extension) defines the function name and is also
     * responsible for documenting the function arguments, including the
     * meaning and data type of each argument and the order in which arguments
     * must be supplied. By convention, when a method of an {@link Expression}
     * subtype represents invocation of a function on a target expression, such
     * as {@link NumericExpression#abs()}, the first element of the argument
     * list should be the target expression, and subsequent elements should
     * be the method arguments, in the same order, if present.</p>
     *
     * @return a list of expressions that represent the arguments to the
     *         function.
     */
    @Override
    List<? extends ComparableExpression<? super T, ?>> arguments();
}
