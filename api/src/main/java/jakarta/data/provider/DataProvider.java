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
package jakarta.data.provider;

import java.util.Set;

/**
 * <p>To enable CDI dependency injection of repositories by Jakarta EE products,
 * providers of Jakarta Data register an implementation of this interface
 * with the {@link java.util.ServiceLoader ServiceLoader}.</p>
 *
 * <p>The {@code DataProvider} implementation and related classes are packaged
 * within the Jakarta Data provider's JAR file, which must include a file with
 * the following name and location,</p>
 *
 * <pre>META-INF/services/jakarta.data.provider.DataProvider</pre>
 * 
 * <p>The content of the above file must be one or more lines, each specifying
 * the fully qualified name of a {@code DataProvider} implementation that is
 * provided within the JAR file.</p>
 *
 * <p>Users of Jakarta EE products do not access the {@code ServiceLoader}
 * directly to interact with Jakarta Data providers.
 * Instead, users can leverage CDI dependency injection to inject
 * {@link jakarta.data.repository.Repository repositories}
 * into their applications. The Jakarta EE product uses the
 * {@code ServiceLoader} to locate a provider of Jakarta Data that is capable
 * of supplying repository implementations of the desired database type.
 * The Jakarta EE product uses the {@link ClassLoader} of the repository
 * interface to search for Jakarta Data providers, requiring the Jakarta Data
 * provider to be accessible to the application (or be part of the application).
 * It is also permissible for a Jakarta EE product to be or include a provider of
 * Jakarta Data, but precedence must be given to Jakart Data providers
 * that are found on the {@code ServiceLoader}.</p>
 *
 * <p>Some Jakarta Data providers have their own vendor-specific dependency
 * injection models which are outside of the Jakarta Data specification
 * and do not involve the provision of a <code>DataProvider</code>.</p>
 */
public interface DataProvider {
    // TODO these constants should be moved elsewhere (jakarta.data.config package?) once config is standardized for Jakarta Data
    /**
     * NoSQL column database.
     */
    static final String COLUMN_DATABASE = "COLUMN";

    /**
     * NoSQL document database.
     */
    static final String DOCUMENT_DATABASE = "DOCUMENT";

    /**
     * NoSQL graph database.
     */
    static final String GRAPH_DATABASE = "GRAPH";

    /**
     * NoSQL key-value database.
     */
    static final String KEY_VALUE_DATABASE = "KEY-VALUE";

    /**
     * Relational database.
     */
    static final String RELATIONAL_DATABASE = "RELATIONAL";


    /**
     * <p>Returns the name of the Jakarta Data provider. This name can be used
     * in configuration when referring to this provider and when logging or
     * displaying messages to the user.</p>
     *
     * @return name that identifies the Jakarta Data provider. Never {@code null}.
     */
    String name();

    /**
     * <p>Returns the provider of repository instances for this
     * Jakarta Data provider.</p>
     *
     * @return the provider of repository instances. Never {@code null}.
     */
    RepositoryProvider repositoryProvider();

    /**
     * <p>Indicates which types of databases are supported by this provider.
     * The Jakarta EE product provider uses this information when there are
     * multiple Jakarta Data providers to identify which is capable of
     * supplying implementation for a repository interface based on the
     * type of database.</p>
     *
     * <p>The set of supported database types that is returned by this method
     * can include custom database types that are defined by the Jakarta Data
     * provider as well as built-in database type constants:</p>
     * <ul>
     * <li>{@link #COLUMN_DATABASE}</li>
     * <li>{@link #DOCUMENT_DATABASE}</li>
     * <li>{@link #GRAPH_DATABASE}</li>
     * <li>{@link #KEY_VALUE_DATABASE}</li>
     * <li>{@link #RELATIONAL_DATABASE}</li>
     * </ul>
     *
     * @return database types that are supported by this provider. Never {@code null}.
     */
    Set<String> supportedDatabaseTypes();
}
