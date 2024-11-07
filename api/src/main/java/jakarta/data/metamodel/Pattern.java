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
 * Represents a pattern-based restriction for matching operations, encapsulating different
 * options such as prefix, suffix, and substring matching. This implementation
 * allows flexibility in creating pattern-based conditions directly as `Restriction` instances.
 *
 * <p>Example usage with metadata attributes:</p>
 * <pre>
 * // Case-sensitive exact match
 * Restriction<Book> exactMatch = Pattern.like(_Book.title, "Jakarta Data");
 *
 * // Case-insensitive prefix match
 * Restriction<Book> prefixIgnoreCase = Pattern.prefixedIgnoreCase(_Book.title, "Jak");
 *
 * // Case-sensitive suffix match
 * Restriction<Book> suffixMatch = Pattern.suffixed(_Book.title, "Guide");
 *
 * // Case-insensitive substring match
 * Restriction<Book> substringIgnoreCase = Pattern.substringedIgnoreCase(_Book.title, "Java");
 * </pre>
 *
 * @param <T> the type of the entity on which the restriction is applied.
 */
public record Pattern<T>(String field, String value, boolean ignoreCase) implements BasicRestriction<T> {

    /**
     * The operator for pattern-based restrictions, fixed as `LIKE`.
     *
     * @return the `LIKE` operator.
     */
    @Override
    public Operator operator() {
        return Operator.LIKE;
    }

    /**
     * The value of the pattern used for matching.
     *
     * @return the pattern value.
     */
    @Override
    public String value() {
        return value;
    }

    /**
     * Creates a pattern for a {@link Operator#LIKE LIKE} match on the specified value.
     * This method sets the field to `null`, allowing it to be applied
     * later to a specific attribute.
     *
     * <p>Example usage:</p>
     * <pre>
     * Restriction<Book> titlePattern = _Book.title.like(Pattern.like("Jakarta"));
     * </pre>
     *
     * @param value the pattern to match.
     * @return a Pattern instance for a pattern match.
     */
    public static <T> Pattern<T> like(String value) {
        return like(null, value);
    }

    /**
     * Creates a pattern for a `LIKE` match where values start with the specified prefix.
     * The `field` is set to `null` initially, allowing it to be assigned to an attribute later.
     *
     * <p>Example usage:</p>
     * <pre>
     * Restriction<Book> titlePattern = _Book.title.like(Pattern.startsWith("Hibernate"));
     * </pre>
     *
     * @param value the prefix to match at the beginning of the field's value.
     * @return a Pattern instance for a prefix match.
     */
    public static <T> Pattern<T> startsWith(String value) {
        return startsWith(null, value);
    }

    /**
     * Creates a pattern for a `LIKE` match where values contain the specified substring.
     * This method initializes the field to `null`, allowing the pattern to be applied to
     * a specific attribute later.
     *
     * <p>Example usage:</p>
     * <pre>
     * Restriction<Book> descriptionPattern = _Book.description.like(Pattern.contains("Java"));
     * </pre>
     *
     * @param value the substring to match within the field's value.
     * @return a Pattern instance for a substring match.
     */
    public static <T> Pattern<T> contains(String value) {
        return contains(null, value);
    }

    /**
     * Creates a pattern for a `LIKE` match where values end with the specified suffix.
     * The field is set to `null`, allowing assignment to a specific attribute later.
     *
     * <p>Example usage:</p>
     * <pre>
     * Restriction<Book> titlePattern = _Book.title.like(Pattern.endsWith("Guide"));
     * </pre>
     *
     * @param value the suffix to match at the end of the field's value.
     * @return a Pattern instance for a suffix match.
     */
    public static <T> Pattern<T> endsWith(String value) {
        return endsWith(null, value);
    }

    static <T> Pattern<T> endsWith(String field, String value) {
        return endsWith(field, value);
    }

    static <T> Pattern<T> contains(String field, String value) {
        return new Pattern<>(field, "%" + value + "%", false);
    }

    static <T> Pattern<T> startsWith(String field, String value) {
        return new Pattern<>(field, value + "%", false);
    }

    static <T> Pattern<T> suffixed(String field, String value) {
        return new Pattern<>(field, "%" + value, false);
    }

    static <T> Pattern<T> substringed(String field, String value) {
        return new Pattern<>(field, "%" + value + "%", false);
    }

    static <T> Pattern<T> like(String field, String value) {
        return new Pattern<>(field, value, false);
    }

    static <T> Pattern<T> prefixed(String field, String value) {
        return new Pattern<>(field, value + "%", false);
    }

    /**
     * Returns a new {@code Pattern} instance with case-insensitive matching.
     * This method allows you to specify that the pattern should ignore case when matching.
     *
     * <p>Example usage:</p>
     * <pre>
     * // Case-insensitive prefix match
     * Restriction<Book> titlePattern = _Book.title.like(Pattern.startsWith("Hibernate").ignoringCase());
     * </pre>
     *
     * @return a new Pattern instance with `ignoreCase` set to `true`.
     */
    public Pattern<T> ignoringCase() {
        return new Pattern<>(field, value, true);
    }
}
