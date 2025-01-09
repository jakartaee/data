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
package jakarta.data;

// Internal implementation class.
// The proper way for users to obtain instances is via
// the static metamodel or Restrict.* methods 

import java.util.Objects;

record TextRestrictionRecord<T>(
        String attribute,
        Operator comparison,
        boolean isCaseSensitive,
        boolean isEscaped,
        String value) implements TextRestriction<T> {

    TextRestrictionRecord {
        Objects.requireNonNull(attribute, "Attribute must not be null");
    }

    TextRestrictionRecord(String attributeName, Operator comparison, boolean escaped, String value) {
        this(attributeName, comparison, true, escaped, value);
    }

    TextRestrictionRecord(String attributeName, Operator comparison, String value) {
        this(attributeName, comparison, true, false, value);
    }

    @Override
    public TextRestriction<T> ignoreCase() {
        return new TextRestrictionRecord<>(attribute, comparison, false, isEscaped, value);
    }

    @Override
    public TextRestriction<T> negate() {

        return new TextRestrictionRecord<>(
                attribute,
                comparison.negate(),
                isCaseSensitive,
                isEscaped,
                value);
    }
}
