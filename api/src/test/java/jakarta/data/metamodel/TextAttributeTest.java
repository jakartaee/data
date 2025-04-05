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
import jakarta.data.metamodel.restrict.ValueRestriction;
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
        ValueRestriction<Author,String> restriction =
                (ValueRestriction<Author, String>) testAttribute.contains("testValue");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(((Like) restriction.constraint()).pattern()).isEqualTo("%testValue%");
        });
    }

    @Test
    void shouldCreateStartsWithRestriction() {
        @SuppressWarnings("unchecked")
        ValueRestriction<Author,String> restriction =
                (ValueRestriction<Author, String>) testAttribute.startsWith("testValue");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(((Like) restriction.constraint()).pattern()).isEqualTo("testValue%");
        });
    }

    @Test
    void shouldCreateEndsWithRestriction() {
        @SuppressWarnings("unchecked")
        ValueRestriction<Author,String> restriction =
                (ValueRestriction<Author,String>) testAttribute.endsWith("testValue");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(((Like) restriction.constraint()).pattern()).isEqualTo("%testValue");
        });
    }

    @Test
    void shouldCreateLikeRestriction() {
        @SuppressWarnings("unchecked")
        ValueRestriction<Author,String> restriction =
                (ValueRestriction<Author, String>) testAttribute.like("%test%");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(((Like) restriction.constraint()).pattern()).isEqualTo("%test%");
        });
    }

    @Test
    void shouldCreateNotContainsRestriction() {
        @SuppressWarnings("unchecked")
        ValueRestriction<Author,String> restriction =
                (ValueRestriction<Author, String>) testAttribute.notContains("testValue");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(restriction.constraint()).isInstanceOf(NotLike.class);
            soft.assertThat(((NotLike) restriction.constraint()).pattern()).isEqualTo("%testValue%");
        });
    }

    @Test
    void shouldCreateNotLikeRestriction() {
        @SuppressWarnings("unchecked")
        ValueRestriction<Author,String> restriction =
                (ValueRestriction<Author, String>) testAttribute.notLike("%test%");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(restriction.constraint()).isInstanceOf(NotLike.class);
            soft.assertThat(((NotLike) restriction.constraint()).pattern()).isEqualTo("%test%");
        });
    }

    @Test
    void shouldCreateNotStartsWithRestriction() {
        @SuppressWarnings("unchecked")
        ValueRestriction<Author,String> restriction =
                (ValueRestriction<Author, String>) testAttribute.notStartsWith("testValue");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(restriction.constraint()).isInstanceOf(NotLike.class);
            soft.assertThat(((NotLike) restriction.constraint()).pattern()).isEqualTo("testValue%");
        });
    }

    @Test
    void shouldCreateNotEndsWithRestriction() {
        @SuppressWarnings("unchecked")
        ValueRestriction<Author,String> restriction =
                (ValueRestriction<Author, String>) testAttribute.notEndsWith("testValue");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(restriction.constraint()).isInstanceOf(NotLike.class);
            soft.assertThat(((NotLike) restriction.constraint()).pattern()).isEqualTo("%testValue");
        });
    }
}
