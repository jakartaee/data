/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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

import jakarta.data.expression.BooleanExpression;
import jakarta.data.expression.NavigableExpression;
import jakarta.data.metamodel.BooleanAttribute;

public interface BooleanPath<T, U>
        extends Path<T, U>,
                BooleanExpression<T> {

    static <T, U> BooleanPath<T, U> of(NavigableExpression<T, U> expression,
                                       BooleanAttribute<U> attribute) {

        return new BooleanPathRecord<>(expression, attribute);
    }
}
