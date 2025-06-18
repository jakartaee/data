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

import jakarta.data.constraint.Between;
import jakarta.data.constraint.GreaterThan;
import jakarta.data.constraint.GreaterThanEqual;
import jakarta.data.constraint.LessThan;
import jakarta.data.constraint.LessThanEqual;
import jakarta.data.constraint.NotBetween;
import jakarta.data.expression.ComparableExpression;
import jakarta.data.restrict.BasicRestriction;

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
                ComparableAttribute.of(Person.class, AGE, Integer.class);
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
            soft.assertThat(((BasicRestriction<?, ?>) restriction).constraint()).isEqualTo(GreaterThanEqual.min(65));
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

    @DisplayName("should create Restriction with lessThan value")
    @Test
    void shouldCreateLessThanRestriction() {
        var restriction = _Person.age.lessThan(40);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            soft.assertThat(((BasicRestriction<?, ?>) restriction).constraint())
                    .isEqualTo(LessThan.bound(40));
        });
    }

    @DisplayName("should create Restriction with lessThanEqual value")
    @Test
    void shouldCreateLessThanEqualRestriction() {
        var restriction = _Person.age.lessThanEqual(60);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            soft.assertThat(((BasicRestriction<?, ?>) restriction).constraint())
                    .isEqualTo(LessThanEqual.max(60));
        });
    }

    @DisplayName("should create Restriction with notBetween values")
    @Test
    void shouldCreateNotBetweenValueRestriction() {
        var restriction = _Person.age.between(18, 30).negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            soft.assertThat(((BasicRestriction<?, ?>) restriction).constraint())
                    .isEqualTo(NotBetween.bounds(18, 30));
        });

    }

    @DisplayName("should create Restriction with notBetween expressions")
    @Test
    void shouldCreateNotBetweenExpressionRestriction() {
        var restriction = _Person.age.between(_Person.age, _Person.age).negate();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            soft.assertThat(((BasicRestriction<?, ?>) restriction).constraint())
                    .isEqualTo(NotBetween.bounds(_Person.age, _Person.age));
        });
    }

    @DisplayName("should create Restriction with greaterThan expression")
    @Test
    void shouldCreateGreaterThanExpressionRestriction() {
        var restriction = _Person.age.greaterThan(_Person.age);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            soft.assertThat(((BasicRestriction<?, ?>) restriction).constraint())
                    .isEqualTo(GreaterThan.bound(_Person.age));
        });
    }

    @DisplayName("should create Restriction with greaterThanEqual expression")
    @Test
    void shouldCreateGreaterThanEqualExpressionRestriction() {
        var restriction = _Person.age.greaterThanEqual(_Person.age);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            soft.assertThat(((BasicRestriction<?, ?>) restriction).constraint())
                    .isEqualTo(GreaterThanEqual.min(_Person.age));
        });
    }

    @DisplayName("should create Restriction with lessThan expression")
    @Test
    void shouldCreateLessThanExpressionRestriction() {
        var restriction = _Person.age.lessThan(_Person.age);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            soft.assertThat(((BasicRestriction<?, ?>) restriction).constraint())
                    .isEqualTo(LessThan.bound(_Person.age));
        });
    }

    @DisplayName("should create Restriction with lessThanEqual expression")
    @Test
    void shouldCreateLessThanEqualExpressionRestriction() {
        var restriction = _Person.age.lessThanEqual(_Person.age);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            soft.assertThat(((BasicRestriction<?, ?>) restriction).constraint())
                    .isEqualTo(LessThanEqual.max(_Person.age));
        });
    }




}