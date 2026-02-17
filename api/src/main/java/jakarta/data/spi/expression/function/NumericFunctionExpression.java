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
package jakarta.data.spi.expression.function;

import jakarta.data.expression.ComparableExpression;
import jakarta.data.expression.NumericExpression;
import jakarta.data.expression.TextExpression;
import jakarta.data.messages.Messages;

import java.util.List;

public interface NumericFunctionExpression<T, N extends Number & Comparable<N>>
        extends FunctionExpression<T, N>, NumericExpression<T, N> {

    String ABS = "abs";
    String NEG = "-";
    String LENGTH = "length";

    static <T, N extends Number & Comparable<N>> NumericFunctionExpression<T, N>
    of(String name, Class<N> returnType, TextExpression<? super T> expression) {
        if (expression == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "expression"));
        }

        return new NumericFunctionExpressionRecord<>(name, returnType, List.of(expression));
    }

    static <T, N extends Number & Comparable<N>> NumericFunctionExpression<T, N>
    of(String name, Class<N> returnType, NumericExpression<? super T, N> expression) {
        if (expression == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "expression"));
        }

        return new NumericFunctionExpressionRecord<>(name, returnType, List.of(expression));
    }

    @Override
    List<? extends ComparableExpression<? super T, ?>> arguments();
}
