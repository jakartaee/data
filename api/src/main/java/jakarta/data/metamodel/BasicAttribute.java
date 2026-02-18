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

import jakarta.data.expression.Expression;
import jakarta.data.messages.Messages;

/**
 * <p>Represents an entity attribute in the {@link StaticMetamodel} that is
 * neither sortable nor capable of order-based comparison. Subclasses of
 * {@code BasicAttribute} allow for entity attributes with those abilities.</p>
 *
 * @param <T> entity class of the static metamodel.
 * @param <V> type of entity attribute (or wrapper type if primitive).
 * @since 1.1
 */
public interface BasicAttribute<T, V> extends Attribute<T>, Expression<T, V> {

    /**
     * Obtain the Java class of the entity attribute.
     *
     * @return the type of the entity attribute.
     */
    @Override
    Class<V> type();

    /**
     * <p>Creates a static metamodel {@code BasicAttribute} representing the
     * entity attribute with the specified name.</p>
     *
     * @param <T>           entity class of the static metamodel.
     * @param <V>           type of entity attribute (or wrapper type if
     *                      primitive).
     * @param entityClass   the entity class.
     * @param name          the name of the entity attribute.
     * @param attributeType type of the entity attribute.
     * @return instance of {@code BasicAttribute}.
     */
    static <T, V> BasicAttribute<T, V> of(Class<T> entityClass,
                                          String name,
                                          Class<V> attributeType) {
        Messages.requireNonNull(entityClass, "entityClass");
        Messages.requireNonNull(name, "name");
        Messages.requireNonNull(attributeType, "attributeType");

        return new BasicAttributeRecord<>(entityClass, name, attributeType);
    }
}

