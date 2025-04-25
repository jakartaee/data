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
 * <p>Represents an entity attribute in the {@link StaticMetamodel}
 * that is neither sortable nor comparable.</p>
 *
 * @param <T> entity class of the static metamodel.
 * @param <V> type of entity attribute (or wrapper type if primitive).
 */
public interface BasicAttribute<T,V> extends Attribute<T>, Expression<T,V> {

    /**
     * <p>Creates a static metamodel {@code BasicAttribute} representing the
     * entity attribute with the specified name.</p>
     *
     * @param <T> entity class of the static metamodel.
     * @param <V> type of entity attribute (or wrapper type if primitive).
     * @param entityClass   the entity class.
     * @param name          the name of the entity attribute.
     * @param attributeType type of the entity attribute.
     * @return instance of {@code BasicAttribute}.
     */
    static <T,V> BasicAttribute<T,V> of(Class<T> entityClass,
                                        String name,
                                        Class<V> attributeType) {
        Objects.requireNonNull(entityClass, "The entityClass is required");
        Objects.requireNonNull(name, "The name is required");
        Objects.requireNonNull(attributeType, "The attributeType is required");

        return new BasicAttributeRecord<>(entityClass, name);
    }
}

