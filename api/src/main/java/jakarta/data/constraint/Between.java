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
import jakarta.data.spi.expression.literal.ComparableLiteral;

/**
 * A constraint that checks if an attribute's value is between two values (inclusive).
 *
 * <pre>{@code
 * List<Product> products = repository.findAll(
 *     Restrict.between(_Product.price, new BigDecimal("10.00"), new BigDecimal("50.00"))
 * );
 * }</pre>
 *
 * @param <V> the type of the attribute
 */
public interface Between<V extends Comparable<?>> extends Constraint<V> {

    static <V extends Comparable<?>> Between<V> bounds(
            V lower,
            V upper) {
        return new BetweenRecord<>(ComparableLiteral.of(lower),
                ComparableLiteral.of(upper));
    }

    static <V extends Comparable<?>> Between<V> bounds(
            V lower,
            ComparableExpression<?, V> upper) {
        return new BetweenRecord<>(ComparableLiteral.of(lower),
                upper);
    }

    static <V extends Comparable<?>> Between<V> bounds(
            ComparableExpression<?, V> lower,
            V upper) {
        return new BetweenRecord<>(lower,
                ComparableLiteral.of(upper));
    }

    static <V extends Comparable<?>> Between<V> bounds(
            ComparableExpression<?, V> lower,
            ComparableExpression<?, V> upper) {
        return new BetweenRecord<>(lower,
                upper);
    }

    ComparableExpression<?, V> lowerBound();

    ComparableExpression<?, V> upperBound();
}
