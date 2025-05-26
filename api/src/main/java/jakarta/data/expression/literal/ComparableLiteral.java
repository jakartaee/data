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
package jakarta.data.expression.literal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.data.expression.ComparableExpression;

/**
 * <p>A {@linkplain Literal literal} expression for a sortable value, such as a
 * {@link Boolean}, {@link Character}, enumerated, or {@link UUID} value.
 * </p>
 *
 * @param <V> entity attribute type.
 * @since 1.1
 */
public interface ComparableLiteral<V extends Comparable<?>>
        extends ComparableExpression<Object, V>, Literal<V> {

    /**
     * <p>Creates a {@code ComparableLiteral} or subtype of
     * {@code ComparableLiteral} that represents the given value.</p>
     *
     * <p>The most specific subtype of {@code ComparableLiteral}, such as
     * {@link NumericLiteral#of(Number) NumericLiteral},
     * {@link StringLiteral#of(String) StringLiteral}, or
     * {@link TemporalLiteral#of(java.time.temporal.Temporal) TemporalLiteral},
     * should be used instead wherever possible.</p>
     *
     * @param <V>   entity attribute type.
     * @param value an immutable value or a mutable value that must never be
     *              modified after it is supplied to this method. Must never be
     *              {@code null}.
     * @return a {@code ComparableLiteral} representing the value.
     * @throws NullPointerException if the value is {@code null}.
     */
    @SuppressWarnings("unchecked")
    static <V extends Comparable<?>> ComparableLiteral<V> of(V value) {
        // Subtypes of Number and Temporal are needed here because
        // NumericExpression has N extends Number & Comparable<N>
        // and
        // TemporalExpression has V extends Temporal & Comparable<? extends Temporal>
        if (value instanceof String s) {
            return (ComparableLiteral<V>) StringLiteral.of(s);
        } else if (value instanceof Integer i) {
            return (ComparableLiteral<V>) NumericLiteral.of(i);
        } else if (value instanceof Long l) {
            return (ComparableLiteral<V>) NumericLiteral.of(l);
        } else if (value instanceof Float f) {
            return (ComparableLiteral<V>) NumericLiteral.of(f);
        } else if (value instanceof Double d) {
            return (ComparableLiteral<V>) NumericLiteral.of(d);
        } else if (value instanceof Byte b) {
            return (ComparableLiteral<V>) NumericLiteral.of(b);
        } else if (value instanceof Short s) {
            return (ComparableLiteral<V>) NumericLiteral.of(s);
        } else if (value instanceof BigInteger i) {
            return (ComparableLiteral<V>) NumericLiteral.of(i);
        } else if (value instanceof BigDecimal d) {
            return (ComparableLiteral<V>) NumericLiteral.of(d);
        } else if (value instanceof Instant i) {
            return (ComparableLiteral<V>) TemporalLiteral.of(i);
        } else if (value instanceof LocalDate d) {
            return (ComparableLiteral<V>) TemporalLiteral.of(d);
        } else if (value instanceof LocalDateTime d) {
            return (ComparableLiteral<V>) TemporalLiteral.of(d);
        } else if (value instanceof LocalTime t) {
            return (ComparableLiteral<V>) TemporalLiteral.of(t);
        } else {
            return new ComparableLiteralRecord<>(value);
        }
    }

    /**
     * <p>Returns a {@code String} representing the literal value.</p>
     *
     * <p>Subtypes of {@code ComparableLiteral} override this method to define
     * more specific formats that more closely align with query language.</p>
     *
     * <p>If the value is of type {@link Boolean}, this method outputs
     * {@code TRUE} for {@code Boolean.TRUE} and {@code FALSE} for
     * {@code Boolean.FALSE}.</p>
     *
     * <p>If the value is of type {@link Character}, this method outputs a
     * {@code String} that consists of the character enclosed in single quotes.
     * If the character is the single quote character, an additional single
     * quote character is included to escape it.</p>
     *
     * <p>If the value is an enumeration type, this method outputs the fully
     * qualified class name of the type, followed by the {@code .} character,
     * followed by the {@linkplain Enum#name() name} of the enumeration element
     * to which the value is assigned.</p>
     *
     * <p>For all other types, this method outputs a {@code String} that begins
     * with an opening curly brace and ends with a closing curly brace. Between
     * the braces are 3 terms delimited by a space character. The first term is
     * {@code ComparableLiteral}. The second term is the fully qualified class
     * name of the value's type. The third term is the {@code toString()}
     * output of the value, enclosed in single quotes.</p>
     *
     * <p>For example, the output of
     * {@code ComparableLiteral.of(Month.MAY).toString()} is</p>
     * <pre>
     * java.time.Month.MAY
     * </pre>
     *
     * <p>For example, the output of
     * {@code ComparableLiteral.of('D').toString()} is</p>
     * <pre>
     * 'D'
     * </pre>
     *
     * <p>The output of
     * {@code ComparableLiteral.of(UUID.fromString("73d518c4-b7f6-4c3b-9f63-60a045a43bb8")).toString()}
     * is</p>
     * <pre>
     * {ComparableLiteral java.util.UUID '73d518c4-b7f6-4c3b-9f63-60a045a43bb8'}
     * </pre>
     *
     * @return a {@code String} representing the literal value.
     */
    @Override
    String toString();
}
