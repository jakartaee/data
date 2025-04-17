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

import static jakarta.data.metamodel.constraint.LikeRecord.ESCAPE;
import static jakarta.data.metamodel.constraint.LikeRecord.STRING_WILDCARD;
import static jakarta.data.metamodel.constraint.LikeRecord.translate;

import java.util.Objects;

import jakarta.data.metamodel.TextExpression;
import jakarta.data.metamodel.expression.StringLiteral;

public interface NotLike extends Constraint<String> {

    static NotLike pattern(String pattern) {
        Objects.requireNonNull(pattern, "Pattern is required.");
        StringLiteral<Object> expression = StringLiteral.of(pattern);
        return new NotLikeRecord(expression, null);
    }

    static NotLike pattern(String pattern, char charWildcard, char stringWildcard) {
        return NotLike.pattern(pattern, charWildcard, stringWildcard, ESCAPE);
    }

    static NotLike pattern(String pattern, char charWildcard, char stringWildcard, char escape) {
        Objects.requireNonNull(pattern, "Pattern is required.");
        StringLiteral<Object> expression = StringLiteral.of(
                translate(pattern, charWildcard, stringWildcard, escape));
        return new NotLikeRecord(expression, escape);
    }

    static NotLike pattern(TextExpression<?> pattern, char escape) {
        Objects.requireNonNull(pattern, "Pattern expression is required.");
        return new NotLikeRecord(pattern, escape);
    }

    static NotLike prefix(String prefix) {
        Objects.requireNonNull(prefix, "Prefix is required.");
        StringLiteral<Object> expression = StringLiteral.of(
                LikeRecord.escape(prefix) + STRING_WILDCARD);
        return new NotLikeRecord(expression, ESCAPE);
    }

    static NotLike substring(String substring) {
        Objects.requireNonNull(substring, "Substring is required.");
        StringLiteral<Object> expression = StringLiteral.of(
                STRING_WILDCARD + LikeRecord.escape(substring) + STRING_WILDCARD);
        return new NotLikeRecord(expression, ESCAPE);
    }

    static NotLike suffix(String suffix) {
        Objects.requireNonNull(suffix, "Suffix is required.");
        StringLiteral<Object> expression = StringLiteral.of(
                STRING_WILDCARD + LikeRecord.escape(suffix));
        return new NotLikeRecord(expression, ESCAPE);
    }

    static NotLike literal(String value) {
        Objects.requireNonNull(value, "Value is required.");
        StringLiteral<Object> expression = StringLiteral.of(
                LikeRecord.escape(value));
        return new NotLikeRecord(expression, ESCAPE);
    }

    Character escape();

    TextExpression<?> pattern();
}
