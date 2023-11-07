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

public interface Pageable extends Pagination {

    /**
     * <p>Returns the <code>Pageable</code> requesting the next page
     * if using offset pagination.</p>
     *
     * <p>If using keyset pagination, traversal of pages must only be done
     * via the {@link KeysetAwareSlice#nextPageable()},
     * {@link KeysetAwareSlice#previousPageable()}, or
     * {@link KeysetAwareSlice#getKeysetCursor(int) keyset cursor},
     * not with this method.</p>
     *
     * @return The next pageable.
     * @throws UnsupportedOperationException if this <code>Pageable</code> has a
     *         {@link Cursor Cursor}.
     */
    Pagination next();
}
