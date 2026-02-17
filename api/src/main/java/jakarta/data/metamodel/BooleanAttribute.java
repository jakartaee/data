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
package jakarta.data.metamodel;

import jakarta.data.expression.BooleanExpression;
import jakarta.data.messages.Messages;

/**
 * <p>Represents a {@linkplain Boolean boolean} entity attribute in the
 * {@link StaticMetamodel}.
 * Boolean entity attributes can be sorted on in query results and can be
 * compared in query restrictions. When sorting in ascending direction,
 * the value {@code false} is ordered before the value {@code true}.
 * When sorting in descending direction, the value {@code true} is ordered
 * before the value {@code false}.
 * </p>
 *
 * <p>Entity attribute types that are considered boolean include:</p>
 *
 * <ul>
 * <li>{@code boolean} primitve type</li>
 * <li>{@link Boolean} wrapper type</li>
 * </ul>
 *
 * <p>Where possible, {@code BooleanAttribute}, which provides more function,
 * is preferred over {@link ComparableAttribute} and {@link SortableAttribute}.
 * </p>
 *
 * @param <T> entity class of the static metamodel.
 * @since 1.1
 */
public interface BooleanAttribute<T>
        extends ComparableAttribute<T, Boolean>,
                BooleanExpression<T> {

    /**
     * <p>Creates a static metamodel {@code BooleanAttribute} representing the
     * entity attribute with the specified name.</p>
     *
     * @param <T>           entity class of the static metamodel.
     * @param entityClass   the entity class.
     * @param name          the name of the entity attribute.
     * @param attributeType type of the entity attribute: {@code boolean.class}
     *                      or {@code Boolean.class}.
     * @return instance of {@code BooleanAttribute}.
     */
    static <T> BooleanAttribute<T> of(
            Class<T> entityClass, String name, Class<Boolean> attributeType) {
        Messages.requireNonNull(entityClass, "entityClass");
        Messages.requireNonNull(name, "name");
        Messages.requireNonNull(attributeType, "attributeType");

        return new BooleanAttributeRecord<>(entityClass, name, attributeType);
    }

}

