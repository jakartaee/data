/*
 * Copyright (c) 2024,2025 Contributors to the Eclipse Foundation
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
package jakarta.data.restrict;

import java.util.List;

/**
 * <p>A Jakarta Data provider's view of a restriction that combines other
 * restrictions.</p>
 *
 * <p>This class is used by the Jakarta Data provider to interpret a
 * restriction that is supplied by the application. The application should use
 * the {@link Restrict#all(Restriction...)} and
 * {@link Restrict#any(Restriction...)} methods to obtain composite
 * restrictions.</p>
 *
 * @param <T> entity type.
 * @since 1.1
 */
public interface CompositeRestriction<T> extends Restriction<T> {
    /**
     * <p>Indicates if the collective combination of {@link #restrictions()}
     * should be negated. The combination of restrictions is evaluated first,
     * according to its {@link #type()}, after which negation is applied to the
     * evaluated result.</p>
     *
     * @return {@code true} if negated, otherwise {@code false}.
     */
    boolean isNegated();

    /**
     * <p>An ordered list of restrictions. The order must match the order that
     * is specified by the application.</p>
     *
     * <p>The elements can be {@link BasicRestriction} or
     * {@link CompositeRestriction} and must not be {@code null}.</p>
     *
     * <p>Any negation that is indicated by the {@link #isNegated()} method has
     * not been applied to the restrictions in the list because the negation
     * instead applies to the entire combination.</p>
     *
     * @return the ordered list of restriction.
     */
    List<Restriction<T>> restrictions();

    /**
     * <p>Indicates how to combine the list of {@link #restrictions()}.</p>
     *
     * @return how to combine the list of restrictions.
     */
    Type type();

    /**
     * <p>Indicates how to combine a list of
     * {@linkplain CompositeRestriction#restrictions() restrictions}.</p>
     */
    enum Type {
        /**
         * <p>Indicates that all restrictions within a list must be satisfied
         * in order for the combination of restrictions to be considered
         * satisfied. If the list is empty, the combination is considered
         * satisfied.</p>
         *
         * <p>{@linkplain CompositeRestriction#isNegated() Negation} is applied
         * to a composite restriction after evaluating the combination of
         * restrictions.</p>
         */
        ALL,

        /**
         * <p>Indicates that at least one of the restrictions within a list
         * must be satisfied in order for the combination of restriction to be
         * considered satisfied. If the list is empty, the combination is
         * is considered unsatisfied.</p>
         *
         * <p>{@linkplain CompositeRestriction#isNegated() Negation} is applied
         * to a composite restriction after evaluating the combination of
         * restrictions.</p>
         */
        ANY;

        /**
         * Representation of the composite restriction type as a query language
         * logical operator. {@link #ALL} is represented as {@code AND} in
         * query language, and {@link #ANY} is represented as {@code OR}.
         *
         * @return the representation as a logical operator in query language.
         */
        String asQueryLanguage() {
            return switch (this) {
                case ALL -> "AND";
                case ANY -> "OR";
            };
        }
    }
}
