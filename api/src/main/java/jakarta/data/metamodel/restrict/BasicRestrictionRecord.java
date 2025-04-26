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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.metamodel.restrict;

// Internal implementation class.
// The proper way for users to obtain instances is via
// the static metamodel or Restrict.* methods 

import jakarta.data.metamodel.Attribute;
import jakarta.data.metamodel.Expression;
import jakarta.data.metamodel.constraint.Constraint;

import java.util.Objects;

record BasicRestrictionRecord<T, V>(Expression<T, V> expression,
                                    Constraint<V> constraint)
        implements BasicRestriction<T, V> {

    BasicRestrictionRecord {
        Objects.requireNonNull(expression, "The expression is required");
        Objects.requireNonNull(constraint, "The constraint is required");
    }

    @Override
    public BasicRestriction<T, V> negate() {
        return BasicRestriction.of(expression, constraint.negate());
    }

    /**
     * Textual representation of a basic restriction.
     * For example,
     * <pre>price < 50.0</pre>
     *
     * @return textual representation of a basic restriction.
     */
    @Override
    public String toString() {
        return (expression instanceof Attribute<?> att ? att.name() : expression.toString())
                + " " + constraint;
    }
}
