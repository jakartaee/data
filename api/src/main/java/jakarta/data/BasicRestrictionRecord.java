/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

record BasicRestrictionRecord<T>(
        String field,
        boolean isNegated,
        Operator comparison,
        Object value) implements BasicRestriction<T> {

    BasicRestrictionRecord {
        Objects.requireNonNull(field, "Field must not be null");
    }

    BasicRestrictionRecord(String field, Operator comparison, Object value) {
        this(field, false, comparison, value);
    }

    @Override
    public Restriction<T> negate() {
        boolean newNegation = isNegated;
        Operator newComparison;
        switch (comparison) {
            case GREATER_THAN:
                newComparison = Operator.LESS_THAN_EQUAL;
                break;
            case GREATER_THAN_EQUAL:
                newComparison = Operator.LESS_THAN;
                break;
            case LESS_THAN:
                newComparison = Operator.GREATER_THAN_EQUAL;
                break;
            case LESS_THAN_EQUAL:
                newComparison = Operator.GREATER_THAN;
                break;
            default:
                newComparison = comparison;
                newNegation = !isNegated;
        }

        return new BasicRestrictionRecord<>(
                field,
                newNegation,
                newComparison,
                value);
    }
}
