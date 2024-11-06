/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package jakarta.data.metamodel.impl;

import jakarta.data.Restriction;
import jakarta.data.Sort;
import jakarta.data.metamodel.SortableAttribute;

/**
 * Record type implementing {@link jakarta.data.metamodel.SortableAttribute}.
 * This may be used to simplify implementation of the static metamodel.
 *
 * @param name the name of the attribute
 */
public record SortableAttributeRecord<T>(String name)
        implements SortableAttribute<T> {
    @Override
    public Sort<T> asc() {
        return Sort.asc(name);
    }

    @Override
    public Sort<T> desc() {
        return Sort.desc(name);
    }

    @Override
    public Restriction<T> equal(Object value) {
        return Restriction.equal(name, value);
    }

    @Override
    public Restriction<T> isNull() {
        return Restriction.isNull(name);
    }

    @Override
    public Restriction<T> greaterThan(Object value) {
        return Restriction.greaterThan(name, value);
    }

    @Override
    public Restriction<T> greaterThanOrEqual(Object value) {
        return Restriction.greaterThanOrEqual(name, value);
    }

    @Override
    public Restriction<T> lessThan(Object value) {
        return Restriction.lessThan(name, value);
    }

    @Override
    public Restriction<T> lessThanOrEqual(Object value) {
        return Restriction.lessThanOrEqual(name, value);
    }

    @Override
    public Restriction<T> between(Object start, Object end) {
        return Restriction.between(name, start, end);
    }
}

