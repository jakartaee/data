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
 * <p>Abstract interface for pagination information.</p>
 *
 * <p><code>Pageable</code> is optionally specified as a parameter to a
 * repository method in one of the parameter positions after the
 * query parameters. For example,</p>
 *
 * <pre>
 * &#64;OrderBy("age")
 * &#64;OrderBy("ssn")
 * Person[] findByAgeBetween(int minAge, int maxAge, Pageable pagination);
 *
 * ...
 * for (Pageable p = Pageable.of(1, 100); p != null; p = page.length == 0 ? null : p.next()) {
 *   page = people.findByAgeBetween(35, 59, p);
 *   ...
 * }
 * </pre>
 *
 * <p>A repository method will raise {@link IllegalArgumentException} if</p>
 * <ul>
 * <li>multiple <code>Pageable</code> parameters are specified on the
 *     same method.</li>
 * <li><code>Pageable</code> and {@link Limit} parameters are specified on the
 *     same method.</li>
 * <li>a <code>Pageable</code> parameter is specified in combination
 *     with the <code>First</code> keyword.</li>
 * </ul>
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