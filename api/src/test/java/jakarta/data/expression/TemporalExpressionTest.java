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
package jakarta.data.expression;

import jakarta.data.constraint.Between;
import jakarta.data.constraint.LessThan;
import jakarta.data.expression.function.CurrentDate;
import jakarta.data.expression.literal.TemporalLiteral;
import jakarta.data.mock.entity.Book;
import jakarta.data.mock.entity._Book;
import jakarta.data.restrict.BasicRestriction;
import jakarta.data.restrict.Restriction;

import java.time.LocalDate;
import java.time.Month;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class TemporalExpressionTest {

    @Test
    void shouldRestrictLessThanCurrentDate() {
        TemporalExpression<Object, LocalDate> today =
            TemporalExpression.localDate();

        Restriction<Book> beforeCurrentDate =
            _Book.publicationDate.lessThan(today);

        @SuppressWarnings("unchecked")
        BasicRestriction<Book, LocalDate> restriction =
            (BasicRestriction<Book, LocalDate>) beforeCurrentDate;

        TemporalExpression<Book, LocalDate> lessThanExpression =
                (TemporalExpression<Book, LocalDate>) restriction.expression();

        LessThan<LocalDate> lessThan =
            (LessThan<LocalDate>) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(lessThanExpression)
                .isEqualTo(_Book.publicationDate);

            soft.assertThat(lessThan.bound())
                .isInstanceOf(CurrentDate.class);

            soft.assertThat(lessThan.bound())
                .isEqualTo(today);
        });
    }

    @Test
    void shouldRestrictLocalDateBetween() {

        Restriction<Book> publishedInApril2025 =
            _Book.publicationDate.between(LocalDate.of(2025, Month.APRIL, 1),
                                          LocalDate.of(2025, Month.APRIL, 30));

        @SuppressWarnings("unchecked")
        BasicRestriction<Book, LocalDate> restriction =
            (BasicRestriction<Book, LocalDate>) publishedInApril2025;

        Between<LocalDate> constraint =
            (Between<LocalDate>) restriction.constraint();

        TemporalLiteral<?> lowerLiteral =
            (TemporalLiteral<?>) constraint.lowerBound();

        TemporalLiteral<?> upperLiteral =
            (TemporalLiteral<?>) constraint.upperBound();

        TemporalExpression<Book, LocalDate> betweenExpression =
            (TemporalExpression<Book, LocalDate>) restriction.expression();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(betweenExpression)
                .isEqualTo(_Book.publicationDate);

            soft.assertThat(lowerLiteral.value())
                .isEqualTo(LocalDate.of(2025, Month.APRIL, 1));

            soft.assertThat(upperLiteral.value())
                .isEqualTo(LocalDate.of(2025, Month.APRIL, 30));
        });
    }
}
