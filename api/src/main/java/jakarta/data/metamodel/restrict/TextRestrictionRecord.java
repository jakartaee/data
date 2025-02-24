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

import jakarta.data.metamodel.constraint.Like;

import java.util.Objects;

record TextRestrictionRecord<T>(String attribute, Like constraint, boolean negated)
        implements TextRestriction<T> {

    TextRestrictionRecord {
        Objects.requireNonNull(attribute, "Attribute must not be null");
        Objects.requireNonNull(constraint, "Constraint must not be null");
    }

    public TextRestrictionRecord(String attribute, Like range) {
        this(attribute, range, false);
    }

    @Override
    public Operator comparison() {
        return negated ? constraint.operator().negate() : constraint.operator();
    }

    @Override
    public TextRestrictionRecord<T> negate() {
        return new TextRestrictionRecord<>(attribute, constraint, !negated);
    }

    @Override
    public TextRestrictionRecord<T> ignoreCase() {
        return new TextRestrictionRecord<>(attribute, constraint.ignoreCase(), negated);
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
        return attribute + ' ' + constraint;
    }
}
