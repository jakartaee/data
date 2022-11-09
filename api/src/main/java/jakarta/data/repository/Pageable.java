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
 * for (Pageable p = Pageable.ofSize(100); p != null; p = page.length == 0 ? null : p.next()) {
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
public interface Pageable {

    /**
     * Creates a new <code>Pageable</code> with the given page number and with a default size of 10.
     *
     * @param pageNumber The page number.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     * @throws IllegalArgumentException when the page number is negative or zero.
     */
    static Pageable ofPage(long pageNumber) {
        return new Pagination(pageNumber, 10, Collections.emptyList());
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
        return new Pagination(1, maxPageSize, Collections.emptyList());
    }

    /**
     * <p>Requests {@link KeysetPageable keyset pagination} in the forward direction,
     * starting after the specified keyset values.</p>
     *
     * @param keyset keyset values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>KeysetPageable</code> with forward keyset pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    KeysetPageable afterKeyset(Object... keyset);

    /**
     * <p>Requests {@link KeysetPageable keyset pagination} in the reverse direction,
     * starting after the specified keyset values.</p>
     *
     * @param keyset keyset values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>KeysetPageable</code> with reverse keyset pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    KeysetPageable beforeKeyset(Object... keyset);

    /**
     * <p>Requests {@link KeysetPageable keyset pagination} in the forward direction,
     * starting after the specified keyset values.</p>
     *
     * @param keysetCursor cursor with keyset values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>KeysetPageable</code> with forward keyset pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    KeysetPageable afterKeysetCursor(Cursor keysetCursor);

    /**
     * <p>Requests {@link KeysetPageable keyset pagination} in the reverse direction,
     * starting after the specified keyset values.</p>
     *
     * @param keysetCursor cursor with keyset values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>KeysetPageable</code> with reverse keyset pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    KeysetPageable beforeKeysetCursor(Cursor keysetCursor);

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
     * Returns the page to be returned.
     *
     * @return the page to be returned.
     */
    long getPage();

    /**
     * Returns the requested size of each page
     *
     * @return the requested size of each page
     */
    int getSize();

    /**
     * Return the order collection if it was specified on this <code>Pageable</code>, otherwise an empty list.
     *
     * @return the order collection
     */
    List<Sort> getSorts();

    /**
     * Returns the <code>Pageable</code> requesting the next page.
     *
     * @return The next pageable.
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
     * The order of precedence of sort criteria is the order of the
     * {@link Iterable} that is supplied to this method.</p>
     *
     * <p>A repository method will fail if a sort criteria is specified on a
     * <code>Pageable</code> in combination with any of:</p>
     * <ul>
     * <li>an <code>OrderBy</code> keyword</li>
     * <li>an {@link OrderBy} annotation</li>
     * <li>a {@link Query} annotation that contains an <code>ORDER BY</code> clause.</li>
     * <li>{@link Sort} parameters that are specified independently of
     *     <code>Pageable</code> on a repository method</li>
     * </ul>
     *
     * @param sorts sort criteria to use.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    Pageable sortBy(Iterable<Sort> sorts);

    /**
     * <p>Creates a new <code>Pageable</code> instance representing the same
     * pagination information, except using the specified sort criteria.
     * The order of precedence of sort criteria is the order in which the
     * {@link Sort} parameters to this method are listed.</p>
     *
     * <p>A repository method will fail if a sort criteria is specified on a
     * <code>Pageable</code> in combination with any of:</p>
     * <ul>
     * <li>an <code>OrderBy</code> keyword</li>
     * <li>an {@link OrderBy} annotation</li>
     * <li>a {@link Query} annotation that contains an <code>ORDER BY</code> clause.</li>
     * <li>{@link Sort} parameters that are specified independently of
     *     <code>Pageable</code> on a repository method</li>
     * </ul>
     *
     * @param sorts sort criteria to use. This method can be invoked without parameters
     *        to request a <code>Pageable</code> that does not specify sort criteria.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    Pageable sortBy(Sort... sorts);
}