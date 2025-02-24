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
package jakarta.data.metamodel.constraint;

import jakarta.data.metamodel.restrict.Operator;

import java.util.Objects;

record LikeLiteralRecord(String literal, boolean caseSensitive)
        implements Like {

    public LikeLiteralRecord {
        Objects.requireNonNull(literal, "Literal must not be null");
    }

    public LikeLiteralRecord(String literal) {
        this(literal, true);
    }

    @Override
    public String string() {
        return literal;
    }

    @Override
    public Like ignoreCase() {
        return new LikeLiteralRecord(literal, false);
    }

    @Override
    public Character escape() {
        return null;
    }

    @Override
    public boolean pattern() {
        return false;
    }

    @Override
    public Operator operator() {
        return Operator.EQUAL;
    }

    @Override
    public String toString() {
        return "= '" + literal + "'" + (caseSensitive ? "" : " IGNORE CASE");
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LikeLiteralRecord that
            && literal.equals(that.literal)
            && caseSensitive == that.caseSensitive;
    }

    @Override
    public int hashCode() {
        return literal.hashCode();
    }
}
