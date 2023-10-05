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
import jakarta.data.exceptions.OptimisticLockingFailureException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * <p>A repository interface for performing basic operations on entities.</p>
 *
 * <p>This repository provides methods to interact with persistent entities of type <code>&lt;T&gt;</code>,
 * where <code>&lt;T&gt;</code> represents the entity bean type, and <code>&lt;K&gt;</code> represents the key type.</p>
 *
 * @param <T> the entity bean type
 * @param <K> the key type.
 */
public interface BasicRepository<T, K> extends DataRepository<T, K> {

    /**
     * Saves a given entity to the database. If the entity has an ID or key that exists in the database,
     * the method will update the existing record. Otherwise, it will insert a new record.
     *
     * <p>If the entity has a non-null ID, the method will attempt to
     * update the existing record in the database. If the entity does not exist in the database or has a null ID,
     * then this method will insert a new record into the database.</p>
     *
     * <p>The entity instance that is returned as a result value of this method
     * must be updated with all automatically generated values and incremented values
     * that changed due to the save. After invoking this method, do not continue to use
     * the entity value that is supplied as a parameter. This method makes no guarantees
     * about the state of the entity value that is supplied as a parameter.</p>
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
    // TODO Jakarta Validation-related doc was found to be inconsistent with the Jakarta Validation spec
    //      so it is removed from the save methods for now. Pull #231 will be making corrections.
    <S extends T> S save(S entity);

    /**
     * Saves all given entities to the database. If an entity has a non-null ID that exists in the database,
     * the method will update the existing record. Otherwise, it will insert a new record.
     *
     * <p>If an entity has a non-null ID, this method will attempt to update the
     * existing record in the database. If an entity does not exist in the database
     * or has a null ID, then this method inserts a new record into the database.</p>
     *
     * <p>The entity instances that are returned as a result of this method
     * must be updated with all automatically generated values and incremented values
     * that changed due to the save. After invoking this method, do not continue to use
     * the entity values that are supplied in the parameter. This method makes no guarantees
     * about the state of the entity values that are supplied in the parameter.</p>
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
     * <p>Inserts an entity into the database. If an entity of this type with the same
     * unique identifier already exists in the database, then this method raises
     * {@link EntityExistsException}.</p>
     *
     * @param entity the entity to insert. Must not be {@code null}.
     * @throws EntityExistsException if the entity is already present in the database.
     * @throws NullPointerException if the entity is null.
     * @throws UnsupportedOperationException for Key-Value and Wide-Column databases
     *         that use an append model to write data.
     */
    void insert(T entity);

    /**
     * <p>Inserts multiple entities into the database. If an entity of this type with the same
     * unique identifier as any of the given entities already exists in the database,
     * then this method raises {@link EntityExistsException}.</p>
     *
     * @param entities entities to insert.
     * @throws EntityExistsException if any of the entities are already present in the database.
     * @throws NullPointerException if either the iterable is null or any element is null.
     * @throws UnsupportedOperationException for Key-Value and Wide-Column databases
     *         that use an append model to write data.
     */
    void insertAll(Iterable<T> entities);

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
     * @return true if a matching entity was found in the database to update, otherwise false.
     * @throws NullPointerException if the entity is null.
     */
    boolean update(T entity);

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
     * @return the number of matching entities that were found in the database to update.
     * @throws NullPointerException if either the iterable is null or any element is null.
     */
    int updateAll(Iterable<T> entities);

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
     * Retrieves all persistent entities of the specified type from the database.
     *
     * @return a stream of all entities; will never be {@literal null}.
     * @throws UnsupportedOperationException  for Key-Value and Wide-Column databases that are not capable
     * of the {@code findAll} operation.
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
    Stream<T> findByIdIn(Iterable<K> ids);

    /**
     * Retrieves the total number of persistent entities of the specified type in the database.
     *
     * @return the total number of entities.
     * @throws UnsupportedOperationException for Key-Value and Wide-Column databases that are not capable of the {@code count} operation.
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
    void deleteByIdIn(Iterable<K> ids);

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
     * Deletes all persistent entities managed by the repository.
     *
     * @throws UnsupportedOperationException for Key-Value and Wide-Column databases that are not capable of the {@code deleteAll} operation.
     */
    void deleteAll();

}
