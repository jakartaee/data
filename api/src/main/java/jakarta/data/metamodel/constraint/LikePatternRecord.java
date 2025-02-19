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
package jakarta.data.metamodel.constraint;

import jakarta.data.metamodel.restrict.Operator;

import java.util.Objects;

record LikePatternRecord(String pattern, boolean caseSensitive)
        implements Like {

    public static final char CHAR_WILDCARD = '_';
    public static final char STRING_WILDCARD = '%';
    public static final char ESCAPE = '\\';

    public LikePatternRecord {
        Objects.requireNonNull(pattern, "Pattern must not be null");
    }

    public LikePatternRecord(String pattern) {
        this(pattern, true);
    }

    public LikePatternRecord(String pattern, char charWildcard, char stringWildcard) {
        this(translate(pattern, charWildcard, stringWildcard));
    }

    @Override
    public LikePatternRecord ignoreCase() {
        return new LikePatternRecord(pattern, false);
    }

    @Override
    public Operator operator() {
        return Operator.LIKE;
    }

    @Override
    public String toString() {
        return "'" + pattern + "'";
    }

    public static LikePatternRecord prefix(String prefix) {
        return new LikePatternRecord(escape(prefix) + STRING_WILDCARD);
    }

    public static LikePatternRecord suffix(String suffix) {
        return new LikePatternRecord(STRING_WILDCARD + escape(suffix));
    }

    public static LikePatternRecord substring(String substring) {
        return new LikePatternRecord(STRING_WILDCARD + escape(substring) + STRING_WILDCARD);
    }

    private static String escape(String literal) {
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LikePatternRecord that
            && pattern.equals(that.pattern)
            && caseSensitive == that.caseSensitive;
    }

    @Override
    public int hashCode() {
        return pattern.hashCode();
    }
}
