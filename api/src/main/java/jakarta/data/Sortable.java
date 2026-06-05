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
package jakarta.data;

/**
 * An entity attribute or expression that can be used to sort query results.
 *
 * @since 1.1
 * @param <T> the entity type
 */
public interface Sortable<T> {

    /**
     * Obtain an ascending {@linkplain Sort sorting criterion} based on
     * this entity attribute or expression.
     *
     * @return the sorting criterion.
     */
    default Sort<T> asc() {
        return Sort.asc(this);
    }

    /**
     * Obtain a descending {@linkplain Sort sorting criterion} based on
     * this entity attribute or expression.
     *
     * @return the sorting criterion.
     */
    default Sort<T> desc() {
        return Sort.desc(this);
    }
}
