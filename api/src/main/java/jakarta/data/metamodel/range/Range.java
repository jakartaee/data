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
package jakarta.data.metamodel.range;

import jakarta.data.metamodel.restrict.Operator;

import java.util.Set;

public sealed interface Range<T>
        permits Value, Enumeration, NullValue,
                LowerBound, UpperBound, Interval,
                TextRange {
    Operator operator();

    static <T> Range<T> value(T value) {
        return new Value<>(value);
    }

    @SafeVarargs
    static <T> Range<T> enumeration(T... values) {
        return new Enumeration<>(Set.of(values));
    }

    static <T> Range<T> enumeration(Set<T> values) {
        return new Enumeration<>(values);
    }

    static <T> Range<T> nullValue() {
        return new NullValue<>();
    }

    static <T extends Comparable<T>> Range<T> lowerBound(T bound) {
        return new LowerBound<>(bound);
    }

    static <T extends Comparable<T>> Range<T> upperBound(T bound) {
        return new UpperBound<>(bound);
    }

    static <T extends Comparable<T>> Range<T> lowerBound(T bound, boolean strict) {
        return new LowerBound<>(bound, strict);
    }

    static <T extends Comparable<T>> Range<T> upperBound(T bound, boolean strict) {
        return new UpperBound<>(bound, strict);
    }

    static <T extends Comparable<T>> Range<T> interval(T lowerBound, T upperBound) {
        return new Interval<>(lowerBound, upperBound);
    }

}
