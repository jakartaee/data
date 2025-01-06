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

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Utility class for creating and combining restrictions for entity queries.
 * <p>
 * The {@code Restrict} class serves as one of the primary mechanisms for constructing
 * restrictions in Jakarta Data. It provides a collection of static methods to create
 * restrictions for various operations, such as comparisons, patterns, and logical
 * groupings of restrictions. These restrictions are used to define conditions for
 * querying or filtering entity data in a type-safe and expressive manner.
 * </p>
 *
 * <p>
 * Restrictions created through this class are immutable. For example, calling
 * {@link Restriction#negate()} does not modify the existing restriction but instead
 * returns a new restriction instance representing the negated condition. This ensures
 * thread-safety and predictable behavior across all operations.
 * </p>
 *
 * <p>
 * For example, to filter a list of {@code Person} entities where the name starts with "John"
 * and age is greater than or equal to 30:
 * <pre>{@code
 * Restriction<Person> restriction = Restrict.all(
 *     Restrict.startsWith("John", "name"),
 *     Restrict.greaterThanEqual(30, "age")
 * );
 * Restriction<Person> negatedRestriction = restriction.negate();
 * }</pre>
 * </p>
 *
 * <p>
 * Note: This class cannot be instantiated and should only be used via its static methods.
 * It is complemented by static metamodel attributes, providing an alternative
 * type-safe mechanism for defining restrictions.
 * </p>
 */
public class Restrict {

    private static final char CHAR_WILDCARD = '_';

    private static final char ESCAPE_CHAR = '\\';

    // used internally for more readable code
    private static final boolean ESCAPED = true;

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

    public static <T, V extends Comparable<V>> Restriction<T> between(V min,
                                                                      V max,
                                                                      String field) {
        return all(greaterThanEqual(min, field),
                   lessThanEqual(max, field));
    }

    // TODO Need to think more about how to best cover negation of multiple
    // and then make negation of Single consistent with it

    public static <T> TextRestriction<T> contains(String substring, String field) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, true, substring, true);
        return new TextRestrictionRecord<>(field, Operator.LIKE, ESCAPED, pattern);
    }

    public static <T> TextRestriction<T> endsWith(String suffix, String field) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, true, suffix, false);
        return new TextRestrictionRecord<>(field, Operator.LIKE, ESCAPED, pattern);
    }

    public static <T> Restriction<T> equalTo(Object value, String field) {
        return new BasicRestrictionRecord<>(field, Operator.EQUAL, value);
    }

    public static <T> TextRestriction<T> equalTo(String value, String field) {
        return new TextRestrictionRecord<>(field, Operator.EQUAL, value);
    }

    public static <T, V extends Comparable<V>> Restriction<T> greaterThan(V value, String field) {
        return new BasicRestrictionRecord<>(field, Operator.GREATER_THAN, value);
    }

    public static <T> TextRestriction<T> greaterThan(String value, String field) {
        return new TextRestrictionRecord<>(field, Operator.GREATER_THAN, value);
    }

    public static <T, V extends Comparable<V>> Restriction<T> greaterThanEqual(V value, String field) {
        return new BasicRestrictionRecord<>(field, Operator.GREATER_THAN_EQUAL, value);
    }

    public static <T> TextRestriction<T> greaterThanEqual(String value, String field) {
        return new TextRestrictionRecord<>(field, Operator.GREATER_THAN_EQUAL, value);
    }

    public static <T> Restriction<T> in(Set<Object> values, String field) {
        return new BasicRestrictionRecord<>(field, Operator.IN, values);
    }

    public static <T, V extends Comparable<V>> Restriction<T> lessThan(V value, String field) {
        return new BasicRestrictionRecord<>(field, Operator.LESS_THAN, value);
    }

    public static <T> TextRestriction<T> lessThan(String value, String field) {
        return new TextRestrictionRecord<>(field, Operator.LESS_THAN, value);
    }

    public static <T, V extends Comparable<V>> Restriction<T> lessThanEqual(V value, String field) {
        return new BasicRestrictionRecord<>(field, Operator.LESS_THAN_EQUAL, value);
    }

    public static <T> TextRestriction<T> lessThanEqual(String value, String field) {
        return new TextRestrictionRecord<>(field, Operator.LESS_THAN_EQUAL, value);
    }

    // TODO this would be possible if Pattern is added, but is it even useful?
    //public static <T> TextRestriction<T> like(Pattern pattern, String field) {
    //    return new TextRestriction<>(field, Operator.LIKE, ESCAPED, pattern);
    //}

    public static <T> TextRestriction<T> like(String pattern, String field) {
        return new TextRestrictionRecord<>(field, Operator.LIKE, pattern);
    }

    public static <T> TextRestriction<T> like(String pattern,
                                               char charWildcard,
                                               char stringWildcard,
                                               String field) {
        String p = toLikeEscaped(charWildcard, stringWildcard, false, pattern, false);
        return new TextRestrictionRecord<>(field, Operator.LIKE, ESCAPED, p);
    }

    // convenience method for those who would prefer to avoid .negate()
    public static <T> Restriction<T> not(Restriction<T> restriction) {
        Objects.requireNonNull(restriction, "Restriction must not be null");
        return restriction.negate();
    }

    public static <T> Restriction<T> notEqualTo(Object value, String field) {
        return new BasicRestrictionRecord<>(field, Operator.NOT_EQUAL, value);
    }

    public static <T> TextRestriction<T> notEqualTo(String value, String field) {
        return new TextRestrictionRecord<>(field, Operator.NOT_EQUAL, value);
    }

    public static <T> TextRestriction<T> notContains(String substring, String field) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, true, substring, true);
        return new TextRestrictionRecord<>(field, Operator.NOT_LIKE, ESCAPED, pattern);
    }

    public static <T> TextRestriction<T> notEndsWith(String suffix, String field) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, true, suffix, false);
        return new TextRestrictionRecord<>(field, Operator.NOT_LIKE, ESCAPED, pattern);
    }

    public static <T> Restriction<T> notIn(Set<Object> values, String field) {
        return new BasicRestrictionRecord<>(field, Operator.NOT_IN, values);
    }

    public static <T> TextRestriction<T> notLike(String pattern, String field) {
        return new TextRestrictionRecord<>(field, Operator.NOT_LIKE, pattern);
    }

    public static <T> TextRestriction<T> notLike(String pattern,
                                                  char charWildcard,
                                                  char stringWildcard,
                                                  String field) {
        String p = toLikeEscaped(charWildcard, stringWildcard, false, pattern, false);
        return new TextRestrictionRecord<>(field, Operator.NOT_LIKE, ESCAPED, p);
    }

    public static <T> TextRestriction<T> notStartsWith(String prefix, String field) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, false, prefix, true);
        return new TextRestrictionRecord<>(field, Operator.NOT_LIKE, ESCAPED, pattern);
    }

    public static <T> TextRestriction<T> startsWith(String prefix, String field) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, false, prefix, true);
        return new TextRestrictionRecord<>(field, Operator.LIKE, ESCAPED, pattern);
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
}
