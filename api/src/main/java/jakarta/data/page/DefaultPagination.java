/*
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
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

import jakarta.data.Sort;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Built-in implementation of Pageable.
 */
record DefaultPagination(long page, int size, List<Sort> sorts, Mode mode, Cursor cursor) implements Pagination {

    DefaultPagination {
        if (page < 1) {
            throw new IllegalArgumentException("pageNumber: " + page);
        } else if (size < 1) {
            throw new IllegalArgumentException("maxPageSize: " + size);
        }

        if (mode != Mode.OFFSET && (cursor == null || cursor.size() == 0)) {
            throw new IllegalArgumentException("No keyset values were provided.");
        }
    }

    @Override
    public Pagination afterKeyset(Object... keyset) {
        return new DefaultPagination(page, size, sorts, Mode.CURSOR_NEXT, new KeysetCursor(keyset));
    }

    @Override
    public Pagination beforeKeyset(Object... keyset) {
        return new DefaultPagination(page, size, sorts, Mode.CURSOR_PREVIOUS, new KeysetCursor(keyset));
    }

    @Override
    public Pagination afterKeysetCursor(Cursor keysetCursor) {
        return new DefaultPagination(page, size, sorts, Mode.CURSOR_NEXT, keysetCursor);
    }

    @Override
    public Pagination beforeKeysetCursor(Cursor keysetCursor) {
        return new DefaultPagination(page, size, sorts, Mode.CURSOR_PREVIOUS, keysetCursor);
    }

    @Override
    public Pagination next() {
        if (mode == Mode.OFFSET) {
            return new DefaultPagination((page + 1), this.size, this.sorts, Mode.OFFSET, null);
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
            s.append(", ").append(sort.property());
            if (sort.ignoreCase()) {
                s.append(" IGNORE CASE");
            }
            s.append(sort.isAscending() ? " ASC" : " DESC");
        }
        return s.append("}").toString();
    }

    @Override
    public Pagination page(long pageNumber) {
        return new DefaultPagination(pageNumber, size, sorts, mode, cursor);
    }

    @Override
    public Pagination size(int maxPageSize) {
        return new DefaultPagination(page, maxPageSize, sorts, mode, cursor);
    }

    @Override
    public Pagination sortBy(Iterable<Sort> sorts) {
        List<Sort> sortList = sorts == null
                ? Collections.emptyList()
                : StreamSupport.stream(sorts.spliterator(), false).collect(Collectors.toUnmodifiableList());
        return new DefaultPagination(page, size, sortList, mode, cursor);
    }

    @Override
    public Pagination sortBy(Sort... sorts) {
        return new DefaultPagination(page, size, sorts == null ? Collections.emptyList() : List.of(sorts), mode, cursor);
    }
}