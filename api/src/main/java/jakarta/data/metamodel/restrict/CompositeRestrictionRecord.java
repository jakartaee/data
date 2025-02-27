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

import java.util.List;

import jakarta.data.metamodel.restrict.CompositeRestriction.Type;

// Internal implementation class.
// The proper way for users to obtain instances is via
// the Restrict.any(...) or Restrict.all(...) methods

record CompositeRestrictionRecord<T>(
        Type type,
        List<Restriction<T>> restrictions,
        boolean isNegated) implements CompositeRestriction<T> {

    /**
     * Initial guess of length per restriction that is being combined.
     */
    private static final int SINGLE_RESTRICTION_LENGTH_ESTIMATE = 100;

    CompositeRestrictionRecord {
        if (restrictions == null || restrictions.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot create a composite restriction without any restrictions to combine.");
        }
    }

    CompositeRestrictionRecord(Type type, List<Restriction<T>> restrictions) {
        this(type, restrictions, false);
    }

    @Override
    public CompositeRestriction<T> negate() {
        return new CompositeRestrictionRecord<>(type, restrictions, !isNegated);
    }

    /**
     * Textual representation of a composite restriction.
     * For example,
     * <pre>(price < 50.0) AND (name LIKE "%Jakarta EE%")</pre>
     *
     * @return textual representation of a composite restriction.
     */
    @Override
    public String toString() {
        String logicalOperator = type.asQueryLanguage();
        StringBuilder builder = new StringBuilder(
                restrictions.size() * SINGLE_RESTRICTION_LENGTH_ESTIMATE +
                6); // number of additional characters that might be appended
        if (isNegated) {
            builder.append("NOT (");
        }

        boolean first = true;
        for (Restriction<T> restriction : restrictions) {
            if (first) {
                first = false;
            } else {
                builder.append(' ').append(logicalOperator).append(' ');
            }
            builder.append('(').append(restriction).append(')');
        }

        if (isNegated) {
            builder.append(')');
        }
        return builder.toString();
    }
}