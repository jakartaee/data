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
 * Represents a condition or constraint applied to a query on a specific field with a comparison operator and value.
 * The {@code BasicRestriction} interface enables the construction of detailed query restrictions,
 * allowing for the retrieval of field-specific conditions, the associated operator, and its value.
 * It is commonly used in dynamic query building and fluent API scenarios to express query constraints concisely.
 *
 * @param <T> the type of the field or entity attribute being restricted
 */
public interface BasicRestriction<T> extends Restriction<T> {

    /**
     * Retrieves the comparison operator used in this restriction.
     * <p>
     * The operator defines how the field and value are compared, such as EQUAL, GREATER_THAN, or LESS_THAN.
     * </p>
     *
     * @return the operator representing the type of comparison
     */
    Operator comparison();

    /**
     * Retrieves the name of the field to which this restriction is applied.
     * <p>
     * This allows access to the specific attribute of the entity that is being constrained.
     * </p>
     *
     * @return the name of the field as a {@code String}
     */
    String field();

    /**
     * Returns a negated version of this restriction.
     * <p>
     * Negating a restriction inverts its logical meaning. For instance, a restriction
     * such as "age > 18" would be negated to "age <= 18." This is useful for dynamically
     * creating logical complements of existing conditions in complex queries.
     * </p>
     *
     * @return a new {@code BasicRestriction} representing the negated condition
     */
    BasicRestriction<T> negate();

    /**
     * Retrieves the value used in this restriction.
     * <p>
     * This value is compared with the field using the specified operator. For example,
     * in the restriction "price > 100," the value would be {@code 100}.
     * </p>
     *
     * @return the value associated with this restriction
     */
    Object value();
}
