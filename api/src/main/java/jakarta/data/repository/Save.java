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
package jakarta.data.repository;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>The {@code Save} annotation indicates that the annotated repository method
 * updates one or more entities if found in the database
 * and inserts entities into the database that are not found.
 * This method must have a single parameter whose type must be one of the following:
 * </p>
 * <ul>
 *     <li>The entity to be saved.</li>
 *     <li>An {@code Iterable} of entities to be saved.</li>
 *     <li>An array of entities to be saved.</li>
 * </ul>
 * <p>The return type of the annotated method must be the same as the parameter type, ensuring consistency
 * with the saved entity or entities.
 * </p>
 * <p>Saving an entity involves persisting it in the database. If the entity has an ID or key that already exists
 * in the database, the method will update the existing record. If the entity does not exist in the database or has a
 * null ID, this method will insert a new record. The entity instance returned by this method will be updated with
 * any automatically generated or incremented values that changed due to the save operation.
 * </p>
 * <p>After invoking this method, avoid using the entity value that was supplied as a parameter, as it may not accurately
 * reflect the changes made during the save process. If the entity uses optimistic locking and its version differs from
 * the version in the database, an {@link jakarta.data.exceptions.OptimisticLockingFailureException} will be thrown.
 * </p>
 *
 * <p>For example, consider an interface representing a garage:</p>
 * <pre>
 * @Repository
 * interface Garage {
 *     {@literal @}Save
 *     Car park(Car car);
 * }
 * </pre>
 * <p>The {@code @Insert} annotation can be used to indicate that the {@code park(Car)} method is responsible for updating the entity in database if it already exists there and otherwise inserting
 * a car entity into a database.
 * </p>
 *
 * @see jakarta.data.exceptions.OptimisticLockingFailureException
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Save {
}
