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
package jakarta.data.metamodel.constraint;

import static jakarta.data.metamodel.constraint.LikeRecord.ESCAPE;
import static jakarta.data.metamodel.constraint.LikeRecord.STRING_WILDCARD;
import static jakarta.data.metamodel.constraint.LikeRecord.translate;

import java.util.Objects;

import jakarta.data.metamodel.TextExpression;
import jakarta.data.metamodel.expression.StringLiteral;

public interface Like extends Constraint<String> {

    static Like pattern(String pattern) {
        Objects.requireNonNull(pattern, "Pattern is required");
        StringLiteral<Object> expression = StringLiteral.of(pattern);
        return new LikeRecord(expression, null);
    }

    static Like pattern(String pattern, char charWildcard, char stringWildcard) {
        return Like.pattern(pattern, charWildcard, stringWildcard, ESCAPE);
    }

    static Like pattern(String pattern, char charWildcard, char stringWildcard, char escape) {
        Objects.requireNonNull(pattern, "Pattern is required");
        StringLiteral<Object> expression = StringLiteral.of(
                translate(pattern, charWildcard, stringWildcard, escape));
        return new LikeRecord(expression, escape);
    }

    static Like pattern(TextExpression<?> pattern, char escape) {
        Objects.requireNonNull(pattern, "Pattern expression is required");
        return new LikeRecord(pattern, escape);
    }

    static Like prefix(String prefix) {
        Objects.requireNonNull(prefix, "Prefix is required");
        StringLiteral<Object> expression = StringLiteral.of(
                LikeRecord.escape(prefix) + STRING_WILDCARD);
        return new LikeRecord(expression, ESCAPE);
    }

    static Like substring(String substring) {
        Objects.requireNonNull(substring, "Substring is required");
        StringLiteral<Object> expression = StringLiteral.of(
                STRING_WILDCARD + LikeRecord.escape(substring) + STRING_WILDCARD);
        return new LikeRecord(expression, ESCAPE);
    }

    static Like suffix(String suffix) {
        Objects.requireNonNull(suffix, "Suffix is required");
        StringLiteral<Object> expression = StringLiteral.of(
                STRING_WILDCARD + LikeRecord.escape(suffix));
        return new LikeRecord(expression, ESCAPE);
    }

    static Like literal(String value) {
        Objects.requireNonNull(value, "Value is required");
        StringLiteral<Object> expression = StringLiteral.of(
                LikeRecord.escape(value));
        return new LikeRecord(expression, ESCAPE);
    }

    Character escape();

    TextExpression<?> pattern();
}
