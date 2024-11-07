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
 * A basic implementation of the `Restriction` interface, representing various conditions
 * used in repository queries, including equality, comparison, and range checks.
 *
 * @param <T> the type of the entity on which the restriction is applied.
 */
public record SimpleRestriction<T>(String field, Operator operator, Object value) implements Restriction<T> {

    /**
     * Constructs a `BasicRestriction` with the specified field, operator, and value.
     *
     * @param field    the name of the field to apply the restriction to.
     * @param operator the operator defining the comparison or condition.
     * @param value    the value to compare the field against (optional for null checks).
     */
    public SimpleRestriction {
        if (field == null || operator == null) {
            throw new IllegalArgumentException("Field and operator must not be null.");
        }
    }
}
