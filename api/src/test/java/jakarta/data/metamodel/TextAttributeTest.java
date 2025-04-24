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
import org.junit.jupiter.api.Test;

class TextAttributeTest {

    static class Author {
        long id;
        String name;
        String testAttribute;
    }

    private final TextAttribute<Author> testAttribute = TextAttribute.of(Author.class, "testAttribute");

    @Test
    void shouldCreateContainsRestriction() {
        @SuppressWarnings("unchecked")
        BasicRestriction<Author,String> restriction =
                (BasicRestriction<Author, String>) testAttribute.contains("testValue");

        Like like = (Like) restriction.constraint();
        StringLiteral<?> literal = ((StringLiteral<?>) like.pattern());

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(literal.value()).isEqualTo("%testValue%");
        });
    }

    @Test
    void shouldCreateStartsWithRestriction() {
        @SuppressWarnings("unchecked")
        BasicRestriction<Author,String> restriction =
                (BasicRestriction<Author, String>) testAttribute.startsWith("testValue");

        Like like = (Like) restriction.constraint();
        StringLiteral<?> literal = ((StringLiteral<?>) like.pattern());

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(literal.value()).isEqualTo("testValue%");
        });
    }

    @Test
    void shouldCreateEndsWithRestriction() {
        @SuppressWarnings("unchecked")
        BasicRestriction<Author,String> restriction =
                (BasicRestriction<Author,String>) testAttribute.endsWith("testValue");

        Like like = (Like) restriction.constraint();
        StringLiteral<?> literal = ((StringLiteral<?>) like.pattern());

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(literal.value()).isEqualTo("%testValue");
        });
    }

    @Test
    void shouldCreateLikeRestriction() {
        @SuppressWarnings("unchecked")
        BasicRestriction<Author,String> restriction =
                (BasicRestriction<Author, String>) testAttribute.like("%test%");

        Like like = (Like) restriction.constraint();
        StringLiteral<?> literal = ((StringLiteral<?>) like.pattern());

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(literal.value()).isEqualTo("%test%");
        });
    }

    @Test
    void shouldCreateNotContainsRestriction() {
        @SuppressWarnings("unchecked")
        BasicRestriction<Author,String> restriction =
                (BasicRestriction<Author, String>) testAttribute.notContains("testValue");

        NotLike notLike = (NotLike) restriction.constraint();
        StringLiteral<?> literal = ((StringLiteral<?>) notLike.pattern());

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(restriction.constraint()).isInstanceOf(NotLike.class);
            soft.assertThat(literal.value()).isEqualTo("%testValue%");
        });
    }

    @Test
    void shouldCreateNotLikeRestriction() {
        @SuppressWarnings("unchecked")
        BasicRestriction<Author,String> restriction =
                (BasicRestriction<Author, String>) testAttribute.notLike("%test%");

        NotLike notLike = (NotLike) restriction.constraint();
        StringLiteral<?> literal = ((StringLiteral<?>) notLike.pattern());

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(literal.value()).isEqualTo("%test%");
        });
    }

    @Test
    void shouldCreateNotStartsWithRestriction() {
        @SuppressWarnings("unchecked")
        BasicRestriction<Author,String> restriction =
                (BasicRestriction<Author, String>) testAttribute.notStartsWith("testValue");

        NotLike notLike = (NotLike) restriction.constraint();
        StringLiteral<?> literal = ((StringLiteral<?>) notLike.pattern());

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(literal.value()).isEqualTo("testValue%");
        });
    }

    @Test
    void shouldCreateNotEndsWithRestriction() {
        @SuppressWarnings("unchecked")
        BasicRestriction<Author,String> restriction =
                (BasicRestriction<Author, String>) testAttribute.notEndsWith("testValue");

        NotLike notLike = (NotLike) restriction.constraint();
        StringLiteral<?> literal = ((StringLiteral<?>) notLike.pattern());

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(literal.value()).isEqualTo("%testValue");
        });
    }
}
