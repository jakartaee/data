/*
 * Copyright (c) 2024,2025 Contributors to the Eclipse Foundation
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

import jakarta.data.metamodel.constraint.Like;
import jakarta.data.metamodel.constraint.NotLike;
import jakarta.data.metamodel.expression.StringLiteral;
import jakarta.data.metamodel.restrict.BasicRestriction;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
class TextAttributeTest {

    static class Author {
        long id;
        String name;
        String testAttribute;
    }

    // Static metamodel
    interface _Author {
        String TEST_ATTRIBUTE = "testAttribute";

        TextAttribute<Author> testAttribute = new TextAttributeRecord<>(Author.class, TEST_ATTRIBUTE);
    }

    @Test
    @DisplayName("should create contains restriction")
    void shouldCreateContainsRestriction() {
        var restriction = (BasicRestriction<?, ?>) _Author.testAttribute.contains("testValue");

        Like like = (Like) restriction.constraint();
        StringLiteral<?> literal = ((StringLiteral<?>) like.pattern());
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Author.testAttribute);
            soft.assertThat(literal.value()).isEqualTo("%testValue%");
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(restriction.expression()).isEqualTo(_Author.testAttribute);
            soft.assertThat(restriction.constraint()).isEqualTo(Like.substring("testValue"));
        });
    }

    @Test
    @DisplayName("should create startsWith restriction")
    void shouldCreateStartsWithRestriction() {
        var restriction = (BasicRestriction<?, ?>) _Author.testAttribute.startsWith("testValue");
        Like like = (Like) restriction.constraint();
        StringLiteral<?> literal = ((StringLiteral<?>) like.pattern());
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Author.testAttribute);
            soft.assertThat(literal.value()).isEqualTo("testValue%");
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(restriction.expression()).isEqualTo(_Author.testAttribute);
            soft.assertThat(restriction.constraint()).isEqualTo(Like.prefix("testValue"));
        });
    }

    @Test
    @DisplayName("should create endsWith restriction")
    void shouldCreateEndsWithRestriction() {
        var restriction = (BasicRestriction<?, ?>) _Author.testAttribute.endsWith("testValue");
        Like like = (Like) restriction.constraint();
        StringLiteral<?> literal = ((StringLiteral<?>) like.pattern());
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(restriction.expression()).isEqualTo(_Author.testAttribute);
            soft.assertThat(restriction.constraint()).isEqualTo(Like.suffix("testValue"));
            soft.assertThat(restriction.expression()).isEqualTo(_Author.testAttribute);
            soft.assertThat(literal.value()).isEqualTo("%testValue");
        });
    }

    @Test
    @DisplayName("should create like restriction")
    void shouldCreateLikeRestriction() {
        var restriction = (BasicRestriction<?, ?>) _Author.testAttribute.like("%test%");

        Like like = (Like) restriction.constraint();
        StringLiteral<?> literal = ((StringLiteral<?>) like.pattern());
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Author.testAttribute);
            soft.assertThat(literal.value()).isEqualTo("%test%");
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(restriction.expression()).isEqualTo(_Author.testAttribute);
            soft.assertThat(restriction.constraint()).isEqualTo(Like.pattern("%test%"));
        });
    }

    @Test
    @DisplayName("should create notContains restriction")
    void shouldCreateNotContainsRestriction() {
        var restriction = (BasicRestriction<?, ?>) _Author.testAttribute.notContains("testValue");
        NotLike notLike = (NotLike) restriction.constraint();
        StringLiteral<?> literal = ((StringLiteral<?>) notLike.pattern());
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Author.testAttribute);
            soft.assertThat(literal.value()).isEqualTo("%testValue%");
            soft.assertThat(restriction.constraint()).isInstanceOf(NotLike.class);
            soft.assertThat(restriction.expression()).isEqualTo(_Author.testAttribute);
            soft.assertThat(restriction.constraint()).isEqualTo(NotLike.substring("testValue"));
        });
    }

    @Test
    @DisplayName("should create notLike restriction")
    void shouldCreateNotLikeRestriction() {
        var restriction = (BasicRestriction<?, ?>) _Author.testAttribute.notLike("%test%");

        NotLike notLike = (NotLike) restriction.constraint();
        StringLiteral<?> literal = ((StringLiteral<?>) notLike.pattern());
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Author.testAttribute);
            soft.assertThat(literal.value()).isEqualTo("%test%");
            soft.assertThat(restriction.constraint()).isInstanceOf(NotLike.class);
            soft.assertThat(restriction.expression()).isEqualTo(_Author.testAttribute);
            soft.assertThat(restriction.constraint()).isEqualTo(NotLike.pattern("%test%"));
        });
    }

    @Test
    @DisplayName("should create notStartsWith restriction")
    void shouldCreateNotStartsWithRestriction() {
        var restriction = (BasicRestriction<?, ?>) _Author.testAttribute.notStartsWith("testValue");
        NotLike notLike = (NotLike) restriction.constraint();
        StringLiteral<?> literal = ((StringLiteral<?>) notLike.pattern());
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Author.testAttribute);
            soft.assertThat(literal.value()).isEqualTo("testValue%");
            soft.assertThat(restriction.constraint()).isInstanceOf(NotLike.class);
            soft.assertThat(restriction.expression()).isEqualTo(_Author.testAttribute);
            soft.assertThat(restriction.constraint()).isEqualTo(NotLike.prefix("testValue"));
        });
    }

    @Test
    @DisplayName("should create notEndsWith restriction")
    void shouldCreateNotEndsWithRestriction() {
        var restriction = (BasicRestriction<?, ?>) _Author.testAttribute.notEndsWith("testValue");

        NotLike notLike = (NotLike) restriction.constraint();
        StringLiteral<?> literal = ((StringLiteral<?>) notLike.pattern());
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Author.testAttribute);
            soft.assertThat(literal.value()).isEqualTo("%testValue");
            soft.assertThat(restriction.constraint()).isInstanceOf(NotLike.class);
            soft.assertThat(restriction.expression()).isEqualTo(_Author.testAttribute);
            soft.assertThat(restriction.constraint()).isEqualTo(NotLike.suffix("testValue"));
        });
    }
}
