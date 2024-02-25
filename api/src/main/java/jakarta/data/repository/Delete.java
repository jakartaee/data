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
 * <p>The {@code Delete} annotation indicates that the annotated repository method the state of one or more entities
 * from in the database. The annotated repository method usually has exactly one parameter whose type must be one of
 * the following:
 * </p>
 * <ul>
 *     <li>The entity to be deleted.</li>
 *     <li>An {@code Iterable} of entities to be deleted.</li>
 *     <li>An array of entities to be deleted.</li>
 * </ul>
 * <p>The annotated method must be declared {@code void}.
 * </p>
 * <p>For example, consider an interface representing a garage:</p>
 * <pre>
 * {@code @Repository}
 * interface Garage {
 *     {@code @Delete}
 *     void unpark(Car car);
 * }
 * </pre>
 * <p>Alternatively, the {@code Delete} annotation may be applied to a repository method with no parameters, indicating
 * that the annotated method deletes all instances of the primary entity type. In this case, the annotated method must
 * either be declared {@code void}, or return {@code int} or {@code long}.
 * </p>
 * <p>Deletes are performed by matching the unique identifier of the entity. If the entity is versioned, for example,
 * with {@code jakarta.persistence.Version}, the version is also checked for consistency. Attributes other than the
 * identifier and version do not need to match. If no entity with a matching identifier is found in the database, or
 * if the entity with a matching identifier does not have a matching version, the annotated method must raise
 * {@link jakarta.data.exceptions.OptimisticLockingFailureException}.
 * </p>
 * <p>If this annotation occurs alongside a different lifecycle annotation, the annotated repository method must raise
 * {@link UnsupportedOperationException} every time it is called. Alternatively, a Jakarta Data provider is permitted to
 * reject such a method declaration at compile time.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Delete {
}
