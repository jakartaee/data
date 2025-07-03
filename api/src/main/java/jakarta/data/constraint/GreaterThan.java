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

import jakarta.data.expression.ComparableExpression;
import jakarta.data.metamodel.Attribute;
import jakarta.data.repository.Is;
import jakarta.data.restrict.Restriction;
import jakarta.data.spi.expression.literal.ComparableLiteral;

/**
 * <p>A constraint that requires exceeding a lower bound.</p>
 *
 * <p>A parameter-based repository method can impose a constraint on an
 * entity attribute by defining a method parameter that is of type
 * {@code GreaterThan} or is annotated {@link Is @Is(GreaterThan.class)} and is
 * of the same type as the entity attribute or is a subtype or primitive
 * wrapper type for it. For example,</p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; ofYearMoreThan(&#64;By(_Car.YEAR) GreaterThan&lt;Integer&gt; bound);
 *
 * &#64;Find
 * List&lt;Car&gt; newerThan(&#64;By(_Car.YEAR) &#64;Is(GreaterThan.class) int bound,
 *                     Order&lt;Car&gt; sorts);
 *
 * ...
 *
 * found = cars.ofYearMoreThan(GreaterThan.bound(2021));
 *
 * found = cars.newerThan(2022,
 *                        Order.by(_Car.price.desc()));
 * </pre>
 *
 * <p>Repository methods can also accept {@code GreaterThan} constraints at
 * run time in the form of a {@link Restriction} on a
 * {@link ComparableExpression}. For example,</p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; searchAll(Restriction&lt;Car&gt; restrict, Order&lt;Car&gt; sorts);
 *
 * ...
 *
 * found = cars.searchAll(_Car.year.greaterThan(2023),
 *                        Order.by(_Car.year.desc(),
 *                                 _Car.price.asc());
 * </pre>
 *
 * <p>The {@linkplain Attribute entity and static metamodel} for the code
 * examples within this class are shown in the {@link Attribute} Javadoc.
 * </p>
 *
 * @param <V> type of the entity attribute.
 * @since 1.1
 */
public interface GreaterThan<V extends Comparable<?>> extends Constraint<V> {

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * greater than or equal to the given {@code lowerBound}. For example,</p>
     *
     * <pre>
     * found = cars.ofYearMoreThan(GreaterThan.bound(2020));
     * </pre>
     *
     * @param <V>        type of the entity attribute.
     * @param lowerBound an exclusive minimum value.
     * @return a {@code GreaterThan} constraint.
     * @throws NullPointerException if the lower bound is {@code null}.
     */
    static <V extends Comparable<?>> GreaterThan<V> bound(
            V lowerBound) {
        return new GreaterThanRecord<>(ComparableLiteral.of(lowerBound));
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * greater than the value to which the the given {@code lowerBound}
     * expression evaluates. For example,</p>
     *
     * <pre>
     * found = cars.ofYearMoreThan(GreaterThan.bound(_Car.firstModelYear.plus(1)));
     * </pre>
     *
     * @param <V>        type of the entity attribute.
     * @param lowerBound an expression that evaluates to an exclusive minimum
     *                   value.
     * @return a {@code GreaterThan} constraint.
     * @throws NullPointerException if the lower bound is {@code null}.
     */
    static <V extends Comparable<?>> GreaterThan<V> bound(
            ComparableExpression<?, V> lowerBound) {
        return new GreaterThanRecord<>(lowerBound);
    }

    /**
     * <p>An expression that evaluates to a lower bound. The constraint target
     * must evaluate to a value greater than this bound.</p>
     *
     * @return an expression representing the lower bound.
     */
    ComparableExpression<?, V> bound();
}
