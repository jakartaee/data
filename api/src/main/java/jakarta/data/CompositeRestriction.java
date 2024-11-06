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

import java.util.Iterator;
import java.util.List;

/**
 * A composite restriction representing a collection of individual {@link Restriction}
 * and {@link LogicalOperator} instances, combined under a single logical operation.
 *
 * <p>This record allows multiple restrictions to be treated as a single entity, making
 * it easy to pass complex conditions to repository methods.</p>
 *
 * @param <T> the entity type that the restrictions apply to.
 */
public record CompositeRestriction<T>(LogicalOperator operator, List<Restriction<? super T>> restrictions) implements Iterable<Restriction<? super T>> {

    /**
     * Constructs a composite restriction with the specified operator and list of restrictions.
     *
     * @param operator     the logical operator (AND or OR) to apply between the restrictions.
     * @param restrictions the list of restrictions to combine.
     */
    public CompositeRestriction {
        restrictions = List.copyOf(restrictions); // Ensure immutability of the list
    }

    @Override
    public Iterator<Restriction<? super T>> iterator() {
        return restrictions.iterator();
    }

    /**
     * Creates a composite restriction where all specified restrictions must be true (AND logic).
     *
     * @param restrictions the individual restrictions to combine.
     * @param <T>          the entity type that the restrictions apply to.
     * @return a CompositeRestriction representing the AND combination of the provided restrictions.
     */
    @SafeVarargs
    public static <T> CompositeRestriction<T> all(Restriction<T>... restrictions) {
        return new CompositeRestriction<>(LogicalOperator.AND, List.of(restrictions));
    }

    /**
     * Creates a composite restriction where any of the specified restrictions may be true (OR logic).
     *
     * @param restrictions the individual restrictions to combine.
     * @param <T>          the entity type that the restrictions apply to.
     * @return a CompositeRestriction representing the OR combination of the provided restrictions.
     */
    @SafeVarargs
    public static <T> CompositeRestriction<T> any(Restriction<T>... restrictions) {
        return new CompositeRestriction<>(LogicalOperator.OR, List.of(restrictions));
    }


}
