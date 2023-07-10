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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.repository;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotates a data repository interface that will be implemented by the container/runtime.</p>
 *
 * <p>This class is a CDI bean-defining annotation when CDI is available.
 * Regardless of whether CDI or custom dependency injection is used, the
 * repository implementation must be made available to applications via the
 * <code>jakarta.inject.Inject</code> annotation.</p>
 *
 * <p>For example,</p>
 *
 * <pre>
 * &#64;Repository
 * public interface Products extends DataRepository&lt;Product, Long&gt; {
 *
 *     &#64;OrderBy("price")
 *     List&lt;Product&gt; findByNameLike(String namePattern);
 *
 *     &#64;Query("UPDATE Product o SET o.price = o.price - (o.price * ?1) WHERE o.price * ?1 &lt;= ?2")
 *     int putOnSale(float rateOfDiscount, float maxDiscount);
 *
 *     ...
 * }
 * </pre>
 *
 * <pre>
 * &#64;Inject
 * Products products;
 *
 * ...
 * found = products.findByNameLike("%Printer%");
 * numUpdated = products.putOnSale(0.15f, 20.0f);
 * </pre>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Repository {
    /**
     * Value for the {@link provider} attribute that allows the use of any
     * available Jakarta Data provider that supports the type of entity
     * annotation that is present on the repository's entity class.
     */
    static final String ANY_PROVIDER = "";

    /**
     * Value for the {@link dataStore} attribute that indicates that the
     * Jakarta Data provider should choose a default data store to use.
     * The default data store might require additional vendor-specific
     * configuration, depending on the vendor.
     */
    static final String DEFAULT_DATA_STORE = "";

    /**
     * <p>Optionally indicates the data store to use for the repository.</p>
     *
     * <p>The Jakarta Data specification does not define a full configuration
     * model, and relies upon the Jakarta Data providers to provide configuration.
     * This value serves as an identifier linking to vendor-specific configuration
     * for each Jakarta Data provider to interpret in a vendor-specific way.</p>
     *
     * <p>For some Jakarta Data providers, this value could map directly to an
     * identifier in vendor-specific configuration. For others, this value could
     * map to a base configuration path that forms a configuration hierarchy,
     * such as in MicroProfile Config, or possibly Jakarta Config in a future
     * version of this specification. For providers backed by Jakarta Persistence,
     * it might point to a {@code jakarta.annotation.sql.DataSourceDefinition} name
     * or a {@code javax.sql.DataSource} JNDI name or resource reference,
     * or other vendor-specific configuration.</p>
     *
     * <p>The default value of this attribute is {@link #DEFAULT_DATA_STORE}.</p>
     *
     * @return the name of a data store or {@link #DEFAULT_DATA_STORE}.
     */
    String dataStore() default DEFAULT_DATA_STORE;

    /**
     * <p>Restricts the repository implementation to that of a specific
     * Jakarta Data provider.</p>
     *
     * <p>This is useful when multiple Jakarta Data providers support the
     * same type of entity annotation, in which case the provider attribute
     * clarifies which Jakarta Data provider must be used.
     * Jakarta Data providers must ignore {@link Repository} annotations
     * that indicate a different provider's name as the provider.</p>
     *
     * <p>The default value of this attribute is {@link #ANY_PROVIDER},
     * allowing the use of any available Jakarta Data provider that
     * supports the type of entity annotation that is present on the
     * repository's entity class.</p>
     *
     * @return the name of a Jakarta Data provider or {@link #ANY_PROVIDER}.
     */
    String provider() default ANY_PROVIDER;
}