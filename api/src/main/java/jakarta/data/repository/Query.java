/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the query string such as SQL, JPA-QL, Cypher etc. that should be executed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Query {

    /**
     * Defines the query to be executed when the annotated method is called.
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

