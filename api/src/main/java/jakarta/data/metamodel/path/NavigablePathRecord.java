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

import java.util.Objects;

import jakarta.data.metamodel.NavigableAttribute;
import jakarta.data.metamodel.NavigableExpression;

record NavigablePathRecord<T,U,V>
        (NavigableExpression<T,U> expression, NavigableAttribute<U,V> attribute)
        implements NavigablePath<T,U,V> {

    NavigablePathRecord {
        Objects.requireNonNull(expression, "Expression is required.");
        Objects.requireNonNull(attribute, "Entity attribute is required.");
    }

    @Override
    public int compareTo(NavigableExpression<T, V> other) {
        if (getClass().equals(other.getClass())) {
            @SuppressWarnings("unchecked")
            NavigablePathRecord<T,U,V> another = (NavigablePathRecord<T,U,V>) other;
            int comp = expression.compareTo(another.expression);
            if (comp == 0) {
                comp = attribute.compareTo(another.attribute);
            }
            return comp;
        } else {
            return getClass().getName().compareTo(other.getClass().getName());
        }
    }
}
