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

import jakarta.data.messages.Messages;

record ComparableLiteralRecord<T, V extends Comparable<?>>(V value)
        implements ComparableLiteral<T, V> {

    ComparableLiteralRecord {
        if (value == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "value"));
        }
    }

    @Override
    public String toString() {
        return switch (value) {
            case Boolean b -> b ? "TRUE" : "FALSE";
            case Character c -> c == '\'' ? "''''" : "'" + c + "'";
            case Enum<?> e -> e.getClass().getName() + '.' + e.name();
            default -> "{ComparableLiteral "
                        + value.getClass().getName()
                        + " '" + value + "'}";
        };
    }

}
