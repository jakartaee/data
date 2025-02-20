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
import jakarta.data.metamodel.restrict.Operator;
import jakarta.data.metamodel.restrict.TextRestriction;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class TextAttributeTest {


    private final TextAttribute<String> testAttribute = new TextAttribute<String>() {
        @Override
        public Sort<String> ascIgnoreCase() {
            throw new UnsupportedOperationException("Not the focus of this test.");
        }

        @Override
        public Sort<String> descIgnoreCase() {
            throw new UnsupportedOperationException("Not the focus of this test.");
        }

        @Override
        public Sort<String> asc() {
            throw new UnsupportedOperationException("Not the focus of this test.");
        }

        @Override
        public Sort<String> desc() {
            throw new UnsupportedOperationException("Not the focus of this test.");
        }

        @Override
        public String name() {
            return "testAttribute";
        }
    };

    @Test
    void shouldCreateContainsRestriction() {
        TextRestriction<String> restriction = testAttribute.contains("testValue");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("testAttribute");
            soft.assertThat(restriction.constraint().string()).isEqualTo("%testValue%");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.LIKE);
        });
    }

    @Test
    void shouldCreateStartsWithRestriction() {
        TextRestriction<String> restriction = testAttribute.startsWith("testValue");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("testAttribute");
            soft.assertThat(restriction.constraint().string()).isEqualTo("testValue%");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.LIKE);
        });
    }

    @Test
    void shouldCreateEndsWithRestriction() {
        TextRestriction<String> restriction = testAttribute.endsWith("testValue");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("testAttribute");
            soft.assertThat(restriction.constraint().string()).isEqualTo("%testValue");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.LIKE);
        });
    }

    @Test
    void shouldCreateLikeRestriction() {
        TextRestriction<String> restriction = testAttribute.like("%test%");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("testAttribute");
            soft.assertThat(restriction.constraint().string()).isEqualTo("%test%");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.LIKE);
        });
    }

    @Test
    void shouldCreateNotContainsRestriction() {
        TextRestriction<String> restriction = testAttribute.notContains("testValue");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("testAttribute");
            soft.assertThat(restriction.constraint().string()).isEqualTo("%testValue%");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.NOT_LIKE);
        });
    }

    @Test
    void shouldCreateNotLikeRestriction() {
        TextRestriction<String> restriction = testAttribute.notLike("%test%");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("testAttribute");
            soft.assertThat(restriction.constraint().string()).isEqualTo("%test%");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.NOT_LIKE);
        });
    }

    @Test
    void shouldCreateNotStartsWithRestriction() {
        TextRestriction<String> restriction = testAttribute.notStartsWith("testValue");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("testAttribute");
            soft.assertThat(restriction.constraint().string()).isEqualTo("testValue%");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.NOT_LIKE);
        });
    }

    @Test
    void shouldCreateNotEndsWithRestriction() {
        TextRestriction<String> restriction = testAttribute.notEndsWith("testValue");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction.attribute()).isEqualTo("testAttribute");
            soft.assertThat(restriction.constraint().string()).isEqualTo("%testValue");
            soft.assertThat(restriction.comparison()).isEqualTo(Operator.NOT_LIKE);
        });
    }
}
