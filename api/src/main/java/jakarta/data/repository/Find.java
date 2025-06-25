/*
 * Copyright (c) 2024,2025 Contributors to the Eclipse Foundation
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

import jakarta.data.Limit;
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.constraint.Constraint;
import jakarta.data.page.PageRequest;
import jakarta.data.restrict.Restriction;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotates a repository method as a parameter-based automatic query method
 * that returns entity instances or entity attribute values.</p>
 *
 * <p>The {@code Find} annotation indicates that the annotated repository
 * method executes a query against a certain entity type, filtering records
 * based on its parameters and the arguments assigned to its parameters.</p>
 *
 * <ul>
 * <li>If the method return type involves an entity type, according to the rules
 *     specified below, the return type determines the queried entity type.</li>
 * <li>Otherwise, if the method return type does not involve an entity type,
 *     the queried entity type is the {@linkplain #value entity class explicitly
 *     specified by the annotation}, if any, or the primary entity type of the
 *     repository.</li>
 * </ul>
 *
 * <p>The return type of the annotated repository method must involve either:</p>
 * <ul>
 * <li>the queried entity type itself,</li>
 * <li>the type of the attribute of the queried entity type specified by the
 *     {@link Select} annotation, or</li>
 * <li>a Java record type packaging the entity attributes returned by the
 *     query. The names and types of the record components must match the
 *     names and types of attributes of the queried entity, or the attribute
 *     names must be explicitly specified by the {@link Select} annotation.</li>
 * </ul>
 * <p>When the return type is not the queried entity type, every returned
 * attribute must be a basic attribute.</p>
 *
 * <p>Each parameter of the annotated method must either:</p>
 * <ul>
 * <li>have exactly the same type and name (the parameter name in the Java source,
 *     or a name assigned by {@link By @By}) as an attribute of the entity class,
 *     or</li>
 * <li>have exactly the same name as a persistent attribute of the entity class
 *     and be of type {@code C<T>}, where {@code C} is {@link Constraint} or any
 *     interface which extends {@link Constraint} and {@code T} is the type of
 *     the persistent attribute,
 * <li>have exactly the same name as a persistent attribute of the entity class,
 *     be annotated {@link Is @Is(C.class)}, where {@code C} is an interface
 *     which extends {@link Constraint}, and be of the same type
 *     as the parameter of a unary static method of {@code C} returning
 *     {@code C<T>} where {@code T} is the type of the persistent attribute, or
 * <li>be of type {@link Limit}, {@link Restriction}, {@link Sort}, {@link Order},
 *     or {@link PageRequest}.</li>
 * </ul>
 * <p>The query is inferred from the method parameters which match attributes of
 * the entity.</p>
 *
 * <p>There is no specific naming convention for methods annotated with {@code @Find};
 * they may be named arbitrarily, and their names do not carry any semantic meaning
 * defined by the Jakarta Data specification.</p>
 *
 * <p>For example, consider an interface representing a garage:</p>
 * <pre>
 * &#64;Repository
 * interface Garage {
 *     &#64;Find
 *     List&lt;Car&gt; getCarsWithModel(@By("model") String model);
 * }
 * </pre>
 * <p>The {@code @Find} annotation indicates that the {@code getCarsWithModel(model)}
 * method retrieves {@code Car} instances with the given value of the {@code model}
 * attribute.</p>
 *
 * <p>A method annotated with {@code @Find} must return one of the following types,
 * where {@code E} is the type returned by the query, either the queried entity type
 * inferred or {@linkplain #value explicitly specified}, the type of the attribute of
 * the entity specified using {@link Select#value @Select}, or a record type:</p>
 * <ul>
 *     <li>{@code E}, when the method returns a single instance</li>
 *     <li>{@code Optional<E>}, when the method returns at most a single instance,</li>
 *     <li>an array type {@code E[]},
 *     <li>{@code List<E>},</li>
 *     <li>{@code Stream<E>},</li>
 *     <li>{@code Page<E>}, or</li>
 *     <li>{@code CursoredPage<E>} (only allowed when {@code E} is the entity type).</li>
 * </ul>
 * <p>The number of query results may be limited using the {@link First} annotation.</p>
 *
 * <p>For example, if a {@code Car} entity has attribute names including {@code make},
 * {@code model}, {@code year}, and {@code vin}, a repository can use a Java record
 * to request that only a subset of entity attributes be retrieved,</p>
 * <pre>
 * &#64;Repository
 * public interface Cars extends BasicRepository&lt;Car, String&gt; {
 *     record ModelInfo(String make,
 *                      String model,
 *                      int year) {}
 *
 *     &#64;Find
 *     Optional&lt;ModelInfo&gt; getModelInfo(@By("vin") String vehicleIdNum);
 * }
 * </pre>
 *
 * <p>An automatic query method annotated {@code Find} returns an entity instance,
 * Java record instance, or entity attribute instance for every database record
 * which satisfies the parameter-based conditions.</p>
 * <ul>
 * <li>If the return type of the annotated method is {@code E} (the entity type,
 *     entity attribute type, or record type) or {@code Optional<E>}, and more
 *     than one database record satisfies the query conditions, the method must
 *     throw {@link jakarta.data.exceptions.NonUniqueResultException}.</li>
 * <li>If the return type of the annotated method is {@code E} (the entity type,
 *     entity attribute type, or record type), and no database record satisfies
 *     the query conditions, the method must throw
 *     {@link jakarta.data.exceptions.EmptyResultException}.</li>
 * </ul>
 *
 * <p>Annotations such as {@code @Find}, {@code @Query}, {@code @Insert}, {@code @Update}, {@code @Delete}, and
 * {@code @Save} are mutually-exclusive. A given method of a repository interface may have at most one {@code @Find}
 * annotation, lifecycle annotation, or query annotation.
 *
 * @see By
 * @see OrderBy
 * @see First
 * @see Select
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Find {
    /**
     * <p>Optionally specifies the queried entity type.</p>
     *
     * <p>The default value, {@code void.class}, has the special meaning of
     * determining the entity type from the method return type if the method
     * returns entities, and otherwise from the primary entity type of the
     * repository.</p>
     *
     * <p>A repository method with the {@code Find} annotation must specify a
     * valid entity class as the value if the method does not return entities
     * and the repository does not otherwise define a primary entity type.</p>
     *
     * <p>For example,</p>
     *
     * <pre>
     * &#64;Repository
     * public interface Vehicles {
     *     &#64;Find(Car.class)
     *     &#64;Select(_Car.PRICE)
     *     Optional&lt;Float&gt; getPrice(@By(_Car.VIN) String vehicleIdNum);
     *
     *     ...
     * }
     * </pre>
     */
    Class<?> value() default void.class;
}
