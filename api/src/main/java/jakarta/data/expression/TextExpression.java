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
package jakarta.data.expression;

import static jakarta.data.spi.expression.function.NumericFunctionExpression.LENGTH;
import static jakarta.data.spi.expression.function.TextFunctionExpression.CONCAT;
import static jakarta.data.spi.expression.function.TextFunctionExpression.LEFT;
import static jakarta.data.spi.expression.function.TextFunctionExpression.LOWER;
import static jakarta.data.spi.expression.function.TextFunctionExpression.RIGHT;
import static jakarta.data.spi.expression.function.TextFunctionExpression.UPPER;

import jakarta.data.constraint.Like;
import jakarta.data.constraint.NotLike;
import jakarta.data.metamodel.Attribute;
import jakarta.data.restrict.BasicRestriction;
import jakarta.data.restrict.Restriction;
import jakarta.data.spi.expression.function.NumericFunctionExpression;
import jakarta.data.spi.expression.function.TextFunctionExpression;

/**
 * <p>An {@linkplain Expression expression} that evaluates to a {@link String}
 * value.</p>
 *
 * <p>The {@linkplain Attribute entity and static metamodel} for the code
 * examples within this class are shown in the {@link Attribute} Javadoc.
 * </p>
 *
 * @param <T> entity type.
 * @since 1.1
 */
public interface TextExpression<T> extends ComparableExpression<T, String> {

    /**
     * Returns {@code String.class} as type of textual expressions.
     *
     * @return {@code String.class}.
     * @since 1.1
     */
    @Override
    default Class<String> type() {
        return String.class;
    }

    /**
     * <p>Represents the function to obtain the {@link String} value that is
     * formed by prepending the specified prefix onto the beginning of the
     * value to which the current expression evaluates.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make,
     *                         _Car.model.prepend("Model ").equalTo(model));
     * }</pre>
     *
     * @return an expression for the function that computes the concatenated
     *         value.
     * @throws NullPointerException if the prefix is {@code null}.
     */
    default TextExpression<T> prepend(String prefix) {
        return TextFunctionExpression.of(CONCAT, prefix, this);
    }

    /**
     * <p>Represents the function to obtain the {@link String} value that is
     * formed by appending the specified suffix onto the end of the value to
     * which the current expression evaluates.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make,
     *                         _Car.model.append(" Hybrid").equalTo(hybridModel));
     * }</pre>
     *
     * @return an expression for the function that computes the concatenated
     *         value.
     * @throws NullPointerException if the suffix is {@code null}.
     */
    default TextExpression<T> append(String suffix) {
        return TextFunctionExpression.of(CONCAT, this, suffix);
    }

    /**
     * <p>Represents the function to obtain the {@link String} value that is
     * formed by prepending the prefix to which the specified expression
     * evaluates onto the beginning of the value to which the current
     * expression evaluates.</p>
     *
     * <p>Example:</p>
     * <pre>TODO {@code
     *     found = cars.search(
     *             make,
     *             _Car.model.prepend(_Car.year.append(" ")).equalTo(yearAndModel));
     * }</pre>
     *
     * @return an expression for the function that computes the concatenated
     *         value.
     * @throws NullPointerException if the prefix expression is {@code null}.
     */
    default TextExpression<T> prepend(
            TextExpression<? super T> prefixExpression) {
        return TextFunctionExpression.of(CONCAT, prefixExpression, this);
    }

    /**
     * <p>Represents the function to obtain the {@link String} value that is
     * formed by appending the suffix to which the specified expression
     * evaluates onto the end of the value to which the current expression
     * evaluates.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(_Car.make.append(' ').append(_Car.model).equalTo(makeAndModel));
     * }</pre>

     *
     * @return an expression for the function that computes the concatenated
     *         value.
     * @throws NullPointerException if the suffix expression is {@code null}.
     */
    default TextExpression<T> append(
            TextExpression<? super T> suffixExpression) {
        return TextFunctionExpression.of(CONCAT, suffixExpression, this);
    }

    /**
     * <p>Represents the function to obtain the upper case form of the value to
     * which the current expression evaluates.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(_Car.make.upper().startsWith("CHEV"));
     * }</pre>
     *
     * @return an expression for the function that computes the upper case form
     *         of the value.
     */
    default TextExpression<T> upper() {
        return TextFunctionExpression.of(UPPER, this);
    }

    /**
     * <p>Represents the function to obtain the lower case form of the value to
     * which the current expression evaluates.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make,
     *                         _Car.model.lower().startsWith("f-"));
     * }</pre>
     *
     * @return an expression for the function that computes the lower case form
     *         of the value.
     */
    default TextExpression<T> lower() {
        return TextFunctionExpression.of(LOWER, this);
    }

    /**
     * <p>Represents the function to obtain the specified number of characters
     * at the beginning of the textual value to which the current expression
     * evaluates.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     startsWithCX = cars.search(make,
     *                                _Car.model.left(2).equalTo("CX"));
     * }</pre>
     *
     * @return an expression for the function that obtains the leftmost
     *         characters.
     */
    default TextExpression<T> left(int length) {
        return TextFunctionExpression.of(LEFT, this, length);
    }

    /**
     * <p>Represents the function to obtain the specified number of characters
     * at the end of the textual value to which the current expression
     * evaluates.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     endsWithHybrid = cars.search(make,
     *                                  _Car.model.right(6).equalTo("Hybrid"));
     * }</pre>
     *
     * @return an expression for the function that obtains the rightmost
     *         characters.
     */
    default TextExpression<T> right(int length) {
        return TextFunctionExpression.of(RIGHT, this, length);
    }

    /**
     * <p>Represents the function to obtain the length of the textual value to
     * which the current expression evaluates.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     withModelNamesUpTo10Chars = cars.search(make,
     *                                             _Car.model.length().lessThanEqual(10));
     * }</pre>
     *
     * @return an expression for the function that obtains the length of the
     *         textual value.
     */
    default NumericExpression<T, Integer> length() {
        return NumericFunctionExpression.of(LENGTH, Integer.class, this);
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that is {@linkplain Like like} the specified
     * pattern.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make, _Car.model.like(Like.pattern("% Hybrid")));
     * }</pre>
     *
     * @param pattern pattern against which to compare. Must not be
     *        {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the pattern value is {@code null}.
     */
    default Restriction<T> like(Like pattern) {
        return BasicRestriction.of(this, pattern);
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that is {@linkplain Like#pattern(String) like} the
     * specified pattern.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(_Car.make.like("Chev%"));
     * }</pre>
     *
     * @param pattern pattern against which to compare. Must not be
     *        {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the pattern is {@code null}.
     */
    default Restriction<T> like(String pattern) {
        return BasicRestriction.of(this, Like.pattern(pattern));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that is
     * {@linkplain Like#pattern(String, char, char) like} the specified
     * pattern.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make, _Car.model.like("F-_50%", '_', '%'));
     * }</pre>
     *
     * @param pattern        pattern against which to compare. Must not be
     *                       {@code null}.
     * @param charWildcard   character that represents a position in the
     *                       pattern where any 1 character is considered to
     *                       match.
     * @param stringWildcard character that represents a position in the
     *                       pattern where any number of characters (including
     *                       0) are considered to match.
     * @return the restriction.
     * @throws NullPointerException if the pattern value is {@code null}.
     */
    default Restriction<T> like(
            String pattern, char charWildcard, char stringWildcard) {
        Like constraint = Like.pattern(pattern, charWildcard, stringWildcard);
        return BasicRestriction.of(this, constraint);
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that is
     * {@linkplain Like#pattern(String, char, char, char) like} the specified
     * pattern.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make, _Car.model.like("_R-V%", '_', '%', '^'));
     * }</pre>
     *
     * @param pattern        pattern against which to compare. Must not be
     *                       {@code null}.
     * @param charWildcard   character that represents a position in the
     *                       pattern where any 1 character is considered to
     *                       match.
     * @param stringWildcard character that represents a position in the
     *                       pattern where any number of characters (including
     *                       0) are considered to match.
     * @param escape         character to use as an escape character within the
     *                       pattern, indicating that the subsequent wildcard
     *                       or escape character must be interpreted as its
     *                       literal value rather than as a wildcard or escape.
     * @return the restriction.
     * @throws NullPointerException if the pattern value is {@code null}.
     */
    default Restriction<T> like(
            String pattern, char charWildcard, char stringWildcard, char escape) {
        Like constraint = Like.pattern(pattern, charWildcard, stringWildcard, escape);
        return BasicRestriction.of(this, constraint);
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that is
     * {@linkplain NotLike#pattern(String) not like} the specified pattern.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make, _Car.model.notLike("% EV"));
     * }</pre>
     *
     * @param pattern pattern against which to compare. Must not be
     *        {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the pattern value is {@code null}.
     */
    default Restriction<T> notLike(String pattern) {
        return BasicRestriction.of(this, NotLike.pattern(pattern));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that is
     * {@linkplain NotLike#pattern(String, char, char) not like} the specified
     * pattern.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make, _Car.model.notLike("CX-_0 *", '_', '*'));
     * }</pre>
     *
     * @param pattern        pattern against which to compare. Must not be
     *                       {@code null}.
     * @param charWildcard   character that represents a position in the
     *                       pattern where any 1 character is considered to
     *                       match.
     * @param stringWildcard character that represents a position in the
     *                       pattern where any number of characters (including
     *                       0) are considered to match.
     * @throws NullPointerException if the pattern value is {@code null}.
     */
    default Restriction<T> notLike(
            String pattern, char charWildcard, char stringWildcard) {
        NotLike constraint = NotLike.pattern(pattern, charWildcard, stringWildcard);
        return BasicRestriction.of(this, constraint);
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that is
     * {@linkplain NotLike#pattern(String, char, char, char) not like} the
     * specified pattern.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make, _Car.model.notLike("* EV*", '_', '*', '^'));
     * }</pre>
     *
     * @param pattern        pattern against which to compare. Must not be
     *                       {@code null}.
     * @param charWildcard   character that represents a position in the
     *                       pattern where any 1 character is considered to
     *                       match.
     * @param stringWildcard character that represents a position in the
     *                       pattern where any number of characters (including
     *                       0) are considered to match.
     * @param escape         character to use as an escape character within the
     *                       pattern, indicating that the subsequent wildcard
     *                       or escape character must be interpreted as its
     *                       literal value rather than as a wildcard or escape.
     * @return the restriction.
     * @throws NullPointerException if the pattern value is {@code null}.
     */
    default Restriction<T> notLike(
            String pattern, char charWildcard, char stringWildcard, char escape) {
        NotLike constraint = NotLike.pattern(pattern, charWildcard, stringWildcard, escape);
        return BasicRestriction.of(this, constraint);
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that {@linkplain Like#substring(String) contains}
     * the specified substring.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make, _Car.model.contains("Hybrid"));
     * }</pre>
     *
     * @param substring substring against which to compare. Must not be
     *        {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the substring value is {@code null}.
     */
    default Restriction<T> contains(String substring) {
        return BasicRestriction.of(this, Like.substring(substring));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that
     * {@linkplain NotLike#substring(String) does not contain} the specified
     * substring.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make, _Car.model.notContains(" EV"));
     * }</pre>
     *
     * @param substring substring against which to compare. Must not be
     *                  {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the substring value is {@code null}.
     */
    default Restriction<T> notContains(String substring) {
        return BasicRestriction.of(this, NotLike.substring(substring));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that {@linkplain Like#prefix(String) begins with}
     * the specified prefix.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(_Car.make.startsWith("Chev"));
     * }</pre>
     *
     * @param prefix prefix against which to compare. Must not be {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the prefix value is {@code null}.
     */
    default Restriction<T> startsWith(String prefix) {
        return BasicRestriction.of(this, Like.prefix(prefix));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that
     * {@linkplain NotLike#prefix(String) does not begin with} the specified
     * prefix.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make, _Car.model.notStartsWith("CR-"));
     * }</pre>
     *
     * @param prefix prefix against which to compare. Must not be {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the prefix value is {@code null}.
     */
    default Restriction<T> notStartsWith(String prefix) {
        return BasicRestriction.of(this, NotLike.prefix(prefix));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that {@linkplain NotLike#suffix(String) ends with}
     * the specified suffix.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make, _Car.model.endsWith(" EV"));
     * }</pre>
     *
     * @param suffix suffix against which to compare. Must not be {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the suffix value is {@code null}.
     */
    default Restriction<T> endsWith(String suffix) {
        return BasicRestriction.of(this, Like.suffix(suffix));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that
     * {@linkplain NotLike#suffix(String) does not end with} the specified
     * suffix.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make, _Car.model.notEndsWith(" Hybrid"));
     * }</pre>
     *
     * @param suffix suffix against which to compare. Must not be {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the suffix value is {@code null}.
     */
    default Restriction<T> notEndsWith(String suffix) {
        return BasicRestriction.of(this, NotLike.suffix(suffix));
    }

}
