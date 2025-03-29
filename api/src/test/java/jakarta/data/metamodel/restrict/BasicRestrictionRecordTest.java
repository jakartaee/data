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

import jakarta.data.metamodel.BasicAttribute;
import jakarta.data.metamodel.ComparableAttribute;
import jakarta.data.metamodel.TextAttribute;
import jakarta.data.metamodel.constraint.*;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class BasicRestrictionRecordTest {
    // A mock static metamodel class for tests
    interface _Book {
        String AUTHOR = "author";
        String ID = "id";
        String NUMCHAPTERS = "numChapters";
        String NUMPAGES = "numPages";
        String TITLE = "title";

        BasicAttribute<Book, String> author = BasicAttribute.of(
                Book.class, AUTHOR, String.class);
        TextAttribute<Book> id = TextAttribute.of(
                Book.class, ID);
        ComparableAttribute<Book, Integer> numChapters = ComparableAttribute.of(
                Book.class, NUMCHAPTERS, int.class);
        ComparableAttribute<Book, Integer> numPages = ComparableAttribute.of(
                Book.class, NUMPAGES, int.class);
        BasicAttribute<Book, String> title = BasicAttribute.of(
                Book.class, TITLE, String.class);
    }

    // A mock entity class for tests
    static class Book {
        String author;
        String id;
        int numChapters;
        int numPages;
        String title;
    }

    @Test
    void shouldCreateBasicRestrictionWithDefaultNegation() {
        BasicRestriction<Book, String> restriction =
                (BasicRestriction<Book, String>) _Book.title.equalTo("Java Guide");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(restriction.constraint()).isEqualTo(EqualTo.value("Java Guide"));
        });
    }

    @Test
    void shouldCreateBasicRestrictionWithExplicitNegation() {
        BasicRestriction<Book, String> restriction =
                (BasicRestriction<Book, String>) _Book.title.equalTo("Java Guide")
                        .negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(restriction.constraint()).isEqualTo(NotEqualTo.value("Java Guide"));
        });
    }

    @Test
    void shouldNegateLTERestriction() {
        BasicRestriction<Book, Integer> numChaptersLTE10Basic =
                (BasicRestriction<Book, Integer>) _Book.numChapters.lessThanEqual(10);
        BasicRestriction<Book, Integer> numChaptersGT10Basic = numChaptersLTE10Basic.negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(numChaptersLTE10Basic.constraint()).isEqualTo(LessThanOrEqual.max(10));

            soft.assertThat(numChaptersGT10Basic.constraint()).isEqualTo(GreaterThan.bound(10));
        });
    }

    @Test
    void shouldNegateNegatedRestriction() {
        BasicRestriction<Book, String> titleRestrictionBasic =
                (BasicRestriction<Book, String>) _Book.title.equalTo("A Developer's Guide to Jakarta Data");
        BasicRestriction<Book, String> negatedTitleRestrictionBasic =
                titleRestrictionBasic.negate();
        BasicRestriction<Book, String> negatedNegatedTitleRestrictionBasic =
                negatedTitleRestrictionBasic.negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(titleRestrictionBasic.constraint())
                .isEqualTo(EqualTo.value("A Developer's Guide to Jakarta Data"));

            soft.assertThat(negatedNegatedTitleRestrictionBasic.constraint())
                .isEqualTo(EqualTo.value("A Developer's Guide to Jakarta Data"));

            soft.assertThat(titleRestrictionBasic.constraint())
                .isEqualTo(EqualTo.value("A Developer's Guide to Jakarta Data"));
        });
    }

    @Test
    void shouldOutputToString() {
        Restriction<Book> restriction =
                (BasicRestriction<Book, Integer>) _Book.numPages.greaterThan(100);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.toString())
                .isEqualTo("numPages > 100");
        });
    }

    @Test
    void shouldSupportNegatedRestrictionUsingDefaultConstructor() {
        BasicRestriction<Book, String> negatedRestriction =
                (BasicRestriction<Book, String>) _Book.author.notEqualTo("Unknown");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(negatedRestriction.attribute()).isEqualTo("author");
            soft.assertThat(negatedRestriction.constraint()).isEqualTo(NotEqualTo.value("Unknown"));
        });
    }

    @Test
    void shouldThrowExceptionWhenAttributeIsNull() {
        assertThatThrownBy(() -> BasicAttribute.of(Book.class, null, Object.class).equalTo(("testValue")))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("entity attribute name is required");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatThrownBy(() -> _Book.title.equalTo((String) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Value must not be null");
    }
}
