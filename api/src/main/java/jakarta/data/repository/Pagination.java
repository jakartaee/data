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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import jakarta.data.repository.KeysetPageable.Cursor;

/**
 * Built-in implementation of Pageable.
 */
class Pagination implements Pageable {

    final long page;

    final int size;

    final List<Sort> sorts;

    Pagination(long pageNumber, int maxPageSize, List<Sort> sorts) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("pageNumber: " + pageNumber);
        } else if (maxPageSize < 1) {
            throw new IllegalArgumentException("maxPageSize: " + maxPageSize);
        }

        this.page = pageNumber;
        this.size = maxPageSize;
        this.sorts = sorts;
    }

    @Override
    public KeysetPageable afterKeyset(Object... keyset) {
        return new KeysetPagination(page, size, sorts, KeysetPageable.Mode.NEXT, new KeysetPagination.CursorImpl(keyset));
    }

    @Override
    public KeysetPageable beforeKeyset(Object... keyset) {
        return new KeysetPagination(page, size, sorts, KeysetPageable.Mode.PREVIOUS, new KeysetPagination.CursorImpl(keyset));
    }

    @Override
    public KeysetPageable afterKeysetCursor(Cursor keysetCursor) {
        return new KeysetPagination(page, size, sorts, KeysetPageable.Mode.NEXT, keysetCursor);
    }

    @Override
    public KeysetPageable beforeKeysetCursor(Cursor keysetCursor) {
        return new KeysetPagination(page, size, sorts, KeysetPageable.Mode.PREVIOUS, keysetCursor);
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
        return size == pageable.size && page == pageable.page && sorts.equals(pageable.sorts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, size, sorts);
    }

    @Override
    public Pageable next() {
        return new Pagination((page + 1), this.size, this.sorts);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(100)
                .append("Pageable{page=").append(page)
                .append(", size=").append(size);
        for (Sort sort : sorts) {
            s.append(", ").append(sort.getProperty()).append(sort.isAscending() ? " ASC" : " DESC");
        }
        return s.append("}").toString();
    }

    @Override
    public long getPage() {
        return page;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public List<Sort> getSorts() {
        return sorts;
    }

    @Override
    public Pageable page(long pageNumber) {
        return new Pagination(pageNumber, size, sorts);
    }

    @Override
    public Pageable size(int maxPageSize) {
        return new Pagination(page, maxPageSize, sorts);
    }

    @Override
    public Pageable sortBy(Iterable<Sort> sorts) {
        List<Sort> sortList = sorts == null
                ? Collections.emptyList()
                : StreamSupport.stream(sorts.spliterator(), false).collect(Collectors.toUnmodifiableList());
        return new Pagination(page, size, sortList);
    }

    @Override
    public Pageable sortBy(Sort... sorts) {
        return new Pagination(page, size, sorts == null ? Collections.emptyList() : List.of(sorts));
    }
}