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
 * <p>A constraint that imposes a minimum value.</p>
 *
 * <p>A parameter-based repository method can impose a constraint on an
 * entity attribute by defining a method parameter that is of type
 * {@code AtLeast} or is annotated {@link Is @Is(AtLeast.class)} and is
 * of the same type or a subtype of the entity attribute. For example,</p>
 *
 * <pre>{@code
 * @Find
 * List<Car> ofYearOrHigher(@By(_Car.YEAR) AtLeast<Integer> minYear);
 *
 * @Find
 * List<Car> yearAtLeast(@By(_Car.YEAR) @Is(AtLeast.class) int minimum,
 *                       Order<Car> sorts);
 *
 * ...
 *
 * found = cars.ofYearOrHigher(AtLeast.min(2022));
 *
 * found = cars.yearAtLeast(2023,
 *                          Order.by(_Car.price.desc()));
 * }</pre>
 *
 * <p>Repository methods can also accept {@code AtLeast} constraints at
 * run time in the form of a {@link Restriction} on a
 * {@link ComparableExpression}. For example,</p>
 *
 * <pre>{@code
 * @Find
 * List<Car> searchAll(Restriction<Car> restrict, Order<Car> sorts);
 *
 * ...
 *
 * found = cars.searchAll(_Car.year.greaterThanEqual(2024),
 *                        Order.by(_Car.year.desc(),
 *                                 _Car.price.asc()));
 * }</pre>
 *
 * <p>The {@linkplain Attribute entity and static metamodel} for the code
 * examples within this class are shown in the {@link Attribute} Javadoc.
 * </p>
 *
 * @param <V> type of the entity attribute or a subtype or primitive wrapper
 *            type for the entity attribute.
 * @since 1.1
 */
public interface AtLeast<V extends Comparable<?>> extends Constraint<V> {

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * greater than or equal to the given {@code minimum}. For example,</p>
     *
     * <pre>
     * found = cars.ofYearOrHigher(AtLeast.min(2021));
     * </pre>
     *
     * @param <V>     type of the entity attribute or a subtype or primitive
     *                wrapper type for the entity attribute.
     * @param minimum the minimum value.
     * @return an {@code AtLeast} constraint.
     * @throws NullPointerException if the minimum is {@code null}.
     */
    static <V extends Comparable<?>> AtLeast<V> min(
            V minimum) {
        return new AtLeastRecord<>(ComparableLiteral.of(minimum));
    }

    /**
     * <p>Requires that the constraint target evaluates to a value that is
     * greater than or equal the value to which the the given {@code minimum}
     * expression evaluates. For example,</p>
     *
     * <pre>
     * found = cars.ofYearOrHigher(AtLeast.min(_Car.firstModelYear.plus(2)));
     * </pre>
     *
     * @param <V>     type of the entity attribute or a subtype or primitive
     *                wrapper type for the entity attribute.
     * @param minimum an expression that evaluates to the minimum value.
     * @return an {@code AtLeast} constraint.
     * @throws NullPointerException if the minimum is {@code null}.
     */
    static <V extends Comparable<?>> AtLeast<V> min(
            ComparableExpression<?, V> minimum) {
        return new AtLeastRecord<>(minimum);
    }

    /**
     * <p>An expression that evaluates to the minimum value allowed for the
     * constraint target.</p>
     *
     * @return an expression representing the minimum value.
     */
    ComparableExpression<?, V> bound();
}
