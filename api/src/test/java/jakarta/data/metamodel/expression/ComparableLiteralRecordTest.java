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
package jakarta.data.metamodel.expression;


import jakarta.data.metamodel.ComparableExpression;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ComparableLiteralRecordTest {

    // Simple entity for testing
    static class Book {
        String title;
        Integer pages;
    }

    // Static metamodel
    interface _Book {
        String TITLE = "title";
        String PAGES = "pages";

        ComparableExpression<Book, String> title = new ComparableLiteralRecord<>(TITLE);
        ComparableExpression<Book, Integer> pages = new ComparableLiteralRecord<>(100);
    }

    @Test
    @DisplayName("should create ComparableLiteralRecord with correct value")
    void shouldCreateComparableLiteralRecord() {
        var literal = new ComparableLiteralRecord<Book, String>("Domain-Driven Design");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(literal.value()).isEqualTo("Domain-Driven Design");
            soft.assertThat(literal).isInstanceOf(ComparableLiteral.class);
            soft.assertThat(literal.toString()).isEqualTo("Domain-Driven Design");
        });
    }

    @Test
    @DisplayName("should support equals and hashCode for identical values")
    void shouldSupportEqualsAndHashCode() {
        var first = new ComparableLiteralRecord<Book, Integer>(42);
        var second = new ComparableLiteralRecord<Book, Integer>(42);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(first).isEqualTo(second);
            soft.assertThat(first.hashCode()).isEqualTo(second.hashCode());
        });
    }

    @Test
    @DisplayName("should not be equal for different values")
    void shouldNotEqualDifferentValues() {
        var first = new ComparableLiteralRecord<Book, Integer>(42);
        var second = new ComparableLiteralRecord<Book, Integer>(99);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(first).isNotEqualTo(second);
        });
    }

    @Test
    @DisplayName("should throw NullPointerException when value is null")
    void shouldThrowWhenValueIsNull() {
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> {
            new ComparableLiteralRecord<Book, String>(null);
        });
    }
}