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

import java.util.List;

import jakarta.data.expression.Expression;
import jakarta.data.expression.TextExpression;

/**
 * <p>An {@linkplain Expression expression} that represents application of a function
 * to zero or more expressions supplied as {@link #arguments()}.</p>
 *
 * <p>Wherever reasonable, a subtype that more preciesly fits the function
 * result type, such as {@link NumericFunctionExpression} or
 * {@link TextFunctionExpression}, should be used instead.</p>
 *
 * @param <T> entity type.
 * @param <V> result type of the function.
 * @since 1.1
 */
public interface FunctionExpression<T, V> extends Expression<T, V> {
    /**
     * <p>The name of the function.</p>
     *
     * <p>Valid function names are defined by constants in a subtype of this
     * class or in vendor documentation. Function names should aim to match
     * the names of equivalent functions in the query language wherever
     * possible.</p>
     *
     * @return the name of the function.
     */
    String name();

    /**
     * <p>An ordered list of inputs to the function.</p>
     *
     * <p>Valid function names and behavior are documented by the function name
     * constants (such as {@link TextFunctionExpression#CONCAT}) in subtypes of
     * this class or in vendor documentation. The respective documentation
     * is also responsible for defining the function arguments, including the
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
    List<? extends Expression<? super T, ?>> arguments();
}
