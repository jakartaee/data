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
 * Built-in implementation of Pageable.
 */
record Pagination<T>(long page, int size, List<Sort<T>> sorts, Mode mode, Cursor type) implements PageRequest<T> {

    Pagination {
        if (page < 1) {
            throw new IllegalArgumentException("pageNumber: " + page);
        } else if (size < 1) {
            throw new IllegalArgumentException("maxPageSize: " + size);
        }

        if (mode != Mode.OFFSET && (type == null || type.size() == 0)) {
            throw new IllegalArgumentException("No keyset values were provided.");
        }
    }

    @Override
    public PageRequest<T> afterKeyset(Object... keyset) {
        return new Pagination<T>(page, size, sorts, Mode.CURSOR_NEXT, new KeysetCursor(keyset));
    }

    @Override
    public PageRequest<T> beforeKeyset(Object... keyset) {
        return new Pagination<T>(page, size, sorts, Mode.CURSOR_PREVIOUS, new KeysetCursor(keyset));
    }

    @Override
    public PageRequest<T> afterKeysetCursor(Cursor keysetCursor) {
        return new Pagination<T>(page, size, sorts, Mode.CURSOR_NEXT, keysetCursor);
    }

    @Override
    public PageRequest<T> beforeKeysetCursor(Cursor keysetCursor) {
        return new Pagination<T>(page, size, sorts, Mode.CURSOR_PREVIOUS, keysetCursor);
    }

    @Override
    public PageRequest<T> asc(String property) {
        return new Pagination<T>(page, size, combine(sorts, Sort.asc(property)), mode, type);
    }

    @Override
    public PageRequest<T> ascIgnoreCase(String property) {
        return new Pagination<T>(page, size, combine(sorts, Sort.ascIgnoreCase(property)), mode, type);
    }

    private static final <E> List<E> combine(List<E> list, E element) {
        int size = list.size();
        if (size == 0) {
            return List.of(element);
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
        return new Pagination<T>(page, size, combine(sorts, Sort.desc(property)), mode, type);
    }

    @Override
    public PageRequest<T> descIgnoreCase(String property) {
        return new Pagination<T>(page, size, combine(sorts, Sort.descIgnoreCase(property)), mode, type);
    }

    @Override
    public PageRequest<T> next() {
        if (mode == Mode.OFFSET) {
            return new Pagination<T>((page + 1), this.size, this.sorts, Mode.OFFSET, null);
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
        if (type != null) {
            s.append(", mode=").append(mode)
            .append(", ").append(type.size()).append(" keys");
        }
        for (Sort<T> sort : sorts) {
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
        return new Pagination<T>(pageNumber, size, sorts, mode, type);
    }

    @Override
    public PageRequest<T> size(int maxPageSize) {
        return new Pagination<T>(page, maxPageSize, sorts, mode, type);
    }

    @Override
    public PageRequest<T> sortBy(Iterable<Sort<T>> sorts) {
        List<Sort<T>> sortList = sorts instanceof List ? List.copyOf((List<Sort<T>>) sorts)
                : sorts == null ? Collections.emptyList()
                : StreamSupport.stream(sorts.spliterator(), false).collect(Collectors.toUnmodifiableList());
        return new Pagination<T>(page, size, sortList, mode, type);
    }

    @Override
    public PageRequest<T> sortBy(Sort<T> sort) {
        return new Pagination<T>(page, size, List.of(sort), mode, type);
    }

    @Override
    public PageRequest<T> sortBy(Sort<T> sort1, Sort<T> sort2) {
        return new Pagination<T>(page, size, List.of(sort1, sort2), mode, type);
    }

    @Override
    public PageRequest<T> sortBy(Sort<T> sort1, Sort<T> sort2, Sort<T> sort3) {
        return new Pagination<T>(page, size, List.of(sort1, sort2, sort3), mode, type);
    }

    @Override
    public PageRequest<T> sortBy(Sort<T> sort1, Sort<T> sort2, Sort<T> sort3, Sort<T> sort4) {
        return new Pagination<T>(page, size, List.of(sort1, sort2, sort3, sort4), mode, type);
    }

    @Override
    public PageRequest<T> sortBy(Sort<T> sort1, Sort<T> sort2, Sort<T> sort3, Sort<T> sort4, Sort<T> sort5) {
        return new Pagination<T>(page, size, List.of(sort1, sort2, sort3, sort4, sort5), mode, type);
    }
}