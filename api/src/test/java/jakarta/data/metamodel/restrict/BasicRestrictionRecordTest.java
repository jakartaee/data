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
import jakarta.data.metamodel.range.Range;
import jakarta.data.metamodel.range.UpperBound;
import jakarta.data.metamodel.range.Value;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class BasicRestrictionRecordTest {
    // A mock entity class for tests
    static class Book {
    }

    @Test
    void shouldCreateBasicRestrictionWithDefaultNegation() {
        BasicRestrictionRecord<String> restriction = new BasicRestrictionRecord<>("title", new Value<>("Java Guide"));

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.EQUAL);
            soft.assertThat(restriction.range()).isEqualTo(new Value<>("Java Guide"));
        });
    }

    @Test
    void shouldCreateBasicRestrictionWithExplicitNegation() {
        BasicRestriction<Book> restriction = Restrict.<Book>equalTo("Java Guide", "title")
                        .negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("title");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.NOT_EQUAL);
            soft.assertThat(restriction.range()).isEqualTo(Pattern.literal("Java Guide"));
        });
    }

    @Test
    void shouldNegateLTERestriction() {
        BasicRestriction<Book> numChaptersLTE10 = Restrict.lessThanEqual(10, "numChapters");
        BasicRestriction<Book> numChaptersLTE10Basic = numChaptersLTE10;
        BasicRestriction<Book> numChaptersGT10Basic = numChaptersLTE10Basic.negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(numChaptersLTE10Basic.comparison()).isEqualTo(Operator.LESS_THAN_EQUAL);
            soft.assertThat(numChaptersLTE10Basic.range()).isEqualTo(new UpperBound<>(10));

            soft.assertThat(numChaptersGT10Basic.comparison()).isEqualTo(Operator.GREATER_THAN);
            soft.assertThat(numChaptersGT10Basic.range()).isEqualTo(new UpperBound<>(10));
        });
    }

    @Test
    void shouldNegateNegatedRestriction() {
        BasicRestriction<Book> titleRestrictionBasic =
                Restrict.equalTo("A Developer's Guide to Jakarta Data", "title");
        BasicRestriction<Book> negatedTitleRestrictionBasic = titleRestrictionBasic.negate();
        BasicRestriction<Book> negatedNegatedTitleRestrictionBasic = negatedTitleRestrictionBasic.negate();

        Range<String> expected = Pattern.literal("A Developer's Guide to Jakarta Data");
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(titleRestrictionBasic.comparison())
                .isEqualTo(Operator.EQUAL);
            soft.assertThat(titleRestrictionBasic.range())
                .isEqualTo(expected);

            soft.assertThat(negatedTitleRestrictionBasic.comparison())
                .isEqualTo(Operator.NOT_EQUAL);
            soft.assertThat(titleRestrictionBasic.range())
                .isEqualTo(expected);

            soft.assertThat(negatedNegatedTitleRestrictionBasic.comparison())
                .isEqualTo(Operator.EQUAL);
            soft.assertThat(titleRestrictionBasic.range())
                .isEqualTo(expected);
        });
    }

    @Test
    void shouldOutputToString() {
        Restriction<Book> restriction = Restrict.greaterThan(100, "numPages");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.toString())
                .isEqualTo("numPages > 100");
        });
    }

    @Test
    void shouldSupportNegatedRestrictionUsingDefaultConstructor() {
        BasicRestriction<Book> negatedRestriction = Restrict.notEqualTo((Object) "Unknown", "author");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(negatedRestriction.attribute()).isEqualTo("author");
            soft.assertThat(negatedRestriction.comparison()).isEqualTo(Operator.NOT_EQUAL);
            soft.assertThat(negatedRestriction.range()).isEqualTo(new Value<>("Unknown"));
        });
    }

    @Test
    void shouldThrowExceptionWhenAttributeIsNull() {
        assertThatThrownBy(() -> new BasicRestrictionRecord<>(null, new Value<>("testValue")))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Attribute must not be null");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatThrownBy(() -> new BasicRestrictionRecord<>("title", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Range must not be null");
    }
}
