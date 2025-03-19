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

import jakarta.data.metamodel.constraint.Constraint;
import jakarta.data.metamodel.restrict.Restriction;

import java.util.Set;

public interface Expression<T,V> {

    Restriction<T> equalTo(V value);
    Restriction<T> notEqualTo(V value);
    Restriction<T> in(Set<V> values);
    Restriction<T> notIn(Set<V> values);
    Restriction<T> isNull();
    Restriction<T> notNull();

    Restriction<T> satisfies(Constraint<V> constraint);

}
