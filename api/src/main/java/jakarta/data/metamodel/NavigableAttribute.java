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
 * <p>Represents an entity attribute that is an embeddable or association to another entity.
 * These types of entity attributes have attributes of their own that can
 * be navigated to.</p>
 *
 * @param <T> entity class of the static metamodel.
 * @param <U> type of entity attribute.
 */
public interface NavigableAttribute<T,U>
        extends Attribute<T>, NavigableExpression<T,U> {

    /**
     * <p>Creates a static metamodel {@code NavigableAttribute} representing the
     * entity attribute with the specified name.</p>
     *
     * @param <T> entity class of the static metamodel.
     * @param <U> type of entity attribute.
     * @param entityClass   the entity class.
     * @param name          the name of the entity attribute.
     * @param attributeType type of the entity attribute.
     * @return instance of {@code NavigableAttribute}.
     */
    static <T,U> NavigableAttribute<T,U> of(Class<T> entityClass,
                                            String name,
                                            Class<U> attributeType) {
        Objects.requireNonNull(entityClass, "entity class is required");
        Objects.requireNonNull(name, "entity attribute name is required");
        Objects.requireNonNull(attributeType, "entity attribute type is required");

        return new NavigableAttributeRecord<>(entityClass, name);
    }

    @Override
    default int compareTo(NavigableExpression<T, U> other) {
        if (getClass().equals(other.getClass())) {
            NavigableAttribute<T,U> another = (NavigableAttribute<T,U>) other;
            int comp = declaringType().getName().compareTo(
                    another.declaringType().getName());
            if (comp == 0) {
                comp = name().compareTo(another.name());
            }
            return comp;
        } else {
            return getClass().getName().compareTo(other.getClass().getName());
        }
    }
}

