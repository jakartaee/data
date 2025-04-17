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
        Objects.requireNonNull(value, "Value is required.");
    }

    @Override
    public String toString() {
        // TODO can use a switch statement after updating to Java 21
        if (value instanceof Instant ||
            value instanceof LocalDateTime) {
            return "TIMESTAMP('" + value + "')";
        } else if (value instanceof LocalDate) {
            return "DATE('" + value + "')";
        } else if (value instanceof LocalTime) {
            return "TIME('" + value + "')";
        } else {
            return value.toString();
        }
    }

}
