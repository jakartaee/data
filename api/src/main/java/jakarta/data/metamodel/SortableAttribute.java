/*
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
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
package jakarta.data.metamodel;

import jakarta.data.Restrict;
import jakarta.data.Restriction;
import jakarta.data.Sort;

/**
 * Represents a sortable entity attribute in the {@link StaticMetamodel}.
 * Entity attribute types that are sortable include:
 *
 * <ul>
 * <li>numeric attributes</li>
 * <li>enum attributes</li>
 * <li>time attributes</li>
 * <li>boolean attributes</li>
 * <li>{@link TextAttribute textual attributes}</li>
 * </ul>
 *
 * @param <T> entity class of the static metamodel.
 */
public interface SortableAttribute<T> extends Attribute<T> {

    /**
     * Obtain a request for an ascending {@link Sort} based on the entity attribute.
     *
     * @return a request for an ascending sort on the entity attribute.
     */
    Sort<T> asc();

    /**
     * Obtain a request for a descending {@link Sort} based on the entity attribute.
     *
     * @return a request for a descending sort on the entity attribute.
     */
    Sort<T> desc();

    default <V extends Comparable<V>> Restriction<T> between(V min, V max) {
        return Restrict.between(min, max, name());
    }

    default <V extends Comparable<V>> Restriction<T> greaterThan(V value) {
        return Restrict.greaterThan(value, name());
    }

    default <V extends Comparable<V>> Restriction<T> greaterThanEqual(V value) {
        return Restrict.greaterThanEqual(value, name());
    }

    default <V extends Comparable<V>> Restriction<T> lessThan(V value) {
        return Restrict.lessThan(value, name());
    }

    default <V extends Comparable<V>> Restriction<T> lessThanEqual(V value) {
        return Restrict.lessThanEqual(value, name());
    }
}
