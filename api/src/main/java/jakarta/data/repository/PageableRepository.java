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

/**
 * Repository fragment to provide methods to retrieve entities using the pagination and sorting abstraction. In many
 * cases this will be combined with {@link CrudRepository} or similar or with manually added methods to provide CRUD
 * functionality.
 * @param <T> the domain type the repository manages
 * @param <K> the type of the id of the entity the repository manages
 * @see CrudRepository
 */
public interface PageableRepository<T, K> extends CrudRepository<T, K> {

    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in the {@link Pageable} object.
     *
     * <p>The availability of specific paging modes (such as keyset or offset) may vary depending on the underlying database.</p>
     *
     * @param pageable the pageable to request a paginated result, must not be null.
     * @return a page of entities; will never be {@literal null}.
     * @throws NullPointerException when pageable is null
     * @throws UnsupportedOperationException  if the specified paging mode is not supported by the database.
     * @see Pageable.Mode
     */
    Page<T> findAll(Pageable pageable);

}
