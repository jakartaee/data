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

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.data.expression.NumericExpression;
import jakarta.data.metamodel.NumericAttribute;

/**
 * <p>A {@linkplain Literal literal} for a
 * {@linkplain NumericAttribute numeric} value.</p>
 *
 * @param <T> entity type.
 * @param <N> entity attribute type.
 * @since 1.1
 */
public interface NumericLiteral<T, N extends Number & Comparable<N>>
        extends ComparableLiteral<T, N>, NumericExpression<T, N> {

    /**
     * <p>Creates a {@code NumericLiteral} that represents the given value.</p>
     *
     * @param <T>   entity type.
     * @param <N>   entity attribute type.
     * @param value an immutable numeric value. Must never be {@code null}.
     * @return a {@code NumericLiteral} representing the value.
     * @throws NullPointerException if the value is {@code null}.
     */
    static <T, N extends Number & Comparable<N>> NumericLiteral<T, N> of(N value) {
        return new NumericLiteralRecord<>(value);
    }

    // TODO Is it a problem that toString for a NumericLiteral with an Integer
    // value is indistinguishable from toString of an Integer/int value?
    // I have already seen this in a test where it reported a failed assertion
    // that appeared to be saying that a number was not equal to the same
    // number. Actually it was erroneously comparing a NumericLiteral to an
    // Integer value, but there was no indication of this given that both
    // convert to the same String. We could switch the output to include a
    // suffix, such as 'I' at the end, but the downside of that would be not
    // matching JDQL.
    /**
     * <p>Returns a {@code String} representing the literal numeric value.</p>
     *
     * <p>For the following types, the {@code String} consists of the numeric
     * value, followed by</p>
     * <ul>
     * <li>{@code L} for {@link Long},</li>
     * <li>{@code F} for {@link Float},</li>
     * <li>{@code D} for {@link Double},</li>
     * <li>{@code BI} for {@link BigInteger}, or</li>
     * <li>{@code BD} for {@link BigDecimal}.</li>
     * </ul>
     *
     * <p>For values of type {@link Integer}, the {@code String} consists of
     * the numeric value without any suffix.</p>
     *
     * <p>For all other types, the {@code String} begins with an opening curly
     * brace and ends with a closing curly brace. Between the braces are 3
     * terms delimited by a space character. The first term is
     * {@code NumericLiteral}. The second term is the fully qualified class
     * name of the value's type. The third term is the {@code toString()}
     * output of the value, enclosed in single quotes.</p>
     *
     * <p>For example, the output of
     * {@code NumericLiteral.of(BigDecimal.valueOf(123456789, 2)).toString()} is</p>
     * <pre>
     * 1234567.89BD
     * </pre>
     *
     * @return a {@code String} representing the literal numeric value.
     */
    @Override
    String toString();
}
