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
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * <p>A repository is an interface annotated with {@link jakarta.data.repository.Repository} that defines
 * operations on entities. Entities represent data in the persistent store.
 * In Domain-driven design, a repository participates in the domain but abstracts away storage
 * and infrastructure details.</p>
 *
 * <p>Repository interfaces can optionally inherit from built-in interfaces within this package,</p>
 *
 * <ul>
 * <li>{@link jakarta.data.repository.DataRepository} - root of the hierarchy, allows the entity type to be specified as a type parameter.</li>
 * <li>{@link jakarta.data.repository.BasicRepository} - provides common find, delete, and save operations.</li>
 * <li>{@link jakarta.data.repository.CrudRepository} - extends the {@code BasicRepository} to add {@link jakarta.data.repository.CrudRepository#insert(java.lang.Object)}
 *     and {@link jakarta.data.repository.CrudRepository#update(java.lang.Object)} operations.</li>
 * </ul>
 *
 * <p>Repository interfaces can also define their own life cycle methods using the
 * {@link jakarta.data.repository.Insert}, {@link jakarta.data.repository.Update}, {@link jakarta.data.repository.Save}, and {@link jakarta.data.repository.Delete} annotations,
 * as well as a variety of other methods following the Query by Method Name pattern,
 * the Parameter-based Conditions pattern, and the {@link jakarta.data.repository.Query} annotation.</p>
 *
 * <p>The module JavaDoc provides an {@link jakarta.data/ overview} of Jakarta Data.</p>
 */
package jakarta.data.repository;