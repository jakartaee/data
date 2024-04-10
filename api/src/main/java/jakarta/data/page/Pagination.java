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
record Pagination(long page, int size, Mode mode, Cursor type, boolean requestTotal) implements PageRequest {

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
    public PageRequest withoutTotal() {
        return new Pagination(page, size, mode, type, false);
    }

    @Override
    public PageRequest withTotal() {
        return new Pagination(page, size, mode, type, true);
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
        return Optional.ofNullable(type);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(mode == Mode.OFFSET ? 100 : 150)
                .append("PageRequest{page=").append(page)
                .append(", size=").append(size)
                .append(", mode=").append(mode);
        if (type != null) {
            s.append(", cursor size=").append(type.size());
        }
        return s.append("}").toString();
    }

    @Override
    public PageRequest size(int maxPageSize) {
        return new Pagination(page, maxPageSize, mode, type, requestTotal);
    }

}