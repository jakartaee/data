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


import java.time.Duration;
import java.util.Optional;

/**
 * Templates are a helper class that increases productivity when performing common database operations.
 * The Template feature in Jakarta Data simplifies the implementation of common database
 * operations by providing a basic API to the underlying persistence engine.
 * It follows the standard template pattern, a common design pattern used in software development.
 *
 * <p>
 * The Template pattern involves creating a skeletal structure for an algorithm, with some steps implemented and others left to be implemented by subclasses. Similarly, the
 * Template feature in Jakarta Data makes a skeleton around NoSQL database operations, allowing developers to focus on implementing the specific logic required for their
 * application.
 * </p>
 * <p>
 * Overall, the Template feature in Jakarta Data provides a simple and efficient way to implement common database operations while following established design patterns like
 * the Template Method. By using the Template feature, developers can save time and effort in implementing their NoSQL database operations, allowing them to focus on other
 * aspects of their application.
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
     * Inserts an entity
     *
     * @param entity entity to insert
     * @param <T>    the instance type
     * @return the entity saved
     * @throws NullPointerException when entity is null
     */
    <T> T insert(T entity);

    /**
     * Inserts entity with time to live, TTL.
     *
     * @param entity entity to insert
     * @param ttl    the time to live
     * @param <T>    the instance type
     * @return the entity saved
     * @throws NullPointerException          when entity or ttl is null
     * @throws UnsupportedOperationException when the database does not provide TTL
     */
    <T> T insert(T entity, Duration ttl);

    /**
     * Inserts entity, by default it's just run for each saving using
     * {@link Template#insert(Object)}},
     * each NoSQL vendor might replace to a more appropriate one.
     *
     * @param entities entities to insert
     * @param <T>      the instance type
     * @return the entity saved
     * @throws NullPointerException when entities is null
     */
    <T> Iterable<T> insert(Iterable<T> entities);

    /**
     * Inserts entities collection entity with time to live, by default it's just run for each saving using
     * {@link Template#insert(Object, Duration)},
     * each NoSQL vendor might replace to a more appropriate one.
     *
     * @param entities entities to be saved
     * @param <T>      the instance type
     * @param ttl      time to live
     * @return the entity saved
     * @throws NullPointerException          when entities is null
     * @throws UnsupportedOperationException when the database does not provide TTL
     */
    <T> Iterable<T> insert(Iterable<T> entities, Duration ttl);

    /**
     * Updates an entity
     *
     * @param entity entity to update
     * @param <T>    the instance type
     * @return the entity updated
     * @throws NullPointerException when entity is null
     */
    <T> T update(T entity);

    /**
     * Saves entity, by default it's just run for each saving using
     * {@link Template#update(Object)}},
     * each NoSQL vendor might replace to a more appropriate one.
     *
     * @param entities entities to update
     * @param <T>      the instance type
     * @return the entity saved
     * @throws NullPointerException when entities is null
     */
    <T> Iterable<T> update(Iterable<T> entities);

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