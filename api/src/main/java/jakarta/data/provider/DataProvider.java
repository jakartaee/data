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

import jakarta.data.exceptions.MappingException;

/**
 * <p>Providers of Jakarta Data register an implementation of this interface
 * with the {@link java.util.ServiceLoader ServiceLoader} to be made available
 * to Jakarta EE product providers.</p>
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
 * Instead, users inject {@link jakarta.data.repository.Repository repositories}
 * into their applications. The Jakarta EE product uses the
 * {@code ServiceLoader} to locate a provider of Jakarta Data that is capable
 * of supplying repository implementations of the desired database type.
 * The Jakarta EE product uses the {@link ClassLoader} of the repository
 * interface to search for Jakarta Data providers, requiring the Jakarta Data
 * provider to be accessible to the application (or part of it). It is also
 * permissible for a Jakarta EE product to be or include a provider of
 * Jakarta Data, but precedence must be given to Jakart Data providers
 * that are found on the {@code ServiceLoader}.<p>
 */
public interface DataProvider {
    /**
     * <p>Provides implementation of the specified repository interface.</p>
     *
     * @param <R>                 interface class that defines the data repository.
     * @param repositoryInterface the repository interface.
     * @param entityClass         type of entity that the repository persists.
     * @return repository instance. Never {@code null}.
     * @throws MappingException for inconsistencies between the repository or
     *         entity class and the database.
     */
    <R> R createRepository(Class<R> repositoryInterface, Class<?> entityClass) throws MappingException;

    // TODO: should we specify that repository methods raise IllegalStateException after they are disposed?
    /**
     * <p>Invoked by the Jakarta EE product to notify the the Jakarta Data
     * provider that a previously-created repository instance has reached the
     * end of its life cycle as a CDI managed bean and should no longer be
     * usable.</p>
     *
     * @param repository repository instance that was previously returned by
     *        {@link #createRepository(Class, Class) createRepository}.
     */
    void disposeRepository(Object repository);

    /**
     * <p>Returns the name of the Jakarta Data provider. This name can be used
     * in configuration when referring to this provider and when logging or
     * displaying messages to the user.</p>
     *
     * @return name that identifies the Jakarta Data provider. Never {@code null}.
     */
    String name();

    /**
     * <p>Indicates which types of databases are supported by this provider.
     * The Jakarta EE product provider uses this information when there are
     * multiple Jakarta Data providers to identify which is capable of
     * supplying implementation for a repository interface based on the
     * type of database.</p>
     *
     * @return database types that are supported by this provider. Never {@code null}.
     */
    Set<DatabaseType> supportedDatabaseTypes();
}
