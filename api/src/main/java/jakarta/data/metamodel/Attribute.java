/*
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
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

import java.util.Set;

import jakarta.data.Restrict;
import jakarta.data.Restriction;

/**
 * Represents an entity attribute in the {@link StaticMetamodel}.
 *
 * @param <T> entity class of the static metamodel.
 */
public interface Attribute<T> {

    default Restriction<T> equalTo(Object value) {
        return Restrict.equalTo(value, name());
    }

    default Restriction<T> in(Object... values) {
        if (values == null || values.length == 0)
            throw new IllegalArgumentException("values are required");

        return Restrict.in(Set.of(values), name());
    }

    default Restriction<T> isNull() {
        return Restrict.equalTo(null, name());
    }

    /**
     * Obtain the entity attribute name, suitable for use wherever the specification requires
     * an entity attribute name. For example, as the parameter to {@link Sort#asc(String)}.
     *
     * @return the entity attribute name.
     */
    String name();

    default Restriction<T> notEqualTo(Object value) {
        return Restrict.notEqualTo(value, name());
    }

    default Restriction<T> notIn(Object... values) {
        if (values == null || values.length == 0)
            throw new IllegalArgumentException("values are required");

        return Restrict.notIn(Set.of(values), name());
    }

    default Restriction<T> notNull() {
        return Restrict.notEqualTo(null, name());
    }
}
