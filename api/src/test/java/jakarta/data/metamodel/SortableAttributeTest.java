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
import jakarta.data.CompositeRestriction;
import jakarta.data.Operator;
import jakarta.data.Restriction;
import jakarta.data.Sort;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;


class SortableAttributeTest {

    //it ignores the implementation of the SortableAttribute interface and uses an anonymous class to test the methods
    private final SortableAttribute<String> testAttribute = new SortableAttribute<String>() {
        @Override
        public Sort<String> asc() {
           throw new UnsupportedOperationException("It is not the focus of this test");
        }

        @Override
        public Sort<String> desc() {
            throw new UnsupportedOperationException("It is not the focus of this test");
        }

        @Override
        public String name() {
            return "testAttribute";
        }
    };

    @Test
    void shouldCreateGreaterThanRestriction() {
        Restriction<String> restriction = testAttribute.greaterThan(10);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<String> basic = (BasicRestriction<String>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.value()).isEqualTo(10);
            soft.assertThat(basic.comparison()).isEqualTo(Operator.GREATER_THAN);
        });
    }

    @Test
    void shouldCreateGreaterThanEqualRestriction() {
        Restriction<String> restriction = testAttribute.greaterThanEqual(10);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<String> basic = (BasicRestriction<String>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.value()).isEqualTo(10);
            soft.assertThat(basic.comparison()).isEqualTo(Operator.GREATER_THAN_EQUAL);
        });
    }

    @Test
    void shouldCreateLessThanRestriction() {
        Restriction<String> restriction = testAttribute.lessThan(10);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<String> basic = (BasicRestriction<String>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.value()).isEqualTo(10);
            soft.assertThat(basic.comparison()).isEqualTo(Operator.LESS_THAN);
        });
    }

    @Test
    void shouldCreateLessThanOrEqualRestriction() {
        Restriction<String> restriction = testAttribute.lessThanEqual(10);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(BasicRestriction.class);
            BasicRestriction<String> basic = (BasicRestriction<String>) restriction;
            soft.assertThat(basic.attribute()).isEqualTo("testAttribute");
            soft.assertThat(basic.value()).isEqualTo(10);
            soft.assertThat(basic.comparison()).isEqualTo(Operator.LESS_THAN_EQUAL);
        });
    }

    @Test
    void shouldCreateBetweenRestriction() {
        Restriction<String> restriction = testAttribute.between(5, 15);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isInstanceOf(CompositeRestriction.class);
            CompositeRestriction<String> composite = (CompositeRestriction<String>) restriction;
            soft.assertThat(composite.type()).isEqualTo(CompositeRestriction.Type.ALL);
            soft.assertThat(composite.restrictions()).hasSize(2);

            Restriction<String> lowerBound = composite.restrictions().get(0);
            soft.assertThat(lowerBound).isInstanceOf(BasicRestriction.class);
            BasicRestriction<String> lower = (BasicRestriction<String>) lowerBound;
            soft.assertThat(lower.attribute()).isEqualTo("testAttribute");
            soft.assertThat(lower.value()).isEqualTo(5);
            soft.assertThat(lower.comparison()).isEqualTo(Operator.GREATER_THAN_EQUAL);

            Restriction<String> upperBound = composite.restrictions().get(1);
            soft.assertThat(upperBound).isInstanceOf(BasicRestriction.class);
            BasicRestriction<String> upper = (BasicRestriction<String>) upperBound;
            soft.assertThat(upper.attribute()).isEqualTo("testAttribute");
            soft.assertThat(upper.value()).isEqualTo(15);
            soft.assertThat(upper.comparison()).isEqualTo(Operator.LESS_THAN_EQUAL);
        });
    }
}
