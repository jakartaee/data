/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
package jakarta.data;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>The {@code Delete} annotation indicates to dynamic templates or repositories that the annotated method
 * will perform a delete operation. This method should have a unique parameter whose type can be one of the following:
 * </p>
 * <ul>
 *     <li>The entity to be deleted.</li>
 *     <li>An {@code Iterable} of entities to be deleted.</li>
 *     <li>An array of entities to be deleted.</li>
 * </ul>
 * <p>The return type of the annotated method should be {@code void}, as the delete operation does not return a value.
 * </p>
 * <p>Deletion of a given entity is performed by matching the entity's Id. If the entity is versioned (e.g.,
 * with {@code jakarta.persistence.Version}), the version is also checked for consistency during deletion.
 * Properties other than the Id and version do not need to match for deletion.
 * </p>
 *
 * @throws jakarta.data.exceptions.OptimisticLockingFailureException if the entity is not found in the database for deletion
 *         or has a version for optimistic locking that is inconsistent with the version in the database.
 * @throws NullPointerException when the entity is null.
 *
 * @see jakarta.data.exceptions.OptimisticLockingFailureException
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Delete {
}
