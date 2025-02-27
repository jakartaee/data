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

import jakarta.data.metamodel.ComparableAttribute;
import jakarta.data.metamodel.TextAttribute;
import jakarta.data.metamodel.impl.ComparableAttributeRecord;
import jakarta.data.metamodel.impl.TextAttributeRecord;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;


class CompositeRestrictionRecordTest {
    // A mock static metamodel class for tests
    interface _Author {
        String AGE = "age";
        String FIRSTNAME = "firstName";
        String LASTNAME = "lastName";
        String NAME = "name";
        String TITLEOFFIRSTBOOK = "titleOfFirstBook";

        ComparableAttribute<Author, Integer> age = new ComparableAttributeRecord<>(AGE);
        TextAttribute<Author> firstName = new TextAttributeRecord<>(FIRSTNAME);
        TextAttribute<Author> lastName = new TextAttributeRecord<>(LASTNAME);
        TextAttribute<Author> name = new TextAttributeRecord<>(NAME);
        TextAttribute<Author> titleOfFirstBook = new TextAttributeRecord<>(TITLEOFFIRSTBOOK);
    }

    // A mock entity class for tests
    static class Author {
        int age;
        String firstName;
        String lastName;
        String name;
    }

    @Test
    void shouldCreateCompositeRestrictionWithDefaultNegation() {
        Restriction<Author> restriction1 = _Author.titleOfFirstBook.equalTo("Java Guide");
        Restriction<Author> restriction2 = _Author.name.equalTo("John Doe");

        CompositeRestrictionRecord<Author> composite = new CompositeRestrictionRecord<>(
                CompositeRestriction.Type.ALL,
                List.of(restriction1, restriction2)
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(composite.type()).isEqualTo(CompositeRestriction.Type.ALL);
            soft.assertThat(composite.restrictions()).containsExactly(restriction1, restriction2);
            soft.assertThat(composite.isNegated()).isFalse();
        });
    }

    @Test
    void shouldCreateCompositeRestrictionWithExplicitNegation() {
        Restriction<Author> restriction1 = _Author.titleOfFirstBook.equalTo("Java Guide");
        Restriction<Author> restriction2 = _Author.name.equalTo("John Doe");

        CompositeRestrictionRecord<Author> composite = new CompositeRestrictionRecord<>(
                CompositeRestriction.Type.ANY,
                List.of(restriction1, restriction2),
                true
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(composite.type()).isEqualTo(CompositeRestriction.Type.ANY);
            soft.assertThat(composite.restrictions()).containsExactly(restriction1, restriction2);
            soft.assertThat(composite.isNegated()).isTrue();
        });
    }

    @Test
    void shouldFailIfEmptyRestrictions() {
        assertThatThrownBy(() -> new CompositeRestrictionRecord<>(CompositeRestriction.Type.ALL, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot create a composite restriction without any restrictions to combine.");
    }

    @Test
    void shouldNegateCompositeRestriction() {
        Restriction<Author> ageLessThan50 = _Author.age.lessThan(50);
        Restriction<Author> nameStartsWithDuke = _Author.name.startsWith("Duke ");
        CompositeRestriction<Author> all =
                (CompositeRestriction<Author>) Restrict.all(ageLessThan50, nameStartsWithDuke);
        CompositeRestriction<Author> allNegated = all.negate();
        CompositeRestriction<Author> notAll =
                (CompositeRestriction<Author>) Restrict.not(all);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(all.isNegated()).isEqualTo(false);

            soft.assertThat(allNegated.isNegated()).isEqualTo(true);

            soft.assertThat(notAll.isNegated()).isEqualTo(true);
        });
    }

    @Test
    void shouldNegateNegatedCompositeRestriction() {
        Restriction<Author> ageBetween20and30 = _Author.age.between(20, 30);
        Restriction<Author> nameContainsDuke = _Author.name.contains("Duke");
        CompositeRestriction<Author> any =
                (CompositeRestriction<Author>) Restrict.any(ageBetween20and30, nameContainsDuke);
        CompositeRestriction<Author> anyNegated = any.negate();
        CompositeRestriction<Author> anyNotNegated =
                (CompositeRestriction<Author>) Restrict.not(anyNegated);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(any.isNegated()).isEqualTo(false);

            soft.assertThat(anyNegated.isNegated()).isEqualTo(true);

            soft.assertThat(anyNotNegated.isNegated()).isEqualTo(false);
        });
    }

    @Test
    void shouldOutputToString() {
        Restriction<Author> namedJackKarta = Restrict
                .all(_Author.firstName.equalTo("Jack"),
                     _Author.lastName.equalTo("Karta"));

        Restriction<Author> minorOrMissingName = Restrict
                .any(_Author.age.lessThan(18),
                     _Author.name.equalTo("null"));
        Restriction<Author> notMinorOrMissingName = minorOrMissingName.negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(namedJackKarta.toString()).isEqualTo("""
                    (firstName = 'Jack') AND (lastName = 'Karta')\
                    """);
            soft.assertThat(notMinorOrMissingName.toString()).isEqualTo("""
                    NOT ((age < 18) OR (name = 'null'))\
                    """);
        });
    }

    @Test
    void shouldPreserveRestrictionsOrder() {
        Restriction<Author> restriction1 = _Author.titleOfFirstBook.equalTo("Java Guide");
        Restriction<Author> restriction2 = _Author.name.equalTo("John Doe");

        CompositeRestrictionRecord<Author> composite = new CompositeRestrictionRecord<>(
                CompositeRestriction.Type.ALL,
                List.of(restriction1, restriction2)
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(composite.restrictions().get(0)).isEqualTo(restriction1);
            soft.assertThat(composite.restrictions().get(1)).isEqualTo(restriction2);
        });
    }

    @Test
    void shouldSupportNegationUsingDefaultConstructor() {
        // Given multiple restrictions
        Restriction<Author> restriction1 = _Author.titleOfFirstBook.equalTo("Java Guide");
        Restriction<Author> restriction2 = _Author.name.equalTo("John Doe");

        // When creating a composite restriction and manually setting negation
        CompositeRestrictionRecord<Author> composite = new CompositeRestrictionRecord<>(
                CompositeRestriction.Type.ALL,
                List.of(restriction1, restriction2)
        );
        CompositeRestrictionRecord<Author> negatedComposite = new CompositeRestrictionRecord<>(
                composite.type(),
                composite.restrictions(),
                true
        );

        // Then validate the negated composite restriction
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(negatedComposite.type()).isEqualTo(CompositeRestriction.Type.ALL);
            soft.assertThat(negatedComposite.restrictions()).containsExactly(restriction1, restriction2);
            soft.assertThat(negatedComposite.isNegated()).isTrue();
        });
    }
}
