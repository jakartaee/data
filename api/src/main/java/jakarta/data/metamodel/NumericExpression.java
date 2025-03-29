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
package jakarta.data.metamodel;

import jakarta.data.metamodel.expression.NumericCast;
import jakarta.data.metamodel.expression.NumericFunctionExpression;
import jakarta.data.metamodel.expression.NumericOperatorExpression;

import java.math.BigDecimal;
import java.math.BigInteger;

import static jakarta.data.metamodel.expression.NumericFunctionExpression.ABS;
import static jakarta.data.metamodel.expression.NumericFunctionExpression.NEG;
import static jakarta.data.metamodel.expression.NumericOperatorExpression.Operator.DIVIDE;
import static jakarta.data.metamodel.expression.NumericOperatorExpression.Operator.MINUS;
import static jakarta.data.metamodel.expression.NumericOperatorExpression.Operator.PLUS;
import static jakarta.data.metamodel.expression.NumericOperatorExpression.Operator.TIMES;

public interface NumericExpression<T, N extends Number & Comparable<N>>
        extends ComparableExpression<T, N> {

    default NumericExpression<T,N> abs() {
        return NumericFunctionExpression.of(ABS, this);
    }
    default NumericExpression<T,N> negated() {
        return NumericFunctionExpression.of(NEG, this);
    }

    default NumericExpression<T,N> plus(N other) {
        return NumericOperatorExpression.of(PLUS, this, other);
    }
    default NumericExpression<T,N> minus(N other) {
        return NumericOperatorExpression.of(MINUS, this, other);
    }
    default NumericExpression<T,N> times(N other) {
        return NumericOperatorExpression.of(TIMES, this, other);
    }
    default NumericExpression<T,N> divide(N other) {
        return NumericOperatorExpression.of(DIVIDE, this, other);
    }

    default NumericExpression<T,N> plus(NumericExpression<T,N> other) {
        return NumericOperatorExpression.of(PLUS, this, other);
    }
    default NumericExpression<T,N> minus(NumericExpression<T,N> other) {
        return NumericOperatorExpression.of(MINUS, this, other);
    }
    default NumericExpression<T,N> times(NumericExpression<T,N> other) {
        return NumericOperatorExpression.of(TIMES, this, other);
    }
    default NumericExpression<T,N> divide(NumericExpression<T,N> other) {
        return NumericOperatorExpression.of(DIVIDE, this, other);
    }

    default NumericExpression<T,Long> asLong() {
        return NumericCast.of(this, Long.class);
    }
    default NumericExpression<T,Double> asDouble() {
        return NumericCast.of(this, Double.class);
    }
    default NumericExpression<T,BigInteger> asBigInteger() {
        return NumericCast.of(this, BigInteger.class);
    }
    default NumericExpression<T,BigDecimal> asBigDecimal() {
        return NumericCast.of(this, BigDecimal.class);
    }
}
