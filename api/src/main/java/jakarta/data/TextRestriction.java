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
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data;

// Internal implementation class.
// The proper way for users to obtain instances is via
// the static metamodel or Restrict.* methods 

record TextRestriction<T>(
        String field,
        boolean isNegated,
        Operator comparison,
        boolean isAnyCase,
        boolean isEscaped,
        Object value) implements Restriction.Text<T> {

    TextRestriction(String field, boolean negated, Operator comparison, boolean escaped, Object value) {
        this(field, negated, comparison, false, escaped, value);
    }

    TextRestriction(String field, boolean negated, Operator comparison, Object value) {
        this(field, negated, comparison, false, false, value);
    }

    TextRestriction(String field, Operator comparison, boolean escaped, Object value) {
        this(field, false, comparison, false, escaped, value);
    }

    TextRestriction(String field, Operator comparison, Object value) {
        this(field, false, comparison, false, false, value);
    }

    @Override
    public Restriction<T> ignoreCase() {
        return new TextRestriction<>(field, isNegated, comparison, true, value);
    }
}