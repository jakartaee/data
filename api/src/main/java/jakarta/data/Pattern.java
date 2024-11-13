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


/**
 * Represents a pattern for use in string matching conditions. This class supports options
 * such as exact match, prefix, suffix, and substring matching. It is intended to be used
 * with attributes that apply the pattern to create a {@link jakarta.data.metamodel.Restriction}.
 *
 * <p>Example usage:</p>
 * <pre>
 * // Pattern for a case-sensitive prefix match for values starting with "Guide"
 * Pattern prefixMatch = Pattern.startsWith("Guide");
 *
 * // Pattern for matching values containing "Java"
 * Pattern containsMatch = Pattern.contains("Java");
 * </pre>
 */
public record Pattern(String pattern, boolean caseSensitive) {

    /**
     * Creates a pattern for an exact match with the specified literal.
     *
     * <p>Example usage:</p>
     * <pre>
     * Pattern exactMatch = Pattern.is("Java Guide");
     * </pre>
     *
     * @param literal the exact text to match.
     * @return a {@code Pattern} instance for an exact match.
     */
    public static Pattern is(String literal) {
        return new Pattern(escape(literal), true);
    }

    /**
     * Creates a pattern for a custom match.
     *
     * <p>Example usage:</p>
     * <pre>
     * Pattern customPatternMatch = Pattern.matches("Ja%_a");
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
     *
     * <p>Example usage:</p>
     * <pre>
     * Pattern wildcardMatch = Pattern.matches("Ja?a%", '?', '*');
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
     * Pattern prefixMatch = Pattern.startsWith("Hibernate");
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
     * Pattern suffixMatch = Pattern.endsWith("Guide");
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
     * Pattern substringMatch = Pattern.contains("Java");
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


