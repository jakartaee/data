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
 * Represents a condition used to filter values in repository queries.
 *
 * <p>The `Restriction` interface defines various types of conditions, including equality,
 * comparison, range, null checks, and pattern matching checks, to support flexible and
 * type-safe filtering.</p>
 *
 * <p>Static factory methods are provided to create instances of `Restriction` with
 * specific conditions, using `BasicRestriction` as the underlying implementation.</p>
 *
 * <pre>
 * Restriction<Book> titleEquals = Restriction.equal("title", "Jakarta Data");
 *
 * Restriction<Book> ratingGreaterThan = Restriction.greaterThan("rating", 4.5);
 *
 * Restriction<Book> publicationDateRange = Restriction.between("publicationDate", pastDate, LocalDate.now());
 *
 * Restriction<Book> authorIsNull = Restriction.isNull("author");
 *
 * Restriction<Book> titleStartsWith = Restriction.like("title", "Jakarta%");
 *
 * Restriction<Book> titleIgnoreCase = Restriction.like("title", Pattern.prefixedIgnoreCase("Java"));
 * </pre>
 *
 * @param <T> the type of the entity on which the restriction is applied.
 */
public interface Restriction<T> {

    /**
     * The name of the field on which this restriction is applied.
     *
     * @return the field name.
     */
    String field();

    /**
     * The operator for this restriction.
     *
     * @return the operator defining the restriction type.
     */
    Operator operator();

    /**
     * The value used in this restriction, if applicable.
     *
     * @return the comparison value, or null if the restriction does not use a value.
     */
    Object value();
}
