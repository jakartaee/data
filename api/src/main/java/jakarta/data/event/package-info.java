/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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

/**
 * <p>{@linkplain LifecycleEvent Events} related to the
 * <a href="../../../module-summary.html#StatelessRepository">
 * stateless lifecycle methods</a>.
 *
 * <p>For lifecycle events named with a prefix of {@code Pre}, the event is
 * automatically published by the Jakarta Data provider after a lifecycle
 * method is invoked, but before the corresponding operation is requested
 * of the datastore.
 *
 * <p>For lifecycle events named with a prefix of {@code Post}, the event is
 * automatically published by the Jakarta Data provider after the corresponding
 * operation is requested of the datastore. The application can choose to
 * receive the event at that point or at a later time, such as when the
 * currently active transaction completes successfully. For example,
 *
 * {@snippet lang="java":
 *     void productDeleted(@Observes(during = TransactionPhase.AFTER_SUCCESS)
 *                         PostDeleteEvent<Product> event) {
 *         Product deleted = event.entity();
 *         System.out.println("Product #" + deleted.getId() + " was deleted");
 *     }
 * }
 *
 * <p>The module Javadoc provides an {@linkplain jakarta.data/ overview} of
 * Jakarta Data.
 *
 * @since 1.1
 */
package jakarta.data.event;
