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
package jakarta.data.metamodel.impl;

import jakarta.data.Operator;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;


class BasicRestrictionRecordTest {
    @Test
    void shouldCreateBasicEqualityRestriction() {
        BasicRestrictionRecord<String> restriction = new BasicRestrictionRecord<>("title", Operator.EQUAL, "Java Guide");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.field()).isEqualTo("title");
            soft.assertThat(restriction.operator()).isEqualTo(Operator.EQUAL);
            soft.assertThat(restriction.value()).isEqualTo("Java Guide");
        });
    }

    @Test
    void shouldCreateGreaterThanRestriction() {
        BasicRestrictionRecord<String> restriction = new BasicRestrictionRecord<>("price", Operator.GREATER_THAN, 100);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.field()).isEqualTo("price");
            soft.assertThat(restriction.operator()).isEqualTo(Operator.GREATER_THAN);
            soft.assertThat(restriction.value()).isEqualTo(100);
        });
    }

    @Test
    void shouldCreateLessThanRestriction() {
        BasicRestrictionRecord<String> restriction = new BasicRestrictionRecord<>("quantity", Operator.LESS_THAN, 50);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.field()).isEqualTo("quantity");
            soft.assertThat(restriction.operator()).isEqualTo(Operator.LESS_THAN);
            soft.assertThat(restriction.value()).isEqualTo(50);
        });
    }

}
