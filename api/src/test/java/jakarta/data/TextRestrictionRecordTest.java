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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;


class TextRestrictionRecordTest {

    @Test
    void shouldCreateTextRestrictionWithDefaultValues() {
        TextRestrictionRecord<String> restriction = new TextRestrictionRecord<>(
                "title",
                Operator.LIKE,
                "%Java%"
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.field()).isEqualTo("title");
            soft.assertThat(restriction.isNegated()).isFalse();
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(restriction.value()).isEqualTo("%Java%");
            soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(restriction.isEscaped()).isFalse();
        });
    }

    @Test
    void shouldCreateTextRestrictionWithExplicitNegation() {
        TextRestrictionRecord<String> restriction = new TextRestrictionRecord<>(
                "title",
                true,
                Operator.LIKE,
                "%Java%"
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.field()).isEqualTo("title");
            soft.assertThat(restriction.isNegated()).isTrue();
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(restriction.value()).isEqualTo("%Java%");
            soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(restriction.isEscaped()).isFalse();
        });
    }

    @Test
    void shouldIgnoreCaseForTextRestriction() {
        TextRestrictionRecord<String> restriction = new TextRestrictionRecord<>(
                "title",
                Operator.LIKE,
                "%Java%"
        );

        Restriction<String> caseInsensitiveRestriction = restriction.ignoreCase();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(caseInsensitiveRestriction).isInstanceOf(TextRestrictionRecord.class);
            TextRestrictionRecord<String> textRestriction = (TextRestrictionRecord<String>) caseInsensitiveRestriction;
            soft.assertThat(textRestriction.field()).isEqualTo("title");
            soft.assertThat(textRestriction.isNegated()).isFalse();
            soft.assertThat(textRestriction.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(textRestriction.value()).isEqualTo("%Java%");
            soft.assertThat(textRestriction.isCaseSensitive()).isFalse();
            soft.assertThat(textRestriction.isEscaped()).isFalse();
        });
    }

    @Test
    void shouldCreateTextRestrictionWithEscapedValue() {
        TextRestrictionRecord<String> restriction = new TextRestrictionRecord<>(
                "title",
                Operator.LIKE,
                true,
                "%Java%"
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.field()).isEqualTo("title");
            soft.assertThat(restriction.isNegated()).isFalse();
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(restriction.value()).isEqualTo("%Java%");
            soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(restriction.isEscaped()).isTrue();
        });
    }

    @Test
    void shouldSupportNegationForTextRestriction() {
        TextRestrictionRecord<String> restriction = new TextRestrictionRecord<>(
                "author",
                true,
                Operator.EQUAL,
                "John Doe"
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.field()).isEqualTo("author");
            soft.assertThat(restriction.isNegated()).isTrue();
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.EQUAL);
            soft.assertThat(restriction.value()).isEqualTo("John Doe");
            soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(restriction.isEscaped()).isFalse();
        });
    }
}
