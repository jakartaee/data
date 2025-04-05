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
import jakarta.data.mock.entity.Book;
import jakarta.data.mock.entity._Book;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class TextRestrictionRecordTest {

    @Test
    void shouldCreateTextRestrictionWithDefaultValues() {
        @SuppressWarnings("unchecked")
        ValueRestriction<Book,String> restriction =
                (ValueRestriction<Book, String>) _Book.title.like("%Java%");

        Like constraint = (Like) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.title);
            // TODO TextRestriction.ignoreCase vs TextAttribute.upper/lowercased
            //soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(constraint.pattern()).isEqualTo("%Java%");
            soft.assertThat(constraint.escape()).isNull();
        });
    }

    @Test
    void shouldCreateTextRestrictionWithExplicitNegation() {
        @SuppressWarnings("unchecked")
        ValueRestriction<Book,String> restriction =
                (ValueRestriction<Book, String>) _Book.title.like("%Java%").negate();

        NotLike constraint = (NotLike) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.title);
            // TODO TextRestriction.ignoreCase vs TextAttribute.upper/lowercased
            //soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(constraint.pattern()).isEqualTo("%Java%");
            soft.assertThat(constraint.escape()).isNull();
        });
    }

    @Test
    void shouldIgnoreCaseForTextRestriction() {
        @SuppressWarnings("unchecked")
        ValueRestriction<Book,String> restriction =
                (ValueRestriction<Book, String>) _Book.title.like("%Java%");

        // TODO TextRestriction.ignoreCase vs TextAttribute.upper/lowercased
        //ValueRestriction<Book,String> caseInsensitiveRestriction = restriction.ignoreCase();
        Like constraint = (Like) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
        //    soft.assertThat(caseInsensitiveRestriction.expression()).isEqualTo(_Book.title);
        //    soft.assertThat(caseInsensitiveRestriction.isCaseSensitive()).isFalse();
            soft.assertThat(constraint.pattern()).isEqualTo("%Java%");
            soft.assertThat(constraint.escape()).isNull();
        });
    }

    @Test
    void shouldCreateTextRestrictionWithEscapedValue() {
        @SuppressWarnings("unchecked")
        ValueRestriction<Book,String> restriction =
                (ValueRestriction<Book, String>) _Book.title.like("%Java%");

        Like constraint = (Like) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.title);
            // TODO TextRestriction.ignoreCase vs TextAttribute.upper/lowercased
            //soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(constraint.pattern()).isEqualTo("%Java%");
            soft.assertThat(constraint.escape()).isNull();
        });
    }

    @Test
    void shouldCreateTextRestrictionWithCustomWildcards() {
        @SuppressWarnings("unchecked")
        ValueRestriction<Book,String> restriction =
                (ValueRestriction<Book, String>) _Book.title.like("*Java??", '?', '*', '$');

        Like constraint = (Like) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.title);
            // TODO TextRestriction.ignoreCase vs TextAttribute.upper/lowercased
            //soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(constraint.pattern()).isEqualTo("%Java__");
            soft.assertThat(constraint.escape()).isEqualTo('$');
        });
    }

    @Test
    void shouldNegateLikeRestriction() {
        @SuppressWarnings("unchecked")
        ValueRestriction<Book,String> likeJakartaEE =
                (ValueRestriction<Book, String>) _Book.title.endsWith("Jakarta EE");
        ValueRestriction<Book,String> notLikeJakartaEE = likeJakartaEE.negate();
        // TODO TextRestriction.ignoreCase vs TextAttribute.upper/lowercased
        //ValueRestriction<Book,String> anyCaseNotLikeJakartaEE = likeJakartaEE.ignoreCase().negate();
        //ValueRestriction<Book,String> notLikeJakartaEEAnyCase = likeJakartaEE.negate().ignoreCase();

        //SoftAssertions.assertSoftly(soft -> {
        //    soft.assertThat(likeJakartaEE.isCaseSensitive()).isTrue();
        //    soft.assertThat(notLikeJakartaEE.isCaseSensitive()).isTrue();
        //    soft.assertThat(anyCaseNotLikeJakartaEE.isCaseSensitive()).isFalse();
        //    soft.assertThat(notLikeJakartaEEAnyCase.isCaseSensitive()).isFalse();
        //});
    }

    @Test
    void shouldNegateNegatedRestriction() {
        @SuppressWarnings("unchecked")
        ValueRestriction<Book,String> endsWithJakartaEE =
                (ValueRestriction<Book, String>) _Book.title.endsWith("Jakarta EE");
        ValueRestriction<Book,String> notEndsWithJakartaEE = endsWithJakartaEE.negate();
        ValueRestriction<Book,String> notNotEndsWithJakartaEE = notEndsWithJakartaEE.negate();

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
        @SuppressWarnings("unchecked")
        ValueRestriction<Book,String> titleRestriction =
                (ValueRestriction<Book, String>) _Book.title.contains("Jakarta Data");
        @SuppressWarnings("unchecked")
        ValueRestriction<Book,String> authorRestriction =
                (ValueRestriction<Book, String>) _Book.author.equalTo("Myself")
                // TODO TextRestriction.ignoreCase vs TextAttribute.upper/lowercased
                //        .ignoreCase()
                        .negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(titleRestriction.toString()).isEqualTo("""
                    title LIKE '%Jakarta Data%' ESCAPE '\\'\
                    """);
            soft.assertThat(authorRestriction.toString()).isEqualTo("""
                    author <> 'Myself'\
                    """);
        });
    }

    @Test
    void shouldSupportNegationForTextRestriction() {
        @SuppressWarnings("unchecked")
        ValueRestriction<Book,String> restriction =
                (ValueRestriction<Book, String>) _Book.author.equalTo("John Doe").negate();

        NotEqualTo<String> constraint = (NotEqualTo<String>) restriction.constraint();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.author);
            // TODO TextRestriction.ignoreCase vs TextAttribute.upper/lowercased
            //soft.assertThat(restriction.isCaseSensitive()).isTrue();
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
