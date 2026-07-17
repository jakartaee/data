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
package jakarta.data.expression;

import java.time.temporal.Temporal;

import jakarta.data.metamodel.BooleanAttribute;
import jakarta.data.metamodel.ComparableAttribute;
import jakarta.data.metamodel.NavigableAttribute;
import jakarta.data.metamodel.NumericAttribute;
import jakarta.data.metamodel.TemporalAttribute;
import jakarta.data.metamodel.TextAttribute;
import jakarta.data.spi.expression.path.BooleanPath;
import jakarta.data.spi.expression.path.ComparablePath;
import jakarta.data.spi.expression.path.NavigablePath;
import jakarta.data.spi.expression.path.NumericPath;
import jakarta.data.spi.expression.path.TemporalPath;
import jakarta.data.spi.expression.path.TextPath;
import jakarta.annotation.Nonnull;

public interface NavigableExpression<T, U> {

    @Nonnull
    default <V> NavigableExpression<T, V> navigate(@Nonnull NavigableAttribute<U, V> attribute) {
        return NavigablePath.of(this, attribute);
    }

    @Nonnull
    default TextExpression<T> navigate(@Nonnull TextAttribute<U> attribute) {
        return TextPath.of(this, attribute);
    }

    @Nonnull
    default <C extends Comparable<C>> ComparableExpression<T, C> navigate(@Nonnull ComparableAttribute<U, C> attribute) {
        return ComparablePath.of(this, attribute);
    }

    @Nonnull
    default BooleanExpression<T> navigate(@Nonnull BooleanAttribute<U> attribute) {
        return BooleanPath.of(this, attribute);
    }

    @Nonnull
    default <N extends Number & Comparable<N>> NumericExpression<T, N> navigate(@Nonnull NumericAttribute<U, N> attribute) {
        return NumericPath.of(this, attribute);
    }

    @Nonnull
    default <V extends Temporal & Comparable<? extends Temporal>> TemporalExpression<T, V> navigate(
            @Nonnull TemporalAttribute<U, V> attribute) {
        return TemporalPath.of(this, attribute);
    }

}
