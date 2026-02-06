/*
 * Copyright (c) 2025,2026 Contributors to the Eclipse Foundation
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

import jakarta.data.mock.entity.Book;
import jakarta.data.mock.entity.BookSimulator;
import jakarta.data.page.CursoredPage;
import jakarta.data.page.PageRequest;
import jakarta.data.page.PageRequest.Cursor;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import java.util.NoSuchElementException;

class CursoredPageRecordTest {

    @Test
    @DisplayName("""
            CursoredPageRecord must disallow mutation of its PageRequest and
            List fields after creation.
            """)
    void shouldBeImmutable() {

        PageRequest page2Request =
                PageRequest.ofPage(2).size(5).withoutTotal()
                           .beforeCursor(Cursor.forKey("100"));

        PageRequest page3Request =
                PageRequest.ofPage(3).size(5).withoutTotal()
                           .afterCursor(Cursor.forKey("99"));

        PageRequest page4Request =
                PageRequest.ofPage(4).size(5).withoutTotal()
                           .afterCursor(Cursor.forKey("104"));

        PageRequest originalPage2Request =
                PageRequest.ofPage(2).size(5).withoutTotal()
                           .beforeCursor(Cursor.forKey("100"));

        PageRequest originalPage3Request =
                PageRequest.ofPage(3).size(5).withoutTotal()
                           .afterCursor(Cursor.forKey("99"));

        PageRequest originalPage4Request =
                PageRequest.ofPage(4).size(5).withoutTotal()
                           .afterCursor(Cursor.forKey("104"));

        List<Book> page3Content = BookSimulator.mock(5);

        List<Book> originalPage3Content = page3Content
                .stream()
                .map(Book::clone)
                .toList();

        CursoredPage<Book> page3 = new CursoredPageRecord<>(
                page3Content,
                page3Content.stream()
                    .map(Book::getId)
                    .map(Cursor::forKey)
                    .toList(),
                33,
                page3Request,
                page4Request,
                page2Request);

        // Modify the values that were supplied to the CursoredPageRecord
        // constructor
        page2Request.page(5);
        page3Request.page(6);
        page4Request.page(7);

        page2Request.size(9);
        page3Request.size(6);
        page4Request.size(3);

        page2Request.withTotal();
        page3Request.withTotal();
        page4Request.withTotal();

        page2Request.beforeCursor(Cursor.forKey("92"));
        page3Request.afterCursor(Cursor.forKey("103"));
        page4Request.afterCursor(Cursor.forKey("112"));

        page3Content.remove(4);

        assertSoftly(softly -> {
            softly.assertThat(page3.previousPageRequest())
                .isEqualTo(originalPage2Request);

            softly.assertThat(page3.pageRequest())
                .isEqualTo(originalPage3Request);

            softly.assertThat(page3.nextPageRequest())
                .isEqualTo(originalPage4Request);

            softly.assertThat(page3.content())
                .containsSequence(originalPage3Content);
        });

        // Modify (or attempt to modify) values returned by the
        // CursoredPageRecord
        page3.previousPageRequest().page(1);
        page3.previousPageRequest().size(8);
        page3.previousPageRequest().withTotal();
        page3.previousPageRequest().beforeCursor(Cursor.forKey("50"));

        page3.pageRequest().page(4);
        page3.pageRequest().size(7);
        page3.pageRequest().withTotal();
        page3.pageRequest().afterCursor(Cursor.forKey("101"));

        page3.nextPageRequest().page(5);
        page3.nextPageRequest().size(3);
        page3.nextPageRequest().withTotal();
        page3.nextPageRequest().afterCursor(Cursor.forKey("115"));

        List<Book> currentPage3Content = page3.content();

        assertSoftly(softly -> {
            softly.assertThat(page3.previousPageRequest())
                .isEqualTo(originalPage2Request);

            softly.assertThat(page3.pageRequest())
                .isEqualTo(originalPage3Request);

            softly.assertThat(page3.nextPageRequest())
                .isEqualTo(originalPage4Request);

            softly.assertThatThrownBy(() -> currentPage3Content.remove(1))
                .isInstanceOf(UnsupportedOperationException.class);

            softly.assertThat(currentPage3Content)
                .containsSequence(originalPage3Content);
        });
    }

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