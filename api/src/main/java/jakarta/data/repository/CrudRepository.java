/*
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
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

import jakarta.data.exceptions.EntityExistsException;

/**
 * <p>A repository interface that extends the capabilities of basic operations on entities, including insert and update operations.</p>
 *
 * <p>This repository extends the {@link BasicRepository} interface, providing a comprehensive set of methods to interact with
 * persistent entities of type {@code <T>}, where {@code <T>} represents the entity bean type, and {@code <K>} represents the key type.</p>
 *
 * <p>It encompasses standard CRUD (Create, Read, Update, Delete) operations, allowing you to perform insert and update operations in
 * addition to basic retrieval and deletion. This interface combines the Data Access Object (DAO) aspect with the repository pattern,
 * offering a versatile and complete solution for managing persistent entities within your Java applications.</p>
 *
 * @param <T> the entity bean type
 * @param <K> the key type.
 * @see BasicRepository
 * @see DataRepository
 */
public interface CrudRepository<T, K> extends BasicRepository<T, K> {

    /**
     * <p>Inserts an entity into the database. If an entity of this type with the same
     * unique identifier already exists in the database, then this method may raise
     * {@link EntityExistsException} for relational databases; however, the behavior
     * in NoSQL databases may vary depending on the provider, especially in cases where
     * the provider supports does not implement ACID or doesn't work with an append model.</p>
     *
     * @param entity the entity to insert. Must not be {@code null}.
     * @return the inserted entity.
     * @throws EntityExistsException if the entity is already present in the database (relational databases).
     * @throws NullPointerException if the entity is null.
     */
    T insert(T entity);

    /**
     * <p>Inserts multiple entities into the database. If an entity of this type with the same
     * unique identifier as any of the given entities already exists in the database,
     * then this method raises {@link EntityExistsException} for relational databases;
     * however, the behavior in NoSQL databases may vary depending on the provider,
     * especially in cases where the provider supports ACID transactions or doesn't work
     * with an append model.</p>
     *
     * @param entities entities to insert.
     * @return an iterable containing the inserted entities.
     * @throws EntityExistsException if any of the entities are already present in the
     *                              database (relational databases).
     * @throws NullPointerException if either the iterable is null or any element is null.
     */
    Iterable<T> insertAll(Iterable<T> entities);

    /**
     * <p>Modifies an entity that already exists in the database.</p>
     *
     * <p>For an update to be made, a matching entity with the same unique identifier
     * must be present in the database.</p>
     *
     * <p>If the entity is versioned (for example, with {@code jakarta.persistence.Version} or by
     * another convention from the entity model such as having an attribute named {@code version}),
     * then the version must also match. The version is automatically incremented when making
     * the update.</p>
     *
     * <p>Non-matching entities are ignored and do not cause an error to be raised.</p>
     *
     * @param entity the entity to update.
     * @return the updated entity.
     * @throws NullPointerException if the entity is null.
     */
    Iterable<T> update(T entity);

    /**
     * <p>Modifies entities that already exists in the database.</p>
     *
     * <p>For an update to be made to an entity, a matching entity with the same unique identifier
     * must be present in the database.</p>
     *
     * <p>If the entity is versioned (for example, with {@code jakarta.persistence.Version} or by
     * another convention from the entity model such as having an attribute named {@code version}),
     * then the version must also match. The version is automatically incremented when making
     * the update.</p>
     *
     * <p>Non-matching entities are ignored and do not cause an error to be raised.</p>
     *
     * @param entities entities to update.
     * @return the updated entities.
     * @throws NullPointerException if either the iterable is null or any element is null.
     */
    Iterable<T> updateAll(Iterable<T> entities);
}
