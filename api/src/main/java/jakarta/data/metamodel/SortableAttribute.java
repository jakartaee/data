/*
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
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
import jakarta.data.Sort;

/**
 * Represents a sortable entity attribute in the {@link StaticMetamodel}.
 * Entity attribute types that are sortable include:
 *
 * <ul>
 * <li>numeric attributes</li>
 * <li>enum attributes</li>
 * <li>time attributes</li>
 * <li>boolean attributes</li>
 * <li>{@link TextAttribute textual attributes}</li>
 * </ul>
 *
 * @param <T> entity class of the static metamodel.
 */
public interface SortableAttribute<T> extends Attribute<T> {

    /**
     * Obtain a request for an ascending {@link Sort} based on the entity attribute.
     *
     * @return a request for an ascending sort on the entity attribute.
     */
    Sort<T> asc();

    /**
     * Obtain a request for a descending {@link Sort} based on the entity attribute.
     *
     * @return a request for a descending sort on the entity attribute.
     */
    Sort<T> desc();

    /**
     * Creates a restriction for values greater than the specified value.
     *
     * @param value the lower bound (exclusive) for the attribute.
     * @return a Restriction representing a greater-than condition.
     */
    default Restriction<T> greaterThan(Object value) {
        return new SimpleRestriction<>(name(), Operator.GREATER_THAN, value);
    }

    /**
     * Creates a restriction for values greater than or equal to the specified value.
     *
     * @param value the lower bound (inclusive) for the attribute.
     * @return a Restriction representing a greater-than-or-equal condition.
     */
    default Restriction<T> greaterThanOrEqual(Object value) {
        return new SimpleRestriction<>(name(), Operator.GREATER_THAN_EQUAL, value);
    }

    /**
     * Creates a restriction for values less than the specified value.
     *
     * @param value the upper bound (exclusive) for the attribute.
     * @return a Restriction representing a less-than condition.
     */
    default Restriction<T> lessThan(Object value) {
        return new SimpleRestriction<>(name(), Operator.LESS_THAN, value);
    }

    /**
     * Creates a restriction for values less than or equal to the specified value.
     *
     * @param value the upper bound (inclusive) for the attribute.
     * @return a Restriction representing a less-than-or-equal condition.
     */
    default Restriction<T> lessThanOrEqual(Object value) {
        return new SimpleRestriction<>(name(), Operator.LESS_THAN_EQUAL, value);
    }

    /**
     * Creates a restriction for values within the specified range, inclusive of both bounds.
     *
     * @param lowerBound the lower bound (inclusive).
     * @param upperBound the upper bound (inclusive).
     * @return a Restriction representing a range condition.
     */
    default Range<T> between(T lowerBound, T upperBound) {
        return Range.between(name(), lowerBound, upperBound);
    }

    /**
     * Creates a restriction for values greater than or equal to the specified lower bound.
     *
     * @param lowerBound the lower bound (inclusive).
     * @return a Restriction representing a "greater than or equal" condition.
     */
    default Range<T> from(T lowerBound) {
        return Range.from(name(), lowerBound);
    }

    /**
     * Creates a restriction for values less than or equal to the specified upper bound.
     *
     * @param upperBound the upper bound (inclusive).
     * @return a Restriction representing a "less than or equal" condition.
     */
    default Range<T> to(T upperBound) {
        return Range.to(name(), upperBound);
    }

    /**
     * Creates a restriction for values greater than the specified lower bound (exclusive).
     *
     * @param lowerBound the lower bound (exclusive).
     * @return a Restriction representing a "greater than" condition.
     */
    default Range<T> above(T lowerBound) {
        return Range.above(name(), lowerBound);
    }

    /**
     * Creates a restriction for values less than the specified upper bound (exclusive).
     *
     * @param upperBound the upper bound (exclusive).
     * @return a Restriction representing a "less than" condition.
     */
    default Range<T> below(T upperBound) {
        return Range.below(name(), upperBound);
    }

}
