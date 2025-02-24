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

        ComparableAttribute<Employee, Integer> badgeNum = ComparableAttribute.of(
                Employee.class, BADGENUM, int.class);
        TextAttribute<Employee> name = TextAttribute.of(
                Employee.class, NAME);
        TextAttribute<Employee> position = TextAttribute.of(
                Employee.class, POSITION);
        ComparableAttribute<Employee, Integer> yearHired = ComparableAttribute.of(
                Employee.class, YEARHIRED, int.class);
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
        BasicRestriction<Employee> restriction = Restrict.equalTo(0, "attributeName");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("attributeName");
            soft.assertThat(restriction.constraint()).isInstanceOf(EqualTo.class);
            soft.assertThat(restriction.constraint()).isEqualTo(Constraint.equalTo(0));
        });
    }

    @Test
    void shouldCreateNotEqualToRestriction() {
        BasicRestriction<Employee> restriction = Restrict.notEqualTo(0, "attributeName");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("attributeName");
            soft.assertThat(restriction.constraint()).isInstanceOf(NotEqualTo.class);
            soft.assertThat(restriction.constraint()).isEqualTo(NotEqualTo.value(0));
        });
    }

    @Test
    void shouldCreateEqualToStringRestriction() {
        TextRestriction<Employee> restriction = Restrict.equalTo("value", "attributeName");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("attributeName");
            soft.assertThat(restriction.constraint()).isInstanceOf(EqualTo.class);
            soft.assertThat(restriction.constraint()).isEqualTo(EqualTo.value("value"));
        });
    }

    @Test
    void shouldCreateNotEqualToStringRestriction() {
        TextRestriction<Employee> restriction = Restrict.notEqualTo("value", "attributeName");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("attributeName");
            soft.assertThat(restriction.constraint()).isInstanceOf(NotEqualTo.class);
            soft.assertThat(restriction.constraint()).isEqualTo(NotEqualTo.value("value"));
        });
    }

    @Test
    void shouldCombineAllRestrictionsWithNegation() {
        Restriction<Employee> r1 = Restrict.notEqualTo("value1", "attributeName1");
        Restriction<Employee> r2 = Restrict.greaterThan(100, "attributeName2");

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
        TextRestriction<Employee> restriction = Restrict.contains("substring", "attributeName");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("attributeName");
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(((Like) restriction.constraint()).pattern()).isEqualTo("%substring%");
        });
    }

    @Test
    void shouldCreateNegatedContainsRestriction() {
        TextRestriction<Employee> restriction = Restrict.notContains("substring", "attributeName");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("attributeName");
            soft.assertThat(restriction.constraint()).isInstanceOf(NotLike.class);
            soft.assertThat(((NotLike) restriction.constraint()).pattern()).isEqualTo("%substring%");
        });
    }

    @Test
    void shouldCreateStartsWithRestriction() {
        TextRestriction<Employee> restriction = Restrict.startsWith("prefix", "attributeName");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("attributeName");
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(((Like) restriction.constraint()).pattern()).isEqualTo("prefix%");
        });
    }

    @Test
    void shouldCreateNegatedStartsWithRestriction() {
        TextRestriction<Employee> restriction = Restrict.notStartsWith("prefix", "attributeName");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("attributeName");
            soft.assertThat(restriction.constraint()).isInstanceOf(NotLike.class);
            soft.assertThat(((NotLike) restriction.constraint()).pattern()).isEqualTo("prefix%");
        });
    }

    @Test
    void shouldCreateEndsWithRestriction() {
        TextRestriction<Employee> restriction = Restrict.endsWith("suffix", "attributeName");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("attributeName");
            soft.assertThat(restriction.constraint()).isInstanceOf(Like.class);
            soft.assertThat(((Like) restriction.constraint()).pattern()).isEqualTo("%suffix");
        });
    }

    @Test
    void shouldCreateNegatedEndsWithRestriction() {
        TextRestriction<Employee> restriction = Restrict.notEndsWith("suffix", "attributeName");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("attributeName");
            soft.assertThat(restriction.constraint()).isInstanceOf(NotLike.class);
            soft.assertThat(((NotLike) restriction.constraint()).pattern()).isEqualTo("%suffix");
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
        Like like = (Like) Restrict.endsWith("test_value", "attributeName").constraint();
        String result = like.pattern();

        assertThat(result).isEqualTo("%test\\_value");
    }

    @Test
    void shouldSupplyUnrestrictedToRepositoryMethod() {
        this.findByPosition("Software Engineer", Restrict.unrestricted());
    }

    @Test
    void shouldThrowExceptionForInvalidWildcard() {
        assertThatThrownBy(() -> Restrict.like("pattern_value", '_', '_', "attributeName"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot use the same character (_) for both wildcards.");
    }
}
