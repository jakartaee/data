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

import jakarta.data.metamodel.StaticMetamodel;
import jakarta.data.repository.OrderBy;

import java.util.Iterator;
import java.util.List;

/**
 * <p>Requests sorting on various entity attributes.</p>
 *
 * <p>A query method of a repository may have a parameter of type
 * {@code Order} if its return type indicates that it may return
 * multiple entities. The parameter of type {@code Order} must occur
 * after the method parameters representing regular parameters of
 * the query itself.</p>
 *
 * <p>The {@code Order} class is useful in combination with the
 * {@link StaticMetamodel} for helping to enforce type safety of
 * sort criteria during development. For example,</p>
 *
 * <pre>
 * Page&lt;Employee>&gt; findByYearHired(int year, PageRequest pageRequest, Order&lt;Employee&gt;);
 * ...
 * page1 = employees.findByYearHired(Year.now(),
 *                                   PageRequest.ofSize(10),
 *                                   Order.by(_Employee.salary.desc(),
 *                                            _Employee.lastName.asc(),
 *                                            _Employee.firstName.asc()));
 * </pre>
 *
 * <p>The relative precedence of an instance of {@link Sort} belonging
 * to an {@code Order} is determined by its position within the
 * {@linkplain #sorts() list} of {@code Sort} instances.</p>
 *
 * <p>A repository method may declare static sorting criteria using
 * the {@code OrderBy} keyword or {@link OrderBy @OrderBy} annotation,
 * and also accept dynamic sorting criteria via its parameters. In this
 * situation, the static sorting criteria are applied first, followed by
 * any dynamic sorting criteria specified by instances of {@link Sort}.</p>
 *
 * <p>In the example above, the matching employees are sorted first by salary
 * from highest to lowest. Employees with the same salary are then sorted
 * alphabetically by last name. Employees with the same salary and last name
 * are then sorted alphabetically by first name.</p>
 *
 * <p>A repository method may not be declared with more than one parameter
 * of type {@code Order}.</p>
 *
 * <p>A repository method throws {@link jakarta.data.exceptions.DataException}
 * if the database is incapable of ordering the query results using the given
 * sort criteria.</p>
 *
 * @param <T> entity class of the attributes that are used as sort criteria.
 */
public class Order<T> implements Iterable<Sort<? super T>> {

    /**
     * Unmodifiable list of Sort instances, from highest precedence to lowest.
     */
    private final List<Sort<? super T>> sorts;

    /**
     * Creates a new instance.
     *
     * @param sorts unmodifiable list of Sort instances, from highest precedence to lowest.
     */
    private Order(List<Sort<? super T>> sorts) {
        this.sorts = sorts;
    }

    /**
     * <p>Defines a list of {@link Sort} criteria, ordered from highest precedence
     * to lowest precedence.</p>
     *
     * @param <T>   entity class of the attributes that are used as sort criteria.
     * @param sorts sort criteria to use, ordered from highest precedence to lowest precedence.
     * @return a new instance indicating the order of precedence for sort criteria.
     *         This method never returns {@code null}.
     */
    @SafeVarargs
    public static <T> Order<T> by(Sort<? super T>... sorts) {
        return new Order<T>(List.of(sorts));
    }

    /**
     * <p>Defines a list of {@link Sort} criteria, ordered from highest precedence
     * to lowest precedence.</p>
     *
     * @param <T>   entity class of the attributes that are used as sort criteria.
     * @param sorts sort criteria to use, ordered from highest precedence to lowest precedence.
     * @return a new instance indicating the order of precedence for sort criteria.
     *         This method never returns {@code null}.
     */
    public static <T> Order<T> by(List<Sort<? super T>> sorts) {
        return new Order<T>(List.copyOf(sorts));
    }

    /**
     * The instances of {@link Sort} belonging to this {@code Order}.
     *
     * @return the instances of {@link Sort}, from highest precedence to lowest precedence.
     */
    public List<Sort<? super T>> sorts() {
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
    public Iterator<Sort<? super T>> iterator() {
        return sorts.iterator();
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
