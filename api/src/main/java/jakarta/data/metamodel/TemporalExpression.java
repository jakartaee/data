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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;

import jakarta.data.metamodel.expression.CurrentDate;
import jakarta.data.metamodel.expression.CurrentDateTime;
import jakarta.data.metamodel.expression.CurrentTime;

public interface TemporalExpression<T, V extends Temporal & Comparable<? extends Temporal>>
        extends ComparableExpression<T, V> {

    static <T> TemporalExpression<T, LocalDate> currentDate() {
        return CurrentDate.now();
    }

    static <T> TemporalExpression<T, LocalDateTime> currentDateTime() {
        return CurrentDateTime.now();
    }

    static <T> TemporalExpression<T, LocalTime> currentTime() {
        return CurrentTime.now();
    }

    // TODO add methods to obtain the year, month, day, hour, and so forth
    // after EXTRACT is added to the query language in some future release

    // Methods for year/month/day would apply to Instant, LocalDateTime, and LocalDate
    // while hour/minute/second would apply to Instant, LocalDateTime, and LocalTime.
    // We need to also consider if it is okay to have some methods that wouldn't
    // apply to LocalDate or LocalTime, or if we should have more specific types
    // instead of Temporal.

    // TODO methods to compare with other TemporalExpressions need to be added,
    // but similar methods are commented out elsewhere, so doing that here as well,

    // default Restriction<T> between(TemporalExpression<T,V> min,
    //                               TemporalExpression<T,V> max)

    // default Restriction<T> equalTo(TemporalExpression<T,V> value)

    // default Restriction<T> greaterThan(TemporalExpression<T,V> value)

    // default Restriction<T> greaterThanEqual(TemporalExpression<T,V> value)

    // default Restriction<T> lessThan(TemporalExpression<T,V> value)

    // default Restriction<T> lessThanEqual(TemporalExpression<T,V> value)

    // default Restriction<T> notBetween(TemporalExpression<T,V> min,
    //                                  TemporalExpression<T,V> max)

    // default Restriction<T> notEqualTo(TemporalExpression<T,V> value)
}
