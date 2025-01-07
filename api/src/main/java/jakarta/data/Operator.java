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

/**
 * Enum for comparison operators used in query restrictions.
 *
 * <p>Supports logical inversion through {@link #negate()}.</p>
 */
public enum Operator {
    /**
     * Equality comparison.
     */
    EQUAL,

    /**
     * Greater-than comparison.
     */
    GREATER_THAN,

    /**
     * Greater-than-or-equal comparison.
     */
    GREATER_THAN_EQUAL,

    /**
     * Inclusion within a set.
     */
    IN,

    /**
     * Less-than comparison.
     */
    LESS_THAN,

    /**
     * Less-than-or-equal comparison.
     */
    LESS_THAN_EQUAL,

    /**
     * Pattern matching (e.g., strings).
     */
    LIKE,

    /**
     * Inequality comparison.
     */
    NOT_EQUAL,

    /**
     * Exclusion from a set.
     */
    NOT_IN,

    /**
     * Negated pattern matching.
     */
    NOT_LIKE;

    /**
     * Returns the operator that is the negation of this operator.
     *
     * @return the operator that is the negation of this operator.
     */
    Operator negate() {
        return switch (this) {
            case EQUAL -> NOT_EQUAL;
            case GREATER_THAN -> LESS_THAN_EQUAL;
            case GREATER_THAN_EQUAL -> LESS_THAN;
            case IN -> NOT_IN;
            case LESS_THAN -> GREATER_THAN_EQUAL;
            case LESS_THAN_EQUAL -> GREATER_THAN;
            case LIKE -> NOT_LIKE;
            case NOT_EQUAL -> EQUAL;
            case NOT_IN -> IN;
            case NOT_LIKE -> LIKE;
        };
    }
}
