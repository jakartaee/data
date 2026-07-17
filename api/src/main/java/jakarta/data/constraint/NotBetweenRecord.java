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
package jakarta.data.constraint;


import jakarta.data.expression.ComparableExpression;
import jakarta.data.messages.Messages;
import jakarta.annotation.Nonnull;
record NotBetweenRecord<V extends Comparable<?>>(
        @Nonnull ComparableExpression<?, V> lowerBound,
        @Nonnull ComparableExpression<?, V> upperBound)
        implements NotBetween<V> {

    NotBetweenRecord {
        if (lowerBound == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "lower"));
        }
        if (upperBound == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "upper"));
        }
    }

    @Override
    @Nonnull
    public Between<V> negate() {
        return Between.bounds(lowerBound, upperBound);
    }

    @Override
    @Nonnull
    public String toString() {
        return "NOT BETWEEN " + lowerBound + " AND " + upperBound;
    }
}
