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
 * <p>A request for a single well-specified page of query results.</p>
 *
 * <p><code>PageRequest</code> is optionally specified as a parameter to
 * a repository method in one of the parameter positions after the query
 * parameters. For example,</p>
 *
 * <pre>
 * &#64;OrderBy("age")
 * &#64;OrderBy("ssn")
 * Person[] findByAgeBetween(int minAge, int maxAge, {@code PageRequest<Person>} pageRequest);
 *
 * ...
 * for ({@code PageRequest<Person>} p = PageRequest.of(Person.class).size(100);
 *      p != null; p = page.length == 0 ? null : p.next()) {
 *   page = people.findByAgeBetween(35, 59, p);
 *   ...
 * }
 * </pre>
 *
 * <p>A repository method will fail with a
 * {@link jakarta.data.exceptions.DataException DataException} or a more
 * specific subclass if:</p>
 * <ul>
 * <li>a repository method has more than one parameter of type
 *     {@code PageRequest}, or has a parameter of type
 *     {@code PageRequest} and a parameter of type {@link Limit},</li>
 * <li>a repository method has a parameter of type {@code PageRequest} in
 *     combination with the keyword {@code First},</li>
 * <li>a {@code PageRequest} argument with sort criteria is supplied to a
 *     repository method, and separate {@link Sort} arguments are also
 *     supplied,</li>
 * <li>a {@code PageRequest} argument with sort criteria is supplied to a
 *     repository method, and an {@link Order} argument is also supplied,
 *     or</li>
 * <li>the database is incapable of ordering the query results using the
 *     given sort criteria.</li>
 * </ul>
 *
 * @param <T> entity class of the attributes that are used as sort criteria.
 */
public interface PageRequest<T> {

    /**
     * <p>Creates a page request to use when querying on entities of the
     * specified entity class.</p>
     *
     * <p>This method is useful for supplying the entity type when you
     * do not have typed {@link Sort} instances. For example,</p>
     *
     * <pre>
     * {@code PageRequest<Car>} page1Request = PageRequest.of(Car.class).page(1).size(25).sortBy(
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
     * {@code PageRequest<Car>} page1Request = Order.by(_Car.price.desc(),
     *                                       _Car.mileage.asc(),
     *                                       _Car.vin.asc())
     *                                   .page(1)
     *                                   .size(25);
     * </pre>
     *
     * @param <T>         entity class of attributes that can be used as sort criteria.
     * @param entityClass entity class of attributes that can be used as sort criteria.
     * @return a new instance of <code>PageRequest</code>. This method never returns <code>null</code>.
     */
    static <T> PageRequest<T> of(Class<T> entityClass) {
        return new Pagination<T>(1, 10, Collections.emptyList(), Mode.OFFSET, null, true);
    }

    /**
     * Creates a new page request with the given page number and with a default size of 10.
     *
     * @param <T>        entity class of the attributes that are used as sort criteria.
     * @param pageNumber The page number.
     * @return a new instance of <code>PageRequest</code>. This method never returns <code>null</code>.
     * @throws IllegalArgumentException when the page number is negative or zero.
     */
    static <T> PageRequest<T> ofPage(long pageNumber) {
        return new Pagination<T>(pageNumber, 10, Collections.emptyList(), Mode.OFFSET, null, true);
    }

    /**
     * Creates a new page request for requesting pages of the specified size,
     * starting with the first page number, which is 1.
     *
     * @param <T>         entity class of the attributes that are used as sort criteria.
     * @param maxPageSize The number of query results in a full page.
     * @return a new instance of <code>PageRequest</code>. This method never returns <code>null</code>.
     * @throws IllegalArgumentException when maximum page size is negative or zero.
     */
    static <T> PageRequest<T> ofSize(int maxPageSize) {
        return new Pagination<T>(1, maxPageSize, Collections.emptyList(), Mode.OFFSET, null, true);
    }

    /**
     * <p>Requests {@link CursoredPage cursor-based pagination} in the forward direction,
     * starting after the specified key.</p>
     *
     * @param key values that combined together form the key,
     *            the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>PageRequest</code> with forward cursor-based pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no values are provided for the key.
     */
    PageRequest<T> afterKey(Object... key);

    /**
     * <p>Requests {@link CursoredPage cursor-based pagination} in the previous page
     * direction relative to the specified key.</p>
     *
     * @param key values that combined together form the key,
     *            the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>PageRequest</code> with cursor-based pagination
     *         in the previous page direction.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no key values are provided.
     */
    PageRequest<T> beforeKey(Object... key);

    /**
     * <p>Requests {@link CursoredPage cursor-based pagination} in the forward direction,
     * starting after the specified key.</p>
     *
     * @param cursor cursor with key values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>PageRequest</code> with forward cursor-based pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no key values are provided.
     */
    PageRequest<T> afterCursor(Cursor cursor);

    /**
     * <p>Requests {@link CursoredPage cursor-based pagination} in the previous page
     * direction relative to the specified key values.</p>
     *
     * @param cursor cursor with key values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>PageRequest</code> with cursor-based pagination
     *         in the previous page direction. This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no key values are provided.
     */
    PageRequest<T> beforeCursor(Cursor cursor);

    /**
     * <p>Creates a new page request with the same pagination information,
     * appending the specified {@link Sort#asc(String) ascending sort}
     * with lower priority than all other sort criteria (if any) that have already
     * been specified.</p>
     *
     * @param property name of the entity attribute upon which to sort.
     * @return a new instance of <code>PageRequest</code> with the ascending sort
     *         as its lowest priority sort criteria.
     * @throws NullPointerException when the property is null
     */
    PageRequest<T> asc(String property);

    /**
     * <p>Creates a new page request with the same pagination information,
     * appending the specified {@link Sort#ascIgnoreCase(String) case-insensitive ascending sort}
     * with lower priority than all other sort criteria (if any) that have already
     * been specified. The case-insensitive sort means that the respective value
     * in the database is compared independent of case.</p>
     *
     * @param property name of the entity attribute upon which to sort.
     * @return a new instance of <code>PageRequest</code> with the case-insensitive ascending sort
     *         as its lowest priority sort criteria.
     * @throws NullPointerException when the property is null
     */
    PageRequest<T> ascIgnoreCase(String property);

    /**
     * <p>Creates a new page request with the same pagination information,
     * appending the specified {@link Sort#desc(String) descending sort}
     * with lower priority than all other sort criteria (if any) that have already
     * been specified.</p>
     *
     * @param property name of the entity attribute upon which to sort.
     * @return a new instance of <code>PageRequest</code> with the descending sort
     *         as its lowest priority sort criteria.
     * @throws NullPointerException when the property is null
     */
    PageRequest<T> desc(String property);

    /**
     * <p>Creates a new page request with the same pagination information,
     * appending the specified {@link Sort#descIgnoreCase(String) case-insensitive descending sort}
     * with lower priority than all other sort criteria (if any) that have already
     * been specified. The case-insensitive sort means that the respective value
     * in the database is compared independent of case.</p>
     *
     * @param property name of the entity attribute upon which to sort.
     * @return a new instance of <code>PageRequest</code> with the case-insensitive descending sort
     *         as its lowest priority sort criteria.
     * @throws NullPointerException when the property is null
     */
    PageRequest<T> descIgnoreCase(String property);

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
     * Returns the key values which are the starting point for
     * cursor-based pagination.
     *
     * @return the cursor; {@link Optional#empty()} if using offset pagination.
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
     * Indicates that a query method which returns a {@link Page}
     * should retrieve the {@linkplain Page#totalElements() total
     * number elements} available across all pages. This behavior
     * is enabled by default. To obtain a page request with total
     * retrieval disabled, call {@link #withoutTotal()}.
     * @return {@code true} if the total number of elements should
     *         be retrieved from the database.
     */
    boolean requestTotal();

    /**
     * Return the order collection if it was specified on this page request,
     * otherwise an empty list.
     *
     * @return the order collection; will never be {@code null}.
     */
    List<Sort<? super T>> sorts();

    /**
     * <p>Returns the <code>PageRequest</code> requesting the next page if
     * using offset pagination.</p>
     *
     * <p>If using cursor-based pagination, traversal of pages must only be done
     * via the {@link CursoredPage#nextPageRequest()},
     * {@link CursoredPage#previousPageRequest()}, or
     * {@linkplain CursoredPage#getCursor(int) cursor},
     * not with this method.</p>
     *
     * @return The next PageRequest.
     * @throws UnsupportedOperationException if this {@code PageRequest}
     *         has a {@link PageRequest.Cursor Cursor}.
     */
    PageRequest<T> next();

    /**
     * <p>Returns the <code>PageRequest</code> requesting the previous page
     * if using offset pagination, or null if this is the first page, that
     * is, when {@link #page()} returns {@code 1}.</p>
     *
     * <p>If using cursor-based pagination, traversal of pages must only be done
     * via the {@link CursoredPage#nextPageRequest()},
     * {@link CursoredPage#previousPageRequest()}, or
     * {@linkplain CursoredPage#getCursor(int) cursor},
     * not with this method.</p>
     *
     * @return The previous PageRequest, or null if this is the first page.
     * @throws UnsupportedOperationException if this {@code PageRequest}
     *         has a {@link PageRequest.Cursor Cursor}.
     */
    PageRequest<T> previous();

    /**
     * <p>Creates a new page request with the same pagination information,
     * but with the specified page number.</p>
     *
     * @param pageNumber The page number
     * @return a new instance of <code>PageRequest</code>.
     *         This method never returns <code>null</code>.
     */
    PageRequest<T> page(long pageNumber);

    /**
     * <p>Creates a new page request with the same pagination information,
     * but with the specified maximum page size. When a page is retrieved
     * from the database, the number of elements in the page must be equal
     * to the maximum page size unless there is an insufficient number of
     * elements to retrieve from the database from the start of the page.</p>
     *
     * @param maxPageSize the number of query results in a full page.
     * @return a new instance of <code>PageRequest</code>.
     *         This method never returns <code>null</code>.
     */
    PageRequest<T> size(int maxPageSize);

    /**
     * <p>Creates a new page request with the same pagination information,
     * but using the specified sort criteria. The order of precedence for
     * sort criteria is that of any statically specified sort criteria
     * (from the <code>OrderBy</code> keyword, {@link OrderBy} annotation,
     * or <code>ORDER BY</code> clause of a the {@link Query} annotation),
     * followed by the order of the {@link Iterable} that is supplied to
     * this method.</p>
     *
     * @param sorts sort criteria to use.
     * @return a new instance of <code>PageRequest</code>.
     *         This method never returns <code>null</code>.
     */
    PageRequest<T> sortBy(Iterable<Sort<? super T>> sorts);

    /**
     * <p>Creates a new page request with the same pagination information,
     * but using the specified sort criteria. The order of precedence for
     * sort criteria is that of any statically specified sort criteria
     * (from the <code>OrderBy</code> keyword, {@link OrderBy} annotation,
     * or <code>ORDER BY</code> clause of a the {@link Query} annotation),
     * followed by the order in which the {@link Sort} parameters to this
     * method are listed.</p>
     *
     * @param sort sort criteria to use.
     * @return a new instance of <code>PageRequest</code>. T
     *         his method never returns <code>null</code>.
     */
    PageRequest<T> sortBy(Sort<? super T> sort);

    /**
     * <p>Creates a new page request with the same pagination information,
     * but using the specified sort criteria. The order of precedence for
     * sort criteria is that of any statically specified sort criteria
     * (from the <code>OrderBy</code> keyword, {@link OrderBy} annotation,
     * or <code>ORDER BY</code> clause of a the {@link Query} annotation),
     * followed by the order in which the {@link Sort} parameters to this
     * method are listed.</p>
     *
     * @param sort1 dynamic sort criteria to use first.
     * @param sort2 dynamic sort criteria to use second.
     * @return a new instance of <code>PageRequest</code>.
     *         This method never returns <code>null</code>.
     */
    PageRequest<T> sortBy(Sort<? super T> sort1, Sort<? super T> sort2);

    /**
     * <p>Creates a new page request with the same pagination information,
     * but using the specified sort criteria. The order of precedence for
     * sort criteria is that of any statically specified sort criteria
     * (from the <code>OrderBy</code> keyword, {@link OrderBy} annotation,
     * or <code>ORDER BY</code> clause of a the {@link Query} annotation),
     * followed by the order in which the {@link Sort} parameters to this
     * method are listed.</p>
     *
     * @param sort1 dynamic sort criteria to use first.
     * @param sort2 dynamic sort criteria to use second.
     * @param sort3 dynamic sort criteria to use last.
     * @return a new instance of <code>PageRequest</code>.
     *         This method never returns <code>null</code>.
     */
    PageRequest<T> sortBy(Sort<? super T> sort1, Sort<? super T> sort2, Sort<? super T> sort3);

    /**
     * <p>Creates a new page request with the same pagination information,
     * but using the specified sort criteria. The order of precedence for
     * sort criteria is that of any statically specified sort criteria
     * (from the <code>OrderBy</code> keyword, {@link OrderBy} annotation,
     * or <code>ORDER BY</code> clause of a the {@link Query} annotation),
     * followed by the order in which the {@link Sort} parameters to this
     * method are listed.</p>
     *
     * @param sort1 dynamic sort criteria to use first.
     * @param sort2 dynamic sort criteria to use second.
     * @param sort3 dynamic sort criteria to use third.
     * @param sort4 dynamic sort criteria to use last.
     * @return a new instance of <code>PageRequest</code>.
     *         This method never returns <code>null</code>.
     */
    PageRequest<T> sortBy(Sort<? super T> sort1, Sort<? super T> sort2, Sort<? super T> sort3, Sort<? super T> sort4);

    /**
     * <p>Creates a new page request with the same pagination information,
     * but using the specified sort criteria. The order of precedence for
     * sort criteria is that of any statically specified sort criteria
     * (from the <code>OrderBy</code> keyword, {@link OrderBy} annotation,
     * or <code>ORDER BY</code> clause of a the {@link Query} annotation),
     * followed by the order in which the {@link Sort} parameters to this
     * method are listed.</p>
     *
     * @param sort1 dynamic sort criteria to use first.
     * @param sort2 dynamic sort criteria to use second.
     * @param sort3 dynamic sort criteria to use third.
     * @param sort4 dynamic sort criteria to use fourth.
     * @param sort5 dynamic sort criteria to use last.
     * @return a new instance of <code>PageRequest</code>.
     *         This method never returns <code>null</code>.
     */
    PageRequest<T> sortBy(Sort<? super T> sort1, Sort<? super T> sort2, Sort<? super T> sort3, Sort<? super T> sort4, Sort<? super T> sort5);

    /**
     * Returns an otherwise-equivalent page request with
     * {@link #requestTotal()} set to {@code false}, so that
     * totals will not be retrieved from the database. Note
     * that when totals are not retrieved by a repository
     * method with return type {@link Page}, the operations
     * {@link Page#totalElements()} and {@link Page#totalPages()}
     * throw an {@link IllegalStateException} when called.
     * @return a page request with {@link #requestTotal()}
     *         set to {@code false}.
     */
    PageRequest<T> withoutTotal();

    /**
     * Returns an otherwise-equivalent page request with
     * {@link #requestTotal()} set to {@code false}, so that
     * totals will be retrieved from the database.
     * @return a page request with {@link #requestTotal()}
     *         set to {@code true}.
     */
    PageRequest<T> withTotal();

    /**
     * The type of pagination: offset-based or cursor-based, which includes
     * a direction.
     */
    enum Mode {
        /**
         * Indicates forward cursor-based pagination, which follows the
         * direction of the sort criteria, using a cursor that is
         * formed from the key of the last entity on the current page.
         */
        CURSOR_NEXT,

        /**
         * Indicates a request for a page with cursor-based pagination
         * in the previous page direction to the sort criteria, using a cursor
         * that is formed from the key of first entity on the current page.
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
     * A cursor that is formed from a key, relative to which a next
     * or previous page can be requested.
     */
    interface Cursor {
        /**
         * Returns whether or not the values that make up the key of this cursor
         * are equal to those of the supplied cursor.
         * Both instances must also have the same cursor implementation class
         * in order to be considered equal.
         *
         * @param cursor a cursor against which to compare.
         * @return true or false.
         */
        @Override
        boolean equals(Object cursor);

        /**
         * Returns the key value at the specified position.
         *
         * @param  index position (0 is first) of the key value to obtain.
         * @return the key value at the specified position.
         * @throws IndexOutOfBoundsException if the index is negative
         *         or greater than or equal to the {@link #size}.
         */
        Object get(int index);

        /**
         * Returns a hash code based on the key values.
         *
         * @return a hash code based on the key values.
         */
        @Override
        int hashCode();

        /**
         * Returns the number of values in the key.
         *
         * @return the number of values in the key.
         */
        int size();

        /**
         * An unmodifiable list of values in the key.
         *
         * @return an unmodifiable list containing the
         *         ordered values
         */
        List<?> elements();

        /**
         * String representation of the cursor, including the number of
         * key values in the cursor but not the values themselves.
         *
         * @return String representation of the cursor.
         */
        @Override
        String toString();

        /**
         * Obtain an instance of {@code Cursor} for the given key.
         * @param key the key
         * @return a new instance of {@code Cursor}
         */
        static Cursor forKey(Object... key) {
            return new PageRequestCursor(key);
        }
    }
}