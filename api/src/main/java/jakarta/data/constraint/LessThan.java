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
 * <p>A constraint that requires being below an upper bound.</p>
 *
 * <p>A parameter-based repository method can impose a constraint on an
 * entity attribute by defining a method parameter that is of type
 * {@code LessThan} or is annotated {@link Is @Is(LessThan.class)} and is
 * of the same type or a subtype of the entity attribute. For example,</p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; withPriceBelow(&#64;By(_Car.PRICE) LessThan&lt;Integer&gt; bound);
 *
 * &#64;Find
 * List&lt;Car&gt; pricedBelow(&#64;By(_Car.PRICE) &#64;Is(LessThan.class) int bound,
 *                        Order&lt;Car&gt; sorts);
 *
 * ...
 *
 * found = cars.withPriceBelow(LessThan.bound(40000));
 *
 * found = cars.pricedBelow(36000,
 *                          Order.by(_Car.price.desc()));
 * </pre>
 *
 * <p>Repository methods can also accept {@code LessThan} constraints at
 * run time in the form of a {@link Restriction} on a
 * {@link ComparableExpression}. For example,</p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; searchAll(Restriction&lt;Car&gt; restrict, Order&lt;Car&gt; sorts);
 *
 * ...
 *
 * found = cars.searchAll(_Car.price.lessThan(35000),
 *                        Order.by(_Car.price.desc(),
 *                                 _Car.vin.asc());
 * </pre>
 *
 * <p>The {@linkplain Attribute entity and static metamodel} for the code
 * examples within this class are shown in the {@link Attribute} Javadoc.
 * </p>
 *
 * @param <V> type of the entity attribute or a subtype or primitive wrapper
 *            type for the entity attribute.
 * @since 1.1
 */
public interface LessThan<V extends Comparable<?>> extends Constraint<V> {

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * less than or equal to the given {@code upperBound}. For example,</p>
     *
     * <pre>
     * found = cars.withPriceBelow(LessThan.bound(37000));
     * </pre>
     *
     * @param <V>        type of the entity attribute or a subtype or primitive
     *                   wrapper type for the entity attribute.
     * @param upperBound an exclusive maximum value.
     * @return a {@code LessThan} constraint.
     * @throws NullPointerException if the upper bound is {@code null}.
     */
    static <V extends Comparable<?>> LessThan<V> bound(
            V upperBound) {
        return new LessThanRecord<>(ComparableLiteral.of(upperBound));
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * less than the value to which the the given {@code upperBound}
     * expression evaluates. For example,</p>
     *
     * <pre>
     * found = cars.withFirstModelYearBefore(LessThan.bound(_Car.year.minus(1)));
     * </pre>
     *
     * @param <V>        type of the entity attribute or a subtype or primitive
     *                   wrapper type for the entity attribute.
     * @param upperBound an expression that evaluates to an exclusive maximum
     *                   value.
     * @return a {@code LessThan} constraint.
     * @throws NullPointerException if the upper bound is {@code null}.
     */
    static <V extends Comparable<?>> LessThan<V> bound(
            ComparableExpression<?, V> upperBound) {
        return new LessThanRecord<>(upperBound);
    }

    /**
     * <p>An expression that evaluates to an upper bound. The constraint target
     * must evaluate to a value less than this bound.</p>
     *
     * @return an expression representing the upper bound.
     */
    ComparableExpression<?, V> bound();
}
