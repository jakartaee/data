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

import jakarta.data.metamodel.constraint.Constraint;
import jakarta.data.metamodel.constraint.EqualTo;
import jakarta.data.metamodel.constraint.In;
import jakarta.data.metamodel.constraint.NotEqualTo;
import jakarta.data.metamodel.constraint.NotIn;
import jakarta.data.metamodel.constraint.NotNull;
import jakarta.data.metamodel.constraint.Null;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import jakarta.data.metamodel.restrict.BasicRestriction;
import jakarta.data.metamodel.restrict.Restriction;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttributeTest {
    // Mock entity class for tests
    static class Person {
        String firstName;
        String lastName;
        int ssn;
        int testAttribute;
    }

    private final BasicAttribute<Person, String> testAttribute = () -> "testAttribute";
    @Test
    void shouldCreateEqualToRestriction() {
        Restriction<Person> restriction = testAttribute.equalTo("testValue");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<Person, String> basic = (BasicRestriction<Person, String>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.constraint()).isInstanceOf(EqualTo.class);
            soft.assertThat(basic.constraint()).isEqualTo(Constraint.equalTo("testValue"));
        });
    }

    @Test
    void shouldCreateNotEqualToRestriction() {
        Restriction<Person> restriction = testAttribute.notEqualTo("testValue");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<Person, String> basic = (BasicRestriction<Person, String>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.constraint()).isInstanceOf(NotEqualTo.class);
            soft.assertThat(basic.constraint()).isEqualTo(NotEqualTo.value("testValue"));
        });
    }

    @Test
    void shouldCreateInRestriction() {
        Restriction<Person> restriction = testAttribute.in(Set.of("value1", "value2"));

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<Person, String> basic = (BasicRestriction<Person, String>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.constraint()).isInstanceOf(In.class);
            soft.assertThat(basic.constraint()).isEqualTo(Constraint.in("value1", "value2"));
        });
    }

    @Test
    void shouldThrowExceptionForEmptyInRestriction() {
        assertThatThrownBy(() -> testAttribute.in(Set.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("values are required");
    }

    @Test
    void shouldCreateNotInRestriction() {
        Restriction<Person> restriction = testAttribute.notIn(Set.of("value1", "value2"));

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<Person, String> basic = (BasicRestriction<Person, String>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.constraint()).isInstanceOf(NotIn.class);
            soft.assertThat(basic.constraint()).isEqualTo(NotIn.values("value1", "value2"));
        });
    }

    @Test
    void shouldThrowExceptionForEmptyNotInRestriction() {
        assertThatThrownBy(() -> testAttribute.notIn(Set.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("values are required");
    }

    @Test
    void shouldCreateIsNullRestriction() {
        Restriction<Person> restriction = testAttribute.isNull();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<Person, String> basic = (BasicRestriction<Person, String>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.constraint()).isInstanceOf(Null.class);
        });
    }

    @Test
    void shouldCreateNotNullRestriction() {
        Restriction<Person> restriction = testAttribute.notNull();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<Person, String> basic = (BasicRestriction<Person, String>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.constraint()).isInstanceOf(NotNull.class);
        });
    }
}
