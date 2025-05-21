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
package jakarta.data.expression.literal;

import jakarta.data.expression.TextExpression;

/**
 * <p>A {@linkplain Literal literal} for a {@link String} value.</p>
 *
 * @param <T> entity type.
 * @since 1.1
 */
public interface StringLiteral<T>
        extends ComparableLiteral<T, String>, TextExpression<T> {

    /**
     * <p>Creates a {@code StringLiteral} that represents the given value.</p>
     *
     * @param <T>   entity type.
     * @param value a {@code String} value. Must never be {@code null}.
     * @return a {@code StringLiteral} representing the value.
     * @throws NullPointerException if the value is {@code null}.
     */
    static <T> StringLiteral<T> of(String value) {
        return new StringLiteralRecord<>(value);
    }

    /**
     * <p>Returns a {@code String} representing the value. The {@code String}
     * is the value enclosed in single quotes and with each single quote
     * character in the value escaped with a single quote.</p>
     *
     * <p>For example, the output of
     * {@code StringLiteral.of("Jakarta Data's second release").toString()} is
     * </p>
     * <pre>
     * 'Jakarta Data''s second release'
     * </pre>
     *
     * @return the {@code String} value escaped and enclosed in single quotes.
     */
    @Override
    String toString();
}
