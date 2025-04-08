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

import jakarta.data.metamodel.constraint.Between;
import jakarta.data.metamodel.constraint.GreaterThan;
import jakarta.data.metamodel.constraint.GreaterThanOrEqual;
import jakarta.data.metamodel.constraint.LessThan;
import jakarta.data.metamodel.constraint.LessThanOrEqual;
import jakarta.data.metamodel.constraint.NotBetween;
import jakarta.data.metamodel.restrict.ValueRestriction;
import jakarta.data.metamodel.restrict.ExpressionRestriction;
import jakarta.data.metamodel.restrict.Restriction;

// We need ComparableExpression to be supplied as a constraint value to
// LessThan/GreaterThan/... and so forth, but these constraint values are
// required to be Comparable. For the most part, we could make
// ComparableExpression be Comparable to satisfy that, although we never
// use its compareTo method. We should look into whether there are better
// options, but doing this at least temporarily could be a path forward
// for now.
public interface ComparableExpression<T,V extends Comparable<?>>
        extends Expression<T, V>, Comparable<ComparableExpression<T, V>> {

    default Restriction<T> between(V min, V max) {
        return ValueRestriction.of(this, Between.bounds(min, max));
    }

    default <U extends ComparableExpression<? super T, V>> Restriction<T> between(
        U minExpression,
        U maxExpression) {
        return ExpressionRestriction
                .of(this, Between.bounds(minExpression, maxExpression));
    }

    default Restriction<T> greaterThan(V value) {
        return ValueRestriction.of(this, GreaterThan.bound(value));
    }

    default Restriction<T> greaterThan(ComparableExpression<? super T, V> expression) {
        return ExpressionRestriction.of(this, GreaterThan.bound(expression));
    }

    default Restriction<T> greaterThanEqual(V value) {
        return ValueRestriction.of(this, GreaterThanOrEqual.min(value));
    }

    default Restriction<T> greaterThanEqual(ComparableExpression<? super T, V> expression) {
        return ExpressionRestriction.of(this, GreaterThanOrEqual.min(expression));
    }

    default Restriction<T> lessThan(V value) {
        return ValueRestriction.of(this, LessThan.bound(value));
    }

    default Restriction<T> lessThan(ComparableExpression<? super T, V> expression) {
        return ExpressionRestriction.of(this, LessThan.bound(expression));
    }

    default Restriction<T> lessThanEqual(V value) {
        return ValueRestriction.of(this, LessThanOrEqual.max(value));
    }

    default Restriction<T> lessThanEqual(ComparableExpression<? super T, V> expression) {
        return ExpressionRestriction.of(this, LessThanOrEqual.max(expression));
    }

    default Restriction<T> notBetween(V min, V max) {
        return ValueRestriction.of(this, NotBetween.bounds(min, max));
    }

    default <U extends ComparableExpression<? super T, V>> Restriction<T> notBetween(
            U minExpression,
            U maxExpression) {
        return ExpressionRestriction
                .of(this, NotBetween.bounds(minExpression, maxExpression));
    }
}
