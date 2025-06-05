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

import java.lang.annotation.*;

/**
 * <p>Lifecycle annotation for repository methods of stateful repositories
 * which perform remove operations. The {@code Remove} annotation must
 * not be applied to a method of a stateless repository.
 * </p>
 * <p>The {@code Remove} annotation indicates that the annotated repository
 * method schedules one or more managed entities for deletion in the database.
 * Deletion might occur immediately, when the annotated repository method is
 * invoked, or it might occur later, when the persistence context is flushed.
 * </p>
 * <p>An {@code Remove} method accepts a managed instance or instances of an
 * entity class. The method must have exactly one parameter whose type is
 * either:
 * </p>
 * <ul>
 *     <li>the class of the entity to be inserted, or</li>
 *     <li>{@code List<E>} or {@code E[]} where {@code E} is the class of the
 *     entities to be inserted.</li>
 * </ul>
 * <p>The annotated method must be declared {@code void}.
 * </p>
 * <p>If an unmanaged entity is passed to a {@code Refresh} method, the method
 * must throw {@link IllegalArgumentException}. If the entity was scheduled for
 * insertion in the database via a call to a {@link Persist} method, but was
 * not yet inserted in the database, then the {@code Refresh} method undoes the
 * effect of the {@code Persist} method.
 * </p>
 * <p>Every Jakarta Data provider which supports stateful repositories is
 * required to accept a {@code Remove} method which conforms to this signature.
 * Application of the {@code Remove} annotation to a method with any other
 * signature is not portable between Jakarta Data providers. Furthermore,
 * support for stateful repositories is optional. A Jakarta Data provider
 * is not required to support stateful repositories.
 * </p>
 * <p>An event of type {@link jakarta.data.event.PreDeleteEvent} must be
 * raised before each record is inserted in the database. An event of type
 * {@link jakarta.data.event.PreDeleteEvent} must be raised after each record
 * is successfully inserted.
 * </p>
 * <p>Annotations such as {@code @Find}, {@code @Query}, {@code @Persist},
 * {@code @Merge}, {@code @Remove}, and {@code @Refresh} are mutually-exclusive.
 * A given method of a repository interface may have at most one {@code @Find}
 * annotation, lifecycle annotation, or query annotation.
 * </p>
 *
 * @since 1.1
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Remove {
}
