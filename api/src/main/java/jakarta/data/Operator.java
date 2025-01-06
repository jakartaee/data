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
 * Defines comparison operators for constructing query restrictions.
 * These operators are used to specify conditions in queries, such as comparisons,
 * inclusion, exclusion, and pattern matching. Each operator can be dynamically negated
 * using the {@link #negate()} method to create logically inverted conditions,
 * enabling flexible query construction.
 */
public enum Operator {
    /**
     * Tests for equality (e.g., field = value).
     */
    EQUAL,

    /**
     * Tests if a value is greater than the field (e.g., field > value).
     */
    GREATER_THAN,

    /**
     * Tests if a value is greater than or equal to the field (e.g., field >= value).
     */
    GREATER_THAN_EQUAL,

    /**
     * Tests if a field's value is within a set of values (e.g., field IN (values)).
     */
    IN,

    /**
     * Tests if a value is less than the field (e.g., field < value).
     */
    LESS_THAN,

    /**
     * Tests if a value is less than or equal to the field (e.g., field <= value).
     */
    LESS_THAN_EQUAL,

    /**
     * Matches a value against a pattern (e.g., field LIKE value).
     */
    LIKE,

    /**
     * Tests for inequality (e.g., field != value).
     */
    NOT_EQUAL,

    /**
     * Tests if a field's value is not within a set of values (e.g., field NOT IN (values)).
     */
    NOT_IN,

    /**
     * Matches a value against a negated pattern (e.g., field NOT LIKE value).
     */
    NOT_LIKE;

    /**
     * Returns the logical negation of this operator.
     * Negation inverts the operator's meaning. For example:
     * <ul>
     *   <li>{@code EQUAL} becomes {@code NOT_EQUAL}</li>
     *   <li>{@code IN} becomes {@code NOT_IN}</li>
     *   <li>{@code GREATER_THAN} becomes {@code LESS_THAN_EQUAL}</li>
     * </ul>
     * This is useful for dynamically constructing queries with reversed logic.
     *
     * @return the negated operator
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
