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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.repository;

import java.util.List;

/**
 * A slice of data that indicates whether there's a next or previous slice available.
 */
public interface Slice<T>  {

    /**
     * Returns the page content as {@link List}.
     *
     * @return the page content as {@link List}.
     */
    List<T> getContent();

    /**
     * Returns whether the {@link Slice} has content at all.
     *
     * @return whether the {@link Slice} has content at all.
     */
    boolean hasContent();

    /**
     * Returns the number of elements currently on this Slice.
     *
     * @return the number of elements currently on this Slice.
     */
    int getNumberOfElements();

    /**
     * Returns the current {@link Pageable}
     *
     * @return the current Pageable
     */
    Pageable getPageable();

    /**
     * Returns the next {@link Pageable#next()}, or <code>null</code> if it is known that there is no next page.
     *
     * @return the next pageable
     */
  Pageable nextPageable();
}
