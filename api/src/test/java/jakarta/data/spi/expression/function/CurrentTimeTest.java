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

import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CurrentTime")
class CurrentTimeTest {

    @Nested
    @DisplayName("When creating the current time expression")
    class WhenCreatingCurrentTimeExpression {

        @Test
        @DisplayName("should create a current time expression")
        void shouldCreateCurrentTimeExpression() {
            // when
            CurrentTime<TestEntity> expression = CurrentTime.now();

            // then
            assertThat(expression).isNotNull();
        }

        @Test
        @DisplayName("should expose LocalTime as the expression type")
        void shouldExposeLocalTimeAsExpressionType() {
            CurrentTime<TestEntity> expression = CurrentTime.now();
            var type = expression.type();
            assertThat(type).isEqualTo(LocalTime.class);
        }

        @Test
        @DisplayName("should represent the expression as LOCAL TIME")
        void shouldRepresentExpressionAsLocalTime() {
            CurrentTime<TestEntity> expression = CurrentTime.now();
            String representation = expression.toString();
            assertThat(representation).isEqualTo("LOCAL TIME");
        }

    private static final class TestEntity {
    }
}