/*
 * Copyright (c) 2024,2026 Contributors to the Eclipse Foundation
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
package jakarta.data.restrict;

import jakarta.data.constraint.Like;
import jakarta.data.constraint.NotEqualTo;
import jakarta.data.constraint.NotLike;
import jakarta.data.metamodel.TextAttribute;
import jakarta.data.mock.entity.Book;
import jakarta.data.mock.entity._Book;
import jakarta.data.spi.expression.literal.StringLiteral;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class TextRestrictionRecordTest {

    @Test
    void shouldCreateTextRestrictionWithDefaultValues() {
        @SuppressWarnings("unchecked")
        BasicRestriction<Book,String> restriction =
                (BasicRestriction<Book, String>) _Book.title.like("%Java%");

        Like constraint = (Like) restriction.constraint();
        StringLiteral literal = (StringLiteral) constraint.escapedPattern();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.title);
            // TODO TextRestriction.ignoreCase vs TextAttribute.upper/lowercased
            //soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(literal.value()).isEqualTo("%Java%");
            soft.assertThat(constraint.escape()).isEqualTo('\\');
        });
    }

    @Test
    void shouldCreateTextRestrictionWithExplicitNegation() {
        @SuppressWarnings("unchecked")
        BasicRestriction<Book,String> restriction =
                (BasicRestriction<Book, String>) _Book.title.like("%Java%").negate();

        NotLike constraint = (NotLike) restriction.constraint();
        StringLiteral literal = (StringLiteral) constraint.escapedPattern();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.title);
            // TODO TextRestriction.ignoreCase vs TextAttribute.upper/lowercased
            //soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(literal.value()).isEqualTo("%Java%");
            soft.assertThat(constraint.escape()).isEqualTo('\\');
        });
    }

    @Test
    void shouldIgnoreCaseForTextRestriction() {
        @SuppressWarnings("unchecked")
        BasicRestriction<Book,String> restriction =
                (BasicRestriction<Book, String>) _Book.title.like("%Java%");

        // TODO TextRestriction.ignoreCase vs TextAttribute.upper/lowercased
        //BasicRestriction<Book,String> caseInsensitiveRestriction = restriction.ignoreCase();
        Like constraint = (Like) restriction.constraint();
        StringLiteral literal = (StringLiteral) constraint.escapedPattern();

        SoftAssertions.assertSoftly(soft -> {
        //    soft.assertThat(caseInsensitiveRestriction.expression()).isEqualTo(_Book.title);
        //    soft.assertThat(caseInsensitiveRestriction.isCaseSensitive()).isFalse();
            soft.assertThat(literal.value()).isEqualTo("%Java%");
            soft.assertThat(constraint.escape()).isEqualTo('\\');
        });
    }

    @Test
    void shouldCreateTextRestrictionWithEscapedValue() {
        @SuppressWarnings("unchecked")
        BasicRestriction<Book,String> restriction =
                (BasicRestriction<Book, String>) _Book.title.like("%Java%");

        Like constraint = (Like) restriction.constraint();
        StringLiteral literal = (StringLiteral) constraint.escapedPattern();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.title);
            // TODO TextRestriction.ignoreCase vs TextAttribute.upper/lowercased
            //soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(literal.value()).isEqualTo("%Java%");
            soft.assertThat(constraint.escape()).isEqualTo('\\');
        });
    }

    @Test
    void shouldCreateTextRestrictionWithCustomWildcards() {
        @SuppressWarnings("unchecked")
        BasicRestriction<Book,String> restriction =
                (BasicRestriction<Book, String>) _Book.title.like("*Java??", '?', '*', '$');

        Like constraint = (Like) restriction.constraint();
        StringLiteral literal = (StringLiteral) constraint.escapedPattern();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.title);
            // TODO TextRestriction.ignoreCase vs TextAttribute.upper/lowercased
            //soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(literal.value()).isEqualTo("%Java__");
            soft.assertThat(constraint.escape()).isEqualTo('$');
        });
    }

    @Test
    void shouldNegateLikeRestriction() {
        @SuppressWarnings("unchecked")
        BasicRestriction<Book,String> likeJakartaEE =
                (BasicRestriction<Book, String>) _Book.title.endsWith("Jakarta EE");
        @SuppressWarnings("unchecked")
        BasicRestriction<Book,String> notLikeJakartaEE =
                (BasicRestriction<Book, String>)likeJakartaEE.negate();
        // TODO TextRestriction.ignoreCase vs TextAttribute.upper/lowercased
        //BasicRestriction<Book,String> anyCaseNotLikeJakartaEE = likeJakartaEE.ignoreCase().negate();
        //BasicRestriction<Book,String> notLikeJakartaEEAnyCase = likeJakartaEE.negate().ignoreCase();

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
        BasicRestriction<Book,String> endsWithJakartaEE =
                (BasicRestriction<Book, String>) _Book.title.endsWith("Jakarta EE");
        @SuppressWarnings("unchecked")
        BasicRestriction<Book,String> notEndsWithJakartaEE =
                (BasicRestriction<Book, String>) endsWithJakartaEE.negate();
        @SuppressWarnings("unchecked")
        BasicRestriction<Book,String> notNotEndsWithJakartaEE =
                (BasicRestriction<Book, String>) notEndsWithJakartaEE.negate();

        Like endsWithJakartaEEConstraint =
                (Like) endsWithJakartaEE.constraint();

        NotLike notEndsWithJakartaEEConstraint =
                (NotLike) notEndsWithJakartaEE.constraint();

        Like notNotEndsWithJakartaEEConstraint =
                (Like) notNotEndsWithJakartaEE.constraint();

        StringLiteral endsWithJakartaEELiteral =
                (StringLiteral) endsWithJakartaEEConstraint.escapedPattern();

        StringLiteral notEndsWithJakartaEELiteral =
                (StringLiteral) notEndsWithJakartaEEConstraint.escapedPattern();

        StringLiteral notNotEndsWithJakartaEELiteral =
                (StringLiteral) notNotEndsWithJakartaEEConstraint.escapedPattern();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(endsWithJakartaEELiteral.value())
                .isEqualTo("%Jakarta EE");

            soft.assertThat(notEndsWithJakartaEELiteral.value())
                .isEqualTo("%Jakarta EE");

            soft.assertThat(notNotEndsWithJakartaEELiteral.value())
                .isEqualTo("%Jakarta EE");
        });
    }

    @Test
    void shouldOutputToString() {
        @SuppressWarnings("unchecked")
        BasicRestriction<Book,String> titleRestriction =
                (BasicRestriction<Book, String>) _Book.title.contains("Jakarta Data");
        @SuppressWarnings("unchecked")
        BasicRestriction<Book,String> authorRestriction =
                (BasicRestriction<Book, String>) _Book.author.equalTo("Myself")
                // TODO TextRestriction.ignoreCase vs TextAttribute.upper/lowercased
                //        .ignoreCase()
                        .negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(titleRestriction.toString()).isEqualTo("""
                    title LIKE '%Jakarta Data%'\
                    """);
            soft.assertThat(authorRestriction.toString()).isEqualTo("""
                    author <> 'Myself'\
                    """);
        });
    }

    @Test
    void shouldSupportNegationForTextRestriction() {
        @SuppressWarnings("unchecked")
        BasicRestriction<Book,String> restriction =
                (BasicRestriction<Book, String>) _Book.author.equalTo("John Doe").negate();

        NotEqualTo<String> constraint =
                (NotEqualTo<String>) restriction.constraint();
        StringLiteral literal = (StringLiteral) constraint.expression();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.expression()).isEqualTo(_Book.author);
            // TODO TextRestriction.ignoreCase vs TextAttribute.upper/lowercased
            //soft.assertThat(restriction.isCaseSensitive()).isTrue();
            soft.assertThat(literal.value()).isEqualTo("John Doe");
        });
    }

    @Test
    void shouldThrowExceptionWhenAttributeIsNullInTextRestriction() {
        assertThatThrownBy(() -> TextAttribute.of(_Book.class, null).equalTo("testValue"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("The name argument is required");
    }
}
