/*
 * Copyright (c) 2025,2026 Contributors to the Eclipse Foundation
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

import java.time.temporal.Temporal;

import jakarta.data.expression.NavigableExpression;
import jakarta.data.messages.Messages;
import jakarta.data.spi.expression.path.BooleanPath;
import jakarta.data.spi.expression.path.ComparablePath;
import jakarta.data.spi.expression.path.NavigablePath;
import jakarta.data.spi.expression.path.NumericPath;
import jakarta.data.spi.expression.path.TemporalPath;
import jakarta.data.spi.expression.path.TextPath;

/**
 * <p>Represents an entity attribute that is an embeddable or association to
 * another entity.
 * These types of entity attributes have attributes of their own that can be
 * navigated to.</p>
 *
 * @param <T> entity class of the static metamodel.
 * @param <U> type of entity attribute.
 * @since 1.1
 */
public interface NavigableAttribute<T, U>
        extends Attribute<T>, NavigableExpression<T, U> {

    @Override
    default BooleanAttribute<T> navigate(BooleanAttribute<U> attribute) {
        return BooleanPath.of(this, attribute);
    }

    @Override
    default <C extends Comparable<C>> ComparableAttribute<T, C> navigate(
            ComparableAttribute<U, C> attribute) {
        return ComparablePath.of(this, attribute);
    }

    @Override
    default <V> NavigableExpression<T, V> navigate(
            NavigableAttribute<U, V> attribute) {
        return NavigablePath.of(this, attribute);
    }

    @Override
    default <N extends Number & Comparable<N>> NumericAttribute<T, N> navigate(
            NumericAttribute<U, N> attribute) {
        return NumericPath.of(this, attribute);
    }

    @Override
    default <V extends Temporal & Comparable<? extends Temporal>>
            TemporalAttribute<T, V> navigate(
                    TemporalAttribute<U, V> attribute) {
        return TemporalPath.of(this, attribute);
    }

    @Override
    default TextAttribute<T> navigate(TextAttribute<U> attribute) {
        return TextPath.of(this, attribute);
    }

    @Override
    Class<U> type();

    /**
     * <p>Creates a static metamodel {@code NavigableAttribute} representing
     * the entity attribute with the specified name.</p>
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
        Messages.requireNonNull(entityClass, "entityClass");
        Messages.requireNonNull(name, "name");
        Messages.requireNonNull(attributeType, "attributeType");

        return new NavigableAttributeRecord<>(entityClass, name, attributeType);
    }
}

