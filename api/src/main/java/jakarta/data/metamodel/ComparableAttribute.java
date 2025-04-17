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

import java.util.Objects;

/**
 * <p>Represents a comparable entity attribute in the {@link StaticMetamodel}.
 * Comparable entity attributes can be sorted on in query results and can be
 * compared against values in query restrictions.</p>
 *
 * <p>Where possible, always use the more specific subtypes of
 * {@code ComparableAttribute}:</p>
 *
 * <ul>
 * <li>{@link TextAttribute} for entity attributes that represent text,
 *     typically of type {@link String}.</li>
 * <li>{@link NumericAttribute} for entity attributes that represent numeric
 *     values, such as {@code long}, {@link Float}, and
 *     {@link java.math.BigInteger}.</li>
 * <li>{@link TemporalAttribute} for entity attributes that represent temporal
 *     values, such as {@link java.time.LocalDate} and {@link java.time.Instant}.
 *     </li>
 * </ul>
 *
 * <p>Entity attribute types for which {@code ComparableAttribute} is appropriate
 * include:</p>
 *
 * <ul>
 * <li>boolean attributes: {@code boolean} and {@link Boolean}</li>
 * <li>enum attributes - Note that it is provider-specific whether order is
 *     based on {@link Enum#ordinal()} or {@link Enum#name()}.
 *     The Jakarta Persistence default of {@code ordinal} can be overridden
 *     with the {@code jakarta.persistence.Enumerated} annotation.</li>
 * </ul>
 *
 * <p>In the above cases, {@code ComparableAttribute}, which provides more
 * function, is preferred over {@link SortableAttribute}.</p>
 *
 * @param <T> entity class of the static metamodel.
 * @param <V> type of entity attribute (or wrapper type if primitive).
 */
public interface ComparableAttribute<T,V extends Comparable<?>>
        extends BasicAttribute<T,V>, SortableAttribute<T>, ComparableExpression<T,V> {

    /**
     * <p>Creates a static metamodel {@code ComparableAttribute} representing the
     * entity attribute with the specified name.</p>
     *
     * @param <T> entity class of the static metamodel.
     * @param <V> type of entity attribute (or wrapper type if primitive).
     * @param entityClass   the entity class.
     * @param name          the name of the entity attribute.
     * @param attributeType type of the entity attribute.
     * @return instance of {@code ComparableAttribute}.
     */
    static <T, V extends Comparable<?>> ComparableAttribute<T, V> of(
            Class<T> entityClass,
            String name,
            Class<V> attributeType) {
        Objects.requireNonNull(entityClass, "entity class is required");
        Objects.requireNonNull(name, "entity attribute name is required");
        Objects.requireNonNull(attributeType, "entity attribute type is required");

        return new ComparableAttributeRecord<>(entityClass, name);
    }
}

