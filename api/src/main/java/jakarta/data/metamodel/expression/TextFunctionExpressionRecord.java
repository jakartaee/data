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

import java.util.List;
import java.util.Objects;

record TextFunctionExpressionRecord<T>(
        String name,
        List<ComparableExpression<? super T,?>> arguments)
        implements TextFunctionExpression<T> {

    TextFunctionExpressionRecord {
        Objects.requireNonNull(name, "Function name is required");
        Objects.requireNonNull(arguments, "Function arguments is required");
    }

}
