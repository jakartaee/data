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
import jakarta.data.metamodel.restrict.BasicRestriction;
import jakarta.data.metamodel.restrict.Restriction;

public interface ComparableExpression<T,V extends Comparable<?>>
        extends Expression<T, V> {

    default Restriction<T> between(V min, V max) {
        return BasicRestriction.of(this, Between.bounds(min, max));
    }

    default Restriction<T> notBetween(V min, V max) {
        return BasicRestriction.of(this, NotBetween.bounds(min, max));
    }

    default Restriction<T> greaterThan(V value) {
        return BasicRestriction.of(this, GreaterThan.bound(value));
    }

    default Restriction<T> greaterThanEqual(V value) {
        return BasicRestriction.of(this, GreaterThanOrEqual.min(value));
    }

    default Restriction<T> lessThan(V value) {
        return BasicRestriction.of(this, LessThan.bound(value));
    }

    default Restriction<T> lessThanEqual(V value) {
        return BasicRestriction.of(this, LessThanOrEqual.max(value));
    }

    // Leave for later, since we need a new kind of Restriction for these

//    default Restriction<T> greaterThan(Expression<? super T,V> value) {
//        throw new UnsupportedOperationException("not yet implemented");
//    }
//
//    default Restriction<T> greaterThanEqual(Expression<? super T,V> value) {
//        throw new UnsupportedOperationException("not yet implemented");
//    }
//
//    default Restriction<T> lessThan(Expression<? super T,V> value) {
//        throw new UnsupportedOperationException("not yet implemented");
//    }
//
//    default Restriction<T> lessThanEqual(Expression<? super T,V> value) {
//        throw new UnsupportedOperationException("not yet implemented");
//    }
}
