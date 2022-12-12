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

import java.util.Set;

/**
 * Parent repository interface for all repositories.
 * Central repository marker interface. Captures the domain type to manage and the domain type's id type.
 * @param <T> the domain type the repository manages
 * @param <K> the type of the id of the entity the repository manages
 */
public interface DataRepository<T, K> {

    /**
     * <p>Returns the set of reserved keywords that are supported by the
     * database to which the repository connects. Reserved keywords begin
     * with a capital letter as they appear within a method name
     * and as defined by the Jakarta Data specification in its list of
     * {@link Repository Reserved Keywords}. This set also includes the
     * Jakarta Data provider's vendor-specific reserved keywords (if any)
     * that are supported by the database.
     * This set does not include reserved method name prefixes,
     * such as <code>find...By</code> and <code>deleteBy</code>.</p>
     *
     * <p>The following reserved keywords must be supported for all
     * databases:</p>
     *
     * <ul>
     * <li><code>And</code></li>
     * <li>TODO specify the remainder of this list in a separate pull once the exact list is determined</li>
     * </ul>
     *
     * @return reserved keywords for repository method names that are
     *         supported by the database.
     */
    Set<String> supportedKeywords();
}
