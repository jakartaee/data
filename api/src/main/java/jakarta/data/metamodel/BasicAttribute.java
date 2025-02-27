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
import jakarta.data.metamodel.constraint.In;
import jakarta.data.metamodel.constraint.NotEqualTo;
import jakarta.data.metamodel.constraint.NotIn;
import jakarta.data.metamodel.constraint.NotNull;
import jakarta.data.metamodel.constraint.Null;
import jakarta.data.metamodel.restrict.Restriction;

import java.util.Set;

public interface BasicAttribute<T,V> extends Attribute<T> {

    default Restriction<T> equalTo(V value) {
        return new BasicRestrictionRecord<>(name(), Constraint.equalTo(value));
    }

    default Restriction<T> in(Set<V> values) {
        if (values == null || values.isEmpty())
            throw new IllegalArgumentException("values are required");

        return new BasicRestrictionRecord<>(name(), In.values(values));
    }

    default Restriction<T> isNull() {
        return new BasicRestrictionRecord<>(name(), Null.instance());
    }

    default Restriction<T> notEqualTo(V value) {
        return new BasicRestrictionRecord<>(name(), NotEqualTo.value(value));
    }

    default Restriction<T> notIn(Set<V> values) {
        if (values == null || values.isEmpty())
            throw new IllegalArgumentException("values are required");

        return new BasicRestrictionRecord<>(name(), NotIn.values(values));
    }

    default Restriction<T> notNull() {
        return new BasicRestrictionRecord<>(name(), NotNull.instance());
    }

    default Restriction<T> restrict(Constraint<V> constraint) {
        return new BasicRestrictionRecord<>(name(), constraint);
    }

}
