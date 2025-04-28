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
package jakarta.data.metamodel.path;

import jakarta.data.metamodel.ComparableAttribute;
import jakarta.data.metamodel.ComparableExpression;
import jakarta.data.metamodel.NavigableExpression;
import jakarta.data.metamodel.Path;

public interface ComparablePath<T, U, C extends Comparable<?>>
        extends Path<T, U>, ComparableExpression<T, C> {
    static <T, U, C extends Comparable<C>> ComparablePath<T, U, C>
    of(NavigableExpression<T, U> expression, ComparableAttribute<U, C> attribute) {
        return new ComparablePathRecord<>(expression, attribute);
    }
}
