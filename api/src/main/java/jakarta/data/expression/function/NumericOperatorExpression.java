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
package jakarta.data.expression.function;

import jakarta.data.expression.Expression;
import jakarta.data.expression.NumericExpression;

import jakarta.data.expression.literal.NumericLiteral;
import jakarta.data.messages.Messages;

public interface NumericOperatorExpression<T, N extends Number & Comparable<N>>
        extends NumericExpression<T, N> {
    enum Operator {
        PLUS, MINUS, TIMES, DIVIDE
    }

    Operator operator();

    Expression<? super T, N> left();

    Expression<? super T, N> right();

    static <T, N extends Number & Comparable<N>> NumericOperatorExpression<T, N> of(
            Operator operator,
            NumericExpression<T, N> left,
            N right) {
        if (right == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "right"));
        }

        return new NumericOperatorExpressionRecord<>(operator, left, NumericLiteral.of(right));
    }

    static <T, N extends Number & Comparable<N>> NumericOperatorExpression<T, N> of(
            Operator operator,
            NumericExpression<T, N> left,
            NumericExpression<T, N> right) {
        return new NumericOperatorExpressionRecord<>(operator, left, right);
    }
}
