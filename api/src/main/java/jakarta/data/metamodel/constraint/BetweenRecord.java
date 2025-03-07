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

record BetweenRecord<T extends Comparable<T>>(
        T lowerBound,
        T upperBound,
        boolean isCaseSensitive) implements Between<T> {

    public BetweenRecord {
        Objects.requireNonNull(lowerBound);
        Objects.requireNonNull(upperBound);
    }

    BetweenRecord(T lowerBound, T upperBound) {
        this(lowerBound, upperBound, lowerBound instanceof String);
    }

    @Override
    public Between<T> ignoreCase() {
        if (!(lowerBound instanceof String)) {
            throw new UnsupportedOperationException(
                    "Cannot ignore case of a " + lowerBound.getClass().getName() +
                    " typed attribute");
        }
        return new BetweenRecord<>(lowerBound, upperBound, false);
    }

    @Override
    public String toString() {
        return lowerBound instanceof String
                ? "BETWEEN '" + lowerBound + "' AND '" + upperBound + "'" +
                        (isCaseSensitive ? "" : " IGNORE CASE")
                : "BETWEEN " + lowerBound + " AND " + upperBound;
    }

    @Override
    public NotBetween<T> negate() {
        return new NotBetweenRecord<>(lowerBound, upperBound, isCaseSensitive);
    }
}
