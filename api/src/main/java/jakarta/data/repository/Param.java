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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <p>Annotates a parameter of a repository method to bind it to a named parameter of a {@link Query}.</p>
 *
 * <p>For example,</p>
 *
 * <pre>
 * {@code @Repository}
 * public interface Products extends BasicRepository{@code <Product, String>} {
 *
 *     {@code @Query("WHERE (p.length * p.width * p.height <= :maxVolume)")}
 *     {@code Page<Product>} freeShippingEligible({@code @Param}("maxVolume") float volumeLimit,
 *                                        {@code PageRequest<?>} pageRequest);
 *
 *     ...
 * }
 * </pre>
 *
 * <p>The {@code Param} annotation is unnecessary when the method parameter name matches the query language
 * named parameter name and the application is compiled with the {@code -parameters} compiler option making
 * parameter names available at runtime.</p>
 *
 * @see Query
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

    /**
     * Defines the name of the query language named parameter to bind to.
     * @return the name of the query language named parameter.
     */
    String value();
}
