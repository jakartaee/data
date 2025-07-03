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
package jakarta.data.constraint;

import java.util.Set;

import jakarta.data.Order;
import jakarta.data.metamodel.Attribute;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Is;
import jakarta.data.restrict.Restriction;

/**
 * <p>Supertype of interfaces that define constraints on entity attributes.</p>
 *
 * <p>Constraints are used on parameter-based {@link Find} and
 * {@link Delete} methods. Constraint parameters must be positioned before
 * special parameters (such as {@link Restriction} and {@link Order}) in the
 * method signature.</p>
 *
 * <h2>Method parameter</h2>
 *
 * <p>Constraint parameters can be subtypes of {@code Constraint}, such as
 * {@link Like} or {@link LessThan}. The type argument of the
 * {@code Constraint} subtype (or the type argument of {@code Constraint}
 * that the subtype defines) must match the entity attribute type.
 * For example,</p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; withinYears(
 * List<Car> withinYears(&#64;By(_Car.YEAR) Between&lt;Integer&gt; year,
 *                       &#64;By(_Car.MAKE) Like makePattern,
 *                       &#64;By(_Car.MODEL) Like modelPattern,
 *                       Order&lt;Car&gt; sorts);
 *
 * ...
 *
 * found = cars.withinYears(Between.bounds(2021, 2025),
 *                          Like.prefix(makePrefix),
 *                          Like.contains(modelSubstring),
 *                          Order.by(_Car.year.desc(),
 *                                   _Car.price.desc(),
 *                                   _Car.vin.asc()));
 * </pre>
 *
 * <h2>Annotation value</h2>
 *
 * <p>Constraint parameters of repository methods can be annotated with the
 * {@link Is @Is} annotation to indicate the subtype of {@code Constraint}.
 * The type of the method parameter must be the entity attribute type.
 * For example,</p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; pricedAtMost(&#64;By(_Car.PRICE) &#64;Is(AtMost.class) int maxPrice,
 *                        &#64;By(_Car.MAKE) &#64;Is(Like.class) String makePattern,
 *                        &#64;By(_Car.MODEL) &#64;Is(Like.class) Sting modelPattern,
 *                        Order&lt;Car&gt; sorts);
 *
 * ...
 *
 * found = cars.pricedAtMost(35000, "Chev%", "% SUV",
 *                           Order.by(_Car.price.desc(),
 *                                    _Car.vin.asc()));
 * </pre>
 *
 * <p>The {@linkplain Attribute entity and static metamodel} for the code
 * examples within this class are shown in the {@link Attribute} Javadoc.
 * </p>
 *
 * @param <V> type of the entity attribute.
 * @since 1.1
 */
public interface Constraint<V> {

    /**
     * <p>Obtains the opposite {@code Constraint}. For example, the opposite of
     * {@link Like} is {@link NotLike}, the opposite of {@link Null} is
     * {@link NotNull}, and the opposite of {@link AtLeast} is {@link LessThan}.
     * </p>
     *
     * @return the opposite {@code Constraint} subtype.
     */
    Constraint<V> negate();

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * equal to the given {@code value}.</p>
     *
     * @param value a value.
     * @return an {@link EqualTo} constraint.
     * @throws NullPointerException if the value is {@code null}.
     * @see EqualTo#value(Object)
     */
    static <V> EqualTo<V> equalTo(V value) {
        return EqualTo.value(value);
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * not equal to the given {@code value}.</p>
     *
     * @param value a value.
     * @return a {@link NotEqualTo} constraint.
     * @throws NullPointerException if the value is {@code null}.
     * @see NotEqualTo#value(Object)
     */
    static <V> NotEqualTo<V> notEqualTo(V value) {
        return NotEqualTo.value(value);
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * equal to one of the given {@code values}.</p>
     *
     * @param values one or more values.
     * @return an {@link In} constraint.
     * @throws IllegalArgumentException if the array of values is empty.
     * @throws NullPointerException if the array of values or any element
     *         of the array is {@code null}.
     * @see In#values(Object...)
     */
    @SafeVarargs
    static <V> In<V> in(V... values) {
        return In.values(values);
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * equal to one of the given {@code values}.</p>
     *
     * @param values one or more values.
     * @return an {@link In} constraint.
     * @throws IllegalArgumentException if the collection of values is empty.
     * @throws NullPointerException if the collection of values or any value
     *         within the collection is {@code null}.
     * @see In#values(java.util.Collection)
     */
    static <V> In<V> in(Set<V> values) {
        return In.values(values);
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * not equal to any of the given {@code values}.</p>
     *
     * @param values one or more values.
     * @return a {@link NotIn} constraint.
     * @throws IllegalArgumentException if the array of values is empty.
     * @throws NullPointerException if the array of values or any element
     *         of the array is {@code null}.
     * @see NotIn#values(Object...)
     */
    @SafeVarargs
    static <V> NotIn<V> notIn(V... values) {
        return NotIn.values(values);
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * not equal to any of the given {@code values}.</p>
     *
     * @param values one or more values.
     * @return a {@link NotIn} constraint.
     * @throws IllegalArgumentException if the collection of values is empty.
     * @throws NullPointerException if the collection of values or any value
     *         within the collection is {@code null}.
     * @see NotIn#values(java.util.Collection)
     */
    static <V> NotIn<V> notIn(Set<V> values) {
        return NotIn.values(values);
    }

    /**
     * <p>Requires that the constraint target match the given {@code pattern},
     * in which {@code _} and {@code %} represent wildcards.</p>
     *
     * @param pattern a pattern in which {@code _} matches a single character
     *                and {@code %} matches 0 or more characters.
     * @return a {@link Like} constraint.
     * @throws NullPointerException if the pattern is {@code null}.
     * @see Like#pattern(String)
     */
    static Like like(String pattern) {
        return Like.pattern(pattern);
    }

    /**
     * <p>Requires that the constraint target match the given {@code pattern},
     * in which the given characters represent wildcards.</p>
     *
     * @param pattern        a pattern that can include the given wildcard
     *                       characters.
     * @param charWildcard   wildcard that represents any single character.
     * @param stringWildcard wildcard that represents 0 or more characters.
     * @return a {@link Like} constraint.
     * @throws NullPointerException if the pattern is {@code null}.
     * @see Like#pattern(String, char, char)
     */
    static Like like(String pattern, char charWildcard, char stringWildcard) {
        return Like.pattern(pattern, charWildcard, stringWildcard);
    }

    /**
     * <p>Requires that the constraint target match the given {@code pattern},
     * in which the given characters represent wildcards and escape.</p>
     *
     * @param pattern        a pattern that can include the given wildcard
     *                       characters and escape character.
     * @param charWildcard   wildcard that represents any single character.
     * @param stringWildcard wildcard that represents 0 or more characters.
     * @param escape         escape character.
     * @return a {@link Like} constraint.
     * @throws NullPointerException if the pattern is {@code null}.
     * @see Like#pattern(String, char, char, char)
     */
    static Like like(String pattern, char charWildcard, char stringWildcard, char escape) {
        return Like.pattern(pattern, charWildcard, stringWildcard, escape);
    }

    /**
     * <p>Requires that the constraint target does not match the given
     * {@code pattern}, in which {@code _} and {@code %} represent wildcards.
     * </p>
     *
     * @param pattern a pattern in which {@code _} matches a single character
     *                and {@code %} matches 0 or more characters.
     * @return a {@link NotLike} constraint.
     * @throws NullPointerException if the pattern is {@code null}.
     * @see NotLike#pattern(String)
     */
    static NotLike notLike(String pattern) {
        return NotLike.pattern(pattern);
    }

    /**
     * <p>Requires that the constraint target does not match the given
     * {@code pattern}, in which the given characters represent wildcards.</p>
     *
     * @param pattern        a pattern that can include the given wildcard
     *                       characters.
     * @param charWildcard   wildcard that represents any single character.
     * @param stringWildcard wildcard that represents 0 or more characters.
     * @return a {@link NotLike} constraint.
     * @throws NullPointerException if the pattern is {@code null}.
     * @see NotLike#pattern(String, char, char)
     */
    static NotLike notLike(String pattern, char charWildcard, char stringWildcard) {
        return NotLike.pattern(pattern, charWildcard, stringWildcard);
    }

    /**
     * <p>Requires that the constraint target does not match the given
     * {@code pattern}, in which the given characters represent wildcards
     * and escape.</p>
     *
     * @param pattern        a pattern that can include the given wildcard
     *                       characters and escape character.
     * @param charWildcard   wildcard that represents any single character.
     * @param stringWildcard wildcard that represents 0 or more characters.
     * @param escape         escape character.
     * @return a {@link NotLike} constraint.
     * @throws NullPointerException if the pattern is {@code null}.
     * @see NotLike#pattern(String, char, char, char)
     */
    static NotLike notLike(String pattern,
                           char charWildcard,
                           char stringWildcard,
                           char escape) {
        return NotLike.pattern(pattern, charWildcard, stringWildcard, escape);
    }

    /**
     * <p>Requires that the constraint target evaluates to {@code null}.</p>
     *
     * @return a {@link Null} constraint.
     * @see Null#instance()
     */
    static <V> Null<V> isNull() {
        return Null.instance();
    }

    /**
     * <p>Requires that the constraint target does not evaluate to
     * {@code null}.</p>
     *
     * @return a {@link NotNull} constraint.
     * @see NotNull#instance()
     */
    static <V> NotNull<V> notNull() {
        return NotNull.instance();
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * greater than the given {@code bound}.</p>
     *
     * @param bound an exclusive minimum value.
     * @return a {@link GreaterThan} constraint.
     * @throws NullPointerException if the bound is {@code null}.
     * @see GreaterThan#bound(Comparable)
     */
    static <V extends Comparable<?>> GreaterThan<V> greaterThan(V bound) {
        return GreaterThan.bound(bound);
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * less than the given {@code bound}.</p>
     *
     * @param bound an exclusive maximum value.
     * @return a {@link LessThan} constraint.
     * @throws NullPointerException if the bound is {@code null}.
     * @see LessThan#bound(Comparable)
     */
    static <V extends Comparable<?>> LessThan<V> lessThan(V bound) {
        return LessThan.bound(bound);
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * greater than or equal to the given {@code minimum}.</p>
     *
     * @param minimum the minimum value.
     * @return a {@link AtLeast} constraint.
     * @throws NullPointerException if the minimum is {@code null}.
     * @see AtLeast#min(Comparable)
     */
    static <V extends Comparable<?>> AtLeast<V> greaterThanEqual(V minimum) {
        return AtLeast.min(minimum);
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * less than or equal to the given {@code maximum}.</p>
     *
     * @param maximum the maximum value.
     * @return a {@link AtMost} constraint.
     * @throws NullPointerException if the maximum is {@code null}.
     * @see AtMost#max(Comparable)
     */
    static <V extends Comparable<?>> AtMost<V> lessThanEqual(V maximum) {
        return AtMost.max(maximum);
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * greater than or equal to the given {@code minimum} and less than or
     * equal to the given {@code maximum}.</p>
     *
     * @param minimum the minimum value.
     * @param maximum the maximum value.
     * @return a {@link Between} constraint.
     * @throws NullPointerException if the minimum or maximum is {@code null}.
     * @see Between#bounds(Comparable, Comparable)
     */
    static <V extends Comparable<?>> Between<V> between(V minimum, V maximum) {
        return Between.bounds(minimum, maximum);
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * less than the given {@code lowerBound} or greater than the given
     * {@code upperBound}.</p>
     *
     * @param lowerBound a lower bound.
     * @param upperBound an upper bound.
     * @return a {@link NotBetween} constraint.
     * @throws NullPointerException if the lower bound or upper bound is
     *         {@code null}.
     * @see NotBetween#bounds(Comparable, Comparable)
     */
    static <V extends Comparable<?>> NotBetween<V> notBetween(V lowerBound, V upperBound) {
        return NotBetween.bounds(lowerBound, upperBound);
    }
}
