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


import jakarta.data.metamodel.constraint.Constraint;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;


class CompositeRestrictionRecordTest {
    // A mock entity class for tests
    static class Person {
    }

    @Test
    void shouldCreateCompositeRestrictionWithDefaultNegation() {
        Restriction<String> restriction1 = new BasicRestrictionRecord<>("title", Constraint.equalTo("Java Guide"));
        Restriction<String> restriction2 = new BasicRestrictionRecord<>("author", Constraint.equalTo("John Doe"));

        CompositeRestrictionRecord<String> composite = new CompositeRestrictionRecord<>(
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
        Restriction<String> restriction1 = new BasicRestrictionRecord<>("title", Constraint.equalTo("Java Guide"));
        Restriction<String> restriction2 = new BasicRestrictionRecord<>("author", Constraint.equalTo("John Doe"));

        CompositeRestrictionRecord<String> composite = new CompositeRestrictionRecord<>(
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
        Restriction<Person> ageLessThan50 = Restrict.lessThan(50, "age");
        Restriction<Person> nameStartsWithDuke = Restrict.startsWith("Duke ", "name");
        CompositeRestriction<Person> all =
                (CompositeRestriction<Person>) Restrict.all(ageLessThan50, nameStartsWithDuke);
        CompositeRestriction<Person> allNegated = all.negate();
        CompositeRestriction<Person> notAll =
                (CompositeRestriction<Person>) Restrict.not(all);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(all.isNegated()).isEqualTo(false);

            soft.assertThat(allNegated.isNegated()).isEqualTo(true);

            soft.assertThat(notAll.isNegated()).isEqualTo(true);
        });
    }

    @Test
    void shouldNegateNegatedCompositeRestriction() {
        Restriction<Person> ageBetween20and30 = Restrict.between(20, 30, "age");
        Restriction<Person> nameContainsDuke = Restrict.contains("Duke", "name");
        CompositeRestriction<Person> any =
                (CompositeRestriction<Person>) Restrict.any(ageBetween20and30, nameContainsDuke);
        CompositeRestriction<Person> anyNegated = any.negate();
        CompositeRestriction<Person> anyNotNegated =
                (CompositeRestriction<Person>) Restrict.not(anyNegated);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(any.isNegated()).isEqualTo(false);

            soft.assertThat(anyNegated.isNegated()).isEqualTo(true);

            soft.assertThat(anyNotNegated.isNegated()).isEqualTo(false);
        });
    }

    @Test
    void shouldOutputToString() {
        Restriction<Person> namedJackKarta = Restrict
                .all(Restrict.equalTo("Jack", "firstName"),
                     Restrict.equalTo("Karta", "lastName"));

        Restriction<Person> minorOrMissingName = Restrict
                .any(Restrict.lessThan(18, "age"),
                     Restrict.equalTo("null", "name"));
        Restriction<Person> notMinorOrMissingName = minorOrMissingName.negate();

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
        Restriction<String> restriction1 = new BasicRestrictionRecord<>("title", Constraint.equalTo("Java Guide"));
        Restriction<String> restriction2 = new BasicRestrictionRecord<>("author", Constraint.equalTo("John Doe"));

        CompositeRestrictionRecord<String> composite = new CompositeRestrictionRecord<>(
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
        Restriction<String> restriction1 = new BasicRestrictionRecord<>("title", Constraint.equalTo("Java Guide"));
        Restriction<String> restriction2 = new BasicRestrictionRecord<>("author", Constraint.equalTo("John Doe"));

        // When creating a composite restriction and manually setting negation
        CompositeRestrictionRecord<String> composite = new CompositeRestrictionRecord<>(
                CompositeRestriction.Type.ALL,
                List.of(restriction1, restriction2)
        );
        CompositeRestrictionRecord<String> negatedComposite = new CompositeRestrictionRecord<>(
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
