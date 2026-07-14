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

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CurrentDateTime")
class CurrentDateTimeTest {

    @Nested
    @DisplayName("When creating the current date and time expression")
    class WhenCreatingCurrentDateTimeExpression {

        @Test
        @DisplayName("should create a current date and time expression")
        void shouldCreateCurrentDateTimeExpression() {
            var expression = CurrentDateTime.now();
            assertThat(expression).isNotNull();
        }

        @Test
        @DisplayName("should expose LocalDateTime as the expression type")
        void shouldExposeLocalDateTimeAsExpressionType() {
            var expression = CurrentDateTime.now();
            var type = expression.type();
            assertThat(type).isEqualTo(LocalDateTime.class);
        }

        @Test
        @DisplayName("should represent the expression as LOCAL DATETIME")
        void shouldRepresentExpressionAsLocalDateTime() {
            var expression = CurrentDateTime.now();
            String representation = expression.toString();
            assertThat(representation).isEqualTo("LOCAL DATETIME");
        }

        @Test
        @DisplayName("should create a new expression for each invocation")
        void shouldCreateNewExpressionForEachInvocation() {
            CurrentDateTime<TestEntity> firstExpression = CurrentDateTime.now();
            CurrentDateTime<TestEntity> secondExpression = CurrentDateTime.now();
            assertThat(firstExpression).isNotSameAs(secondExpression);
        }
    }

    private static final class TestEntity {
    }
}