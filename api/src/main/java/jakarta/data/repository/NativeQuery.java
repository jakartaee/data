/*
 * Copyright (c) 2022,2026 Contributors to the Eclipse Foundation
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
package jakarta.data.repository;

import jakarta.data.Limit;
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.page.PageRequest;
import jakarta.data.restrict.Restriction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotates a repository method as a native query method, specifying a query
 * written in a native query language specific to the underlying datastore.
 * The syntax and capabilities of the native query language are determined
 * by the Jakarta Data provider and the target datastore technology.</p>
 *
 * <p>This specification does not define or constrain the syntax of native
 * queries, which is expected to be distinct from JCQL or JPQL.
 * For that reason, repository methods using {@code @NativeQuery}
 * are inherently non-portable between different types of datastores,
 * and possibly even between different Jakarta Data providers targeting
 * the same type of datastore.</p>
 *
 * <p>The required {@link #value} member specifies the native query as a
 * string, using whatever query language is appropriate for the datastore.</p>
 *
 * <p>For queries that return data extracted from the datastore (e.g. SQL {@code select}),
 * the return type of the native query method must be consistent with the type returned by
 * the query. The exact meaning of "consistent", as well as the rules for mapping between
 * native query results and Java types, is determined by the Jakarta Data provider.</p>
 *
 * <p>For queries that do not return data extracted from the datastore
 * (e.g. the simplest forms of SQL {@code insert}/{@code update}/{@code delete}),
 * the return value must be one of:</p>
 * <ul>
 * <li>{@code void}</li>
 * <li>{@code int} or {@code long}, where the value is the number of matching
 *     records affected by the operation. The Jakarta Data provider
 *     may throw {@link UnsupportedOperationException} if the datastore cannot
 *     reliably determine this count.
 *     </li>
 * </ul>
 *
 * <p>A query might involve parameters.
 * The syntax of parameters in the native query string,
 * as well as the mapping mechanism from Java parameters to native parameters,
 * are determined by the Jakarta Data provider and the underlying datastore.</p>
 *
 * <p>Each parameter of an annotated native query method must either:</p>
 * <ul>
 * <li>have exactly the same name (the parameter name in the Java source, or
 *     a name assigned by {@link Param @Param}) and type as a named parameter
 *     of the native query, if the Jakarta Data provider supports named parameters,</li>
 * <li>have exactly the same type and position within the parameter list of
 *     the method as a positional parameter of the native query,
 *     if the Jakarta Data provider supports positional parameters, or</li>
 * <li>be of type {@link Limit} or {@link PageRequest}.</li>
 * </ul>
 * <p>{@link Restriction}, {@link Sort} and {@link Order} are not valid parameter types
 * in native query methods.</p>
 * <p>The Jakarta Data provider may throw {@link UnsupportedOperationException}
 * when passed a cursor-based {@link PageRequest}
 * if the provider doesn't support cursors (key-based pagination).</p>
 *
 * <p>The {@link Param} annotation associates a method parameter with a named
 * parameter. The {@code Param} annotation is unnecessary when the method
 * parameter name matches the name of a named parameter and the application is
 * compiled with the {@code -parameters} compiler option making parameter names
 * available at runtime.</p>
 *
 * <p>For example (note that the query syntax and parameter syntax shown below
 * are for illustration only; actual syntax is determined by the Jakarta Data
 * provider):</p>
 *
 * <pre>{@code
 * @Repository
 * public interface Products extends CrudRepository<Product, Long> {
 *
 *     // Native SQL query with positional parameter (example syntax)
 *     @NativeQuery("SELECT * FROM product WHERE price > ? ORDER BY price DESC")
 *     List<Product> findExpensiveProducts(double minPrice);
 *
 *     // Native query with named parameter (example syntax)
 *     @NativeQuery("SELECT * FROM product WHERE name LIKE :pattern")
 *     List<Product> searchByName(@Param("pattern") String pattern);
 *
 *     // Native delete operation
 *     @NativeQuery("DELETE FROM product WHERE discontinued = true")
 *     long removeDiscontinuedProducts();
 *
 *     ...
 * }
 * }</pre>
 *
 * <p>A method annotated with {@code @NativeQuery} must return one of the following types:</p>
 * <ul>
 *     <li>the query result type {@code R}, when the query returns a single result,</li>
 *     <li>{@code Optional<R>}, when the query returns at most a single result,</li>
 *     <li>an array type {@code R[]},
 *     <li>{@code List<R>},</li>
 *     <li>{@code Stream<R>}, or</li>
 *     <li>{@code Page<R>}, or</li>
 *     <li>or {@code CursoredPage<R>}, if supported by the
 *         Jakarta Data provider for native queries.</li>
 * </ul>
 * <p>The method returns an object for every query result.</p>
 * <p>The number of query results may be limited using the {@link First} annotation.</p>
 * <ul>
 * <li>If the return type of the annotated method is {@code R} or {@code Optional<R>}
 *     and more than one record satisfies the query restriction, the method must throw
 *     {@link jakarta.data.exceptions.NonUniqueResultException}.</li>
 * <li>If the return type of the annotated method is {@code R} and no record satisfies
 *     the query restriction, the method must throw
 *     {@link jakarta.data.exceptions.EmptyResultException}.</li>
 * </ul>
 *
 * <p>Annotations such as {@code @Find}, {@code @Query}, {@code @NativeQuery}, {@code @Insert},
 * {@code @Update}, {@code @Delete}, and {@code @Save} are mutually-exclusive. A given method
 * of a repository interface may have at most one {@code @Find} annotation, lifecycle annotation,
 * or query annotation.
 *
 * @see Query
 * @see Param
 * @see First
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NativeQuery {

    /**
     * <p>Specifies the native query executed by the annotated repository method,
     * using the query language of the underlying datastore.</p>
     *
     * <p>The syntax and semantics of the native query are determined by the
     * Jakarta Data provider and are specific to the target datastore technology.</p>
     *
     * @return the native query to be executed when the annotated method is called.
     */
    String value();
}
