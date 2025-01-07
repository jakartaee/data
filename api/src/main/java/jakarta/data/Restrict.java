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

    /**
     * Combines multiple restrictions using a logical {@link jakarta.data.CompositeRestriction.Type#ALL}.
     *
     * <p>This method creates a composite restriction that is only satisfied when all
     * the provided restrictions are true. Use this for cases where multiple conditions
     * must be met simultaneously.</p>
     *
     * <pre>{@code
     * Restriction<Person> restriction = Restrict.all(
     *     Restrict.equalTo("John", "name"),
     *     Restrict.greaterThanEqual(30, "age")
     * );
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param restrictions the restrictions to combine
     * @return a composite restriction that requires all conditions to be true
     */
    @SafeVarargs
    public static <T> Restriction<T> all(Restriction<T>... restrictions) {
        return new CompositeRestrictionRecord<>(CompositeRestriction.Type.ALL,
                List.of(restrictions));
    }

    /**
     * Combines multiple restrictions using a logical {@link jakarta.data.CompositeRestriction.Type#ANY}.
     *
     * <p>This method creates a composite restriction that is satisfied when at least
     * one of the provided restrictions is true. Use this for cases where only one of
     * multiple conditions needs to be met.</p>
     *
     * <pre>{@code
     * Restriction<Book> restriction = Restrict.any(
     *     Restrict.like("Java", "title"),
     *     Restrict.greaterThan(2010, "publicationYear")
     * );
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param restrictions the restrictions to combine
     * @return a composite restriction that requires at least one condition to be true
     */

    @SafeVarargs
    public static <T> Restriction<T> any(Restriction<T>... restrictions) {
        return new CompositeRestrictionRecord<>(CompositeRestriction.Type.ANY,
                List.of(restrictions));
    }

    /**
     * Creates a restriction to check if a attribute is between two values.
     *
     * <p>This method combines two restrictions: one for greater than or equal to the
     * lower bound and another for less than or equal to the upper bound.</p>
     *
     * <pre>{@code
     * Restriction<Animal> restriction = Restrict.between(10, 20, "age");
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param <V> the type of the attribute value
     * @param min the minimum value (inclusive)
     * @param max the maximum value (inclusive)
     * @param attribute the attribute name
     * @return a restriction for values between the specified range
     */
    public static <T, V extends Comparable<V>> Restriction<T> between(V min,
                                                                      V max,
                                                                          String attribute) {
        return all(greaterThanEqual(min, attribute),
                lessThanEqual(max, attribute));
    }

    // TODO Need to think more about how to best cover negation of multiple
    // and then make negation of Single consistent with it

    /**
     * Creates a restriction to check if a text attribute contains the specified substring.
     *
     * <p>This method generates a {@link TextRestriction} that matches any value containing the given substring.
     * It is particularly useful for "LIKE" operations with wildcards for flexible matching.</p>
     *
     * <pre>{@code
     * TextRestriction<Person> restriction = Restrict.contains("Smith", "lastName");
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param substring the substring to check for
     * @param attribute the attribute name
     * @return a {@link TextRestriction} representing the condition
     * @throws NullPointerException if the substring or attribute is null
     */
    public static <T> TextRestriction<T> contains(String substring, String attribute) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, true, substring, true);
        return new TextRestrictionRecord<>(attribute, Operator.LIKE, ESCAPED, pattern);
    }

    /**
     * Creates a restriction to check if a text attribute ends with the specified suffix.
     *
     * <p>This method generates a {@link TextRestriction} that matches any value ending with the given suffix.
     * It is ideal for "LIKE" operations with trailing wildcards.</p>
     *
     * <pre>{@code
     * TextRestriction<Book> restriction = Restrict.endsWith("Guide", "title");
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param suffix the suffix to check for
     * @param attribute the attribute name
     * @return a {@link TextRestriction} representing the condition
     * @throws NullPointerException if the suffix or attribute is null
     */
    public static <T> TextRestriction<T> endsWith(String suffix, String attribute) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, true, suffix, false);
        return new TextRestrictionRecord<>(attribute, Operator.LIKE, ESCAPED, pattern);
    }

    /**
     * Creates a restriction to check if a attribute is equal to the specified value.
     *
     * <p>This method generates a {@link BasicRestriction} for attributes where the value must exactly match
     * the specified value. It is applicable to various data types.</p>
     *
     * <pre>{@code
     * Restriction<Animal> restriction = Restrict.equalTo("Dog", "species");
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param value the value to check for equality
     * @param attribute the attribute name
     * @return a {@link Restriction} representing the condition
     * @throws NullPointerException if the value or attribute is null
     */
    public static <T> Restriction<T> equalTo(Object value, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.EQUAL, value);
    }

    /**
     * Creates a restriction to check if a text attribute is equal to the specified value.
     *
     * <p>This method generates a {@link TextRestriction} for text-based attributes where the value must
     * exactly match the specified value.</p>
     *
     * <pre>{@code
     * TextRestriction<Person> restriction = Restrict.equalTo("Alice", "firstName");
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param value the value to check for equality
     * @param attribute the attribute name
     * @return a {@link TextRestriction} representing the condition
     * @throws NullPointerException if the value or attribute is null
     */
    public static <T> TextRestriction<T> equalTo(String value, String attribute) {
        return new TextRestrictionRecord<>(attribute, Operator.EQUAL, value);
    }

    /**
     * Creates a restriction to check if a attribute's value is greater than a specified value.
     *
     * <p>Use this for numeric or comparable attributes.</p>
     *
     * <pre>{@code
     * Restriction<Animal> restriction = Restrict.greaterThan(5, "age");
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param <V> the type of the attribute value
     * @param value the value to compare
     * @param attribute the attribute name
     * @return a restriction that matches attributes greater than the specified value
     */
    public static <T, V extends Comparable<V>> Restriction<T> greaterThan(V value, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.GREATER_THAN, value);
    }

    /**
     * Creates a restriction to check if a text attribute is greater than a specified value.
     *
     * <p>Primarily used for text-based comparisons, this method creates a {@link TextRestriction}
     * for attributes where the value must lexicographically exceed the given value.</p>
     *
     * <pre>{@code
     * TextRestriction<Person> restriction = Restrict.greaterThan("Alice", "name");
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param value the value to compare
     * @param attribute the attribute name
     * @return a {@link TextRestriction} representing the condition
     * @throws NullPointerException if the value or attribute is null
     */
    public static <T> TextRestriction<T> greaterThan(String value, String attribute) {
        return new TextRestrictionRecord<>(attribute, Operator.GREATER_THAN, value);
    }

    /**
     * Creates a restriction to check if a attribute is greater than or equal to a specified value.
     *
     * <p>Useful for numeric or comparable attributes, this method creates a {@link BasicRestriction}
     * for comparisons where the value must be at least the given threshold.</p>
     *
     * <pre>{@code
     * Restriction<Book> restriction = Restrict.greaterThanEqual(2010, "publicationYear");
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param <V> the type of the attribute value
     * @param value the value to compare
     * @param attribute the attribute name
     * @return a {@link Restriction} representing the condition
     * @throws NullPointerException if the value or attribute is null
     */
    public static <T, V extends Comparable<V>> Restriction<T> greaterThanEqual(V value, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.GREATER_THAN_EQUAL, value);
    }

    /**
     * Creates a restriction to check if a text attribute is greater than or equal to a specified value.
     *
     * <p>This method is ideal for lexicographic comparisons of text-based attributes.</p>
     *
     * <pre>{@code
     * TextRestriction<Person> restriction = Restrict.greaterThanEqual("Alice", "name");
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param value the value to compare
     * @param attribute the attribute name
     * @return a {@link TextRestriction} representing the condition
     * @throws NullPointerException if the value or attribute is null
     */
    public static <T> TextRestriction<T> greaterThanEqual(String value, String attribute) {
        return new TextRestrictionRecord<>(attribute, Operator.GREATER_THAN_EQUAL, value);
    }

    /**
     * Creates a restriction to check if a attribute is within a set of specified values.
     *
     * <p>Commonly used for filtering attributes against a predefined list of acceptable values.</p>
     *
     * <pre>{@code
     * Restriction<Animal> restriction = Restrict.in(Set.of("Cat", "Dog", "Rabbit"), "species");
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param values the set of values to compare against
     * @param attribute the attribute name
     * @return a {@link Restriction} representing the condition
     * @throws NullPointerException if the values or attribute is null
     */
    public static <T> Restriction<T> in(Set<Object> values, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.IN, values);
    }

    /**
     * Creates a restriction to check if a attribute is less than a specified value.
     *
     * <p>This method is applicable to numeric or comparable attributes.</p>
     *
     * <pre>{@code
     * Restriction<Book> restriction = Restrict.lessThan(300, "pageCount");
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param <V> the type of the attribute value
     * @param value the value to compare
     * @param attribute the attribute name
     * @return a {@link Restriction} representing the condition
     * @throws NullPointerException if the value or attribute is null
     */
    public static <T, V extends Comparable<V>> Restriction<T> lessThan(V value, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.LESS_THAN, value);
    }

    /**
     * Creates a restriction to check if a text attribute is less than a specified value.
     *
     * <p>Useful for lexicographic comparisons of text attributes.</p>
     *
     * <pre>{@code
     * TextRestriction<Person> restriction = Restrict.lessThan("Zoe", "name");
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param value the value to compare
     * @param attribute the attribute name
     * @return a {@link TextRestriction} representing the condition
     * @throws NullPointerException if the value or attribute is null
     */
    public static <T> TextRestriction<T> lessThan(String value, String attribute) {
        return new TextRestrictionRecord<>(attribute, Operator.LESS_THAN, value);
    }

    /**
     * Creates a restriction to check if a attribute is less than or equal to a specified value.
     *
     * <p>This method is applicable to numeric or comparable attributes where the value must not exceed
     * the specified threshold.</p>
     *
     * <pre>{@code
     * Restriction<Animal> restriction = Restrict.lessThanEqual(10, "age");
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param <V> the type of the attribute value
     * @param value the value to compare
     * @param attribute the attribute name
     * @return a {@link Restriction} representing the condition
     * @throws NullPointerException if the value or attribute is null
     */
    public static <T, V extends Comparable<V>> Restriction<T> lessThanEqual(V value, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.LESS_THAN_EQUAL, value);
    }

    /**
     * Creates a restriction to check if a text attribute is less than or equal to a specified value.
     *
     * <p>Primarily used for lexicographic comparisons of text attributes.</p>
     *
     * <pre>{@code
     * TextRestriction<Book> restriction = Restrict.lessThanEqual("Advanced Java", "title");
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param value the value to compare
     * @param attribute the attribute name
     * @return a {@link TextRestriction} representing the condition
     * @throws NullPointerException if the value or attribute is null
     */
    public static <T> TextRestriction<T> lessThanEqual(String value, String attribute) {
        return new TextRestrictionRecord<>(attribute, Operator.LESS_THAN_EQUAL, value);
    }

    // TODO this would be possible if Pattern is added, but is it even useful?
    //public static <T> TextRestriction<T> like(Pattern pattern, String attribute) {
    //    return new TextRestriction<>(attribute, Operator.LIKE, ESCAPED, pattern);
    //}

    public static <T> TextRestriction<T> like(String pattern, String attribute) {
        return new TextRestrictionRecord<>(attribute, Operator.LIKE, pattern);
    }

    public static <T> TextRestriction<T> like(String pattern,
                                              char charWildcard,
                                              char stringWildcard,
                                              String attribute) {
        String p = toLikeEscaped(charWildcard, stringWildcard, false, pattern, false);
        return new TextRestrictionRecord<>(attribute, Operator.LIKE, ESCAPED, p);
    }

    // convenience method for those who would prefer to avoid .negate()
    /**
     * Creates a restriction that negates the provided restriction.
     *
     * <p>This method is a convenience for applying the logical NOT operation to an existing restriction.
     * It inverts the logic of the given restriction by calling its {@code negate()} method.</p>
     *
     * <pre>{@code
     * Restriction<Person> ageRestriction = Restrict.greaterThan(18, "age");
     * Restriction<Person> notAgeRestriction = Restrict.not(ageRestriction);
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param restriction the restriction to negate
     * @return a new restriction representing the negated condition
     * @throws NullPointerException if the restriction is null
     */
    public static <T> Restriction<T> not(Restriction<T> restriction) {
        Objects.requireNonNull(restriction, "Restriction must not be null");
        return restriction.negate();
    }

    /**
     * Creates a restriction for attributes that must not equal the specified value.
     *
     * <p>This method generates a {@link Restriction} for "NOT EQUAL" operations on attributes
     * of any object type.</p>
     *
     * <pre>{@code
     * Restriction<Book> restriction = Restrict.notEqualTo("Java Concurrency in Practice", "title");
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param value the value that the attribute must not equal
     * @param attribute the attribute name
     * @return a {@link Restriction} representing the condition
     * @throws NullPointerException if the value or attribute is null
     */
    public static <T> Restriction<T> notEqualTo(Object value, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.NOT_EQUAL, value);
    }

    /**
     * Creates a text-based restriction for attributes that must not equal the specified string value.
     *
     * <p>This method generates a {@link TextRestriction} for "NOT EQUAL" operations specifically
     * for text attributes, allowing additional text-specific features if needed.</p>
     *
     * <pre>{@code
     * TextRestriction<Animal> restriction = Restrict.notEqualTo("Lion", "species");
     * }</pre>
     *
     * @param <T> the type of the entity
     * @param value the string value that the attribute must not equal
     * @param attribute the attribute name
     * @return a {@link TextRestriction} representing the condition
     * @throws NullPointerException if the value or attribute is null
     */
    public static <T> TextRestriction<T> notEqualTo(String value, String attribute) {
        return new TextRestrictionRecord<>(attribute, Operator.NOT_EQUAL, value);
    }

    public static <T> TextRestriction<T> notContains(String substring, String attribute) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, true, substring, true);
        return new TextRestrictionRecord<>(attribute, Operator.NOT_LIKE, ESCAPED, pattern);
    }

    public static <T> TextRestriction<T> notEndsWith(String suffix, String attribute) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, true, suffix, false);
        return new TextRestrictionRecord<>(attribute, Operator.NOT_LIKE, ESCAPED, pattern);
    }

    public static <T> Restriction<T> notIn(Set<Object> values, String attribute) {
        return new BasicRestrictionRecord<>(attribute, Operator.NOT_IN, values);
    }

    public static <T> TextRestriction<T> notLike(String pattern, String attribute) {
        return new TextRestrictionRecord<>(attribute, Operator.NOT_LIKE, pattern);
    }

    public static <T> TextRestriction<T> notLike(String pattern,
                                                 char charWildcard,
                                                 char stringWildcard,
                                                 String attribute) {
        String p = toLikeEscaped(charWildcard, stringWildcard, false, pattern, false);
        return new TextRestrictionRecord<>(attribute, Operator.NOT_LIKE, ESCAPED, p);
    }

    public static <T> TextRestriction<T> notStartsWith(String prefix, String attribute) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, false, prefix, true);
        return new TextRestrictionRecord<>(attribute, Operator.NOT_LIKE, ESCAPED, pattern);
    }

    public static <T> TextRestriction<T> startsWith(String prefix, String attribute) {
        String pattern = toLikeEscaped(CHAR_WILDCARD, STRING_WILDCARD, false, prefix, true);
        return new TextRestrictionRecord<>(attribute, Operator.LIKE, ESCAPED, pattern);
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
