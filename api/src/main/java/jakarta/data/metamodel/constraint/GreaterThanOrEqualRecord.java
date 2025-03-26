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

record GreaterThanOrEqualRecord<T extends Comparable<T>>(T bound, boolean isCaseSensitive)
        implements GreaterThanOrEqual<T> {
    public GreaterThanOrEqualRecord {
        Objects.requireNonNull(bound, "Lower bound must not be null");
    }

    GreaterThanOrEqualRecord(T bound) {
        this(bound, bound instanceof String);
    }

    @Override
    public GreaterThanOrEqual<T> ignoreCase() {
        if (!(bound instanceof String)) {
            throw new UnsupportedOperationException(
                    "Cannot ignore case of a " + bound.getClass().getName() +
                    " typed attribute");
        }
        return new GreaterThanOrEqualRecord<>(bound, false);
    }

    @Override
    public LessThan<T> negate() {
        return new LessThanRecord<>(bound, isCaseSensitive);
    }

    @Override
    public String toString() {
        return bound instanceof String
                ? ">= '" + bound + (isCaseSensitive ? "'" : "' IGNORE CASE")
                : ">= " + bound;    }
}
