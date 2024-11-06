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
 * Represents a restriction condition for repository queries, defining an entity's attribute,
 * an operator, a comparison value, and an optional negation flag for flexible query construction.
 *
 * <p>Restrictions are used to specify filtering conditions, supporting type-safe attributes and
 * a range of operators such as {@code EQUAL}, {@code LIKE}, {@code BETWEEN}, and more.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * Restriction<Book> titleRestriction = Restriction.like("title", "Data%");
 * Restriction<Book> dateRestriction = Restriction.between("publicationDate", pastDate, LocalDate.now());
 * </pre>
 *
 * @param <T> the entity type that this restriction applies to, ensuring type-safe usage.
 */
public record Restriction<T>(String field, Operator operator, Object value, boolean negate) {

    /**
     * Constructs a restriction condition without negation.
     *
     * @param field    the name of the field to apply the restriction to (e.g., "title" or "publicationDate").
     * @param operator the operator defining the comparison or condition (e.g., `Operator.LIKE`).
     * @param value    the value to compare the field against.
     */
    public Restriction(String field, Operator operator, Object value) {
        this(field, operator, value, false);
    }

    /**
     * Creates a basic restriction condition without negation.
     *
     * @param field    the field name of the entity.
     * @param operator the operator defining the restriction type.
     * @param value    the value to apply in the restriction.
     * @return a restriction condition.
     */
    public static <T> Restriction<T> where(String field, Operator operator, Object value) {
        return new Restriction<>(field, operator, value, false);
    }

    /**
     * Creates a restriction condition with negation.
     *
     * @param field    the field name of the entity.
     * @param operator the operator defining the restriction type.
     * @param value    the value to apply in the restriction.
     * @return a negated restriction condition.
     */
    public static <T> Restriction<T> not(String field, Operator operator, Object value) {
        return new Restriction<>(field, operator, value, true);
    }

    /**
     * Creates a LIKE restriction with a pattern, without negation.
     *
     * @param field   the field name of the entity.
     * @param pattern the pattern to match.
     * @return a LIKE restriction condition.
     */
    public static <T> Restriction<T> like(String field, String pattern) {
        return new Restriction<>(field, Operator.LIKE, pattern, false);
    }

    /**
     * Creates a BETWEEN restriction for range queries, without negation.
     *
     * @param field the field name of the entity.
     * @param start the starting value of the range.
     * @param end   the ending value of the range.
     * @return a BETWEEN restriction condition.
     */
    public static <T> Restriction<T> between(String field, Object start, Object end) {
        return new Restriction<>(field, Operator.BETWEEN, List.of(start, end), false);
    }
}
