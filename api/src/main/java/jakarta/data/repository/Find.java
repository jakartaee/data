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
 * <p>The {@code Find} annotation indicates that the annotated repository method executes a query to retrieve entities based on specified parameters.
 * The method parameters must match the types and names of the corresponding fields of the entity being queried.
 * There is no specific naming convention for methods annotated with {@code @Find}; they may be named arbitrarily,
 * and their names do not carry any specific semantic meaning.
 * </p>
 * <p>For example, consider an interface representing a garage:</p>
 * <pre>
 * {@code @Repository}
 * interface Garage {
 *     {@code @Find}
 *     {@code List<Car>} getCarsWithModel(@By("model") String model);
 * }
 * </pre>
 * <p>The {@code @Find} annotation indicates that the {@code getCarsWithModel(model)} method is responsible for searching
 * a list of Car objects based on the provided model parameter in a database.
 * </p>
 *
 * <p>The return type of a method annotated with {@code @Find} must be one of the following:</p>
 * <ul>
 *     <li>{@code List}</li>
 *     <li>{@code Set}</li>
 *     <li>{@code Stream}</li>
 *     <li>{@code Iterable}</li>
 *     <li>{@code Collection}</li>
 *     <li>{@code Optional} (for single instances)</li>
 *     <li>The entity type itself (for single instances)</li>
 *     <li>Array of the entity type</li>
 * </ul>
 *
 * <p>If the annotated method returns a single instance (either through {@code Optional} or directly), but the query returns more than one element,
 * it will throw a {@link jakarta.data.exceptions.NonUniqueResultException}.</p>
 *
 * <p>If this annotation is combined with other operation annotations (e.g., {@code @Update}, {@code @Delete},
 *  {@code @Save}), it will result in an {@link UnsupportedOperationException} being thrown, as only one operation type can be specified.
 *  A Jakarta Data provider implementation must detect and report this error either at compile time or runtime.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Find {
}
