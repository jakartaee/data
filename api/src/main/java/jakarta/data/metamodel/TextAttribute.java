/*
 * Copyright (c) 2023,2025 Contributors to the Eclipse Foundation
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

import jakarta.data.Sort;

import java.util.Objects;

/**
 * Represents an textual entity attribute in the {@link StaticMetamodel}.
 *
 * @param <T> entity class of the static metamodel.
 */
public interface TextAttribute<T> extends ComparableAttribute<T,String>, TextExpression<T> {

    /**
     * Obtain a request for an ascending, case-insensitive {@link Sort} based on the entity attribute.
     *
     * @return a request for an ascending, case-insensitive sort on the entity attribute.
     */
    default Sort<T> ascIgnoreCase() {
        return Sort.ascIgnoreCase(name());
    }

    /**
     * Obtain a request for a descending, case insensitive {@link Sort} based on the entity attribute.
     *
     * @return a request for a descending, case insensitive sort on the entity attribute.
     */
    default Sort<T> descIgnoreCase() {
        return Sort.descIgnoreCase(name());
    }

    /**
     * <p>Creates a static metamodel {@code TextAttribute} representing the
     * entity attribute with the specified name.</p>
     *
     * @param <T> entity class of the static metamodel.
     * @param entityClass the entity class.
     * @param name        the name of the entity attribute.
     * @return instance of {@code TextAttribute}.
     */
    static <T> TextAttribute<T> of(Class<T> entityClass, String name) {
        Objects.requireNonNull(entityClass, "Entity class is required.");
        Objects.requireNonNull(name, "Entity attribute name is required.");

        return new TextAttributeRecord<>(entityClass, name);
    }

}
