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

/**
 * <p>Keyset pagination is a form of pagination that aims to reduce the
 * possibility of missed or duplicate results by making the request for
 * each subsequent page relative to the observed values of entity properties
 * from the current page. This list of values is referred to as the keyset
 * and only includes values of entity properties that are in the sort criteria
 * of the repository method. The combination of sort criteria must uniquely
 * identify each entity. The keyset values can be from the last entity
 * (for pagination in a forward direction) or first entity on the page
 * (if requesting pages in a reverse direction),
 * or can be any other desired list of values which serve as a new starting
 * point. Keyset pagination also has the potential to improve performance
 * by avoiding the fetching and ordering of results from prior pages
 * because these become non-matching.</p>
 *
 * <p>To use keyset pagination, define a repository method with return value of
 * {@link KeysetAwareSlice} or {@link KeysetAwarePage} and which accepts a
 * special parameter (after the normal query parameters) that is a
 * {@link Pageable}. For example,</p>
 *
 * <pre>
 * &#64;OrderBy("lastName")
 * &#64;OrderBy("firstName")
 * &#64;OrderBy("id")
 * KeysetAwareSlice&lt;Employee&gt; findByHoursWorkedGreaterThan(int hours, Pageable pagination);
 * </pre>
 *
 * <p>You can use a normal {@link Pageable} to request an initial page,</p>
 *
 * <pre>
 * page = employees.findByHoursWorkedGreaterThan(1500, Pageable.ofSize(50));
 * </pre>
 *
 * <p>For subsequent pages, you can request pagination relative to the
 * end of the current page as follows,</p>
 *
 * <pre>
 * page = employees.findByHoursWorkedGreaterThan(1500, page.nextPageable());
 * </pre>
 *
 * <p>Because the page is keyset aware, the <code>KeysetPageable</code>
 * that it returns from the call to {@link KeysetAwareSlice#nextPageable}
 * above is based upon a keyset from that page to use as a starting point
 * after which the results for the next page are to be found.</p>
 *
 * <p>You can also construct a <code>KeysetPageable</code> directly, which
 * allows you to make it relative to a specific list of values. The number and
 * order of values must match that of the {@link OrderBy} annotations,
 * {@link Pageable#sortBy(Sort...)} or {@link Pageable#sortBy(Iterable)} parameters,
 * or <code>OrderBy</code> name pattern of the repository method.
 * For example,</p>
 *
 * <pre>
 * Employee emp = ...
 * KeysetPageable pagination = Pageable.ofSize(50).afterKeyset(emp.lastName, emp.firstName, emp.id);
 * page = employees.findByHoursWorkedGreaterThan(1500, pagination);
 * </pre>
 *
 * <p>By making the query for the next page relative to observed values,
 * not a numerical position, keyset pagination is less vulnerable to changes
 * that are made to data in between page requests. Adding or removing entities
 * is possible without causing unexpected missed or duplicate results.
 * Keyset pagination does not prevent misses and duplicates if the entity
 * properties which are the sort criteria for existing entities are modified
 * or if an entity is re-added with different sort criteria after having
 * previously been removed.</p>
 *
 * <h2>Keyset Pagination with &#64;Query</h2>
 *
 * <p>Keyset pagination involves generating and appending to the query
 * additional query conditions for the keyset properties. In order for that to
 * be possible, a user-provided
 * <a href="https://eclipse-ee4j.github.io/jakartaee-tutorial/#full-query-language-syntax">JPQL</a>
 * query must end with a
 * <code>WHERE</code> clause to which additional conditions can be appended.
 * Enclose the entire conditional expression of the <code>WHERE</code> clause
 * in parenthesis.
 * Sort criteria must be specified independently from the user-provided query,
 * either with the {@link OrderBy} annotation or
 * {@link Pageable#sortBy(Sort...)} or {@link Pageable#sortBy(Iterable)} parameters.
 * For example,</p>
 *
 * <pre>
 * &#64;Query("SELECT o FROM Customer o WHERE (o.ordersPlaced &gt;= ?1 OR o.totalSpent &gt;= ?2)")
 * &#64;OrderBy("zipcode")
 * &#64;OrderBy("birthYear")
 * &#64;OrderBy("id")
 * KeysetAwareSlice&lt;Customer&gt; getTopBuyers(int minOrders, float minSpent, Pageable pagination);
 * </pre>
 *
 * <h2>Page Numbers and Totals</h2>
 *
 * <p>Page numbers, total numbers of elements across all pages, and total
 * count of pages are not accurate when keyset pagination is used and should
 * not be relied upon.</p>
 */
public interface KeysetPageable extends Pageable {
    /**
     * Direction of keyset pagination.
     */
    public static enum Mode {
        /**
         * Indicates forward keyset pagination, which follows the
         * direction of the {@link OrderBy} annotations,
         * {@link Pageable#sortBy(Sort...)} or {@link Pageable#sortBy(Iterable)}
         * parameters, repository method {@link Sort} parameters,
         * or <code>OrderBy</code> name pattern of the repository method.
         */
        NEXT,

        /**
         * Indicates a request for a page in the reverse direction of
         * the {@link OrderBy} annotations,
         * {@link Pageable#sortBy(Sort...)} or {@link Pageable#sortBy(Iterable)}
         * parameters, repository method {@link Sort} parameters,
         * or <code>OrderBy</code> name pattern of the repository method.
         * The order of results on each page follows the sort criteria
         * and is not reversed.
         */
        PREVIOUS
    }

    /**
     * Represents keyset values, which can be a starting point for
     * requesting a next or previous page.
     */
    public interface Cursor {
        /**
         * Returns whether or not the keyset values of this instance
         * are equal to those of the supplied instance.
         * Cursor implementation classes must also match to be equal.
         *
         * @return true or false.
         */
        @Override
        public boolean equals(Object o);

        /**
         * Returns the keyset value at the specified position.
         *
         * @param  index position (0 is first) of the keyset value to obtain.
         * @return the keyset value at the specified position.
         * @throws IndexOutOfBoundsException if the index is negative
         *         or greater than or equal to the {@link #size}.
         */
        public Object getKeysetElement(int index);

        /**
         * Returns a hash code based on the keyset values.
         *
         * @return a hash code based on the keyset values.
         */
        @Override
        public int hashCode();

        /**
         * Returns the number of values in the keyset.
         *
         * @return the number of values in the keyset.
         */
        public int size();

        /**
         * String representation of the keyset cursor, including the number of
         * key values in the cursor but not the values themselves.
         *
         * @return String representation of the keyset cursor.
         */
        @Override
        public String toString();
    }

    /**
     * Returns the keyset values which are the starting point for
     * keyset pagination.
     *
     * @return the keyset values.
     */
    public Cursor getCursor();

    /**
     * Returns the direction of keyset pagination.
     *
     * @return the direction of keyset pagination.
     */
    public Mode getMode();

    /**
     * Raises an error because traversal of pages with keyset pagination can
     * only be done via the {@link KeysetAwareSlice#nextPageable()},
     * {@link KeysetAwareSlice#previousPageable()}, or
     * {@link KeysetAwareSlice#getKeysetCursor(int) keyset cursor}.
     *
     * @return unreachable
     * @throws UnsupportedOperationException because this operation is not
     *         supported for keyset pagination.
     */
    @Override
    public KeysetPageable next();

    /**
     * <p>Creates a new <code>KeysetPageable</code> instance representing the same
     * pagination information, except with the specified page number.</p>
     *
     * @param pageNumber The page number
     * @return a new instance of <code>KeysetPageable</code>. This method never returns <code>null</code>.
     */
    @Override
    public KeysetPageable page(long pageNumber);

    /**
     * <p>Creates a new <code>KeysetPageable</code> instance representing the same
     * pagination information, except with the specified maximum page size.</p>
     *
     * @param maxPageSize the number of query results in a full page.
     * @return a new instance of <code>KeysetPageable</code>. This method never returns <code>null</code>.
     */
    @Override
    public KeysetPageable size(int maxPageSize);

    /**
     * <p>Creates a new <code>KeysetPageable</code> instance representing the same
     * pagination information, except using the specified sort criteria.
     * The order of precedence of sort criteria is the order of the
     * {@link Iterable} that is supplied to this method.</p>
     *
     * <p>A repository method will fail if a sort criteria is specified on a
     * <code>KeysetPageable</code> in combination with any of:</p>
     * <ul>
     * <li>an <code>OrderBy</code> keyword</li>
     * <li>an {@link OrderBy} annotation</li>
     * <li>a {@link Query} annotation that contains an <code>ORDER BY</code> clause.</li>
     * <li>{@link Sort} parameters that are specified independently of
     *     <code>KeysetPageable</code> on a repository method</li>
     * </ul>
     *
     * @param sorts sort criteria to use.
     * @return a new instance of <code>KeysetPageable</code>. This method never returns <code>null</code>.
     */
    @Override
    public KeysetPageable sortBy(Iterable<Sort> sorts);

    /**
     * <p>Creates a new <code>KeysetPageable</code> instance representing the same
     * pagination information, except using the specified sort criteria.
     * The order of precedence of sort criteria is the order in which the
     * {@link Sort} parameters to this method are listed.</p>
     *
     * <p>A repository method will fail if a sort criteria is specified on a
     * <code>KeysetPageable</code> in combination with any of:</p>
     * <ul>
     * <li>an <code>OrderBy</code> keyword</li>
     * <li>an {@link OrderBy} annotation</li>
     * <li>a {@link Query} annotation that contains an <code>ORDER BY</code> clause.</li>
     * <li>{@link Sort} parameters that are specified independently of
     *     <code>KeysetPageable</code> on a repository method</li>
     * </ul>
     *
     * @param sorts sort criteria to use. This method can be invoked without parameters
     *        to request a <code>KeysetPageable</code> that does not specify sort criteria.
     * @return a new instance of <code>KeysetPageable</code>. This method never returns <code>null</code>.
     */
    @Override
    public KeysetPageable sortBy(Sort... sorts);
}