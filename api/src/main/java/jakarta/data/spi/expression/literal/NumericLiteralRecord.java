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

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.data.messages.Messages;

record NumericLiteralRecord<N extends Number & Comparable<N>>
        (Class<? extends N> type, N value)
        implements NumericLiteral<N> {

    NumericLiteralRecord {
        Messages.requireNonNull(type, "type");
        Messages.requireNonNull(value, "value");
    }

    @Override
    public String toString() {
        if (value instanceof Long l) {
            return l + "L";
        } else if (value instanceof Integer i) {
            return i.toString();
        } else if (value instanceof Double d) {
            return d + "D";
        } else if (value instanceof Float f) {
            return f + "F";
        } else if (value instanceof BigDecimal b) {
            return b + "BD";
        } else if (value instanceof BigInteger b) {
            return b + "BI";
        } else {
            return "{NumericLiteral " +
                   value.getClass().getName() +
                   " '" + value + "'}";
        }
    }
}
