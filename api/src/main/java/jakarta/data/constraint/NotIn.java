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
package jakarta.data.constraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.data.expression.Expression;
import jakarta.data.messages.Messages;
import jakarta.data.spi.expression.literal.Literal;

import static java.util.Collections.unmodifiableList;

/**
 * A constraint that checks if an attribute's value is not in a given set of values.
 *
 * <p>Useful when filtering out a group of unwanted values.</p>
 *
 * <pre>{@code
 * List<Order> orders = repository.findAll(
 *     Restrict.notIn(_Order.status, Status.CANCELLED, Status.FAILED)
 * );
 * }</pre>
 *
 * @param <V> the type of the attribute
 */
public interface NotIn<V> extends Constraint<V> {

    @SafeVarargs
    static <V> NotIn<V> values(V... values) {
        if (values == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "values"));
        }

        if (values.length == 0) {
            throw new IllegalArgumentException(
                    Messages.get("002.no.elements", "values"));
        }

        final List<Expression<?, V>> expressions = new ArrayList<>(values.length);
        for (V value : values) {
            if (value == null) {
                throw new NullPointerException(
                        Messages.get("003.null.element", "values"));
            }

            expressions.add(Literal.of(value));
        }

        return new NotInRecord<>(unmodifiableList(expressions));
    }

    static <V> NotIn<V> values(Collection<V> values) {
        if (values == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "values"));
        }

        if (values.isEmpty()) {
            throw new IllegalArgumentException(
                    Messages.get("002.no.elements", "values"));
        }

        final List<Expression<?, V>> expressions = new ArrayList<>(values.size());
        for (V value : values) {
            if (value == null) {
                throw new NullPointerException(
                        Messages.get("003.null.element", "values"));
            }

            expressions.add(Literal.of(value));
        }

        return new NotInRecord<>(unmodifiableList(expressions));
    }

    static <V> NotIn<V> expressions(List<Expression<?, V>> expressions) {
        if (expressions == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "expressions"));
        }

        if (expressions.isEmpty()) {
            throw new IllegalArgumentException(
                    Messages.get("002.no.elements", "expressions"));
        }

        for (Expression<?, V> expression : expressions) {
            if (expression == null) {
                throw new NullPointerException(
                        Messages.get("003.null.element", "expressions"));
            }
        }

        return new NotInRecord<>(List.copyOf(expressions));
    }

    @SafeVarargs
    static <V> NotIn<V> expressions(Expression<?, V>... expressions) {
        if (expressions == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "expressions"));
        }

        if (expressions.length == 0) {
            throw new IllegalArgumentException(
                    Messages.get("002.no.elements", "expressions"));
        }

        for (Expression<?, V> expression : expressions) {
            if (expression == null) {
                throw new NullPointerException(
                        Messages.get("003.null.element", "expressions"));
            }
        }

        return new NotInRecord<>(List.of(expressions));
    }

    List<Expression<?, V>> expressions();
}
