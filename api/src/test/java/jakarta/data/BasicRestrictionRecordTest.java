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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class BasicRestrictionRecordTest {

    @Test
    void shouldCreateBasicRestrictionWithDefaultNegation() {
        BasicRestrictionRecord<String> restriction = new BasicRestrictionRecord<>("title", Operator.EQUAL, "Java Guide");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.field()).isEqualTo("title");
            soft.assertThat(restriction.isNegated()).isFalse();
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.EQUAL);
            soft.assertThat(restriction.value()).isEqualTo("Java Guide");
        });
    }

    @Test
    void shouldCreateBasicRestrictionWithExplicitNegation() {
        BasicRestrictionRecord<String> restriction = new BasicRestrictionRecord<>("title", true, Operator.EQUAL, "Java Guide");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.field()).isEqualTo("title");
            soft.assertThat(restriction.isNegated()).isTrue();
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.EQUAL);
            soft.assertThat(restriction.value()).isEqualTo("Java Guide");
        });
    }

    @Test
    void shouldCreateBasicRestrictionWithNullValue() {
        // Create a restriction with a null value
        BasicRestrictionRecord<String> restriction = new BasicRestrictionRecord<>("title", Operator.EQUAL, null);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.field()).isEqualTo("title");
            soft.assertThat(restriction.isNegated()).isFalse();
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.EQUAL);
            soft.assertThat(restriction.value()).isNull();
        });
    }

    @Test
    void shouldSupportNegatedRestrictionUsingDefaultConstructor() {
        BasicRestrictionRecord<String> restriction = new BasicRestrictionRecord<>("author", Operator.EQUAL, "Unknown");
        BasicRestrictionRecord<String> negatedRestriction = new BasicRestrictionRecord<>(restriction.field(), true, restriction.comparison(), restriction.value());

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(negatedRestriction.field()).isEqualTo("author");
            soft.assertThat(negatedRestriction.isNegated()).isTrue();
            soft.assertThat(negatedRestriction.comparison()).isEqualTo(Operator.EQUAL);
            soft.assertThat(negatedRestriction.value()).isEqualTo("Unknown");
        });
    }

    @Test
    void shouldThrowExceptionWhenFieldIsNull() {
        assertThatThrownBy(() -> new BasicRestrictionRecord<>(null, Operator.EQUAL, "testValue"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Field must not be null");
    }
}
