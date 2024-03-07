/*
 * Copyright (c) 2022,2024 Contributors to the Eclipse Foundation
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Built-in implementation of PageRequest.
 */
record Pagination<T>(long page, int size, List<Sort<? super T>> sorts, Mode mode, Cursor type, boolean requestTotal) implements PageRequest<T> {

    Pagination {
        if (page < 1) {
            throw new IllegalArgumentException("pageNumber: " + page);
        } else if (size < 1) {
            throw new IllegalArgumentException("maxPageSize: " + size);
        }

        if (mode != Mode.OFFSET && (type == null || type.size() == 0)) {
            throw new IllegalArgumentException("No key values were provided.");
        }
    }

    @Override
    public PageRequest<T> withoutTotal() {
        return new Pagination<>(page, size, sorts, mode, type, false);
    }

    @Override
    public PageRequest<T> withTotal() {
        return new Pagination<>(page, size, sorts, mode, type, true);
    }

    @Override
    public PageRequest<T> afterKey(Object... key) {
        return new Pagination<T>(page, size, sorts, Mode.CURSOR_NEXT, new PageRequestCursor(key), requestTotal);
    }

    @Override
    public PageRequest<T> beforeKey(Object... key) {
        return new Pagination<T>(page, size, sorts, Mode.CURSOR_PREVIOUS, new PageRequestCursor(key), requestTotal);
    }

    @Override
    public PageRequest<T> afterCursor(Cursor cursor) {
        return new Pagination<T>(page, size, sorts, Mode.CURSOR_NEXT, cursor, requestTotal);
    }

    @Override
    public PageRequest<T> beforeCursor(Cursor cursor) {
        return new Pagination<T>(page, size, sorts, Mode.CURSOR_PREVIOUS, cursor, requestTotal);
    }

    @Override
    public PageRequest<T> asc(String property) {
        return new Pagination<T>(page, size, combine(sorts, Sort.asc(property)), mode, type, requestTotal);
    }

    @Override
    public PageRequest<T> ascIgnoreCase(String property) {
        return new Pagination<T>(page, size, combine(sorts, Sort.ascIgnoreCase(property)), mode, type, requestTotal);
    }

    private static final <E> List<E> combine(List<E> list, E element) {
        int size = list.size();
        if (size == 0) {
            return java.util.List.of(element);
        } else {
            Object[] array = list.toArray(new Object[size + 1]);
            array[size] = element;
            @SuppressWarnings("unchecked")
            List<E> newList = (List<E>) Collections.unmodifiableList(Arrays.asList(array));
            return newList;
        }
    }

    @Override
    public Optional<Cursor> cursor() {
        return Optional.ofNullable(type);
    }

    @Override
    public PageRequest<T> desc(String property) {
        return new Pagination<T>(page, size, combine(sorts, Sort.desc(property)), mode, type, requestTotal);
    }

    @Override
    public PageRequest<T> descIgnoreCase(String property) {
        return new Pagination<T>(page, size, combine(sorts, Sort.descIgnoreCase(property)), mode, type, requestTotal);
    }

    @Override
    public PageRequest<T> next() {
        if (mode == Mode.OFFSET) {
            return new Pagination<T>(page + 1, this.size, this.sorts, Mode.OFFSET, null, requestTotal);
        } else {
            throw new UnsupportedOperationException("Not supported for cursor-based pagination. Instead use afterKey or afterCursor " +
                    "to provide a cursor or obtain the nextPageRequest from a CursoredPage.");
        }
    }

    @Override
    public PageRequest<T> previous() {
        if (mode == Mode.OFFSET) {
            return page()<=1 ? null : new Pagination<T>(page - 1, this.size, this.sorts, Mode.OFFSET, null, requestTotal);
        } else {
            throw new UnsupportedOperationException("Not supported for cursor-based pagination. Instead use beforeKey or beforeCursor " +
                    "to provide a cursor or obtain the previousPageRequest from a CursoredPage.");
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(mode == Mode.OFFSET ? 100 : 150)
                .append("PageRequest{page=").append(page)
                .append(", size=").append(size);
        if (type != null) {
            s.append(", mode=").append(mode)
            .append(", ").append(type.size()).append(" keys");
        }
        for (Sort<? super T> sort : sorts) {
            s.append(", ").append(sort.property());
            if (sort.ignoreCase()) {
                s.append(" IGNORE CASE");
            }
            s.append(sort.isAscending() ? " ASC" : " DESC");
        }
        return s.append("}").toString();
    }

    @Override
    public PageRequest<T> page(long pageNumber) {
        return new Pagination<T>(pageNumber, size, sorts, mode, type, requestTotal);
    }

    @Override
    public PageRequest<T> size(int maxPageSize) {
        return new Pagination<T>(page, maxPageSize, sorts, mode, type, requestTotal);
    }

    @Override
    public PageRequest<T> sortBy(Iterable<Sort<? super T>> sorts) {
        List<Sort<? super T>> sortList = sorts instanceof List ? List.copyOf((List<Sort<? super T>>) sorts)
                : sorts == null ? Collections.emptyList()
                : StreamSupport.stream(sorts.spliterator(), false).collect(Collectors.toUnmodifiableList());
        return new Pagination<T>(page, size, sortList, mode, type, requestTotal);
    }

    @Override
    public PageRequest<T> sortBy(Sort<? super T> sort) {
        return new Pagination<T>(page, size, List.of(sort), mode, type, requestTotal);
    }

    @Override
    public PageRequest<T> sortBy(Sort<? super T> sort1, Sort<? super T> sort2) {
        return new Pagination<T>(page, size, List.of(sort1, sort2), mode, type, requestTotal);
    }

    @Override
    public PageRequest<T> sortBy(Sort<? super T> sort1, Sort<? super T> sort2, Sort<? super T> sort3) {
        return new Pagination<T>(page, size, List.of(sort1, sort2, sort3), mode, type, requestTotal);
    }

    @Override
    public PageRequest<T> sortBy(Sort<? super T> sort1, Sort<? super T> sort2, Sort<? super T> sort3, Sort<? super T> sort4) {
        return new Pagination<T>(page, size, List.of(sort1, sort2, sort3, sort4), mode, type, requestTotal);
    }

    @Override
    public PageRequest<T> sortBy(Sort<? super T> sort1, Sort<? super T> sort2, Sort<? super T> sort3, Sort<? super T> sort4, Sort<? super T> sort5) {
        return new Pagination<T>(page, size, List.of(sort1, sort2, sort3, sort4, sort5), mode, type, requestTotal);
    }
}