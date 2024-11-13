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
 * Represents a pattern-based restriction for matching operations, supporting options
 * such as exact match, prefix, suffix, and substring matching. This implementation
 * provides flexibility in creating pattern-based conditions directly as {@link Restriction} instances.
 *
 * <p>Example usage with metadata attributes:</p>
 * <pre>
 * // Match for values starting with "Guide"
 * Restriction<Book> prefixMatch = _Book.title.startsWith("Guide");
 *
 * // Match for values containing "Java"
 * Restriction<Book> containsMatch = _Book.title.contains(Pattern.contains("Java"));
 * </pre>
 */
public record Pattern(String pattern, boolean caseSensitive) {

    /**
     * Creates a pattern for an exact match with the specified literal.
     *
     * <p>Example usage:</p>
     * <pre>
     * Restriction<Book> exactMatch = _Book.title.is(Pattern.is("Java Guide"));
     * </pre>
     *
     * @param literal the exact text to match.
     * @return a {@code Pattern} instance for an exact match.
     */
    public static Pattern is(String literal) {
        return new Pattern(escape(literal), true);
    }

    /**
     * Creates a pattern for a match based on the specified custom pattern.
     *
     * <p>Example usage:</p>
     * <pre>
     * Restriction<Book> customPatternMatch = _Book.title.matches(Pattern.matches("Ja%_a"));
     * </pre>
     *
     * @param pattern the pattern to match.
     * @return a {@code Pattern} instance for a custom match.
     */
    public static Pattern matches(String pattern) {
        return new Pattern(pattern, true);
    }

    /**
     * Creates a pattern using custom single and multi-character wildcards.
     * Allows replacing placeholders in the pattern with standard SQL wildcards.
     *
     * <p>Example usage:</p>
     * <pre>
     * Restriction<Book> wildcardMatch = _Book.title.matches(Pattern.matches("Ja?a%", '?', '*'));
     * </pre>
     *
     * @param pattern           the custom pattern to match.
     * @param characterWildcard the character to use as a single-character wildcard.
     * @param stringWildcard    the character to use as a multi-character wildcard.
     * @return a {@code Pattern} instance for a custom match with specified wildcards.
     */
    public static Pattern matches(String pattern, char characterWildcard, char stringWildcard) {
        final String standardized = escape(pattern)
                .replace(characterWildcard, '_')
                .replace(stringWildcard, '%');
        return new Pattern(standardized, true);
    }

    /**
     * Creates a pattern for a match where values start with the specified prefix.
     *
     * <p>Example usage:</p>
     * <pre>
     * Restriction<Book> prefixMatch = _Book.title.startsWith(Pattern.startsWith("Hibernate"));
     * </pre>
     *
     * @param prefix the prefix to match at the beginning of the value.
     * @return a {@code Pattern} instance for a prefix match.
     */
    public static Pattern startsWith(String prefix) {
        return new Pattern(escape(prefix) + '%', true);
    }

    /**
     * Creates a pattern for a match where values end with the specified suffix.
     *
     * <p>Example usage:</p>
     * <pre>
     * Restriction<Book> suffixMatch = _Book.title.endsWith(Pattern.endsWith("Guide"));
     * </pre>
     *
     * @param suffix the suffix to match at the end of the value.
     * @return a {@code Pattern} instance for a suffix match.
     */
    public static Pattern endsWith(String suffix) {
        return new Pattern('%' + escape(suffix), true);
    }

    /**
     * Creates a pattern for a match where values contain the specified substring.
     *
     * <p>Example usage:</p>
     * <pre>
     * Restriction<Book> substringMatch = _Book.title.contains(Pattern.contains("Java"));
     * </pre>
     *
     * @param substring the substring to match within the value.
     * @return a {@code Pattern} instance for a substring match.
     */
    public static Pattern contains(String substring) {
        return new Pattern('%' + escape(substring) + '%', true);
    }

    /**
     * Escapes special characters in the pattern, such as underscores and percent signs,
     * to ensure literal matches for these characters.
     *
     * @param literal the text to escape.
     * @return the escaped text with special characters handled.
     */
    private static String escape(String literal) {
        return literal.replace("_", "\\_").replace("%", "\\%");
    }
}

