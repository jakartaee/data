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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.page;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static jakarta.data.page.PageRequest.Mode.CURSOR_NEXT;
import static jakarta.data.page.PageRequest.Mode.CURSOR_PREVIOUS;
import static jakarta.data.page.PageRequest.Mode.OFFSET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("PageRequest")
class PageRequestTest {

    @Nested
    @DisplayName("When creating offset page requests")
    class WhenCreatingOffsetPageRequests {

        @Test
        @DisplayName("Should create a page request with the default size")
        void shouldCreatePageRequestWithDefaultSize() {
            // when
            PageRequest pageRequest = PageRequest.ofPage(5);

            // then
            assertSoftly(softly -> {
                softly.assertThat(pageRequest.pageNumber()).isEqualTo(5L);
                softly.assertThat(pageRequest.size()).isEqualTo(10);
                softly.assertThat(pageRequest.mode()).isEqualTo(OFFSET);
                softly.assertThat(pageRequest.requestTotal()).isTrue();
                softly.assertThat(pageRequest.cursor()).isEmpty();
            });
        }

        @Test
        @DisplayName("Should create a page request with a specified size")
        void shouldCreatePageRequestWithSpecifiedSize() {
            // when
            PageRequest pageRequest = PageRequest.ofSize(50);

            // then
            assertSoftly(softly -> {
                softly.assertThat(pageRequest.pageNumber()).isEqualTo(1L);
                softly.assertThat(pageRequest.size()).isEqualTo(50);
                softly.assertThat(pageRequest.mode()).isEqualTo(OFFSET);
                softly.assertThat(pageRequest.requestTotal()).isTrue();
            });
        }

        @Test
        @DisplayName("Should create a page request with all specified values")
        void shouldCreatePageRequestWithAllSpecifiedValues() {
            // when
            PageRequest pageRequest = PageRequest.ofPage(2, 6, false);

            // then
            assertSoftly(softly -> {
                softly.assertThat(pageRequest.pageNumber()).isEqualTo(2L);
                softly.assertThat(pageRequest.size()).isEqualTo(6);
                softly.assertThat(pageRequest.mode()).isEqualTo(OFFSET);
                softly.assertThat(pageRequest.requestTotal()).isFalse();
            });
        }

        @Test
        @DisplayName("Should reject invalid page numbers")
        void shouldRejectInvalidPageNumbers() {
            // when and then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> PageRequest.ofPage(0));

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> PageRequest.ofPage(-1));
        }

        @Test
        @DisplayName("Should reject invalid page sizes")
        void shouldRejectInvalidPageSizes() {
            // given
            PageRequest pageRequest = PageRequest.ofPage(1);

            // when and then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> pageRequest.size(0));

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> pageRequest.size(-1));

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> PageRequest.ofSize(0));

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> PageRequest.ofSize(-1));
        }
    }

    @Nested
    @DisplayName("When changing the page number")
    class WhenChangingPageNumber {

        @Test
        @DisplayName("Should replace the page number on a new instance")
        void shouldReplacePageNumberOnNewInstance() {
            // given
            PageRequest original = PageRequest.ofPage(2, 30, false);

            // when
            PageRequest changed = original.pageNumber(7);

            // then
            assertSoftly(softly -> {
                softly.assertThat(original.pageNumber()).isEqualTo(2L);
                softly.assertThat(changed.pageNumber()).isEqualTo(7L);
                softly.assertThat(changed.size()).isEqualTo(30);
                softly.assertThat(changed.mode()).isEqualTo(OFFSET);
                softly.assertThat(changed.requestTotal()).isFalse();
            });
        }

        @Test
        @DisplayName("Should reject an invalid replacement page number")
        void shouldRejectInvalidReplacementPageNumber() {
            // given
            PageRequest pageRequest = PageRequest.ofPage(1);

            // when and then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> pageRequest.pageNumber(0));
        }
    }

    @Nested
    @DisplayName("When changing the page offset")
    class WhenChangingPageOffset {

        @Test
        @DisplayName("Should convert a zero-based offset to a one-based page number")
        void shouldConvertOffsetToPageNumber() {
            // given
            PageRequest pageRequest = PageRequest.ofPage(1, 25, false);

            // when
            PageRequest changed = pageRequest.pageOffset(4);

            // then
            assertSoftly(softly -> {
                softly.assertThat(changed.pageNumber()).isEqualTo(5L);
                softly.assertThat(changed.size()).isEqualTo(25);
                softly.assertThat(changed.mode()).isEqualTo(OFFSET);
                softly.assertThat(changed.requestTotal()).isFalse();
            });
        }

        @Test
        @DisplayName("Should request the first page for an offset of zero")
        void shouldRequestFirstPageForZeroOffset() {
            // given
            PageRequest pageRequest = PageRequest.ofPage(5);

            // when
            PageRequest changed = pageRequest.pageOffset(0);

            // then
            assertThat(changed.pageNumber()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should reject a negative offset")
        void shouldRejectNegativeOffset() {
            // given
            PageRequest pageRequest = PageRequest.ofPage(1);

            // when and then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> pageRequest.pageOffset(-1));
        }

        @Test
        @DisplayName("Should reject the maximum long value as an offset")
        void shouldRejectMaximumLongValueAsOffset() {
            // given
            PageRequest pageRequest = PageRequest.ofPage(1);

            // when and then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> pageRequest.pageOffset(Long.MAX_VALUE));
        }

        @Test
        @DisplayName("Should reject an offset for forward cursor pagination")
        void shouldRejectOffsetForForwardCursorPagination() {
            // given
            PageRequest pageRequest = PageRequest.afterCursor(
                    PageRequest.Cursor.forKey(10),
                    1,
                    20,
                    false);

            // when and then
            assertThatIllegalStateException()
                    .isThrownBy(() -> pageRequest.pageOffset(1));
        }

        @Test
        @DisplayName("Should reject an offset for previous cursor pagination")
        void shouldRejectOffsetForPreviousCursorPagination() {
            // given
            PageRequest pageRequest = PageRequest.beforeCursor(
                    PageRequest.Cursor.forKey(10),
                    2,
                    20,
                    false);

            // when and then
            assertThatIllegalStateException()
                    .isThrownBy(() -> pageRequest.pageOffset(1));
        }
    }

    @Nested
    @DisplayName("When changing the page size")
    class WhenChangingPageSize {

        @Test
        @DisplayName("Should replace the size on a new instance")
        void shouldReplaceSizeOnNewInstance() {
            // given
            PageRequest original = PageRequest.ofPage(4).size(90);

            // when
            PageRequest changed = original.size(80);

            // then
            assertSoftly(softly -> {
                softly.assertThat(original.size()).isEqualTo(90);
                softly.assertThat(changed.size()).isEqualTo(80);
                softly.assertThat(original.pageNumber()).isEqualTo(4L);
                softly.assertThat(changed.pageNumber()).isEqualTo(4L);
            });
        }

        @Test
        @DisplayName("Should preserve cursor pagination information")
        void shouldPreserveCursorPaginationInformation() {
            // given
            PageRequest.Cursor cursor = PageRequest.Cursor.forKey(10);
            PageRequest original =
                    PageRequest.afterCursor(cursor, 3, 20, false);

            // when
            PageRequest changed = original.size(50);

            // then
            assertSoftly(softly -> {
                softly.assertThat(changed.pageNumber()).isEqualTo(3L);
                softly.assertThat(changed.size()).isEqualTo(50);
                softly.assertThat(changed.mode()).isEqualTo(CURSOR_NEXT);
                softly.assertThat(changed.cursor()).contains(cursor);
                softly.assertThat(changed.requestTotal()).isFalse();
            });
        }
    }

    @Nested
    @DisplayName("When configuring total retrieval")
    class WhenConfiguringTotalRetrieval {

        @Test
        @DisplayName("Should disable total retrieval without changing pagination")
        void shouldDisableTotalRetrievalWithoutChangingPagination() {
            // given
            PageRequest original = PageRequest.ofPage(2, 80, true);

            // when
            PageRequest changed = original.withoutTotal();

            // then
            assertSoftly(softly -> {
                softly.assertThat(changed.pageNumber()).isEqualTo(2L);
                softly.assertThat(changed.size()).isEqualTo(80);
                softly.assertThat(changed.mode()).isEqualTo(OFFSET);
                softly.assertThat(changed.requestTotal()).isFalse();
            });
        }

        @Test
        @DisplayName("Should enable total retrieval without changing pagination")
        void shouldEnableTotalRetrievalWithoutChangingPagination() {
            // given
            PageRequest original = PageRequest.ofPage(2, 80, false);

            // when
            PageRequest changed = original.withTotal();

            // then
            assertSoftly(softly -> {
                softly.assertThat(changed.pageNumber()).isEqualTo(2L);
                softly.assertThat(changed.size()).isEqualTo(80);
                softly.assertThat(changed.mode()).isEqualTo(OFFSET);
                softly.assertThat(changed.requestTotal()).isTrue();
            });
        }

        @Test
        @DisplayName("Should preserve total retrieval when adding subsequent configuration")
        void shouldPreserveTotalRetrievalWhenAddingConfiguration() {
            // when
            PageRequest withTotal =
                    PageRequest.ofPage(1).withTotal().size(70);

            PageRequest withoutTotal =
                    PageRequest.ofPage(2).withoutTotal().size(80);

            // then
            assertSoftly(softly -> {
                softly.assertThat(withTotal.requestTotal()).isTrue();
                softly.assertThat(withTotal.size()).isEqualTo(70);
                softly.assertThat(withoutTotal.requestTotal()).isFalse();
                softly.assertThat(withoutTotal.size()).isEqualTo(80);
            });
        }
    }

    @Nested
    @DisplayName("When creating cursor page requests")
    class WhenCreatingCursorPageRequests {

        @Test
        @DisplayName("Should create a forward cursor page request")
        void shouldCreateForwardCursorPageRequest() {
            // given
            PageRequest.Cursor cursor =
                    PageRequest.Cursor.forKey("Santana", 10);

            // when
            PageRequest pageRequest =
                    PageRequest.afterCursor(cursor, 2, 30, false);

            // then
            assertSoftly(softly -> {
                softly.assertThat(pageRequest.pageNumber()).isEqualTo(2L);
                softly.assertThat(pageRequest.size()).isEqualTo(30);
                softly.assertThat(pageRequest.mode()).isEqualTo(CURSOR_NEXT);
                softly.assertThat(pageRequest.cursor()).contains(cursor);
                softly.assertThat(pageRequest.requestTotal()).isFalse();
            });
        }

        @Test
        @DisplayName("Should create a previous cursor page request")
        void shouldCreatePreviousCursorPageRequest() {
            // given
            PageRequest.Cursor cursor =
                    PageRequest.Cursor.forKey("Santana", 10);

            // when
            PageRequest pageRequest =
                    PageRequest.beforeCursor(cursor, 3, 40, true);

            // then
            assertSoftly(softly -> {
                softly.assertThat(pageRequest.pageNumber()).isEqualTo(3L);
                softly.assertThat(pageRequest.size()).isEqualTo(40);
                softly.assertThat(pageRequest.mode())
                        .isEqualTo(CURSOR_PREVIOUS);
                softly.assertThat(pageRequest.cursor()).contains(cursor);
                softly.assertThat(pageRequest.requestTotal()).isTrue();
            });
        }

        @Test
        @DisplayName("Should change an offset request to forward cursor pagination")
        void shouldChangeOffsetRequestToForwardCursorPagination() {
            // given
            PageRequest.Cursor cursor = PageRequest.Cursor.forKey(10);
            PageRequest original = PageRequest.ofPage(4, 50, false);

            // when
            PageRequest changed = original.afterCursor(cursor);

            // then
            assertSoftly(softly -> {
                softly.assertThat(changed.pageNumber()).isEqualTo(4L);
                softly.assertThat(changed.size()).isEqualTo(50);
                softly.assertThat(changed.mode()).isEqualTo(CURSOR_NEXT);
                softly.assertThat(changed.cursor()).contains(cursor);
                softly.assertThat(changed.requestTotal()).isFalse();
            });
        }

        @Test
        @DisplayName("Should change an offset request to previous cursor pagination")
        void shouldChangeOffsetRequestToPreviousCursorPagination() {
            // given
            PageRequest.Cursor cursor = PageRequest.Cursor.forKey(10);
            PageRequest original = PageRequest.ofPage(4, 50, true);

            // when
            PageRequest changed = original.beforeCursor(cursor);

            // then
            assertSoftly(softly -> {
                softly.assertThat(changed.pageNumber()).isEqualTo(4L);
                softly.assertThat(changed.size()).isEqualTo(50);
                softly.assertThat(changed.mode())
                        .isEqualTo(CURSOR_PREVIOUS);
                softly.assertThat(changed.cursor()).contains(cursor);
                softly.assertThat(changed.requestTotal()).isTrue();
            });
        }

        @Test
        @DisplayName("Should reject a null cursor")
        void shouldRejectNullCursor() {
            // when and then
            assertThatIllegalArgumentException()
                    .isThrownBy(() ->
                            PageRequest.afterCursor(null, 1, 10, true));

            assertThatIllegalArgumentException()
                    .isThrownBy(() ->
                            PageRequest.beforeCursor(null, 1, 10, true));
        }

        @Test
        @DisplayName("Should reject an empty cursor")
        void shouldRejectEmptyCursor() {
            // given
            PageRequest.Cursor cursor = PageRequest.Cursor.forKey();

            // when and then
            assertThatIllegalArgumentException()
                    .isThrownBy(() ->
                            PageRequest.afterCursor(cursor, 1, 10, true));

            assertThatIllegalArgumentException()
                    .isThrownBy(() ->
                            PageRequest.beforeCursor(cursor, 1, 10, true));
        }
    }

    @Nested
    @DisplayName("When accessing the deprecated page operation")
    class WhenAccessingDeprecatedPageOperation {

        @Test
        @DisplayName("Should delegate to the current page number")
        @SuppressWarnings("removal")
        void shouldDelegateToCurrentPageNumber() {
            // given
            PageRequest pageRequest = PageRequest.ofPage(7);

            // when
            long page = pageRequest.page();

            // then
            assertThat(page).isEqualTo(pageRequest.pageNumber());
        }
    }

    @Nested
    @DisplayName("When comparing page requests")
    class WhenComparingPageRequests {

        @Test
        @DisplayName("Should consider equivalent page requests equal")
        void shouldConsiderEquivalentPageRequestsEqual() {
            // given
            PageRequest first = PageRequest.ofPage(2, 30, false);
            PageRequest second = PageRequest.ofPage(2, 30, false);

            // then
            assertThat(first)
                    .isEqualTo(second)
                    .hasSameHashCodeAs(second);
        }

        @Test
        @DisplayName("Should distinguish page requests with different pagination")
        void shouldDistinguishDifferentPageRequests() {
            // given
            PageRequest first = PageRequest.ofPage(2, 30, false);
            PageRequest second = PageRequest.ofPage(3, 30, false);

            // then
            assertThat(first).isNotEqualTo(second);
        }
    }

    @Nested
    @DisplayName("When displaying a page request")
    class WhenDisplayingPageRequest {

        @Test
        @DisplayName("Should display offset pagination information")
        void shouldDisplayOffsetPaginationInformation() {
            // given
            PageRequest pageRequest = PageRequest.ofSize(60);

            // when
            String result = pageRequest.toString();

            // then
            assertThat(result).isEqualTo(
                    "PageRequest{pageNumber=1, size=60, mode=OFFSET}");
        }

        @Test
        @DisplayName("Should display cursor size without exposing cursor values")
        void shouldDisplayCursorSizeWithoutExposingValues() {
            // given
            PageRequest pageRequest = PageRequest.afterCursor(
                    PageRequest.Cursor.forKey("secret", 10),
                    2,
                    20,
                    false);

            // when
            String result = pageRequest.toString();

            // then
            assertThat(result)
                    .isEqualTo(
                            "PageRequest{pageNumber=2, size=20, "
                                    + "mode=CURSOR_NEXT, cursor size=2}")
                    .doesNotContain("secret");
        }
    }
}