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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Built-in implementation of Pageable.
 */
final class Pagination implements Pageable {

    private final Cursor cursor;

    private final Mode mode;

    private final long page;

    private final int size;

    private final List<Sort> sorts;

    Pagination(long pageNumber, int maxPageSize, List<Sort> sorts, Mode mode, Cursor cursor) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("pageNumber: " + pageNumber);
        } else if (maxPageSize < 1) {
            throw new IllegalArgumentException("maxPageSize: " + maxPageSize);
        }

        if (mode != Mode.OFFSET && (cursor == null || cursor.size() == 0)) {
            throw new IllegalArgumentException("No keyset values were provided.");
        }

        this.page = pageNumber;
        this.size = maxPageSize;
        this.sorts = sorts;
        this.mode = mode;
        this.cursor = cursor;
    }

    @Override
    public Pageable afterKeyset(Object... keyset) {
        return new Pagination(page, size, sorts, Mode.CURSOR_NEXT, new CursorImpl(keyset));
    }

    @Override
    public Pageable beforeKeyset(Object... keyset) {
        return new Pagination(page, size, sorts, Mode.CURSOR_PREVIOUS, new CursorImpl(keyset));
    }

    @Override
    public Pageable afterKeysetCursor(Cursor keysetCursor) {
        return new Pagination(page, size, sorts, Mode.CURSOR_NEXT, keysetCursor);
    }

    @Override
    public Pageable beforeKeysetCursor(Cursor keysetCursor) {
        return new Pagination(page, size, sorts, Mode.CURSOR_PREVIOUS, keysetCursor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pagination pageable = (Pagination) o;
        return size == pageable.size
                && page == pageable.page
                && mode == pageable.mode
                && Objects.equals(cursor, pageable.cursor)
                && sorts.equals(pageable.sorts);
    }

    @Override
    public Cursor cursor() {
        return cursor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, page, sorts, mode, cursor);
    }

    @Override
    public Mode mode() {
        return mode;
    }

    @Override
    public Pageable next() {
        if (mode == Mode.OFFSET) {
            return new Pagination((page + 1), this.size, this.sorts, Mode.OFFSET, null);
        } else {
            throw new UnsupportedOperationException("Not supported for keyset pagination. Instead use afterKeyset or afterKeysetCursor " +
                    "to provide the next keyset values or obtain the nextPageable from a KeysetAwareSlice.");
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(mode == Mode.OFFSET ? 100 : 150)
                .append("Pageable{page=").append(page)
                .append(", size=").append(size);
        if (cursor != null) {
            s.append(", mode=").append(mode)
            .append(", ").append(cursor.size()).append(" keys");
        }
        for (Sort sort : sorts) {
            s.append(", ").append(sort.property()).append(sort.isAscending() ? " ASC" : " DESC");
        }
        return s.append("}").toString();
    }

    @Override
    public long page() {
        return page;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public List<Sort> sorts() {
        return sorts;
    }

    @Override
    public Pageable newPage(long pageNumber) {
        return new Pagination(pageNumber, size, sorts, mode, cursor);
    }

    @Override
    public Pageable newSize(int maxPageSize) {
        return new Pagination(page, maxPageSize, sorts, mode, cursor);
    }

    @Override
    public Pageable sortBy(Iterable<Sort> sorts) {
        List<Sort> sortList = sorts == null
                ? Collections.emptyList()
                : StreamSupport.stream(sorts.spliterator(), false).collect(Collectors.toUnmodifiableList());
        return new Pagination(page, size, sortList, mode, cursor);
    }

    @Override
    public Pageable sortBy(Sort... sorts) {
        return new Pagination(page, size, sorts == null ? Collections.emptyList() : List.of(sorts), mode, cursor);
    }

    /**
     * Built-in implementation of Cursor.
     */
    static final class CursorImpl implements Cursor {
        /**
         * Keyset values.
         */
        private final Object[] keyset;

        /**
         * Constructs a keyset cursor with the specified values.
         *
         * @param keyset keyset values.
         * @throws IllegalArgumentException if no keyset values are provided.
         */
        CursorImpl(Object... keyset) {
            this.keyset = keyset;
            if (keyset == null || keyset.length == 0)
                throw new IllegalArgumentException("No keyset values were provided.");
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o != null
                    && o.getClass() == getClass()
                    && Arrays.equals(keyset, ((CursorImpl) o).keyset);
        }

        public Object getKeysetElement(int index) {
            return keyset[index];
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(keyset);
        }

        public int size() {
            return keyset.length;
        }

        @Override
        public String toString() {
            return new StringBuilder(27).append("Cursor@").append(Integer.toHexString(hashCode()))
                            .append(" with ").append(keyset.length).append(" keys")
                            .toString();
        }
    }
}