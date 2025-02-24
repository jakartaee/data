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

/**
 * <p>Supertype for {@link StaticMetamodel} fields representing entity attributes.</p>
 *
 * <p>The following subtypes are provided:</p>
 * <ul>
 * <li>{@link TextAttribute} for entity attributes that represent text,
 *     typically of type {@link String}.</li>
 * <li>{@link ComparableAttribute} for entity attributes that represent other
 *     sortable and comparable values, such as {@code int}, {@link Long},
 *     {@code boolean}, {@link java.time.LocalDateTime}, and enumerations.</li>
 * <li>{@link SortableAttribute} for entity types that are sortable, but not
 *     comparable. Generally this subtype is unused but is applicable for
 *     databases that allow sorting on {@code byte[]} attributes. </li>
 * <li>{@link BasicAttribute} for other types of entity attributes, such as
 *     collections, embeddables, and other relation attributes.</li>
 * </ul>
 *
 * @param <T> entity class of the static metamodel.
 */
public interface Attribute<T> {

    /**
     * Obtain the entity attribute name, suitable for use wherever the specification requires
     * an entity attribute name. For example, as the parameter to {@link jakarta.data.Sort#asc(String)}.
     *
     * @return the entity attribute name.
     */
    String name();
}
