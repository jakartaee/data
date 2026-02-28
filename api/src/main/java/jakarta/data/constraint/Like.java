/*
 * Copyright (c) 2024,2026 Contributors to the Eclipse Foundation
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

import static jakarta.data.constraint.LikeRecord.ESCAPE;
import static jakarta.data.constraint.LikeRecord.STRING_WILDCARD;
import static jakarta.data.constraint.LikeRecord.translate;

import jakarta.data.expression.TextExpression;
import jakarta.data.messages.Messages;
import jakarta.data.metamodel.Attribute;
import jakarta.data.repository.Is;
import jakarta.data.restrict.Restriction;
import jakarta.data.spi.expression.literal.StringLiteral;

/**
 * <p>A constraint that requires matching a pattern.</p>
 *
 * <p>A parameter-based repository method can impose a constraint on an
 * entity attribute by defining a method parameter that is of type
 * {@code Like} or is annotated {@link Is @Is(Like.class)} and is of type
 * {@link String}. For example,</p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; matchVIN(&#64;By(_Car.VIN) Like vinPattern);
 *
 * &#64;Find // requires the -parameters compiler option to preserve parameter names
 * List&lt;Car&gt; makeAndModel(Like make, Like model);
 *
 * &#64;Find
 * List&lt;Car&gt; search(&#64;By(_Car.MAKE) &#64;Is(Like.class) String makePattern,
 *                  &#64;By(_Car.MODEL) &#64;Is(Like.class) String modelPattern,
 *                  Order&lt;Car&gt; sorts);
 *
 * ...
 *
 * found = cars.matchVIN(Like.prefix("1GM"));
 *
 * found = cars.makeAndModel(Like.contains(makeSubstring),
 *                           Like.contains(modelSubstring));
 *
 * found = cars.search("Chev%",
 *                     "% EV",
 *                     Order.by(_Car.price.desc()));
 * </pre>
 *
 * <p>Repository methods can also accept {@code Like} constraints at run time
 * in the form of a {@link Restriction} on a {@link TextExpression}.
 * For example,</p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; searchAll(Restriction&lt;Car&gt; restrict, Order&lt;Car&gt; sorts);
 *
 * ...
 *
 * found = cars.searchAll(_Car.make.startsWith("Chev"),
 *                        Order.by(_Car.model.asc(),
 *                                 _Car.year.desc(),
 *                                 _Car.price.asc());
 * </pre>
 *
 * <p>The {@linkplain Attribute entity and static metamodel} for the code
 * examples within this class are shown in the {@link Attribute} Javadoc.
 * </p>
 *
 * @since 1.1
 */
public interface Like extends Constraint<String> {

    /**
     * <p>Requires that the constraint target match the given {@code pattern},
     * in which {@code _} and {@code %} represent wildcards. The supplied
     * pattern has no escape character.</p>
     *
     * <p>For example, the following requires that the first 3 positions of a
     * VIN number are {@code JHM}, positions 4 through 6 are any character,
     * position 7 is {@code E}, and the remaining positions are any characters,
     * </p>
     *
     * <pre>
     * found = cars.matchVIN(Like.pattern("JHM___E%"));
     * </pre>
     *
     * @param pattern a pattern in which {@code _} matches a single character
     *                and {@code %} matches 0 or more characters.
     * @return a {@code Like} constraint.
     * @throws NullPointerException if the pattern is {@code null}.
     */
    static Like pattern(String pattern) {
        Messages.requireNonNull(pattern, "pattern");

        String patternEscaped = LikeRecord.escape(pattern, false);
        StringLiteral escaped = StringLiteral.of(patternEscaped);
        StringLiteral unescaped = pattern == patternEscaped
                ? escaped
                : StringLiteral.of(pattern);

        return new LikeRecord(unescaped, escaped, ESCAPE);
    }

    /**
     * <p>Requires that the constraint target match the given {@code pattern},
     * in which the given characters represent wildcards. The supplied
     * pattern has no escape character.</p>
     *
     * <p>For example, the following requires that the first 3 positions of a
     * VIN number are {@code JHM}, positions 4 through 6 are any character,
     * position 7 is {@code F}, and the remaining positions are any characters,
     * </p>
     *
     * <pre>
     * found = cars.matchVIN(Like.pattern("JHM???F*", '?', '*'));
     * </pre>
     *
     * @param pattern        a pattern that can include the given wildcard
     *                       characters.
     * @param charWildcard   wildcard that represents any single character.
     * @param stringWildcard wildcard that represents 0 or more characters.
     * @return a {@code Like} constraint.
     * @throws NullPointerException if the pattern is {@code null}.
     */
    static Like pattern(String pattern, char charWildcard, char stringWildcard) {
        Messages.requireNonNull(pattern, "pattern");

        StringLiteral escaped =
                StringLiteral.of(translate(pattern,
                                           charWildcard,
                                           stringWildcard,
                                           ESCAPE,
                                           false));

        StringLiteral unescaped =
                escaped.value().indexOf(ESCAPE) < 0
                        ? escaped
                        : null;

        return new LikeRecord(unescaped, escaped, ESCAPE);
    }

    /**
     * <p>Requires that the constraint target match the given {@code pattern},
     * in which the given characters represent wildcards and escape.</p>
     *
     * <p>For example, the following requires that the first 3 positions of a
     * VIN number are {@code JHM}, positions 4 through 6 are any character,
     * position 7 is {@code C}, and the remaining positions are any characters,
     * </p>
     *
     * <pre>
     * found = cars.matchVIN(Like.pattern("JHM---^CC", '-', 'C', '^'));
     * </pre>
     *
     * @param pattern        a pattern that can include the given wildcard
     *                       characters and escape character.
     * @param charWildcard   wildcard that represents any single character.
     * @param stringWildcard wildcard that represents 0 or more characters.
     * @param escape         escape character.
     * @return a {@code Like} constraint.
     * @throws NullPointerException if the pattern is {@code null}.
     */
    static Like pattern(String pattern,
                        char charWildcard,
                        char stringWildcard,
                        char escape) {
        Messages.requireNonNull(pattern, "pattern");

        StringLiteral escaped =
                StringLiteral.of(translate(pattern,
                                           charWildcard,
                                           stringWildcard,
                                           escape,
                                           true));

        StringLiteral unescaped = escaped.value().indexOf(escape) < 0
                ? escaped
                : null;

        return new LikeRecord(unescaped, escaped, escape);
    }

    /**
     * <p>Requires that the constraint target match the given {@code pattern}
     * expression, in which {@code _} and {@code %} represent wildcards and the
     * given character represents escape.</p>
     *
     * @param pattern an expression representing a pattern that can include the
     *                given escape character.
     * @param escape  escape character.
     * @return a {@code Like} constraint.
     * @throws NullPointerException if the pattern expression is {@code null}.
     */
    static Like pattern(TextExpression<?> pattern, char escape) {
        Messages.requireNonNull(pattern, "pattern");

        StringLiteral unescaped =
                pattern instanceof StringLiteral escaped &&
                escaped.value().indexOf(escape) < 0
                        ? escaped
                        : null;

        return new LikeRecord(unescaped, pattern, escape);
    }

    /**
     * <p>Requires that the constraint target begin with the given
     * {@code prefix}.</p>
     *
     * <p>For example, the following requires that the first 3 positions of a
     * VIN number are the characters {@code JTP}.</p>
     *
     * <pre>
     * found = cars.matchVIN(Like.prefix("JTP"));
     * </pre>
     *
     * @param prefix text that the beginning characters of the constraint
     *               target must exactly match.
     * @return a {@code Like} constraint.
     * @throws NullPointerException if the prefix is {@code null}.
     */
    static Like prefix(String prefix) {
        Messages.requireNonNull(prefix, "prefix");

        String prefixEscaped = LikeRecord.escape(prefix, true);
        StringLiteral escaped = StringLiteral.of(prefixEscaped + STRING_WILDCARD);
        StringLiteral unescaped = prefix == prefixEscaped
                ? escaped
                : prefix.indexOf(STRING_WILDCARD) < 0
                        ? StringLiteral.of(prefix + STRING_WILDCARD)
                        : null;

        return new LikeRecord(unescaped, escaped, ESCAPE);
    }

    /**
     * <p>Requires that the constraint target contain the given
     * {@code substring}.</p>
     *
     * <p>For example, the following requires that the entity attribute value
     * contain the character string {@code Hybrid},</p>
     *
     * <pre>
     * found = cars.makeAndModel(Like.literal(make),
     *                           Like.substring("Hybrid"));
     * </pre>
     *
     * @param substring text that must be contained in the constraint target.
     * @return a {@code Like} constraint.
     * @throws NullPointerException if the substring is {@code null}.
     */
    static Like substring(String substring) {
        Messages.requireNonNull(substring, "substring");

        String substringEscaped = LikeRecord.escape(substring, true);

        StringLiteral escaped = StringLiteral.of(
                STRING_WILDCARD + substringEscaped + STRING_WILDCARD);

        StringLiteral unescaped = substring == substringEscaped
                ? escaped
                : substring.indexOf(STRING_WILDCARD) < 0
                        ? StringLiteral.of(
                                STRING_WILDCARD + substring + STRING_WILDCARD)
                        : null;

        return new LikeRecord(unescaped, escaped, ESCAPE);
    }

    /**
     * <p>Requires that the constraint target end with the given
     * {@code suffix}.</p>
     *
     * <p>For example, the following requires that the entity attribute value
     * end with the characters {@code EV},</p>
     *
     * <pre>
     * found = cars.makeAndModel(Like.literal(make),
     *                           Like.suffix("EV"));
     * </pre>
     *
     * @param suffix text that the ending characters of the constraint
     *               target must exactly match.
     * @return a {@code Like} constraint.
     * @throws NullPointerException if the suffix is {@code null}.
     */
    static Like suffix(String suffix) {
        Messages.requireNonNull(suffix, "suffix");

        String suffixEscaped = LikeRecord.escape(suffix, true);
        StringLiteral escaped = StringLiteral.of(STRING_WILDCARD + suffixEscaped);
        StringLiteral unescaped = suffix == suffixEscaped
                ? escaped
                : suffix.indexOf(STRING_WILDCARD) < 0
                        ? StringLiteral.of(STRING_WILDCARD + suffix)
                        : null;

        return new LikeRecord(unescaped, escaped, ESCAPE);
    }

    /**
     * <p>Requires that the constraint target consist of exactly the same
     * characters as the given {@code value}. A repository method that does not
     * require the flexibility of allowing different types of {@code Like}
     * constraints should use the {@link EqualTo} constraint instead.</p>
     *
     * <p>For example, the following requires a VIN number to exactly match,
     * </p>
     *
     * <pre>
     * found = cars.matchVIN(Like.literal(vin));
     * </pre>
     *
     * @param value a value that must exactly match.
     * @return a {@code Like} constraint.
     * @throws NullPointerException if the literal value is {@code null}.
     */
    static Like literal(String value) {
        Messages.requireNonNull(value, "value");

        String valueEscaped = LikeRecord.escape(value, true);
        StringLiteral escaped = StringLiteral.of(valueEscaped);
        StringLiteral unescaped = value == valueEscaped
                ? escaped
                : StringLiteral.of(value);

        return new LikeRecord(unescaped, escaped, ESCAPE);
    }

    /**
     * <p>The escape character to use for the {@link #escapedPattern()}.
     * The pattern is assigned an escape character even if the application
     * did not supply one when requesting the {@code Like} constraint.</p>
     *
     * @return the escape character.
     */
    char escape();

    /**
     * <p>An expression that evaluates to a pattern against which the
     * constraint target must match.</p>
     *
     * <p>Any {@linkplain #pattern(String, char, char) custom wildcards} that
     * were supplied appear as {@code _} and {@code %} within the pattern,
     * with the {@link #escape() escape} character used to indicate where
     * characters are interpreted literally rather than as wildcards or
     * escape.</p>
     *
     * <p>For example, {@code Like.pattern("is --.-*% of", '-', '*', '^')}
     * is represented as {@code is __._%^% of} where {@code ^} is the escape
     * character.</p>
     *
     * @return an expression representing the pattern.
     */
    TextExpression<?> escapedPattern();

    /**
     * <p>An expression that evaluates to an unescaped pattern against which
     * the constraint target must match.</p>
     *
     * <p>Any {@linkplain #pattern(String, char, char) custom wildcards} that
     * were supplied appear as {@code _} and {@code %} within the pattern.</p>
     *
     * <p>An unescaped pattern is not always available, in which case this
     * method returns {@code null}.
     *
     * @return an expression representing an unescaped pattern, if available.
     *         Otherwise {@code null}.
     */
    TextExpression<?> unescapedPattern();
}
