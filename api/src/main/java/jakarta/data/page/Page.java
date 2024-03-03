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

import jakarta.data.repository.Query;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <p>A page contains the data that is retrieved to satisfy a given page request.
 * An instance of {@code Page} is obtained by supplying a {@link PageRequest} as
 * an argument of a repository method. For example,</p>
 *
 * <pre>
 * {@code @Find}
 * {@code Page<Vehicle>} search({@code @By("make")} String make,
 *                       {@code @By("model")} String model,
 *                       {@code @By("year")} int designYear,
 *                       {@code PageRequest<?>} pageRequest);
 * </pre>
 *
 * <p>If {@link PageRequest#requestTotal()} is enabled, the {@link Page} also
 * contains information about the {@linkplain #totalPages total number of pages}
 * and the {@linkplain #totalElements total number of elements} that can be
 * retrieved by the query.</p>
 *
 * @param <T> the type of elements in this page.
 */
public interface Page<T> extends Iterable<T> {

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
     * Returns whether the {@link Page} has content at all.
     *
     * @return whether the {@link Page} has content at all.
     */
    boolean hasContent();

    /**
     * Returns a sequential stream of results, which follow the order of the sort criteria if specified.
     *
     * @return a stream of results.
     */
    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Returns the number of elements on this {@code Page},
     * which must be no larger than the maximum
     * {@link PageRequest#size() size} of the page request.
     * If the number of elements in the page is less than the maximum page size,
     * then there are no subsequent pages of data to read.
     *
     * @return the number of elements on this {@code Page}.
     */
    int numberOfElements();

    /**
     * Returns {@code true} if it is known that there are more results or that it is
     * necessary to request a next page to determine whether there are more results, so that
     * {@link #nextPageRequest()} will definitely not return {@code null}.
     * @return {@code false} if this is the last page of results.
     */
    boolean hasNext();

    /**
     * Returns {@code true} if it is known that there are previous results or that it
     * is necessary to request the previous page to determine whether there are previous
     * results, so that {@link #previousPageRequest()} will not return {@code null}.
     * @return {@code false} if this is the first page of results.
     */
    boolean hasPrevious();

    /**
     * Returns the {@linkplain PageRequest page request} for which this
     * page was obtained.
     *
     * @return the request for the current page; will never be {@code null}.
     */
    PageRequest<T> pageRequest();

    /**
     * <p>Returns the {@linkplain PageRequest page request} for which this page
     * was obtained.</p>
     *
     * <p>This method is provided for when {@linkplain Query query language} is used to
     * return a result of different type than the entity that is being queried.
     * This method allows the {@link PageRequest} to be returned for the
     * type of entity class that was queried.</p>
     *
     * @param <E>         entity class of the attributes that are used as sort criteria.
     * @param entityClass entity class of the attributes that are used as sort criteria.
     * @return the request for the current page; will never be {@code null}.
     */
    <E> PageRequest<E> pageRequest(Class<E> entityClass);

    /**
     * Returns a request for the {@linkplain PageRequest#next() next} page if
     * {@link #hasNext()} indicates there might be a next page.
     *
     * @return a request for the next page.
     * @throws NoSuchElementException if it is known that there is no next page.
     *         To avoid this exception, check for a {@code true} result of
     *         {@link #hasNext()} before invoking this method.
     */
    PageRequest<T> nextPageRequest();

    /**
     * <p>Returns a request for the {@linkplain PageRequest#next() next} page if
     * {@link #hasNext()} indicates there might be a next page.</p>
     *
     * <p>This method is useful when {@linkplain Query query language} is used to
     * return a result of different type than the entity that is being queried.
     * This method allows the subsequent {@link PageRequest} to be returned for the
     * type of entity class that is being queried.</p>
     *
     * @param <E>         entity class of the attributes that are used as sort criteria.
     * @param entityClass entity class of the attributes that are used as sort criteria.
     * @return a request for the next page.
     */
    <E> PageRequest<E> nextPageRequest(Class<E> entityClass);

    /**
     * <p>Returns a request for the {@link PageRequest#previous() previous} page,
     * or <code>null</code> if it is known that there is no previous page.</p>
     *
     * @return a request for the previous page.
     */
    PageRequest<T> previousPageRequest();

    /**
     * <p>Returns a request for the {@linkplain PageRequest#previous() previous}
     * page, or <code>null</code> if it is known that there is no previous page.</p>
     *
     * <p>This method is useful when {@linkplain Query query language} is used to
     * return a result of different type than the entity that is being queried.
     * This method allows the subsequent {@link PageRequest} to be returned for the
     * type of entity class that is being queried.</p>
     *
     * @param <E>         entity class of the attributes that are used as sort criteria.
     * @param entityClass entity class of the attributes that are used as sort criteria.
     * @return a request for the previous page.
     */
    <E> PageRequest<E> previousPageRequest(Class<E> entityClass);

    /**
     * Returns {@code true} if the {@link #pageRequest()} specified that the
     * {@linkplain PageRequest#requestTotal total number of elements should
     * be retrieved from the database}, and that it is therefore safe to call
     * {@link #totalElements()} or {@link #totalPages()}.
     * @return {@code true} if totals are available.
     */
    boolean hasTotals();

    /**
     * Returns the total number of elements across all pages of query results, if the
     * {@link #pageRequest()} specified that {@linkplain PageRequest#requestTotal the
     * total should be retrieved from the database}.
     * @return the total number of elements across all pages.
     * @throws IllegalStateException if the total was not retrieved from the database.
     */
    long totalElements();

    /**
     * Returns the total number of pages of query results, if the {@link #pageRequest()}
     * specified that {@linkplain PageRequest#requestTotal the total should be retrieved
     * from the database}.
     * @return the total number of pages.
     * @throws IllegalStateException if the total was not retrieved from the database.
     */
    long totalPages();
}
