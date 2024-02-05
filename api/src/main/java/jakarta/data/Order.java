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

import jakarta.data.page.Pageable;

/**
 * TODO Requests the ordering of find operation results based on {@link Sort} criteria.
 *
 * @param <T> entity class of the attributes that are used as sort criteria.
 */
public class Order<T> implements Iterable<Sort<T>> {

    private final List<Sort<T>> sorts;

    private Order(List<Sort<T>> sorts) {
        this.sorts = sorts;
    }

    /**
     * TODO
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
     * TODO
     */
    @Override
    public boolean equals(Object other) {
        return this == other
            || other instanceof Order s && sorts.equals(s.sorts);
    }

    /**
     * TODO
     */
    @Override
    public int hashCode() {
        return sorts.hashCode();
    }

    /**
     * TODO
     */
    @Override
    public Iterator<Sort<T>> iterator() {
        return sorts.iterator();
    }

    /**
     * TODO
     *
     * @param pageNumber requested page number.
     * @return a request for a page of results that are sorted based on the sort criteria represented by this instance
     *         and with the specified page number. This method never returns <code>null</code>.
     */
    public Pageable<T> page(long pageNumber) {
        return Pageable.<T>ofPage(pageNumber).sortBy(sorts);
    }

    /**
     * TODO
     *
     * @param size requested size of pages.
     * @return a request for a page of results that are sorted based on the sort criteria represented by this instance
     *         and with the specified page size. This method never returns <code>null</code>.
     */
    public Pageable<T> pageSize(int size) {
        return Pageable.<T>ofSize(size).sortBy(sorts);
    }

    /**
     * TODO
     */
    @Override
    public String toString() {
        return sorts.toString();
    }
}