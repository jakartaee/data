/*
 * Copyright (c) 2023,2025 Contributors to the Eclipse Foundation
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

import jakarta.data.metamodel.restrict.Restrict;
import jakarta.data.metamodel.restrict.Restriction;

public interface ComparableAttribute<T,V extends Comparable<V>>
        extends BasicAttribute<T,V>, SortableAttribute<T> {

    default Restriction<T> between(V min, V max) {
        return Restrict.between(min, max, name());
    }

    default Restriction<T> notBetween(V min, V max) {
        return Restrict.notBetween(min, max, name());
    }

    default Restriction<T> greaterThan(V value) {
        return Restrict.greaterThan(value, name());
    }

    default Restriction<T> greaterThanEqual(V value) {
        return Restrict.greaterThanEqual(value, name());
    }

    default Restriction<T> lessThan(V value) {
        return Restrict.lessThan(value, name());
    }

    default Restriction<T> lessThanEqual(V value) {
        return Restrict.lessThanEqual(value, name());
    }

}
