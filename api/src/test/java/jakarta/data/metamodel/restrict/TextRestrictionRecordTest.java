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

import jakarta.data.metamodel.constraint.EqualTo;
import jakarta.data.metamodel.constraint.Like;
import jakarta.data.metamodel.constraint.NotEqualTo;
import jakarta.data.metamodel.constraint.NotLike;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import jakarta.data.metamodel.restrict.BasicRestrictionRecordTest.Book;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class TextRestrictionRecordTest {

    @Test
    void shouldCreateTextRestrictionWithDefaultValues() {
        TextRestriction<String> restriction = new TextRestrictionRecord<>(
                "title",
                Like.pattern("%Java%")
        );

        Like constraint = (Like) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(constraint.pattern()).isEqualTo("%Java%");
            soft.assertThat(constraint.escape()).isNull();
        });
    }

    @Test
    void shouldCreateTextRestrictionWithExplicitNegation() {
        TextRestriction<String> restriction = new TextRestrictionRecord<String>(
                "title",
                Like.pattern("%Java%")
        ).negate();

        NotLike constraint = (NotLike) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(constraint.pattern()).isEqualTo("%Java%");
            soft.assertThat(constraint.escape()).isNull();
        });
    }

    @Test
    void shouldIgnoreCaseForTextRestriction() {
        TextRestriction<String> restriction = new TextRestrictionRecord<>(
                "title",
                Like.pattern("%Java%")
        );

        TextRestriction<String> caseInsensitiveRestriction = restriction.ignoreCase();
        Like constraint = (Like) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(caseInsensitiveRestriction.attribute()).isEqualTo("title");
            soft.assertThat(caseInsensitiveRestriction.isCaseSensitive()).isFalse();
            soft.assertThat(constraint.pattern()).isEqualTo("%Java%");
            soft.assertThat(constraint.escape()).isNull();
        });
    }

    @Test
    void shouldCreateTextRestrictionWithEscapedValue() {
        TextRestriction<String> restriction = new TextRestrictionRecord<>(
                "title",
                Like.pattern("%Java%")
        );

        Like constraint = (Like) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(constraint.pattern()).isEqualTo("%Java%");
            soft.assertThat(constraint.escape()).isNull();
        });
    }

    @Test
    void shouldCreateTextRestrictionWithCustomWildcards() {
        TextRestriction<String> restriction = new TextRestrictionRecord<>(
                "title",
                Like.pattern("*Java??", '?', '*', '$')
        );

        Like constraint = (Like) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(constraint.pattern()).isEqualTo("%Java__");
            soft.assertThat(constraint.escape()).isEqualTo('$');
        });
    }

    @Test
    void shouldNegateLikeRestriction() {
        TextRestriction<Book> likeJakartaEE = Restrict.like("%Jakarta EE%", "title");
        TextRestriction<Book> notLikeJakartaEE = likeJakartaEE.negate();
        TextRestriction<Book> anyCaseNotLikeJakartaEE = likeJakartaEE.ignoreCase().negate();
        TextRestriction<Book> notLikeJakartaEEAnyCase = likeJakartaEE.negate().ignoreCase();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(likeJakartaEE.isCaseSensitive()).isTrue();
            soft.assertThat(notLikeJakartaEE.isCaseSensitive()).isTrue();
            soft.assertThat(anyCaseNotLikeJakartaEE.isCaseSensitive()).isFalse();
            soft.assertThat(notLikeJakartaEEAnyCase.isCaseSensitive()).isFalse();
        });
    }

    @Test
    void shouldNegateNegatedRestriction() {
        TextRestriction<Book> endsWithJakartaEE = Restrict.endsWith("Jakarta EE", "title");
        TextRestriction<Book> notEndsWithJakartaEE = endsWithJakartaEE.negate();
        TextRestriction<Book> notNotEndsWithJakartaEE = notEndsWithJakartaEE.negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(endsWithJakartaEE.constraint()).isInstanceOf(Like.class);
            soft.assertThat(((Like) endsWithJakartaEE.constraint()).pattern())
                .isEqualTo("%Jakarta EE");

            soft.assertThat(notEndsWithJakartaEE.constraint()).isInstanceOf(NotLike.class);
            soft.assertThat(((NotLike) notEndsWithJakartaEE.constraint()).pattern())
                .isEqualTo("%Jakarta EE");

            soft.assertThat(notNotEndsWithJakartaEE.constraint()).isInstanceOf(Like.class);
            soft.assertThat(((Like) notNotEndsWithJakartaEE.constraint()).pattern())
                .isEqualTo("%Jakarta EE");
        });
    }

    @Test
    void shouldOutputToString() {
        BasicRestriction<Book> titleRestriction =
                Restrict.contains("Jakarta Data", "title");
        BasicRestriction<Book> authorRestriction =
                Restrict.<Book>equalTo("Myself", "author").ignoreCase().negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(titleRestriction.toString()).isEqualTo("""
                    title LIKE '%Jakarta Data%' ESCAPE '\\'\
                    """);
            soft.assertThat(authorRestriction.toString()).isEqualTo("""
                    author <> 'Myself' IGNORE CASE\
                    """);
        });
    }

    @Test
    void shouldSupportNegationForTextRestriction() {
        TextRestriction<String> restriction = new TextRestrictionRecord<String>(
                "author",
                EqualTo.value("John Doe")
        ).negate();

        NotEqualTo<String> constraint = (NotEqualTo<String>) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("author");
            soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(constraint.value()).isEqualTo("John Doe");
        });
    }

    @Test
    void shouldThrowExceptionWhenAttributeIsNullInTextRestriction() {
        assertThatThrownBy(() -> new TextRestrictionRecord<>(null, EqualTo.value("testValue")))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Attribute must not be null");
    }
}
