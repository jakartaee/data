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
import jakarta.data.metamodel.restrict.BasicRestriction;
import jakarta.data.metamodel.restrict.Restriction;

import java.util.Set;

public interface Expression<T,V> {

    default Restriction<T> equalTo(V value) {
        return BasicRestriction.of(this, Constraint.equalTo(value));
    }

    default Restriction<T> in(Set<V> values) {
        if (values == null || values.isEmpty())
            throw new IllegalArgumentException("values are required");

        return BasicRestriction.of(this, In.values(values));
    }

    default Restriction<T> isNull() {
        return BasicRestriction.of(this, Null.instance());
    }

    default Restriction<T> notEqualTo(V value) {
        return BasicRestriction.of(this, NotEqualTo.value(value));
    }

    default Restriction<T> notIn(Set<V> values) {
        if (values == null || values.isEmpty())
            throw new IllegalArgumentException("values are required");

        return BasicRestriction.of(this, NotIn.values(values));
    }

    default Restriction<T> notNull() {
        return BasicRestriction.of(this, NotNull.instance());
    }

    // TODO: should this be called restrict() ?
    default Restriction<T> satisfies(Constraint<V> constraint) {
        return BasicRestriction.of(this, constraint);
    }

    // Leave for later, since we need a new kind of Restriction for these

//    default Restriction<T> equalTo(Expression<T,V> expression) {
//        throw new UnsupportedOperationException("not yet implemented");
//    }
//
//    default Restriction<T> notEqualTo(Expression<T,V> expression) {
//        throw new UnsupportedOperationException("not yet implemented");
//    }
}
