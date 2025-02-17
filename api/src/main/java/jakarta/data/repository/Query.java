/*
 * Copyright (c) 2022,2025 Contributors to the Eclipse Foundation
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

import jakarta.data.Sort;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotates a repository method as a query method, specifying a query written in Jakarta Data Query Language (JDQL)
 * or in Jakarta Persistence Query Language (JPQL). A Jakarta Data provider is not required to support the complete JPQL
 * language, which targets relational data stores. However, a given provider might offer features of JPQL which go
 * beyond the subset required by JDQL, or might even offer vendor-specific extensions to JDQL which target particular
 * capabilities of the target data store technology. Such extensions come with no guarantee of portability between
 * providers, nor between databases.</p>
 *
 * <p>The required {@link #value} member specifies the JDQL or JPQL query as a string.</p>
 *
 * <p>For {@code select} statements, the return type of the query method must be
 * consistent with the type returned by the query.</p>
 *
 * <p>For {@code update} or {@code delete} statements, the return value must be one of:</p>
 * <ul>
 * <li>{@code void}</li>
 * <li>{@code int} or {@code long}, where the value is the number of matching entities.
 *     The value might not be precise on databases that provide eventual consistency,
 *     in which case some Jakarta Data providers might choose to raise
 *     {@link UnsupportedOperationException} instead of returning an imprecise value.
 *     </li>
 * </ul>
 *
 * <p>Compared to SQL, JDQL allows an abbreviated syntax for {@code select} statements:</p>
 * <ul>
 * <li>The {@code from} clause is optional in JDQL. When it is missing, the queried entity is determined by the return
 *     type of the repository method, or, if the return type is not an entity type, by the primary entity type of the
 *     repository.</li>
 * <li>The {@code select} clause is optional in both JDQL and JPQL. When it is missing, the query returns the queried
 *     entity.</li>
 * </ul>
 *
 * <p>A query might involve:</p>
 * <ul>
 * <li>named parameters of form {@code :name} where the labels {@code name} are legal Java identifiers, or </li>
 * <li>ordinal parameters of form {@code ?n} where the labels {@code n} are sequential positive integers starting
 *     from {@code 1}.</li>
 * </ul>
 * <p>A given query must not mix named and ordinal parameters.</p>
 *
 * <p>Each parameter of an annotated query method must either:</p>
 * <ul>
 * <li>have exactly the same name (the parameter name in the Java source, or a name assigned by {@link Param @Param})
 *     and type as a named parameter of the query,</li>
 * <li>have exactly the same type and position within the parameter list of the method as a positional parameter of the
 *     query, or</li>
 * <li>be of type {@code Limit}, {@code Order}, {@code PageRequest}, or {@code Sort}.</li>
 * </ul>
 *
 * <p>The {@link Param} annotation associates a method parameter with a named parameter. The {@code Param} annotation is
 * unnecessary when the method parameter name matches the name of a named parameter and the application is compiled with
 * the {@code -parameters} compiler option making parameter names available at runtime.</p>
 *
 * <p>A method parameter is associated with an ordinal parameter by its position in the method parameter list. The first
 * parameter of the method is associated with the ordinal parameter {@code ?1}.</p>
 *
 * <p>For example,</p>
 *
 * <pre>
 * &#64;Repository
 * public interface People extends CrudRepository&lt;Person, Long&gt; {
 *
 *     // JDQL with positional parameters
 *     &#64;Query("where firstName = ?1 and lastName = ?2")
 *     List&lt;Person&gt; byName(String first, String last);
 *
 *     // JDQL with a named parameter
 *     &#64;Query("where firstName || ' ' || lastName like :pattern")
 *     List&lt;Person&gt; byName(String pattern);
 *
 *     // JPQL using a positional parameter
 *     &#64;Query("from Person where extract(year from birthdate) = ?1")
 *     List&lt;Person&gt; bornIn(int year);
 *
 *     // JPQL using named parameters
 *     &#64;Query("select distinct name from Person " +
 *            "where length(name) &gt;= :min and length(name) &lt;= :max")
 *     Page&lt;String&gt; namesOfLength(&#64;Param("min") int minLength,
 *                                &#64;Param("max") int maxLength,
 *                                PageRequest pageRequest,
 *                                Order&lt;Person&gt; order);
 *
 *     ...
 * }
 * </pre>
 *
 * <p>A method annotated with {@code @Query} must return one of the following types:</p>
 * <ul>
 *     <li>the query result type {@code R}, when the query returns a single result,</li>
 *     <li>{@code Optional<R>}, when the query returns at most a single result,</li>
 *     <li>an array type {@code R[]},
 *     <li>{@code List<R>},</li>
 *     <li>{@code Stream<R>}, or</li>
 *     <li>{@code Page<R>} or {@code CursoredPage<R>}.</li>
 * </ul>
 * <p>The method returns an object for every query result.</p>
 * <ul>
 * <li>If the return type of the annotated method is {@code R} or {@code Optional<R>} and more than one record satisfies
 *     the query restriction, the method must throw {@link jakarta.data.exceptions.NonUniqueResultException}.</li>
 * <li>If the return type of the annotated method is {@code R} and no record satisfies the query restriction, the method
 *     must throw {@link jakarta.data.exceptions.EmptyResultException}.</li>
 * </ul>
 *
 * <p>Annotations such as {@code @Find}, {@code @Query}, {@code @Insert}, {@code @Update}, {@code @Delete}, and
 * {@code @Save} are mutually-exclusive. A given method of a repository interface may have at most one {@code @Find}
 * annotation, lifecycle annotation, or query annotation.
 *
 * @see Param
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Query {

    /**
     * <p>Specifies the query executed by the annotated repository method,
     * in JDQL or JPQL.</p>
     *
     * <p>If the annotated repository method accepts other forms of sorting
     * (such as a parameter of type {@link Sort}), it is the responsibility
     * of the application programmer to compose the query so that an
     * {@code ORDER BY} clause can be validly appended to the text of the
     * query.</p>
     *
     * @return the query to be executed when the annotated method is called.
     */
    String value();
}

