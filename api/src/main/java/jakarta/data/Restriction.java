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

import jakarta.data.repository.Pattern;

import java.util.List;

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

    // Static Factory Methods

    /**
     * Creates an equality restriction for the specified field and value.
     * <pre>
     * Restriction<Book> titleEquals = Restriction.equal("title", "Jakarta Data");
     * </pre>
     */
    static <T> Restriction<T> equal(String field, Object value) {
        return new BasicRestriction<>(field, Operator.EQUAL, value);
    }

    /**
     * Creates a restriction for values greater than the specified value.
     * <pre>
     * Restriction<Book> ratingGreaterThan = Restriction.greaterThan("rating", 4.5);
     * </pre>
     */
    static <T> Restriction<T> greaterThan(String field, Object value) {
        return new BasicRestriction<>(field, Operator.GREATER_THAN, value);
    }

    /**
     * Creates a restriction for values greater than or equal to the specified value.
     * <pre>
     * Restriction<Book> ratingAtLeast = Restriction.greaterThanOrEqual("rating", 4.0);
     * </pre>
     */
    static <T> Restriction<T> greaterThanOrEqual(String field, Object value) {
        return new BasicRestriction<>(field, Operator.GREATER_THAN_EQUAL, value);
    }

    /**
     * Creates a restriction for values less than the specified value.
     * <pre>
     * Restriction<Book> ratingLessThan = Restriction.lessThan("rating", 3.0);
     * </pre>
     */
    static <T> Restriction<T> lessThan(String field, Object value) {
        return new BasicRestriction<>(field, Operator.LESS_THAN, value);
    }

    /**
     * Creates a restriction for values less than or equal to the specified value.
     * <pre>
     * Restriction<Book> ratingMax = Restriction.lessThanOrEqual("rating", 5.0);
     * </pre>
     */
    static <T> Restriction<T> lessThanOrEqual(String field, Object value) {
        return new BasicRestriction<>(field, Operator.LESS_THAN_EQUAL, value);
    }

    /**
     * Creates a restriction for a range of values between the specified start and end.
     * <pre>
     * Restriction<Book> publicationDateRange = Restriction.between("publicationDate", pastDate, LocalDate.now());
     * </pre>
     */
    static <T> Restriction<T> between(String field, Object start, Object end) {
        return new BasicRestriction<>(field, Operator.BETWEEN, List.of(start, end));
    }

    /**
     * Creates a restriction to check if the specified field is null.
     * <pre>
     * Restriction<Book> authorIsNull = Restriction.isNull("author");
     * </pre>
     */
    static <T> Restriction<T> isNull(String field) {
        return new BasicRestriction<>(field, Operator.IS_NULL, null);
    }


    /**
     * Creates a `LIKE` restriction using a simple string pattern.
     * <pre>
     * Restriction<Book> titleStartsWith = Restriction.like("title", "Jakarta%");
     * </pre>
     */
    static <T> Restriction<T> like(String field, String pattern) {
        return new BasicRestriction<>(field, Operator.LIKE, pattern);
    }

    /**
     * Creates a `LIKE` restriction using a `Pattern`, supporting complex `LIKE` operations.
     * <pre>
     * Restriction<Book> titleIgnoreCase = Restriction.like("title", Pattern.prefixedIgnoreCase("Java"));
     * </pre>
     */
    static <T> Restriction<T> like(String field, Pattern<T> pattern) {
        return pattern;
    }
}
