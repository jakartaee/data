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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class CompositeRestrictionRecordTest {

    @Test
    void shouldCreateAllCompositeRestriction() {
        Restriction<String> restriction1 = Restrict.equalTo("value1", "field1");
        Restriction<String> restriction2 = Restrict.lessThan(10, "field2");

        CompositeRestrictionRecord<String> composite = CompositeRestrictionRecord.all(restriction1, restriction2);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(composite.type()).isEqualTo(CompositeRestrictionType.ALL);
            soft.assertThat(composite.restrictions()).containsExactly(restriction1, restriction2);
        });
    }

    @Test
    void shouldCreateAnyCompositeRestriction() {
        Restriction<String> restriction1 = Restrict.greaterThan(5, "field1");
        Restriction<String> restriction2 = Restrict.in(Set.of(1, 2, 3), "field2");

        CompositeRestrictionRecord<String> composite = CompositeRestrictionRecord.any(restriction1, restriction2);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(composite.type()).isEqualTo(CompositeRestrictionType.ANY);
            soft.assertThat(composite.restrictions()).containsExactly(restriction1, restriction2);
        });
    }

    @Test
    void shouldBeIterable() {
        Restriction<String> restriction1 = Restrict.equalTo("value1", "field1");
        Restriction<String> restriction2 = Restrict.lessThan(10, "field2");

        CompositeRestrictionRecord<String> composite = CompositeRestrictionRecord.all(restriction1, restriction2);

        assertThat(composite).containsExactly(restriction1, restriction2);
    }

    @Test
    void shouldReturnImmutableRestrictionsList() {
        Restriction<String> restriction1 = Restrict.equalTo("value1", "field1");
        Restriction<String> restriction2 = Restrict.lessThan(10, "field2");

        CompositeRestrictionRecord<String> composite = CompositeRestrictionRecord.all(restriction1, restriction2);

        assertThatThrownBy(() -> composite.restrictions().add(Restrict.greaterThan(15, "field3")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldSupportEmptyRestrictions() {
        CompositeRestrictionRecord<String> composite = CompositeRestrictionRecord.all();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(composite.type()).isEqualTo(CompositeRestrictionType.ALL);
            soft.assertThat(composite.restrictions()).isEmpty();
        });
    }

    @Test
    void shouldCombineDifferentRestrictionTypes() {
        Restriction<String> restriction1 = Restrict.contains("text", "field1");
        Restriction<String> restriction2 = Restrict.startsWith("prefix", "field2");
        Restriction<String> restriction3 = Restrict.endsWith("suffix", "field3");

        CompositeRestrictionRecord<String> composite = CompositeRestrictionRecord.all(restriction1, restriction2, restriction3);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(composite.type()).isEqualTo(CompositeRestrictionType.ALL);
            soft.assertThat(composite.restrictions()).containsExactly(restriction1, restriction2, restriction3);
        });
    }
}
