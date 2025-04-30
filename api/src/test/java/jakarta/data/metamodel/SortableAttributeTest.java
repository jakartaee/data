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

import jakarta.data.Sort;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link SortableAttribute}, using {@link TextAttribute}
 */
class SortableAttributeTest {

    static class Document {
        String title;
    }

    // Static metamodel
    interface _Document {
        String TITLE = "title";

        TextAttribute<Document> title = TextAttribute.of(Document.class, TITLE);
    }

    @Test
    @DisplayName("should create TextAttribute as a SortableAttribute via static factory")
    void shouldCreateTextAttributeAsSortable() {
        var attribute = TextAttribute.of(Document.class, "title");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(attribute.name()).isEqualTo("title");
            soft.assertThat(attribute.declaringType()).isEqualTo(Document.class);
            soft.assertThat(attribute).isInstanceOf(SortableAttribute.class);
        });
    }

    @Test
    @DisplayName("should return ascending sort from TextAttribute")
    void shouldReturnAscendingSort() {
        var sort = _Document.title.asc();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(sort.property()).isEqualTo("title");
            soft.assertThat(sort.isAscending()).isTrue();
        });
    }

    @Test
    @DisplayName("should return descending sort from TextAttribute")
    void shouldReturnDescendingSort() {
        var sort = _Document.title.desc();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(sort.property()).isEqualTo("title");
            soft.assertThat(sort.isAscending()).isFalse();
        });
    }

    @Test
    @DisplayName("should throw NullPointerException when factory method receives nulls")
    void shouldThrowWhenNullArgumentsPassedToFactory() {
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> {
            TextAttribute.of(null, "title");
        });

        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> {
            TextAttribute.of(Document.class, null);
        });
    }

    @Test
    @DisplayName("should return ascending sort ignoring case from TextAttribute")
    void shouldReturnAscendingIgnoreCaseSort() {
        var sort = _Document.title.ascIgnoreCase();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(sort.property()).isEqualTo("title");
            soft.assertThat(sort.isAscending()).isTrue();
            soft.assertThat(sort.ignoreCase()).isTrue();
        });
    }

    @Test
    @DisplayName("should return descending sort ignoring case from TextAttribute")
    void shouldReturnDescendingIgnoreCaseSort() {
        var sort = _Document.title.descIgnoreCase();

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(sort.property()).isEqualTo("title");
            soft.assertThat(sort.isAscending()).isFalse();
            soft.assertThat(sort.ignoreCase()).isTrue();
        });
    }
}