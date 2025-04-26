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
 * <p>Lifecycle annotation for repository methods which conditionally perform
 * insert or update operations.</p>
 *
 * <p>The {@code Save} annotation indicates that the annotated repository method
 * accepts one or more entities and, for
 * each entity, either adds its state to the database, or updates state already
 * held in the database.
 * </p>
 * <p>A {@code Save} method accepts an instance or instances of an entity class.
 * The method must have exactly one
 * parameter whose type is either:
 * </p>
 * <ul>
 *     <li>the class of the entity to be inserted or updated, or</li>
 *     <li>{@code List<E>} or {@code E[]} where {@code E} is the class of the entities to be inserted or updated.</li>
 * </ul>
 * <p>The annotated method must either be declared {@code void}, or have a return type that is the same as the type of
 * its parameter.
 * </p>
 * <p>All Jakarta Data providers are required to accept a {@code Save} method which conforms to this signature.
 * Application of the {@code Save} annotation to a method with any other signature is not portable between Jakarta Data
 * providers.
 * </p>
 * <p>For example, consider an interface representing a garage:</p>
 * <pre>
 * &#64;Repository
 * interface Garage {
 *     &#64;Save
 *     Car park(Car car);
 * }
 * </pre>
 * <p>The operation performed by the annotated method depends on whether the database already holds an entity with the
 * unique identifier of an entity passed as an argument:
 * </p>
 * <ul>
 * <li>If there is such an entity already held in the database, the annotated method must behave as if it were annotated
 *     {@link Update @Update}.
 * <li>Otherwise, if there is no such entity in the database, the annotated method must behave as if it were annotated
 *     {@link Insert @Insert}.
 * </ul>
 * <p>Annotations such as {@code @Find}, {@code @Query}, {@code @Insert}, {@code @Update}, {@code @Delete}, and
 * {@code @Save} are mutually-exclusive. A given method of a repository interface may have at most one {@code @Find}
 * annotation, lifecycle annotation, or query annotation.
 * </p>
 *
 * @see Insert
 * @see Update
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Save {
}
