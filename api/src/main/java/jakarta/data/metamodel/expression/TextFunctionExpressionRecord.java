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

import jakarta.data.metamodel.ComparableExpression;
import jakarta.data.metamodel.Expression;

import java.util.List;
import java.util.Objects;

record TextFunctionExpressionRecord<T>(String name, List<? extends Expression<? super T,?>> arguments)
        implements TextFunctionExpression<T> {

    TextFunctionExpressionRecord {
        Objects.requireNonNull(name, "Function name is required.");
        Objects.requireNonNull(arguments, "Function arguments is required.");
    }

    @Override
    public int compareTo(ComparableExpression<T, String> other) {
        if (getClass().equals(other.getClass())) {
            TextFunctionExpressionRecord<T> another =
                    (TextFunctionExpressionRecord<T>) other;
            int comp = name.compareTo(another.name);
            if (comp == 0) {
                comp = Integer.compare(arguments.size(), another.arguments.size());
            }
            for (int i = 0; comp == 0 && i < arguments.size(); i++) {
                if (arguments.get(i) instanceof ComparableExpression &&
                    another.arguments.get(i) instanceof ComparableExpression) {
                    comp = ((ComparableExpression) arguments.get(i)).compareTo(
                            (ComparableExpression) another.arguments.get(i));
                } else {
                    throw new UnsupportedOperationException(
                            "Not comparable: " + arguments + ", " + another.arguments);
                }
            }
            return comp;
        } else {
            return getClass().getName().compareTo(other.getClass().getName());
        }
    }
}
