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
package jakarta.data.orm;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Lifecycle annotation for repository methods of stateful repositories
 * which perform refresh operations. The {@code Refresh} annotation must
 * not be applied to a method of a stateless repository.
 * </p>
 * <p>The {@code Refresh} annotation indicates that the annotated repository
 * method immediately reloads the state of the given entity from the database,
 * overwriting the persistent state previously held by the entity.
 * </p>
 * <p>A {@code Refresh} method accepts a managed instance or instances of an
 * entity class. The method must have exactly one parameter whose type is
 * either:
 * </p>
 * <ul>
 *     <li>the class of the entity to be refreshed, or</li>
 *     <li>{@code List<E>} or {@code E[]} where {@code E} is the class of the
 *     entities to be refreshed.</li>
 * </ul>
 * <p>The annotated method must be declared {@code void}.
 * </p>
 * <p>If an unmanaged entity is passed to a {@code Refresh} method, the method
 * must throw {@link IllegalArgumentException}.
 * </p>
 * <p>Every Jakarta Data provider which supports stateful repositories is
 * required to accept a {@code Refresh} method which conforms to this signature.
 * Application of the {@code Refresh} annotation to a method with any other
 * signature is not portable between Jakarta Data providers. Furthermore,
 * support for stateful repositories is optional. A Jakarta Data provider
 * is not required to support stateful repositories.
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
@interface Refresh {
}