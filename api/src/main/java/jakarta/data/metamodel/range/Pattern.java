/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
package jakarta.data.metamodel.range;

import java.util.Objects;

public record Pattern(String pattern, boolean caseSensitive)
        implements Range<String> {

    public static final char CHAR_WILDCARD = '_';
    public static final char STRING_WILDCARD = '%';
    public static final char ESCAPE = '\\';

    public Pattern {
        Objects.requireNonNull(pattern, "Pattern must not be null");
    }

    public Pattern(String pattern) {
        this(pattern, true);
    }

    public Pattern(String pattern, char charWildcard, char stringWildcard) {
        this(translate(pattern, charWildcard, stringWildcard));
    }

    public Pattern ignoreCase() {
        return new Pattern(pattern, false);
    }

    @Override
    public String toString() {
        return "'" + pattern + "'";
    }

    public static Pattern prefix(String prefix) {
        return prefix(prefix, true);
    }

    public static Pattern prefix(String prefix, boolean caseSensitive) {
        return new Pattern(literal(prefix) + STRING_WILDCARD, caseSensitive);
    }

    public static Pattern suffix(String suffix) {
        return suffix(suffix, true);
    }

    public static Pattern suffix(String suffix, boolean caseSensitive) {
        return new Pattern(STRING_WILDCARD + literal(suffix), caseSensitive);
    }

    public static Pattern substring(String substring) {
        return substring(substring, true);
    }

    public static Pattern substring(String substring, boolean caseSensitive) {
        return new Pattern(STRING_WILDCARD + literal(substring) + STRING_WILDCARD, caseSensitive);
    }

    private static String literal(String literal) {
        final var result = new StringBuilder();
        for (int i = 0; i<literal.length(); i++) {
            final char ch = literal.charAt(i);
            if (ch == STRING_WILDCARD || ch == CHAR_WILDCARD || ch == ESCAPE) {
                result.append(ESCAPE);
            }
            result.append(ch);
        }
        return result.toString();
    }

    private static String translate(String pattern, char charWildcard, char stringWildcard) {
        if ( charWildcard == stringWildcard ) {
            throw new IllegalArgumentException("Cannot use the same character (" + charWildcard + ") for both wildcards.");
        }
        final var result = new StringBuilder();
        for (int i = 0; i<pattern.length(); i++) {
            final char ch = pattern.charAt(i);
            if (ch == charWildcard) {
                result.append(CHAR_WILDCARD);
            } else if (ch == stringWildcard) {
                result.append(STRING_WILDCARD);
            } else {
                if (ch == STRING_WILDCARD || ch == CHAR_WILDCARD || ch == ESCAPE) {
                    result.append(ESCAPE);
                }
                result.append(ch);
            }
        }
        return result.toString();
    }
}
