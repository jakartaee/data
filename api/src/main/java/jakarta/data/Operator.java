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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data;


/**
 * Enum representing common operators used to define query conditions in Jakarta Data queries.
 * The {@code Operator} enum provides a set of operations to specify how values should be compared,
 * matched, or restricted when filtering data, supporting flexible and expressive querying across
 * different contexts.
 */
public enum Operator {

    /**
     * Matches records where the field value is exactly equal to the specified value.
     * Typically used for exact matching on unique identifiers, names, or other exact-value fields.
     */
    EQUAL,

    /**
     * Matches records where the field value is greater than the specified value.
     * Often applied to numerical fields (e.g., dates, prices) for retrieving values above a given threshold.
     */
    GREATER_THAN,

    /**
     * Matches records where the field value is greater than or equal to the specified value.
     * Useful for inclusive range queries, where the specified boundary is included in the results.
     */
    GREATER_THAN_EQUAL,

    /**
     * Matches records where the field value is contained within a specified collection of values.
     * Commonly used to match against multiple possible values, such as category or status lists.
     */
    IN,

    /**
     * Matches records where the field value is less than the specified value.
     * Frequently used in numerical comparisons to retrieve values below a certain threshold.
     */
    LESS_THAN,

    /**
     * Matches records where the field value is less than or equal to the specified value.
     * Suitable for inclusive range queries that include the specified boundary in the results.
     */
    LESS_THAN_EQUAL,

    /**
     * Matches records where the field value conforms to a specified pattern, often with wildcards.
     * Commonly applied to string fields for partial matches (e.g., names containing a substring).
     */
    LIKE,

    /**
     * Matches records where the field value falls within a specified range.
     * Typically used for range-based comparisons, such as finding dates between a start and end date.
     */
    BETWEEN,

    /**
     * Matches records where the field value is null.
     *
     * <p>This operator is used to filter records where the specified field
     * does not contain a value. Commonly used to find records with unset or
     * missing data in a particular field.</p>
     */
    IS_NULL
}



