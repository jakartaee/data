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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.page;

import java.util.NoSuchElementException;

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
public interface OffSetPage<T> extends Page<T> {


    /**
     * Returns the {@linkplain PageRequest page request} for which this
     * page was obtained.
     *
     * @return the request for the current page; will never be {@code null}.
     */
    OffsetPageRequest pageRequest();


    /**
     * Returns a request for the next page if {@link #hasNext()} indicates there
     * might be a next page.
     *
     * @return a request for the next page.
     * @throws NoSuchElementException if it is known that there is no next page.
     *         To avoid this exception, check for a {@code true} result of
     *         {@link #hasNext()} before invoking this method.
     */
    OffsetPageRequest nextPageRequest();

    /**
     * <p>Returns a request for the previous page, if {@link #hasPrevious()}
     * indicates there might be a previous page.</p>
     *
     * @return a request for the previous page.
     * @throws NoSuchElementException if it is known that there is no previous page.
     *         To avoid this exception, check for a {@code true} result of
     *         {@link #hasPrevious()} before invoking this method.
     */
    OffsetPageRequest previousPageRequest();

}
