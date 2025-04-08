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
import jakarta.data.metamodel.NumericExpression;
import jakarta.data.metamodel.TextExpression;

import java.util.List;

public interface NumericFunctionExpression<T, N extends Number & Comparable<N>>
        extends FunctionExpression<T,N>, NumericExpression<T,N> {

    String ABS = "abs";
    String NEG = "neg";
    String LENGTH = "length";

    static <T,N extends Number & Comparable<N>> NumericFunctionExpression<T, N>
    of(String name, TextExpression<? super T> argument) {
        return new NumericFunctionExpressionRecord<>(name, List.of(argument));
    }
    static <T,N extends Number & Comparable<N>> NumericFunctionExpression<T, N>
    of(String name, NumericExpression<? super T,N> argument) {
        return new NumericFunctionExpressionRecord<>(name, List.of(argument));
    }

    @Override
    List<? extends ComparableExpression<? super T,?>> arguments();
}
