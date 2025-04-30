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
package jakarta.data.expression;

import jakarta.data.constraint.Between;
import jakarta.data.constraint.GreaterThan;
import jakarta.data.constraint.GreaterThanOrEqual;
import jakarta.data.constraint.LessThan;
import jakarta.data.constraint.LessThanOrEqual;
import jakarta.data.constraint.NotBetween;
import jakarta.data.restrict.BasicRestriction;
import jakarta.data.restrict.Restriction;

public interface ComparableExpression<T, V extends Comparable<?>>
        extends Expression<T, V> {

    default Restriction<T> between(V min, V max) {
        return BasicRestriction.of(this, Between.bounds(min, max));
    }

    default <U extends ComparableExpression<? super T, V>> Restriction<T> between(
            U minExpression,
            U maxExpression) {
        return BasicRestriction.of(
                this,
                Between.bounds(minExpression, maxExpression));
    }

    default Restriction<T> greaterThan(V value) {
        return BasicRestriction.of(this, GreaterThan.bound(value));
    }

    default Restriction<T> greaterThan(ComparableExpression<? super T, V> expression) {
        return BasicRestriction.of(this, GreaterThan.bound(expression));
    }

    default Restriction<T> greaterThanEqual(V value) {
        return BasicRestriction.of(this, GreaterThanOrEqual.min(value));
    }

    default Restriction<T> greaterThanEqual(ComparableExpression<? super T, V> expression) {
        return BasicRestriction.of(this, GreaterThanOrEqual.min(expression));
    }

    default Restriction<T> lessThan(V value) {
        return BasicRestriction.of(this, LessThan.bound(value));
    }

    default Restriction<T> lessThan(ComparableExpression<? super T, V> expression) {
        return BasicRestriction.of(this, LessThan.bound(expression));
    }

    default Restriction<T> lessThanEqual(V value) {
        return BasicRestriction.of(this, LessThanOrEqual.max(value));
    }

    default Restriction<T> lessThanEqual(ComparableExpression<? super T, V> expression) {
        return BasicRestriction.of(this, LessThanOrEqual.max(expression));
    }

    default Restriction<T> notBetween(V min, V max) {
        return BasicRestriction.of(this, NotBetween.bounds(min, max));
    }

    default <U extends ComparableExpression<? super T, V>> Restriction<T> notBetween(
            U minExpression,
            U maxExpression) {
        return BasicRestriction.of(
                this,
                NotBetween.bounds(minExpression, maxExpression));
    }
}
