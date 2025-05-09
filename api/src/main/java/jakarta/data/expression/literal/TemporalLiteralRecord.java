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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Objects;

record TemporalLiteralRecord<T, V extends Temporal & Comparable<? extends Temporal>>(
        V value)
        implements TemporalLiteral<T, V> {

    TemporalLiteralRecord {
        Objects.requireNonNull(value, "The value is required");
    }

    @Override
    public String toString() {
        return switch (value) {
            case Instant i       -> "TIMESTAMP('" + i + "')";
            case LocalDateTime d -> "TIMESTAMP('" + d + "')";
            case LocalDate d     -> "DATE('" + d + "')";
            case LocalTime t     -> "TIME('" + t + "')";
            default              -> value.toString();
        };
    }

}
