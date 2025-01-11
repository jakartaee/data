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
 * <p>Annotates a repository method returning entities as a parameter-based automatic query method.</p>
 *
 * <p>The {@code Find} annotation indicates that the annotated repository method executes a query to retrieve entities
 * based on its parameters and on the arguments assigned to its parameters. The method return type identifies the entity
 * type returned by the query. Each parameter of the annotated method must either:
 * </p>
 * <ul>
 * <li>have exactly the same type and name (the parameter name in the Java source, or a name assigned by {@link By @By})
 *     as an attribute of the entity class, or</li>
 * <li>be of type {@link jakarta.data.Limit}, {@link jakarta.data.Sort}, {@link jakarta.data.Order}, or
 *     {@link jakarta.data.page.PageRequest}.</li>
 * </ul>
 * <p>The query is inferred from the method parameters which match attributes of the entity.
 * </p>
 * <p>There is no specific naming convention for methods annotated with {@code @Find}; they may be named arbitrarily,
 * and their names do not carry any semantic meaning defined by the Jakarta Data specification.
 * </p>
 * <p>For example, consider an interface representing a garage:</p>
 * <pre>
 * &#64;Repository
 * interface Garage {
 *     &#64;Find
 *     List&lt;Car&gt; getCarsWithModel(@By("model") String model);
 * }
 * </pre>
 * <p>The {@code @Find} annotation indicates that the {@code getCarsWithModel(model)} method retrieves {@code Car}
 * instances with the given value of the {@code model} attribute.
 * </p>
 *
 * <p>A method annotated with {@code @Find} must return one of the following types:</p>
 * <ul>
 *     <li>an entity type {@code E}, when the method returns a single instance,</li>
 *     <li>{@code Optional<E>}, when the method returns at most a single instance,</li>
 *     <li>an entity array type {@code E[]},
 *     <li>{@code List<E>},</li>
 *     <li>{@code Stream<E>}, or</li>
 *     <li>{@code Page<E>} or {@code CursoredPage<E>}.</li>
 * </ul>
 *
 * <p>An automatic query method annotated {@code Find} returns an entity instance for every record which satisfies the
 * parameter-based conditions.</p>
 * <ul>
 * <li>If the return type of the annotated method is {@code E} or {@code Optional<E>} and more than one record satisfies
 *     the query conditions, the method must throw {@link jakarta.data.exceptions.NonUniqueResultException}.</li>
 * <li>If the return type of the annotated method is {@code E} and no record satisfies the query conditions, the method
 *     must throw {@link jakarta.data.exceptions.EmptyResultException}.</li>
 * </ul>
 *
 * <p>Annotations such as {@code @Find}, {@code @Query}, {@code @Insert}, {@code @Update}, {@code @Delete}, and
 * {@code @Save} are mutually-exclusive. A given method of a repository interface may have at most one {@code @Find}
 * annotation, lifecycle annotation, or query annotation.
 *
 * @see By
 * @see OrderBy
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Find {
}
