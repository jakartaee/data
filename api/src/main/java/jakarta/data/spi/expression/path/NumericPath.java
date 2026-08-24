/*
 * Copyright (c) 2025,2026 Contributors to the Eclipse Foundation
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
package jakarta.data.spi.expression.path;

import jakarta.data.expression.NavigableExpression;
import jakarta.data.expression.NumericExpression;
import jakarta.data.metamodel.NavigableAttribute;
import jakarta.data.metamodel.NumericAttribute;
import jakarta.annotation.Nonnull;

/**
 * A {@linkplain Path path} that navigates to a {@link NumericAttribute}.
 * The path is represented as a {@link NumericExpression}.
 *
 * @param <T> entity type
 * @param <U> type of the intermediate embeddable or association that
 *            owns the navigated attribute
 * @param <N> numeric type of the navigated attribute
 * @since 1.1
 */
public interface NumericPath<T, U, N extends Number & Comparable<N>>
        extends Path<T, U>, NumericExpression<T, N> {

    /**
     * Obtains a {@link NumericPath} representing navigation from the given
     * expression to the given {@link NumericAttribute}.
     *
     * <p>
     * This class is part of the Jakarta Data SPI for Jakarta Data providers.
     * Applications must not use the SPI directly and can instead invoke
     * {@link NavigableExpression#navigate(NumericAttribute)} starting from a
     * {@linkplain NavigableAttribute navigable metamodel attribute}
     * to access a numeric path expression.
     *
     * @param <T>        entity type
     * @param <U>        type of the intermediate object that owns the
     *                   navigated attribute
     * @param <N>        numeric type of the navigated attribute
     * @param expression the expression from which to navigate
     * @param attribute  the numeric attribute to which to navigate
     * @return a {@link NumericPath} representing the path
     * @throws NullPointerException if either argument is {@code null}
     */
    @Nonnull
    static <T, U, N extends Number & Comparable<N>>
            NumericPath<T, U, N> of(
                    @Nonnull NavigableExpression<T, U> expression,
                    @Nonnull NumericAttribute<U, N> attribute) {

        return new NumericPathRecord<>(expression, attribute);
    }
}
