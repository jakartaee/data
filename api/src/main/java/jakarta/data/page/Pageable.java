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

import jakarta.data.Limit;
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.metamodel.StaticMetamodel;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * <p>A request for a page or slice.</p>
 *
 * <p><code>Pageable</code> is optionally specified as a parameter to a
 * repository method in one of the parameter positions after the
 * query parameters. For example,</p>
 *
 * <pre>
 * &#64;OrderBy("age")
 * &#64;OrderBy("ssn")
 * Person[] findByAgeBetween(int minAge, int maxAge, {@code Pageable<Person>} pageRequest);
 *
 * ...
 * for ({@code Pageable<Person>} p = Pageable.of(Person.class).size(100);
 *      p != null; p = page.length == 0 ? null : p.next()) {
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
 * <li>a <code>Pageable</code> parameter with sort criteria is supplied and separate
 *     {@link Sort} parameters are also supplied to the same method.</li>
 * <li>a <code>Pageable</code> parameter with sort criteria is supplied and an
 *     {@link Order} parameter is also supplied to the same method.</li>
 * <li>the database is incapable of ordering with the requested
 *     sort criteria.</li>
 * </ul>
 *
 * @param <T> entity class of the attributes that are used as sort criteria.
 */
public interface Pageable<T> {

    /**
     * <p>Creates a page request to use when querying on entities of the specified entity class.</p>
     *
     * <p>This method is useful for supplying the entity type when you do not have
     * typed {@link Sort} instances. For example,</p>
     *
     * <pre>
     * {@code Pageable<Car>} page1Request = Pageable.of(Car.class).page(1).size(25).sortBy(
     *                                          Sort.desc("price"),
     *                                          Sort.asc("mileage"),
     *                                          Sort.asc("vin"));
     * </pre>
     *
     * <p>If using typed {@link Sort} instances from the {@link StaticMetamodel},
     * a more concise way to create page requests is to start with the {@link Order} class,
     * as follows:</p>
     *
     * <pre>
     * {@code Pageable<Car>} page1Request = Order.by(_Car.price.desc(),
     *                                       _Car.mileage.asc(),
     *                                       _Car.vin.asc())
     *                                   .page(1)
     *                                   .size(25);
     * </pre>
     *
     * @param <T>         entity class of attributes that can be used as sort criteria.
     * @param entityClass entity class of attributes that can be used as sort criteria.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    static <T> Pageable<T> of(Class<T> entityClass) {
        return new Pagination<T>(1, 10, Collections.emptyList(), Mode.OFFSET, null);
    }

    /**
     * Creates a new page request with the given page number and with a default size of 10.
     *
     * @param <T>        entity class of the attributes that are used as sort criteria.
     * @param pageNumber The page number.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     * @throws IllegalArgumentException when the page number is negative or zero.
     */
    static <T> Pageable<T> ofPage(long pageNumber) {
        return new Pagination<T>(pageNumber, 10, Collections.emptyList(), Mode.OFFSET, null);
    }

    /**
     * Creates a new page request for requesting pages of the
     * specified size, starting with the first page number, which is 1.
     *
     * @param <T>         entity class of the attributes that are used as sort criteria.
     * @param maxPageSize The number of query results in a full page.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     * @throws IllegalArgumentException when maximum page size is negative or zero.
     */
    static <T> Pageable<T> ofSize(int maxPageSize) {
        return new Pagination<T>(1, maxPageSize, Collections.emptyList(), Mode.OFFSET, null);
    }

    /**
     * <p>Requests {@link KeysetAwareSlice keyset pagination} in the forward direction,
     * starting after the specified keyset values.</p>
     *
     * @param keyset keyset values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>Pageable</code> with forward keyset pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    Pageable<T> afterKeyset(Object... keyset);

    /**
     * <p>Requests {@link KeysetAwareSlice keyset pagination} in the reverse direction,
     * starting after the specified keyset values.</p>
     *
     * @param keyset keyset values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>Pageable</code> with reverse keyset pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    Pageable<T> beforeKeyset(Object... keyset);

    /**
     * <p>Requests {@link KeysetAwareSlice keyset pagination} in the forward direction,
     * starting after the specified keyset values.</p>
     *
     * @param keysetCursor cursor with keyset values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>Pageable</code> with forward keyset pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    Pageable<T> afterKeysetCursor(Cursor keysetCursor);

    /**
     * <p>Requests {@link KeysetAwareSlice keyset pagination} in the reverse direction,
     * starting after the specified keyset values.</p>
     *
     * @param keysetCursor cursor with keyset values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>Pageable</code> with reverse keyset pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    Pageable<T> beforeKeysetCursor(Cursor keysetCursor);

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
     * @return the keyset values; {@link Optional#empty()} if using offset pagination.
     */
    Optional<Cursor> cursor();

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
     * Return the order collection if it was specified on this page request, otherwise an empty list.
     *
     * @return the order collection; will never be {@code null}.
     */
    List<Sort<T>> sorts();

    /**
     * <p>Returns the <code>Pageable</code> requesting the next page
     * if using offset pagination.</p>
     *
     * <p>If using keyset pagination, traversal of pages must only be done
     * via the {@link KeysetAwareSlice#nextPageable()},
     * {@link KeysetAwareSlice#previousPageable()}, or
     * {@link KeysetAwareSlice#getKeysetCursor(int) keyset cursor},
     * not with this method.</p>
     *
     * @return The next pageable.
     * @throws UnsupportedOperationException if this <code>Pageable</code> has a
     *         {@link Pageable.Cursor Cursor}.
     */
    Pageable<T> next();

    /**
     * <p>Creates a new page request with the same
     * pagination information, except with the specified page number.</p>
     *
     * @param pageNumber The page number
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    Pageable<T> page(long pageNumber);

    /**
     * <p>Creates a new page request with the same
     * pagination information, except with the specified maximum page size.
     * When a page is retrieved from the database, the number of elements in the page
     * must be equal to the maximum page size unless there is an insufficient number
     * of elements to retrieve from the database from the start of the page.</p>
     *
     * @param maxPageSize the number of query results in a full page.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    Pageable<T> size(int maxPageSize);

    /**
     * <p>Creates a new page request with the same
     * pagination information, except using the specified sort criteria.
     * The order of precedence for sort criteria is that of any statically
     * specified sort criteria (from the <code>OrderBy</code> keyword,
     * {@link OrderBy} annotation or <code>ORDER BY</code> clause of a the
     * {@link Query} annotation) followed by the order of the
     * {@link Iterable} that is supplied to this method.</p>
     *
     * @param sorts sort criteria to use.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    Pageable<T> sortBy(Iterable<Sort<T>> sorts);

    /**
     * <p>Creates a new page request with the same
     * pagination information, except using the specified sort criteria.
     * The order of precedence for sort criteria is that of any statically
     * specified sort criteria (from the <code>OrderBy</code> keyword,
     * {@link OrderBy} annotation or <code>ORDER BY</code> clause of a the
     * {@link Query} annotation) followed by the order in which the
     * {@link Sort} parameters to this method are listed.</p>
     *
     * @param sort sort criteria to use.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    Pageable<T> sortBy(Sort<T> sort);

    /**
     * <p>Creates a new page request with the same
     * pagination information, except using the specified sort criteria.
     * The order of precedence for sort criteria is that of any statically
     * specified sort criteria (from the <code>OrderBy</code> keyword,
     * {@link OrderBy} annotation or <code>ORDER BY</code> clause of a the
     * {@link Query} annotation) followed by the order in which the
     * {@link Sort} parameters to this method are listed.</p>
     *
     * @param sort1 dynamic sort criteria to use first.
     * @param sort2 dynamic sort criteria to use second.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    Pageable<T> sortBy(Sort<T> sort1, Sort<T> sort2);

    /**
     * <p>Creates a new page request with the same
     * pagination information, except using the specified sort criteria.
     * The order of precedence for sort criteria is that of any statically
     * specified sort criteria (from the <code>OrderBy</code> keyword,
     * {@link OrderBy} annotation or <code>ORDER BY</code> clause of a the
     * {@link Query} annotation) followed by the order in which the
     * {@link Sort} parameters to this method are listed.</p>
     *
     * @param sort1 dynamic sort criteria to use first.
     * @param sort2 dynamic sort criteria to use second.
     * @param sort3 dynamic sort criteria to use last.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    Pageable<T> sortBy(Sort<T> sort1, Sort<T> sort2, Sort<T> sort3);

    /**
     * <p>Creates a new page request with the same
     * pagination information, except using the specified sort criteria.
     * The order of precedence for sort criteria is that of any statically
     * specified sort criteria (from the <code>OrderBy</code> keyword,
     * {@link OrderBy} annotation or <code>ORDER BY</code> clause of a the
     * {@link Query} annotation) followed by the order in which the
     * {@link Sort} parameters to this method are listed.</p>
     *
     * @param sort1 dynamic sort criteria to use first.
     * @param sort2 dynamic sort criteria to use second.
     * @param sort3 dynamic sort criteria to use third.
     * @param sort4 dynamic sort criteria to use last.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    Pageable<T> sortBy(Sort<T> sort1, Sort<T> sort2, Sort<T> sort3, Sort<T> sort4);

    /**
     * <p>Creates a new page request with the same
     * pagination information, except using the specified sort criteria.
     * The order of precedence for sort criteria is that of any statically
     * specified sort criteria (from the <code>OrderBy</code> keyword,
     * {@link OrderBy} annotation or <code>ORDER BY</code> clause of a the
     * {@link Query} annotation) followed by the order in which the
     * {@link Sort} parameters to this method are listed.</p>
     *
     * @param sort1 dynamic sort criteria to use first.
     * @param sort2 dynamic sort criteria to use second.
     * @param sort3 dynamic sort criteria to use third.
     * @param sort4 dynamic sort criteria to use fourth.
     * @param sort5 dynamic sort criteria to use last.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    Pageable<T> sortBy(Sort<T> sort1, Sort<T> sort2, Sort<T> sort3, Sort<T> sort4, Sort<T> sort5);

    /**
     * The type of pagination: offset-based or
     * keyset cursor-based, which includes a direction.
     */
    enum Mode {
        /**
         * Indicates forward keyset pagination, which follows the
         * direction of the sort criteria, using a cursor that is
         * formed from the keyset of the last entity on the current page.
         */
        CURSOR_NEXT,

        /**
         * Indicates a request for a page with keyset pagination
         * in the reverse direction of the sort criteria, using a cursor
         * that is formed from the keyset of first entity on the current page.
         * The order of results on each page follows the sort criteria
         * and is not reversed.
         */
        CURSOR_PREVIOUS,

        /**
         * Indicates a request for a page using offset pagination.
         * The starting position for pages is computed as an offset from
         * the first result based on the page number and maximum page size.
         * Offset pagination is used when a cursor is not supplied.
         */
        OFFSET
    }

    /**
     * A cursor that is formed from a keyset, relative to which a next
     * or previous page can be requested.
     */
    interface Cursor {
        /**
         * Returns whether or not the keyset values of this cursor
         * are equal to those of the supplied cursor.
         * Both instances must also have the same cursor implementation class
         * in order to be considered equal.
         *
         * @param cursor a keyset cursor against which to compare.
         * @return true or false.
         */
        @Override
        boolean equals(Object cursor);

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