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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <p>A page contains the data that is retrieved to satisfy a given page request.
 * An instance of {@code Page} is obtained by supplying a {@link PageRequest} as
 * an argument of a repository method. For example,</p>
 *
 * <pre>
 * &#64;Find
 * Page&lt;Vehicle&gt; search(&#64;By("make") String make,
 *                      &#64;By("model") String model,
 *                      &#64;By("year") int designYear,
 *                      PageRequest pageRequest,
 *                      Order&lt;Vehicle&gt; order);
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
     * Returns a sequential stream of results, which follow the order of the sort
     * criteria, if any were specified.
     *
     * @return a stream of results.
     */
    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Returns the number of elements on this {@code Page}, which must be no larger
     * than the maximum {@link PageRequest#size() size} of the page request.
     * If the number of elements in the page is smaller than the maximum page size,
     * then there are no subsequent pages of data to read.
     *
     * @return the number of elements on this {@code Page}.
     */
    int numberOfElements();

    /**
     * Returns {@code true} if it is known that there are more results or that it is
     * necessary to request a next page to determine whether there are more results,
     * so that {@link #nextPageRequest()} will definitely not return {@code null}.
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
    PageRequest pageRequest();


    /**
     * Returns a request for the next page if {@link #hasNext()} indicates there
     * might be a next page.
     *
     * @return a request for the next page.
     * @throws NoSuchElementException if it is known that there is no next page.
     *         To avoid this exception, check for a {@code true} result of
     *         {@link #hasNext()} before invoking this method.
     */
    PageRequest nextPageRequest();



    /**
     * <p>Returns a request for the previous page, if {@link #hasPrevious()}
     * indicates there might be a previous page.</p>
     *
     * @return a request for the previous page.
     * @throws NoSuchElementException if it is known that there is no previous page.
     *         To avoid this exception, check for a {@code true} result of
     *         {@link #hasPrevious()} before invoking this method.
     */
    PageRequest previousPageRequest();


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
     * @apiNote Not all NoSQL databases support counting the total number of
     *          elements. This operation is not supported for Key-Value and
     *          Wide-Column databases. For Graph and Document databases,
     *          support for this operation may vary depending on the provider.
     *          If the database does not support retrieving the total number
     *          of elements, calling this method will result in an
     *          {@link UnsupportedOperationException}.
     * @return the total number of elements across all pages.
     * @throws IllegalStateException if the total was not retrieved from the
     *                               database because the page was requested
     *                               {@linkplain PageRequest#withoutTotal() without totals}.
     * @throws UnsupportedOperationException if the database is not capable of
     *                               retrieving a total number of elements.
     */
    long totalElements();

    /**
     * Returns the total number of pages of query results, if the {@link #pageRequest()}
     * specified that {@linkplain PageRequest#requestTotal the total should be retrieved
     * from the database}.
     * @return the total number of pages.
     * @throws IllegalStateException if the total was not retrieved from the
     *                               database because the page was requested
     *                               {@linkplain PageRequest#withoutTotal() without totals}.
     * @throws UnsupportedOperationException if the database is not capable of
     *                               retrieving a total number of elements or pages.
     */
    long totalPages();
}
