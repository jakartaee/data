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
package jakarta.data.metamodel.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.data.metamodel.ComparableExpression;

public interface ComparableLiteral<T, V extends Comparable<?>>
        extends ComparableExpression<T, V>, Literal<T, V> {

    @SuppressWarnings("unchecked")
    static <T, V extends Comparable<?>> ComparableLiteral<T, V> of(V value) {
        // Subtypes of Number and Temporal are needed here because
        // NumericExperssion has N extends Number & Comparable<N>
        // and
        // TemporalExpression has V extends Temporal & Comparable<? extends Temporal>
        if (value instanceof String s) {
            return (ComparableLiteral<T, V>) StringLiteral.of(s);
        } else if (value instanceof Integer i) {
            return (ComparableLiteral<T, V>) NumericLiteral.of(i);
        } else if (value instanceof Long l) {
            return (ComparableLiteral<T, V>) NumericLiteral.of(l);
        } else if (value instanceof Float f) {
            return (ComparableLiteral<T, V>) NumericLiteral.of(f);
        } else if (value instanceof Double d) {
            return (ComparableLiteral<T, V>) NumericLiteral.of(d);
        } else if (value instanceof Byte b) {
            return (ComparableLiteral<T, V>) NumericLiteral.of(b);
        } else if (value instanceof Short s) {
            return (ComparableLiteral<T, V>) NumericLiteral.of(s);
        } else if (value instanceof BigInteger i) {
            return (ComparableLiteral<T, V>) NumericLiteral.of(i);
        } else if (value instanceof BigDecimal d) {
            return (ComparableLiteral<T, V>) NumericLiteral.of(d);
        } else if (value instanceof Instant i) {
            return (ComparableLiteral<T, V>) TemporalLiteral.of(i);
        } else if (value instanceof LocalDate d) {
            return (ComparableLiteral<T, V>) TemporalLiteral.of(d);
        } else if (value instanceof LocalDateTime d) {
            return (ComparableLiteral<T, V>) TemporalLiteral.of(d);
        } else if (value instanceof LocalTime t) {
            return (ComparableLiteral<T, V>) TemporalLiteral.of(t);
        } else {
            return new ComparableLiteralRecord<>(value);
        }
    }
}
