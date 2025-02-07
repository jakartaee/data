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
package jakarta.data.metamodel.restrict;

import java.util.List;

public interface CompositeRestriction<T> extends Restriction<T> {
    boolean isNegated();

    @Override
    CompositeRestriction<T> negate();

    List<Restriction<T>> restrictions();

    Type type();

    enum Type {
        ALL("AND"),
        ANY("OR");

        /**
         * Representation of the corresponding logical operator in query language.
         */
        private final String qlLogicalOperator;

        private Type(String qlLogicalOperator) {
            this.qlLogicalOperator = qlLogicalOperator;
        }

        /**
         * Representation of the composite restriction type as a query language
         * logical operator.
         * For example, {@link #ALL} is represented as {@code AND} in query language.
         *
         * @return the representation as a logical operator in query language.
         */
        String asQueryLanguage() {
            return qlLogicalOperator;
        }
    }
}
