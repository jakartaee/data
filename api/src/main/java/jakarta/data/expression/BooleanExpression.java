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
package jakarta.data.expression;

import jakarta.data.constraint.EqualTo;
import jakarta.data.metamodel.Attribute;
import jakarta.data.metamodel.BooleanAttribute;
import jakarta.data.restrict.BasicRestriction;
import jakarta.data.restrict.Restriction;

/**
 * <p>An {@linkplain Expression expression} that evaluates to a
 * {@linkplain BooleanAttribute true or false} value.</p>
 *
 * <p>The {@linkplain Attribute entity and static metamodel} for the code
 * examples within this class are shown in the {@link Attribute} Javadoc.
 * </p>
 *
 * @param <T> entity type.
 * @since 1.1
 */
public interface BooleanExpression<T>
        extends ComparableExpression<T, Boolean> {
    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a {@code false} value.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     newVehicles = cars.search(make,
     *                               model,
     *                               _Car.previouslyOwned.isFalse());
     * }</pre>
     *
     * @return the restriction.
     */
    default Restriction<T> isFalse() {
        return BasicRestriction.of(this, EqualTo.value(false));
    }

    /**
     * <p>Obtains a {@link Restriction} that requires that this expression
     * evaluate to a {@code true} value.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     *     usedVehicles = cars.search(make,
     *                                model,
     *                                _Car.previouslyOwned.isTrue());
     * }</pre>
     *
     * @return the restriction.
     */
    default Restriction<T> isTrue() {
        return BasicRestriction.of(this, EqualTo.value(true));
    }

    /**
     * Returns {@code Boolean.class} as the type of the boolean expression.
     *
     * @return {@code Boolean.class}.
     */
    @Override
    default Class<Boolean> type() {
        return Boolean.class;
    }

}
