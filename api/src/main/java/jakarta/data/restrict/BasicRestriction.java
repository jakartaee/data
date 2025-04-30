/*
 * Copyright (c) 2024,2025 Contributors to the Eclipse Foundation
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
package jakarta.data.restrict;

import jakarta.data.constraint.Constraint;
import jakarta.data.expression.Expression;

public interface BasicRestriction<T, V> extends Restriction<T> {

    @Override
    BasicRestriction<T, V> negate();

    Expression<T, V> expression();

    Constraint<V> constraint();

    static <T, V> BasicRestriction<T, V> of(Expression<T, V> expression, Constraint<V> constraint) {
        return new BasicRestrictionRecord<>(expression, constraint);
    }
}
