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

import jakarta.data.metamodel.restrict.Restrict;
import jakarta.data.metamodel.restrict.Restriction;

import java.util.Set;

/**
 * <p>Represents an entity attribute in the {@link StaticMetamodel}
 * that is not sortable or comparable.</p>
 *
 * @param <T> entity class of the static metamodel.
 * @param <V> type of entity attribute (or wrapper type if primitive).
 */
public interface BasicAttribute<T,V> extends Attribute<T> {

    default Restriction<T> equalTo(V value) {
        return Restrict.equalTo(value, name());
    }

    default Restriction<T> in(Set<V> values) {
        if (values == null || values.isEmpty())
            throw new IllegalArgumentException("values are required");

        return Restrict.in(values, name());
    }

    default Restriction<T> isNull() {
        return Restrict.isNull(name());
    }

    default Restriction<T> notEqualTo(V value) {
        return Restrict.notEqualTo(value, name());
    }

    default Restriction<T> notIn(Set<V> values) {
        if (values == null || values.isEmpty())
            throw new IllegalArgumentException("values are required");

        return Restrict.notIn(values, name());
    }

    default Restriction<T> notNull() {
        return Restrict.notNull(name());
    }

    /**
     * <p>Creates a static metamodel {@code BasicAttribute} representing the
     * entity attribute with the specified name.</p>
     *
     * @param <T> entity class of the static metamodel.
     * @param <V> type of entity attribute (or wrapper type if primitive).
     * @param name the name of the entity attribute.
     * @return instance of {@code BasicAttribute}.
     */
    static <T,V> BasicAttribute<T,V> of(String name) {
        if (name == null)
            throw new IllegalArgumentException("entity attribute name is required");

        return new BasicAttributeRecord<>(name);
    }
}

/**
 * Hidden internal implementation of BasicAttribute.
 *
 * @param <T> entity class of the static metamodel.
 * @param <V> type of entity attribute (or wrapper type if primitive).
 */
record BasicAttributeRecord<T,V>(String name)
    implements BasicAttribute<T,V> {
}
