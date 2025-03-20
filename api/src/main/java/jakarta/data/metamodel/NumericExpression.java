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

public interface NumericExpression<T, N extends Number & Comparable<N>>
        extends ComparableExpression<T, N> {

    default  NumericExpression<T,N> abs() {
        throw new UnsupportedOperationException("not yet implemented");
    }
    default NumericExpression<T,N> negated() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    default NumericExpression<T,N> plus(N other) {
        throw new UnsupportedOperationException("not yet implemented");
    }
    default NumericExpression<T,N> minus(N other) {
        throw new UnsupportedOperationException("not yet implemented");
    }
    default  NumericExpression<T,N> times(N other) {
        throw new UnsupportedOperationException("not yet implemented");
    }
    default NumericExpression<T,N> divide(N other) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    default NumericExpression<T,N> plus(Expression<T,N> other) {
        throw new UnsupportedOperationException("not yet implemented");
    }
    default NumericExpression<T,N> minus(Expression<T,N> other) {
        throw new UnsupportedOperationException("not yet implemented");
    }
    default NumericExpression<T,N> times(Expression<T,N> other) {
        throw new UnsupportedOperationException("not yet implemented");
    }
    default NumericExpression<T,N> divide(Expression<T,N> other) {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
