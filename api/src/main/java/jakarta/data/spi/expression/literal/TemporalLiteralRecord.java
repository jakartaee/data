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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;

import jakarta.data.messages.Messages;
import jakarta.annotation.Nonnull;

record TemporalLiteralRecord<V extends Temporal & Comparable<? extends Temporal>>
        (@Nonnull Class<V> type, @Nonnull V value)
        implements TemporalLiteral<V> {

    TemporalLiteralRecord {
        Messages.requireNonNull(type, "type");
        Messages.requireNonNull(value, "value");
    }

    @Override
    @Nonnull
    public String toString() {
        final Temporal temporal =
                value instanceof Instant instant
                        ? instant.atOffset(ZoneOffset.UTC).toLocalDateTime()
                        : value;

        if (temporal instanceof LocalDateTime d) {
            String dateString = d.getYear() >= 10000
                    ? d.toLocalDate().toString().substring(1) // omit leading +
                    : d.toLocalDate().toString();
            return "DATETIME " + dateString + ' ' + d.toLocalTime().toString();
        } else if (temporal instanceof LocalDate d) {
            return "DATE " + (d.getYear() >= 10000
                                ? d.toString().substring(1) // omit leading +
                                : d.toString());
        } else if (temporal instanceof LocalTime) {
            return "TIME " + temporal.toString();
        } else if (temporal instanceof Year y) {
            return "YEAR " + y.getValue();
        } else {
            return "TEMPORAL " + temporal.getClass().getName() + " " + temporal;
        }
    }

}
