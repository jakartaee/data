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

import java.util.List;

/**
 * Represents a criteria condition for repository queries, defining a field, an operator,
 * a comparison value, and an optional negation flag for flexible query construction.
 */
public record Criteria(String field, FilterType operator, Object value, boolean negate) {

    /**
     * Constructs a criteria condition without negation.
     *
     * @param field    the name of the field to apply the criteria to (e.g., "title" or "publicationDate").
     * @param operator the operator defining the comparison or condition (e.g., `FilterType.Like`).
     * @param value    the value to compare the field against.
     */
    public Criteria(String field, FilterType operator, Object value) {
        this(field, operator, value, false);
    }


    /**
     * Creates a basic criteria condition without negation.
     */
    public static Criteria where(String field, FilterType operator, Object value) {
        return new Criteria(field, operator, value, false);
    }

    /**
     * Creates a criteria condition with negation.
     */
    public static Criteria not(String field, FilterType operator, Object value) {
        return new Criteria(field, operator, value, true);
    }

    /**
     * Creates a LIKE criteria with a pattern, without negation.
     */
    public static Criteria like(String field, String pattern) {
        return new Criteria(field, FilterType.LIKE, pattern, false);
    }

    /**
     * Creates a BETWEEN criteria for range queries, without negation.
     */
    public static Criteria between(String field, Object start, Object end) {
        return new Criteria(field, FilterType.BETWEEN, List.of(start, end), false);
    }
}
