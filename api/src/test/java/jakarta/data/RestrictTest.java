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
package jakarta.data;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class RestrictTest {


    @Test
    void shouldCreateEqualToRestriction() {
        Restriction<String> restriction = Restrict.equalTo("value", "field");

        assertThat(restriction).isInstanceOf(TextRestrictionRecord.class);

        TextRestrictionRecord<String> basic = (TextRestrictionRecord<String>) restriction;
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(basic.field()).isEqualTo("field");
            soft.assertThat(basic.comparison()).isEqualTo(Operator.EQUAL);
            soft.assertThat(basic.value()).isEqualTo("value");
            soft.assertThat(basic.isNegated()).isFalse();
        });
    }

    @Test
    void shouldCreateNotEqualToRestriction() {
        Restriction<String> restriction = Restrict.notEqualTo("value", "field");

        assertThat(restriction).isInstanceOf(TextRestrictionRecord.class);

        TextRestrictionRecord<String> basic = (TextRestrictionRecord<String>) restriction;
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(basic.field()).isEqualTo("field");
            soft.assertThat(basic.comparison()).isEqualTo(Operator.EQUAL);
            soft.assertThat(basic.value()).isEqualTo("value");
            soft.assertThat(basic.isNegated()).isTrue();
        });
    }

    @Test
    void shouldCombineAllRestrictionsWithNegation() {
        Restriction<String> r1 = Restrict.notEqualTo("value1", "field1");
        Restriction<String> r2 = Restrict.greaterThan(100, "field2");

        Restriction<String> combined = Restrict.all(r1, r2);

        assertThat(combined).isInstanceOf(CompositeRestrictionRecord.class);

        CompositeRestrictionRecord<String> composite = (CompositeRestrictionRecord<String>) combined;
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(composite.type()).isEqualTo(CompositeRestriction.Type.ALL);
            soft.assertThat(composite.restrictions()).containsExactly(r1, r2);
            soft.assertThat(composite.isNegated()).isFalse();
        });
    }

    @Test
    void shouldCreateContainsRestriction() {
        TextRestriction<String> restriction = Restrict.contains("substring", "field");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.field()).isEqualTo("field");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(restriction.value()).isEqualTo("%substring%");
            soft.assertThat(restriction.isNegated()).isFalse();
        });
    }

    @Test
    void shouldCreateNegatedContainsRestriction() {
        TextRestriction<String> restriction = Restrict.notContains("substring", "field");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.field()).isEqualTo("field");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(restriction.value()).isEqualTo("%substring%");
            soft.assertThat(restriction.isNegated()).isTrue();
        });
    }

    @Test
    void shouldCreateStartsWithRestriction() {
        TextRestriction<String> restriction = Restrict.startsWith("prefix", "field");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.field()).isEqualTo("field");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(restriction.value()).isEqualTo("prefix%");
            soft.assertThat(restriction.isNegated()).isFalse();
        });
    }

    @Test
    void shouldCreateNegatedStartsWithRestriction() {
        TextRestriction<String> restriction = Restrict.notStartsWith("prefix", "field");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.field()).isEqualTo("field");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(restriction.value()).isEqualTo("prefix%");
            soft.assertThat(restriction.isNegated()).isTrue();
        });
    }

    @Test
    void shouldCreateEndsWithRestriction() {
        TextRestriction<String> restriction = Restrict.endsWith("suffix", "field");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.field()).isEqualTo("field");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(restriction.value()).isEqualTo("%suffix");
            soft.assertThat(restriction.isNegated()).isFalse();
        });
    }

    @Test
    void shouldCreateNegatedEndsWithRestriction() {
        TextRestriction<String> restriction = Restrict.notEndsWith("suffix", "field");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.field()).isEqualTo("field");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(restriction.value()).isEqualTo("%suffix");
            soft.assertThat(restriction.isNegated()).isTrue();
        });
    }

    @Test
    void shouldEscapeToLikePatternCorrectly() {
        String result = invokeToLikeEscaped('_', '%', true, "test_value", false);

        assertThat(result).isEqualTo("%test\\_value");
    }

    @Test
    void shouldThrowExceptionForInvalidWildcard() {
        assertThatThrownBy(() -> invokeToLikeEscaped('_', '_', true, "value", false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot use the same character (_) for both types of wildcards.");
    }

    private String invokeToLikeEscaped(char charWildcard, char stringWildcard, boolean allowPrevious, String literal, boolean allowSubsequent) {
        return Restrict.toLikeEscaped(charWildcard, stringWildcard, allowPrevious, literal, allowSubsequent);
    }
}
