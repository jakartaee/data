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
package jakarta.data.spi.expression.literal;

import jakarta.data.expression.Expression;
import jakarta.data.metamodel.Attribute;

/**
 * <p>{@code Literal} represents an immutable value within an
 * {@link Expression}. {@code Literal}s are created implicitly when obtaining
 * expressions that involve literal values.</p>
 *
 * <p>The {@linkplain Attribute entity and static metamodel} for the code
 * examples within this class are shown in the {@link Attribute} Javadoc.
 * </p>
 *
 * @param <V> entity attribute type.
 * @since 1.1
 */
public interface Literal<V> extends Expression<Object, V> {
    /**
     * <p>Returns the value represented by this {@code Literal}.
     * The value will never be {@code null}.</p>
     *
     * @return the value.
     */
    V value();

    /**
     * <p>Creates a {@code Literal} or subtype of {@code Literal} that
     * represents the given value.</p>
     *
     * <p>The most specific subtype of {@code Literal}, such as
     * {@link NumericLiteral#of(Class, Number) NumericLiteral},
     * {@link StringLiteral#of(String) StringLiteral},
     * {@link TemporalLiteral#of(Class, java.time.temporal.Temporal) TemporalLiteral},
     * or {@link ComparableLiteral#of(Comparable) ComparableLiteral},
     * should be used instead wherever possible.</p>
     *
     * @param <V>   entity attribute type.
     * @param value an immutable value or a mutable value that must never be
     *              modified after it is supplied to this method. Must never be
     *              {@code null}.
     * @return a {@code Literal} representing the value.
     * @throws NullPointerException if the value is {@code null}.
     */
    @SuppressWarnings("unchecked")
    static <V> Literal<V> of(V value) {
        if (value instanceof Comparable<?> comparable) {
            return (Literal<V>) ComparableLiteral.of(comparable);
        } else {
            return new LiteralRecord<>((Class<? extends V>) value.getClass(), value);
        }
    }

    /**
     * <p>Returns a {@code String} representing the literal value.</p>
     *
     * <p>Subtypes of {@code Literal} override this method to define more
     * specific formats that more closely align with query language.</p>
     *
     * <p>This method outputs a {@code String} that begins with an opening
     * curly brace and ends with a closing curly brace. Between the braces are
     * 3 terms delimited by a space character. The first term is
     * {@code Literal}. The second term is the fully qualified class name of
     * the value's type. The third term is the {@code toString()} output of the
     * value, enclosed in single quotes.</p>
     *
     * <p>For example, the output of
     * {@code Literal.of(ZoneId.of("America/Chicago")).toString()} is</p>
     * <pre>
     * {Literal java.time.ZoneId 'America/Chicago'}
     * </pre>
     *
     * @return a {@code String} representing the literal value.
     */
    @Override
    String toString();
}
