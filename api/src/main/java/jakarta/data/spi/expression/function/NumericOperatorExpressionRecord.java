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

import java.math.BigDecimal;
import java.math.BigInteger;

record NumericOperatorExpressionRecord<T, N extends Number & Comparable<N>>
        (Operator operator, NumericExpression<? super T, N> left,
         NumericExpression<? super T, N> right)
        implements NumericOperatorExpression<T, N> {

    NumericOperatorExpressionRecord {
        if (operator == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "operator"));
        }

        if (left == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "left"));
        }

        if (right == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "right"));
        }

        if (operator == Operator.DIVIDE
                && right instanceof NumericLiteral<?> l
                && isZero(l.value())) {
            throw new IllegalArgumentException(
                    Messages.get("005.zero.not.allowed", "divisor"));
        }
    }

    @Override
    public Class<? extends N> type() {
        return left.type();
    }

    /**
     * Internal method that determines if a number is equal to 0.
     *
     * @param number a Number that is also Comparable.
     * @return true if the number is equal to 0. Otherwise false.
     */
    private static boolean isZero(Number number) {
        if (number instanceof Integer i)
            return i == 0;
        if (number instanceof Long l)
            return l == 0L;
        if (number instanceof Double d)
            return 0 == Double.compare(d, 0.0d);
        if (number instanceof Float f)
            return 0 == Float.compare(f, 0.0f);
        if (number instanceof BigDecimal bd)
            return 0 == bd.compareTo(BigDecimal.ZERO);
        if (number instanceof BigInteger bi)
            return 0 == bi.compareTo(BigInteger.ZERO);
        if (number instanceof Short s)
            return s == (short) 0;
        if (number instanceof Byte b)
            return b == (byte) 0;

        throw new IllegalArgumentException(
                Messages.get("009.unknown.number.type",
                             number.getClass().getName()));
    }

    @Override
    public String toString() {
        char symbol = switch (operator) {
            case PLUS   -> '+';
            case MINUS  -> '-';
            case TIMES  -> '*';
            case DIVIDE -> '/';
        };

        String leftString = left.toString();
        String rightString = right.toString();

        StringBuilder expression =
                new StringBuilder(leftString.length() + rightString.length() + 7);

        if (left instanceof NumericOperatorExpression) {
            expression.append('(').append(leftString).append(')');
        } else {
            expression.append(leftString);
        }

        expression.append(' ').append(symbol).append(' ');

        if (right instanceof NumericOperatorExpression) {
            expression.append('(').append(rightString).append(')');
        } else {
            expression.append(rightString);
        }

        return expression.toString();
    }
}
