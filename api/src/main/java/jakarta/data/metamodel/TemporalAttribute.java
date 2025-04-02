/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

import jakarta.data.metamodel.impl.TemporalAttributeRecord;

import java.time.temporal.Temporal;
import java.util.Objects;

/**
 * <p>Represents a {@linkplain Temporal temporal} entity attribute in the
 * {@link StaticMetamodel}. Temporal entity attributes can be sorted on in
 * query results and can be compared against values in query restrictions.
 * </p>
 *
 * <p>Jakarta Data supports the following entity attribute types for temporal
 * data:</p>
 *
 * <ul>
 * <li>{@link java.time.Instant}</li>
 * <li>{@link java.time.LocalDate}</li>
 * <li>{@link java.time.LocalDateTime}</li>
 * <li>{@link java.time.LocalTime}</li>
 * </ul>
 *
 * <p>Where possible, {@code TemporalAttribute} is preferred over
 * {@link ComparableAttribute} and {@link SortableAttribute}.</p>
 *
 * @param <T> entity class of the static metamodel.
 * @param <V> type of entity attribute.
 */

public interface TemporalAttribute<T,V extends Temporal & Comparable<? extends Temporal>>
        extends ComparableAttribute<T,V>, TemporalExpression<T, V> {

    /**
     * <p>Creates a static metamodel {@code TemporalAttribute} representing the
     * entity attribute with the specified name.</p>
     *
     * @param <T> entity class of the static metamodel.
     * @param <V> type of entity attribute.
     * @param entityClass   the entity class.
     * @param name          the name of the entity attribute.
     * @param attributeType type of the entity attribute.
     * @return instance of {@code TemporalAttribute}.
     */
    static <T,V extends Temporal & Comparable<? extends Temporal>> TemporalAttribute<T,V> of(
            Class<T> entityClass, String name, Class<V> attributeType) {
        Objects.requireNonNull(entityClass, "entity class is required");
        Objects.requireNonNull(name, "entity attribute name is required");
        Objects.requireNonNull(attributeType, "entity attribute type is required");

        return new TemporalAttributeRecord<T, V>(entityClass, name);
    }
}

