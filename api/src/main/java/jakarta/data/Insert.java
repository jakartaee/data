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
 * <p>The {@code Insert} annotation indicates that the annotated repository method requests that one or more entities
 * be inserted into the database. This method must have a single parameter whose type must be one of the following:
 * </p>
 * <ul>
 *     <li>The entity to be inserted.</li>
 *     <li>An {@code Iterable} of entities to be inserted.</li>
 *     <li>An array of entities to be inserted.</li>
 * </ul>
 * <p>The return type of the annotated method should match the type of the parameter. For example, if the method is
 * annotated with {@code @Insert} and takes a parameter of type {@code Car car}, the return type should be {@code Car}.
 * Similarly, if the parameter is an {@code Iterable<Car>} or an array of {@code Car}, the return type should be the
 * corresponding type.
 * </p>
 * <p>After invoking this method, it is recommended not to use the entity value supplied as a parameter, as this method
 * makes no guarantees about the state of the entity value after insertion.
 * </p>
 * <p>If the entity uses optimistic locking, and the version differs from the version in the database, an
 * {@link jakarta.data.exceptions.OptimisticLockingFailureException} may be thrown.
 * </p>
 * <p>For example, consider an interface representing a garage:</p>
 * <pre>
 * {@literal @}Repository
 * interface Garage {
 *     {@literal @}Insert
 *     Car parking(Car car);
 * }
 * </pre>
 * <p>The {@code @Insert} annotation can be used to indicate that the {@code parkCar} method is responsible for inserting
 * a car entity into a database.
 * </p>
 *
 * <p>If this annotation is combined with other operation annotations (e.g., {@code @Update}, {@code @Delete},
 *  {@code @Save}), it will throw an {@link IllegalStateException} as only one operation type can be specified.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Insert {
}
