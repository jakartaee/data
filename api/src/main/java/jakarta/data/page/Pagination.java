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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.page;

import java.util.Optional;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.data.messages.Messages;

/**
 * Built-in implementation of PageRequest.
 */
record Pagination(long pageNumber, int size, @Nonnull Mode mode, @Nullable Cursor type,
                  boolean requestTotal) implements PageRequest {

    Pagination {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("pageNumber: " + pageNumber);
        } else if (size < 1) {
            throw new IllegalArgumentException("maxPageSize: " + size);
        }

        if (mode != Mode.OFFSET && (type == null || type.size() == 0)) {
            throw new IllegalArgumentException(
                    Messages.get("006.zero.size.key"));
        }
    }

    @Override
    @Nonnull
    public PageRequest withoutTotal() {
        return new Pagination(pageNumber, size, mode, type, false);
    }

    @Override
    @Nonnull
    public PageRequest withTotal() {
        return new Pagination(pageNumber, size, mode, type, true);
    }

    @Override
    @Nonnull
    public PageRequest afterCursor(@Nonnull Cursor cursor) {
        return new Pagination(pageNumber,
                              size,
                              Mode.CURSOR_NEXT,
                              cursor,
                              requestTotal);
    }

    @Override
    @Nonnull
    public PageRequest beforeCursor(@Nonnull Cursor cursor) {
        return new Pagination(pageNumber,
                              size,
                              Mode.CURSOR_PREVIOUS,
                              cursor,
                              requestTotal);
    }

    @Override
    @Nonnull
    public Optional<Cursor> cursor() {
        return Optional.ofNullable(type);
    }

    @Override
    @Nonnull
    public String toString() {
        var s = new StringBuilder(mode == Mode.OFFSET ? 100 : 150)
                .append("PageRequest{pageNumber=").append(pageNumber)
                .append(", size=").append(size)
                .append(", mode=").append(mode);
        if (type != null) {
            s.append(", cursor size=").append(type.size());
        }
        return s.append('}').toString();
    }

    @Override
    @Nonnull
    public PageRequest size(int maxPageSize) {
        return new Pagination(pageNumber, maxPageSize, mode, type, requestTotal);
    }

    @Override
    @Nonnull
    public PageRequest pageNumber(long pageNumber) {
        return new Pagination(pageNumber, size, mode, type, requestTotal);
    }

}
