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

import jakarta.data.expression.NavigableExpression;

/**
 * <p>Represents an entity attribute that is an embeddable or association to
 * another entity.
 * These types of entity attributes have attributes of their own that can be
 * navigated to.</p>
 *
 * @param <T> entity class of the static metamodel.
 * @param <U> type of entity attribute.
 */
public interface NavigableAttribute<T, U>
        extends Attribute<T>, NavigableExpression<T, U> {

    @Override
    Class<U> attributeType();

    /**
     * <p>Creates a static metamodel {@code NavigableAttribute} representing
     * the
     * entity attribute with the specified name.</p>
     *
     * @param <T>           entity class of the static metamodel.
     * @param <U>           type of entity attribute.
     * @param entityClass   the entity class.
     * @param name          the name of the entity attribute.
     * @param attributeType type of the entity attribute.
     * @return instance of {@code NavigableAttribute}.
     */
    static <T, U> NavigableAttribute<T, U> of(Class<T> entityClass,
                                              String name,
                                              Class<U> attributeType) {
        Objects.requireNonNull(entityClass, "The entityClass is required");
        Objects.requireNonNull(name, "The name is required");
        Objects.requireNonNull(attributeType, "The attributeType is required");

        return new NavigableAttributeRecord<>(entityClass, name, attributeType);
    }
}

