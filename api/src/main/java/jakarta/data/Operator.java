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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data;

public enum Operator {
    EQUAL,
    GREATER_THAN,
    GREATER_THAN_EQUAL,
    IN,
    LESS_THAN,
    LESS_THAN_EQUAL,
    LIKE,
    NOT_EQUAL,
    NOT_IN,
    NOT_LIKE;

    /**
     * Returns the operator that is the negation of this operator.
     *
     * @return the operator that is the negation of this operator.
     */
    Operator negate() {
        switch (this) {
            case EQUAL:
                return NOT_EQUAL;
            case GREATER_THAN:
                return LESS_THAN_EQUAL;
            case GREATER_THAN_EQUAL:
                return LESS_THAN;
            case IN:
                return NOT_IN;
            case LESS_THAN:
                return GREATER_THAN_EQUAL;
            case LESS_THAN_EQUAL:
                return GREATER_THAN;
            case LIKE:
                return NOT_LIKE;
            case NOT_EQUAL:
                return EQUAL;
            case NOT_IN:
                return IN;
            case NOT_LIKE:
                return LIKE;
            default: // should be unreachable
                throw new IllegalStateException(name());
        }
    }
}
