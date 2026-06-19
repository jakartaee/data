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
package jakarta.data.spi.expression.function;

import jakarta.data.expression.NumericExpression;
import jakarta.annotation.Nonnull;

/**
 * <p>An {@linkplain NumericExpression expression} that represents conversion
 * from one numeric type to another.</p>
 *
 * <p>The methods {@link NumericExpression#asBigDecimal() asBigDecimal()},
 * {@link NumericExpression#asBigInteger() asBigInteger()},
 * {@link NumericExpression#asDouble() asDouble()}, and
 * {@link NumericExpression#asLong() asLong()} allow conversion of numeric
 * expressions to the respective types.</p>
 *
 * @param <T> entity type
 * @param <N> the target numeric type to which the expression is cast
 * @since 1.1
 */
public interface NumericCast<T, N extends Number & Comparable<N>>
        extends NumericExpression<T, N> {

    /**
     * <p>The numeric expression to cast to a different numeric type.</p>
     *
     * @return the numeric expression to cast
     */
    @Nonnull
    NumericExpression<T, ?> expression();

    /**
     * <p>The target numeric type to which to cast the {@link #expression()}.
     * </p>
     *
     * @return the target numeric type
     */
    @Override
    @Nonnull
    Class<N> type();

    /**
     * <p>Creates a {@code NumericCast} expression that represents casting
     * the given numeric expression to the given target numeric type.</p>
     *
     * @param <T>        entity type
     * @param <N>        target numeric type
     * @param expression numeric expression to cast
     * @param type       numeric type to which to cast
     * @return expression representing the cast operation
     * @throws NullPointerException if {@code expression} or {@code type} is
     *                              {@code null}
     */
    @Nonnull
    static <T, N extends Number & Comparable<N>> NumericCast<T, N> of(
            @Nonnull NumericExpression<T, ?> expression,
            @Nonnull Class<N> type) {

        return new NumericCastRecord<>(expression, type);
    }
}
