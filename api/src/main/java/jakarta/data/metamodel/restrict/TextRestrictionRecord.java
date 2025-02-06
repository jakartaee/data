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

    /**
     * Textual representation of a text restriction.
     * For example,
     * <pre>name LIKE_IGNORE_CASE "Jakarta EE %"</pre>
     *
     * @return textual representation of a text restriction.
     */
    @Override
    public String toString() {
        String valueString = value == null ? "null" : value;
        StringBuilder builder = new StringBuilder(
                attribute.length() +
                comparison.name().length() +
                valueString.length() +
                24); // number of additional characters that might be appended
        builder.append(attribute).append(' ')
               .append(comparison.name());
        if (!isCaseSensitive) {
            builder.append("_IGNORE_CASE");
        }
        builder.append(' ');
        if (value == null) {
            builder.append(valueString);
        } else {
            builder.append('"').append(valueString).append('"');
        }
        if (isEscaped) {
            builder.append(" ESCAPED");
        }
        return builder.toString();
    }
}
