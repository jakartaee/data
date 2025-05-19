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

import jakarta.data.expression.Expression;
import jakarta.data.expression.literal.Literal;
import jakarta.data.messages.Messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public interface In<V> extends Constraint<V> {

    @SafeVarargs
    static <V> In<V> values(V... values) {
        if (values == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "values"));
        }

        if (values.length == 0) {
            throw new IllegalArgumentException(
                    Messages.get("002.no.elements", "values"));
        }

        List<Expression<?, V>> expressions = new ArrayList<>(values.length);
        for (V value : values) {
            if (value == null) {
                throw new NullPointerException(
                        Messages.get("003.null.element", "values"));
            }

            expressions.add(Literal.of(value));
        }

        return new InRecord<>(unmodifiableList(expressions));
    }

    static <V> In<V> values(Collection<V> values) {
        if (values == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "values"));
        }

        if (values.isEmpty()) {
            throw new IllegalArgumentException(
                    Messages.get("002.no.elements", "values"));
        }

        List<Expression<?, V>> expressions = new ArrayList<>(values.size());
        for (V value : values) {
            if (value == null) {
                throw new NullPointerException(
                        Messages.get("003.null.element", "values"));
            }

            expressions.add(Literal.of(value));
        }

        return new InRecord<>(unmodifiableList(expressions));
    }

    static <V> In<V> expressions(List<Expression<?, V>> expressions) {
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

        return new InRecord<>(List.copyOf(expressions));
    }

    @SafeVarargs
    static <V> In<V> expressions(Expression<?, V>... expressions) {
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

        return new InRecord<>(List.of(expressions));
    }

    List<Expression<?, V>> expressions();
}
