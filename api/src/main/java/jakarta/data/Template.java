/*
 *   Copyright (c) 2022 Contributors to the Eclipse Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package jakarta.data;

import java.time.Duration;
import java.util.Optional;

/**
 * Templates are a helper class that increases productivity when performing common persistence layer operations.
 */
public interface Template {

    /**
     * Inserts entity
     *
     * @param entity entity to insert
     * @param <T>    the instance type
     * @return the entity saved
     * @throws NullPointerException when entity is null
     */
    <T> T insert(T entity);

    /**
     * Inserts entity with time to live
     *
     * @param entity entity to insert
     * @param ttl    the time to live
     * @param <T>    the instance type
     * @return the entity saved
     * @throws NullPointerException when entity is null
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
     * @throws NullPointerException when entities is null
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
     * Finds by Id.
     *
     * @param entityClass the entity class
     * @param id          the id value
     * @param <T>         the entity class type
     * @param <K>         the id type
     * @return the entity instance otherwise {@link Optional#empty()}
     * @throws NullPointerException when either the entityClass or id are null
     */
    <T, K> Optional<T> find(Class<T> entityClass, K id);

    /**
     * Deletes by Id.
     *
     * @param entityClass the entity class
     * @param id          the id value
     * @param <T>         the entity class type
     * @param <K>         the id type
     * @throws NullPointerException when either the entityClass or id are null
     */
    <T, K> void delete(Class<T> entityClass, K id);
}
