/*
 * Copyright (c) 2023,2026 Contributors to the Eclipse Foundation
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

import jakarta.data.expression.TextExpression;
import jakarta.data.messages.Messages;

/**
 * Represents an textual entity attribute in the {@link StaticMetamodel}.
 *
 * @param <T> entity class of the static metamodel.
 */
public interface TextAttribute<T> extends ComparableAttribute<T, String>, TextExpression<T> {

    /**
     * Returns {@code String.class} as the entity attribute type for text
     * attributes.
     *
     * @return {@code String.class}.
     * @since 1.1
     */
    @Override
    default Class<String> type() {
        return String.class;
    }

    /**
     * <p>Creates a static metamodel {@code TextAttribute} representing the
     * entity attribute with the specified name.</p>
     *
     * @param <T>         entity class of the static metamodel.
     * @param entityClass the entity class.
     * @param name        the name of the entity attribute.
     * @return instance of {@code TextAttribute}.
     * @since 1.1
     */
    static <T> TextAttribute<T> of(Class<T> entityClass, String name) {
        Messages.requireNonNull(entityClass, "entityClass");
        Messages.requireNonNull(name, "name");

        return new TextAttributeRecord<>(entityClass, name);
    }

}
