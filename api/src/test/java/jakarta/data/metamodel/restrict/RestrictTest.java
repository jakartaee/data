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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.metamodel.restrict;

import jakarta.data.metamodel.constraint.Constraint;
import jakarta.data.metamodel.constraint.EqualTo;
import jakarta.data.metamodel.constraint.Like;
import jakarta.data.metamodel.constraint.NotEqualTo;
import jakarta.data.metamodel.constraint.NotLike;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import jakarta.data.metamodel.ComparableAttribute;
import jakarta.data.metamodel.TextAttribute;
import jakarta.data.metamodel.impl.ComparableAttributeRecord;
import jakarta.data.metamodel.impl.TextAttributeRecord;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;

class RestrictTest {
    // Mock static metamodel class for tests
    interface _Employee {
        String BADGENUM = "badgeNum";
        String NAME = "name";
        String POSITION = "position";
        String YEARHIRED = "yearHired";

        ComparableAttribute<Employee, Integer> badgeNum = new ComparableAttributeRecord<>(BADGENUM);
        TextAttribute<Employee> name = new TextAttributeRecord<>(NAME);
        TextAttribute<Employee> position = new TextAttributeRecord<>(POSITION);
        ComparableAttribute<Employee, Integer> yearHired = new ComparableAttributeRecord<>(YEARHIRED);
    }

    // Mock entity class for tests
    static class Employee {
        int badgeNum;
        String name;
        String position;
        int yearHired;
    }

    /**
     * Mock repository method for tests.
     */
    List<Employee> findByPosition(String position,
                                  Restriction<Employee> restriction) {
        return List.of();
    }

    @Test
    void shouldCreateEqualToRestriction() {
        BasicRestriction<Employee, Integer> restriction =
                (BasicRestriction<Employee, Integer>) _Employee.yearHired.equalTo(2020);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("yearHired");
            soft.assertThat(restriction.constraint()).isInstanceOf(EqualTo.class);
            soft.assertThat(restriction.constraint()).isEqualTo(Constraint.equalTo(2020));
        });
    }

    @Test
    void shouldCreateNotEqualToRestriction() {
        BasicRestriction<Employee, Integer> restriction =
                (BasicRestriction<Employee, Integer>) _Employee.badgeNum.notEqualTo(0);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("badgeNum");
            soft.assertThat(restriction.constraint()).isInstanceOf(NotEqualTo.class);
            soft.assertThat(restriction.constraint()).isEqualTo(NotEqualTo.value(0));
        });
    }

    @Test
    void shouldCreateEqualToStringRestriction() {
        TextRestriction<Employee> restriction = _Employee.position.equalTo("Software Engineer");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("position");
            soft.assertThat(restriction.constraint()).isInstanceOf(EqualTo.class);
            soft.assertThat(restriction.constraint()).isEqualTo(EqualTo.value("Software Engineer"));
        });
    }

    @Test
    void shouldCreateNotEqualToStringRestriction() {
        TextRestriction<Employee> restriction = _Employee.position.notEqualTo("Manager");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("position");
            soft.assertThat(restriction.constraint()).isInstanceOf(NotEqualTo.class);
            soft.assertThat(restriction.constraint()).isEqualTo(NotEqualTo.value("Manager"));
        });
    }

    @Test
    void shouldCombineAllRestrictionsWithNegation() {
        Restriction<Employee> r1 = _Employee.name.notEqualTo("Duke");
        Restriction<Employee> r2 = _Employee.yearHired.greaterThan(2010);

        Restriction<Employee> combined = Restrict.all(r1, r2);

        assertThat(combined).isInstanceOf(CompositeRestrictionRecord.class);

        CompositeRestrictionRecord<Employee> composite = (CompositeRestrictionRecord<Employee>) combined;
        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(composite.type()).isEqualTo(CompositeRestriction.Type.ALL);
            soft.assertThat(composite.restrictions()).containsExactly(r1, r2);
            soft.assertThat(composite.isNegated()).isFalse();
        });
    }

    @Test
    void shouldCreateContainsRestriction() {
        TextRestriction<Employee> restriction = _Employee.position.contains("Manager");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("position");
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(((Like) restriction.constraint()).pattern()).isEqualTo("%Manager%");
        });
    }

    @Test
    void shouldCreateNegatedContainsRestriction() {
        TextRestriction<Employee> restriction = _Employee.position.notContains("Director");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("position");
            soft.assertThat(restriction.constraint()).isInstanceOf(NotLike.class);
            soft.assertThat(((NotLike) restriction.constraint()).pattern()).isEqualTo("%Director%");
        });
    }

    @Test
    void shouldCreateStartsWithRestriction() {
        TextRestriction<Employee> restriction = _Employee.position.startsWith("Director");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("position");
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(((Like) restriction.constraint()).pattern()).isEqualTo("Director%");
        });
    }

    @Test
    void shouldCreateNegatedStartsWithRestriction() {
        TextRestriction<Employee> restriction = _Employee.position.notStartsWith("Manager");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("position");
            soft.assertThat(restriction.constraint()).isInstanceOf(NotLike.class);
            soft.assertThat(((NotLike) restriction.constraint()).pattern()).isEqualTo("Manager%");
        });
    }

    @Test
    void shouldCreateEndsWithRestriction() {
        TextRestriction<Employee> restriction = _Employee.position.endsWith("Manager");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("position");
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(((Like) restriction.constraint()).pattern()).isEqualTo("%Manager");
        });
    }

    @Test
    void shouldCreateNegatedEndsWithRestriction() {
        TextRestriction<Employee> restriction = _Employee.position.notEndsWith("Supervisor");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("position");
            soft.assertThat(restriction.constraint()).isInstanceOf(NotLike.class);
            soft.assertThat(((NotLike) restriction.constraint()).pattern()).isEqualTo("%Supervisor");
        });
    }

    @Test
    void shouldCreateUnmatchableRestrictionWhenNegatingUnrestricted() {
        Restriction<Employee> restriction = Restrict.unrestricted();
        Restriction<Employee> negated = restriction.negate();

        assertThat(negated).isInstanceOf(Unmatchable.class);

        Unmatchable<Employee> unmatchable = (Unmatchable<Employee>) negated;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(unmatchable.isNegated()).isEqualTo(false);
            soft.assertThat(unmatchable.negate()).isEqualTo(restriction);
            soft.assertThat(unmatchable.restrictions()).isEmpty();
            soft.assertThat(unmatchable.type()).isEqualTo(CompositeRestriction.Type.ANY);
        });
    }

    @Test
    void shouldCreateUnrestricted() {
        Restriction<Employee> restriction = Restrict.unrestricted();

        assertThat(restriction).isInstanceOf(Unrestricted.class);

        Unrestricted<Employee> unrestricted =
                (Unrestricted<Employee>) restriction;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(unrestricted.isNegated()).isEqualTo(false);
            soft.assertThat(unrestricted.restrictions()).isEmpty();
            soft.assertThat(unrestricted.type()).isEqualTo(CompositeRestriction.Type.ALL);
        });
    }

    @Test
    void shouldEscapeToLikePatternCorrectly() {
        Like like = (Like) _Employee.position.endsWith("test_value").constraint();
        String result = like.pattern();

        assertThat(result).isEqualTo("%test\\_value");
    }

    @Test
    void shouldIgnoreCase() {
        TextRestriction<Employee> restriction = _Employee.position.contains("SOFTWARE")
                .ignoreCase();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo(_Employee.POSITION);
            soft.assertThat(restriction.isCaseSensitive()).isFalse();
            soft.assertThat(restriction.constraint()).isEqualTo(Like.substring("SOFTWARE"));
        });
    }

    @Test
    void shouldSupplyUnrestrictedToRepositoryMethod() {
        this.findByPosition("Software Engineer", Restrict.unrestricted());
    }

    @Test
    void shouldThrowExceptionForInvalidWildcard() {
        assertThatThrownBy(() -> _Employee.name.like("pattern_value", '_', '_'))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot use the same character (_) for both wildcards.");
    }
}
