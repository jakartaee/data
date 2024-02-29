/*
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
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

/**
 * <p>A page contains the data that is retrieved for a page request
 * and has awareness of the total number of pages.</p>
 *
 * <p>A page is a sublist of results. It provides information about its position relative to the entire list.
 * A page is obtained by supplying a {@link PageRequest} parameter to a repository method. For example,</p>
 *
 * <pre>
 * {@code Page<Employee>} findByYearHired(int year, {@code PageRequest<?>} pageRequest);
 * </pre>
 *
 * <p>Repository methods that are declared to return <code>Page</code> or
 * {@link KeysetAwarePage} must raise {@link UnsupportedOperationException} if the
 * database is incapable of counting the total number of results across all pages,
 * in which case a return type of {@link Slice} or {@link KeysetAwareSlice}
 * should be used instead.</p>
 *
 * <p>For a lighter weight subset of query results that does not have awareness of the
 * total number of pages, {@link Slice} can be used instead of page.</p>
 *
 * @param <T> the type of elements in this page
 */
public interface Page<T> extends Slice<T> {

    /**
     * Returns {@code true} if it is known that there are more results or that it is
     * necessary to request a next page to determine whether there are more results, so that
     * {@link #nextPageRequest()} will definitely not return {@code null}.
     * @return {@code false} if this is the last page of results.
     */
    boolean hasNext();

    /**
     * Returns the total number of elements across all pages that can be requested for the query.
     * @return the total number of elements across all pages.
     */
    long totalElements();

    /**
     * Returns the total number of pages.
     * @return the total number of pages.
     */
    long totalPages();
}