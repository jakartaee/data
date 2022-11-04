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
 * <p>This class represents pagination information.</p>
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

    final long page;

    final long size;

    final List<Sort> sorts;

    Pageable(long page, long size, List<Sort> sorts) {
        this.page = page;
        this.size = size;
        this.sorts = sorts;
    }

    /**
     * <p>Requests {@link KeysetPageable keyset pagination} in the forward direction,
     * starting after the specified keyset values.</p>
     *
     * @param keyset keyset values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return forward keyset pagination.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    public KeysetPageable afterKeyset(Object... keyset) {
        return new KeysetPageable(this, KeysetPageable.Mode.NEXT, new KeysetPageable.Cursor(keyset));
    }

    /**
     * <p>Requests {@link KeysetPageable keyset pagination} in the reverse direction,
     * starting after the specified keyset values.</p>
     *
     * @param keyset keyset values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return reverse keyset pagination.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    public KeysetPageable beforeKeyset(Object... keyset) {
        return new KeysetPageable(this, KeysetPageable.Mode.PREVIOUS, new KeysetPageable.Cursor(keyset));
    }

    /**
     * <p>Requests {@link KeysetPageable keyset pagination} in the forward direction,
     * starting after the specified keyset values.</p>
     *
     * @param keysetCursor cursor with keyset values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return forward keyset pagination.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    public KeysetPageable afterKeysetCursor(Cursor keysetCursor) {
        return new KeysetPageable(this, KeysetPageable.Mode.NEXT, keysetCursor);
    }

    /**
     * <p>Requests {@link KeysetPageable keyset pagination} in the reverse direction,
     * starting after the specified keyset values.</p>
     *
     * @param keysetCursor cursor with keyset values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return reverse keyset pagination.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    public KeysetPageable beforeKeysetCursor(Cursor keysetCursor) {
        return new KeysetPageable(this, KeysetPageable.Mode.PREVIOUS, keysetCursor);
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
     * Returns the size of each page
     *
     * @return the size of each page
     */
    public long getSize() {
        return size;
    }

    /**
     * Return the order collection if it was specified on this <code>Pageable</code>, otherwise an empty list.
     *
     * @return the order collection
     */
    public List<Sort> getSorts() {
        return sorts;
    }

    /**
     * Returns the Pageable requesting the next Page.
     *
     * @return The next pageable.
     */
    public Pageable next() {
        return new Pageable((page + 1), this.size, this.sorts);
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
        return size == pageable.size && page == pageable.page && sorts.equals(pageable.sorts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, size, sorts);
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

    /**
     * Creates a new Pageable at the given page with a default size of 10.
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
     * @throws IllegalArgumentException when page or size are negative or zero
     */
    public static Pageable of(long page, long size) {
        return of(page, size, Collections.emptyList());
    }

    /**
     * Creates a new Pageable at the given size, page and Sort
     *
     * @param page The page
     * @param size The size
     * @param sort the sort
     * @return The pageable
     * @throws IllegalArgumentException when page or size are negative or zero
     */
    public static Pageable of(long page, long size, Sort sort) {
        Objects.requireNonNull(sort, "sort is required");
        return of(page, size, Collections.singletonList(sort));
    }

    /**
     * Creates a new Pageable at the given size, page and Sort
     *
     * @param page  The page
     * @param size  The size
     * @param sorts the sorts
     * @return The pageable
     * @throws IllegalArgumentException when page or size are negative or zero
     */
    public static Pageable of(long page, long size, Sort... sorts) {
        return of(page, size, List.of(sorts));
    }

    /**
     * Creates a new Pageable at the given size, page and Sort
     *
     * @param page  The page
     * @param size  The size
     * @param sorts the sorts
     * @return The pageable
     * @throws IllegalArgumentException when page or size are negative
     * @throws NullPointerException     when sorts is null
     */
    public static Pageable of(long page, long size, Iterable<Sort> sorts) {
        if (page < 1) {
            throw new IllegalArgumentException("page: " + page);
        } else if (size < 1) {
            throw new IllegalArgumentException("size: " + size);
        }
        Objects.requireNonNull(sorts, "sorts is required");
        return new Pageable(page, size, StreamSupport.stream(sorts.spliterator(), false)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableList()));
    }

    /**
     * Creates a new <code>Pageable</code> for requesting pages of the
     * specified size, starting with the first page number, which is 1.
     *
     * @param pageSize number of query results in a full page.
     * @return The pageable.
     */
    public static Pageable size(long pageSize) {
        return of(1, pageSize);
    }
}