/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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
package jakarta.data.constraint;

/**
 * A rule for adding escape characters to a pattern.
 */
enum EscapeRule {
    /**
     * Escape all special characters.
     */
    ALL,
    /**
     * Only escape backslash characters.
     */
    BACKSLASH_ONLY,
    /**
     * Escape special characters after the first character.
     */
    SKIP_FIRST,
    /**
     * Escape special characters between, but not including, the first
     * and last characters.
     */
    SKIP_FIRST_AND_LAST,
    /**
     * Escape special characters before the last character.
     */
    SKIP_LAST;

    static final char CHAR_WILDCARD = '_';
    static final char STRING_WILDCARD = '%';
    static final char ESCAPE = '\\';

    /**
     * Apply this escape rule to the given pattern.
     *
     * @param literal an unescaped pattern.
     * @return a pattern to which escape characters are added according to the
     *         given escape rule.
     */
    String apply(final String literal) {
        final int last = literal.length() - 1;
        final StringBuilder result = new StringBuilder();

        for (int i = 0; i <= last; i++) {
            final char ch = literal.charAt(i);
            if ((ch == STRING_WILDCARD ||
                 ch == CHAR_WILDCARD ||
                 ch == ESCAPE) &&
                switch (this) {
                    case ALL -> true;
                    case BACKSLASH_ONLY -> ch == ESCAPE;
                    case SKIP_FIRST -> i > 0;
                    case SKIP_LAST -> i < last;
                    case SKIP_FIRST_AND_LAST -> i > 0 && i < last;
                }) {
                result.append(ESCAPE);
            }
            result.append(ch);
        }

        return result.toString();
    }
}
