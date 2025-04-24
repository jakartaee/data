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

import jakarta.data.metamodel.TextAttribute;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TextAttributeRecordTest {


    // A mock static metamodel for tests
    interface _SimpleEntity {
        String ID = "id";
        String NAME = "name";

        TextAttribute<SimpleEntity> id = new TextAttributeRecord<>(ID);
        TextAttribute<SimpleEntity> name = new TextAttributeRecord<>(NAME);
    }

    // A simple test entity
    static class SimpleEntity {
        String id;
        String name;
    }

    @Test
    @DisplayName("should create text attribute record and validate interface type")
    void shouldCreateTextAttributeRecord() {
        var attribute = new TextAttributeRecord<SimpleEntity>("name");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(attribute.name()).isEqualTo("name");
            soft.assertThat(attribute).isInstanceOf(TextAttribute.class);
        });
    }

    @Test
    @DisplayName("should support equals and hashCode for same name")
    void shouldSupportEqualsAndHashCode() {
        var name1 = new TextAttributeRecord<SimpleEntity>("name");
        var name2 = new TextAttributeRecord<SimpleEntity>("name");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(name1).isEqualTo(name2);
            soft.assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        });
    }

    @Test
    @DisplayName("should not be equal if names differ")
    void shouldNotEqualDifferentNames() {
        var idAttribute = new TextAttributeRecord<SimpleEntity>("id");
        var nameAttribute = new TextAttributeRecord<SimpleEntity>("name");

        SoftAssertions.assertSoftly(soft -> soft.assertThat(idAttribute).isNotEqualTo(nameAttribute));
    }
}