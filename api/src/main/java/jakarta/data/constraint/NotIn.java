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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.data.expression.Expression;
import jakarta.data.messages.Messages;
import jakarta.data.metamodel.Attribute;
import jakarta.data.restrict.Restriction;
import jakarta.data.spi.expression.literal.Literal;

import static java.util.Collections.unmodifiableList;

/**
 * <p>A constraint that requires inequality with every member of a collection.</p>
 *
 * <p>A parameter-based repository method can impose a constraint on an
 * entity attribute by defining a method parameter that is of type
 * {@code NotIn}. For example,</p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; excludingManufacturers(&#64;By(_Car.MAKE) NotIn&lt;String&gt; excluded);
 * ...
 *
 * found = cars.excludingManufacturers(NotIn.values("Leakoil Motors",
 *                                                  "Stallmore Motors"));
 * </pre>
 *
 * <p>Repository methods can also accept {@code NotIn} constraints at run time
 * in the form of a {@link Restriction} on an {@link Expression}. For example,
 * </p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; searchAll(Restriction&lt;Car&gt; restrict, Order&lt;Car&gt; sorts);
 *
 * ...
 *
 * found = cars.searchAll(_Car.make.notIn("Leakoil Motors",
 *                                        "Stallmore Motors"),
 *                        Order.by(_Car.make.asc(),
 *                                 _Car.model.asc()));
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
public interface NotIn<V> extends Constraint<V> {

    /**
     * <p>Requires that the constraint target not equal any of the given
     * {@code values}. For example,</p>
     *
     * <pre>
     * found = cars.excludingManufacturers(NotIn.values("Stallmore Motors",
     *                                                  "Knockhard Motors"));
     * </pre>
     *
     * @param <V>    type of the entity attribute or a subtype or primitive
     *               wrapper type for the entity attribute.
     * @param values values against which the constraint target is compared.
     * @return a {@code NotIn} constraint.
     * @throws IllegalArgumentException if the values array is empty.
     * @throws NullPointerException     if the values array or any value within
     *                                  it is {@code null}.
     */
    @SafeVarargs
    static <V> NotIn<V> values(V... values) {
        if (values == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "values"));
        }

        if (values.length == 0) {
            throw new IllegalArgumentException(
                    Messages.get("002.no.elements", "values"));
        }

        final List<Expression<?, V>> expressions = new ArrayList<>(values.length);
        for (V value : values) {
            if (value == null) {
                throw new NullPointerException(
                        Messages.get("003.null.element", "values"));
            }

            expressions.add(Literal.of(value));
        }

        return new NotInRecord<>(unmodifiableList(expressions));
    }

    /**
     * <p>Requires that the constraint target not equal any of the given
     * {@code values}. For example,</p>
     *
     * <pre>
     * found = cars.excludingManufacturers(NotIn.values(Set.of("Leakoil Motors",
     *                                                         "Knockhard Motors")));
     * </pre>
     *
     * @param <V>    type of the entity attribute or a subtype or primitive
     *               wrapper type for the entity attribute.
     * @param values values against which the constraint target is compared.
     * @return a {@code NotIn} constraint.
     * @throws IllegalArgumentException if the collection of values is empty.
     * @throws NullPointerException     if the collection of values or any
     *                                  value within it is {@code null}.
     */
    static <V> NotIn<V> values(Collection<V> values) {
        if (values == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "values"));
        }

        if (values.isEmpty()) {
            throw new IllegalArgumentException(
                    Messages.get("002.no.elements", "values"));
        }

        final List<Expression<?, V>> expressions = new ArrayList<>(values.size());
        for (V value : values) {
            if (value == null) {
                throw new NullPointerException(
                        Messages.get("003.null.element", "values"));
            }

            expressions.add(Literal.of(value));
        }

        return new NotInRecord<>(unmodifiableList(expressions));
    }

    /**
     * <p>Requires that the constraint target not equal any of the values to
     * which the given {@code expressions} evaluate. For example,</p>
     *
     * <pre>
     * found = cars.excludingManufacturers(
     *                 NotIn.expressions(List.of(_Car.model.left(_Car.make.length()),
     *                                           _Car.model.right(_Car.make.length()))));
     * </pre>
     *
     * @param <V>         type of the entity attribute or a subtype or
     *                    primitive wrapper type for the entity attribute.
     * @param expressions expressions that evaluate to the values against which
     *                    the constraint target is compared.
     * @return a {@code NotIn} constraint.
     * @throws IllegalArgumentException if the list of expressions is empty.
     * @throws NullPointerException     if the list of expressions or any value
     *                                  within it is {@code null}.
     */
    static <V> NotIn<V> expressions(List<Expression<?, V>> expressions) {
        if (expressions == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "expressions"));
        }

        if (expressions.isEmpty()) {
            throw new IllegalArgumentException(
                    Messages.get("002.no.elements", "expressions"));
        }

        for (Expression<?, V> expression : expressions) {
            if (expression == null) {
                throw new NullPointerException(
                        Messages.get("003.null.element", "expressions"));
            }
        }

        return new NotInRecord<>(List.copyOf(expressions));
    }

    /**
     * <p>Requires that the constraint target not equal any of the values to
     * which the given {@code expressions} evaluate. For example,</p>
     *
     * <pre>
     * found = cars.excludingManufacturers(
     *                 NotIn.expressions(_Car.model.left(_Car.make.length()),
     *                                   _Car.model.right(_Car.make.length())));
     * </pre>
     *
     * @param <V>         type of the entity attribute or a subtype or
     *                    primitive wrapper type for the entity attribute.
     * @param expressions expressions that evaluate to the values against which
     *                    the constraint target is compared.
     * @return a {@code NotIn} constraint.
     * @throws IllegalArgumentException if the array of expressions is empty.
     * @throws NullPointerException     if the array of expressions or any value
     *                                  within it is {@code null}.
     */
    @SafeVarargs
    static <V> NotIn<V> expressions(Expression<?, V>... expressions) {
        if (expressions == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "expressions"));
        }

        if (expressions.length == 0) {
            throw new IllegalArgumentException(
                    Messages.get("002.no.elements", "expressions"));
        }

        for (Expression<?, V> expression : expressions) {
            if (expression == null) {
                throw new NullPointerException(
                        Messages.get("003.null.element", "expressions"));
            }
        }

        return new NotInRecord<>(List.of(expressions));
    }

    /**
     * <p>Expressions that evaluate to the values against which the
     * constraint target is compared. The order of the list of expressions
     * matches the order of the array or the iteration order of the
     * {@link Collection} that was supplied to the static method that created
     * the {@code NotIn} constraint.</p>
     *
     * @return expressions representing the values.
     */
    List<Expression<?, V>> expressions();
}
