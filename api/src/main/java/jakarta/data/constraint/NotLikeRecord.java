/*
 * Copyright (c) 2025,2026 Contributors to the Eclipse Foundation
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
package jakarta.data.constraint;

import static jakarta.data.constraint.EscapeRule.ESCAPE;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.data.expression.TextExpression;
import jakarta.data.spi.expression.literal.StringLiteral;

record NotLikeRecord(StringLiteral unescapedPattern,
                     EscapeRule escapeRule,
                     AtomicReference<TextExpression<?>> escapedPatternRef,
                     char escape)
        implements NotLike {

    /**
     * Constructor for when an unescaped pattern is available and the escaped
     * pattern is lazily computed.
     *
     * @param unescaped  unescaped pattern.
     * @param escapeRule rule for adding escapes to the unescaped pattern.
     */
    NotLikeRecord(String unescaped,
                  EscapeRule escapeRule) {
        this(StringLiteral.of(unescaped),
             escapeRule,
             new AtomicReference<>(), 
             ESCAPE);
    }

    /**
     * Constructor for when an escaped pattern is available (or must be computed)
     * in advance. An unescaped pattern might or might not be available.
     *
     * @param unescapedPattern unescaped pattern if available. Otherwise null.
     * @param escapedPattern   escaped pattern.
     * @param escape           escape character.
     */
    NotLikeRecord(StringLiteral unescapedPattern,
                  TextExpression<?> escapedPattern,
                  char escape) {
        this(unescapedPattern,
             null, // no EscapeRule needed because we already have escapedPattern
             new AtomicReference<>(escapedPattern),
             escape);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof NotLikeRecord notlike &&
            notlike.escape == escape &&
            notlike.escapeRule == escapeRule) {
            return notlike.unescapedPattern == null
                    ? Objects.equals(notlike.escapedPatternRef.get(),
                                     escapedPatternRef.get())
                    : notlike.unescapedPattern.equals(unescapedPattern);
        }
        return false;
    }

    @Override
    public TextExpression<?> escapedPattern() {
        TextExpression<?> escapedPattern = escapedPatternRef.get();
        if (escapedPattern == null) {
            escapedPattern = StringLiteral.of(
                    escapeRule.apply(unescapedPattern.value()));
            escapedPatternRef.set(escapedPattern);
        }
        return escapedPattern;
    }

    @Override
    public int hashCode() {
        return Objects.hash(escape,
                            escapeRule,
                            unescapedPattern == null
                                    ? escapedPatternRef.get()
                                    : unescapedPattern);
    }

    @Override
    public Like negate() {
        return new LikeRecord(unescapedPattern,
                              escapeRule,
                              escapedPatternRef,
                              escape);
    }

    @Override
    public String toString() {
        TextExpression<?> escapedPattern = escapedPatternRef.get();
        if (escapedPattern == null) {
            return "NOT LIKE " + unescapedPattern;
        } else {
            return "NOT LIKE " + escapedPattern + " ESCAPE '" + escape + "'";
        }
    }
}
