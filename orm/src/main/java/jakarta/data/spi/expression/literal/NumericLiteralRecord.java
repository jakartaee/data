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

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.data.messages.Messages;

record NumericLiteralRecord<N extends Number & Comparable<N>>
        (N value)
        implements NumericLiteral<N> {

    NumericLiteralRecord {
        if (value == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "value"));
        }
    }

    @Override
    public String toString() {
        return switch (value) {
            case Integer i -> i.toString();
            case Long l -> l + "L";
            case Float f -> f + "F";
            case Double d -> d + "D";
            case BigInteger i -> i + "BI";
            case BigDecimal d -> d + "BD";
            default -> "{NumericLiteral "
                          + value.getClass().getName()
                          + " '" + value + "'}";
        };
    }
}
