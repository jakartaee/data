/*
 * Copyright (c) 2022,2024 Contributors to the Eclipse Foundation
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

/**
 * <p>A built-in repository supertype for performing Create, Read, Update, and Delete (CRUD) operations.</p>
 *
 * <p>This repository extends the {@link BasicRepository} interface, adding {@linkplain #insert} and {@linkplain #update}
 * operations in addition to basic {@linkplain #findById retrieval} and {@linkplain #delete deletion}. This interface
 * combines the Data Access Object (DAO) aspect with the repository pattern, offering a versatile and complete solution
 * for managing persistent entities within Java applications.</p>
 *
 * <p>The type parameters of {@code CrudRepository<T,K>} capture the primary entity type ({@code T}) for the repository
 * and the type of the unique identifier attribute ({@code K}) of the primary entity type.</p>
 *
 * <p>The primary entity type is used for repository methods, such as {@code countBy...} and {@code deleteBy...}, which
 * do not explicitly specify an entity type.</p>
 *
 * <p>Example entity:</p>
 *
 * <pre>
 * {@code @Entity}
 * public class Cars {
 *     {@code @Id}
 *     public long vin;
 *     public String make;
 *     public String model;
 *     public int year;
 *     ...
 * }
 * </pre>
 *
 * <p>Example repository:</p>
 *
 * <pre>
 * {@code @Repository}
 * public interface Cars extends CrudRepository{@code <Car, Long>} {
 *
 *     List{@code <Car>} findByMakeAndModel(String make, String model, {@code Sort<?>...} sorts);
 *
 *     ...
 * }
 * </pre>
 *
 * <p>Example usage:</p>
 *
 * <pre>
 * {@code @Inject}
 * Cars cars;
 *
 * ...
 *
 * Car car1 = ...
 * car1 = cars.insert(car1);
 *
 * List{@code <Car>} found = findByMakeAndModel(car1.make,
 *                                      car1.model,
 *                                      Sort.desc("year"),
 *                                      Sort.asc("vin"));
 * </pre>
 *
 * <p>The module Javadoc provides an {@link jakarta.data/ overview} of Jakarta Data.</p>
 *
 * @param <T> the type of the primary entity class of the repository.
 * @param <K> the type of the unique identifier field of property of the primary entity.
 * @see BasicRepository
 * @see DataRepository
 */
public interface CrudRepository<T, K> extends BasicRepository<T, K> {

    /**
     * <p>Inserts an entity into the database. If an entity of this type with the same
     * unique identifier already exists in the database and the database supports ACID transactions,
     * then this method raises {@link EntityExistsException}. In databases that follow the BASE model
     * or use an append model to write data, this exception is not thrown.</p>
     *
     * <p>The entity instance returned as a result of this method must include all values that were
     * written to the database, including all automatically generated values and incremented values
     * that changed due to the insert. After invoking this method, do not continue to use the instance
     * that is supplied as a parameter. This method makes no guarantees about the state of the
     * instance that is supplied as a parameter.</p>
     *
     * @param entity the entity to insert. Must not be {@code null}.
     * @param <S> Type of the entity to insert.
     * @return the inserted entity, which may or may not be a different instance depending on whether the insert caused values to be generated or automatically incremented.
     * @throws EntityExistsException if the entity is already present in the database (in ACID-supported databases).
     * @throws NullPointerException if the entity is null.
     */
    @Insert 
    <S extends T> S insert(S entity);

    /**
     * <p>Inserts multiple entities into the database. If any entity of this type with the same
     * unique identifier as any of the given entities already exists in the database and the database
     * supports ACID transactions, then this method raises {@link EntityExistsException}.
     * In databases that follow the BASE model or use an append model to write data, this exception
     * is not thrown.</p>
     *
     * <p>The entities within the returned {@link Iterable} must include all values that were
     * written to the database, including all automatically generated values and incremented values
     * that changed due to the insert. After invoking this method, do not continue to use
     * the entity instances that are supplied in the parameter. This method makes no guarantees
     * about the state of the entity instances that are supplied in the parameter.
     * The position of entities within the {@code Iterable} return value must correspond to the
     * position of entities in the parameter based on the unique identifier of the entity.</p>
     *
     * @param entities entities to insert.
     * @param <S> Type of the entities to insert.
     * @return an iterable containing the inserted entities, which may or may not be different instances depending
     * on whether the insert caused values to be generated or automatically incremented.
     * @throws EntityExistsException if any of the entities are already present in the database (in ACID-supported databases).
     * @throws NullPointerException if the iterable is null or any element is null.
     */
    @Insert
    <S extends T> Iterable<S> insertAll(Iterable<S> entities);

    /**
     * <p>Modifies an entity that already exists in the database.</p>
     *
     * <p>For an update to be made, a matching entity with the same unique identifier
     * must be present in the database. In databases that use an append model to write data or
     * follow the BASE model, this method behaves the same as the {@link #insert} method.</p>
     *
     * <p>If the entity is versioned (for example, with {@code jakarta.persistence.Version} or by
     * another convention from the entity model such as having an attribute named {@code version}),
     * then the version must also match. The version is automatically incremented when making
     * the update.</p>
     *
     * @param entity the entity to update. Must not be {@code null}.
     * @param <S> Type of the entity to update.
     * @return an updated entity instance including all automatically generated values,
     *         updated versions, and incremented values which changed as a result of the update.
     * @throws OptimisticLockingFailureException the entity is not found in the database
     *         or has a version that differs from the version in the database.
     * @throws NullPointerException if the entity is null.
     */
    @Update
    <S extends T> S update(S entity);

    /**
     * <p>Modifies entities that already exist in the database.</p>
     *
     * <p>For an update to be made to an entity, a matching entity with the same unique identifier
     * must be present in the database. In databases that use an append model to write data or
     * follow the BASE model, this method behaves the same as the {@link #insertAll} method.</p>
     *
     * <p>If the entity is versioned (for example, with {@code jakarta.persistence.Version} or by
     * another convention from the entity model such as having an attribute named {@code version}),
     * then the version must also match. The version is automatically incremented when making
     * the update.</p>
     *
     * @param entities entities to update.
     * @param <S> Type of the entities to update.
     * @return updated entity instances, in the same order as the supplied entities,
     *         and including all automatically generated values, updated versions, and
     *         incremented values which changed as a result of the update.
     * @throws OptimisticLockingFailureException If any of the supplied entities is not found in the database
     *         or has a version that differs from the version in the database.
     * @throws NullPointerException if either the supplied {@code Iterable} is null or any element is null.
     */
    @Update
    <S extends T> Iterable<S> updateAll(Iterable<S> entities);

}
