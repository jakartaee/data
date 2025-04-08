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

import jakarta.data.metamodel.NumericExpression;
import jakarta.data.metamodel.constraint.*;
import jakarta.data.metamodel.restrict.ExpressionRestriction;
import jakarta.data.metamodel.restrict.Restriction;
import jakarta.data.mock.entity.Book;
import jakarta.data.mock.entity._Book;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class NumericExpressionTest {

    @Test
    void shouldCompareWithNumericAttribute() {
        Restriction<Book> averageChapterAtLeastAsLongAsNumChapters =
                _Book.numPages.divide(_Book.numChapters)
                        .greaterThanEqual(_Book.numChapters);

        @SuppressWarnings("unchecked")
        ExpressionRestriction<Book, NumericExpression<Book, Integer>, Integer> restriction =
                (ExpressionRestriction<Book, NumericExpression<Book, Integer>, Integer>)
                        averageChapterAtLeastAsLongAsNumChapters;

        GreaterThanOrEqual<NumericExpression<Book, Integer>> gteNumChapters =
                (GreaterThanOrEqual<NumericExpression<Book, Integer>>)
                        restriction.constraint();

        NumericOperatorExpression<Book, Integer> divide =
                (NumericOperatorExpression<Book, Integer>) restriction.expression();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(gteNumChapters.bound())
                .isEqualTo(_Book.numChapters);

            soft.assertThat(divide.left())
                .isEqualTo(_Book.numPages);

            soft.assertThat(divide.operator())
                .isEqualTo(NumericOperatorExpression.Operator.DIVIDE);

            soft.assertThat(divide.right())
                .isEqualTo(_Book.numChapters);
        });
    }

    @Test
    void shouldCompareWithNumericFunctionExpression() {
        Restriction<Book> fewerChaptersThanCharsInTitle =
                _Book.numChapters.lessThan(
                        _Book.title.length());

        @SuppressWarnings("unchecked")
        ExpressionRestriction<Book, NumericExpression<Book, Integer>, Integer> restriction =
                (ExpressionRestriction<Book, NumericExpression<Book, Integer>, Integer>)
                        fewerChaptersThanCharsInTitle;

        LessThan<NumericExpression<Book, Integer>> lessThanLength =
                (LessThan<NumericExpression<Book, Integer>>) restriction.constraint();

        NumericFunctionExpression<Book, Integer> lengthExpression =
                (NumericFunctionExpression<Book, Integer>) lessThanLength.bound();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression())
                .isEqualTo(_Book.numChapters);

            soft.assertThat(lengthExpression.name())
                .isEqualTo(NumericFunctionExpression.LENGTH);

            soft.assertThat(lengthExpression.arguments().get(0))
                .isEqualTo(_Book.title);
        });
    }
}
