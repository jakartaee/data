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
package jakarta.data.metamodel;

import jakarta.data.metamodel.constraint.Between;
import jakarta.data.metamodel.constraint.Constraint;
import jakarta.data.metamodel.constraint.GreaterThan;
import jakarta.data.metamodel.constraint.GreaterThanOrEqual;
import jakarta.data.metamodel.constraint.LessThan;
import jakarta.data.metamodel.constraint.LessThanOrEqual;
import jakarta.data.metamodel.restrict.BasicRestriction;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;


class ComparableAttributeTest {
    // Mock entity class for tests
    static class Person {
        String firstName;
        String lastName;
        int ssn;
        int testAttribute;
    }

    private final ComparableAttribute<Person, Integer> testAttribute =
            ComparableAttribute.of(Person.class, "testAttribute", Integer.class);

    @Test
    void shouldCreateGreaterThanRestriction() {
        BasicRestriction<Person, Integer> restriction =
                (BasicRestriction<Person, Integer>) testAttribute.greaterThan(10);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(restriction.constraint()).isInstanceOf(GreaterThan.class);
            soft.assertThat(restriction.constraint()).isEqualTo(Constraint.greaterThan(10));
        });
    }

    @Test
    void shouldCreateGreaterThanEqualRestriction() {
        BasicRestriction<Person, Integer> restriction =
                (BasicRestriction<Person, Integer>) testAttribute.greaterThanEqual(10);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(restriction.constraint()).isInstanceOf(GreaterThanOrEqual.class);
            soft.assertThat(restriction.constraint()).isEqualTo(Constraint.greaterThanOrEqual(10));
        });
    }

    @Test
    void shouldCreateLessThanRestriction() {
        BasicRestriction<Person, Integer> restriction =
                (BasicRestriction<Person, Integer>) testAttribute.lessThan(10);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(restriction.constraint()).isInstanceOf(LessThan.class);
            soft.assertThat(restriction.constraint()).isEqualTo(Constraint.lessThan(10));
        });
    }

    @Test
    void shouldCreateLessThanOrEqualRestriction() {
        BasicRestriction<Person, Integer> restriction =
                (BasicRestriction<Person, Integer>) testAttribute.lessThanEqual(10);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(restriction.constraint()).isInstanceOf(LessThanOrEqual.class);
            soft.assertThat(restriction.constraint()).isEqualTo(Constraint.lessThanOrEqual(10));
        });
    }

    @Test
    void shouldCreateBetweenRestriction() {
        BasicRestriction<Person, Integer> restriction =
                (BasicRestriction<Person, Integer>) testAttribute.between(5, 15);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            soft.assertThat(restriction.expression()).isEqualTo(testAttribute);
            soft.assertThat(restriction.constraint()).isInstanceOf(Between.class);
            soft.assertThat(restriction.constraint()).isEqualTo(Constraint.between(5,15));
        });
    }
}
