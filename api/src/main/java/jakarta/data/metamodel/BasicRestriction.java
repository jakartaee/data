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
package jakarta.data.metamodel;

import jakarta.data.Operator;

/**
 * A basic restriction applied to a single field, representing conditions such as equality,
 * comparisons, range checks, and pattern matches.
 *
 * <p>The `BasicRestriction` interface provides methods for defining simple, singular restrictions
 * based on a specific field, an operator, and an optional comparison value. This interface supports
 * common operators (e.g., EQUAL, GREATER_THAN) and serves as a foundation for filtering
 * logic on individual fields.</p>
 *
 * @param <T> the type of the entity on which the restriction is applied.
 */
public interface BasicRestriction<T> extends Restriction<T> {

    /**
     * The name of the field on which this restriction is applied.
     *
     * @return the field name as a String.
     */
    String field();

    /**
     * The operator defining the type of comparison or condition for this restriction.
     *
     * @return the operator representing the restriction type (e.g., EQUAL, LIKE, BETWEEN).
     */
    Operator operator();

    /**
     * The value used for comparison in this restriction, if applicable.
     *
     * @return the comparison value, or {@code null} if the restriction does not use a value (e.g., IS_NULL).
     */
    Object value();
}
