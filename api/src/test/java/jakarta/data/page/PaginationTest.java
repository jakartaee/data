/*
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
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
package jakarta.data.page;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class PaginationTest {

    @Test
    @DisplayName("Should throw IllegalArgumentException when no key values were provided")
    void shouldThrowExceptionWhenNoKeysetValuesWereProvided() {
        assertThatIllegalArgumentException()
                .as("Mode must be different from OFFSET when cursor is null")
                .isThrownBy(() -> new Pagination(1, 10, PageRequest.Mode.CURSOR_NEXT, null, true));
    }

    @Test
    @DisplayName("should create a Pagination without total")
    void shouldReturnPaginationWithoutTotal() {
        var original = new Pagination(1, 10, PageRequest.Mode.OFFSET, null, true);
        var result = original.withoutTotal();

        assertThat(result).hasFieldOrPropertyWithValue("requestTotal", false);
    }

    @Test
    @DisplayName("should create a Pagination with total")
    void shouldReturnPaginationWithTotal() {
        var original = new Pagination(1, 10, PageRequest.Mode.OFFSET, null, false);
        var result = original.withTotal();

        assertThat(result).hasFieldOrPropertyWithValue("requestTotal", true);
    }

    @Test
    @DisplayName("should set cursor mode as CURSOR_NEXT")
    void shouldSetCursorAfter() {
        var original = new Pagination(1, 10, PageRequest.Mode.OFFSET, null, false);
        var cursor = PageRequest.Cursor.forKey("next");

        var result = original.afterCursor(cursor);

        assertThat(result).hasFieldOrPropertyWithValue("mode", PageRequest.Mode.CURSOR_NEXT);
        assertThat(result.cursor()).contains(cursor);
    }

    @Test
    @DisplayName("should set cursor mode as CURSOR_PREVIOUS")
    void shouldSetCursorBefore() {
        var original = new Pagination(1, 10, PageRequest.Mode.OFFSET, null, false);
        var cursor = PageRequest.Cursor.forKey("prev");

        var result = original.beforeCursor(cursor);

        assertThat(result).hasFieldOrPropertyWithValue("mode", PageRequest.Mode.CURSOR_PREVIOUS);
        assertThat(result.cursor()).contains(cursor);
    }

    @Test
    @DisplayName("should return cursor wrapped in Optional")
    void shouldReturnCursorOptional() {
        var cursor = PageRequest.Cursor.forKey("cursor");
        var pagination = new Pagination(1, 10, PageRequest.Mode.CURSOR_NEXT, cursor, true);

        assertThat(pagination.cursor()).contains(cursor);
    }

    @Test
    @DisplayName("should override page size")
    void shouldUpdatePageSize() {
        var pagination = new Pagination(1, 10, PageRequest.Mode.OFFSET, null, false);
        var updated = pagination.size(25);

        assertThat(updated).hasFieldOrPropertyWithValue("size", 25);
    }

    @Test
    @DisplayName("should override page number")
    void shouldUpdatePageNumber() {
        var pagination = new Pagination(1, 10, PageRequest.Mode.OFFSET, null, false);
        var updated = pagination.page(5);

        assertThat(updated).hasFieldOrPropertyWithValue("page", 5L);
    }

    @Test
    @DisplayName("toString should contain mode, page, size and cursor size if present")
    void shouldPrintToStringWithCursorInfo() {
        var cursor = PageRequest.Cursor.forKey("key1", "key2");
        var pagination = new Pagination(2, 50, PageRequest.Mode.CURSOR_NEXT, cursor, true);

        assertThat(pagination.toString())
                .contains("page=2", "size=50", "mode=CURSOR_NEXT", "cursor size=2");
    }


}
