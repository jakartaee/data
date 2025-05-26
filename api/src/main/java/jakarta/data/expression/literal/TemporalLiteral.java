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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;

import jakarta.data.expression.TemporalExpression;
import jakarta.data.metamodel.TemporalAttribute;

/**
 * <p>A {@linkplain Literal literal} for a
 * {@linkplain TemporalAttribute temporal} value.</p>
 *
 * @param <V> entity attribute type.
 * @since 1.1
 */
public interface TemporalLiteral<V extends Temporal & Comparable<? extends Temporal>>
        extends ComparableLiteral<V>, TemporalExpression<Object, V> {

    /**
     * <p>Creates a {@code TemporalLiteral} that represents the given value.
     * </p>
     *
     * @param <V>   entity attribute type.
     * @param value an immutable temporal value. Must never be {@code null}.
     * @return a {@code TemporalLiteral} representing the value.
     * @throws NullPointerException if the value is {@code null}.
     */
    static <V extends Temporal & Comparable<? extends Temporal>> TemporalLiteral<V>
    of(V value) {
        return new TemporalLiteralRecord<>(value);
    }

    // TODO Is there a possibility that other temporal types might be added in
    // the future that might also need to use the format of
    // timestamp/date/time? If so, we should state that the output for other
    // types is currently undefined so it can be more completely defined in the
    // future.
    /**
     * <p>Returns a {@code String} representing the literal temporal value. The
     * {@code String} begins with an opening curly brace and ends with a
     * closing curly brace. Between the braces are 2 or 3 terms delimited by a
     * space character. The first term is</p>
     * <ul>
     * <li>{@code ts} for {@link Instant} and {@link LocalDateTime},</li>
     * <li>{@code d} for {@link LocalDate},</li>
     * <li>{@code t} for {@link LocalTime}, or</li>
     * <li>{@code TemporalLiteral} for all other types, in which case a middle
     * term is included. The middle term is the fully qualified class name of
     * the value's type.</li>
     * </ul>
     *
     * <p>For {@link Instant}, the final term is the
     * {@link LocalDateTime#toLocalDate() LocalDate} and
     * {@link LocalDateTime#toLocalTime() LocalTime} components of the
     * {@code Instant} in {@link ZoneOffset#UTC UTC}, delimited by a space
     * character and enclosed in single quotes.</p>
     *
     * <p>For {@link LocalDateTime}, the final term is the
     * {@link LocalDateTime#toLocalDate() LocalDate} and
     * {@link LocalDateTime#toLocalTime() LocalTime} components delimited by a
     * space character and enclosed in single quotes.</p>
     *
     * <p>For all other types, the final term is the {@code toString()} output
     * of the value enclosed in single quotes.</p>
     *
     * <p>For example, the output of
     * {@code TemporalLiteral.of(LocalDate.of(2025, 5, 8)).toString()} is</p>
     * <pre>
     * {d '2025-05-08'}
     * </pre>
     *
     * <p>The output of
     * {@code TemporalLiteral.of(LocalDateTime.of(2025, 5, 9, 16, 25, 52)).toString()}
     * is</p>
     * <pre>
     * {ts '2025-05-09 16:25:52'}
     * </pre>
     *
     * @return a {@code String} representing the literal temporal value.
     */
    @Override
    public String toString();
}
