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
import jakarta.data.expression.TextExpression;
import jakarta.data.messages.Messages;
import jakarta.data.spi.expression.literal.NumericLiteral;
import jakarta.data.spi.expression.literal.StringLiteral;

import java.util.List;

/**
 * <p>An {@linkplain Expression expression} that represents applying a function
 * to one or more expressions that are supplied as {@link #arguments()} to
 * compute a {@link String} result.</p>
 *
 * @param <T> entity type.
 */
public interface TextFunctionExpression<T>
        extends FunctionExpression<T, String>, TextExpression<T> {

    /**
     * <p>Name of the function that computes the
     * {@linkplain TextExpression#append(TextExpression) concatenation} of the
     * {@link String} values to which two
     * {@linkplain TextExpression textual expressions} evaluate.</p>
     *
     * <p>This function accepts two textual expressions as its arguments:
     * <ol>
     * <li>The first argument is a textual expression that evaluates to a
     * {@link String} value that becomes the first part of the concatenated
     * text.</li>
     * <li>The second argument is a textual expression that evaluates to a
     * {@link String} value that becomes the second part of the concatenated
     * text.</li>
     * </ol>
     * </p>
     *
     * <p>The result of the {@code CONCAT} function is a textual expression
     * with the same entity type as the arguments. The result represents the
     * concatenation of the values to which the first and second expression
     * arguments evaluate.</p>
     */
    String CONCAT = "concat";

    /**
     * <p>Name of the function that computes the
     * {@linkplain TextExpression#left(int) beginning (leftmost) characters}
     * of the {@link String} value to which a
     * {@linkplain TextExpression textual expression} evaluates.</p>
     *
     * <p>This function accepts two arguments:
     * <ol>
     * <li>The first argument is a textual expression that evaluates to a
     * {@link String} value.</li>
     * <li>The second argument is a {@link NumericLiteral} that evaluates to
     * the {@code int} value indicating the number of characters to obtain
     * from the beginning of the {@link String}.</li>
     * </ol>
     * </p>
     *
     * <p>The result of the {@code LEFT} function is a textual expression
     * with the same entity type as the arguments. The result represents the
     * {@link String} formed by copying a given length of characters from the
     * beginning of the {@link String} to which the first expression argument
     * evaluates.</p>
     */
    String LEFT = "left";

    /**
     * <p>Name of the function that computes the
     * {@linkplain TextExpression#right(int) ending (rightmost) characters}
     * of the {@link String} value to which a 
     * {@linkplain TextExpression textual expression} evaluates.</p>
     *
     * <p>This function accepts two arguments:
     * <ol>
     * <li>The first argument is a textual expression that evaluates to a
     * {@link String} value.</li>
     * <li>The second argument is a {@link NumericLiteral} that evaluates to
     * the {@code int} value indicating the number of characters to obtain
     * from the end of the {@link String}.</li>
     * </ol>
     * </p>
     *
     * <p>The result of the {@code RIGHT} function is a textual expression
     * with the same entity type as the arguments. The result represents the
     * {@link String} formed by copying a given length of characters from the
     * end of the {@link String} to which the first expression argument
     * evaluates.</p>
     */
    String RIGHT = "right";

    /**
     * <p>Name of the function that computes the
     * {@linkplain TextExpression#lower() lower case} form of the
     * {@link String} value to which a
     * {@linkplain TextExpression textual expression} evaluates.</p>
     *
     * <p>This function accepts a textual expression as its only argument.</p>
     *
     * <p>The result of the {@code LOWER} function is a textual expression
     * with the same entity type as the argument. The result represents the
     * lower case form of the {@link String} value to which the expression
     * argument evaluates.</p>
     */
    String LOWER = "lower";

    /**
     * <p>Name of the function that computes the
     * {@linkplain TextExpression#upper() upper case} form of the
     * {@link String} value to which a
     * {@linkplain TextExpression textual expression} evaluates.</p>
     *
     * <p>This function accepts a textual expression as its only argument.</p>
     *
     * <p>The result of the {@code UPPER} function is a textual expression
     * with the same entity type as the argument. The result represents the
     * upper case form of the {@link String} value to which the expression
     * argument evaluates.</p>
     */
    String UPPER = "upper";

    /**
     * Creates a {@code TextFunctionExpression} to represent a function with
     * the given {@code name} that accepts the given textual {@code expression}
     * as input.
     *
     * @param <T>        entity type.
     * @param name       a function name constant (such as {@link #LOWER})
     *                   defined in this class or a function name constant
     *                   from a vendor extension.
     * @param expression the only function argument, in the form of an
     *                   expression that evaluates to a {@link String} value.
     * @return a {@code TextFunctionExpression} representing the function.
     */
    static <T> TextFunctionExpression<T> of(
            String name,
            TextExpression<? super T> expression) {

        Messages.requireNonNull(expression, "expression");

        return new TextFunctionExpressionRecord<>(name, List.of(expression));
    }

    /**
     * Creates a {@code TextFunctionExpression} to represent a function with
     * the given {@code name} that accepts a textual expression ({@code left})
     * and a {@link String} value ({@code right}) as input.
     *
     * @param <T>   entity type.
     * @param name  a function name constant (such as {@link #CONCAT})
     *              defined in this class or a function name constant
     *              from a vendor extension.
     * @param left  the function's first argument, in the form of an expression
     *              that evaluates to a {@link String} value.
     * @param right the function's second argument.
     * @return a {@code TextFunctionExpression} representing the function.
     */
    static <T> TextFunctionExpression<T> of(
            String name,
            TextExpression<? super T> left,
            String right) {

        Messages.requireNonNull(left, "left");
        Messages.requireNonNull(right, "right");

        return new TextFunctionExpressionRecord<>(
                name,
                List.of(left, StringLiteral.of(right)));
    }

    /**
     * Creates a {@code TextFunctionExpression} to represent a function with
     * the given {@code name} that accepts a {@link String} value
     * ({@code left}) and a textual expression ({@code right}) as input.
     *
     * @param <T>   entity type.
     * @param name  a function name constant (such as {@link #CONCAT})
     *              defined in this class or a function name constant
     *              from a vendor extension.
     * @param left  the function's first argument.
     * @param right the function's second argument, in the form of an
     *              expression that evaluates to a {@link String} value.
     * @return a {@code TextFunctionExpression} representing the function.
     */
    static <T> TextFunctionExpression<T> of(
            String name,
            String left,
            TextExpression<? super T> right) {

        Messages.requireNonNull(left, "left");
        Messages.requireNonNull(right, "right");

        return new TextFunctionExpressionRecord<>(
                name,
                List.of(StringLiteral.of(left), right));
    }

    /**
     * Creates a {@code TextFunctionExpression} to represent a function with
     * the given {@code name} that accepts textual expressions ({@code left}
     * and {@code right}) as input.
     *
     * @param <T>   entity type.
     * @param name  a function name constant (such as {@link #CONCAT})
     *              defined in this class or a function name constant
     *              from a vendor extension.
     * @param right the function's first argument, in the form of an
     *              expression that evaluates to a {@link String} value.
     * @param left  the function's second argument, in the form of an
     *              expression that evaluates to a {@link String} value.
     * @return a {@code TextFunctionExpression} representing the function.
     */
    static <T> TextFunctionExpression<T> of(
            String name,
            TextExpression<? super T> left,
            TextExpression<? super T> right) {

        Messages.requireNonNull(left, "left");
        Messages.requireNonNull(right, "right");

        return new TextFunctionExpressionRecord<>(name, List.of(left, right));
    }

    /**
     * Creates a {@code TextFunctionExpression} to represent a function with
     * the given {@code name} that accepts a textual {@code expression} and
     * a numeric ({@code literal}) as input.
     *
     * @param <T>        entity type.
     * @param name       a function name constant (such as {@link #LEFT})
     *                   defined in this class or a function name constant
     *                   from a vendor extension.
     * @param expression the function's first argument, in the form of an
     *                   expression that evaluates to a {@link String} value.
     * @param literal    the function's second argument, in the form of a
     *                   numeric value.
     * @return a {@code TextFunctionExpression} representing the function.
     */
    static <T> TextFunctionExpression<T> of(
            String name,
            TextExpression<? super T> expression,
            int literal) {

        Messages.requireNonNull(expression, "expression");

        return new TextFunctionExpressionRecord<>(
                name,
                List.of(expression,
                        NumericLiteral.of(Integer.class, literal)));
    }

    /**
     * <p>An ordered list of inputs to the function.</p>
     *
     * <p>A constant (such as {@link #CONCAT} or {@link #LEFT} defined in this
     * class or in a vendor extension) defines the function name and is also
     * responsible for documenting the function arguments, including the
     * meaning and data type of each argument and the order in which arguments
     * must be supplied. By convention, when a method of an {@link Expression}
     * subtype represents invocation of a function on a target expression, such
     * as {@link TextExpression#left(int)}, the first element of the argument
     * list should be the target expression, and subsequent elements should
     * be the method arguments, in the same order, if present.</p>
     *
     * @return a list of expressions that represent the arguments to the
     *         function.
     */
    @Override
    List<? extends ComparableExpression<? super T, ?>> arguments();
}
