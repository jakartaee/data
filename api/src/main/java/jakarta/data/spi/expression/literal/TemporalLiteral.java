/*
 * Copyright (c) 2025,2026 Contributors to the Eclipse Foundation
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;

import jakarta.data.expression.TemporalExpression;
import jakarta.data.metamodel.TemporalAttribute;
import jakarta.annotation.Nonnull;

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
    @Nonnull
    static <V extends Temporal & Comparable<? extends Temporal>> TemporalLiteral<V>
    of(@Nonnull Class<V> type, @Nonnull V value) {
        return new TemporalLiteralRecord<>(type, value);
    }

    /**
     * Create a {@code TemporalLiteral} representing the given {@link Instant}.
     */
    @Nonnull
    static TemporalLiteral<Instant> of(@Nonnull Instant value) {
        return of(Instant.class, value);
    }

    /**
     * Create a {@code TemporalLiteral} representing the given {@link LocalDateTime}.
     */
    @Nonnull
    static TemporalLiteral<LocalDateTime> of(@Nonnull LocalDateTime value) {
        return of(LocalDateTime.class, value);
    }

    /**
     * Create a {@code TemporalLiteral} representing the given {@link LocalDate}.
     */
    @Nonnull
    static TemporalLiteral<LocalDate> of(@Nonnull LocalDate value) {
        return of(LocalDate.class, value);
    }

    /**
     * Create a {@code TemporalLiteral} representing the given {@link LocalTime}.
     */
    @Nonnull
    static TemporalLiteral<LocalTime> of(@Nonnull LocalTime value) {
        return of(LocalTime.class, value);
    }

    /**
     * Create a {@code TemporalLiteral} representing the given {@link Year}.
     */
    @Nonnull
    static TemporalLiteral<Year> of(@Nonnull Year value) {
        return of(Year.class, value);
    }

    /**
     * <p>
     * Returns a {@code String} representing the literal temporal value.
     *
     * <h2>Instant</h2>
     * <p>
     * An {@link Instant}-typed value is converted to a {@link LocalDateTime}
     * value in {@link ZoneOffset#UTC UTC}, and subsequently follows the
     * pattern for {@code LocalDateTime}.
     *
     * <h2>LocalDateTime</h2>
     * <p>
     * A {@link LocalDateTime}-typed value is converted to {@code DATETIME},
     * followed by the space character, followed by the concatenation of the
     * 3 terms of the {@link LocalDateTime#toLocalDate() LocalDate} delimited
     * by the {@code -} character according to the pattern for
     * {@code LocalDate}, followed by the space character, followed by the
     * concatenation of 2 or 3 terms of the
     * {@link LocalDateTime#toLocalTime() LocalTime} delimited by the {@code :}
     * character according to the pattern for {@code LocalTime}. For example,
     * {@code DATETIME 2025-05-09 16:25:02}.
     *
     * <h2>LocalDate</h2>
     * <p>
     * A {@link LocalDate}-typed value is converted to {@code DATE},
     * followed by the space character, followed by the concatenation of 3
     * terms delimited by the {@code -} character:
     * {@linkplain LocalDate#getYear() year} as a minimum of 4 digits, the
     * {@linkplain LocalDate#getMonthValue() month value from 01 to 12} as 2
     * digits, and the
     * {@linkplain LocalDate#getDayOfMonth() day of month from 01 to 31} as
     * 2 digits, following after {@link LocalDate#toString()}. For example,
     * {@code DATE 2024-06-10}
     *
     * <h2>LocalTime</h2>
     * <p>
     * A {@link LocalTime}-typed value is converted to {@code TIME},
     * followed by the space character, followed by the concatenation of 2 or 3
     * terms delimited by the {@code :} character:
     * {@linkplain LocalTime#getHour() hour from 00 to 23} as 2 digits, the
     * {@linkplain LocalTime#getMinute() minute from 00 to 59} as 2 digits,
     * optionally followed by the
     * {@linkplain LocalTime#getSecond() second from 00 to 59} as 2 digits
     * and possibly the {@code .} character followed by fractional seconds
     * according to the pattern defined by {@link LocalTime#toString()}.
     * For example, {@code TIME 08:30:05} or {@code TIME 16:00:01.875}.
     *
     * <h2>Year</h2>
     * <p>
     * A {@link Year}-typed value is converted to {@code YEAR}, followed by
     * the space character, followed by the
     * {@linkplain Year#getValue() year value}. For example, {@code YEAR 2026}.
     *
     * <h2>Other temporal types</h2>
     * <p>
     * Do not rely on the format of {@code toString} for other temporal types.
     * Jakarta Data does not define a format, but might do in a future release
     * where additional temporal types are added to the specification.
     *
     * @return a {@code String} representing the literal temporal value.
     */
    @Override
    @Nonnull
    String toString();
}
