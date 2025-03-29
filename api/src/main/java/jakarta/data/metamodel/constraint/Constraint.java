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

    Constraint<T> negate();

    static <T> EqualTo<T> equalTo(T value) {
        return EqualTo.value(value);
    }

    static <T> NotEqualTo<T> notEqualTo(T value) {
        return NotEqualTo.value(value);
    }

    @SafeVarargs
    static <T> In<T> in(T... values) {
        return In.values(values);
    }

    static <T> In<T> in(Set<T> values) {
        return In.values(values);
    }

    @SafeVarargs
    static <T> NotIn<T> notIn(T... values) {
        return NotIn.values(values);
    }

    static <T> NotIn<T> notIn(Set<T> values) {
        return NotIn.values(values);
    }

    static Like like(String pattern) {
        return Like.pattern(pattern);
    }

    static Like like(String pattern, char charWildcard, char stringWildcard) {
        return Like.pattern(pattern, charWildcard, stringWildcard);
    }

    static Like like(String pattern, char charWildcard, char stringWildcard, char escape) {
        return Like.pattern(pattern, charWildcard, stringWildcard, escape);
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

    static <T> Null<T> isNull() {
        return Null.instance();
    }

    static <T> NotNull<T> notNull() {
        return NotNull.instance();
    }

    static <T extends Comparable<?>> GreaterThan<T> greaterThan(T bound) {
        return GreaterThan.bound(bound);
    }

    static <T extends Comparable<?>> LessThan<T> lessThan(T bound) {
        return LessThan.bound(bound);
    }

    static <T extends Comparable<?>> GreaterThanOrEqual<T> greaterThanOrEqual(T bound) {
        return GreaterThanOrEqual.min(bound);
    }

    static <T extends Comparable<?>> LessThanOrEqual<T> lessThanOrEqual(T bound) {
        return LessThanOrEqual.max(bound);
    }

    static <T extends Comparable<?>> Between<T> between(T lowerBound, T upperBound) {
        return Between.bounds(lowerBound, upperBound);
    }

    static <T extends Comparable<?>> NotBetween<T> notBetween(T lowerBound, T upperBound) {
        return NotBetween.bounds(lowerBound, upperBound);
    }
}
