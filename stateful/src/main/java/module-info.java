/*
 * Copyright (c) 2025,2026 Contributors to the Eclipse Foundation
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

import jakarta.data.metamodel.Attribute;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Save;
import jakarta.data.repository.Update;
import jakarta.data.repository.stateful.Detach;
import jakarta.data.repository.stateful.Merge;
import jakarta.data.repository.stateful.Persist;
import jakarta.data.repository.stateful.Refresh;
import jakarta.data.repository.stateful.Remove;

/**
 * <p>API for Stateful Repositories in Jakarta Data</p>
 *
 * <p>A stateful repository is an interface annotated
 * {@link jakarta.data.repository.Repository @Repository} that defines
 * at least one stateful lifecycle method. A stateful lifecycle method
 * is annotated with exactly one of the following annotations defined in
 * the {@link jakarta.data.repository.stateful} package:</p>
 *
 * <ul>
 * <li>{@link Detach @Detach}</li>
 * <li>{@link Merge @Merge}</li>
 * <li>{@link Persist @Persist}</li>
 * <li>{@link Refresh @Refresh}</li>
 * <li>{@link Remove @Remove}</li>
 * </ul>
 *
 * <p>Stateful repository interfaces can optionally inherit from the built-in
 * {@link DataRepository} interface to define a primary entity class and the
 * type of its Id attribute. Otherwise, if all life cycle methods specify the
 * same entity class, then the primary entity class is that class.</p>
 *
 * <p>A stateful repository must not define or inherit any method annotated
 * {@link Delete @Delete}, {@link Insert @Insert}, {@link Save @Save}, or
 * {@link Update @Update}, which are to be used only on stateless repositories.
 * </p>
 *
 * <h2>Transactions</h2>
 *
 * <p>When using a stateful repository, all lifecycle methods and all updates
 * performed on entities obtained from query methods must run within a
 * transaction. A persistence context remains active for the duration of the
 * transaction, after which all updates must be committed or rolled back in the
 * data store, per the outcome of the transaction. In Jakarta EE environments,
 * a Jakarta Transactions (JTA) transaction is used.</p>
 *
 * <h2>Examples</h2>
 *
 * <p>The {@linkplain Attribute entity and static metamodel} for the following
 * code examples are shown in the {@link Attribute} Javadoc.</p>
 *
 * <p>A stateful repository interface:
 *
 * <pre>{@code
 * @Repository
 * public interface Vehicles extends DataRepository<Car, String> {
 *     @Persist
 *     @Transactional
 *     void persist(Car... cars);
 *
 *     @Refresh
 *     void refresh(Car car);
 *
 *     @Remove
 *     void remove(List<Car> cars);
 *
 *     @Find
 *     List<Car> search(Restriction<Car> filter);
 * }}</pre>
 * </p>
 *
 * <p>Application code relying on the
 * {@code jakarta.transaction.Transactional} annotation on the example
 * {@code persist} method for container-provided transaction management:
 *
 * <pre>{@code
 *     @Inject
 *     Vehicles vehicles;
 *
 *     ...
 *
 *     vehicles.persist(car1, car2, car3);
 * }</pre>
 * </p>
 *
 * <p>Application code explicitly managing a transaction covering multiple
 * repository operations and updates to entities:
 *
 * <pre>{@code
 *     @Resource
 *     UserTransaction tx;
 *
 *     @Inject
 *     Vehicles vehicles;
 *
 *     ...
 *
 *     tx.begin();
 *     try {
 *         for (Car car : vehicles.search(_Car.price.lessThan(5000))) {
 *             if (car.price > 1000)
 *                 car.price -= 200;
 *             else
 *                 scrapList.add(car);
 *         }
 *         if (!scrapList.isEmpty)
 *             vehicles.remove(scrapList);
 *     } finally {
 *         if (tx.getStatus() == Status.STATUS_MARKED_ROLLBACK)
 *             tx.rollback();
 *         else
 *             tx.commit();
 *     }
 * }</pre>
 * </p>
 *
 * <p>The Javadoc of the Jakarta Data module provides an
 * <a href="../jakarta.data/">overview</a> of Jakarta Data.</p>
 *
 * @since 1.1
 */
module jakarta.data.stateful {

    exports jakarta.data.repository.stateful;
    requires jakarta.data;
}