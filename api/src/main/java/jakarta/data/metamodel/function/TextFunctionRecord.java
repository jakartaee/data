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
package jakarta.data.metamodel.function;

import jakarta.data.metamodel.Expression;
import jakarta.data.metamodel.TextExpression;

import java.util.List;

public record TextFunctionRecord<T>(String name, List<? extends Expression<T,?>> arguments)
        implements TextFunction<T> {
    public TextFunctionRecord(String name, TextExpression<T> argument) {
        this(name, List.of(argument));
    }
    public TextFunctionRecord(String name, Expression<T,String> left, String right) {
        this(name, List.of(left, new LiteralRecord<>(right)));
    }
    public TextFunctionRecord(String name, String left, Expression<T,String> right) {
        this(name, List.of(new LiteralRecord<>(left), right));
    }
    public TextFunctionRecord(String name, Expression<T,String> left, Expression<T,String> right) {
        this(name, List.of(left, right));
    }
    public TextFunctionRecord(String name, Expression<T,String> left, int literal) {
        this(name, List.of(left, new LiteralRecord<>(literal)));
    }
}
