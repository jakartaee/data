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
package jakarta.data.spi.expression.literal;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LiteralRecordTest {
    // Static metamodel using LiteralRecord
    interface _SimpleEntity {
        Literal<String> name = new LiteralRecord<>("name");
        Literal<Integer> age = new LiteralRecord<>(42);
    }

    // Simple mock entity
    static class SimpleEntity {
        String name;
        Integer age;
    }

    @Test
    @DisplayName("should create LiteralRecord and return its value")
    void shouldCreateLiteralRecord() {
        Literal<String> literal =
                new LiteralRecord<>("literal-value");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(literal.value()).isEqualTo("literal-value");
        });
    }

    @Test
    @DisplayName("should support equals and hashCode for identical values")
    void shouldSupportEqualsAndHashCode() {
        var first = new LiteralRecord<>(10);
        var second = new LiteralRecord<>(10);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(first).isEqualTo(second);
            soft.assertThat(first.hashCode()).isEqualTo(second.hashCode());
        });
    }

    @Test
    @DisplayName("should not be equal for different values")
    void shouldNotEqualDifferentValues() {
        var one = new LiteralRecord<>("one");
        var two = new LiteralRecord<>("two");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(one).isNotEqualTo(two);
        });
    }

    @Test
    @DisplayName("should throw NullPointerException when value is null")
    void shouldThrowWhenValueIsNull() {
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> {
            new LiteralRecord<>(null);
        }, "The value argument is required");
    }
}