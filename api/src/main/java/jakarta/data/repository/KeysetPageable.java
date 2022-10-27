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

import java.util.Arrays;
import java.util.Objects;

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
 * page = employees.findByHoursWorkedGreaterThan(1500, Pageable.size(50));
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
 * <code>Pageable</code> {@link Pageable#of(long, long, Sort...) Sort} parameters,
 * or <code>OrderBy</code> name pattern of the repository method.
 * For example,</p>
 *
 * <pre>
 * Employee emp = ...
 * KeysetPageable pagination = Pageable.size(50).afterKeyset(emp.lastName, emp.firstName, emp.id);
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
 * <code>Pageable</code> {@link Pageable#of(long, long, Sort...) Sort} parameters.
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
public class KeysetPageable extends Pageable {
    /**
     * Direction of keyset pagination.
     */
    public static enum Mode {
        /**
         * Indicates forward keyset pagination, which follows the
         * direction of the {@link OrderBy} annotations, <code>Pageable</code>
         * {@link Pageable#of(long, long, Sort...) Sort} parameters,
         * or <code>OrderBy</code> name pattern of the repository method.
         */
        NEXT,

        /**
         * Indicates a request for a page in the reverse direction of
         * the {@link OrderBy} annotations, <code>Pageable</code>
         * {@link Pageable#of(long, long, Sort...) Sort} parameters,
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
    public static class Cursor {
        /**
         * Keyset values.
         */
        protected final Object[] keyset;

        /**
         * Constructs a keyset cursor with the specified values.
         *
         * @param keyset keyset values.
         * @throws IllegalArgumentException if no keyset values are provided.
         */
        public Cursor(Object... keyset) {
            this.keyset = keyset;
            if (keyset == null || keyset.length == 0)
                throw new IllegalArgumentException("No keyset values were provided.");
        }

        /**
         * Returns whether or not the keyset values of this instance
         * are equal to those of the supplied <code>Cursor</code> instance.
         *
         * @return true if the supplied <code>Cursor</code> is equal, otherwise false.
         */
        public boolean equals(Object o) {
            return this == o || o != null
                    && o.getClass() == getClass()
                    && Arrays.equals(keyset, ((Cursor) o).keyset);
        }

        /**
         * Returns the keyset value at the specified position.
         *
         * @param  index position (0 is first) of the keyset value to obtain.
         * @return the keyset value at the specified position.
         * @throws IndexOutOfBoundsException if the index is negative
         *         or greater than or equal to the {@link #size}.
         */
        public Object getKeysetElement(int index) {
            return keyset[index];
        }

        /**
         * Returns a hash code based on the keyset values.
         *
         * @return a hash code based on the keyset values.
         */
        public int hashCode() {
            return Objects.hash(keyset);
        }

        /**
         * Returns the number of values in the keyset.
         *
         * @return the number of values in the keyset.
         */
        public int size() {
            return keyset.length;
        }

        /**
         * String representation of the keyset cursor, including the number of
         * key values in the cursor but not the values themselves.
         *
         * @return String representation of the keyset cursor.
         */
        @Override
        public String toString() {
            return new StringBuilder(getClass().getSimpleName())
                            .append('@').append(Integer.toHexString(hashCode()))
                            .append(" with ").append(keyset.length).append(" keys")
                            .toString();
        }
    }

    private final Cursor cursor;
    private final Mode mode;

    KeysetPageable(Pageable copyFrom, Mode mode, Cursor cursor) {
        super(copyFrom.size, copyFrom.page, copyFrom.sorts);

        if (cursor.keyset == null || cursor.keyset.length == 0)
            throw new IllegalArgumentException("No keyset values were provided.");

        this.cursor = cursor;
        this.mode = mode;
    }


    /**
     * Indicates if this keyset pagination information is equal to other
     * keyset pagination information. The comparison includes keyset cursor
     * instances but does not do a deep comparison of keyset values within
     * the cursor instance.
     *
     * @return true if equal. Otherwise false.
     */
    @Override
    public boolean equals(Object o) {
        KeysetPageable p;
        return this == o || o != null
                && o.getClass() == getClass()
                && (p = (KeysetPageable) o).size == size
                && p.page == page
                && p.mode == mode
                && p.cursor.equals(cursor)
                && p.sorts.equals(sorts);
    }

    /**
     * Returns the keyset values which are the starting point for
     * keyset pagination.
     *
     * @return the keyset values.
     */
    public Cursor getCursor() {
        return cursor;
    }

    /**
     * Returns the direction of keyset pagination.
     *
     * @return the direction of keyset pagination.
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * <p>Returns a hash code based upon:</p>
     * <ul>
     * <li>page number</li>
     * <li>maximum page size</li>
     * <li>mode</li>
     * <li>keyset cursor</li>
     * </ul>
     *
     * @return a hash code for the pagination information.
     */
    @Override
    public int hashCode() {
        return Objects.hash(size, page, sorts, mode, cursor);
    }

    /**
     * Returns a string representation of the pagination information.
     *
     * @return a string representation of the pagination information.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(150)
                .append("KeysetPageable{page=").append(page)
                .append(", size=").append(size)
                .append(", mode=").append(mode)
                .append(", ").append(cursor.keyset.length).append(" keys");
        for (Sort sort : sorts) {
            s.append(", ").append(sort.getProperty()).append(sort.isAscending() ? " ASC" : " DESC");
        }
        return s.append("}").toString();
    }
}