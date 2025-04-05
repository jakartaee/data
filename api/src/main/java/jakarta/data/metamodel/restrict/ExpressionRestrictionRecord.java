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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.metamodel.restrict;

import jakarta.data.metamodel.Expression;
import jakarta.data.metamodel.constraint.Constraint;

import java.util.Objects;

/**
 * Internal implementation class.
 * The proper way for users to obtain instances is via the static metamodel
 */
record ExpressionRestrictionRecord<T, U extends Expression<T,V>, V>(
        Expression<T,V> expression,
        Constraint<U> constraint)
        implements ExpressionRestriction<T,U,V> {

    ExpressionRestrictionRecord {
        Objects.requireNonNull(expression, "Expression is required.");
        Objects.requireNonNull(constraint, "Constraint is required.");
    }

    @Override
    public ExpressionRestriction<T,U,V> negate() {
        return ExpressionRestriction.of(expression, constraint.negate());
    }

    // TODO toString involving expressions
}
