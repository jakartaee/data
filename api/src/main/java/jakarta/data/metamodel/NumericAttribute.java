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
 * <p>Represents a {@linkplain Number numeric} entity attribute in the
 * {@link StaticMetamodel}.
 * Numeric entity attributes can be sorted on in query results and can be
 * compared against values in query restrictions. They can also be used as and
 * within numeric expressions involving various arithmetic operations.
 * </p>
 *
 * <p>Entity attribute types that are considered numeric include:</p>
 *
 * <ul>
 * <li>numeric primitve types: {@code byte}, {@code double}, {@code float},
 *     {@code int}, {@code long}, {@code short}</li>
 * <li>numeric wrapper types: {@link Byte}, {@link Double}, {@link Float},
 *     {@link Integer}, {@link Long}, {@link Short}</li>
 * <li>{@link java.math.BigDecimal} and {@link java.math.BigInteger}</li>
 * </ul>
 *
 * <p>Where possible, {@code NumericAttribute}, which provides more function,
 * is preferred over {@link ComparableAttribute} and {@link SortableAttribute}.
 * </p>
 *
 * @param <T> entity class of the static metamodel.
 * @param <N> type of entity attribute (or wrapper type if primitive).
 */

public interface NumericAttribute<T, N extends Number & Comparable<N>>
        extends ComparableAttribute<T, N>, NumericExpression<T, N> {

    /**
     * <p>Creates a static metamodel {@code NumericAttribute} representing the
     * entity attribute with the specified name.</p>
     *
     * @param <T>           entity class of the static metamodel.
     * @param <N>           type of entity attribute (or wrapper type if
     *                      primitive).
     * @param entityClass   the entity class.
     * @param name          the name of the entity attribute.
     * @param attributeType type of the entity attribute.
     * @return instance of {@code NumericAttribute}.
     */
    static <T, N extends Number & Comparable<N>> NumericAttribute<T, N> of(
            Class<T> entityClass, String name, Class<N> attributeType) {
        Objects.requireNonNull(entityClass, "The entityClass is required");
        Objects.requireNonNull(name, "The name is required");
        Objects.requireNonNull(attributeType, "The attributeType is required");

        return new NumericAttributeRecord<>(entityClass, name);
    }
}

