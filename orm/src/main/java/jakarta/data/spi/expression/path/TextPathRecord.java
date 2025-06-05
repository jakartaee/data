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
package jakarta.data.spi.expression.path;

import java.util.Objects;

import jakarta.data.expression.NavigableExpression;
import jakarta.data.metamodel.TextAttribute;

record TextPathRecord<T, U>
        (NavigableExpression<T, U> expression, TextAttribute<U> attribute)
        implements TextPath<T, U> {

    TextPathRecord {
        Objects.requireNonNull(expression, "The expression is required");
        Objects.requireNonNull(attribute, "The attribute is required");
    }

    @Override
    public String toString() {
        String expr = expression.toString();
        String attrName = attribute.name();
        StringBuilder path =
                new StringBuilder(expr.length() + 1 + attrName.length());
        path.append(expr).append('.').append(attrName);
        return path.toString();
    }
}
