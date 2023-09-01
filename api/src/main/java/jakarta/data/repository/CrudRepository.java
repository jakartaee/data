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

import jakarta.data.exceptions.OptimisticLockingFailureException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * <p>A repository interface for performing CRUD (Create, Read, Update, Delete).</p>
 * @param <T> the bean type
 * @param <K> the key type
 */
public interface CrudRepository<T, K> extends DataRepository<T, K> {

    /**
     * Saves a given entity to the database. If the entity has an ID or key that exists in the database,
     * the method will update the existing record. Otherwise, it will insert a new record.
     *
     * <p>If the entity is already persistent (has a non-null ID or key), the method will attempt to
     * update the existing record in the database. If the entity is not persistent (has a null ID or key),
     * the method will insert a new record into the database.</p>
     *
     * <p>If the entity is in violation of any validation constraints, a {@code jakarta.validation.ConstraintViolationException}
     * will be raised before saving the entity to the database, provided a Jakarta Validation provider is present.</p>
     *
     * <p>If the entity uses optimistic locking and the version differs from the version in the database,
     * an {@link OptimisticLockingFailureException} will be thrown.</p>
     *
     * @param entity The entity to be saved. Must not be {@literal null}.
     * @param <S> Type of the entity to save.
     * @return The saved entity; never {@literal null}.
     * @throws OptimisticLockingFailureException If the entity uses optimistic locking and the version in the
     *         database differs from the version in the entity.
     * @throws NullPointerException If the provided entity is {@literal null}.
     */
    <S extends T> S save(S entity);

    /**
     * Saves all given entities.
     *
     * <p>This method raises {@code jakarta.validation.ConstraintViolationException} prior to saving the entities to the database
     * if a Jakarta Validation provider is present and any of the entities are in violation of one or more validation constraints.</p>
     *
     * @param entities An iterable of entities.
     * @param <S> Type of entity to save.
     * @return The saved entities; will never be {@literal null}.
     * @throws OptimisticLockingFailureException If an entity has a version for optimistic locking
     *         that differs from the version in the database.
     * @throws NullPointerException If either the iterable is null or any element is null.
     */
    <S extends T> Iterable<S> saveAll(Iterable<S> entities);

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given id or {@literal Optional#empty()} if none found.
     * @throws NullPointerException when the id is null
     */
    Optional<T> findById(K id);

    /**
     * Returns whether an entity with the given id exists.
     *
     * @param id must not be {@literal null}.
     * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
     * @throws NullPointerException when the ID is null
     */
    boolean existsById(K id);

    /**
     * Returns all instances of the type.
     *
     * @return all entities; will never be {@literal null}.
     */
    Stream<T> findAll();

    /**
     * Returns all instances of the type {@code T} with the given IDs.
     * <p>
     * If some or all ids are not found, no entities are returned for these IDs.
     * <p>
     * Note that the order of elements in the result is not guaranteed.
     *
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     * @return guaranteed to be not {@literal null}. The size can be equal or less than the number of given
     * {@literal ids}.
     * @throws NullPointerException in case the given {@link Iterable ids} or one of its items is {@literal null}.
     */
    Stream<T> findAllById(Iterable<K> ids);

    /**
     * Returns the number of entities available.
     *
     * @return the number of entities.
     */
    long count();

    /**
     * Deletes the entity with the given id.
     * <p>
     * If the entity is not found in the persistence store it is silently ignored.
     *
     * @param id must not be {@literal null}.
     * @throws NullPointerException when the id is null
     */
    void deleteById(K id);

    /**
     * Deletes a given entity. Deletion is performed by matching the Id, and if the entity is
     * versioned (for example, with {@code jakarta.persistence.Version}), then also the version.
     * Other properties of the entity do not need to match.
     *
     * @param entity must not be {@literal null}.
     * @throws OptimisticLockingFailureException if the entity is not found in the database for deletion
     *         or has a version for optimistic locking that is inconsistent with the version in the database.
     * @throws NullPointerException when the entity is null
     */
    void delete(T entity);

    /**
     * Deletes all instances of the type {@code T} with the given IDs.
     * <p>
     * Entities that aren't found in the persistence store are silently ignored.
     *
     * @param ids must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws NullPointerException when either the iterable is null or contains null elements
     */
    void deleteAllById(Iterable<K> ids);

    /**
     * Deletes the given entities. Deletion of each entity is performed by matching the ID, and if the entity is
     * versioned (for example, with {@code jakarta.persistence.Version}), then also the version.
     * Other properties of the entity do not need to match.
     *
     * @param entities Must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws OptimisticLockingFailureException If an entity is not found in the database for deletion
     *         or has a version for optimistic locking that is inconsistent with the version in the database.
     * @throws NullPointerException If either the iterable is null or contains null elements.
     */
    void deleteAll(Iterable<? extends T> entities);

    /**
     * Deletes all entities managed by the repository.
     */
    void deleteAll();

}
