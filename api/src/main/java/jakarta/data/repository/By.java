/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
 * <p>Annotates a parameter of a repository method to specify the name of an
 * entity attribute against which to compare.</p>
 *
 * <p>Example usage for a {@code Person} entity with attributes
 * {@code id}, {@code firstName}, and {@code lastName}:</p>
 *
 * <pre>
 * &#64;Repository
 * public interface People extends BasicRepository&lt;Person, Long&gt; {
 *
 *     List&lt;Person&gt; findNamed(&#64;By("firstName") String first,
 *                            &#64;By("lastName") String last);
 * }
 * ...
 * found = people.findNamed(first, last);
 * </pre>
 *
 * <p>The {@code By} annotation is unnecessary when the method parameter name
 * matches the entity attribute name and the application is compiled with the
 * {@code -parameters} compiler option that makes parameter names available
 * at run time.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface By {

    /**
     * The name of the entity attribute against which to compare.
     *
     * @return the entity attribute name.
     */
    String value();
}
