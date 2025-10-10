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
 * which perform remove operations. The {@code Remove} annotation must
 * not be applied to a method of a stateless repository.
 * </p>
 * <p>The {@code Remove} annotation indicates that the annotated repository
 * method schedules one or more managed entities for deletion from the database.
 * Deletion might occur immediately, when the annotated repository method is
 * invoked, or it might occur later, when the persistence context is flushed.
 * </p>
 * <p>A {@code Remove} method accepts a managed instance or instances of an
 * entity class. The method must have exactly one parameter whose type is
 * either:
 * </p>
 * <ul>
 *     <li>the class of the entity to be removed, or</li>
 *     <li>{@code List<E>} or {@code E[]} where {@code E} is the class of the
 *     entities to be removed.</li>
 * </ul>
 * <p>The annotated method must be declared {@code void}.
 * </p>
 * <p>If an unmanaged entity is passed to a {@code Remove} method, the method
 * must throw {@link IllegalArgumentException}. If the entity was scheduled for
 * insertion in the database via a call to a {@link Persist} method, but was
 * not yet inserted in the database, then the {@code Remove} method undoes the
 * effect of the {@code Persist} method.
 * </p>
 * <p>A Jakarta Data provider that is capable of returning
 * {@code jakarta.persistence.EntityManager} from a resource accessor method is
 * required to accept a {@code Remove} method which conforms to this signature.
 * A {@code Remove} method that is not accepted must raise
 * {@link UnsupportedOperationException} or be rejected at compile time.
 * Application of the {@code Remove} annotation to a method with any other
 * signature is not portable between Jakarta Data providers.
 * </p>
 * <p>A given method of a repository interface may have at most one
 * {@code @Find} annotation, {@code @Query} annotation, {@code @Remove}
 * annotation, or other lifecycle annotation.</p>
 *
 * @since 1.1
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Remove {
}