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
 * Represents a specific condition or restriction applied to a query.
 * A {@code BasicRestriction} encapsulates a comparison operation ({@link Operator}),
 * a field name, and a value. It is used to define precise query filters or criteria
 * that target specific fields and values within a dataset. Restrictions can also
 * be negated to dynamically adjust query logic.
 * Example usage:
 * <pre>{@code
 * BasicRestriction<Person> restriction = ...
 * String field = restriction.field(); // e.g., "name"
 * Operator operator = restriction.comparison(); // e.g., EQUAL
 * Object value = restriction.value(); // e.g., "John"
 * }</pre>
 *
 * @param <T> the type of the value being restricted
 */
public interface BasicRestriction<T> extends Restriction<T> {

    /**
     * Returns the comparison operator associated with this restriction.
     * <p>
     * The operator defines the logical relationship between the field and the value,
     * such as equality, greater than, less than, or inclusion in a set.
     * For example:
     * <ul>
     *   <li>{@link Operator#EQUAL} for equality (e.g., {@code field = value})</li>
     *   <li>{@link Operator#IN} for inclusion in a set (e.g., {@code field IN (values)})</li>
     * </ul>
     * </p>
     *
     * @return the {@link Operator} for this restriction
     */
    Operator comparison();

    /**
     * Returns the name of the entity field targeted by this restriction.
     * The field name represents the specific attribute of the entity
     * that this restriction applies to. For example, in a query filtering
     * {@code Person} entities by name, the field might be "name."
     *
     * @return the name of the field
     */
    String field();

    /**
     * Returns a negated version of this restriction.
     * Negating a restriction inverts its logical meaning. For instance, a restriction
     * such as "age > 18" would be negated to "age <= 18." This is useful for dynamically
     * creating logical complements of existing conditions in complex queries.
     *
     * @return a new {@code BasicRestriction} representing the negated condition
     * @see Operator#negate()
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
