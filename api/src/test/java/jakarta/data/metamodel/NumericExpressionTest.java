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


import jakarta.data.expression.function.NumericCast;
import jakarta.data.expression.function.NumericFunctionExpression;
import jakarta.data.expression.function.NumericOperatorExpression;
import jakarta.data.expression.literal.Literal;
import jakarta.data.metamodel.NumericAttribute;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

class NumericExpressionTest {
    static class Invoice {
        Integer amount;
    }

    // Static metamodel
    interface _Invoice {
        String AMOUNT = "amount";

        NumericAttribute<Invoice, Integer> amount = NumericAttribute.of(Invoice.class, AMOUNT, Integer.class);
    }

    @Test
    @DisplayName("should create abs function")
    void shouldCreateAbsFunction() {
        var expression = (NumericFunctionExpression<?, ?>) _Invoice.amount.abs();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(expression.name()).isEqualTo("abs");
            soft.assertThat(expression.arguments().get(0)).isEqualTo(_Invoice.amount);
        });
    }

    @Test
    @DisplayName("should create negated function")
    void shouldCreateNegatedFunction() {
        var expression = (NumericFunctionExpression<?, ?>) _Invoice.amount.negated();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(expression.name()).isEqualTo("neg");
            soft.assertThat(expression.arguments().get(0)).isEqualTo(_Invoice.amount);
        });
    }

    @Test
    @DisplayName("should create plus expression with literal")
    void shouldCreatePlusWithLiteral() {
        var expression = (NumericOperatorExpression<?, ?>) _Invoice.amount.plus(10);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(expression.operator()).isEqualTo(NumericOperatorExpression.Operator.PLUS);
            soft.assertThat(expression.left()).isEqualTo(_Invoice.amount);
            soft.assertThat(expression.right()).isEqualTo(Literal.of(10));
        });
    }

    @Test
    @DisplayName("should create minus expression with literal")
    void shouldCreateMinusWithLiteral() {
        var expression = (NumericOperatorExpression<?, ?>) _Invoice.amount.minus(5);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(expression.operator()).isEqualTo(NumericOperatorExpression.Operator.MINUS);
            soft.assertThat(expression.left()).isEqualTo(_Invoice.amount);
            soft.assertThat(expression.right()).isEqualTo(Literal.of(5));
        });
    }

    @Test
    @DisplayName("should create times expression with literal")
    void shouldCreateTimesWithLiteral() {
        var expression = (NumericOperatorExpression<?, ?>) _Invoice.amount.times(2);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(expression.operator()).isEqualTo(NumericOperatorExpression.Operator.TIMES);
            soft.assertThat(expression.left()).isEqualTo(_Invoice.amount);
            soft.assertThat(expression.right()).isEqualTo(Literal.of(2));
        });
    }

    @Test
    @DisplayName("should create divide expression with literal")
    void shouldCreateDivideWithLiteral() {
        var expression = (NumericOperatorExpression<?, ?>) _Invoice.amount.divide(4);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(expression.operator()).isEqualTo(NumericOperatorExpression.Operator.DIVIDE);
            soft.assertThat(expression.left()).isEqualTo(_Invoice.amount);
            soft.assertThat(expression.right()).isEqualTo(Literal.of(4));
        });
    }

    @Test
    @DisplayName("should create plus expression with another expression")
    void shouldCreatePlusWithExpression() {
        var expression = (NumericOperatorExpression<?, ?>) _Invoice.amount.plus(_Invoice.amount);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(expression.operator()).isEqualTo(NumericOperatorExpression.Operator.PLUS);
            soft.assertThat(expression.left()).isEqualTo(_Invoice.amount);
            soft.assertThat(expression.right()).isEqualTo(_Invoice.amount);
        });
    }

    @Test
    @DisplayName("should create minus expression with another expression")
    void shouldCreateMinusWithExpression() {
        var expression = (NumericOperatorExpression<?, ?>) _Invoice.amount.minus(_Invoice.amount);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(expression.operator()).isEqualTo(NumericOperatorExpression.Operator.MINUS);
            soft.assertThat(expression.left()).isEqualTo(_Invoice.amount);
            soft.assertThat(expression.right()).isEqualTo(_Invoice.amount);
        });
    }

    @Test
    @DisplayName("should create times expression with another expression")
    void shouldCreateTimesWithExpression() {
        var expression = (NumericOperatorExpression<?, ?>) _Invoice.amount.times(_Invoice.amount);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(expression.operator()).isEqualTo(NumericOperatorExpression.Operator.TIMES);
            soft.assertThat(expression.left()).isEqualTo(_Invoice.amount);
            soft.assertThat(expression.right()).isEqualTo(_Invoice.amount);
        });
    }

    @Test
    @DisplayName("should create divide expression with another expression")
    void shouldCreateDivideWithExpression() {
        var expression = (NumericOperatorExpression<?, ?>) _Invoice.amount.divide(_Invoice.amount);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(expression.operator()).isEqualTo(NumericOperatorExpression.Operator.DIVIDE);
            soft.assertThat(expression.left()).isEqualTo(_Invoice.amount);
            soft.assertThat(expression.right()).isEqualTo(_Invoice.amount);
        });
    }

    @Test
    @DisplayName("should cast to Long")
    void shouldCastToLong() {
        var cast = (NumericCast<?, ?>) _Invoice.amount.asLong();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(cast.type()).isEqualTo(Long.class);
        });
    }

    @Test
    @DisplayName("should cast to Double")
    void shouldCastToDouble() {
        var cast = (NumericCast<?, ?>) _Invoice.amount.asDouble();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(cast.type()).isEqualTo(Double.class);
        });
    }

    @Test
    @DisplayName("should cast to BigInteger")
    void shouldCastToBigInteger() {
        var cast = (NumericCast<?, ?>) _Invoice.amount.asBigInteger();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(cast.type()).isEqualTo(BigInteger.class);
        });
    }

    @Test
    @DisplayName("should cast to BigDecimal")
    void shouldCastToBigDecimal() {
        var cast = (NumericCast<?, ?>) _Invoice.amount.asBigDecimal();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(cast.type()).isEqualTo(BigDecimal.class);
        });
    }
}