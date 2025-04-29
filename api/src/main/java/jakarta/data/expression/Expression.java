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
package jakarta.data.expression;

import jakarta.data.constraint.Constraint;
import jakarta.data.constraint.EqualTo;
import jakarta.data.constraint.In;
import jakarta.data.constraint.NotEqualTo;
import jakarta.data.constraint.NotIn;
import jakarta.data.constraint.NotNull;
import jakarta.data.constraint.Null;
import jakarta.data.restrict.BasicRestriction;
import jakarta.data.restrict.Restriction;

import java.util.Collection;

public interface Expression<T, V> {

    default Restriction<T> equalTo(V value) {
        return BasicRestriction.of(this, EqualTo.value(value));
    }

    default Restriction<T> equalTo(Expression<? super T, V> expression) {
        return BasicRestriction.of(this, EqualTo.expression(expression));
    }

    default Restriction<T> in(Collection<V> values) {
        if (values == null || values.isEmpty())
            throw new IllegalArgumentException("The values are required");

        return BasicRestriction.of(this, In.values(values));
    }

    @SuppressWarnings("unchecked")
    default Restriction<T> in(V... values) {
        return BasicRestriction.of(this, In.values(values));
    }

    @SuppressWarnings("unchecked")
    default Restriction<T> in(Expression<? super T, V>... expressions) {
        return BasicRestriction.of(this, In.expressions(expressions));
    }

    default Restriction<T> isNull() {
        return BasicRestriction.of(this, Null.instance());
    }

    default Restriction<T> notEqualTo(V value) {
        return BasicRestriction.of(this, NotEqualTo.value(value));
    }

    default Restriction<T> notEqualTo(Expression<? super T, V> expression) {
        return BasicRestriction.of(this, NotEqualTo.expression(expression));
    }

    default Restriction<T> notIn(Collection<V> values) {
        if (values == null || values.isEmpty())
            throw new IllegalArgumentException("The values are required");

        return BasicRestriction.of(this, NotIn.values(values));
    }

    @SuppressWarnings("unchecked")
    default Restriction<T> notIn(V... values) {
        return BasicRestriction.of(this, NotIn.values(values));
    }

    @SuppressWarnings("unchecked")
    default Restriction<T> notIn(Expression<? super T, V>... expressions) {
        return BasicRestriction.of(this, NotIn.expressions(expressions));
    }

    default Restriction<T> notNull() {
        return BasicRestriction.of(this, NotNull.instance());
    }

    // TODO: should this be called restrict() ?
    default Restriction<T> satisfies(Constraint<V> constraint) {
        return BasicRestriction.of(this, constraint);
    }
}
