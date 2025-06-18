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
package jakarta.data.constraint;

import java.util.Set;

public interface Constraint<V> {

    Constraint<V> negate();

    static <V> EqualTo<V> equalTo(V value) {
        return EqualTo.value(value);
    }

    static <V> NotEqualTo<V> notEqualTo(V value) {
        return NotEqualTo.value(value);
    }

    @SafeVarargs
    static <V> In<V> in(V... values) {
        return In.values(values);
    }

    static <V> In<V> in(Set<V> values) {
        return In.values(values);
    }

    @SafeVarargs
    static <V> NotIn<V> notIn(V... values) {
        return NotIn.values(values);
    }

    static <V> NotIn<V> notIn(Set<V> values) {
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

    static <V> Null<V> isNull() {
        return Null.instance();
    }

    static <V> NotNull<V> notNull() {
        return NotNull.instance();
    }

    static <V extends Comparable<?>> GreaterThan<V> greaterThan(V bound) {
        return GreaterThan.bound(bound);
    }

    static <V extends Comparable<?>> LessThan<V> lessThan(V bound) {
        return LessThan.bound(bound);
    }

    static <V extends Comparable<?>> GreaterThanEqual<V> greaterThanEqual(V bound) {
        return GreaterThanEqual.min(bound);
    }

    static <V extends Comparable<?>> LessThanEqual<V> lessThanEqual(V bound) {
        return LessThanEqual.max(bound);
    }

    static <V extends Comparable<?>> Between<V> between(V lowerBound, V upperBound) {
        return Between.bounds(lowerBound, upperBound);
    }

    static <V extends Comparable<?>> NotBetween<V> notBetween(V lowerBound, V upperBound) {
        return NotBetween.bounds(lowerBound, upperBound);
    }
}
