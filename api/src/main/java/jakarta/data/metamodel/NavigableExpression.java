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
package jakarta.data.metamodel;

import jakarta.data.metamodel.path.ComparablePath;
import jakarta.data.metamodel.path.NavigablePath;
import jakarta.data.metamodel.path.NumericPath;
import jakarta.data.metamodel.path.TextPath;

public interface NavigableExpression<T,U> {

    // QUESTION: should these operations expose NavigablePath, TextPath, ComparablePath, NumericPath

    default <V> NavigableExpression<T, V> navigate(NavigableAttribute<U, V> attribute) {
        return NavigablePath.of(this, attribute);
    }

    default TextExpression<T> navigate(TextAttribute<U> attribute) {
        return TextPath.of(this, attribute);
    }

    default  <C extends Comparable<C>> ComparableExpression<T, C> navigate(ComparableAttribute<U, C> attribute) {
        return ComparablePath.of(this, attribute);
    }

    default <N extends Number & Comparable<N>> NumericExpression<T, N> navigate(NumericAttribute<U, N> attribute) {
        return NumericPath.of(this, attribute);
    }

}
