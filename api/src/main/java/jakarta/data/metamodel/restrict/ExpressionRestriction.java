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
package jakarta.data.metamodel.restrict;

import jakarta.data.metamodel.Expression;
import jakarta.data.metamodel.constraint.Constraint;

public interface ExpressionRestriction<T, U extends Expression<T,V>, V>
        extends Restriction<T> {

    @Override
    ExpressionRestriction<T,U,V> negate();

    Expression<T,V> expression();

    Constraint<U> constraint();

    static <T, U extends Expression<T,V>, V> ExpressionRestriction<T,U,V> of(
            Expression<T,V> expression,
            Constraint<U> constraint) {
        return new ExpressionRestrictionRecord<>(expression, constraint);
    }
}
