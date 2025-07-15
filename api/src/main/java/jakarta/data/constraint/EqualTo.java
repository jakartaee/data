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

import jakarta.data.expression.Expression;
import jakarta.data.messages.Messages;
import jakarta.data.metamodel.Attribute;
import jakarta.data.repository.Is;
import jakarta.data.restrict.Restriction;
import jakarta.data.spi.expression.literal.Literal;

/**
 * <p>A constraint that requires equality.</p>
 *
 * <p>A parameter-based repository method can impose a constraint on an
 * entity attribute by defining a method parameter that is of type
 * {@code EqualTo} or is annotated {@link Is @Is(EqualTo.class)} and is
 * of the same type or a subtype of the entity attribute. The equality
 * constraint is also the default when the {@code @Is} annotation is omitted.
 * For example,</p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; fromManufacturer(&#64;By(_Car.MAKE) &#64;IgnoreCase EqualTo&lt;String&gt; manufacturer);
 *
 * &#64;Find
 * List&lt;Car&gt; ofMakeAndModel(&#64;By(_Car.MAKE) &#64;IgnoreCase &#64;Is(EqualTo.class) String manufacturer,
 *                          &#64;By(_Car.MODEL) &#64;IgnoreCase &#64;Is(EqualTo.class) String model,
 *                          Order&lt;Car&gt; sorts);
 *
 * &#64;Find
 * List&lt;Car&gt; ofMakeAndModelAndYear(&#64;By(_Car.MAKE) &#64;IgnoreCase String manufacturer,
 *                                 &#64;By(_Car.MODEL) &#64;IgnoreCase String model,
 *                                 &#64;By(_Car.YEAR) int modelYear,
 *                                 Order&lt;Car&gt; sorts);
 * ...
 *
 * found = cars.fromManufacturer(EqualTo.value("Jakarta Motors"));
 *
 * found = cars.ofMakeAndModel("Jakarta Motors",
 *                             "J-150",
 *                             Order.by(_Car.price.desc()));
 *
 * found = cars.ofMakeAndModelAndYear("Jakarta Motors",
 *                                    "J-150",
 *                                    2025,
 *                                    Order.by(_Car.price.desc()));
 * </pre>
 *
 * <p>Repository methods can also accept {@code EqualTo} constraints at
 * runtime in the form of a {@link Restriction} on an {@link Expression}.
 * For example,</p>
 *
 * <pre>
 * &#64;Find
 * List&lt;Car&gt; searchAll(Restriction&lt;Car&gt; restrict, Order&lt;Car&gt; sorts);
 *
 * ...
 *
 * found = cars.searchAll(Restrict.all(_Car.make.equalTo("Jakarta Motors"),
 *                                     _Car.model.equalTo("J-150")),
 *                        Order.by(_Car.year.desc(),
 *                                 _Car.price.asc());
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
public interface EqualTo<V> extends Constraint<V> {

    /**
     * <p>Requires that the constraint target equal the value to which the
     * given {@code expression} evaluates. For example,</p>
     *
     * <pre>
     * found = cars.fromManufacturer(
     *                 EqualTo.expression(_Car.model.left(_Car.make.length())));
     * </pre>
     *
     * @param <V>        type of the entity attribute or a subtype or primitive
     *                   wrapper type for the entity attribute.
     * @param expression an expression that evaluates to a value against which
     *                   the constraint target is compared.
     * @return an {@code EqualTo} constraint.
     * @throws NullPointerException if the expression is {@code null}.
     */
    static <V> EqualTo<V> expression(Expression<?, V> expression) {
        if (expression == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "expression"));
        }

        return new EqualToRecord<>(expression);
    }

    /**
     * <p>Requires that the constraint target equal the given value. For
     * example,</p>
     *
     * <pre>
     * found = cars.fromManufacturer(EqualTo.value("Jakarta Motors"));
     * </pre>
     *
     * @param <V>   type of the entity attribute or a subtype or primitive
     *              wrapper type for the entity attribute.
     * @param value a value against which the constraint target is compared.
     * @return an {@code EqualTo} constraint.
     * @throws NullPointerException if the value is {@code null}.
     */
    static <V> EqualTo<V> value(V value) {
        if (value == null) {
            throw new NullPointerException(
                    Messages.get("001.arg.required", "value"));
        }

        return new EqualToRecord<>(Literal.of(value));
    }

    /**
     * <p>An expression that evaluates to the value against which the
     * constraint target is compared.</p>
     *
     * @return an expression representing the value.
     */
    Expression<?, V> expression();
}
