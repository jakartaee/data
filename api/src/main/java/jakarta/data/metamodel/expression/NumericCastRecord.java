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
import jakarta.data.metamodel.NumericExpression;

record NumericCastRecord<T, N extends Number & Comparable<N>>
        (NumericExpression<T,?> expression, Class<N> type)
        implements NumericCast<T,N> {

    NumericCastRecord {
        Objects.requireNonNull(expression, "Expression is required.");
        Objects.requireNonNull(type, "Attribute type is required.");
    }

    @Override
    public int compareTo(ComparableExpression<T, N> other) {
        if (getClass().equals(other.getClass())) {
            NumericCastRecord<T, N> another = (NumericCastRecord<T,N>) other;
            int comp = type.getName().compareTo(
                    another.type.getName());
            if (comp == 0) {
                comp = expression.compareTo((NumericCastRecord) another.expression);
            }
            return comp;
        } else {
            return getClass().getName().compareTo(other.getClass().getName());
        }
    }
}
