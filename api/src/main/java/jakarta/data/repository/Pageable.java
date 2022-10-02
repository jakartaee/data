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

import java.util.Objects;

/**
 * Abstract interface for pagination information.
 */
public class Pageable {

    private static final long DEFAULT_SIZE = 10;

    private final long size;

    private final long page;

    private Pageable(long size, long page) {
        this.size = size;
        this.page = page;
    }

    /**
     * Returns the size of each page
     *
     * @return the size of each page
     */
    public long getSize() {
        return size;
    }

    /**
     * Returns the page to be returned.
     *
     * @return the page to be returned.
     */
    public long getPage() {
        return page;
    }

    /**
     * Returns the Pageable requesting the next Page.
     *
     * @return The next pageable.
     */
    public Pageable next() {
        return new Pageable(this.size, (page + 1));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pageable pageable = (Pageable) o;
        return size == pageable.size && page == pageable.page;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, page);
    }

    @Override
    public String toString() {
        return "Pageable{" +
                "size=" + size +
                ", page=" + page +
                '}';
    }

    /**
     * Creates a new Pageable at the given size with a default size of 10.
     *
     * @param page The page
     * @return The pageable
     */
    public static Pageable page(long page) {
        return of(page, DEFAULT_SIZE);
    }

    /**
     * Creates a new Pageable at the given size and page
     *
     * @param page The page
     * @param size The size
     * @return The pageable
     */
    public static Pageable of(long page, long size) {
        if (page < 1) {
            throw new IllegalArgumentException("page: " + page);
        } else if (size < 1) {
            throw new IllegalArgumentException("size: " + size);
        }
        return new Pageable(size, page);
    }

}