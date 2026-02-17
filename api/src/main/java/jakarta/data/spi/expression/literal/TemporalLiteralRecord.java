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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;

import jakarta.data.messages.Messages;

record TemporalLiteralRecord<V extends Temporal & Comparable<? extends Temporal>>
        (V value)
        implements TemporalLiteral<V> {

    TemporalLiteralRecord {
        if (value == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "value"));
        }
    }

    @Override
    public Class<V> type() {
        return (Class<V>) value.getClass();
    }

    @Override
    public String toString() {
        final Temporal temporal =
                value instanceof Instant instant
                        ? instant.atOffset(ZoneOffset.UTC).toLocalDateTime()
                        : value;

        return switch (temporal) {
            case LocalDateTime d ->
                "{ts '" + d.toLocalDate() + ' ' + d.toLocalTime() + "'}";
            case LocalDate d ->
                "{d '" + value + "'}";
            case LocalTime t ->
                "{t '" + value + "'}";
            case Year y ->
                "{d '" + y.getValue() + "'}";
            default ->
                "{TemporalLiteral '"
                        + value.getClass().getName() + " '"
                        + value + "'}";
        };
    }

}
