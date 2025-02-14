/*
 * Copyright (c) 2024,2025 Contributors to the Eclipse Foundation
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
package jakarta.data.metamodel.restrict;

import jakarta.data.metamodel.range.Interval;
import jakarta.data.metamodel.range.Enumeration;
import jakarta.data.metamodel.range.LowerBound;
import jakarta.data.metamodel.range.Pattern;
import jakarta.data.metamodel.range.UpperBound;
import jakarta.data.metamodel.range.Value;

import java.util.List;
import java.util.Objects;
import java.util.Set;

// TODO document
// This is one of two places from which to obtain restrictions.
// The other place is from static metamodel attributes.
public class Restrict {

    private static final char CHAR_WILDCARD = '_';

    private static final char ESCAPE_CHAR = '\\';

    private static final char STRING_WILDCARD = '%';

    // prevent instantiation
    private Restrict() {
    }

    @SafeVarargs
    public static <T> Restriction<T> all(Restriction<T>... restrictions) {
        return new CompositeRestrictionRecord<>(CompositeRestriction.Type.ALL,
                                                List.of(restrictions));
    }

    @SafeVarargs
    public static <T> Restriction<T> any(Restriction<T>... restrictions) {
        return new CompositeRestrictionRecord<>(CompositeRestriction.Type.ANY,
                                                List.of(restrictions));
    }

    // TODO Need to think more about how to best cover negation of multiple
    // and then make negation of Single consistent with it

    public static <T> BasicRestriction<T> contains(String substring, String attribute) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, true, substring, true);
        return new BasicRestrictionRecord<>(attribute, Operator.LIKE, new Pattern(pattern));
    }

    public static <T> BasicRestriction<T> endsWith(String suffix, String attribute) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, true, suffix, false);
        return new BasicRestrictionRecord<>(attribute, Operator.LIKE, new Pattern(pattern));
    }

    public static <T> BasicRestriction<T> equalTo(Object value, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.EQUAL, new Value<>(value));
    }

    public static <T> BasicRestriction<T> equalTo(String value, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.EQUAL, new Value<>(value));
    }

    public static <T> UnaryRestriction<T> isNull(String attribute) {
        return new UnaryRestrictionRecord<>(attribute, UnaryOperator.IS_NULL);
    }

    public static <T, V extends Comparable<V>> BasicRestriction<T> greaterThan(V value, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.GREATER_THAN, new LowerBound<>(value));
    }

    public static <T, V extends Comparable<V>> BasicRestriction<T> greaterThanEqual(V value, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.GREATER_THAN_EQUAL, new LowerBound<>(value));
    }

    public static <T> BasicRestriction<T> in(Set<?> values, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.IN, new Enumeration<>(values));
    }

    public static <T, V extends Comparable<V>> BasicRestriction<T> lessThan(V value, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.LESS_THAN, new UpperBound<>(value));
    }

    public static <T, V extends Comparable<V>> BasicRestriction<T> lessThanEqual(V value, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.LESS_THAN_EQUAL, new UpperBound<>(value));
    }

    public static <T, V extends Comparable<V>> BasicRestriction<T> between(V lowerBound, V upperBound, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.IN,
                new Interval<>(new LowerBound<>(lowerBound), new UpperBound<>(upperBound)));
    }

    public static <T> BasicRestriction<T> like(Pattern pattern, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.LIKE, pattern);
    }

    public static <T> BasicRestriction<T> like(String pattern, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.LIKE, new Pattern(pattern));
    }

    public static <T> BasicRestriction<T> like(String pattern,
                                               char charWildcard,
                                               char stringWildcard,
                                               String attribute) {
        String p = toLikeEscaped(charWildcard, stringWildcard, false, pattern, false);
        return new BasicRestrictionRecord<>(attribute, Operator.LIKE, new Pattern(p));
    }

    // convenience method for those who would prefer to avoid .negate()
    public static <T> Restriction<T> not(Restriction<T> restriction) {
        Objects.requireNonNull(restriction, "Restriction must not be null");
        return restriction.negate();
    }

    public static <T> BasicRestriction<T> notEqualTo(Object value, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.NOT_EQUAL, new Value<>(value));
    }

    public static <T> BasicRestriction<T> notEqualTo(String value, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.NOT_EQUAL, new Value<>(value));
    }

    public static <T> UnaryRestriction<T> notNull(String attribute) {
        return new UnaryRestrictionRecord<>(attribute, UnaryOperator.IS_NOT_NULL);
    }

    public static <T> BasicRestriction<T> notContains(String substring, String attribute) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, true, substring, true);
        return new BasicRestrictionRecord<>(attribute, Operator.NOT_LIKE, new Pattern(pattern));
    }

    public static <T> BasicRestriction<T> notEndsWith(String suffix, String attribute) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, true, suffix, false);
        return new BasicRestrictionRecord<>(attribute, Operator.NOT_LIKE, new Pattern(pattern));
    }

    public static <T> BasicRestriction<T> notIn(Set<?> values, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.NOT_IN, new Enumeration<>(values));
    }

    public static <T> BasicRestriction<T> notLike(String pattern, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.NOT_LIKE, new Pattern(pattern));
    }

    public static <T> BasicRestriction<T> notLike(String pattern,
                                                  char charWildcard,
                                                  char stringWildcard,
                                                  String attribute) {
        String p = toLikeEscaped(charWildcard, stringWildcard, false, pattern, false);
        return new BasicRestrictionRecord<>(attribute, Operator.NOT_LIKE, new Pattern(p));
    }

    public static <T> BasicRestriction<T> notStartsWith(String prefix, String attribute) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, false, prefix, true);
        return new BasicRestrictionRecord<>(attribute, Operator.NOT_LIKE, new Pattern(pattern));
    }

    public static <T> BasicRestriction<T> startsWith(String prefix, String attribute) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, false, prefix, true);
        return new BasicRestrictionRecord<>(attribute, Operator.LIKE, new Pattern(pattern));
    }

    public static <T, V extends Comparable<V>> BasicRestriction<T> notBetween(V lowerBound, V upperBound, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.NOT_IN,
                new Interval<>(new LowerBound<>(lowerBound), new UpperBound<>(upperBound)));
    }

    /**
     * Converts the literal pattern into an escaped LIKE pattern.
     * This method prepends a % character if previous characters are allowed,
     * escapes the charWildcard (typically _), the stringWildcard (typically %),
     * and the \ character within the literal by inserting \ prior to each,
     * and then appends a % character if subsequent characters are allowed.
     *
     * @param charWildcard    single character wildcard, typically _.
     * @param stringWildcard  0 or more character wildcard, typically %.
     * @param allowPrevious   whether to allow characters prior to the text.
     * @param literal text    that is not escaped that must be matched.
     * @param allowSubsequent whether to allow more characters after the text.
     * @return escaped pattern.
     * @throws IllegalArgumentException if the same character is supplied for
     *                                  both wildcard types.
     */
    // TODO could move to Pattern class
    private static String toLikeEscaped(char charWildcard,
                                        char stringWildcard,
                                        boolean allowPrevious,
                                        String literal,
                                        boolean allowSubsequent) {
        if (charWildcard == stringWildcard)
            throw new IllegalArgumentException(
                    "Cannot use the same character (" + charWildcard +
                    ") for both types of wildcards.");

        int length = literal.length();
        StringBuilder s = new StringBuilder(length + 10);
        if (allowPrevious) {
            s.append(STRING_WILDCARD);
        }
        for (int i = 0; i < length; i++) {
            char ch = literal.charAt(i);
            if (ch == charWildcard) {
                s.append(ESCAPE_CHAR)
                 .append(CHAR_WILDCARD);
            } else if (ch == stringWildcard) {
                s.append(ESCAPE_CHAR)
                 .append(STRING_WILDCARD);
            } else if (ch == ESCAPE_CHAR) {
                s.append(ESCAPE_CHAR)
                 .append(ESCAPE_CHAR);
            } else {
                s.append(ch);
            }
        }
        if (allowSubsequent) {
            s.append(STRING_WILDCARD);
        }
        return s.toString();
    }

    @SuppressWarnings("unchecked")
    public static <T> Restriction<T> unrestricted() {
        return (Restriction<T>) Unrestricted.INSTANCE;
    }
}
