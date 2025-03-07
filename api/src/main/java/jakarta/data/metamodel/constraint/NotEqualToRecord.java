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
package jakarta.data.metamodel.constraint;

import java.util.Objects;

record NotEqualToRecord<T>(T value, boolean isCaseSensitive) implements NotEqualTo<T> {

    NotEqualToRecord {
        Objects.requireNonNull(value, "value must not be null");
    }

    NotEqualToRecord(T value) {
        this(value, value instanceof String);
    }

    @Override
    public NotEqualTo<T> ignoreCase() {
        if (!(value instanceof String)) {
            throw new UnsupportedOperationException(
                   "Cannot ignore case of a " + value.getClass().getName() +
                    " typed attribute");
        }
        return new NotEqualToRecord<>(value, false);
    }

    @Override
    public EqualTo<T> negate() {
        return new EqualToRecord<>(value, isCaseSensitive);
    }

    @Override
    public String toString() {
        return value instanceof String
                ? "<> '" + value + (isCaseSensitive ? "'" : "' IGNORE CASE")
                : "<> " + value;
    }
}
