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
 * A constraint that tests whether an attribute's value is less than a specified value.
 *
 * <p><strong>Example usage:</strong></p>
 * <pre>{@code
 * List<Product> cheapProducts = productRepository.findAll(
 *     _Product.price.lessThan(10.0)
 * );
 * }</pre>
 *
 * @param <V> the type of the attribute being tested
 */
public interface LessThan<V extends Comparable<?>> extends Constraint<V> {

    static <V extends Comparable<?>> LessThan<V> bound(
            V upperBound) {
        return new LessThanRecord<>(ComparableLiteral.of(upperBound));
    }

    static <V extends Comparable<?>> LessThan<V> bound(
            ComparableExpression<?, V> upperBound) {
        return new LessThanRecord<>(upperBound);
    }

    ComparableExpression<?, V> bound();
}
