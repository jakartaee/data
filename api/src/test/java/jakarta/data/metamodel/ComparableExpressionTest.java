/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

import jakarta.data.metamodel.constraint.*;
import jakarta.data.metamodel.restrict.BasicRestriction;
import jakarta.data.metamodel.restrict.Restriction;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ComparableExpressionTest {

    static class Person {
        Integer age;
    }

    // Mock metamodel
    interface _Person {
        String AGE = "age";
        ComparableExpression<Person, Integer> age =
                new ComparableAttributeRecord<>(Person.class, AGE);
    }

    @Test
    @DisplayName("should create Restriction with between values")
    void shouldCreateBetweenValueRestriction() {
        var restriction = _Person.age.between(18, 30);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            soft.assertThat(((BasicRestriction<?, ?>) restriction).constraint()).isEqualTo(Between.bounds(18, 30));
        });
    }

    @Test
    @DisplayName("should create Restriction with greaterThan value")
    void shouldCreateGreaterThanRestriction() {
        var restriction = _Person.age.greaterThan(21);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            soft.assertThat(((BasicRestriction<?, ?>) restriction).constraint()).isEqualTo(GreaterThan.bound(21));
        });
    }

    @Test
    @DisplayName("should create Restriction with greaterThanEqual value")
    void shouldCreateGreaterThanEqualRestriction() {
        var restriction = _Person.age.greaterThanEqual(65);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            soft.assertThat(((BasicRestriction<?, ?>) restriction).constraint()).isEqualTo(GreaterThanOrEqual.min(65));
        });
    }

    @Test
    @DisplayName("should create between expression restriction with min and max expressions")
    void shouldCreateBetweenExpressionRestriction() {
        var min = _Person.age;
        var max = _Person.age;

        var restriction = _Person.age.between(min, max);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            soft.assertThat(((BasicRestriction<?, ?>) restriction).constraint())
                    .isEqualTo(Between.bounds(min, max));
        });
    }
}