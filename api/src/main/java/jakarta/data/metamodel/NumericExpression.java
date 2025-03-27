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

import jakarta.data.metamodel.function.NumericFunction;
import jakarta.data.metamodel.function.NumericFunctionRecord;

public interface NumericExpression<T, N extends Number & Comparable<N>>
        extends ComparableExpression<T, N> {

    default NumericFunction<T,N> abs() {
        return new NumericFunctionRecord<>("abs", this);
    }
    default NumericFunction<T,N> negated() {
        return new NumericFunctionRecord<>("neg", this);
    }

    default NumericFunction<T,N> plus(N other) {
        return new NumericFunctionRecord<>("plus", this, other);
    }
    default NumericFunction<T,N> minus(N other) {
        return new NumericFunctionRecord<>("minus", this, other);
    }
    default NumericFunction<T,N> times(N other) {
        return new NumericFunctionRecord<>("times", this, other);
    }
    default NumericFunction<T,N> divide(N other) {
        return new NumericFunctionRecord<>("divide", this, other);
    }

    default NumericFunction<T,N> plus(Expression<T,N> other) {
        return new NumericFunctionRecord<>("plus", this, other);
    }
    default NumericFunction<T,N> minus(Expression<T,N> other) {
        return new NumericFunctionRecord<>("minus", this, other);
    }
    default NumericFunction<T,N> times(Expression<T,N> other) {
        return new NumericFunctionRecord<>("times", this, other);
    }
    default NumericFunction<T,N> divide(Expression<T,N> other) {
        return new NumericFunctionRecord<>("divide", this, other);
    }
}
