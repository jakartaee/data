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

import jakarta.data.expression.Expression;
import jakarta.data.messages.Messages;
import jakarta.annotation.Nonnull;

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
    @Nonnull
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
    @Nonnull
    static <T, V> BasicAttribute<T, V> of(@Nonnull Class<T> entityClass,
                                          @Nonnull String name,
                                          @Nonnull Class<V> attributeType) {
        Messages.requireNonNull(entityClass, "entityClass");
        Messages.requireNonNull(name, "name");
        Messages.requireNonNull(attributeType, "attributeType");

        return new BasicAttributeRecord<>(entityClass, name, attributeType);
    }

    /**
     * <p>Creates a static metamodel {@code BasicAttribute} representing an
     * entity attribute of a parameterized type. For example,
     * {@code List<String>} or {@code Map<String, Integer>}.</p>
     *
     * <p>Provide an anonymous {@link TypeToken} subclass to capture the
     * complete type. The compiler enforces consistency between the field
     * declaration and the type arguments:</p>
     *
     * <pre>{@code
     * BasicAttribute<Country, List<String>> cityNames = BasicAttribute.of(
     *         Country.class, CITYNAMES, new TypeToken<List<String>>(){});
     * }</pre>
     *
     * <p>The {@link #type()} method returns the raw container class
     * (for example, {@code List.class}).</p>
     *
     * @param <T>         entity class of the static metamodel
     * @param <V>         type of entity attribute, parameterized. For example,
     *                    {@code List<String>}
     * @param entityClass the entity class
     * @param name        the name of the entity attribute
     * @param token       type token capturing the full generic attribute type
     * @return instance of {@code BasicAttribute}
     */
    @Nonnull
    static <T, V> BasicAttribute<T, V> of(@Nonnull Class<T> entityClass,
                                          @Nonnull String name,
                                          @Nonnull TypeToken<V> token) {
        Messages.requireNonNull(entityClass, "entityClass");
        Messages.requireNonNull(name, "name");
        Messages.requireNonNull(token, "token");

        // TODO should we make token.type() available? If so, it might be
        // better to create a new Attribute subclass instead of reusing
        // BasicAttribute
        return new BasicAttributeRecord<>(entityClass, name, token.rawType());
    }
}

