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

import jakarta.data.DataException;

import java.util.Objects;
import java.util.ServiceLoader;

/**
 * Abstract interface for pagination information.
 */
public interface Pageable {

    long DEFAULT_SIZE = 10;
    Sort EMPTY_SORT = Sort.of();

    /**
     * Returns the offset to be taken according to the underlying page and page size.
     *
     * @return the offset to be taken according to the underlying page and page size.
     */
    long getOffset();

    /**
     * Returns the page to be returned.
     *
     * @return the page to be returned.
     */
    long getPageNumber();

    /**
     * Returns the Pageable requesting the next Page.
     *
     * @return The next pageable.
     */
    Pageable next();

    /**
     * Returns the previous Pageable or the first Pageable if the current one already is the first one.
     *
     * @return The previous pageable
     */
    Pageable previous();

    /**
     * Returns the sorting parameters.
     *
     * @return The sort definition to use.
     */
    Sort getSort();

    /**
     * Creates a new Pageable at the given offset with a default size of 10.
     *
     * @param page The page
     * @param <P>  page type
     * @return The pageable
     */
    static <P extends Pageable> P page(long page) {
        return of(page, DEFAULT_SIZE);
    }

    /**
     * Creates a new Pageable at the given offset and page
     *
     * @param page The page
     * @param size The size
     * @param <P>  page type
     * @return The pageable
     */
    static <P extends Pageable> P of(long page, long size) {
        return of(page, size, EMPTY_SORT);
    }

    /**
     * Creates a new Pageable at the given offset with a default size of 10.
     *
     * @param page The page
     * @param size The size
     * @param sort the sort
     * @param <P>  page type
     * @return The pageable
     */
    static <P extends Pageable> P of(long page, long size, Sort sort) {
        Objects.requireNonNull(sort, "sort is required");
        PageableSupplier<P> supplier =
                ServiceLoader.load(PageableSupplier.class)
                        .findFirst()
                        .orElseThrow(() -> new DataException("There is no implementation of PageableSupplier" +
                                " on the Class Loader"));
        return supplier.apply(page, size, sort);
    }


    /**
     * The {@link Pageable} supplier that the API will use on the method {@link Pageable#of(long, long, Sort)}
     *
     * @param <P> the {@link  Pageable}  implementation
     */
    interface PageableSupplier<P extends Pageable> {
        /**
         * Applies this function to the given argument.
         *
         * @param page the page
         * @param size the size
         * @param sort the sort
         * @return a {@link Pageable} instance
         * @throws NullPointerException when sort is null
         */
        P apply(long page, long size, Sort sort);
    }
}
