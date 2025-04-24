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
package jakarta.data.metamodel.constraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import jakarta.data.metamodel.Expression;
import jakarta.data.metamodel.expression.Literal;

import static java.util.Collections.unmodifiableList;

public interface NotIn<V> extends Constraint<V> {

    @SafeVarargs
    static <V> NotIn<V> values(V... values) {
        Objects.requireNonNull(values, "Values are required.");

        if (values.length == 0) {
            throw new IllegalArgumentException("Array of values must not be empty.");
        }

        final List<Expression<?, V>> expressions = new ArrayList<>(values.length);
        for (V value : values) {
            Objects.requireNonNull(value, "Value must not be null.");
            expressions.add(Literal.of(value));
        }

        return new NotInRecord<>(unmodifiableList(expressions));
    }

    static <V> NotIn<V> values(Collection<V> values) {
        Objects.requireNonNull(values, "Values are required.");

        if (values.isEmpty()) {
            throw new IllegalArgumentException("Values must not be empty.");
        }

        final List<Expression<?, V>> expressions = new ArrayList<>(values.size());
        for (V value : values) {
            Objects.requireNonNull(value, "Value must not be null.");
            expressions.add(Literal.of(value));
        }

        return new NotInRecord<>(unmodifiableList(expressions));
    }

    static <V> NotIn<V> expressions(List<Expression<?, V>> expressions) {
        Objects.requireNonNull(expressions, "Value expressions are required.");

        if (expressions.isEmpty()) {
            throw new IllegalArgumentException("Value expressions must not be empty.");
        }

        for (Expression<?, V> expression : expressions) {
            Objects.requireNonNull(expression, "Value expression must not be null.");
        }

        return new NotInRecord<>(List.copyOf(expressions));
    }

    @SafeVarargs
    static <V> NotIn<V> expressions(Expression<?, V>... expressions) {
        Objects.requireNonNull(expressions, "Value expressions are required.");

        if (expressions.length == 0) {
            throw new IllegalArgumentException(
                    "Array of value expressions must not be empty.");
        }

        for (Expression<?, V> expression : expressions) {
            Objects.requireNonNull(expression, "Value expression must not be null.");
        }

        return new NotInRecord<>(List.of(expressions));
    }

    List<Expression<?, V>> expressions();
}
