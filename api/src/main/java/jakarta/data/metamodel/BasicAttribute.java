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

import jakarta.data.metamodel.constraint.Constraint;
import jakarta.data.metamodel.impl.BasicAttributeRecord;
import jakarta.data.metamodel.restrict.BasicRestriction;
import jakarta.data.metamodel.restrict.Restrict;

import java.util.Set;

public interface BasicAttribute<T,V> extends Attribute<T> {

    default BasicRestriction<T> equalTo(V value) {
        return Restrict.equalTo(value, this);
    }

    default BasicRestriction<T> in(Set<V> values) {
        if (values == null || values.isEmpty())
            throw new IllegalArgumentException("values are required");

        return Restrict.in(values, this);
    }

    default BasicRestriction<T> isNull() {
        return Restrict.isNull(this);
    }

    default BasicRestriction<T> notEqualTo(V value) {
        return Restrict.notEqualTo(value, this);
    }

    default BasicRestriction<T> notIn(Set<V> values) {
        if (values == null || values.isEmpty())
            throw new IllegalArgumentException("values are required");

        return Restrict.notIn(values, this);
    }

    default BasicRestriction<T> notNull() {
        return Restrict.notNull(this);
    }

    default BasicRestriction<T> restrict(Constraint<V> constraint) {
        return Restrict.restrict(constraint, this);
    }

    static <T,V> BasicAttribute<T,V> of(Class<T> entity, String attribute, Class<V> type) {
        return new BasicAttributeRecord<>(attribute);
    }

}
