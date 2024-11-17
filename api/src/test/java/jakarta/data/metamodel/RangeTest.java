/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

import jakarta.data.Operator;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RangeTest {


    @Test
    void shouldCreateInclusiveBetweenRange() {
        Range<LocalDate> range = Range.between("publicationDate", LocalDate.of(2020, 1, 1), LocalDate.of(2023, 1, 1));

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(range.field()).isEqualTo("publicationDate");
            soft.assertThat(range.operator()).isEqualTo(Operator.BETWEEN);
            soft.assertThat(range.value()).isEqualTo(List.of(LocalDate.of(2020, 1, 1), LocalDate.of(2023, 1, 1)));
        });
    }

    @Test
    void shouldCreateInclusiveFromRange() {
        Range<Double> range = Range.from("rating", 4.0);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(range.field()).isEqualTo("rating");
            soft.assertThat(range.operator()).isEqualTo(Operator.GREATER_THAN_EQUAL);
            soft.assertThat(range.value()).isEqualTo(4.0);
        });
    }

    @Test
    void shouldCreateInclusiveToRange() {
        Range<Integer> range = Range.to("price", 100);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(range.field()).isEqualTo("price");
            soft.assertThat(range.operator()).isEqualTo(Operator.LESS_THAN_EQUAL);
            soft.assertThat(range.value()).isEqualTo(100);
        });
    }

    @Test
    void shouldCreateExclusiveAboveRange() {
        Range<Double> range = Range.above("rating", 4.0);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(range.field()).isEqualTo("rating");
            soft.assertThat(range.operator()).isEqualTo(Operator.GREATER_THAN);
            soft.assertThat(range.value()).isEqualTo(4.0);
        });
    }

    @Test
    void shouldCreateExclusiveBelowRange() {
        Range<Double> range = Range.below("price", 200.0);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(range.field()).isEqualTo("price");
            soft.assertThat(range.operator()).isEqualTo(Operator.LESS_THAN);
            soft.assertThat(range.value()).isEqualTo(200.0);
        });
    }

    @Test
    void shouldHandleNullLowerBoundForToRange() {
        Range<Double> range = Range.to("price", 200.0);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(range.field()).isEqualTo("price");
            soft.assertThat(range.operator()).isEqualTo(Operator.LESS_THAN_EQUAL);
            soft.assertThat(range.lowerBound()).isNull();
            soft.assertThat(range.upperBound()).isEqualTo(200.0);
        });
    }

    @Test
    void shouldHandleNullUpperBoundForFromRange() {
        Range<Double> range = Range.from("price", 50.0);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(range.field()).isEqualTo("price");
            soft.assertThat(range.operator()).isEqualTo(Operator.GREATER_THAN_EQUAL);
            soft.assertThat(range.lowerBound()).isEqualTo(50.0);
            soft.assertThat(range.upperBound()).isNull();
        });
    }

    @Test
    void shouldHandleBothBoundsForBetweenRange() {
        Range<Double> range = Range.between("price", 50.0, 200.0);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(range.field()).isEqualTo("price");
            soft.assertThat(range.operator()).isEqualTo(Operator.BETWEEN);
            soft.assertThat(range.lowerBound()).isEqualTo(50.0);
            soft.assertThat(range.upperBound()).isEqualTo(200.0);
        });
    }

    @Test
    void shouldHandleOpenRangeForAbove() {
        Range<Integer> range = Range.above("age", 18);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(range.field()).isEqualTo("age");
            soft.assertThat(range.operator()).isEqualTo(Operator.GREATER_THAN);
            soft.assertThat(range.lowerBound()).isEqualTo(18);
            soft.assertThat(range.upperBound()).isNull();
            soft.assertThat(range.open()).isTrue();
        });
    }

    @Test
    void shouldHandleOpenRangeForBelow() {
        Range<Integer> range = Range.below("age", 65);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(range.field()).isEqualTo("age");
            soft.assertThat(range.operator()).isEqualTo(Operator.LESS_THAN);
            soft.assertThat(range.lowerBound()).isNull();
            soft.assertThat(range.upperBound()).isEqualTo(65);
            soft.assertThat(range.open()).isTrue();
        });
    }
}
