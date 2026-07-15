/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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


import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CurrentDate")
class CurrentDateTest {

    @Nested
    @DisplayName("When creating the current date expression")
    class WhenCreatingCurrentDateExpression {

        @Test
        @DisplayName("should create a current date expression")
        void shouldCreateCurrentDateExpression() {
            // when
            CurrentDate<TestEntity> expression = CurrentDate.now();

            // then
            assertThat(expression).isNotNull();
        }

        @Test
        @DisplayName("should expose LocalDate as the expression type")
        void shouldExposeLocalDateAsExpressionType() {
            CurrentDate<TestEntity> expression = CurrentDate.now();
            var type = expression.type();
            assertThat(type).isEqualTo(LocalDate.class);
        }

        @Test
        @DisplayName("should represent the expression as LOCAL DATE")
        void shouldRepresentExpressionAsLocalDate() {
            CurrentDate<TestEntity> expression = CurrentDate.now();
            String representation = expression.toString();
            assertThat(representation).isEqualTo("LOCAL DATE");
        }

        @Test
        @DisplayName("should create an independent expression for each invocation")
        void shouldCreateIndependentExpressionForEachInvocation() {
            CurrentDate<TestEntity> firstExpression = CurrentDate.now();
            CurrentDate<TestEntity> secondExpression = CurrentDate.now();
            assertThat(firstExpression).isNotSameAs(secondExpression);
        }
    }
    private static final class TestEntity {
    }
}