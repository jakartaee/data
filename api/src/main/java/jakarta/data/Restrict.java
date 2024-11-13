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
import java.util.Set;

/**
 * Utility class for constructing complex restrictions in repository queries.
 * The `Restrict` class provides static methods for creating basic and composite
 * restrictions, supporting conditions like equality, comparisons, and logical
 * combinations (`ALL` and `ANY`).
 *
 * <p>Example usage:</p>
 * <pre>
 * // Create a single equality restriction
 * Restriction<Book> titleRestriction = Restrict.equalTo("Java Guide", "title");
 *
 * // Create a composite restriction using AND logic
 * Restriction<Book> compositeRestriction = Restrict.all(
 *     Restrict.equalTo("Java Guide", "title"),
 *     Restrict.greaterThan(2020, "publicationYear")
 * );
 * </pre>
 */
public final class Restrict {

    private Restrict() {
    }

    /**
     * Creates a composite restriction that requires all specified restrictions to be met.
     *
     * @param restrictions the list of restrictions to combine with AND logic.
     * @param <T> the entity type.
     * @return a composite restriction using AND logic.
     */
    @SafeVarargs
    public static <T> Restriction<T> all(Restriction<T>... restrictions) {
        return new CompositeRestrictionRecord<>(CompositeRestrictionType.ALL, List.of(restrictions));
    }

    /**
     * Creates a composite restriction that requires at least one of the specified restrictions to be met.
     *
     * @param restrictions the list of restrictions to combine with OR logic.
     * @param <T> the entity type.
     * @return a composite restriction using OR logic.
     */
    @SafeVarargs
    public static <T> Restriction<T> any(Restriction<T>... restrictions) {
        return new CompositeRestrictionRecord<>(CompositeRestrictionType.ANY, List.of(restrictions));
    }

    /**
     * Creates an equality restriction for the specified field and value.
     *
     * @param value the value to match exactly.
     * @param field the name of the field to apply the restriction on.
     * @param <T> the entity type.
     * @return an equality restriction for the specified field.
     */
    public static <T> Restriction<T> equalTo(Object value, String field) {
        return new RestrictionRecord<>(field, Operator.EQUAL, value);
    }

    /**
     * Creates a "less than" restriction for the specified field and value.
     *
     * @param value the upper bound (exclusive) for the field.
     * @param field the name of the field to apply the restriction on.
     * @param <T> the entity type.
     * @return a "less than" restriction for the specified field.
     */
    public static <T> Restriction<T> lessThan(Object value, String field) {
        return new RestrictionRecord<>(field, Operator.LESS_THAN, value);
    }

    /**
     * Creates a "greater than or equal to" restriction for the specified field and value.
     *
     * @param value the lower bound (inclusive) for the field.
     * @param field the name of the field to apply the restriction on.
     * @param <T> the entity type.
     * @return a "greater than or equal to" restriction for the specified field.
     */
    public static <T> Restriction<T> greaterThanEqual(Object value, String field) {
        return new RestrictionRecord<>(field, Operator.GREATER_THAN_EQUAL, value);
    }

    /**
     * Creates a "less than or equal to" restriction for the specified field and value.
     *
     * @param value the upper bound (inclusive) for the field.
     * @param field the name of the field to apply the restriction on.
     * @param <T> the entity type.
     * @return a "less than or equal to" restriction for the specified field.
     */
    public static <T> Restriction<T> lessThanEqual(Object value, String field) {
        return new RestrictionRecord<>(field, Operator.LESS_THAN_EQUAL, value);
    }

    /**
     * Creates a "greater than" restriction for the specified field and value.
     *
     * @param value the lower bound (exclusive) for the field.
     * @param field the name of the field to apply the restriction on.
     * @param <T> the entity type.
     * @return a "greater than" restriction for the specified field.
     */
    public static <T> Restriction<T> greaterThan(Object value, String field) {
        return new RestrictionRecord<>(field, Operator.GREATER_THAN, value);
    }

    /**
     * Creates an "in" restriction, restricting the field to match any value in the specified set.
     *
     * @param values the set of allowed values for the field.
     * @param field the name of the field to apply the restriction on.
     * @param <T> the entity type.
     * @return an "in" restriction for the specified field.
     */
    public static <T> Restriction<T> in(Set<Object> values, String field) {
        return new RestrictionRecord<>(field, Operator.IN, values);
    }
}
