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

import jakarta.data.constraint.Constraint;
import jakarta.data.constraint.EqualTo;
import jakarta.data.constraint.In;
import jakarta.data.constraint.NotEqualTo;
import jakarta.data.constraint.NotIn;
import jakarta.data.constraint.NotNull;
import jakarta.data.constraint.Null;
import jakarta.data.metamodel.Attribute;
import jakarta.data.metamodel.StaticMetamodel;
import jakarta.data.restrict.BasicRestriction;
import jakarta.data.restrict.Restriction;

import java.util.Collection;

/**
 * <p>An expression represents an {@linkplain Attribute entity attribute},
 * function, or literal value.</p>
 *
 * <p>Expressions are used to compose {@linkplain Restriction restrictions}
 * that allow applications to supply query criteria at runtime. For example,
 * </p>
 * <pre>{@code
 * List<Car> affordableVehicles =
 *         cars.search(make,
 *                     model,
 *                     _Car.price.plus(fees).lessThan(25000));
 * }</pre>
 *
 * <p>The {@linkplain Attribute entity and static metamodel} for the code
 * examples within this class are shown in the {@link Attribute} Javadoc.
 * </p>
 *
 * <p>Expressions are immutable and do not change after they are created.</p>
 *
 * <h2>Attribute expressions</h2>
 *
 * <p>All subtypes of {@link Attribute} obtained from the
 * {@linkplain StaticMetamodel static metamodel} are expressions.
 * For example,</p>
 * <pre>{@code
 * _Car.vin
 * _Car.price
 * }</pre>
 *
 * <h2>Function expressions</h2>
 *
 * <p>Function expressions can be obtained from attribute expressions. For
 * example,</p>
 * <pre>{@code
 * _Car.price.minus(discount)
 * _Car.model.upper()
 * _Car.vin.length()
 * }</pre>
 *
 * <p>Function expressions can also be obtained from other function
 * expressions. For example, the following expression represents the second
 * through third digits of a Vehicle Id Number,</p>
 * <pre>{@code
 * _Car.vin.left(3).right(2)
 * }</pre>
 *
 * <p>Some function expressions are available via static methods. For example,
 * </p>
 * <pre>{@code
 * CurrentDateTime.now()
 * }</pre>
 *
 * <h2>Literal expressions</h2>
 *
 * <p>Literal expressions are used indirectly when literal values are supplied
 * to methods that create other expressions and
 * {@linkplain Restriction restrictions}. For example, the value {@code 1000}
 * in</p>
 *
 * <pre>{@code
 * _Car.price.minus(1000)
 * }</pre>
 *
 * @param <T> entity type.
 * @param <V> entity attribute type.
 * @since 1.1
 */
public interface Expression<T, V> {

    /**
     * The type of the expression.
     */
    Class<V> type();

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that is equal to the specified value.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     newCars = cars.search(make, model, _Car.year.equalTo(Year.now().getValue()));
     * }</pre>
     *
     * @param value value against which to compare. Must not be {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the value is {@code null}.
     */
    default Restriction<T> equalTo(V value) {
        return BasicRestriction.of(this, EqualTo.value(value));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * and the specified expression evaluate to values that are equal.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     listedToday = cars.search(make, model, _Car.listed.equalTo(CurrentDate.now()));
     * }</pre>
     *
     * @param expression expression against which to compare. Must not be
     *                   {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the expression is {@code null}.
     */
    default Restriction<T> equalTo(Expression<? super T, V> expression) {
        return BasicRestriction.of(this, EqualTo.expression(expression));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that is equal to one of the values within the
     * specified collection.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make,
     *                         model,
     *                         _Car.year.in(Set.of(2022, 2024)));
     * }</pre>
     *
     * @param values values against which to compare. Must not be {@code null},
     *               empty, or contain a {@code null} element.
     * @return the restriction.
     * @throws IllegalArgumentException if the values collection is empty.
     * @throws NullPointerException     if the values collection is
     *                                  {@code null} or contains a {@code null}
     *                                  element.
     */
    default Restriction<T> in(Collection<V> values) {
        return BasicRestriction.of(this, In.values(values));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that is equal to one of the specified values.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make,
     *                         model,
     *                         _Car.color.in(Color.BLACK, Color.BLUE, Color.GRAY));
     * }</pre>
     *
     * @param values values against which to compare. Must not be {@code null},
     *               empty, or contain a {@code null} element.
     * @return the restriction.
     * @throws IllegalArgumentException if the values array is empty.
     * @throws NullPointerException     if the values array is {@code null} or
     *                                  contains a {@code null} element.
     */
    @SuppressWarnings("unchecked")
    default Restriction<T> in(V... values) {
        return BasicRestriction.of(this, In.values(values));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * and at least one of the specified expressions evaluate to values that
     * are equal.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make,
     *                         model,
     *                         _Car.firstModelYear.in(_Car.year.minus(3),
     *                                                _Car.year.minus(4),
     *                                                _Car.year.minus(5)));
     * }</pre>
     *
     * @param expressions expressions against which to compare. Must not be
     *                    {@code null}, empty, or contain a {@code null}
     *                    element.
     * @return the restriction.
     * @throws IllegalArgumentException if the expressions array is empty.
     * @throws NullPointerException     if the expressions array is
     *                                  {@code null} or contains a {@code null}
     *                                  element.
     */
    @SuppressWarnings("unchecked")
    default Restriction<T> in(Expression<? super T, V>... expressions) {
        return BasicRestriction.of(this, In.expressions(expressions));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a {@code null} value.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     notListedYet = cars.search(make, model, _Car.listed.isNull());
     * }</pre>
     *
     * @return the restriction.
     */
    default Restriction<T> isNull() {
        return BasicRestriction.of(this, Null.instance());
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that is not equal to the specified value.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make, model, _Car.color.notEqualTo(Color.RED));
     * }</pre>
     *
     * @param value value against which to compare. Must not be {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the value is {@code null}.
     */
    default Restriction<T> notEqualTo(V value) {
        return BasicRestriction.of(this, NotEqualTo.value(value));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * and the specified expression evaluate to values that are not equal to
     * each other.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make, model, _Car.listed.notEqualTo(CurrentDate.now()));
     * }</pre>
     *
     * @param expression expression against which to compare. Must not be
     *                   {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the expression is {@code null}.
     */
    default Restriction<T> notEqualTo(Expression<? super T, V> expression) {
        return BasicRestriction.of(this, NotEqualTo.expression(expression));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that is not equal to any of the values within the
     * specified collection.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make,
     *                         model,
     *                         _Car.color.notIn(Set.of(Color.GRAY, Color.WHITE)));
     * }</pre>
     *
     * @param values values against which to compare. Must not be {@code null},
     *               empty, or contain a {@code null} element.
     * @return the restriction.
     * @throws IllegalArgumentException if the values collection is empty.
     * @throws NullPointerException     if the values collection is
     *                                  {@code null} or contains a {@code null}
     *                                  element.
     */
    default Restriction<T> notIn(Collection<V> values) {
        return BasicRestriction.of(this, NotIn.values(values));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that is not equal to any of the specified values.
     * </p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make,
     *                         model,
     *                         _Car.year.notIn(2019, 2020, 2023));
     * }</pre>
     *
     * @param values values against which to compare. Must not be {@code null},
     *               empty, or contain a {@code null} element.
     * @return the restriction.
     * @throws IllegalArgumentException if the values array is empty.
     * @throws NullPointerException     if the values array is {@code null} or
     *                                  contains a {@code null} element.
     */
    @SuppressWarnings("unchecked")
    default Restriction<T> notIn(V... values) {
        return BasicRestriction.of(this, NotIn.values(values));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that is not equal to any of the values to which the
     * specified expressions evaluate.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make,
     *                         model,
     *                         _Car.year.notIn(_Car.firstModelYear,
     *                                         _Car.firstModelYear.plus(1)));
     * }</pre>
     *
     * @param expressions expressions against which to compare. Must not be
     *                    {@code null}, empty, or contain a {@code null}
     *                    element.
     * @return the restriction.
     * @throws IllegalArgumentException if the expressions array is empty.
     * @throws NullPointerException     if the expressions array is
     *                                  {@code null} or contains a {@code null}
     *                                  element.
     */
    @SuppressWarnings("unchecked")
    default Restriction<T> notIn(Expression<? super T, V>... expressions) {
        return BasicRestriction.of(this, NotIn.expressions(expressions));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * does not evaluate to a {@code null} value.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make, model, _Car.listed.notNull());
     * }</pre>
     *
     * @return the restriction.
     */
    default Restriction<T> notNull() {
        return BasicRestriction.of(this, NotNull.instance());
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a value that satisfies the specified {@link Constraint}.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     found = cars.search(make,
     *                         model,
     *                         _Car.year.satisfies(Constraint.between(2021, 2024)));
     * }</pre>
     *
     * @param constraint constraint to use for comparing the value to which
     *                   this expression evaluates. Must not be {@code null}.
     * @return the restriction.
     * @throws NullPointerException if the constraint is {@code null}.
     */
    // TODO: should this be called restrict() ?
    default Restriction<T> satisfies(Constraint<V> constraint) {
        return BasicRestriction.of(this, constraint);
    }
}
