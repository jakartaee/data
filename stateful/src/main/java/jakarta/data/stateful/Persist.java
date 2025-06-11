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
package jakarta.data.stateful;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Lifecycle annotation for repository methods of stateful repositories
 * which perform persist operations. The {@code Persist} annotation must
 * not be applied to a method of a stateless repository.
 * </p>
 * <p>The {@code Persist} annotation indicates that the annotated repository
 * method makes one or more entities managed, adding them to the current
 * persistence context associated with the repository, and schedules the
 * entities for insertion in the database. Insertion might occur immediately,
 * when the annotated repository method is invoked, or it might occur later,
 * when the persistence context is flushed.
 * </p>
 * <p>An {@code Persist} method accepts an instance or instances of an entity
 * class. The method must have exactly one parameter whose type is either:
 * </p>
 * <ul>
 *     <li>the class of the entity to be persisted, or</li>
 *     <li>{@code List<E>} or {@code E[]} where {@code E} is the class of the
 *     entities to be persisted.</li>
 * </ul>
 * <p>The annotated method must be declared {@code void}.
 * </p>
 * <p>If an instance passed to the {@code Persist} method is already managed,
 * the call has no effect unless the instance was previously scheduled for
 * deletion via a call to a {@link Remove} method. If the instance was
 * scheduled for deletion, then the {@code Persist} method undoes the effect
 * of the {@code Remove} method.
 * </p>
 * <p>Every Jakarta Data provider which supports stateful repositories is
 * required to accept a {@code Persist} method which conforms to this signature.
 * Application of the {@code Persist} annotation to a method with any other
 * signature is not portable between Jakarta Data providers. Furthermore,
 * support for stateful repositories is optional. A Jakarta Data provider
 * is not required to support stateful repositories.
 * </p>
 * <p>An event of type {@link jakarta.data.event.PreInsertEvent} must be
 * raised before each record is inserted in the database. An event of type
 * {@link jakarta.data.event.PostInsertEvent} must be raised after each record
 * is successfully inserted.
 * </p>
 * <p>A given method of a repository interface may have at most one
 * {@code @Find} annotation, {@code @Query} annotation, {@code @Persist}
 * annotation, or other lifecycle annotation.</p>
 *
 * @since 1.1
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Persist {
}