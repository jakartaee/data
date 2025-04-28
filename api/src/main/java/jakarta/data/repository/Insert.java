/*
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
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
 * <p>Lifecycle annotation for repository methods which perform insert
 * operations.</p>
 *
 * <p>The {@code Insert} annotation indicates that the annotated repository
 * method adds the state of one or more
 * entities to the database.
 * </p>
 * <p>An {@code Insert} method accepts an instance or instances of an entity
 * class. The method must have exactly one
 * parameter whose type is either:
 * </p>
 * <ul>
 *     <li>the class of the entity to be inserted, or</li>
 *     <li>{@code List<E>} or {@code E[]} where {@code E} is the class of the entities to be inserted.</li>
 * </ul>
 * <p>The annotated method must either be declared {@code void}, or have a return type that is the same as the type of
 * its parameter.
 * </p>
 * <p>All Jakarta Data providers are required to accept an {@code Insert} method which conforms to this signature.
 * Application of the {@code Insert} annotation to a method with any other signature is not portable between Jakarta
 * Data providers.
 * </p>
 * <p>For example, if the method is annotated with {@code @Insert} and takes a parameter of type {@code Car car}, the
 * return type can be {@code Car}. Similarly, if the parameter is of type {@code Iterable<Car>}, the return type can be
 * {@code Iterable<Car>}. Consider an interface representing a garage:</p>
 * <pre>
 * &#64;Repository
 * interface Garage {
 *     &#64;Insert
 *     Car park(Car car);
 * }
 * </pre>
 * <p>When the annotated method is non-{@code void}, it must return an inserted entity instance for each entity instance
 * passed as an argument. Instances returned by the annotated method must include all values that were written to the
 * database, including all automatically generated identifiers, initial versions, and other values which changed as a
 * result of the insert. The order of entities within an {@code Iterable} or array return value must match the position
 * of entities in the argument. After the annotated method returns, an original entity instance supplied as an argument
 * might not accurately reflect the inserted state.
 * </p>
 * <p>If an entity of the given type, and with the same unique identifier already exists in the database when the
 * annotated method is called, and if the databases uses ACID (atomic, consistent, isolated, durable) transactions,
 * then the annotated method must raise {@link jakarta.data.exceptions.EntityExistsException}.
 * If the database follows the BASE model, or uses an append model to write data, this exception is not thrown.
 * </p>
 * <p>
 * An event of type {@link jakarta.data.event.PreInsertEvent} must be raised by the annotated lifecycle
 * method before each record is inserted. An event of type {@link jakarta.data.event.PostInsertEvent}
 * must be raised by the annotated lifecycle method after each record is successfully inserted.
 * </p>
 * <p>Annotations such as {@code @Find}, {@code @Query}, {@code @Insert}, {@code @Update}, {@code @Delete}, and
 * {@code @Save} are mutually-exclusive. A given method of a repository interface may have at most one {@code @Find}
 * annotation, lifecycle annotation, or query annotation.
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Insert {
}
