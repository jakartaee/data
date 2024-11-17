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
package jakarta.data;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RestrictTest {

    @Test
    void shouldCreateBasicEqualityRestriction() {
        Restriction<String> restriction = Restrict.equalTo("Java Guide", "title");

        assertThat(restriction).isInstanceOf(BasicRestriction.class);
        BasicRestriction<String> basic = (BasicRestriction<String>) restriction;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(basic.field()).isEqualTo("title");
            soft.assertThat(basic.operator()).isEqualTo(Operator.EQUAL);
            soft.assertThat(basic.value()).isEqualTo("Java Guide");
        });
    }

    @Test
    void shouldCreateLessThanRestriction() {
        Restriction<String> restriction = Restrict.lessThan(100, "price");

        assertThat(restriction).isInstanceOf(BasicRestriction.class);
        BasicRestriction<String> basic = (BasicRestriction<String>) restriction;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(basic.field()).isEqualTo("price");
            soft.assertThat(basic.operator()).isEqualTo(Operator.LESS_THAN);
            soft.assertThat(basic.value()).isEqualTo(100);
        });
    }

    @Test
    void shouldCreateGreaterThanRestriction() {
        Restriction<String> restriction = Restrict.greaterThan(2020, "year");

        assertThat(restriction).isInstanceOf(BasicRestriction.class);
        BasicRestriction<String> basic = (BasicRestriction<String>) restriction;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(basic.field()).isEqualTo("year");
            soft.assertThat(basic.operator()).isEqualTo(Operator.GREATER_THAN);
            soft.assertThat(basic.value()).isEqualTo(2020);
        });
    }

    @Test
    void shouldCreateInRestriction() {
        Restriction<String> restriction = Restrict.in(Set.of("Java", "Spring"), "title");

        assertThat(restriction).isInstanceOf(BasicRestriction.class);
        BasicRestriction<String> basic = (BasicRestriction<String>) restriction;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(basic.field()).isEqualTo("title");
            soft.assertThat(basic.operator()).isEqualTo(Operator.IN);
            soft.assertThat(basic.value()).isInstanceOf(Set.class);
            Set<String> values = (Set<String>) basic.value();
            soft.assertThat(values).containsExactlyInAnyOrder("Java", "Spring");
        });
    }

    @Test
    void shouldCreateContainsRestriction() {
        Restriction<String> restriction = Restrict.contains("Hibernate", "title");

        assertThat(restriction).isInstanceOf(BasicRestriction.class);
        BasicRestriction<String> basic = (BasicRestriction<String>) restriction;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(basic.field()).isEqualTo("title");
            soft.assertThat(basic.operator()).isEqualTo(Operator.LIKE);
            soft.assertThat(basic.value()).isEqualTo("%Hibernate%");
        });
    }

    @Test
    void shouldCreateStartsWithRestriction() {
        Restriction<String> restriction = Restrict.startsWith("Hibernate", "title");

        assertThat(restriction).isInstanceOf(BasicRestriction.class);
        BasicRestriction<String> basic = (BasicRestriction<String>) restriction;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(basic.field()).isEqualTo("title");
            soft.assertThat(basic.operator()).isEqualTo(Operator.LIKE);
            soft.assertThat(basic.value()).isEqualTo("Hibernate%");
        });
    }

    @Test
    void shouldCreateEndsWithRestriction() {
        Restriction<String> restriction = Restrict.endsWith("Guide", "title");

        assertThat(restriction).isInstanceOf(BasicRestriction.class);
        BasicRestriction<String> basic = (BasicRestriction<String>) restriction;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(basic.field()).isEqualTo("title");
            soft.assertThat(basic.operator()).isEqualTo(Operator.LIKE);
            soft.assertThat(basic.value()).isEqualTo("%Guide");
        });
    }

    @Test
    void shouldCreateCompositeAllRestriction() {
        Restriction<String> restriction = Restrict.all(
                Restrict.equalTo("Java Guide", "title"),
                Restrict.greaterThan(2020, "publicationYear")
        );

        assertThat(restriction).isInstanceOf(CompositeRestriction.class);
        CompositeRestriction<String> composite = (CompositeRestriction<String>) restriction;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(composite.type()).isEqualTo(CompositeRestrictionType.ALL);
            soft.assertThat(composite.restrictions()).hasSize(2);
            soft.assertThat(composite.restrictions().get(0)).isInstanceOf(BasicRestriction.class);
            soft.assertThat(composite.restrictions().get(1)).isInstanceOf(BasicRestriction.class);
        });
    }

    @Test
    void shouldCreateCompositeAnyRestriction() {
        Restriction<String> restriction = Restrict.any(
                Restrict.contains("Java", "title"),
                Restrict.lessThan(500, "pages")
        );

        assertThat(restriction).isInstanceOf(CompositeRestriction.class);
        CompositeRestriction<String> composite = (CompositeRestriction<String>) restriction;

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(composite.type()).isEqualTo(CompositeRestrictionType.ANY);
            soft.assertThat(composite.restrictions()).hasSize(2);
            soft.assertThat(composite.restrictions().get(0)).isInstanceOf(BasicRestriction.class);
            soft.assertThat(composite.restrictions().get(1)).isInstanceOf(BasicRestriction.class);
        });
    }
}
