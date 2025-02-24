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

record NotBetweenRecord<T extends Comparable<T>>(T lowerBound, T upperBound)
    implements NotBetween<T> {

    NotBetweenRecord {
        Objects.requireNonNull(lowerBound, "lowerBound must not be null");
        Objects.requireNonNull(upperBound, "upperBound must not be null");
    }

    @Override
    public Between<T> negate() {
        return Between.bounds(lowerBound, upperBound);
    }

    @Override
    public String toString() {
        return lowerBound instanceof String
                ? "NOT BETWEEN '" + lowerBound + "' AND '" + upperBound + "'"
                : "NOT BETWEEN " + lowerBound + " AND " + upperBound;
    }
}
