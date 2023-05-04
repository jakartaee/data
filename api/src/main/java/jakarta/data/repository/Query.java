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
}

