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

import jakarta.data.Sort;
import jakarta.data.metamodel.constraint.Between;
import jakarta.data.metamodel.constraint.Constraint;
import jakarta.data.metamodel.constraint.GreaterThan;
import jakarta.data.metamodel.constraint.GreaterThanOrEqual;
import jakarta.data.metamodel.constraint.LessThan;
import jakarta.data.metamodel.constraint.LessThanOrEqual;
import jakarta.data.metamodel.restrict.BasicRestriction;
import jakarta.data.metamodel.restrict.Restriction;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;


class SortableAttributeTest {
    // Mock entity class for tests
    static class Person {
        String firstName;
        String lastName;
        int ssn;
        int testAttribute;
    }

    //it ignores the implementation of the SortableAttribute interface and uses an anonymous class to test the methods
    private final ComparableAttribute<Person, Integer> testAttribute = new ComparableAttribute<>() {
        @Override
        public Sort<Person> asc() {
           throw new UnsupportedOperationException("It is not the focus of this test");
        }

        @Override
        public Sort<Person> desc() {
            throw new UnsupportedOperationException("It is not the focus of this test");
        }

        @Override
        public String name() {
            return "testAttribute";
        }
    };

    @Test
    void shouldCreateGreaterThanRestriction() {
        Restriction<Person> restriction = testAttribute.greaterThan(10);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<Person, Integer> basic = (BasicRestriction<Person, Integer>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.constraint()).isInstanceOf(GreaterThan.class);
            soft.assertThat(basic.constraint()).isEqualTo(Constraint.greaterThan(10));
        });
    }

    @Test
    void shouldCreateGreaterThanEqualRestriction() {
        Restriction<Person> restriction = testAttribute.greaterThanEqual(10);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<Person, Integer> basic = (BasicRestriction<Person, Integer>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.constraint()).isInstanceOf(GreaterThanOrEqual.class);
            soft.assertThat(basic.constraint()).isEqualTo(Constraint.greaterThanOrEqual(10));
        });
    }

    @Test
    void shouldCreateLessThanRestriction() {
        Restriction<Person> restriction = testAttribute.lessThan(10);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<Person, Integer> basic = (BasicRestriction<Person, Integer>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.constraint()).isInstanceOf(LessThan.class);
            soft.assertThat(basic.constraint()).isEqualTo(Constraint.lessThan(10));
        });
    }

    @Test
    void shouldCreateLessThanOrEqualRestriction() {
        Restriction<Person> restriction = testAttribute.lessThanEqual(10);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<Person, Integer> basic = (BasicRestriction<Person, Integer>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.constraint()).isInstanceOf(LessThanOrEqual.class);
            soft.assertThat(basic.constraint()).isEqualTo(Constraint.lessThanOrEqual(10));
        });
    }

    @Test
    void shouldCreateBetweenRestriction() {
        Restriction<Person> restriction = testAttribute.between(5, 15);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<Person, Integer> basic = (BasicRestriction<Person, Integer>) restriction;

            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.constraint()).isInstanceOf(Between.class);
            soft.assertThat(basic.constraint()).isEqualTo(Constraint.between(5,15));
        });
    }
}
