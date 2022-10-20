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

/**
 * <p>A page of results from a repository query that performs
 * {@link KeysetPageable keyset pagination}.</p>
 *
 * TODO switch code to link once #37 is merged.
 * <p>The concept of <code>Slice</code> differs from <code>Page</code> in that slices
 * do not have awareness of a total number of pages or results. This fits well
 * with keyset pagination, which allows entities to be added and removed
 * in between traversal of slices or pages and makes estimates of a total
 * inaccurate.</p>
 */
public interface KeysetAwareSlice<T> /** TODO extends Slice<T> */ {
    /**
     * Returns a {@link KeysetPageable.Cursor Cursor} for keyset values at the
     * specified position.
     *
     * @param index position (0 is first) of a result on the page.
     * @return cursor for keyset values at the specified position.
     */
    KeysetPageable.Cursor getKeysetCursor(int index);

    /**
     * <p>Returns pagination information for requesting the next page
     * in a forward direction from the current page. This method computes a
     * keyset from the last entity of the current page, including that
     * keyset in the pagination information so that it can be used to
     * obtain results in a forward direction according to the sort criteria
     * and relative to that entity.</p>
     *
     * @return pagination information for requesting the next page.
     */
    // TODO @Override once Slice is added to the spec
    KeysetPageable nextPageable();

    /**
     * <p>Returns pagination information for requesting the previous page
     * in a reverse direction from the current page. This method computes a
     * keyset from the first entity of the current page, including that
     * keyset in the pagination information so that it can be used to
     * obtain results in a reverse direction to the sort criteria
     * and relative to that entity.</p>
     *
     * <p>Page numbers are not accurate and should not be relied upon when
     * using keyset pagination. Jakarta Data providers should aim to at least
     * avoid returning negative or <code>0</code> as page numbers when
     * traversing pages in the reverse direction (this might otherwise occur
     * when matching entities are added prior to the first page and the
     * previous page is requested) by assigning a page number of <code>1</code>
     * to such pages. This means that there can be multiple consecutive pages
     * numbered <code>1</code> and that
     * <code>currentPage.previousPageable().next().getPage()</code>
     * cannot be relied upon to return a page number that is equal to the
     * current page number.</p>
     *
     * @return pagination information for requesting the previous page.
     */
    // TODO @Override once Slice is added to the spec
    KeysetPageable previousPageable();
}