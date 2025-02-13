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

record BasicRestrictionRecord<T>(
        String attribute,
        Operator comparison,
        Object value) implements BasicRestriction<T> {

    BasicRestrictionRecord {
        Objects.requireNonNull(attribute, "Attribute must not be null");
        Objects.requireNonNull(value, "Value must not be null");
    }

    @Override
    public BasicRestriction<T> negate() {
        return new BasicRestrictionRecord<>(
                attribute,
                comparison.negate(),
                value);
    }

    /**
     * Textual representation of a basic restriction.
     * For example,
     * <pre>price LESS_THAN 50.0</pre>
     *
     * @return textual representation of a basic restriction.
     */
    @Override
    public String toString() {
        String valueString = value.toString();
        StringBuilder builder = new StringBuilder(
                attribute.length() +
                comparison.name().length() +
                valueString.length() +
                4); // number of additional characters that might be appended
        builder.append(attribute).append(' ')
         .append(comparison.name()).append(' ');
        if (value instanceof CharSequence) {
            builder.append('"').append(valueString).append('"');
        } else {
            builder.append(valueString);
        }
        return builder.toString();
    }
}
