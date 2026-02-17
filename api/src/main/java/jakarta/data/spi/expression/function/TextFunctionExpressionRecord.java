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
package jakarta.data.spi.expression.function;

import jakarta.data.expression.ComparableExpression;
import jakarta.data.messages.Messages;
import jakarta.data.spi.expression.literal.NumericLiteral;

import java.util.List;

record TextFunctionExpressionRecord<T>(
        String name,
        List<ComparableExpression<? super T, ?>> arguments)
        implements TextFunctionExpression<T> {

    TextFunctionExpressionRecord {
        if (name == null) {
            throw new NullPointerException(
                Messages.get("001.arg.required", "name"));
        }

        if (TextFunctionExpression.LEFT == name ||
            TextFunctionExpression.RIGHT == name) {
            ComparableExpression<? super T, ?> sizeExp = arguments.get(1);
            if (sizeExp instanceof NumericLiteral sizeLiteral &&
                sizeLiteral.value() instanceof Integer size &&
                size < 0) {
                throw new IllegalArgumentException(
                        Messages.get("004.arg.negative", "length"));
            }
        }
    }

    @Override
    public Class<String> type() {
        return String.class;
    }

    @Override
    public String toString() {
        StringBuilder function =
                new StringBuilder(name.length() + 2 + 50 * arguments.size());
        function.append(name).append('(');
        boolean first = true;
        for (ComparableExpression<? super T, ?> arg : arguments) {
            if (first) {
                first = false;
            } else {
                function.append(", ");
            }
            function.append(arg);
        }
        function.append(')');

        return function.toString();
    }
}
