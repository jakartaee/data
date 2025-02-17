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

import jakarta.data.metamodel.range.TextRange;

import java.util.Objects;

record TextRestrictionRecord<T>(String attribute, TextRange range, boolean negated)
        implements TextRestriction<T> {

    TextRestrictionRecord {
        Objects.requireNonNull(attribute, "Attribute must not be null");
        Objects.requireNonNull(range, "Pattern must not be null");
    }

    public TextRestrictionRecord(String attribute, TextRange range) {
        this(attribute, range, false);
    }

    @Override
    public Operator comparison() {
        return negated ? range.operator().negate() : range.operator();
    }

    @Override
    public TextRestrictionRecord<T> negate() {
        return new TextRestrictionRecord<>(attribute, range, !negated);
    }

    @Override
    public TextRestrictionRecord<T> ignoreCase() {
        return new TextRestrictionRecord<>(attribute, range.ignoreCase(), negated);
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
        return attribute + ' ' + comparison().asQueryLanguage() + ' ' + range;
    }
}
