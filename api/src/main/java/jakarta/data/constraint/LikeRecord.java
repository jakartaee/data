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

import static jakarta.data.constraint.EscapeRule.CHAR_WILDCARD;
import static jakarta.data.constraint.EscapeRule.ESCAPE;
import static jakarta.data.constraint.EscapeRule.STRING_WILDCARD;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.data.expression.TextExpression;
import jakarta.data.messages.Messages;
import jakarta.data.spi.expression.literal.StringLiteral;

record LikeRecord(StringLiteral unescapedPattern,
                  EscapeRule escapeRule,
                  AtomicReference<TextExpression<?>> escapedPatternRef,
                  char escape)
        implements Like {

    /**
     * Constructor for when an unescaped pattern is available and the escaped
     * pattern is lazily computed.
     *
     * @param unescaped  unescaped pattern.
     * @param escapeRule rule for adding escapes to the unescaped pattern.
     */
    LikeRecord(String unescaped,
               EscapeRule escapeRule) {
        this(StringLiteral.of(unescaped),
             escapeRule,
             new AtomicReference<>(), 
             ESCAPE);
    }

    /**
     * Constructor for when an escaped pattern is available (or must be computed)
     * in advance. An unescaped pattern might or might not be available.
     *
     * @param unescapedPattern unescaped pattern if available. Otherwise null.
     * @param escapedPattern   escaped pattern.
     * @param escape           escape character.
     */
    LikeRecord(StringLiteral unescapedPattern,
               TextExpression<?> escapedPattern,
               char escape) {
        this(unescapedPattern,
             null, // no EscapeRule needed because we already have escapedPattern
             new AtomicReference<>(escapedPattern),
             escape);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof LikeRecord like &&
            like.escape == escape &&
            like.escapeRule == escapeRule) {
            return like.unescapedPattern == null
                    ? Objects.equals(like.escapedPatternRef.get(),
                                     escapedPatternRef.get())
                    : like.unescapedPattern.equals(unescapedPattern);
        }
        return false;
    }

    @Override
    public TextExpression<?> escapedPattern() {
        TextExpression<?> escapedPattern = escapedPatternRef.get();
        if (escapedPattern == null) {
            escapedPattern = StringLiteral.of(
                    escapeRule.apply(unescapedPattern.value()));
            escapedPatternRef.set(escapedPattern);
        }
        return escapedPattern;
    }

    @Override
    public int hashCode() {
        return Objects.hash(escape,
                            escapeRule,
                            unescapedPattern == null
                                    ? escapedPatternRef.get()
                                    : unescapedPattern);
    }

    @Override
    public NotLike negate() {
        return new NotLikeRecord(unescapedPattern,
                                 escapeRule,
                                 escapedPatternRef,
                                 escape);
    }

    @Override
    public String toString() {
        TextExpression<?> escapedPattern = escapedPatternRef.get();
        if (escapedPattern == null) {
            return "LIKE " + unescapedPattern;
        } else {
            return "LIKE " + escapedPattern + " ESCAPE '" + escape + "'";
        }
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
