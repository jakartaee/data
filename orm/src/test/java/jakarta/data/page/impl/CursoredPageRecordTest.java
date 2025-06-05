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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.page.impl;

import jakarta.data.page.PageRequest;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

class CursoredPageRecordTest {
    @Test
    @DisplayName("should report content and number of elements correctly")
    void shouldReportContentAndSize() {
        var page = new CursoredPageRecord<>(
                List.of("A", "B"),
                List.of(PageRequest.Cursor.forKey("a"), PageRequest.Cursor.forKey("b")),
                10,
                PageRequest.ofPage(1).size(2),
                true,
                false
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(page.hasContent()).isTrue();
            soft.assertThat(page.numberOfElements()).isEqualTo(2);
            soft.assertThat(page.iterator()).toIterable().containsExactly("A", "B");
        });
    }

    @Test
    @DisplayName("should return cursor by index")
    void shouldReturnCursorByIndex() {
        var cursorA = PageRequest.Cursor.forKey("a");
        var cursorB = PageRequest.Cursor.forKey("b");

        var page = new CursoredPageRecord<>(
                List.of("A", "B"),
                List.of(cursorA, cursorB),
                10,
                PageRequest.ofPage(1).size(2),
                true,
                false
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(page.cursor(0)).isEqualTo(cursorA);
            soft.assertThat(page.cursor(1)).isEqualTo(cursorB);
        });
    }

    @Test
    @DisplayName("should indicate when next and previous pages are available")
    void shouldDetectNavigationPossibilities() {
        var page = new CursoredPageRecord<>(
                List.of("X"),
                List.of(PageRequest.Cursor.forKey("x")),
                10,
                PageRequest.ofPage(2).size(1),
                false,
                false
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(page.hasNext()).isTrue();
            soft.assertThat(page.hasPrevious()).isTrue();
        });
    }

    @Test
    @DisplayName("should throw when accessing missing next/previous page requests")
    void shouldThrowWhenPageNavigationUnavailable() {
        var page = new CursoredPageRecord<>(
                List.of("X"),
                List.of(PageRequest.Cursor.forKey("x")),
                10,
                PageRequest.ofPage(1).size(1),
                true,
                true
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThatThrownBy(page::nextPageRequest).isInstanceOf(NoSuchElementException.class);
            soft.assertThatThrownBy(page::previousPageRequest).isInstanceOf(NoSuchElementException.class);
        });
    }

    @Test
    @DisplayName("should compute totalPages correctly")
    void shouldComputeTotalPages() {
        var page = new CursoredPageRecord<>(
                List.of("A", "B"),
                List.of(PageRequest.Cursor.forKey("a"), PageRequest.Cursor.forKey("b")),
                10,
                PageRequest.ofPage(1).size(3),
                true,
                false
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(page.totalPages()).isEqualTo(4); // (10 + 3 - 1) / 3 = 4
        });
    }

    @Test
    @DisplayName("should throw if totals not available")
    void shouldThrowIfTotalUnavailable() {
        var page = new CursoredPageRecord<>(
                List.of("A"),
                List.of(PageRequest.Cursor.forKey("a")),
                -1,
                PageRequest.ofPage(1).size(1),
                true,
                true
        );

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(page.hasTotals()).isFalse();
            soft.assertThatThrownBy(page::totalElements).isInstanceOf(IllegalStateException.class);
            soft.assertThatThrownBy(page::totalPages).isInstanceOf(IllegalStateException.class);
        });
    }
}