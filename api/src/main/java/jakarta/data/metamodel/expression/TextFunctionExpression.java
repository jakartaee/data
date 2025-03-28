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

import jakarta.data.metamodel.TextExpression;

import java.util.List;

public interface TextFunctionExpression<T>
        extends FunctionExpression<T,String>, TextExpression<T> {
    static <T> TextFunctionExpression<T> of(String name, TextExpression<T> argument) {
        return new TextFunctionExpressionRecord<>(name, List.of(argument));
    }
    static <T> TextFunctionExpression<T> of(String name, TextExpression<T> left, String right) {
        return new TextFunctionExpressionRecord<>(name, List.of(left, StringLiteral.of(right)));
    }
    static <T> TextFunctionExpression<T> of(String name, String left, TextExpression<T> right) {
        return new TextFunctionExpressionRecord<>(name, List.of(StringLiteral.of(left), right));
    }
    static <T> TextFunctionExpression<T> of(String name, TextExpression<T> left, TextExpression<T> right) {
        return new TextFunctionExpressionRecord<>(name, List.of(left, right));
    }
    static <T> TextFunctionExpression<T> of(String name, TextExpression<T> left, int literal) {
        return new TextFunctionExpressionRecord<>(name, List.of(left, NumericLiteral.of(literal)));
    }
}
