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
 * <p>A constraint that imposes a maximum value.</p>
 *
 * <p>A parameter-based repository method can impose a constraint on an
 * entity attribute by defining a method parameter that is of type
 * {@code AtMost} or is annotated {@link Is @Is(AtMost.class)} and is
 * of the same type or a subtype of the entity attribute. For example,</p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; withMaximumPrice(&#64;By(_Car.PRICE) AtMost&lt;Integer&gt; maxPrice);
 *
 * &#64;Find
 * List&lt;Car&gt; pricedAtMost(&#64;By(_Car.PRICE) &#64;Is(AtMost.class) int maximum,
 *                        Order&lt;Car&gt; sorts);
 *
 * ...
 *
 * found = cars.withMaximumPrice(AtMost.max(40000));
 *
 * found = cars.pricedAtMost(36000,
 *                           Order.by(_Car.price.desc(),
 *                                    _Car.vin.asc()));
 * </pre>
 *
 * <p>Repository methods can also accept {@code AtMost} constraints at
 * run time in the form of a {@link Restriction} on a
 * {@link ComparableExpression}. For example,</p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; searchAll(Restriction&lt;Car&gt; restrict, Order&lt;Car&gt; sorts);
 *
 * ...
 *
 * found = cars.searchAll(_Car.price.lessThanEqual(35000),
 *                        Order.by(_Car.price.desc(),
 *                                 _Car.year.desc());
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
public interface AtMost<V extends Comparable<?>> extends Constraint<V> {

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * less than or equal to the given {@code maximum}. For example,</p>
     *
     * <pre>
     * found = cars.withMaximumPrice(AtMost.max(33000));
     * </pre>
     *
     * @param <V>     type of the entity attribute or a subtype or primitive
     *                wrapper type for the entity attribute.
     * @param maximum the maximum value.
     * @return an {@code AtMost} constraint.
     * @throws NullPointerException if the maximum is {@code null}.
     */
    static <V extends Comparable<?>> AtMost<V> max(
            V maximum) {
        return new AtMostRecord<>(ComparableLiteral.of(maximum));
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * less than or equal the value to which the the given {@code maximum}
     * expression evaluates. For example,</p>
     *
     * <pre>
     * found = cars.withMaxFirstYear(AtMost.max(_Car.year.minus(2)));
     * </pre>
     *
     * @param <V>     type of the entity attribute or a subtype or primitive
     *                wrapper type for the entity attribute.
     * @param maximum an expression that evaluates to the maximum value.
     * @return an {@code AtMost} constraint.
     * @throws NullPointerException if the maximum is {@code null}.
     */
    static <V extends Comparable<?>> AtMost<V> max(
            ComparableExpression<?, V> maximum) {
        return new AtMostRecord<>(maximum);
    }

    /**
     * <p>An expression that evaluates to the maximum value allowed for the
     * constraint target.</p>
     *
     * @return an expression representing the maximum value.
     */
    ComparableExpression<?, V> bound();
}
