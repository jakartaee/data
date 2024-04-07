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

import java.util.Optional;

/**
 * Built-in implementation of PageRequest.
 */
record Pagination(long page, int size, Mode mode, Cursor keys, boolean requestTotal) implements PageRequest {

    Pagination {
        if (page < 1) {
            throw new IllegalArgumentException("pageNumber: " + page);
        } else if (size < 1) {
            throw new IllegalArgumentException("maxPageSize: " + size);
        }

        if (mode != Mode.OFFSET && (keys == null || keys.size() == 0)) {
            throw new IllegalArgumentException("No key values were provided.");
        }
    }

    @Override
    public PageRequest withoutTotal() {
        return new Pagination(page, size, mode, keys, false);
    }

    @Override
    public PageRequest withTotal() {
        return new Pagination(page, size, mode, keys, true);
    }

    @Override
    public PageRequest afterKey(Object... key) {
        return new Pagination(page, size, Mode.CURSOR_NEXT, new PageRequestCursor(key), requestTotal);
    }

    @Override
    public PageRequest beforeKey(Object... key) {
        return new Pagination(page, size, Mode.CURSOR_PREVIOUS, new PageRequestCursor(key), requestTotal);
    }

    @Override
    public PageRequest afterCursor(Cursor cursor) {
        return new Pagination(page, size, Mode.CURSOR_NEXT, cursor, requestTotal);
    }

    @Override
    public PageRequest beforeCursor(Cursor cursor) {
        return new Pagination(page, size, Mode.CURSOR_PREVIOUS, cursor, requestTotal);
    }

    @Override
    public Optional<Cursor> cursor() {
        return Optional.ofNullable(keys);
    }

    @Override
    public PageRequest next() {
        if (mode == Mode.OFFSET) {
            return new Pagination(page + 1, this.size, Mode.OFFSET, null, requestTotal);
        } else {
            throw new UnsupportedOperationException("Not supported for cursor-based pagination. Instead use afterKey or afterCursor " +
                    "to provide a cursor or obtain the nextPageRequest from a CursoredPage.");
        }
    }

    @Override
    public PageRequest previous() {
        if (mode == Mode.OFFSET) {
            return page()<=1 ? null : new Pagination(page - 1, this.size, Mode.OFFSET, null, requestTotal);
        } else {
            throw new UnsupportedOperationException("Not supported for cursor-based pagination. Instead use beforeKey or beforeCursor " +
                    "to provide a cursor or obtain the previousPageRequest from a CursoredPage.");
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(mode == Mode.OFFSET ? 100 : 150)
                .append("PageRequest{page=").append(page)
                .append(", size=").append(size)
                .append(", mode=").append(mode);
        if (keys != null) {
            s.append(", ").append(keys.size()).append(" keys");
        }
        return s.append("}").toString();
    }

    @Override
    public PageRequest page(long pageNumber) {
        return new Pagination(pageNumber, size, Mode.OFFSET, null, requestTotal);
    }

    @Override
    public PageRequest size(int maxPageSize) {
        return new Pagination(page, maxPageSize, mode, keys, requestTotal);
    }

}