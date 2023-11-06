/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

/**
 * <p>Splits query results into slices or pages.</p>
 *
 * <p>For query results that are expected to be large, it can be useful to
 * read the results in separate parts instead of retrieving all of the results
 * from the database at once.</p>
 *
 * <p>To accomplish this, ensure that the results are sorted in a consistent
 * order. This is often done by having the final sort criterion be the
 * unique identifier, but it can be achieved by other means as long as the
 * order is guaranteed to be deterministic. Here is an example of sort criteria
 * to allow data to be read in slices or pages,</p>
 *
 * <pre>
 * Pageable.ofSize(25).sortBy(Sort.asc("lastName"),
 *                            Sort.asc("firstName"),
 *                            Sort.asc("id")); // unique identifier
 * </pre>
 *
 * <p>In the example above, even if multiple people have the same last names
 * and same first names, the results will always be in the same order due to
 * the unique identifier. The predictable order makes it possible to retrieve
 * the query results from the database in separate slices or pages.</p>
 *
 * <p>In Jakarta Data, a slice has most of the same functionality as a page,
 * but does not offer a total count of results across all slices. Using slice
 * rather than page is more efficient when a total count of results is not needed.</p>
 *
 * <p>Slices and pages can be determined based on fixed positional index
 * or relative to a cursor.</p>
 *
 * <p>{@link jakarta.data.page.Slice} and {@link jakarta.data.page.Page} are computed based on their positional index
 * within the list of results. For example, if you are on the first page of 10
 * and request the next page, the database identifies the matching entities
 * for the query at positions 11 through 20 and retrieves them. Results are
 * predictable if data is not modified in between slice/page requests.
 * If additional entities were inserted prior to requesting the second page
 * then some of the entities that are retrieved for the second page might have
 * also appeared on the first page. Similarly, if entities from the first page
 * were instead removed prior to requesting the second page, then the second page
 * will not include the entities that have now moved into positions 1 to 10.</p>
 *
 * <p>For situations where the above must be avoided or the application wishes to
 * avoid the performance cost of the database re-scanning data from previous pages
 * (to determine the position), Jakarta Data offers a cursor based approach with
 * {@link jakarta.data.page.KeysetAwareSlice} and {@link jakarta.data.page.KeysetAwarePage}. In this approach,
 * queries for the next or previous page are performed relative to the last
 * or first entry of the current page.</p>
 */
package jakarta.data.page;