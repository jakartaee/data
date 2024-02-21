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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.page;

import jakarta.data.Streamable;
import jakarta.data.repository.Query;
import java.util.List;

/**
 * <p>A slice contains the data that is retrieved for a page request.
 * A slice is obtained by supplying a {@link PageRequest} parameter to a repository method.
 * For example,</p>
 *
 * <pre>
 * {@code @Find}
 * {@code Slice<Vehicle>} search({@code @By("make")} String make,
 *                       {@code @By("model")} String model,
 *                       {@code @By("year")} int designYear,
 *                       {@code Pageable<?>} pageRequest);
 * </pre>
 *
 * <p>Unlike {@link Page}, a {@code Slice} does not have awareness of the total number of pages
 * that can be retrieved for the query.</p>
 *
 * <p>The lowercase terms, <i>slice</i> and <i>page</i>, are used interchangeably
 * throughout this specification. Wherever a distinction needs to be made between them,
 * the class name, {@code Slice} or {@code Page}, is used.</p>
 *
 * @param <T> the type of elements in this slice.
 */
public interface Slice<T> extends Streamable<T> {

    /**
     * Returns the page content as a {@link List}.
     * The list is sorted according to the combined sort criteria of the repository method
     * and the sort criteria of the page request that is supplied to the repository method,
     * sorting first by the sort criteria of the repository method,
     * and then by the sort criteria of the page request.
     *
     * @return the page content as a {@link List}; will never be {@code null}.
     */
    List<T> content();

    /**
     * Returns whether the {@link Slice} has content at all.
     *
     * @return whether the {@link Slice} has content at all.
     */
    boolean hasContent();

    /**
     * Returns the number of elements in this Slice,
     * which must be no larger than the maximum
     * {@link PageRequest#size() size} of the page request.
     * If the number of elements in the slice is less than the maximum page size,
     * then there are no subsequent slices of data to read.
     *
     * @return the number of elements in this Slice.
     */
    int numberOfElements();

    /**
     * Returns the {@link PageRequest page request} for which this
     * slice was obtained.
     *
     * @return the request for the current page; will never be {@code null}.
     */
    PageRequest<T> pageable();

    /**
     * <p>Returns the {@link PageRequest page request} for which this
     * slice was obtained.</p>
     *
     * <p>This method is provided for when {@link Query query language} is used to
     * return a result of different type than the entity that is being queried.
     * This method allows the {@link PageRequest} to be returned for the
     * type of entity class that was queried.</p>
     *
     * @param <E>         entity class of the attributes that are used as sort criteria.
     * @param entityClass entity class of the attributes that are used as sort criteria.
     * @return the request for the current page; will never be {@code null}.
     */
    <E> PageRequest<E> pageable(Class<E> entityClass);

    /**
     * Returns a request for the {@link PageRequest#next() next} page, or <code>null</code> if it is known that there is no next page.
     *
     * @return a request for the next page.
     */
    PageRequest<T> nextPageable();

    /**
     * <p>Returns a request for the {@link PageRequest#next() next} page,
     * or <code>null</code> if it is known that there is no next page.</p>
     *
     * <p>This method is useful when {@link Query query language} is used to
     * return a result of different type than the entity that is being queried.
     * This method allows the subsequent {@link PageRequest} to be returned for the
     * type of entity class that is being queried.</p>
     *
     * @param <E>         entity class of the attributes that are used as sort criteria.
     * @param entityClass entity class of the attributes that are used as sort criteria.
     * @return a request for the next page.
     */
    <E> PageRequest<E> nextPageable(Class<E> entityClass);
}
