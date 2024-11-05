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
 * Enum representing common filter types used to compare, match, or restrict values in Jakarta Data queries.
 * The {@code FilterType} enum defines a set of operators to specify how values should be compared or matched
 * in data filtering, providing flexibility for various querying needs across different contexts.
 */
public enum FilterType {

    /**
     * Matches records where the field value is exactly equal to the specified value.
     * Often used for precise matching on identifiers, names, or other exact-value fields.
     */
    Equal,

    /**
     * Matches records where the field value is greater than the specified value.
     * Commonly used for numerical fields (e.g., dates, prices) to retrieve values above a threshold.
     */
    GreaterThan,

    /**
     * Matches records where the field value is greater than or equal to the specified value.
     * Useful for inclusive range queries to include a specified boundary.
     */
    GreaterThanEqual,

    /**
     * Filters records where the field value is contained within a specified collection or set of values.
     * Often applied to match multiple values (e.g., category lists, status collections).
     */
    In,

    /**
     * Matches records where the field value is less than the specified value.
     * Frequently used in numerical comparisons to retrieve values below a threshold.
     */
    LessThan,

    /**
     * Matches records where the field value is less than or equal to the specified value.
     * Suitable for inclusive range queries that include the specified boundary.
     */
    LessThanEqual,

    /**
     * Matches records where the field value conforms to a specified pattern, often utilizing wildcards.
     * Commonly applied to string fields, allowing partial matches (e.g., names containing a substring).
     */
    Like,

    /**
     * Matches records where the field value does not equal the specified value.
     * Typically used to exclude specific values from results, providing inverse filtering.
     */
    Not;
}


