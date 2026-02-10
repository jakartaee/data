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

import static jakarta.data.constraint.LikeRecord.CHAR_WILDCARD;
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
 * <p>A constraint that requires not matching a pattern.</p>
 *
 * <p>A parameter-based repository method can impose a constraint on an
 * entity attribute by defining a method parameter that is of type
 * {@code NotLike} or is annotated {@link Is @Is(NotLike.class)} and is of
 * type {@link String}. For example,</p>
 *
 * <pre>{@code
 * @Find
 * List<Car> matchVIN(@By(_Car.VIN) NotLike pattern);
 *
 * @Find
 * List<Car> ofMakeNotModel(@By(_Car.MAKE) String manufacturer,
 *                          @By(_Car.MODEL) @Is(NotLike.class) String excludePattern,
 *                          Order<Car> sorts);
 *
 * ...
 *
 * found = cars.matchVIN(NotLike.prefix("1GM"));
 *
 * found = cars.ofMakeNotModel("Jakarta Motors",
 *                             "%Hybrid%",
 *                             Order.by(_Car.price.desc()));
 * }</pre>
 *
 * <p>Repository methods can also accept {@code NotLike} constraints at
 * run time in the form of a {@link Restriction} on a {@link TextExpression}.
 * For example,</p>
 *
 * <pre>{@code
 * @Find
 * List<Car> searchAll(Restriction<Car> restrict, Order<Car> sorts);
 *
 * ...
 *
 * found = cars.searchAll(Restrict.all(_Car.make.equalTo("Jakarta Motors"),
 *                                     _Car.model.notContains("Electric"),
 *                                     _Car.model.notEndsWith("EV")),
 *                        Order.by(_Car.model.asc(),
 *                                 _Car.year.desc(),
 *                                 _Car.price.asc()));
 * }</pre>
 *
 * <p>The {@linkplain Attribute entity and static metamodel} for the code
 * examples within this class are shown in the {@link Attribute} Javadoc.
 * </p>
 *
 * @since 1.1
 */
public interface NotLike extends Constraint<String> {

    /**
     * <p>Requires that the constraint target not match the given
     * {@code pattern}, in which {@code _} and {@code %} represent wildcards.
     * The supplied pattern has no escape character.</p>
     *
     * <p>For example, the following requires that the VIN number not have
     * {@code JHM} as its first 3 character positions and {@code E} in
     * character position 7.</p>
     *
     * <pre>{@code
     *     found = cars.matchVIN(NotLike.pattern("JHM___E%"));
     * }</pre>
     *
     * @param pattern a pattern in which {@code _} matches a single character
     *                and {@code %} matches 0 or more characters.
     * @return a {@code NotLike} constraint.
     * @throws NullPointerException if the pattern is {@code null}.
     */
    static NotLike pattern(String pattern) {
        return pattern(pattern, CHAR_WILDCARD, STRING_WILDCARD);
    }

    /**
     * <p>Requires that the constraint target not match the given
     * {@code pattern}, in which the given characters represent wildcards.
     * The supplied pattern has no escape character.</p>
     *
     * <p>For example, the following requires that the VIN number not have
     * {@code JHM} as its first 3 character positions and {@code F} in
     * character position 7.</p>
     *
     * <pre>{@code
     *     found = cars.matchVIN(NotLike.pattern("JHM???F*", '?', '*'));
     * }</pre>
     *
     * @param pattern        a pattern that can include the given wildcard
     *                       characters.
     * @param charWildcard   wildcard that represents any single character.
     * @param stringWildcard wildcard that represents 0 or more characters.
     * @return a {@code NotLike} constraint.
     * @throws NullPointerException if the pattern is {@code null}.
     */
    static NotLike pattern(String pattern, char charWildcard, char stringWildcard) {
        if (pattern == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "pattern"));
        }

        StringLiteral expression = StringLiteral.of(
                translate(pattern, charWildcard, stringWildcard, ESCAPE, false));

        return new NotLikeRecord(expression, ESCAPE);
    }

    /**
     * <p>Requires that the constraint target not match the given
     * {@code pattern}, in which the given characters represent wildcards and
     * escape.</p>
     *
     * <p>For example, the following requires that the VIN number not have
     * {@code JHM} as its first 3 character positions and {@code C} in
     * character position 7.</p>
     *
     * <pre>{@code
     *     found = cars.matchVIN(Like.pattern("JHM---^CC", '-', 'C', '^'));
     * }</pre>
     *
     * @param pattern        a pattern that can include the given wildcard
     *                       characters and escape character.
     * @param charWildcard   wildcard that represents any single character.
     * @param stringWildcard wildcard that represents 0 or more characters.
     * @param escape         escape character.
     * @return a {@code NotLike} constraint.
     * @throws NullPointerException if the pattern is {@code null}.
     */
    static NotLike pattern(String pattern, char charWildcard, char stringWildcard, char escape) {
        if (pattern == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "pattern"));
        }

        StringLiteral expression = StringLiteral.of(
                translate(pattern, charWildcard, stringWildcard, escape, true));
        return new NotLikeRecord(expression, escape);
    }

    /**
     * <p>Requires that the constraint target not match the given
     * {@code pattern} expression, in which {@code _} and {@code %} represent
     * wildcards and the given character represents escape.</p>
     *
     * @param pattern an expression representing a pattern that can include the
     *                given escape character.
     * @param escape  escape character.
     * @return a {@code NotLike} constraint.
     * @throws NullPointerException if the pattern expression is {@code null}.
     */
    static NotLike pattern(TextExpression<?> pattern, char escape) {
        if (pattern == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "pattern"));
        }

        return new NotLikeRecord(pattern, escape);
    }

    /**
     * <p>Requires that the constraint target not begin with the given
     * {@code prefix}.</p>
     *
     * <p>For example, the following requires that the first 3 positions of a
     * VIN number are not the characters {@code JTP}.</p>
     *
     * <pre>{@code
     *     found = cars.matchVIN(NotLike.prefix("JTP"));
     * }</pre>
     *
     * @param prefix text that the beginning characters of the constraint
     *               target must not match.
     * @return a {@code NotLike} constraint.
     * @throws NullPointerException if the prefix is {@code null}.
     */
    static NotLike prefix(String prefix) {
        if (prefix == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "prefix"));
        }

        StringLiteral expression = StringLiteral.of(
                LikeRecord.escape(prefix) + STRING_WILDCARD);
        return new NotLikeRecord(expression, ESCAPE);
    }

    /**
     * <p>Requires that the constraint target not contain the given
     * {@code substring}.</p>
     *
     * <p>For example, the following requires that the entity attribute value
     * not contain the character string {@code Hybrid},</p>
     *
     * <pre>{@code
     *     found = cars.ofModel(NotLike.substring("Hybrid"));
     * }</pre>
     *
     * @param substring text that must not be contained in the constraint
     *                  target.
     * @return a {@code NotLike} constraint.
     * @throws NullPointerException if the substring is {@code null}.
     */
    static NotLike substring(String substring) {
        if (substring == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "substring"));
        }

        StringLiteral expression = StringLiteral.of(
                STRING_WILDCARD + LikeRecord.escape(substring) + STRING_WILDCARD);
        return new NotLikeRecord(expression, ESCAPE);
    }

    /**
     * <p>Requires that the constraint target not end with the given
     * {@code suffix}.</p>
     *
     * <p>For example, the following requires that the entity attribute value
     * not end with the characters {@code EV},</p>
     *
     * <pre>{@code
     *     found = cars.ofModel(NotLike.suffix("EV"));
     * }</pre>
     *
     * @param suffix text that the ending characters of the constraint
     *               target must not match.
     * @return a {@code NotLike} constraint.
     * @throws NullPointerException if the suffix is {@code null}.
     */
    static NotLike suffix(String suffix) {
        if (suffix == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "suffix"));
        }

        StringLiteral expression = StringLiteral.of(
                STRING_WILDCARD + LikeRecord.escape(suffix));
        return new NotLikeRecord(expression, ESCAPE);
    }

    /**
     * <p>Requires that the constraint target not consist of the same
     * characters as the given {@code value}. A repository method that does not
     * require the flexibility of allowing different types of {@code NotLike}
     * constraints should use the {@link NotEqualTo} constraint instead.</p>
     *
     * <p>For example, the following requires a VIN number to exactly match,
     * </p>
     *
     * <pre>{@code
     *     found = cars.ofModel(NotLike.literal("J-150"));
     * }</pre>
     *
     * @param value a value that must not match the constraint target.
     * @return a {@code NotLike} constraint.
     * @throws NullPointerException if the literal value is {@code null}.
     */
    static NotLike literal(String value) {
        if (value == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "value"));
        }

        StringLiteral expression = StringLiteral.of(
                LikeRecord.escape(value));
        return new NotLikeRecord(expression, ESCAPE);
    }

    /**
     * <p>The escape character to use for the {@link #pattern()}. The pattern
     * is assigned an escape character even if the application did not supply
     * one when requesting the {@code NotLike} constraint.</p>
     *
     * @return the escape character.
     */
    char escape();

    /**
     * <p>An expression that evaluates to a pattern against which the
     * constraint target must not match.</p>
     *
     * <p>Any {@linkplain #pattern(String, char, char) custom wildcards} that
     * were supplied appear as {@code _} and {@code %} within the pattern,
     * with the {@link #escape() escape} character used to indicate where
     * characters are interpreted literally rather than as wildcards or
     * escape.</p>
     *
     * <p>For example, {@code NotLike.pattern("is --.-*% of", '-', '*', '^')"}
     * is represented as {@code is __._%^% of} where {@code ^} is the escape
     * character.</p>
     *
     * @return an expression representing the pattern.
     */
    TextExpression<?> pattern();
}
