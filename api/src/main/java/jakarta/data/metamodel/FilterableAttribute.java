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

import jakarta.data.Criteria;

import java.util.Collection;

/**
 * Represents an entity attribute in the {@link StaticMetamodel} that supports filtering conditions.
 * This interface provides methods for defining common query conditions, allowing the attribute to be used
 * in filtering expressions such as `_Book.title.like("Jakarta Data%")`.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Define criteria for filtering
 * Criteria titleCriteria = _Book.title.like("Jakarta Data%");
 * Criteria dateCriteria = _Book.publicationDate.between(pastDate, LocalDate.now());
 * Criteria authorCriteria = _Book.author.equal("John Doe");
 *
 * // Apply criteria in a repository query
 * repo.books(List.of(titleCriteria, dateCriteria, authorCriteria));
 * }</pre>
 *
 * @param <T> the type of the entity class in the static metamodel.
 * @param <V> the type of the attribute's value.
 */
public interface FilterableAttribute<T, V> extends Attribute<T> {

    /**
     * Creates an equality condition for the attribute, matching the specified value.
     *
     * @param value the value to compare with the attribute.
     * @return a criteria condition representing "attribute = value".
     */
    Criteria equal(V value);

    /**
     * Creates an inequality condition for the attribute, excluding the specified value.
     *
     * @param value the value to exclude.
     * @return a criteria condition representing "attribute != value".
     */
    Criteria notEqual(V value);

    /**
     * Creates a "like" condition for textual attributes, matching the specified pattern.
     * This is typically used for partial matches in string fields.
     *
     * @param pattern the pattern to match, often using wildcards.
     * @return a criteria condition representing "attribute LIKE pattern".
     */
    Criteria like(String pattern);

    /**
     * Creates a "greater than" condition for the attribute, matching values greater than the specified value.
     *
     * @param value the minimum value to match.
     * @return a criteria condition representing "attribute > value".
     */
    Criteria greaterThan(V value);

    /**
     * Creates a "greater than or equal to" condition for the attribute, matching values greater than or equal to the specified value.
     *
     * @param value the minimum value to match inclusively.
     * @return a criteria condition representing "attribute >= value".
     */
    Criteria greaterThanOrEqual(V value);

    /**
     * Creates a "less than" condition for the attribute, matching values less than the specified value.
     *
     * @param value the maximum value to match.
     * @return a criteria condition representing "attribute < value".
     */
    Criteria lessThan(V value);

    /**
     * Creates a "less than or equal to" condition for the attribute, matching values less than or equal to the specified value.
     *
     * @param value the maximum value to match inclusively.
     * @return a criteria condition representing "attribute <= value".
     */
    Criteria lessThanOrEqual(V value);

    /**
     * Creates a "between" condition for the attribute, matching values within the specified range.
     *
     * @param start the lower bound of the range.
     * @param end   the upper bound of the range.
     * @return a criteria condition representing "attribute BETWEEN start AND end".
     */
    Criteria between(V start, V end);

    /**
     * Creates an "in" condition for the attribute, matching any of the specified values.
     *
     * @param values the collection of values to match.
     * @return a criteria condition representing "attribute IN (values)".
     */
    Criteria in(Collection<V> values);
}
