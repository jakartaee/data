/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
package jakarta.data.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class SortTest {
    public static final String NAME = "name";

    @Test
    @DisplayName("Should throw NullPointerException when one of the properties are null")
    void shouldReturnErrorWhenPropertyDirectionNull() {
        assertThatNullPointerException().isThrownBy(() -> Sort.of(null, null));
        assertThatNullPointerException().isThrownBy(() -> Sort.of(NAME, null));
        assertThatNullPointerException().isThrownBy(() -> Sort.of(null, Direction.ASC));
    }

    @Test
    @DisplayName("Should ascending short when direction is ASC")
    void shouldCreateAscendingSort() {
        Sort order = Sort.of(NAME, Direction.ASC);

        assertSoftly(softly -> {
            softly.assertThat(order).isNotNull();
            softly.assertThat(order.property()).isEqualTo(NAME);
            softly.assertThat(order.isAscending()).isTrue();
            softly.assertThat(order.isDescending()).isFalse();
        });
    }

    @Test
    @DisplayName("Should descending short when direction is DESC")
    void shouldCreateDescendingSort() {
        Sort order = Sort.of(NAME, Direction.DESC);

        assertSoftly(softly -> {
            softly.assertThat(order).isNotNull();
            softly.assertThat(order.property()).isEqualTo(NAME);
            softly.assertThat(order.isAscending()).isFalse();
            softly.assertThat(order.isDescending()).isTrue();
        });
    }

    @Test
    @DisplayName("Should ascending sort when Sort.asc method is used")
    void shouldCreateAsc() {
        Sort order = Sort.asc("name");

        assertSoftly(softly -> {
            softly.assertThat(order).isNotNull();
            softly.assertThat(order.property()).isEqualTo(NAME);
            softly.assertThat(order.isAscending()).isTrue();
            softly.assertThat(order.isDescending()).isFalse();
        });
    }

    @Test
    @DisplayName("Should descending sort when Sort.desc method is used")
    void shouldCreateDesc() {
        Sort order = Sort.desc(NAME);

        assertSoftly(softly -> {
            softly.assertThat(order).isNotNull();
            softly.assertThat(order.property()).isEqualTo(NAME);
            softly.assertThat(order.isAscending()).isFalse();
            softly.assertThat(order.isDescending()).isTrue();
        });
    }
}
