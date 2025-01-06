/*
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
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

import jakarta.data.Restrict;
import jakarta.data.Sort;
import jakarta.data.TextRestriction;

/**
 * Represents an textual entity attribute in the {@link StaticMetamodel}. It supports creating restrictions and sort
 * operations that are tailored to textual values, including case-insensitive sorting
 * and operations such as {@code startsWith}, {@code endsWith}, and {@code contains}.
 * <p>Examples:</p>
 * <pre>{@code
 * // Create a restriction for products where the name contains "Pro".
 * TextRestriction<Product> containsRestriction = _Product.name.contains("Pro");
 *
 * // Create a case-insensitive ascending sort on the name attribute.
 * Sort<Product> sortByNameAsc = _Product.name.ascIgnoreCase();
 *
 * // Create a restriction where the name does not start with "Demo".
 * TextRestriction<Product> notStartsWithRestriction = _Product.name.notStartsWith("Demo");
 * }</pre>
 * @param <T> entity class of the static metamodel.
 */
public interface TextAttribute<T> extends SortableAttribute<T> {

    /**
     * Obtain a request for an ascending, case insensitive {@link Sort} based on the entity attribute.
     *
     * @return a request for an ascending, case insensitive sort on the entity attribute.
     */
    Sort<T> ascIgnoreCase();

    /**
     * Obtain a request for a descending, case insensitive {@link Sort} based on the entity attribute.
     *
     * @return a request for a descending, case insensitive sort on the entity attribute.
     */
    Sort<T> descIgnoreCase();

    default TextRestriction<T> contains(String substring) {
        return Restrict.contains(substring, name());
    }

    default TextRestriction<T> endsWith(String suffix) {
        return Restrict.endsWith(suffix, name());
    }

    /**
     * Creates a restriction to match text that is equal to the specified value.
     *
     * <p>Example:</p>
     * <pre>{@code
     * TextRestriction<Product> equalsDemo = _Product.name.equalTo("Demo");
     * }</pre>
     *
     * @param value the value to match
     * @return a {@link TextRestriction} for the {@code equalTo} condition
     */
    default TextRestriction<T> equalTo(String value) {
        return Restrict.equalTo(value, name());
    }

    /**
     * Creates a restriction to match text greater than the specified value.
     *
     * <p>Example:</p>
     * <pre>{@code
     * TextRestriction<Product> greaterThanPro = _Product.name.greaterThan("Pro");
     * }</pre>
     *
     * @param value the value to compare against
     * @return a {@link TextRestriction} for the {@code greaterThan} condition
     */
    default TextRestriction<T> greaterThan(String value) {
        return Restrict.greaterThan(value, name());
    }

    /**
     * Creates a restriction to match text greater than or equal to the specified value.
     *
     * <p>Use this method to filter text-based attributes where the value is
     * greater than or equal to the provided value. This is commonly used for
     * lexicographical comparisons of string values.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     * TextRestriction<Product> greaterThanOrEqualToDemo = _Product.name.greaterThanEqual("Demo");
     * }</pre>
     *
     * @param value the value to compare against
     * @return a {@link TextRestriction} for the {@code greaterThanEqual} condition
     */
    default TextRestriction<T> greaterThanEqual(String value) {
        return Restrict.greaterThanEqual(value, name());
    }

    /**
     * Creates a restriction to match text less than the specified value.
     *
     * <p>Use this method to filter text-based attributes where the value is
     * less than the provided value. This is useful for lexicographical comparisons
     * in cases where ordering matters.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     * TextRestriction<Product> lessThanPro = _Product.name.lessThan("Pro");
     * }</pre>
     *
     * @param value the value to compare against
     * @return a {@link TextRestriction} for the {@code lessThan} condition
     */
    default TextRestriction<T> lessThan(String value) {
        return Restrict.lessThan(value, name());
    }

    /**
     * Creates a restriction to match text less than or equal to the specified value.
     *
     * <p>Use this method to filter text-based attributes where the value is
     * less than or equal to the provided value. This is useful for inclusive
     * lexicographical comparisons.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     * TextRestriction<Product> lessThanOrEqualToDemo = _Product.name.lessThanEqual("Demo");
     * }</pre>
     *
     * @param value the value to compare against
     * @return a {@link TextRestriction} for the {@code lessThanEqual} condition
     */
    default TextRestriction<T> lessThanEqual(String value) {
        return Restrict.lessThanEqual(value, name());
    }

    // TODO once we have Pattern:
    //default TextRestriction<T> like(Pattern pattern) {
    //    return Restrict.like(pattern, name());
    //}

    default TextRestriction<T> like(String pattern) {
        return Restrict.like(pattern, name());
    }

    default TextRestriction<T> like(String pattern,
                                    char charWildcard,
                                    char stringWildcard) {
        return Restrict.like(pattern, charWildcard, stringWildcard, name());
    }

    default TextRestriction<T> notContains(String substring) {
        return Restrict.notContains(substring, name());
    }

    default TextRestriction<T> notEndsWith(String suffix) {
        return Restrict.notEndsWith(suffix, name());
    }

    default TextRestriction<T> notEqualTo(String value) {
        return Restrict.notEqualTo(value, name());
    }

    default TextRestriction<T> notLike(String pattern) {
        return Restrict.notLike(pattern, name());
    }

    default TextRestriction<T> notLike(String pattern,
                                       char charWildcard,
                                       char stringWildcard) {
        return Restrict.notLike(pattern, charWildcard, stringWildcard, name());
    }

    default TextRestriction<T> notStartsWith(String prefix) {
        return Restrict.notStartsWith(prefix, name());
    }

    default TextRestriction<T> startsWith(String prefix) {
        return Restrict.startsWith(prefix, name());
    }

}
