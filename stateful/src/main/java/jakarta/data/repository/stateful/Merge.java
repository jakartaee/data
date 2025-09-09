/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.repository.stateful;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Lifecycle annotation for repository methods of stateful repositories
 * which perform merge operations. The {@code Merge} annotation must
 * not be applied to a method of a stateless repository.
 * </p>
 * <p>The {@code Merge} annotation indicates that the annotated repository
 * method copies the state of one of more unmanaged entities to managed
 * entities belonging to the current persistence context associated with the
 * repository. The merge might change the persistent state of the managed
 * entities and might cause that state to be inserted or updated in the
 * database. This typically occurs later, when the persistence context is
 * flushed.
 * </p>
 * <p>A {@code Merge} method accepts an instance or instances of an entity
 * class. The method must have exactly one parameter whose type is either:
 * </p>
 * <ul>
 *     <li>the class of the entity to be merged, or</li>
 *     <li>{@code List<E>} or {@code E[]} where {@code E} is the class of the
 *     entities to be merged.</li>
 * </ul>
 * <p>The annotated method must have a return type that is the same as the
 * type of its parameter.
 * </p>
 * <p>If an instance passed to the method is already managed, the call has no
 * effect, and the method just returns the argument entity. Otherwise, if the
 * instance is unmanaged, the method returns a managed entity instance which
 * must be distinct from the argument entity, but which holds the same
 * persistent state.
 * </p>
 * <p>If a managed entity which was already scheduled for deletion from the
 * database via a {@code @Remove} method is subsequently passed to a method
 * annotated {@code Merge}, the second method is permitted to throw
 * {@link IllegalArgumentException}.
 * </p>
 * <p>The effect of merging an unmanaged entity depends on whether the database
 * already holds a corresponding record with the same persistent identity as
 * the merged entity.
 * <ul>
 *     <li>If such a record exists, the merge operation schedules an update
 *     of the existing record. The record is eventually updated to reflect the
 *     merged changes.</li>
 *     <li>Otherwise, if no such record exists, the merge operation schedules
 *     the insertion of a new record with the state held by the merged entity.
 * </ul>
 * <p>
 * In either case, the method annotated {@code @Merge} returns a managed entity
 * representing the inserted or updated record and reflecting the merged changes,
 * even if the scheduled insertion or update has not yet occurred.
 * </p>
 * <p>A Jakarta Data provider that is capable of returning
 * {@code jakarta.persistence.EntityManager} from a resource accessor method is
 * required to accept a {@code Merge} method which conforms to this signature.
 * A {@code Merge} method that is not accepted must raise
 * {@link UnsupportedOperationException} or be rejected at compile time.
 * Application of the {@code Merge} annotation to a method with any other
 * signature is not portable between Jakarta Data providers.
 * </p>
 * <p>A given method of a repository interface may have at most one
 * {@code @Find} annotation, {@code @Query} annotation, {@code @Merge}
 * annotation, or other lifecycle annotation.</p>
 *
 * @since 1.1
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Merge {
}