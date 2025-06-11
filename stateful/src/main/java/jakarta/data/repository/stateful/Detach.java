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
 * which perform detach operations. The {@code Detach} annotation must
 * not be applied to a method of a stateless repository.
 * </p>
 * <p>The {@code Detach} annotation indicates that the annotated repository
 * method immediately makes one or more entities unmanaged, removing them
 * from the current persistence context associated with the repository.
 * </p>
 * <p>A {@code Detach} method accepts an instance or instances of an entity
 * class. The method must have exactly one parameter whose type is either:
 * </p>
 * <ul>
 *     <li>the class of the entity to be detached, or</li>
 *     <li>{@code List<E>} or {@code E[]} where {@code E} is the class of the
 *     entities to be detached.</li>
 * </ul>
 * <p>The annotated method must be declared {@code void}.
 * </p>
 * <p>If an unmanaged entity is passed to a {@code Detach} method, the call
 * has no effect.
 * </p>
 * <p>A given method of a repository interface may have at most one
 * {@code @Find} annotation, {@code @Query} annotation, {@code @Detach}
 * annotation, or other lifecycle annotation.</p>
 *
 * @since 1.1
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Detach {
}