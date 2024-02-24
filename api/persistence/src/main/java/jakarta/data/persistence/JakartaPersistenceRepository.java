/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package jakarta.data.persistence;

import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Find;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FindOption;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockOption;
import jakarta.persistence.RefreshOption;

/**
 * <p>A built-in repository supertype for repositories backed by a Jakarta Persistence
 * {@code EntityManager}.</p>
 *
 * @param <T> the type of the primary entity class of the repository.
 * @param <K> the type of the Id attribute of the primary entity.
 */
public interface JakartaPersistenceRepository <T, K> extends DataRepository<T, K> {

    @Find
    T find(K id);

    @Find
    T find(K id, FindOption... options);

    @Persist
    void persist(T entity);

    @Persist
    void persistAll(Iterable<T> entities);

    @Remove
    void remove(T entity);

    @Merge
    T merge(T entity);

    @Detach
    void detach(T entity);

    @Refresh
    void refresh(T entity);

    @Refresh
    void refresh(T entity, RefreshOption... options);

    @Lock
    void lock(T entity, LockModeType lockModeType);

    @Lock
    void lock(T entity, LockModeType lockModeType, LockOption... options);

    EntityManager entityManager();

}