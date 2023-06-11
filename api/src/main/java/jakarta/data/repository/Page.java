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
package jakarta.data.repository;

import jakarta.data.exceptions.FunctionalityNotSupportedException;

/**
 * <p>A page is a sublist of results. It provides information about its position relative to the entire list.</p>
 *
 * <p>Repository methods that are declared to return <code>Page</code> or
 * {@link KeysetAwarePage} must raise {@link FunctionalityNotSupportedException} if the
 * database is incapable of counting the total number of results across all pages,
 * in which case a return type of {@link Slice} or {@link KeysetAwareSlice}
 * should be used instead.</p>
 *
 * @param <T> the type of elements in this page 
 */
public interface Page<T> extends Slice<T> {

    /**
     * Returns the total amount of elements.
     * @return the total amount of elements.
     */
    long totalElements();

    /**
     * Returns the total number of pages.
     * @return the total number of pages.
     */
    long totalPages();
}