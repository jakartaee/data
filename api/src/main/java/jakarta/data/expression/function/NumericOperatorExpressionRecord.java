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

import jakarta.data.expression.NumericExpression;
import jakarta.data.expression.literal.NumericLiteral;
import jakarta.data.messages.Messages;

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

        if (operator == Operator.DIVIDE &&
            right instanceof NumericLiteral l &&
            isZero((Number) l.value())) {
            throw new IllegalArgumentException(
                    Messages.get("005.zero.not.allowed"));
        }
    }

    /**
     * Internal method that determines if a number is equal to 0.
     *
     * @param number a Number that is also Comparable.
     * @return true if the number is equal to 0. Otherwise false.
     */
    private static boolean isZero(Number number) {
        return switch (number) {
            case Integer i    -> i.intValue() == 0;
            case Long l       -> l.longValue() == 0L;
            case Float f      -> Float.compare(f.floatValue(), 0.0f) == 0;
            case Double d     -> Double.compare(d.doubleValue(), 0.0d) == 0;
            case BigInteger i -> i.compareTo(BigInteger.ZERO) == 0;
            case BigDecimal d -> d.compareTo(BigDecimal.ZERO) == 0;
            case Byte b       -> b.byteValue() == (byte) 0;
            case Short s      -> s.shortValue() == (short) 0;
            default -> throw new IllegalArgumentException(
                    Messages.get("009.unknown.number.type",
                                 number.getClass().getName()));
        };
    }

    @Override
    public String toString() {
        char symbol = switch (operator) {
            case PLUS   -> '+';
            case MINUS  -> '-';
            case TIMES  -> '*';
            case DIVIDE -> '/';
            default     -> throw new IllegalStateException();
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
