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
package jakarta.data.expression;

/**
 * <p>An {@linkplain Expression expression} that represents a literal value.
 * Applications do not interact with literal expressions directly and instead
 * supply literal values to various methods of expressions. For example,</p>
 *
 * <pre>
 * _Car.year.greaterThanEqual(_Car.firstModelYear.plus(2))
 * </pre>
 *
 * @param <T> entity type.
 * @param <V> entity attribute type.
 * @since 1.1
 */
public interface LiteralExpression<T, V> extends Expression<T, V> {

    /**
     * <p>Returns a {@code String} representing the literal value. In some
     * cases, the output of this method resembles a query language
     * equivalent of the literal expression.</p>
     *
     * @return a {@code String} representing the literal value.
     */
    @Override
    public String toString();

    /**
     * <p>Returns the value represented by this {@code LiteralExpression}.
     * The value will never be {@code null}.</p>
     *
     * @return the value.
     */
    V value();
}
