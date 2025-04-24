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
package jakarta.data.metamodel.impl;

import jakarta.data.metamodel.Attribute;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AttributeRecordTest {

    interface _SimpleEntity {
        String ID = "id";
        String DESCRIPTION = "description";

        Attribute<SimpleEntity> id = new AttributeRecord<>(ID);
        Attribute<SimpleEntity> description = new AttributeRecord<>(DESCRIPTION);
    }

    // A simple test entity
    static class SimpleEntity {
        String id;
        String description;
    }

    @Test
    @DisplayName("should create attribute record with name and validate interface type")
    void shouldCreateAttributeRecordWithName() {
        var attribute = new AttributeRecord<>("id");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(attribute.name()).isEqualTo("id");
            soft.assertThat(attribute).isInstanceOf(Attribute.class);
        });
    }

    @Test
    @DisplayName("should support equals and hashCode for same name")
    void shouldSupportEqualsAndHashCode() {
        var id1 = new AttributeRecord<>("id");
        var id2 = new AttributeRecord<>("id");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(id1).isEqualTo(id2);
            soft.assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        });
    }

    @Test
    @DisplayName("should not be equal if names differ")
    void shouldNotEqualDifferentNames() {
        var id = new AttributeRecord<>("id");
        var desc = new AttributeRecord<>("description");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(id).isNotEqualTo(desc);
        });
    }
}