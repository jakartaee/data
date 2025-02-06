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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.metamodel.restrict;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import jakarta.data.metamodel.restrict.BasicRestrictionRecordTest.Book;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class TextRestrictionRecordTest {

    @Test
    void shouldCreateTextRestrictionWithDefaultValues() {
        TextRestrictionRecord<String> restriction = new TextRestrictionRecord<>(
                "title",
                Operator.LIKE,
                "%Java%"
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
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
                Operator.NOT_LIKE,
                "%Java%"
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.NOT_LIKE);
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
            soft.assertThat(textRestriction.attribute()).isEqualTo("title");
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
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(restriction.value()).isEqualTo("%Java%");
            soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(restriction.isEscaped()).isTrue();
        });
    }

    @Test
    void shouldNegateLikeRestriction() {
        TextRestriction<Book> likeJakartaEE = Restrict.like("%Jakarta EE%", "title");
        TextRestriction<Book> notLikeJakartaEE = likeJakartaEE.negate();
        TextRestriction<Book> anyCaseNotLikeJakartaEE = likeJakartaEE.ignoreCase().negate();
        TextRestriction<Book> notLikeJakartaEEAnyCase = likeJakartaEE.negate().ignoreCase();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(likeJakartaEE.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(likeJakartaEE.isCaseSensitive()).isTrue();

            soft.assertThat(notLikeJakartaEE.comparison()).isEqualTo(Operator.NOT_LIKE);
            soft.assertThat(notLikeJakartaEE.isCaseSensitive()).isTrue();

            soft.assertThat(anyCaseNotLikeJakartaEE.comparison()).isEqualTo(Operator.NOT_LIKE);
            soft.assertThat(anyCaseNotLikeJakartaEE.isCaseSensitive()).isFalse();

            soft.assertThat(notLikeJakartaEEAnyCase.comparison()).isEqualTo(Operator.NOT_LIKE);
            soft.assertThat(notLikeJakartaEEAnyCase.isCaseSensitive()).isFalse();
        });
    }

    @Test
    void shouldNegateNegatedRestriction() {
        TextRestriction<Book> endsWithJakartaEE = Restrict.endsWith("Jakarta EE", "title");
        TextRestriction<Book> notEndsWithJakartaEE = endsWithJakartaEE.negate();
        TextRestriction<Book> notNotEndsWithJakartaEE = notEndsWithJakartaEE.negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(endsWithJakartaEE.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(endsWithJakartaEE.value()).isEqualTo("%Jakarta EE");

            soft.assertThat(notEndsWithJakartaEE.comparison()).isEqualTo(Operator.NOT_LIKE);
            soft.assertThat(notEndsWithJakartaEE.value()).isEqualTo("%Jakarta EE");

            soft.assertThat(notNotEndsWithJakartaEE.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(notNotEndsWithJakartaEE.value()).isEqualTo("%Jakarta EE");
        });
    }

    @Test
    void shouldOutputToString() {
        TextRestriction<Book> titleRestriction =
                Restrict.contains("Jakarta Data", "title");
        TextRestriction<Book> authorRestriction =
                Restrict.<Book>equalTo("Myself", "author").ignoreCase().negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(titleRestriction.toString()).isEqualTo("""
                    title LIKE "%Jakarta Data%" ESCAPED\
                    """);
            soft.assertThat(authorRestriction.toString()).isEqualTo("""
                    author NOT_EQUAL_IGNORE_CASE "Myself"\
                    """);
        });
    }

    @Test
    void shouldSupportNegationForTextRestriction() {
        TextRestrictionRecord<String> restriction = new TextRestrictionRecord<>(
                "author",
                Operator.NOT_EQUAL,
                "John Doe"
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("author");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.NOT_EQUAL);
            soft.assertThat(restriction.value()).isEqualTo("John Doe");
            soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(restriction.isEscaped()).isFalse();
        });
    }

    @Test
    void shouldThrowExceptionWhenAttributeIsNullInTextRestriction() {
        assertThatThrownBy(() -> new TextRestrictionRecord<>(null, Operator.LIKE, "testValue"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Attribute must not be null");
    }
}
