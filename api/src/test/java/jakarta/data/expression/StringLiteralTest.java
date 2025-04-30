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
package jakarta.data.expression;

import jakarta.data.constraint.Like;
import jakarta.data.constraint.NotLike;
import jakarta.data.expression.literal.StringLiteral;
import jakarta.data.metamodel.TextAttribute;
import jakarta.data.restrict.BasicRestriction;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringLiteralTest {

    interface _SimpleEntity {
        TextAttribute<SimpleEntity> name = TextAttribute.of(SimpleEntity.class, "name");
    }

    static class SimpleEntity {
        String name;
    }

    @Test
    @DisplayName("should create StringLiteralRecord and validate value")
    void shouldCreateStringLiteralRecord() {
        var literal = StringLiteral.of("Jakarta EE");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(literal.value()).isEqualTo("Jakarta EE");
            soft.assertThat(literal).isInstanceOf(StringLiteral.class);
            soft.assertThat(literal.toString()).isEqualTo("'Jakarta EE'");
        });
    }

    @Test
    @DisplayName("should support equals and hashCode for identical values")
    void shouldSupportEqualsAndHashCode() {
        var first = StringLiteral.of("DDD");
        var second = StringLiteral.of("DDD");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(first).isEqualTo(second);
            soft.assertThat(first.hashCode()).isEqualTo(second.hashCode());
        });
    }

    @Test
    @DisplayName("should not be equal for different values")
    void shouldNotEqualDifferentValues() {
        var one = StringLiteral.of("one");
        var two = StringLiteral.of("two");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(one).isNotEqualTo(two);
        });
    }

    @Test
    @DisplayName("should throw NullPointerException when value is null")
    void shouldThrowWhenValueIsNull() {
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> {
            StringLiteral.of(null);
        });
    }

    @Test
    @DisplayName("should create startsWith restriction with correct pattern")
    void shouldCreateStartsWithRestriction() {
        var restriction = (BasicRestriction<?, ?>) _SimpleEntity.name.startsWith("Jakarta");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(restriction.expression()).isNotNull();
            soft.assertThat(restriction.constraint()).isEqualTo(Like.prefix("Jakarta"));
        });
    }

    @Test
    @DisplayName("should create endsWith restriction with correct pattern")
    void shouldCreateEndsWithRestriction() {
        var restriction = (BasicRestriction<?, ?>) _SimpleEntity.name.endsWith("EE");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(restriction.expression()).isNotNull();
            soft.assertThat(restriction.constraint()).isEqualTo(Like.suffix("EE"));
        });
    }

    @Test
    @DisplayName("should create contains restriction with correct pattern")
    void shouldCreateContainsRestriction() {
        var restriction = (BasicRestriction<?, ?>) _SimpleEntity.name.contains("Data");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(restriction.expression()).isNotNull();
            soft.assertThat(restriction.constraint()).isEqualTo(Like.substring("Data"));
        });
    }

    @Test
    @DisplayName("should create like restriction with correct pattern")
    void shouldCreateLikeRestriction() {
        var restriction = (BasicRestriction<?, ?>) _SimpleEntity.name.like("%EE%");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(restriction.expression()).isNotNull();
            soft.assertThat(restriction.constraint()).isEqualTo(Like.pattern("%EE%"));
        });
    }

    @Test
    @DisplayName("should create notLike restriction with correct pattern")
    void shouldCreateNotLikeRestriction() {
        var restriction = (BasicRestriction<?, ?>) _SimpleEntity.name.notLike("%Legacy%");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.constraint()).isInstanceOf(NotLike.class);
            soft.assertThat(restriction.expression()).isNotNull();
            soft.assertThat(restriction.constraint()).isEqualTo(NotLike.pattern("%Legacy%"));
        });
    }
}