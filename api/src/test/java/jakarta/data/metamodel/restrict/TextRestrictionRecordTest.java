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

import jakarta.data.metamodel.range.Pattern;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import jakarta.data.metamodel.restrict.BasicRestrictionRecordTest.Book;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class TextRestrictionRecordTest {

    @Test
    void shouldCreateTextRestrictionWithDefaultValues() {
        BasicRestrictionRecord<String> restriction = new BasicRestrictionRecord<>(
                "title",
                Operator.LIKE,
                new Pattern("%Java%")
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(((Pattern)restriction.range()).pattern()).isEqualTo("%Java%");
            soft.assertThat(((Pattern)restriction.range()).caseSensitive()).isTrue();
//            soft.assertThat(restriction.isEscaped()).isFalse();
        });
    }

    @Test
    void shouldCreateTextRestrictionWithExplicitNegation() {
        BasicRestrictionRecord<String> restriction = new BasicRestrictionRecord<>(
                "title",
                Operator.NOT_LIKE,
                new Pattern("%Java%")
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.NOT_LIKE);
            soft.assertThat(((Pattern)restriction.range()).pattern()).isEqualTo("%Java%");
            soft.assertThat(((Pattern)restriction.range()).caseSensitive()).isTrue();
//            soft.assertThat(restriction.isEscaped()).isFalse();
        });
    }

    @Test
    void shouldIgnoreCaseForTextRestriction() {
        BasicRestrictionRecord<String> caseInsensitiveRestriction = new BasicRestrictionRecord<>(
                "title",
                Operator.LIKE,
                new Pattern("%Java%", false)
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(caseInsensitiveRestriction).isInstanceOf(BasicRestrictionRecord.class);
            soft.assertThat(caseInsensitiveRestriction.attribute()).isEqualTo("title");
            soft.assertThat(caseInsensitiveRestriction.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(((Pattern)caseInsensitiveRestriction.range()).pattern()).isEqualTo("%Java%");
            soft.assertThat(((Pattern)caseInsensitiveRestriction.range()).caseSensitive()).isFalse();
//            soft.assertThat(textRestriction.isEscaped()).isFalse();
        });
    }

    @Test
    void shouldCreateTextRestrictionWithEscapedValue() {
        BasicRestrictionRecord<String> restriction = new BasicRestrictionRecord<>(
                "title",
                Operator.LIKE,
                new Pattern("%Java%")
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(((Pattern)restriction.range()).pattern()).isEqualTo("%Java%");
            soft.assertThat(((Pattern)restriction.range()).caseSensitive()).isTrue();
//            soft.assertThat(restriction.isEscaped()).isTrue();
        });
    }

    @Test
    void shouldNegateLikeRestriction() {
        BasicRestriction<Book> likeJakartaEE = Restrict.like("%Jakarta EE%", "title");
        BasicRestriction<Book> notLikeJakartaEE = likeJakartaEE.negate();
//        BasicRestriction<Book> anyCaseNotLikeJakartaEE = likeJakartaEE.ignoreCase().negate();
//        BasicRestriction<Book> notLikeJakartaEEAnyCase = likeJakartaEE.negate().ignoreCase();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(likeJakartaEE.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(((Pattern)notLikeJakartaEE.range()).caseSensitive()).isTrue();

            soft.assertThat(notLikeJakartaEE.comparison()).isEqualTo(Operator.NOT_LIKE);
            soft.assertThat(((Pattern)notLikeJakartaEE.range()).caseSensitive()).isTrue();

//            soft.assertThat(anyCaseNotLikeJakartaEE.comparison()).isEqualTo(Operator.NOT_LIKE);
//            soft.assertThat(anyCaseNotLikeJakartaEE.isCaseSensitive()).isFalse();
//
//            soft.assertThat(notLikeJakartaEEAnyCase.comparison()).isEqualTo(Operator.NOT_LIKE);
//            soft.assertThat(notLikeJakartaEEAnyCase.isCaseSensitive()).isFalse();
        });
    }

    @Test
    void shouldNegateNegatedRestriction() {
        BasicRestriction<Book> endsWithJakartaEE = Restrict.endsWith("Jakarta EE", "title");
        BasicRestriction<Book> notEndsWithJakartaEE = endsWithJakartaEE.negate();
        BasicRestriction<Book> notNotEndsWithJakartaEE = notEndsWithJakartaEE.negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(endsWithJakartaEE.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(((Pattern)endsWithJakartaEE.range()).pattern()).isEqualTo("%Jakarta EE");

            soft.assertThat(notEndsWithJakartaEE.comparison()).isEqualTo(Operator.NOT_LIKE);
            soft.assertThat(((Pattern)notEndsWithJakartaEE.range()).pattern()).isEqualTo("%Jakarta EE");

            soft.assertThat(notNotEndsWithJakartaEE.comparison()).isEqualTo(Operator.LIKE);
            soft.assertThat(((Pattern)notNotEndsWithJakartaEE.range()).pattern()).isEqualTo("%Jakarta EE");
        });
    }

//    @Test
//    void shouldOutputToString() {
//        BasicRestriction<Book> titleRestriction =
//                Restrict.contains("Jakarta Data", "title");
//        BasicRestriction<Book> authorRestriction =
//                Restrict.<Book>equalTo("Myself", "author").ignoreCase().negate();
//
//        SoftAssertions.assertSoftly(soft -> {
//            soft.assertThat(titleRestriction.toString()).isEqualTo("""
//                    title LIKE "%Jakarta Data%" ESCAPED\
//                    """);
//            soft.assertThat(authorRestriction.toString()).isEqualTo("""
//                    author <> "Myself" IGNORE_CASE\
//                    """);
//        });
//    }

//    @Test
//    void shouldSupportNegationForTextRestriction() {
//        BasicRestrictionRecord<String> restriction = new BasicRestrictionRecord<>(
//                "author",
//                Operator.NOT_EQUAL,
//                new Value<>("John Doe")
//        );
//
//        SoftAssertions.assertSoftly(soft -> {
//            soft.assertThat(restriction.attribute()).isEqualTo("author");
//            soft.assertThat(restriction.comparison()).isEqualTo(Operator.NOT_EQUAL);
//            soft.assertThat(((Value<?>)restriction.range()).value()).isEqualTo("John Doe");
//            soft.assertThat(restriction.isCaseSensitive()).isTrue();
////            soft.assertThat(restriction.isEscaped()).isFalse();
//        });
//    }

    @Test
    void shouldThrowExceptionWhenAttributeIsNullInTextRestriction() {
        assertThatThrownBy(() -> new BasicRestrictionRecord<>(null, Operator.LIKE, new Pattern("testValue")))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Attribute must not be null");
    }
}
