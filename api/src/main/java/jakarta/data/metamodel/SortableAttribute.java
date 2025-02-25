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

import java.util.Objects;

import jakarta.data.Sort;
import jakarta.data.metamodel.impl.SortableAttributeRecord;

/**
 * <p>Represents a entity attribute in the {@link StaticMetamodel}
 * that is sortable, but not comparable.</p>
 *
 *<p>A {@code SortableAttribute} may be used to sort query results.
 * When an attribute type (or if a primitive, its wrapper class)
 * is a subtype of {@link java.lang.Comparable},
 * use of {@link ComparableAttribute} is usually preferred, since a
 * {@code SortableAttribute} cannot be used in order-based query
 * restrictions. Direct use of {@code SortableAttribute} is appropriate 
 * for attributes of type {@code byte[]}.</p>
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
        Objects.requireNonNull(name, "entity attribute name is required");

        return new SortableAttributeRecord<>(name);
    }
}
