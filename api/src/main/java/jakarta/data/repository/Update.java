/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <p>Annotates a repository method to perform update operations.</p>
 *
 * <p>The {@code Update} annotation indicates that the annotated repository method requests that one or more entities
 * be updated if found in the database. To request updates to specific entity instances, the annotated
 * repository method must have a single parameter whose type must be one of the following:
 * </p>
 * <ul>
 *     <li>The entity to be updated.</li>
 *     <li>An {@code Iterable} of entities to be updated.</li>
 *     <li>An array of entities to be updated.</li>
 * </ul>
 * <p>The return type of the annotated method must be {@code void}, {@code boolean}, a numeric primitive type
 * (such as {@code int}), a corresponding primitive wrapper type (such as {@link Integer}), or the same type as the parameter.
 * </p>
 * <p>
 * A boolean return type indicates whether a matching entity was found in the database to update.
 * A numeric return type indicates how many matching entities were found in the database to update.
 * An entity return type indicates the updated entity if found in the database. If the entity is not found
 * in the database or has a non-matching version, then {@code null} is returned.
 * An {@code Iterable} or array return type includes all matching entities that are found in the database,
 * skipping over entities that are not present in the database or have a non-matching version.
 * For example, if the method is annotated with {@code @Update} and takes a parameter of type {@code Car car},
 * the return type can be {@code Car}.
 * Similarly, if the parameter is an {@code Iterable<Car>} or an array of {@code Car}, the return type can be
 * {@code Iterable<Car>}.
 * Entities that are returned by the annotated method must include all values that were
 * written to the database, including all automatically generated values, updated versions and incremented values
 * that changed due to the update. The order of entities within an {@code Iterable} or array return value
 * must correspond to the position of entities in the parameter based on the unique identifier of the entity,
 * leaving out those that did not match the unique identifier and version that is in the database.
 * </p>
 * <p>Updating an entity involves modifying its existing data in the database. The method will search for the entity
 * in the database using its ID (and version, if versioned) and then update the corresponding record with the new data. After invoking
 * this method, do not continue to use the entity value that is supplied as a parameter, as it may not accurately
 * reflect the changes made during the update process.
 * </p>
 * <p>If the entity does not exist in the database or it is versioned and its version differs from the version in the database,
 * no update is made and no error is raised.
 * </p>
 *<p>
 * In databases that use an append model to write data or follow the BASE model, this method
 * behaves the same as the {@code @Insert} method.
 *  </p>
 * <p>For example, consider an interface representing a garage:</p>
 * <pre>
 * {@code @Repository}
 * interface Garage {
 *     {@code @Update}
 *     Car update(Car car);
 * }
 * </pre>
 * <p>If this annotation is combined with other operation annotations (e.g., {@code @Insert}, {@code @Delete},
 * {@code @Save}), it will throw an {@link UnsupportedOperationException} because only one operation type can be specified.
 * A Jakarta Data provider implementation must detect (and report) this error at compile time or at runtime.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Update {
}
