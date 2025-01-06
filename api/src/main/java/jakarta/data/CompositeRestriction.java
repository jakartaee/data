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
package jakarta.data;

import java.util.List;

/**
 * Represents a composite restriction that combines multiple individual restrictions on an entity.
 * A {@code CompositeRestriction} is used to define conditions involving multiple restrictions, grouped by
 * logical constructs such as {@link Type#ALL} or {@link Type#ANY}. This interface enables the composition
 * of restrictions, supporting operations like negation and querying the structure of the composite restriction.
 * Composite restrictions are immutable. Any operation that modifies the state of a restriction,
 * such as negation, will return a new {@code CompositeRestriction} instance without altering the original.
 * This ensures thread safety and predictable behavior.
 *
 * <p>Example usage:
 * // TODO add example usage
 * @param <T> the entity type being restricted
 */
public interface CompositeRestriction<T> extends Restriction<T> {

    /**
     * Checks whether this composite restriction has been negated.
     * <p>
     * Negation inverts the meaning of the composite restriction. For example:
     * <ul>
     *   <li>A composite restriction of type {@link Type#ALL} negated becomes "NOT (ALL conditions are satisfied)."</li>
     *   <li>A composite restriction of type {@link Type#ANY} negated becomes "NOT (ANY condition is satisfied)."</li>
     * </ul>
     * </p>
     * //TODO does it make sense?
     * @return {@code true} if the composite restriction is negated, otherwise {@code false}
     */
    boolean isNegated();

    /**
     * Returns a new {@code CompositeRestriction} instance with the negated condition.
     * Negation inverts the meaning of this composite restriction while retaining its structure.
     * For example, a composite restriction of type {@link Type#ALL} will result in a new restriction
     * representing "NOT (ALL conditions)." Similarly, for {@link Type#ANY}, it will represent
     * "NOT (ANY condition)."
     * //TODO does it make sense?
     * @return a new negated {@code CompositeRestriction} instance
     */
    @Override
    CompositeRestriction<T> negate();

    /**
     * Retrieves the list of individual restrictions that make up this composite restriction.
     * The returned list contains the individual restrictions, in the order they were added.
     * These restrictions can be used to examine or analyze the structure of the composite restriction.
     *
     * @return a list of individual {@link Restriction} instances that form this composite restriction
     */
    List<Restriction<T>> restrictions();

    /**
     * Retrieves the type of the composite restriction, indicating how the individual restrictions
     * are evaluated.
     * The type can be one of the following:
     * <ul>
     *   <li>{@link Type#ALL}: All conditions in the composite restriction must be satisfied.</li>
     *   <li>{@link Type#ANY}: At least one condition in the composite restriction must be satisfied.</li>
     * </ul>
     *
     * @return the {@link Type} of the composite restriction
     */
    Type type();

    /**
     * Determines how individual restrictions within a composite restriction are evaluated.
     * The type specifies whether all conditions must be satisfied ({@link #ALL}) or at least
     * one condition must be satisfied ({@link #ANY}).
     */
    enum Type {
        /**
         * All conditions in the composite restriction must be satisfied.
         */
        ALL,

        /**
         * At least one condition in the composite restriction must be satisfied.
         */
        ANY
    }
}
