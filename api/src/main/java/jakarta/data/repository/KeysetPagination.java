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
 * Built-in implementation of KeysetPageable.
 */
final class KeysetPagination extends Pagination implements KeysetPageable {

    private final Cursor cursor;
    private final Mode mode;

    KeysetPagination(long pageNumber, int maxPageSize, List<Sort> sorts, Mode mode, Cursor cursor) {
        super(pageNumber, maxPageSize, sorts);

        if (cursor == null || cursor.size() == 0)
            throw new IllegalArgumentException("No keyset values were provided.");

        this.cursor = cursor;
        this.mode = mode;
    }

    @Override
    public boolean equals(Object o) {
        KeysetPagination p;
        return this == o || o != null
                && o.getClass() == getClass()
                && (p = (KeysetPagination) o).size == size
                && p.page == page
                && p.mode == mode
                && p.cursor.equals(cursor)
                && p.sorts.equals(sorts);
    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, page, sorts, mode, cursor);
    }

    @Override
    public KeysetPageable next() {
        throw new UnsupportedOperationException("Not supported for keyset pagination. Instead use afterKeyset or afterKeysetCursor " +
                                                "to provide the next keyset values or obtain the nextPageable from a KeysetAwareSlice.");
    }

    @Override
    public KeysetPageable page(long pageNumber) {
        return new KeysetPagination(pageNumber, size, sorts, mode, cursor);
    }

    @Override
    public KeysetPageable size(int maxPageSize) {
        return new KeysetPagination(page, maxPageSize, sorts, mode, cursor);
    }

    @Override
    public KeysetPageable sortBy(Iterable<Sort> sorts) {
        List<Sort> sortList = sorts == null
                ? Collections.emptyList()
                : StreamSupport.stream(sorts.spliterator(), false).collect(Collectors.toUnmodifiableList());
        return new KeysetPagination(page, size, sortList, mode, cursor);
    }

    @Override
    public KeysetPageable sortBy(Sort... sorts) {
        List<Sort> sortList = sorts == null ? Collections.emptyList() : List.of(sorts);
        return new KeysetPagination(page, size, sortList, mode, cursor);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(150)
                .append("KeysetPageable{page=").append(page)
                .append(", size=").append(size)
                .append(", mode=").append(mode)
                .append(", ").append(cursor.size()).append(" keys");
        for (Sort sort : sorts) {
            s.append(", ").append(sort.getProperty()).append(sort.isAscending() ? " ASC" : " DESC");
        }
        return s.append("}").toString();
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