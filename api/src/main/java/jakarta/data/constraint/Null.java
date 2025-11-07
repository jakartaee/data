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
import jakarta.data.metamodel.Attribute;
import jakarta.data.restrict.Restriction;

/**
 * <p>A constraint that requires a {@code null} value.</p>
 *
 * <p>A parameter-based repository method can impose a constraint on an
 * entity attribute by defining a method parameter that is of type
 * {@code Null}. For example,</p>
 *
 * <pre>{@code
 * @Find
 * List<Car> ofUnknownModel(@By(_Car.MAKE) String make,
 *                          @By(_Car.MODEL) Null<String> nullModel);
 * ...
 *
 * found = cars.ofUnknownModel("Jakarta Motors",
 *                             Null.instance());
 * }</pre>
 *
 * <p>Repository methods can also accept {@code Null} constraints at run time
 * in the form of a {@link Restriction} on an {@link Expression}. For example,
 * </p>
 *
 * <pre>{@code
 * @Find
 * List<Car> searchAll(Restriction<Car> restrict, Order<Car> sorts);
 *
 * ...
 *
 * found = cars.searchAll(Restrict.all(_Car.make.equalTo("Jakarta Motors"),
 *                                     _Car.model.isNull()),
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
public interface Null<V> extends Constraint<V> {

    /**
     * <p>Requires that the constraint target has a {@code null} value. For
     * example,</p>
     *
     * <pre>{@code
     *     found = cars.ofMakeAndModel("Jakarta Motors",
     *                                 Null.instance());
     * }</pre>
     *
     * @param <V> type of the entity attribute or a subtype or primitive
     *            wrapper type for the entity attribute.
     * @return a {@code Null} constraint.
     */
    @SuppressWarnings("unchecked")
    static <V> Null<V> instance() {
        return (Null<V>) NullRecord.INSTANCE;
    }
}
