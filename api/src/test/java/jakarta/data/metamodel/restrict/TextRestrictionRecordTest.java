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

import jakarta.data.metamodel.TextAttribute;
import jakarta.data.metamodel.constraint.Like;
import jakarta.data.metamodel.constraint.NotEqualTo;
import jakarta.data.metamodel.constraint.NotLike;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class TextRestrictionRecordTest {
    // A mock static metamodel class for tests
    interface _Book {
        String AUTHOR = "author";
        String ID = "id";
        String TITLE = "title";

        TextAttribute<Book> author = TextAttribute.of(
                Book.class, AUTHOR);
        TextAttribute<Book> id = TextAttribute.of(
                Book.class, ID);
        TextAttribute<Book> title = TextAttribute.of(
                Book.class, TITLE);
    }

    // A mock entity class for tests
    static class Book {
        String author;
        String id;
        String title;
    }

    @Test
    void shouldCreateTextRestrictionWithDefaultValues() {
        TextRestriction<Book> restriction = _Book.title.like("%Java%");

        Like constraint = (Like) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(((Like) restriction.constraint()).isCaseSensitive()).isTrue();
            soft.assertThat(constraint.pattern()).isEqualTo("%Java%");
            soft.assertThat(constraint.escape()).isNull();
        });
    }

    @Test
    void shouldCreateTextRestrictionWithExplicitNegation() {
        TextRestriction<Book> restriction = _Book.title.like("%Java%").negate();

        NotLike constraint = (NotLike) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(((NotLike) restriction.constraint()).isCaseSensitive()).isTrue();
            soft.assertThat(constraint.pattern()).isEqualTo("%Java%");
            soft.assertThat(constraint.escape()).isNull();
        });
    }

    @Test
    void shouldIgnoreCaseForTextRestriction() {
        TextRestriction<Book> restriction = _Book.title.like("%Java%");

        TextRestriction<Book> caseInsensitiveRestriction = restriction.ignoreCase();
        Like constraint = (Like) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(caseInsensitiveRestriction.attribute()).isEqualTo("title");
            soft.assertThat(((Like) caseInsensitiveRestriction.constraint()).isCaseSensitive()).isFalse();
            soft.assertThat(constraint.pattern()).isEqualTo("%Java%");
            soft.assertThat(constraint.escape()).isNull();
        });
    }

    @Test
    void shouldCreateTextRestrictionWithEscapedValue() {
        TextRestriction<Book> restriction = _Book.title.like("%Java%");

        Like constraint = (Like) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(((Like) restriction.constraint()).isCaseSensitive()).isTrue();
            soft.assertThat(constraint.pattern()).isEqualTo("%Java%");
            soft.assertThat(constraint.escape()).isNull();
        });
    }

    @Test
    void shouldCreateTextRestrictionWithCustomWildcards() {
        TextRestriction<Book> restriction = _Book.title.like("*Java??", '?', '*', '$');

        Like constraint = (Like) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(((Like) restriction.constraint()).isCaseSensitive()).isTrue();
            soft.assertThat(constraint.pattern()).isEqualTo("%Java__");
            soft.assertThat(constraint.escape()).isEqualTo('$');
        });
    }

    @Test
    void shouldNegateLikeRestriction() {
        TextRestriction<Book> likeJakartaEE = _Book.title.endsWith("Jakarta EE");
        TextRestriction<Book> notLikeJakartaEE = likeJakartaEE.negate();
        TextRestriction<Book> anyCaseNotLikeJakartaEE = likeJakartaEE.ignoreCase().negate();
        TextRestriction<Book> notLikeJakartaEEAnyCase = likeJakartaEE.negate().ignoreCase();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(((Like) likeJakartaEE.constraint()).isCaseSensitive()).isTrue();
            soft.assertThat(((NotLike) notLikeJakartaEE.constraint()).isCaseSensitive()).isTrue();
            soft.assertThat(((NotLike) anyCaseNotLikeJakartaEE.constraint()).isCaseSensitive()).isFalse();
            soft.assertThat(((NotLike) notLikeJakartaEEAnyCase.constraint()).isCaseSensitive()).isFalse();
        });
    }

    @Test
    void shouldNegateNegatedRestriction() {
        TextRestriction<Book> endsWithJakartaEE = _Book.title.endsWith("Jakarta EE");
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
        TextRestriction<Book> titleRestriction = _Book.title.contains("Jakarta Data");
        TextRestriction<Book> authorRestriction = _Book.author.equalTo("Myself")
                        .ignoreCase()
                        .negate();

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
        TextRestriction<Book> restriction = _Book.author.equalTo("John Doe").negate();

        NotEqualTo<String> constraint = (NotEqualTo<String>) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("author");
            soft.assertThat(constraint.isCaseSensitive()).isTrue();
            soft.assertThat(constraint.value()).isEqualTo("John Doe");
        });
    }

    @Test
    void shouldThrowExceptionWhenAttributeIsNullInTextRestriction() {
        assertThatThrownBy(() -> TextAttribute.of(_Book.class, null).equalTo("testValue"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("entity attribute name is required");
    }
}
