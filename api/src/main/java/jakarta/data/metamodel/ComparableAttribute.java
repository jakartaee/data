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

import jakarta.data.metamodel.impl.ComparableAttributeRecord;

import java.util.Objects;

/**
 * <p>Represents a comparable entity attribute in the {@link StaticMetamodel}.
 * Comparable entity attributes can be sorted on in query results and can be
 * compared against values in query restrictions.</p>
 *
 * <p>Entity attribute types that are comparable include:</p>
 *
 * <ul>
 * <li>time attributes, such as {@link java.time.LocalDateTime} and
 *     {@link java.time.Instant}</li>
 * <li>boolean attributes: {@code boolean} and {@link Boolean}</li>
 * <li>enum attributes - Note that it is provider-specific whether order is
 *     based on {@link Enum#ordinal()} or {@link Enum#name()}.
 *     The Jakarta Persistence default of {@code ordinal} can be overridden
 *     with the {@code jakarta.persistence.Enumerated} annotation.</li>
 * <li>numeric attributes, such as {@code long}, {@link Float}, and
 *     {@link java.math.BigInteger} - Use the {@link NumericAttribute} subtype
 *     instead</li>
 * <li>textual attributes - Use the {@link TextAttribute} subtype instead</li>
 * </ul>
 *
 * <p>Where possible, {@code ComparableAttribute}, which provides more function,
 * is preferred over {@link SortableAttribute}. Likewise, subtypes of
 * {@code ComparableAttribute}, such as {@link NumericAttribute} and
 * {@link TextAttribute} are preferred over {@code ComparableAttribute}.</p>
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

