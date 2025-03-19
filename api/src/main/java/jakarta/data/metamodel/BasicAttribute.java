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

import jakarta.data.metamodel.constraint.Constraint;
import jakarta.data.metamodel.constraint.In;
import jakarta.data.metamodel.constraint.NotEqualTo;
import jakarta.data.metamodel.constraint.NotIn;
import jakarta.data.metamodel.constraint.NotNull;
import jakarta.data.metamodel.constraint.Null;
import jakarta.data.metamodel.restrict.Restriction;

import java.util.Objects;
import java.util.Set;

/**
 * <p>Represents an entity attribute in the {@link StaticMetamodel}
 * that is neither sortable nor comparable.</p>
 *
 * @param <T> entity class of the static metamodel.
 * @param <V> type of entity attribute (or wrapper type if primitive).
 */
public interface BasicAttribute<T,V> extends Attribute<T>, Expression<T,V> {

    default Restriction<T> equalTo(V value) {
        return new BasicRestrictionRecord<>(name(), Constraint.equalTo(value));
    }

    @Override
    default Restriction<T> in(Set<V> values) {
        if (values == null || values.isEmpty())
            throw new IllegalArgumentException("values are required");

        return new BasicRestrictionRecord<>(name(), In.values(values));
    }

    @Override
    default Restriction<T> isNull() {
        return new BasicRestrictionRecord<>(name(), Null.instance());
    }

    @Override
    default Restriction<T> notEqualTo(V value) {
        return new BasicRestrictionRecord<>(name(), NotEqualTo.value(value));
    }

    @Override
    default Restriction<T> notIn(Set<V> values) {
        if (values == null || values.isEmpty())
            throw new IllegalArgumentException("values are required");

        return new BasicRestrictionRecord<>(name(), NotIn.values(values));
    }

    @Override
    default Restriction<T> notNull() {
        return new BasicRestrictionRecord<>(name(), NotNull.instance());
    }

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
        Objects.requireNonNull(entityClass, "entity class is required");
        Objects.requireNonNull(name, "entity attribute name is required");
        Objects.requireNonNull(attributeType, "entity attribute type is required");

        return new BasicAttributeRecord<>(name);
    }

    default Restriction<T> restrict(Constraint<V> constraint) {
        return new BasicRestrictionRecord<>(name(), constraint);
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
