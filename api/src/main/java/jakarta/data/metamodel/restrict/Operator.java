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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.metamodel.restrict;

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
    NOT_LIKE,
    NOT_PREFIXED,
    NOT_SUBSTRINGED,
    NOT_SUFFIXED,
    PREFIXED,
    SUBSTRINGED,
    SUFFIXED;

    /**
     * Representation of the operator as it appears in query language.
     * For example, {@link #GREATER_THAN} is represented as {@code >}
     * in query langugae.
     *
     * @return the representation of the operator in query language.
     *         For operators that have a more complex representation in
     *         query language, this method returns the {@link #name()}
     *         of the operator.
     */
    String asQueryLanguage() {
        return switch (this) {
            case EQUAL -> "=";
            case GREATER_THAN -> ">";
            case GREATER_THAN_EQUAL -> ">=";
            case IN -> "IN";
            case LESS_THAN -> "<";
            case LESS_THAN_EQUAL -> "<=";
            case LIKE -> "LIKE";
            case NOT_EQUAL -> "<>";
            case NOT_IN -> "NOT IN";
            case NOT_LIKE -> "NOT LIKE";
            case NOT_PREFIXED -> NOT_PREFIXED.name();
            case NOT_SUBSTRINGED -> NOT_SUBSTRINGED.name();
            case NOT_SUFFIXED -> NOT_SUFFIXED.name();
            case PREFIXED -> PREFIXED.name();
            case SUBSTRINGED -> SUBSTRINGED.name();
            case SUFFIXED -> SUFFIXED.name();
        };
    }

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
            case NOT_PREFIXED -> PREFIXED;
            case NOT_SUBSTRINGED -> SUBSTRINGED;
            case NOT_SUFFIXED -> SUFFIXED;
            case PREFIXED -> NOT_PREFIXED;
            case SUBSTRINGED -> NOT_SUBSTRINGED;
            case SUFFIXED -> NOT_SUFFIXED;
        };
    }
}
