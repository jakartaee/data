/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package jakarta.data;

import java.util.Iterator;
import java.util.List;

import jakarta.data.metamodel.StaticMetamodel;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;

/**
 * <p>Requests sorting on various entity attributes.</p>
 *
 * <p><code>Order</code> can be optionally specified as a
 * parameter to a repository find method in any of the positions
 * that are after the query parameters, or it can be used
 * to obtain a {@link PageRequest} that is similarly
 * specified as a parameter to a repository find method.</p>
 *
 * <p>The {@code Order} class is useful in combination with the
 * {@link StaticMetamodel} for helping to enforce type safety of
 * sort criteria during development. For example,</p>
 *
 * <pre>
 * {@code Page<Employee>} findByYearHired(int year, {@code PageRequest<Employee>} pageRequest);
 * ...
 * page1 = employees.findByYearHired(Year.now(),
 *                                   Order.by(_Employee.salary.desc(),
 *                                            _Employee.lastName.asc(),
 *                                            _Employee.firstName.asc())
 *                                        .page(1)
 *                                        .size(10));
 * </pre>
 *
 * <p>When combined on a method with static sort criteria
 * (<code>OrderBy</code> keyword or {@link OrderBy} annotation or
 * {@link Query} with an <code>ORDER BY</code> clause), the static
 * sort criteria is applied first, followed by the dynamic sort criteria
 * that is defined by {@link Sort} instances in the order listed.</p>
 *
 * <p>In the example above, the matching employees are sorted first by salary
 * from highest to lowest. Employees with the same salary are then sorted
 * alphabetically by last name. Employees with the same salary and last name
 * are then sorted alphabetically by first name.</p>
 *
 * <p>A repository method will fail with a
 * {@link jakarta.data.exceptions.DataException DataException}
 * or a more specific subclass if</p>
 * <ul>
 * <li>an <code>Order</code> parameter is
 *     specified in combination with a {@link PageRequest} parameter with
 *     {@link PageRequest#sorts()}.</li>
 * <li>the database is incapable of ordering with the requested
 *     sort criteria.</li>
 * </ul>
 *
 * @param <T> entity class of the attributes that are used as sort criteria.
 */
public class Order<T> implements Iterable<Sort<T>> {

    /**
     * Unmodifiable list of Sort instances, from highest precedence to lowest.
     */
    private final List<Sort<T>> sorts;

    /**
     * Creates a new instance.
     *
     * @param sorts unmodifiable list of Sort instances, from highest precedence to lowest.
     */
    private Order(List<Sort<T>> sorts) {
        this.sorts = sorts;
    }

    /**
     * <p>Defines a list of {@link Sort} criteria, ordered from highest precedence
     * to lowest precedence.</p>
     *
     * @param <T>   entity class of the attributes that are used as sort criteria.
     * @param sorts sort criteria to use, ordered from highest precedence to lowest precedence.
     * @return a new instance indicating the order of precedence for sort criteria.
     *         This method never returns <code>null</code>.
     */
    @SafeVarargs
    public static final <T> Order<T> by(Sort<T>... sorts) {
        return new Order<T>(List.of(sorts));
    }

    /**
     * The instances of {@link Sort} belonging to this {@code Order}.
     *
     * @return the instances of {@link Sort}, from highest precedence to lowest precedence.
     */
    public List<Sort<T>> sorts() {
        return sorts;
    }

    /**
     * Determines whether this instance specifies matching {@link Sort} criteria
     * in the same order of precedence as another instance.
     *
     * @return true if the other instance is an {@code Order} that specifies
     *         the same ordering of sort criteria as this instance.
     */
    @Override
    public boolean equals(Object other) {
        return this == other
            || other instanceof Order s && sorts.equals(s.sorts);
    }

    /**
     * Computes a hash code for this instance.
     *
     * @return hash code.
     */
    @Override
    public int hashCode() {
        return sorts.hashCode();
    }

    /**
     * Returns an iterator that follows the order of precedence for the
     * {@link Sort} criteria, from highest precedence to lowest.
     *
     * @return iterator over the sort criteria.
     */
    @Override
    public Iterator<Sort<T>> iterator() {
        return sorts.iterator();
    }

    /**
     * Create a {@link PageRequest} for the specified page number
     * of page size 10 (the default for {@code PageRequest})
     * of the query results sorted according to any static sort criteria that
     * is specified and the ordered list of {@link Sort} criteria
     * that is represented by this instance.
     *
     * @param pageNumber requested page number.
     * @return a request for a page of results that are sorted based on the sort criteria represented by this instance
     *         and with the specified page number. This method never returns <code>null</code>.
     */
    public PageRequest<T> page(long pageNumber) {
        return PageRequest.<T>ofPage(pageNumber).sortBy(sorts);
    }

    /**
     * Create a {@link PageRequest} for the first page of the specified page size
     * of the query results sorted according to any static sort criteria that
     * is specified and the ordered list of {@link Sort} criteria
     * that is represented by this instance.
     *
     * @param size requested size of pages.
     * @return a request for a page of results that are sorted based on the sort criteria represented by this instance
     *         and with the specified page size. This method never returns <code>null</code>.
     */
    public PageRequest<T> pageSize(int size) {
        return PageRequest.<T>ofSize(size).sortBy(sorts);
    }

    /**
     * Textual representation of this instance, including the result of invoking
     * {@link Sort#toString()} on each member of the sort criteria, in order of
     * precedence from highest to lowest.
     *
     * @return textual representation of this instance.
     */
    @Override
    public String toString() {
        return sorts.toString();
    }
}