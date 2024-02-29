/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
 * <p>Annotates a repository method that performs entity search operations as Parameter-based automatic query methods.</p>
 *
 * <p>The {@code Find} annotation indicates that the annotated repository method executes a query to retrieve entities
 * based on specified parameters. The method parameters must match the types and names of the corresponding fields of
 * the entity being queried. There is no specific naming convention for methods annotated with {@code @Find}; they may
 * be named arbitrarily, and their names do not carry any specific semantic meaning. The method return type identifies
 * the entity type returned by the query.
 * </p>
 * <p>For example, consider an interface representing a garage:</p>
 * <pre>
 * {@code @Repository}
 * interface Garage {
 *     {@code @Find}
 *     {@code List<Car>} getCarsWithModel(@By("model") String model);
 * }
 * </pre>
 * <p>The {@code @Find} annotation indicates that the {@code getCarsWithModel(model)} method retrieves {@code Car}
 * instances with the given value of the {@code model} field.
 * </p>
 *
 * <p>A method annotated with {@code @Find} must return one of the following types:</p>
 * <ul>
 *     <li>an entity type {@code E}, when the method returns a single instance,</li>
 *     <li>{@code Optional<E>}, when the method returns at most a single instance,</li>
 *     <li>an entity array type {@code E[]},
 *     <li>{@code List<E>},</li>
 *     <li>{@code Stream<E>}, or</li>
 *     <li>{@code Page<E>}, {@code Slice<E>}, {@code KeysetAwarePage<E>}, or {@code KeysetAwareSlice<E>}.</li>
 * </ul>
 *
 * <p>If the return type of the annotated method is {@code E} or {@code Optional<E>}, and the query returns more than
 * one element when executed, the method must throw {@link jakarta.data.exceptions.NonUniqueResultException}.
 * </p>
 * <p>Annotations such as {@code @Find}, {@code @Query}, {@code @Insert}, {@code @Update}, {@code @Delete}, and
 * {@code @Save} are mutually-exclusive. A given method of a repository interface may have at most one {@code @Find}
 * annotation, lifecycle annotation, or query annotation.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Find {
}
