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
package jakarta.data.repository;


import java.util.Collections;
import java.util.List;

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
 * for (Pageable p = Pageable.ofSize(100); p != null; p = page.length == 0 ? null : p.next()) {
 *   page = people.findByAgeBetween(35, 59, p);
 *   ...
 * }
 * </pre>
 *
 * <p>A repository method will fail with a
 * {@link jakarta.data.exceptions.DataException DataException}
 * or a more specific subclass if</p>
 * <ul>
 * <li>multiple <code>Pageable</code> parameters are supplied to the
 *     same method.</li>
 * <li><code>Pageable</code> and {@link Limit} parameters are supplied to the
 *     same method.</li>
 * <li>a <code>Pageable</code> parameter is supplied in combination
 *     with the <code>First</code> keyword.</li>
 * <li>a <code>Pageable</code> parameter is supplied and separate
 *     {@link Sort} parameters are also supplied to the same method.</li>
 * <li>the database is incapable of ordering with the requested
 *     sort criteria.</li>
 * </ul>
 */
public interface Pageable {

    /**
     * Creates a new <code>Pageable</code> with the given page number and with a default size of 10.
     *
     * @param pageNumber The page number.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     * @throws IllegalArgumentException when the page number is negative or zero.
     */
    static Pageable ofPage(long pageNumber) {
        return new Pagination(pageNumber, 10, Collections.emptyList(), Mode.OFFSET, null);
    }

    /**
     * Creates a new <code>Pageable</code> for requesting pages of the
     * specified size, starting with the first page number, which is 1.
     *
     * @param maxPageSize The number of query results in a full page.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     * @throws IllegalArgumentException when maximum page size is negative or zero.
     */
    static Pageable ofSize(int maxPageSize) {
        return new Pagination(1, maxPageSize, Collections.emptyList(), Mode.OFFSET, null);
    }

    /**
     * <p>Requests {@link jakarta.data.repository.jpa.KeysetAwareSlice keyset pagination} in the forward direction,
     * starting after the specified keyset values.</p>
     *
     * @param keyset keyset values, the order and number of which must match the
     *        {@link jakarta.data.repository.jpa.OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>Pageable</code> with forward keyset pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    Pageable afterKeyset(Object... keyset);

    /**
     * <p>Requests {@link jakarta.data.repository.jpa.KeysetAwareSlice keyset pagination} in the reverse direction,
     * starting after the specified keyset values.</p>
     *
     * @param keyset keyset values, the order and number of which must match the
     *        {@link jakarta.data.repository.jpa.OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>Pageable</code> with reverse keyset pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    Pageable beforeKeyset(Object... keyset);

    /**
     * <p>Requests {@link jakarta.data.repository.jpa.KeysetAwareSlice keyset pagination} in the forward direction,
     * starting after the specified keyset values.</p>
     *
     * @param keysetCursor cursor with keyset values, the order and number of which must match the
     *        {@link jakarta.data.repository.jpa.OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>Pageable</code> with forward keyset pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    Pageable afterKeysetCursor(Cursor keysetCursor);

    /**
     * <p>Requests {@link jakarta.data.repository.jpa.KeysetAwareSlice keyset pagination} in the reverse direction,
     * starting after the specified keyset values.</p>
     *
     * @param keysetCursor cursor with keyset values, the order and number of which must match the
     *        {@link jakarta.data.repository.jpa.OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>Pageable</code> with reverse keyset pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    Pageable beforeKeysetCursor(Cursor keysetCursor);

    /**
     * Compares with another instance to determine if both represent the same
     * pagination information.
     *
     * @return true if both instances are of the same class and
     *         represent the same pagination information. Otherwise false.
     */
    @Override
    boolean equals(Object o);

    /**
     * Returns the keyset values which are the starting point for
     * keyset pagination.
     *
     * @return the keyset values; <code>null</code> if using offset pagination.
     */
    Cursor cursor();

    /**
     * Returns the type of pagination.
     *
     * @return the type of pagination.
     */
    Mode mode();

    /**
     * Returns the page to be returned.
     *
     * @return the page to be returned.
     */
    long page();

    /**
     * Returns the requested size of each page
     *
     * @return the requested size of each page
     */
    int size();

    /**
     * Return the order collection if it was specified on this <code>Pageable</code>, otherwise an empty list.
     *
     * @return the order collection; will never be {@literal null}.
     */
    List<Sort> sorts();

    /**
     * <p>Returns the <code>Pageable</code> requesting the next page
     * if using offset pagination.</p>
     *
     * <p>If using keyset pagination, traversal of pages must only be done
     * via the {@link jakarta.data.repository.jpa.KeysetAwareSlice#nextPageable()},
     * {@link jakarta.data.repository.jpa.KeysetAwareSlice#previousPageable()}, or
     * {@link jakarta.data.repository.jpa.KeysetAwareSlice#getKeysetCursor(int) keyset cursor},
     * not with this method.</p>
     *
     * @return The next pageable.
     * @throws UnsupportedOperationException if this <code>Pageable</code> has a
     *         {@link Pageable.Cursor Cursor}.
     */
    Pageable next();

    /**
     * <p>Creates a new <code>Pageable</code> instance representing the same
     * pagination information, except with the specified page number.</p>
     *
     * @param pageNumber The page number
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    Pageable page(long pageNumber);

    /**
     * <p>Creates a new <code>Pageable</code> instance representing the same
     * pagination information, except with the specified maximum page size.</p>
     *
     * @param maxPageSize the number of query results in a full page.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    Pageable size(int maxPageSize);

    /**
     * <p>Creates a new <code>Pageable</code> instance representing the same
     * pagination information, except using the specified sort criteria.
     * The order of precedence for sort criteria is that of any statically
     * specified sort criteria (from the <code>OrderBy</code> keyword,
     * {@link jakarta.data.repository.jpa.OrderBy} annotation or <code>ORDER BY</code> clause of a the
     * {@link Query} annotation) followed by the order of the
     * {@link Iterable} that is supplied to this method.</p>
     *
     * @param sorts sort criteria to use.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    Pageable sortBy(Iterable<Sort> sorts);

    /**
     * <p>Creates a new <code>Pageable</code> instance representing the same
     * pagination information, except using the specified sort criteria.
     * The order of precedence for sort criteria is that of any statically
     * specified sort criteria (from the <code>OrderBy</code> keyword,
     * {@link jakarta.data.repository.jpa.OrderBy} annotation or <code>ORDER BY</code> clause of a the
     * {@link Query} annotation) followed by the order in which the
     * {@link Sort} parameters to this method are listed.</p>
     *
     * @param sorts sort criteria to use. This method can be invoked without parameters
     *        to request a <code>Pageable</code> that does not specify sort criteria.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    Pageable sortBy(Sort... sorts);

    /**
     * The type of pagination, which can be offset pagination or
     * keyset cursor pagination which includes a direction.
     */
    enum Mode {
        /**
         * Indicates forward keyset pagination, which follows the
         * direction of the {@link jakarta.data.repository.jpa.OrderBy} annotations,
         * {@link Pageable#sortBy(Sort...)} or {@link Pageable#sortBy(Iterable)}
         * parameters, repository method {@link Sort} parameters,
         * or <code>OrderBy</code> name pattern of the repository method.
         */
        CURSOR_NEXT,

        /**
         * Indicates a request for a page with keyset pagination
         * in the reverse direction of the {@link jakarta.data.repository.jpa.OrderBy} annotations,
         * {@link Pageable#sortBy(Sort...)} or {@link Pageable#sortBy(Iterable)}
         * parameters, repository method {@link Sort} parameters,
         * or <code>OrderBy</code> name pattern of the repository method.
         * The order of results on each page follows the sort criteria
         * and is not reversed.
         */
        CURSOR_PREVIOUS,

        /**
         * Indicates a reqeust for a page using offset pagination.
         * The starting position for pages is computed as an offset from
         * the first result based on the page number and maximum page size.
         * Offset pagination is used when a cursor is not supplied.
         */
        OFFSET
    }

    /**
     * Represents keyset values, which can be a starting point for
     * requesting a next or previous page.
     */
    interface Cursor {
        /**
         * Returns whether or not the keyset values of this instance
         * are equal to those of the supplied instance.
         * Cursor implementation classes must also match to be equal.
         *
         * @return true or false.
         */
        @Override
        boolean equals(Object o);

        /**
         * Returns the keyset value at the specified position.
         *
         * @param  index position (0 is first) of the keyset value to obtain.
         * @return the keyset value at the specified position.
         * @throws IndexOutOfBoundsException if the index is negative
         *         or greater than or equal to the {@link #size}.
         */
        Object getKeysetElement(int index);

        /**
         * Returns a hash code based on the keyset values.
         *
         * @return a hash code based on the keyset values.
         */
        @Override
        int hashCode();

        /**
         * Returns the number of values in the keyset.
         *
         * @return the number of values in the keyset.
         */
        int size();

        /**
         * String representation of the keyset cursor, including the number of
         * key values in the cursor but not the values themselves.
         *
         * @return String representation of the keyset cursor.
         */
        @Override
        String toString();
    }
}