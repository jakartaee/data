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

import java.util.Objects;

import jakarta.data.metamodel.ComparableExpression;

record StringLiteralRecord<T>(String value)
        implements StringLiteral<T> {

    StringLiteralRecord {
        Objects.requireNonNull(value, "Value is required.");
    }

    @Override
    public int compareTo(ComparableExpression<T, String> other) {
        if (getClass().equals(other.getClass())) {
            return value.compareTo(((StringLiteralRecord<T>) other).value);
        } else {
            return getClass().getName().compareTo(other.getClass().getName());
        }
    }
}
