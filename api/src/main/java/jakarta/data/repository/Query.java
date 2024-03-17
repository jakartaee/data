/*
 * Copyright (c) 2022,2024 Contributors to the Eclipse Foundation
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
import jakarta.data.page.CursoredPage;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotates a repository method as a query method, specifying a query written in Jakarta Data Query Language (JDQL)
 * or in Jakarta Persistence Query Language (JPQL). A Jakarta Data provider is not required to support the complete JPQL
 * language, which targets relational data stores.</p>
 *
 * <p>The required {@link #value} member specifies the JDQL or JPQL query as a string. When {@linkplain Page pagination}
 * is used, the {@link #count} member optionally specifies a query which returns the total number of elements satisfying
 * the query.</p>
 *
 * <p>For {@code select} statements, the return type of the query method must be consistent with the type returned by
 * the query. For {@code update} or {@code delete} statements, it must be {@code void}, {@code int} or {@code long}.</p>
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
 * <p>A given query may not mix named and ordinal parameters.</p>
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
 * {@code @Repository}
 * public interface People extends CrudRepository{@code <Person, Long>} {
 *
 *     // JDQL with positional parameters
 *     {@code @Query("where firstName = ?1 and lastName = ?2")}
 *     {@code List<Person>} byName(String first, String last);
 *
 *     // JDQL with a named parameter
 *     {@code @Query("where firstName || ' ' || lastName like :pattern")}
 *     {@code List<Person>} byName(String pattern);
 *
 *     // JPQL using a positional parameter
 *     {@code @Query("from Person where extract(year from birthdate) = ?1")}
 *     {@code List<Person>} bornIn(int year);
 *
 *     // JPQL using named parameters
 *     {@code @Query("select distinct name from Person where length(name) >= :min and length(name) <= :max")}
 *     {@code Page<String>} namesOfLength({@code @Param}("min") int minLength,
 *                                {@code @Param}("max") int maxLength,
 *                                {@code PageRequest<Person>} pageRequest);
 *
 *     ...
 * }
 * </pre>
 *
 * <p>A query with an explicit {@code select} clause may return a type other than the entity class, as shown in the
 * example above, resulting in a {@link Page} parameterized with a query result type different to the queried entity
 * type when pagination is used. This results in a mismatch between the {@link Page} type returned by the repository
 * method and the {@link PageRequest} type accepted by the repository method. Therefore, a client must use the method
 * {@link Page#nextPageRequest(Class)}, explicitly specifying the entity class, to obtain the next page of results. For
 * example,</p>
 *
 * <pre>
 * {@code Page<String>} page2 = people.namesOfLength(5, 10, page1.nextPageRequest(Person.class));
 * </pre>
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

    /**
     * <p>Specifies an additional query that counts the number of elements
     * returned by the {@linkplain #value() primary query} and is used
     * to compute the {@linkplain Page#totalElements total elements} and
     * {@linkplain Page#totalPages total pages} for paginated repository
     * methods returning {@link Page} or {@link CursoredPage}.</p>
     *
     * <p>The additional query is optional. It is not used when pagination
     * is performed with {@link PageRequest#withoutTotal()} or when the
     * repository method returns a type other than {@link Page} or
     * {@link CursoredPage}.</p>
     *
     * @return a query for counting the number of elements across all pages.
     *         Empty string indicates that no counting query is provided.
     */
    String count() default "";
}

