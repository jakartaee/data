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
import java.util.Objects;

record NumericLiteralRecord<T, N extends Number & Comparable<N>>
        (N value) implements NumericLiteral<T,N> {

    NumericLiteralRecord {
        Objects.requireNonNull(value, "Value is required.");
    }

    @Override
    public String toString() {
        // TODO can use switch after compilation is switched to Java 21
        String suffix;
        if (value instanceof Long) {
            suffix = "L";
        } else if (value instanceof Float) {
            suffix = "F";
        } else if (value instanceof Double) {
            suffix = "D";
        } else if (value instanceof BigInteger) {
            suffix = "BI";
        } else if (value instanceof BigDecimal) {
            suffix = "BD";
        } else {
            suffix = "";
        }
        return value.toString() + suffix;
    }
}
