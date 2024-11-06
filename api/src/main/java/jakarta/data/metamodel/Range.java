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
package jakarta.data.metamodel;

import jakarta.data.Operator;

import java.util.List;

/**
 * Represents a range-based restriction for repository queries, allowing comparisons within
 * a specified range or boundary on a particular entity attribute.
 *
 * <p>This class implements {@link Restriction<T>} and provides flexible factory methods
 * to create range-based conditions, supporting inclusive and exclusive bounds.</p>
 *
 * <p>Usage examples:</p>
 * <pre>
 * // A range between two dates (inclusive)
 * Restriction<Book> publicationDateRange = Range.between(_Book.publicationDate, pastDate, LocalDate.now());
 *
 * // An exclusive range above a certain rating
 * Restriction<Book> ratingAbove = Range.above(_Book.rating, 4.5);
 *
 * // An inclusive range up to a certain price
 * Restriction<Book> priceTo = Range.to(_Book.price, 100);
 * </pre>
 *
 * <p>The `operator()` method dynamically determines the appropriate operator based on the range type:</p>
 * <ul>
 *     <li>If both bounds are specified, {@code Operator.BETWEEN} is used.</li>
 *     <li>If only the lower bound is specified and the range is exclusive, {@code Operator.GREATER_THAN} is used.</li>
 *     <li>If only the lower bound is specified and the range is inclusive, {@code Operator.GREATER_THAN_EQUAL} is used.</li>
 *     <li>If only the upper bound is specified and the range is exclusive, {@code Operator.LESS_THAN} is used.</li>
 *     <li>If only the upper bound is specified and the range is inclusive, {@code Operator.LESS_THAN_EQUAL} is used.</li>
 * </ul>
 *
 * @param <T> the type of the entity's attribute on which the range is applied.
 */
public record Range<T>(String field, T lowerBound, T upperBound, boolean open) implements Restriction<T> {

    @Override
    public String field() {
        return field;
    }

    @Override
    public Operator operator() {
        if (lowerBound != null && upperBound != null) {
            return Operator.BETWEEN;
        } else if (lowerBound != null && open) {
            return Operator.GREATER_THAN;
        } else if (lowerBound != null) {
            return Operator.GREATER_THAN_EQUAL;
        } else if (upperBound != null && open) {
            return Operator.LESS_THAN;
        } else {
            return Operator.LESS_THAN_EQUAL;
        }
    }

    @Override
    public Object value() {
        if (lowerBound != null && upperBound != null) {
            return List.of(lowerBound, upperBound);
        }
        return lowerBound != null ? lowerBound : upperBound;
    }

    public static <T> Range<T> between(String field, T lowerBound, T upperBound) {
        return new Range<>(field, lowerBound, upperBound, false);
    }

    public static <T> Range<T> from(String field, T lowerBound) {
        return new Range<>(field, lowerBound, null, false);
    }

    public static <T> Range<T> to(String field, T upperBound) {
        return new Range<>(field, null, upperBound, false);
    }

    public static <T> Range<T> above(String field, T lowerBound) {
        return new Range<>(field, lowerBound, null, true);
    }

    public static <T> Range<T> below(String field, T upperBound) {
        return new Range<>(field, null, upperBound, true);
    }
}
