/*
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
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
 * <p>Maps a repository method parameter to a Query Language named parameter
 * if the method is annotated with {@link Query}. Otherwise, specifies the name of an
 * entity attribute to compare against the value of the annotated repository method parameter.</p>
 *
 * <p>Example usage for a {@code Person} entity with attributes
 * {@code id}, {@code birthYear}, {@code firstName}, and {@code lastName}:</p>
 *
 * <pre>
 * &#64;Repository
 * public interface People extends BasicRepository&lt;Person, Long&gt; {
 *
 *     // Here, Param refers to the name of a Query Language named parameter,
 *     &#64;Query("SELECT o FROM Person o WHERE ( EXTRACT(YEAR FROM CURRENT_DATE) - o.birthYear > :age )")
 *     List&lt;Person&gt; olderThan(&#64;Param("age") int exclusiveMinAge, Sort... sorts);
 *
 *     // Here, Param refers to the name of an entity attribute with which to compare,
 *     List&lt;Person&gt; findNamed(&#64;Param("firstName") String first,
 *                            &#64;Param("lastName") String last);
 * }
 * </pre>
 *
 * <p>The {@code Param} annotation is unnecessary when the method parameter name
 * matches the entity attribute name and the application is compiled with the
 * {@code -parameters} compiler option that makes parameter names available
 * at run time.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

    /**
     * The Query Language named parameter name when used with {@link Query} or
     * otherwise the entity attribute name to compare with.
     * @return the name of the Query Language named parameter
     *         or the name of the entity attribute.
     */
    String value();
}
