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

import jakarta.data.metamodel.TemporalExpression;
import jakarta.data.metamodel.constraint.*;
import jakarta.data.metamodel.restrict.BasicRestriction;
import jakarta.data.metamodel.restrict.Restriction;
import jakarta.data.mock.entity.Book;
import jakarta.data.mock.entity._Book;

import java.time.LocalDate;
import java.time.Month;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class TemporalExpressionTest {

    @Test
    void shouldRestrictLessThanCurrentDate() {
        TemporalExpression<Book, LocalDate> today =
            TemporalExpression.currentDate(Book.class);

        // TODO write the test once comparisons with expressions are added
        //Restriction<Book> beforeCurrentDate =
        //    _Book.publicationDate.lessThan(today);
        // ...
    }

    @Test
    void shouldRestrictLocalDateBetween() {

        Restriction<Book> publishedInApril2025 =
            _Book.publicationDate.between(LocalDate.of(2025, Month.APRIL, 1),
                                          LocalDate.of(2025, Month.APRIL, 30));

        @SuppressWarnings("unchecked")
        BasicRestriction<Book, LocalDate> restriction =
            (BasicRestriction<Book, LocalDate>) publishedInApril2025;

        Constraint<LocalDate> constraint = restriction.constraint();

        TemporalExpression<Book, LocalDate> betweenExpression =
            (TemporalExpression<Book, LocalDate>) restriction.expression();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute())
                .isEqualTo(_Book.PUBLICATIONDATE);

            soft.assertThat(betweenExpression)
                .isEqualTo(_Book.publicationDate);

            soft.assertThat(constraint)
                .isInstanceOf(Between.class);

            soft.assertThat(((Between<LocalDate>) constraint).lowerBound())
                .isEqualTo(LocalDate.of(2025, Month.APRIL, 1));

            soft.assertThat(((Between<LocalDate>) constraint).upperBound())
                .isEqualTo(LocalDate.of(2025, Month.APRIL, 30));
        });
    }
}
