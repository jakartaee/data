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
package jakarta.data.spi.expression.function;

import jakarta.data.mock.entity.Book;
import jakarta.data.mock.entity._Book;
import jakarta.data.spi.expression.function.TextFunctionExpression;
import jakarta.data.spi.expression.literal.NumericLiteral;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FunctionTest {

    @Test
    @DisplayName("""
            Supplying a size argument that is negative to the LEFT function
            must case IllegalArgumentException to be raised.
            """)
    void testDivisionByZero() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> _Book.numPages.divide(0));
    }

    @Test
    @DisplayName("""
            Supplying a size argument that is negative to the LEFT function
            must case IllegalArgumentException to be raised.
            """)
    void testLeftNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> _Book.title.left(-2));
    }

    @Test
    @DisplayName("""
            Supplying a size argument of 0 to the LEFT function must be
            permitted.
            """)
    void testLeftZero() {
        TextFunctionExpression<Book> expr =
                (TextFunctionExpression<Book>) _Book.title.left(0);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(expr.name())
                .isEqualTo(TextFunctionExpression.LEFT);

            soft.assertThat(expr.arguments().get(0))
                .isEqualTo(_Book.title);

            soft.assertThat(expr.arguments().get(1))
                .isInstanceOf(NumericLiteral.class);

            soft.assertThat(((NumericLiteral<?>) expr.arguments().get(1))
                    .value())
                .isEqualTo(0);
        });
    }

    @Test
    @DisplayName("""
            Supplying a size argument that is negative to the RIGHT function
            must case IllegalArgumentException to be raised.
            """)
    void testRightNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> _Book.title.right(-10));
    }

    @Test
    @DisplayName("""
            Supplying a size argument of 0 to the RIGHT function must be
            permitted.
            """)
    void testRightZero() {
        TextFunctionExpression<Book> expr =
                (TextFunctionExpression<Book>) _Book.title.right(0);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(expr.name())
                .isEqualTo(TextFunctionExpression.RIGHT);

            soft.assertThat(expr.arguments().get(0))
                .isEqualTo(_Book.title);

            soft.assertThat(expr.arguments().get(1))
                .isInstanceOf(NumericLiteral.class);

            soft.assertThat(((NumericLiteral<?>) expr.arguments().get(1))
                    .value())
                .isEqualTo(0);
        });
    }
}