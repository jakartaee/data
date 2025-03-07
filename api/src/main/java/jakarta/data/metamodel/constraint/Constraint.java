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
package jakarta.data.metamodel.constraint;

import java.util.Set;

public interface Constraint<T> {

    static <T> Constraint<T> equalTo(T value) {
        return EqualTo.value(value);
    }

    @SafeVarargs
    static <T> Constraint<T> in(T... values) {
        return new InRecord<>(Set.of(values));
    }

    static <T> Constraint<T> in(Set<T> values) {
        return new InRecord<>(values);
    }

    static <T> Constraint<T> isNull() {
        return new NullRecord<>();
    }

    static <T extends Comparable<T>> GreaterThan<T> greaterThan(T bound) {
        return GreaterThan.bound(bound);
    }

    static <T extends Comparable<T>> LessThan<T> lessThan(T bound) {
        return LessThan.bound(bound);
    }

    static <T extends Comparable<T>> GreaterThanOrEqual<T> greaterThanOrEqual(T bound) {
        return GreaterThanOrEqual.min(bound);
    }

    static <T extends Comparable<T>> LessThanOrEqual<T> lessThanOrEqual(T bound) {
        return LessThanOrEqual.max(bound);
    }

    static <T extends Comparable<T>> Between<T> between(T lowerBound, T upperBound) {
        return Between.bounds(lowerBound, upperBound);
    }

    Constraint<T> negate();

    static <T extends Comparable<T>> Constraint<T> notBetween(T lowerBound, T upperBound) {
        return NotBetween.bounds(lowerBound, upperBound);
    }

    static <T> NotEqualTo<T> notEqualTo(T value) {
        return NotEqualTo.value(value);
    }

    @SafeVarargs
    static <T> Constraint<T> notIn(T... values) {
        return NotIn.values(values);
    }

    static <T> Constraint<T> notIn(Set<T> values) {
        return NotIn.values(values);
    }

    static NotLike notLike(String pattern) {
        return NotLike.pattern(pattern);
    }

    static NotLike notLike(String pattern, char charWildcard, char stringWildcard) {
        return NotLike.pattern(pattern, charWildcard, stringWildcard);
    }

    static NotLike notLike(String pattern, char charWildcard, char stringWildcard, char escape) {
        return NotLike.pattern(pattern, charWildcard, stringWildcard, escape);
    }

    static <T> Constraint<T> notNull() {
        return NotNull.instance();
    }
}
