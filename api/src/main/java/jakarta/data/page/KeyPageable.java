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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.page;


import jakarta.data.Sort;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;

public interface KeyPageable extends Pagination {


    /**
     * <p>Creates a new <code>Pageable</code> instance representing the same
     * pagination information, except with the specified page number.</p>
     *
     * @param pageNumber The page number
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    KeyPageable page(long pageNumber);

    /**
     * <p>Creates a new <code>Pageable</code> instance representing the same
     * pagination information, except with the specified maximum page size.</p>
     *
     * @param maxPageSize the number of query results in a full page.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    KeyPageable size(int maxPageSize);


    /**
     * Returns the keyset values which are the starting point for
     * keyset pagination.
     *
     * @return the keyset values; <code>null</code> if using offset pagination.
     */
    Cursor cursor();

    /**
     * <p>Creates a new <code>KeyPageable</code> instance representing the same
     * pagination information, except using the specified sort criteria.
     * The order of precedence for sort criteria is that of any statically
     * specified sort criteria (from the <code>OrderBy</code> keyword,
     * {@link OrderBy} annotation or <code>ORDER BY</code> clause of a the
     * {@link Query} annotation) followed by the order in which the
     * {@link Sort} parameters to this method are listed.</p>
     *
     * @param sorts sort criteria to use. This method can be invoked without parameters
     *        to request a <code>Pageable</code> that does not specify sort criteria.
     * @return a new instance of <code>Pageable</code>. This method never returns <code>null</code>.
     */
    KeyPageable sortBy(Sort... sorts);
}
