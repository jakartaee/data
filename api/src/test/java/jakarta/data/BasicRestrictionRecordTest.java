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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class BasicRestrictionRecordTest {
    // A mock entity class for tests
    static class Book {
    }

    @Test
    void shouldCreateBasicRestrictionWithDefaultNegation() {
        BasicRestrictionRecord<String> restriction = new BasicRestrictionRecord<>("title", Operator.EQUAL, "Java Guide");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.EQUAL);
            soft.assertThat(restriction.value()).isEqualTo("Java Guide");
        });
    }

    @Test
    void shouldCreateBasicRestrictionWithExplicitNegation() {
        BasicRestriction<Book> restriction =
                (BasicRestriction<Book>) Restrict.<Book>equalTo("Java Guide", "title")
                        .negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.NOT_EQUAL);
            soft.assertThat(restriction.value()).isEqualTo("Java Guide");
        });
    }

    @Test
    void shouldCreateBasicRestrictionWithNullValue() {
        // Create a restriction with a null value
        BasicRestrictionRecord<String> restriction = new BasicRestrictionRecord<>("title", Operator.EQUAL, null);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.EQUAL);
            soft.assertThat(restriction.value()).isNull();
        });
    }

    @Test
    void shouldNegateLTERestriction() {
        Restriction<Book> numChaptersLTE10 = Restrict.lessThanEqual(10, "numChapters");
        BasicRestriction<Book> numChaptersLTE10Basic = (BasicRestriction<Book>) numChaptersLTE10;
        BasicRestriction<Book> numChaptersGT10Basic = (BasicRestriction<Book>) numChaptersLTE10Basic.negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(numChaptersLTE10Basic.comparison()).isEqualTo(Operator.LESS_THAN_EQUAL);
            soft.assertThat(numChaptersLTE10Basic.value()).isEqualTo(10);

            soft.assertThat(numChaptersGT10Basic.comparison()).isEqualTo(Operator.GREATER_THAN);
            soft.assertThat(numChaptersGT10Basic.value()).isEqualTo(10);
        });
    }

    @Test
    void shouldNegateNegatedRestriction() {
        Restriction<Book> titleRestriction =
                Restrict.equalTo("A Developer's Guide to Jakarta Data", "title");
        BasicRestriction<Book> titleRestrictionBasic =
                (BasicRestriction<Book>) titleRestriction;
        BasicRestriction<Book> negatedTitleRestrictionBasic =
                (BasicRestriction<Book>) titleRestriction.negate();
        BasicRestriction<Book> negatedNegatedTitleRestrictionBasic =
                (BasicRestriction<Book>) negatedTitleRestrictionBasic.negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(titleRestrictionBasic.comparison())
                .isEqualTo(Operator.EQUAL);
            soft.assertThat(titleRestrictionBasic.value())
                .isEqualTo("A Developer's Guide to Jakarta Data");

            soft.assertThat(negatedTitleRestrictionBasic.comparison())
                .isEqualTo(Operator.NOT_EQUAL);
            soft.assertThat(negatedTitleRestrictionBasic.value())
                .isEqualTo("A Developer's Guide to Jakarta Data");

            soft.assertThat(negatedNegatedTitleRestrictionBasic.comparison())
                .isEqualTo(Operator.EQUAL);
            soft.assertThat(negatedNegatedTitleRestrictionBasic.value())
                .isEqualTo("A Developer's Guide to Jakarta Data");
        });
    }

    @Test
    void shouldSupportNegatedRestrictionUsingDefaultConstructor() {
        BasicRestriction<Book> negatedRestriction =
                (BasicRestriction<Book>) Restrict.<Book>notEqualTo((Object) "Unknown", "author");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(negatedRestriction.attribute()).isEqualTo("author");
            soft.assertThat(negatedRestriction.comparison()).isEqualTo(Operator.NOT_EQUAL);
            soft.assertThat(negatedRestriction.value()).isEqualTo("Unknown");
        });
    }

    @Test
    void shouldThrowExceptionWhenAttributeIsNull() {
        assertThatThrownBy(() -> new BasicRestrictionRecord<>(null, Operator.EQUAL, "testValue"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Attribute must not be null");
    }
}
