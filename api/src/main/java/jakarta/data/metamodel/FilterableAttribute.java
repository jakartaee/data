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

import jakarta.data.Restriction;

import java.util.function.Supplier;

/**
 * Represents an entity attribute that supports filtering operations in repository queries,
 * including type-safe comparisons, range restrictions, and pattern matching (e.g., LIKE queries).
 *
 * <p>The `FilterableAttribute` interface provides methods for creating various filtering
 * restrictions, enabling expressive and type-safe query construction for attributes
 * such as numeric values, dates, and strings.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * // Define filtering conditions on entity attributes
 * Restriction<Book> titleRestriction = _Book.title.like(Pattern.prefixedIgnoreCase("Jak"));
 * Restriction<Book> dateRestriction = _Book.publicationDate.between(pastDate, LocalDate.now());
 * </pre>
 *
 * @param <T> the entity type that this attribute belongs to.
 */
public interface FilterableAttribute<T> {

    /**
     * Creates an equality restriction for the attribute.
     *
     * @param value the value to match exactly.
     * @return a Restriction representing an equality condition.
     */
    Restriction<T> equal(Object value);

    /**
     * Creates a restriction for values greater than the specified value.
     *
     * @param value the lower bound (exclusive) for the attribute.
     * @return a Restriction representing a greater-than condition.
     */
    Restriction<T> greaterThan(Object value);

    /**
     * Creates a restriction for values greater than or equal to the specified value.
     *
     * @param value the lower bound (inclusive) for the attribute.
     * @return a Restriction representing a greater-than-or-equal condition.
     */
    Restriction<T> greaterThanOrEqual(Object value);

    /**
     * Creates a restriction for values less than the specified value.
     *
     * @param value the upper bound (exclusive) for the attribute.
     * @return a Restriction representing a less-than condition.
     */
    Restriction<T> lessThan(Object value);

    /**
     * Creates a restriction for values less than or equal to the specified value.
     *
     * @param value the upper bound (inclusive) for the attribute.
     * @return a Restriction representing a less-than-or-equal condition.
     */
    Restriction<T> lessThanOrEqual(Object value);

    /**
     * Creates a restriction that matches values within the specified range.
     *
     * @param start the starting value of the range (inclusive).
     * @param end   the ending value of the range (inclusive).
     * @return a Restriction representing a range condition.
     */
    Restriction<T> between(Object start, Object end);

    /**
     * Creates a `LIKE` restriction using a `Pattern` for the attribute,
     * supporting different `LIKE` options such as prefix, suffix, and substring matching.
     *
     * @param pattern the pattern to match, defined using the `Pattern` class.
     * @return a Restriction representing the `LIKE` condition.
     */
    default Restriction<T> like(Supplier<Restriction<T>> pattern) {
        return pattern.get();
    }
}
