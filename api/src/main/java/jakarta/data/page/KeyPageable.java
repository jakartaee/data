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

public interface KeyPageable {

    /**
     * <p>Requests {@link KeysetAwareSlice keyset pagination} in the forward direction,
     * starting after the specified keyset values.</p>
     *
     * @param keyset keyset values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>Pageable</code> with forward keyset pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    KeyPageable afterKeyset(Object... keyset);

    /**
     * <p>Requests {@link KeysetAwareSlice keyset pagination} in the reverse direction,
     * starting after the specified keyset values.</p>
     *
     * @param keyset keyset values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>Pageable</code> with reverse keyset pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    KeyPageable beforeKeyset(Object... keyset);

    /**
     * <p>Requests {@link KeysetAwareSlice keyset pagination} in the forward direction,
     * starting after the specified keyset values.</p>
     *
     * @param keysetCursor cursor with keyset values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>Pageable</code> with forward keyset pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    KeyPageable afterKeysetCursor(Cursor keysetCursor);

    /**
     * <p>Requests {@link KeysetAwareSlice keyset pagination} in the reverse direction,
     * starting after the specified keyset values.</p>
     *
     * @param keysetCursor cursor with keyset values, the order and number of which must match the
     *        {@link OrderBy} annotations, {@link Sort} parameters, or
     *        <code>OrderBy</code> name pattern of the repository method to which
     *        this pagination will be supplied.
     * @return a new instance of <code>Pageable</code> with reverse keyset pagination.
     *         This method never returns <code>null</code>.
     * @throws IllegalArgumentException if no keyset values are provided.
     */
    KeyPageable beforeKeysetCursor(Cursor keysetCursor);


    /**
     * Returns the keyset values which are the starting point for
     * keyset pagination.
     *
     * @return the keyset values; <code>null</code> if using offset pagination.
     */
    Cursor cursor();
}
