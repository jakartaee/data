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
 * Represents a condition or constraint applied to a query in Jakarta Data.
 * The {@code Restriction} interface defines a type-safe mechanism to represent
 * and manipulate query restrictions, such as conditions in a WHERE clause.
 * This abstraction allows for fluent and dynamic query building while maintaining
 * readability and flexibility in query construction.
 *
 * @param <T> the type of the entity or attribute to which the restriction applies
 */
public interface Restriction<T> {

    /**
     * Returns a negated version of the current restriction.
     * <p>
     * This method allows for the creation of an inverse or "NOT" version of
     * the current condition. For example, if the restriction represents
     * "age greater than 18," calling {@code negate()} would produce
     * "age not greater than 18."
     *
     * @return a new {@code Restriction} instance representing the negated condition
     */
    Restriction<T> negate();
}
