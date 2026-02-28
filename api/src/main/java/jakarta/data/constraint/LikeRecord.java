/*
 * Copyright (c) 2025,2026 Contributors to the Eclipse Foundation
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

import jakarta.data.expression.TextExpression;
import jakarta.data.messages.Messages;

record LikeRecord(TextExpression<?> unescapedPattern,
                  TextExpression<?> escapedPattern,
                  char escape)
        implements Like {

    static final char CHAR_WILDCARD = '_';
    static final char STRING_WILDCARD = '%';
    static final char ESCAPE = '\\';

    @Override
    public NotLike negate() {
        return new NotLikeRecord(unescapedPattern,
                                 escapedPattern,
                                 escape);
    }

    @Override
    public String toString() {
        if (unescapedPattern == null) {
            return "LIKE " + escapedPattern + " ESCAPE '" + escape + "'";
        } else {
            return "LIKE " + unescapedPattern;
        }
    }

    static String escape(String literal, boolean escapeWildcards) {
        final var result = new StringBuilder();
        for (int i = 0; i < literal.length(); i++) {
            final char ch = literal.charAt(i);
            if (ch == ESCAPE ||
                escapeWildcards && (ch == STRING_WILDCARD || ch == CHAR_WILDCARD)) {
                result.append(ESCAPE);
            }
            result.append(ch);
        }
        return result.length() == literal.length()
                ? literal // no escape characters were added
                : result.toString();
    }

    static String translate(String pattern,
                            char charWildcard,
                            char stringWildcard,
                            char escape,
                            boolean isPatternAlreadyEscaped) {
        if (charWildcard == stringWildcard) {
            throw new IllegalArgumentException(
                    Messages.get("007.wildcard.conflict", charWildcard));
        }
        if (charWildcard == escape || stringWildcard == escape) {
            throw new IllegalArgumentException(
                    Messages.get("008.escape.conflict", escape));
        }
        final var result = new StringBuilder();
        boolean isPreviousCharEscape = false;
        for (int i = 0; i < pattern.length(); i++) {
            final char ch = pattern.charAt(i);
            if (isPreviousCharEscape) {
                if (ch == CHAR_WILDCARD ||
                    ch == STRING_WILDCARD ||
                    ch == escape) {
                    result.append(escape);
                }
                result.append(ch);
                isPreviousCharEscape = false;
            } else if (ch == charWildcard) {
                result.append(CHAR_WILDCARD);
            } else if (ch == stringWildcard) {
                result.append(STRING_WILDCARD);
            } else if (ch == escape && isPatternAlreadyEscaped) {
                isPreviousCharEscape = true;
            } else {
                if (ch == STRING_WILDCARD ||
                    ch == CHAR_WILDCARD ||
                    ch == escape) {
                    result.append(escape);
                }
                result.append(ch);
            }
        }
        if (isPreviousCharEscape) {
            // Pattern cannot end with the escape character
            throw new IllegalArgumentException("pattern: " + pattern);
        }
        return result.toString();
    }
}
