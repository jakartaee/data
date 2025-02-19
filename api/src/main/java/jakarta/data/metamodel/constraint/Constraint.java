/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package jakarta.data.metamodel.constraint;

import jakarta.data.metamodel.restrict.Operator;

import java.util.Set;

public interface Constraint<T> {
    Operator operator();

    static <T> Constraint<T> value(T value) {
        return new EqualToRecord<>(value);
    }

    @SafeVarargs
    static <T> Constraint<T> enumeration(T... values) {
        return new InRecord<>(Set.of(values));
    }

    static <T> Constraint<T> enumeration(Set<T> values) {
        return new InRecord<>(values);
    }

    static <T> Constraint<T> nullValue() {
        return new NullRecord<>();
    }

    static <T extends Comparable<T>> Constraint<T> lowerBound(T bound) {
        return new GreaterThanRecord<>(bound);
    }

    static <T extends Comparable<T>> Constraint<T> upperBound(T bound) {
        return new LessThanRecord<>(bound);
    }

    static <T extends Comparable<T>> Constraint<T> lowerBound(T bound, boolean strict) {
        return new GreaterThanRecord<>(bound, strict);
    }

    static <T extends Comparable<T>> Constraint<T> upperBound(T bound, boolean strict) {
        return new LessThanRecord<>(bound, strict);
    }

    static <T extends Comparable<T>> Constraint<T> interval(T lowerBound, T upperBound) {
        return new BetweenRecord<>(lowerBound, upperBound);
    }

}
