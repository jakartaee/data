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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.metamodel.expression;

import jakarta.data.metamodel.Expression;
import jakarta.data.metamodel.constraint.*;
import jakarta.data.metamodel.restrict.BasicRestriction;
import jakarta.data.metamodel.restrict.ExpressionRestriction;
import jakarta.data.metamodel.restrict.Restriction;
import jakarta.data.mock.entity.Book;
import jakarta.data.mock.entity._Book;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class ExpressionTest {

    @Test
    void shouldCompareWithOtherEntityAttribute() {
        Restriction<Book> autobiographies =
                _Book.title.equalTo(_Book.author);

        @SuppressWarnings("unchecked")
        ExpressionRestriction<Book, Expression<Book, String>, String> restriction =
                (ExpressionRestriction<Book, Expression<Book, String>, String>)
                        autobiographies;

        EqualTo<Expression<Book, String>> equalToConstraint =
                (EqualTo<Expression<Book, String>>) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression())
                .isEqualTo(_Book.title);

            soft.assertThat(equalToConstraint.value())
                .isEqualTo(_Book.author);
        });
    }

    @Test
    void shouldRestrictLast2ofFirst10Chars() {

        Restriction<Book> titleWithEE =
            _Book.title.left(10).right(2).upper().equalTo("EE");

        BasicRestriction<Book, String> restriction =
            (BasicRestriction<Book, String>) titleWithEE;

        Constraint<String> constraint = restriction.constraint();

        TextFunctionExpression<Book> upperExpression =
            (TextFunctionExpression<Book>) restriction.expression();

        List<? extends Expression<? super Book, ?>> upperArgs = upperExpression.arguments();
        assertEquals(1, upperArgs.size());

        @SuppressWarnings("unchecked")
        TextFunctionExpression<Book> rightExpression =
            (TextFunctionExpression<Book>) upperArgs.get(0);

        List<? extends Expression<? super Book, ?>> rightArgs = rightExpression.arguments();
        assertEquals(2, rightArgs.size());

        assertEquals(true, rightArgs.get(1) instanceof NumericLiteral);

        @SuppressWarnings("unchecked")
        NumericLiteral<Book, Integer> rightArg1 =
            (NumericLiteral<Book, Integer>) rightArgs.get(1);

        @SuppressWarnings("unchecked")
        TextFunctionExpression<Book> leftExpression =
            (TextFunctionExpression<Book>) rightArgs.get(0);

        List<? extends Expression<? super Book, ?>> leftArgs = leftExpression.arguments();
        assertEquals(2, leftArgs.size());

        assertEquals(true, leftArgs.get(1) instanceof NumericLiteral);

        @SuppressWarnings("unchecked")
        NumericLiteral<Book, Integer> leftArg1 =
            (NumericLiteral<Book, Integer>) leftArgs.get(1);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(leftExpression.name())
                .isEqualTo(TextFunctionExpression.LEFT);

            soft.assertThat(leftArgs.get(0))
                .isEqualTo(_Book.title);

            soft.assertThat(leftArg1.value())
                .isEqualTo(10);

            soft.assertThat(rightExpression.name())
                .isEqualTo(TextFunctionExpression.RIGHT);

            soft.assertThat(rightArg1.value())
                .isEqualTo(2);

            soft.assertThat(upperExpression.name())
                .isEqualTo(TextFunctionExpression.UPPER);

            soft.assertThat(constraint)
                .isInstanceOf(EqualTo.class);

            soft.assertThat(((EqualTo<String>) constraint).value())
                .isEqualTo("EE");
        });
    }

    @Test
    void shouldRestrictLengthOfText() {

        Restriction<Book> titleUpTo50Chars = _Book.title.length().lessThanEqual(50);

        @SuppressWarnings("unchecked")
        BasicRestriction<Book, Integer> restriction =
            (BasicRestriction<Book, Integer>) titleUpTo50Chars;

        Constraint<Integer> constraint = restriction.constraint();

        NumericFunctionExpression<Book, Integer> lengthExpression =
            (NumericFunctionExpression<Book, Integer>) restriction.expression();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(lengthExpression.name())
                .isEqualTo(NumericFunctionExpression.LENGTH);

            soft.assertThat(lengthExpression.arguments().size())
                .isEqualTo(1);

            soft.assertThat(lengthExpression.arguments().get(0))
                .isEqualTo(_Book.title);

            soft.assertThat(constraint)
                .isInstanceOf(LessThanOrEqual.class);

            soft.assertThat(((LessThanOrEqual<Integer>) constraint).bound())
                .isEqualTo(50);
        });
    }
}
