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
import jakarta.data.page.KeysetAwarePage;
import jakarta.data.page.Page;
import jakarta.data.page.Slice;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotates a repository method to specify a query string such as SQL, JPQL, Cypher etc. to execute.</p>
 *
 * <p>Jakarta Data providers for relational databases must support
 * JPQL queries if backed by a Jakarta Persistence provider,
 * and otherwise must support SQL queries.</p>
 *
 * <h2>Parameters</h2>
 *
 * <p>The query language can used named parameters or positional parameters.</p>
 *
 * <p><b>Named parameters</b> are referred to by name within the query language.
 * The {@link Param} annotation annotates method parameters to bind them to a named parameter name.
 * The {@code Param} annotation is unnecessary for named parameters when the method parameter name
 * matches the query language named parameter name and the application is compiled with the
 * {@code -parameters} compiler option that makes parameter names available
 * at run time. When the {@code Param} annotation is not used, the Jakarta Data provider must
 * interpret the query by scanning for the delimiter that is used for positional parameters.
 * If the delimiter appears for another purpose in a query that requries named parameters,
 * it might be necessary for the application to explictly define the {@code Param} in order to
 * disambiguate.</p>
 *
 * <p><b>Positional parameters</b> are referred to by a number that corresponds to the
 * numerical position, starting with 1, of the repository method parameters.</p>
 *
 * <p>For example,</p>
 *
 * <pre>
 * {@code @Repository}
 * public interface People extends CrudRepository{@code <Person, Long>} {
 *
 *     // JPQL using positional parameters
 *     {@code @Query("SELECT p from Person p WHERE (EXTRACT(YEAR FROM p.birthday) = ?1)")}
 *     {@code List<Person>} bornIn(int year);
 *
 *     // JPQL using named parameters
 *     {@code @Query("SELECT DISTINCT p.name from Person p WHERE (LENGTH(p.name) >= :min AND LENGTH(p.name) <= :max)")}
 *     {@code Page<String>} namesOfLength({@code @Param}("min") int minLength,
 *                                {@code @Param}("max") int maxLength,
 *                                {@code Pageable<Person>} pageRequest);
 *
 *     ...
 * }
 * </pre>
 *
 * <h2>Return Types</h2>
 *
 * <p>Some query languages such as JPQL can be used to return a type other than the
 * entity class, as shown in the above example, resulting in a {@link Page} that is
 * parameterized with the query result type rather than the entity class.
 * to request a subsequent page, use the {@link Slice#nextPageRequest(Class)} method
 * to specify the entity class. For example,</p>
 *
 * <pre>
 * {@code Page<String>} page2 = people.namesOfLength(5, 10, page1.nextPageable(Person.class));
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Query {

    /**
     * <p>Defines the query to be executed when the annotated method is called.</p>
     *
     * <p>If an application defines a repository method with <code>&#64;Query</code>
     * and supplies other forms of sorting (such as {@link Sort}) to that method,
     * then it is the responsibility of the application to compose the query in
     * such a way that an <code>ORDER BY</code> clause (or query language equivalent)
     * can be validly appended. The Jakarta Data provider is not expected to
     * parse query language that is provided by the application.</p>
     *
     * @return the query to be executed when the annotated method is called.
     */
    String value();

    /**
     * <p>Defines an additional query that counts the number of elements that are
     * returned by the {@link #value() primary} query. This is used to compute
     * the {@link Page#totalElements() total elements}
     * and {@link Page#totalPages() total pages}
     * for paginated repository queries that are annotated with
     * <code>@Query</code> and return a {@link Page} or {@link KeysetAwarePage}.
     * Slices do not use a counting query.</p>
     *
     * <p>The default value of empty string indicates that no counting query
     * is provided. A counting query is unnecessary when pagination is
     * performed with slices instead of pages and when pagination is
     * not used at all.</p>
     *
     * @return a query for counting the number of elements across all pages.
     *         Empty string indicates that no counting query is provided.
     */
    String count() default "";
}

