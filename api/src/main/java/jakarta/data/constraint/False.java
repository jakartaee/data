/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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
 * <p>A constraint that requires a {@code false} value.</p>
 *
 * <p>A parameter-based repository method can impose a constraint on an
 * entity attribute by defining a method parameter that is of type
 * {@code False}. For example,</p>
 *
 * <pre>{@code
 * @Find
 * List<Car> newVehicles(@By(_Car.MAKE) String make,
 *                       @By(_Car.MODEL) String model,
 *                       @By(_Car.PREVIOUSLYOWNED) False preOwned);
 * ...
 *
 * found = cars.newVehciles("Jakarta Motors",
 *                          "Jakadia",
 *                          False.instance());
 * }</pre>
 *
 * <p>Repository methods can also accept {@code False} constraints at run time
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
 *                                     _Car.model.equalTo("Jakadia"),
 *                                     _Car.previouslyOwned.isFalse()),
 *                        Order.by(_Car.year.desc(),
 *                                 _Car.price.asc()));
 * }</pre>
 *
 * <p>The {@linkplain Attribute entity and static metamodel} for the code
 * examples within this class are shown in the {@link Attribute} Javadoc.
 * </p>
 *
 * @since 1.1
 */
public interface False extends Constraint<Boolean> {

    /**
     * <p>Requires that the constraint target has a {@code false} value. For
     * example,</p>
     *
     * <pre>{@code
     *     found = cars.newVehicles("Jakarta Motors",
     *                              "Jakadia",
     *                              False.instance());
     * }</pre>
     *
     * @return a {@code False} constraint.
     */
    static False instance() {
        return FalseRecord.INSTANCE;
    }
}
