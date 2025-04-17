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

import jakarta.data.metamodel.NumericExpression;

record NumericOperatorExpressionRecord<T, N extends Number & Comparable<N>>
        (Operator operator, NumericExpression<T,N> left, NumericExpression<T,N> right)
        implements NumericOperatorExpression<T,N> {

    NumericOperatorExpressionRecord {
        Objects.requireNonNull(operator, "Operator is required.");
        Objects.requireNonNull(left, "Left side expression is required.");
        Objects.requireNonNull(left, "Right side expression is required.");
    }

}
