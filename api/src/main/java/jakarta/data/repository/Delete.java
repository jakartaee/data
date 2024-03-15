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
 * <p>Lifecycle annotation for repository methods which perform delete operations.</p>
 *
 * <p>The {@code Delete} annotation indicates that the annotated repository method deletes the state of one or more
 * entities from the database. The method must follow one of the following patterns.
 * </p>
 *
 * <p>A {@code Delete} method might accept an instance or instances of an entity class. In this case, the method must
 * have exactly one parameter whose type is either:
 * </p>
 * <ul>
 *     <li>the class of the entity to be deleted, or</li>
 *     <li>{@code Iterable<E>} where {@code E} is the class of the entities to be deleted.</li>
 * </ul>
 * <p>The annotated method must be declared {@code void}.
 * </p>
 * <p>All Jakarta Data providers are required to accept a {@code Delete} method which conforms to this signature.
 * </p>
 * <p>For example, consider an interface representing a garage:</p>
 * <pre>
 * {@code @Repository}
 * interface Garage {
 *     {@code @Delete}
 *     void unpark(Car car);
 * }
 * </pre>
 * <p>Deletes are performed by matching the unique identifier of the entity. If the entity is versioned, for example,
 * with {@code jakarta.persistence.Version}, the version is also checked for consistency. Attributes other than the
 * identifier and version do not need to match. If no entity with a matching identifier is found in the database, or
 * if the entity with a matching identifier does not have a matching version, the annotated method must raise
 * {@link jakarta.data.exceptions.OptimisticLockingFailureException}.
 * </p>
 *
 * <p>Alternatively, as a special exception, the {@code Delete} annotation may be used to annotate a repository method
 * with no a parameter of entity type:</p>
 * <ul>
 * <li>{@code Delete} may be applied to a repository method with no parameters, indicating that the annotated method
 *     deletes all instances of the primary entity type. The annotated method must either be declared {@code void}, or
 *     return {@code int} or {@code long}.</li>
 * <li>{@code Delete} may be applied to a repository method with exactly one parameter annotated {@code By}, with a
 *     defaulted {@linkplain By#value field name}, and with the same type as the unique identifier field or property of
 *     the primary entity of the repository, indicating that the annotated method deletes the entity instance with the
 *     given unique identifier. The annotated method must be declared {@code void}.
 * </ul>
 * <p>Annotations such as {@code @Find}, {@code @Query}, {@code @Insert}, {@code @Update}, {@code @Delete}, and
 * {@code @Save} are mutually-exclusive. A given method of a repository interface may have at most one {@code @Find}
 * annotation, lifecycle annotation, or query annotation.
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Delete {
}
