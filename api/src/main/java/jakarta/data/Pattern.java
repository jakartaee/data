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

import jakarta.data.metamodel.Restriction;


/**
 * Represents a pattern-based restriction for matching operations, encapsulating different
 * options such as prefix, suffix, and substring matching. This implementation
 * allows flexibility in creating pattern-based conditions directly as {@link Restriction} instances.
 *
 * <p>Example usage with metadata attributes:</p>
 * <pre>
 * Restriction<Book> prefixIgnoreCase = _Book.title.endsWith("Guide");
 * </pre>
 *
 */
public record Pattern(String value, boolean ignoreCase) {

    /**
     * Creates a pattern for a {@link Operator#LIKE LIKE} match on the specified value.
     * This method sets the field to `null`, allowing it to be applied
     * later to a specific attribute.
     *
     * @return a Pattern instance for a pattern match.
     */
    public static Pattern like(String pattern) {
        return new Pattern(pattern, false);
    }

    /**
     * Creates a pattern for a `LIKE` match where values start with the specified prefix.
     * The `field` is set to `null` initially, allowing it to be assigned to an attribute later.
     *
     * @param value the prefix to match at the beginning of the field's value.
     * @return a Pattern instance for a prefix match.
     */
    public static Pattern startsWith(String value) {
        return new Pattern( value + "%", false);
    }

    /**
     * Creates a pattern for a `LIKE` match where values contain the specified substring.
     * This method initializes the field to `null`, allowing the pattern to be applied to
     * a specific attribute later.
     *
     * @param value the substring to match within the field's value.
     * @return a Pattern instance for a substring match.
     */
    public static Pattern contains(String value) {
        return new Pattern("%" + value + "%", false);
    }

    /**
     * Creates a pattern for a `LIKE` match where values end with the specified suffix.
     * The field is set to `null`, allowing assignment to a specific attribute later.
     *
     *
     * @param value the suffix to match at the end of the field's value.
     * @return a Pattern instance for a suffix match.
     */
    public static Pattern endsWith(String value) {
        return new Pattern(value + "%", false);
    }

    /**
     * Returns a new {@code Pattern} instance with case-insensitive matching.
     * This method allows you to specify that the pattern should ignore case when matching.
     *
     * <pre>
     * // Case-insensitive prefix match
     * Restriction<Book> titlePattern = Pattern.startsWith("Hibernate").ignoringCase();
     * </pre>
     *
     * @return a new Pattern instance with `ignoreCase` set to `true`.
     */
    public Pattern ignoringCase() {
        return new Pattern(value, true);
    }
}
