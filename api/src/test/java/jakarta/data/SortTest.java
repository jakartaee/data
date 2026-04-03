/*
 * Copyright (c) 2022,2026 Contributors to the Eclipse Foundation
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

import jakarta.data.mock.entity._Book;
import jakarta.data.mock.entity.Book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class SortTest {
    public static final String NAME = "name";

    @Test
    @DisplayName("Should throw NullPointerException when one of the properties are null")
    void shouldReturnErrorWhenPropertyDirectionNull() {
        assertThatNullPointerException().isThrownBy(() ->
                Sort.of(null, null, false));
        assertThatNullPointerException().isThrownBy(() ->
                Sort.of(NAME, null, true));
        assertThatNullPointerException().isThrownBy(() ->
                Sort.of(null, Direction.ASC, false));
        assertThatNullPointerException().isThrownBy(() ->
                Sort.of(null, null, false, true));
        assertThatNullPointerException().isThrownBy(() ->
                Sort.of(NAME, null, true, false));
        assertThatNullPointerException().isThrownBy(() ->
                Sort.of(null, Direction.ASC, false, true));
    }

    @Test
    @DisplayName("Should use ascending sort when direction is ASC")
    void shouldCreateAscendingSort() {
        Sort<?> order = Sort.of(NAME, Direction.ASC, false);

        assertSoftly(softly -> {
            softly.assertThat(order).isNotNull();
            softly.assertThat(order.property()).isEqualTo(NAME);
            softly.assertThat(order.isAscending()).isTrue();
            softly.assertThat(order.isDescending()).isFalse();
            softly.assertThat(order.ignoreCase()).isFalse();
            softly.assertThat(order.orderNullsFirst()).isNull();
        });
    }

    @Test
    @DisplayName("Should descending short when direction is DESC")
    void shouldCreateDescendingSort() {
        Sort<?> order = Sort.of(NAME, Direction.DESC, true);

        assertSoftly(softly -> {
            softly.assertThat(order).isNotNull();
            softly.assertThat(order.property()).isEqualTo(NAME);
            softly.assertThat(order.isAscending()).isFalse();
            softly.assertThat(order.isDescending()).isTrue();
            softly.assertThat(order.ignoreCase()).isTrue();
            softly.assertThat(order.orderNullsFirst()).isNull();
        });
    }

    @Test
    @DisplayName("Should ascending sort when Sort.asc method is used")
    void shouldCreateAsc() {
        Sort<?> order = Sort.asc("name");

        assertSoftly(softly -> {
            softly.assertThat(order).isNotNull();
            softly.assertThat(order.property()).isEqualTo(NAME);
            softly.assertThat(order.isAscending()).isTrue();
            softly.assertThat(order.isDescending()).isFalse();
            softly.assertThat(order.ignoreCase()).isFalse();
            softly.assertThat(order.orderNullsFirst()).isNull();
        });
    }

    @Test
    @DisplayName("Should use ascending sort ignoring case when Sort.ascIgnoreCase method is used")
    void shouldCreateAscIgnoreCase() {
        Sort<?> order = Sort.ascIgnoreCase("name");

        assertSoftly(softly -> {
            softly.assertThat(order).isNotNull();
            softly.assertThat(order.property()).isEqualTo(NAME);
            softly.assertThat(order.isAscending()).isTrue();
            softly.assertThat(order.isDescending()).isFalse();
            softly.assertThat(order.ignoreCase()).isTrue();
            softly.assertThat(order.orderNullsFirst()).isNull();
        });
    }

    @Test
    @DisplayName("Should descending sort when Sort.desc method is used")
    void shouldCreateDesc() {
        Sort<?> order = Sort.desc(NAME);

        assertSoftly(softly -> {
            softly.assertThat(order).isNotNull();
            softly.assertThat(order.property()).isEqualTo(NAME);
            softly.assertThat(order.isAscending()).isFalse();
            softly.assertThat(order.isDescending()).isTrue();
            softly.assertThat(order.ignoreCase()).isFalse();
            softly.assertThat(order.orderNullsFirst()).isNull();
        });
    }

    @Test
    @DisplayName("Should use descending sort ignoring case when Sort.descIgnoreCase method is used")
    void shouldCreateDescIgnoreCase() {
        Sort<?> order = Sort.descIgnoreCase(NAME);

        assertSoftly(softly -> {
            softly.assertThat(order).isNotNull();
            softly.assertThat(order.property()).isEqualTo(NAME);
            softly.assertThat(order.isAscending()).isFalse();
            softly.assertThat(order.isDescending()).isTrue();
            softly.assertThat(order.ignoreCase()).isTrue();
            softly.assertThat(order.orderNullsFirst()).isNull();
        });
    }

    @DisplayName("""
            The nullsFirst method must create a new instance of Sort in which
            orderNullsFirst is TRUE and all other record component values
            from the original instance are preserved.
            """)
    @Test
    void testNullsFirst() {
        Sort<Book> sort = _Book.numChapters.asc().nullsFirst();

        assertSoftly(softly -> {
            softly.assertThat(sort).isNotNull();
            softly.assertThat(sort.property()).isEqualTo(_Book.NUMCHAPTERS);
            softly.assertThat(sort.isAscending()).isTrue();
            softly.assertThat(sort.isDescending()).isFalse();
            softly.assertThat(sort.ignoreCase()).isFalse();
            softly.assertThat(sort.orderNullsFirst()).isEqualTo(Boolean.TRUE);
        });
    }

    @DisplayName("""
            The nullsLast method must create a new instance of Sort in which
            orderNullsFirst is FALSE and all other record component values
            from the original instance are preserved.
            """)
    @Test
    void testNullsLast() {
        Sort<Book> sort = _Book.title.descIgnoreCase().nullsLast();

        assertSoftly(softly -> {
            softly.assertThat(sort).isNotNull();
            softly.assertThat(sort.property()).isEqualTo(_Book.TITLE);
            softly.assertThat(sort.isAscending()).isFalse();
            softly.assertThat(sort.isDescending()).isTrue();
            softly.assertThat(sort.ignoreCase()).isTrue();
            softly.assertThat(sort.orderNullsFirst()).isEqualTo(Boolean.FALSE);
        });
    }
}
