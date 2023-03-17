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
package jakarta.data.spi.provider;

import java.lang.annotation.Annotation;
import java.util.Set;

import jakarta.data.exceptions.MappingException;

/**
 * <p>In a Jakarta EE profile, Jakarta Data providers can use the
 * Jakarta Data Provider SPI to register to provide repository instances
 * to the Jakarta EE product's CDI extension that handles
 * {@link jakarta.data.repository.Repository Repository} injection points.</p>
 *
 * <p>Jakarta Data providers that implement the Jakarta Data Provider SPI
 * make an implementation of this interface available to the
 * {@link java.util.ServiceLoader ServiceLoader} as follows.</p>
 *
 * <p>The {@code DataProvider} implementation and related classes are packaged
 * within the Jakarta Data provider's JAR file, also including a file with the
 * following name and location,</p>
 *
 * <pre>META-INF/services/jakarta.data.spi.provider.DataProvider</pre>
 * 
 * <p>The content of the above file must be one or more lines, each specifying
 * the fully qualified name of a {@code DataProvider} implementation that is
 * provided within the JAR file.</p>
 *
 * <p>Jakarta EE applications do not use the Jakarta Data Provider SPI.
 * Jakarta EE products are not required to make this class available to
 * applications.</p>
 */
public interface DataProvider {
    /**
     * <p>Provides an instance that implements the specified repository interface.</p>
     *
     * @param <R>        interface class that defines the data repository.
     * @param repository the repository interface.
     * @return repository instance. Never {@code null}.
     * @throws MappingException for inconsistencies between the repository or
     *         entity class and the database.
     */
    <R> R getRepository(Class<R> repository) throws MappingException;


    /**
     * <p>Returns the name of the Jakarta Data provider. This name can be used
     * in configuration when referring to this provider and when logging or
     * displaying messages to the user.</p>
     *
     * @return name that identifies the Jakarta Data provider. Never {@code null}.
     */
    String name();

    /**
     * <p>Invoked by the Jakarta EE product to notify the Jakarta Data
     * provider that the CDI managed bean for a previously-obtained repository
     * instance has reached the end of its life cycle. If multiple invocations of
     * {@link #getRepository(Class) getRepository} return the same
     * repository instance, multiple dispose notifications will be sent for the
     * instance, each corresponding to the disposal of a different CDI managed bean.</p>
     *
     * @param repository instance that was previously returned by
     *        {@link #getRepository(Class) getRepository}.
     */
    void repositoryBeanDisposed(Object repository);

    /**
     * <p>Returns the type of entity annotations that are supported by this provider.
     * For example, <code>jakarta.persistence.Entity</code> or <code>jakarta.nosql.Entity</code>.
     * This tells the Jakarta EE product that it can request repositories for entities
     * of this type from this provider.</p>
     *
     * @return type of entity annotations that are supported by this provider.
     */
    Set<Class<? extends Annotation>> supportedEntityAnnotations();
}
