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
package jakarta.data.repository;

/**
 * <p>A repository for entities that are stored in a relational database
 * with capability that is consistent with SQL and JPQL.</p>
 *
 * <p>The {@link #supportedKeywords() supportedKeywords} method return value
 * of a repository that implements {@code SQLRepository} must include the
 * following supported reserved keywords,</p>
 *
 * <ul>
 * <li><code>IgnoreCase</code></li>
 * <li>TODO specify the remainder of this list in a separate pull once the exact list is determined</li>
 * </ul>
 *
 * @param <T> type of entity managed by the repository.
 * @param <K> type of key that is the unique identifier for the entity.
 */
public interface SQLRepository<T, K> extends DataRepository<T, K> {
}
