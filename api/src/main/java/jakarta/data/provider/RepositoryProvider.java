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

import jakarta.data.exceptions.MappingException;

/**
 * <p>Obtains repository instances from a Jakarta Data provider.</p>
 */
public interface RepositoryProvider {
    // TODO configuration needs to be standardized as a separate issue. This is difficult because
    //      Jakarta Config isn't very far along and lacks detail at this point and it isn't clear
    //      if it will be part of Jakarta EE 11. For, now the "configPrefix" is listed here as a
    //      reminder that we need to supply something for configuration here.
    /**
     * <p>Provides implementation of the specified repository interface.</p>
     *
     * @param <R>                 interface class that defines the data repository.
     * @param repositoryInterface the repository interface.
     * @param entityClass         type of entity that the repository persists.
     * @param configPrefix        prefix of Jakarta config properties for the repository.
     * @return repository instance. Never {@code null}.
     * @throws MappingException for inconsistencies between the repository or
     *         entity class and the database.
     */
    <R> R getRepository(Class<R> repositoryInterface, Class<?> entityClass, String configPrefix) throws MappingException;

    /**
     * <p>Invoked by the Jakarta EE product to notify the the Jakarta Data
     * provider that the CDI managed bean for a previously-obtained repository
     * instance has reached the end of its life cycle and should no longer be
     * usable. If multiple invocations of
     * {@link #getRepository(Class, Class, String) getRepository}
     * return the same repository instance, multiple dispose notifications
     * will be sent for the instance, each corresponding to the disposal of a
     * different CDI managed bean.</p>
     *
     * @param repository instance that was previously returned by
     *        {@link #getRepository(Class, Class, String) getRepository}.
     */
    void beanDisposed(Object repository);
}