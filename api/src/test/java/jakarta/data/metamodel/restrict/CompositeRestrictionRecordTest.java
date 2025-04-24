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

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
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

        ComparableAttribute<Author, Integer> age = ComparableAttribute.of(
                Author.class, AGE, int.class);
        TextAttribute<Author> firstName = TextAttribute.of(
                Author.class, FIRSTNAME);
        TextAttribute<Author> lastName = TextAttribute.of(
                Author.class, LASTNAME);
        TextAttribute<Author> name = TextAttribute.of(
                Author.class, NAME);
        TextAttribute<Author> titleOfFirstBook = TextAttribute.of(
                Author.class, TITLEOFFIRSTBOOK);
    }

    // A mock entity class for tests
    static class Author {
        int age;
        String firstName;
        String lastName;
        String name;
    }

    @Test
    @DisplayName("should create composite restriction with default negation (false)")
    void shouldCreateCompositeRestrictionWithDefaultNegation() {
        Restriction<Author> r1 = _Author.titleOfFirstBook.equalTo("Java Guide");
        Restriction<Author> r2 = _Author.name.equalTo("John Doe");

        CompositeRestrictionRecord<Author> composite = new CompositeRestrictionRecord<>(
                CompositeRestriction.Type.ALL, List.of(r1, r2));

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(composite.type()).isEqualTo(CompositeRestriction.Type.ALL);
            soft.assertThat(composite.restrictions()).containsExactly(r1, r2);
            soft.assertThat(composite.isNegated()).isFalse();
        });
    }

    @Test
    @DisplayName("should create composite restriction with explicit negation (true)")
    void shouldCreateCompositeRestrictionWithExplicitNegation() {
        Restriction<Author> r1 = _Author.titleOfFirstBook.equalTo("Java Guide");
        Restriction<Author> r2 = _Author.name.equalTo("John Doe");

        CompositeRestrictionRecord<Author> composite = new CompositeRestrictionRecord<>(
                CompositeRestriction.Type.ANY, List.of(r1, r2), true);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(composite.type()).isEqualTo(CompositeRestriction.Type.ANY);
            soft.assertThat(composite.restrictions()).containsExactly(r1, r2);
            soft.assertThat(composite.isNegated()).isTrue();
        });
    }

    @Test
    @DisplayName("should throw exception when composite restriction is created without restrictions")
    void shouldFailIfEmptyRestrictions() {
        assertThatThrownBy(() -> new CompositeRestrictionRecord<>(CompositeRestriction.Type.ALL, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot create a composite restriction without any restrictions to combine.");
    }

    @Test
    @DisplayName("should negate a composite restriction and support 'not' syntax")
    void shouldNegateCompositeRestriction() {
        Restriction<Author> age = _Author.age.lessThan(50);
        Restriction<Author> name = _Author.name.startsWith("Duke ");

        CompositeRestriction<Author> all = (CompositeRestriction<Author>) Restrict.all(age, name);
        CompositeRestriction<Author> negated = all.negate();
        CompositeRestriction<Author> fromNot = (CompositeRestriction<Author>) Restrict.not(all);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(all.isNegated()).isFalse();
            soft.assertThat(negated.isNegated()).isTrue();
            soft.assertThat(fromNot.isNegated()).isTrue();
        });
    }

    @Test
    @DisplayName("should negate a negated composite restriction and return to original")
    void shouldNegateNegatedCompositeRestriction() {
        Restriction<Author> r1 = _Author.age.between(20, 30);
        Restriction<Author> r2 = _Author.name.contains("Duke");

        CompositeRestriction<Author> any = (CompositeRestriction<Author>) Restrict.any(r1, r2);
        CompositeRestriction<Author> negated = any.negate();
        CompositeRestriction<Author> unNegated = (CompositeRestriction<Author>) Restrict.not(negated);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(any.isNegated()).isFalse();
            soft.assertThat(negated.isNegated()).isTrue();
            soft.assertThat(unNegated.isNegated()).isFalse();
        });
    }

    @Test
    @DisplayName("should correctly format toString for nested AND/OR composite restrictions")
    void shouldOutputToString() {
        Restriction<Author> jackKarta = Restrict.all(
                _Author.firstName.equalTo("Jack"),
                _Author.lastName.equalTo("Karta"));

        Restriction<Author> minorOrMissing = Restrict.any(
                _Author.age.lessThan(18),
                _Author.name.equalTo("null"));

        Restriction<Author> notMinorOrMissing = minorOrMissing.negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(jackKarta.toString()).isEqualTo("(firstName = 'Jack') AND (lastName = 'Karta')");
            soft.assertThat(notMinorOrMissing.toString()).isEqualTo("NOT ((age < 18) OR (name = 'null'))");
        });
    }

    @Test
    @DisplayName("should preserve the order of restrictions in composite")
    void shouldPreserveRestrictionsOrder() {
        Restriction<Author> r1 = _Author.titleOfFirstBook.equalTo("Java Guide");
        Restriction<Author> r2 = _Author.name.equalTo("John Doe");

        CompositeRestrictionRecord<Author> composite = new CompositeRestrictionRecord<>(
                CompositeRestriction.Type.ALL, List.of(r1, r2));

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(composite.restrictions().get(0)).isEqualTo(r1);
            soft.assertThat(composite.restrictions().get(1)).isEqualTo(r2);
        });
    }

    @Test
    @DisplayName("should support manual negation using constructor")
    void shouldSupportNegationUsingDefaultConstructor() {
        Restriction<Author> r1 = _Author.titleOfFirstBook.equalTo("Java Guide");
        Restriction<Author> r2 = _Author.name.equalTo("John Doe");

        CompositeRestrictionRecord<Author> original = new CompositeRestrictionRecord<>(
                CompositeRestriction.Type.ALL, List.of(r1, r2));

        CompositeRestrictionRecord<Author> negated = new CompositeRestrictionRecord<>(
                original.type(), original.restrictions(), true);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(negated.type()).isEqualTo(CompositeRestriction.Type.ALL);
            soft.assertThat(negated.restrictions()).containsExactly(r1, r2);
            soft.assertThat(negated.isNegated()).isTrue();
        });
    }
}
