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
 * <p>The {@code Update} annotation indicates that the annotated repository method requests that one or more entities
 * be updated if found in the database. This method must have a single parameter whose type must be one of the following:
 * </p>
 * <ul>
 *     <li>The entity to be updated.</li>
 *     <li>An {@code Iterable} of entities to be updated.</li>
 *     <li>An array of entities to be updated.</li>
 * </ul>
 * <p>The return type of the annotated method must be {@code void}, {@code boolean}, a numeric primitive type (such as {@code int}), or a corresponding primitive wrapper type (such as {@link Integer}). A boolean return type indicates whether a matching entity was found in the database to update. A numeric return type indicates how many matching entities were found in the database to update.
 * </p>
 * <p>Updating an entity involves modifying its existing data in the database. The method will search for the entity
 * in the database using its ID (and version, if versioned) and then update the corresponding record with the new data. After invoking
 * this method, do not continue to use the entity value that is supplied as a parameter, as it may not accurately
 * reflect the changes made during the update process.
 * </p>
 * <p>If the entity uses optimistic locking and its version differs from the version in the database,
 * an {@link jakarta.data.exceptions.OptimisticLockingFailureException} will be thrown.
 * </p>
 *
 * <p>For example, consider an interface representing a garage:</p>
 * <pre>
 * &#64;Repository
 * interface Garage {
 *     {@literal @}Update
 *     Car update(Car car);
 * }
 * </pre>
 * <p>If this annotation is combined with other operation annotations (e.g., {@code @Insert}, {@code @Delete},
 * {@code @Save}), it will throw an {@link UnsupportedOperationException} as only one operation type can be specified.</p>
 * @see jakarta.data.exceptions.OptimisticLockingFailureException
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Update {
}
