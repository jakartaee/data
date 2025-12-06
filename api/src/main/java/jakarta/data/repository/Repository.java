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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.repository;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotates a repository interface to be implemented by the
 * container/runtime.</p>
 *
 * <p>This class is a CDI bean-defining annotation when CDI is available.
 * Regardless of whether CDI or custom dependency injection is used, the
 * repository implementation must be made available to applications via the
 * {@code jakarta.inject.Inject} annotation.</p>
 *
 * <p>For example,</p>
 *
 * <pre>{@code
 * @Repository
 * public interface Products extends DataRepository<Product, Long> {
 *
 *     @Find
 *     @OrderBy("price")
 *     List<Product> namedLike(@By("name") @Is(Like.class) String namePattern);
 *
 *     @Query("UPDATE Product SET price = price - (price * ?1) WHERE price * ?1 <= ?2")
 *     int putOnSale(float rateOfDiscount, float maxDiscount);
 *
 *     ...
 * }
 * }</pre>
 *
 * <pre>{@code
 * @Inject
 * Products products;
 *
 * ...
 * found = products.namedLike("%Printer%");
 * numUpdated = products.putOnSale(0.15f, 20.0f);
 * }</pre>
 *
 * <p>The module Javadoc provides an {@link jakarta.data/ overview} of Jakarta
 * Data.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Repository {
    /**
     * Value for the {@link #provider()} attribute that allows the use of
     * any available Jakarta Data provider that supports the type of entity
     * annotation that is present on the repository's entity classes.
     */
    String ANY_PROVIDER = "";

    /**
     * <p>Value for the {@link #dataStore()} attribute that indicates use of
     * a default data store.</p>
     *
     * <p>In a Jakarta EE profile or platform environment, if the repository
     * implementation requires direct access to a {@code javax.sql.DataSource},
     * the default data store is the Jakarta EE default data source with name
     * {@code java:comp/DefaultDataSource}. Otherwise, the default data store
     * is determined by the Jakarta Data provider.</p>
     *
     * <p>The default data store might require additional vendor-specific
     * configuration, depending on the vendor.</p>
     */
    String DEFAULT_DATA_STORE = "";

    /**
     * <p>Optionally indicates the data store to use for the repository.</p>
     *
     * <p>Precedence for interpreting the {@code dataStore} value is as
     * follows, from higher precedence to lower precedence.</p>
     *
     * <ul>
     * <li>If running in an environment where Jakarta Config is available
     * and the value is found in Jakarta Config, an error is raised.
     * Interoperability with Jakarta Config is reserved for future versions
     * of Jakarta Data.
     * </li>
     * <li>In a Jakarta EE profile or platform environment, if the repository
     * implementation requires direct access to a {@code javax.sql.DataSource}
     * and if the value begins with {@code java:} and matches the name of a
     * {@code jakarta.annotation.sql.DataSourceDefinition}, the JNDI name of
     * a {@code DataSource}, or a resource reference to a {@code DataSource},
     * then the corresponding {@code DataSource} obtained from JNDI is used.
     * </li>
     * <li>In a Jakarta EE profile or platform environment, if the repository
     * implementation requires direct access to a Jakarta Persistence
     * {@code jakarta.persistence.EntityManagerFactory} and if the value does
     * not begin with {@code java:} and matches the name of a persistence unit,
     * then the container-managed {@code EntityManagerFactory} for that
     * persistence unit is used, obtained as if it had been injected using the
     * {@code PersistenceUnit} annotation. Or, if the value does begin with
     * {@code java:} and matches the name of a persistence unit reference,
     * then the corresponding {@code EntityManagerFactory} obtained from JNDI
     * is used.
     * </li>
     * <li>Otherwise, the value serves as an identifier linking to vendor-specific
     * configuration for the Jakarta Data provider to interpret in a vendor-specific
     * way. Refer to the documentation of the Jakarta Data provider.
     * </li>
     * </ul>
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
     * clarifies which Jakarta Data provider must be used. Jakarta Data
     * providers must ignore {@link Repository} annotations that indicate a
     * different provider's name as the provider.</p>
     *
     * <p>The default value of this attribute is {@link #ANY_PROVIDER},
     * allowing the use of any available Jakarta Data provider that supports
     * the type of entity annotation that is present on the repository's entity
     * class.</p>
     *
     * @return the name of a Jakarta Data provider or {@link #ANY_PROVIDER}.
     */
    String provider() default ANY_PROVIDER;
}