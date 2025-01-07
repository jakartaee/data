/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

import jakarta.data.BasicRestriction;
import jakarta.data.Operator;
import jakarta.data.Restriction;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttributeTest {

    private final Attribute<String> testAttribute = () -> "testAttribute";
    @Test
    void shouldCreateEqualToRestriction() {
        Restriction<String> restriction = testAttribute.equalTo("testValue");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<String> basic = (BasicRestriction<String>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.value()).isEqualTo("testValue");
            soft.assertThat(basic.comparison()).isEqualTo(Operator.EQUAL);
        });
    }

    @Test
    void shouldCreateNotEqualToRestriction() {
        Restriction<String> restriction = testAttribute.notEqualTo("testValue");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<String> basic = (BasicRestriction<String>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.value()).isEqualTo("testValue");
            soft.assertThat(basic.comparison()).isEqualTo(Operator.NOT_EQUAL);
        });
    }

    @Test
    void shouldCreateInRestriction() {
        Restriction<String> restriction = testAttribute.in("value1", "value2");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<String> basic = (BasicRestriction<String>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.value()).isEqualTo(Set.of("value1", "value2"));
            soft.assertThat(basic.comparison()).isEqualTo(Operator.IN);
        });
    }

    @Test
    void shouldThrowExceptionForEmptyInRestriction() {
        assertThatThrownBy(() -> testAttribute.in())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("values are required");
    }

    @Test
    void shouldCreateNotInRestriction() {
        Restriction<String> restriction = testAttribute.notIn("value1", "value2");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<String> basic = (BasicRestriction<String>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.value()).isEqualTo(Set.of("value1", "value2"));
            soft.assertThat(basic.comparison()).isEqualTo(Operator.NOT_IN);
        });
    }

    @Test
    void shouldThrowExceptionForEmptyNotInRestriction() {
        assertThatThrownBy(() -> testAttribute.notIn())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("values are required");
    }

    @Test
    void shouldCreateIsNullRestriction() {
        Restriction<String> restriction = testAttribute.isNull();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<String> basic = (BasicRestriction<String>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.value()).isNull();
            soft.assertThat(basic.comparison()).isEqualTo(Operator.EQUAL);
        });
    }

    @Test
    void shouldCreateNotNullRestriction() {
        Restriction<String> restriction = testAttribute.notNull();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<String> basic = (BasicRestriction<String>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.value()).isNull();
            soft.assertThat(basic.comparison()).isEqualTo(Operator.NOT_EQUAL);
        });
    }
}
