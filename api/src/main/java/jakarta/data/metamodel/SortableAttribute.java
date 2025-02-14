/*
 * Copyright (c) 2023,2025 Contributors to the Eclipse Foundation
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

import jakarta.data.Sort;
import jakarta.data.metamodel.impl.SortableAttributeRecord;

/**
 * <p>Represents a entity attribute in the {@link StaticMetamodel}
 * that is sortable, but not comparable.</p>
 *
 * <p>Wherever possible, it is preferable to use {@link ComparableAttribute}
 * instead because it provides more function. Use {@code SortableAttribute}
 * only where you have an entity attribute upon which query results can be sorted,
 * but cannot otherwise be compared within query restrictions. For some databases,
 * entity attributes of type {@code byte[]} fall under this category.</p>
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

    /**
     * <p>Creates a static metamodel {@code SortableAttribute} representing the
     * entity attribute with the specified name.</p>
     *
     * @param <T> entity class of the static metamodel.
     * @param name the name of the entity attribute.
     * @return instance of {@code SortableAttribute}.
     */
    static <T> SortableAttribute<T> of(String name) {
        if (name == null)
            throw new IllegalArgumentException("entity attribute name is required");

        return new SortableAttributeRecord<>(name);
    }
}
