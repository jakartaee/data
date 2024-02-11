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
package jakarta.data.dao;


import jakarta.data.exceptions.EntityExistsException;

import java.time.Duration;
import java.util.Optional;

/**
 * Templates are a helper class that increases productivity when performing common database operations.
 * The Template feature in Jakarta Data simplifies the implementation of common database
 * operations by providing a basic API to the underlying persistence engine, achieving the Data Access Object (DAO) pattern.
 * It provides a standard set of methods for performing CRUD (Create, Read, Update, Delete) operations on entities.
 *
 * <p>
 * The DAO pattern involves encapsulating the logic for interacting with the database within dedicated DAO classes. Similarly, the
 * Template feature in Jakarta Data provides a standardized approach for performing database operations, allowing developers to focus on implementing the specific logic required for their
 * application.
 * </p>
 * <p>
 * By using the Template feature, developers can save time and effort in implementing their database operations, allowing them to focus on other
 * aspects of their application.
 * </p>
 *
 * <p>
 * Additionally, the Jakarta Data provider may extend this Template class to include specific behavior tailored to a particular database, providing flexibility and customization options.
 * </p>
 *
 * <pre>{@code
 * @Inject
 * Template template;
 *
 * Book book = Book.builder()
 *         .id(id)
 *         .title("Java Concurrency in Practice")
 *         .author("Brian Goetz")
 *         .year(Year.of(2006))
 *         .edition(1)
 *         .build();
 *
 * template.insert(book);
 *
 * Optional<Book> optional = template.find(Book.class, id);
 *
 * System.out.println("The result " + optional);
 *
 * template.delete(Book.class,id);
 * }</pre>
 * <p>
 * Furthermore, in CRUD operations, Template provides a fluent-API for either select or delete entities. Thus, Template offers the capability for search and remove beyond the ID
 * attribute. Take a look at {@link QueryMapper} for more detail about the provided fluent-API.
 * </p>
 * <pre>{@code
 * @Inject
 * Template template;
 *
 * List<Book> books = template.select(Book.class)
 *         .where("author")
 *         .eq("Joshua Bloch")
 *         .and("edition")
 *         .gt(3)
 *         .results();
 *
 * Stream<Book> books = template.select(Book.class)
 *         .where("author")
 *         .eq("Joshua Bloch")
 *         .stream();
 *
 * Optional<Book> optional = template.select(Book.class)
 *         .where("title")
 *         .eq("Java Concurrency in Practice")
 *         .and("author")
 *         .eq("Brian Goetz")
 *         .and("year")
 *         .eq(Year.of(2006))
 *         .singleResult();
 *
 * template.delete(Book.class)
 *         .where("author")
 *         .eq("Joshua Bloch")
 *         .and("edition")
 *         .gt(3)
 *         .execute();
 *
 * }</pre>
 *
 * @see QueryMapper
 */
public interface Template {

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
     * @return the inserted entity, which may or may not be a different instance depending on whether the insert caused values to be generated or automatically incremented.
     * @throws EntityExistsException if the entity is already present in the database (in ACID-supported databases).
     * @throws NullPointerException if the entity is null.
     */
    <T> T insert(T entity);


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
     * @return an iterable containing the inserted entities, which may or may not be different instances depending
     * on whether the insert caused values to be generated or automatically incremented.
     * @throws EntityExistsException if any of the entities are already present in the database (in ACID-supported databases).
     * @throws NullPointerException if the iterable is null or any element is null.
     */
    <T> Iterable<T> insertAll(Iterable<T> entities);

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
     * <p>Non-matching entities are ignored and do not cause an error to be raised.</p>
     *
     * @param entity the entity to update. Must not be {@code null}.
     * @return true if a matching entity was found in the database to update, otherwise false.
     * @throws NullPointerException if the entity is null.
     */
    <T> boolean update(T entity);

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
     * <p>Non-matching entities are ignored and do not cause an error to be raised.</p>
     *
     * @param entities entities to update.
     * @return the number of matching entities that were found in the database to update.
     * @throws NullPointerException if either the iterable is null or any element is null.
     */
    <T> int updateAll(Iterable<T> entities);

    /**
     * Finds by ID or key.
     *
     * @param type the entity class
     * @param id   the id value
     * @param <T>  the entity class type
     * @param <K>  the id type
     * @return the entity instance otherwise {@link Optional#empty()}
     * @throws NullPointerException when either the type or id are null
     */
    <T, K> Optional<T> find(Class<T> type, K id);

    /**
     * Deletes by ID or key.
     *
     * @param type the entity class
     * @param id   the id value
     * @param <T>  the entity class type
     * @param <K>  the id type
     * @throws NullPointerException when either the type or id are null
     */
    <T, K> void delete(Class<T> type, K id);

    /**
     * It starts a query using the fluent-API journey. It is a mutable and non-thread-safe instance.
     *
     * @param type the entity class
     * @param <T>  the entity type
     * @return a {@link QueryMapper.MapperFrom} instance
     * @throws NullPointerException          when type is null
     * @throws UnsupportedOperationException when the database cannot operate,
     *                                       such as key-value where most operations are key-based.
     */
    <T> QueryMapper.MapperFrom select(Class<T> type);

    /**
     * It starts a query builder using the fluent-API journey. It is a mutable and non-thread-safe instance.
     *
     * @param type the entity class
     * @param <T>  the entity type
     * @return a {@link QueryMapper.MapperDeleteFrom} instance
     * @throws NullPointerException          when type is null
     * @throws UnsupportedOperationException when the database cannot operate,
     *                                       such as key-value where most operations are key-based.
     */
    <T> QueryMapper.MapperDeleteFrom delete(Class<T> type);
}