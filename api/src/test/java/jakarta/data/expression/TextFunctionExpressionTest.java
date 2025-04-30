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

import jakarta.data.expression.function.TextFunctionExpression;
import jakarta.data.metamodel.TextAttribute;
import jakarta.data.restrict.Restriction;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class TextFunctionExpressionTest {

    static class Author {
        String name;
    }

    // Mock metamodel
    interface _Author {
        String NAME = "name";

        TextAttribute<Author> name = TextAttribute.of(Author.class, NAME);;
    }

    @Test
    @DisplayName("should create append function from TextAttribute with string")
    void shouldCreateAppendFunctionWithString() {
        var expression = (TextFunctionExpression<?>) _Author.name.append("Suffix");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(expression.name()).isEqualTo(TextFunctionExpression.CONCAT);
            soft.assertThat(expression.arguments()).hasSize(2);
            soft.assertThat(expression.arguments().get(0)).isEqualTo(_Author.name);
        });
    }

    @Test
    @DisplayName("should create prepend function from TextAttribute with string")
    void shouldCreatePrependFunctionWithString() {
        var expression = (TextFunctionExpression<?>) _Author.name.prepend("Prefix");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(expression.name()).isEqualTo(TextFunctionExpression.CONCAT);
            soft.assertThat(expression.arguments()).hasSize(2);
            soft.assertThat(expression.arguments().get(1)).isEqualTo(_Author.name);
        });
    }

    @Test
    @DisplayName("should create upper function from TextAttribute")
    void shouldCreateUpperFunction() {
        var expression = (TextFunctionExpression<?>) _Author.name.upper();

        SoftAssertions.assertSoftly(soft -> soft.assertThat(expression.name()).isEqualTo(TextFunctionExpression.UPPER));
    }

    @Test
    @DisplayName("should create lower function from TextAttribute")
    void shouldCreateLowerFunction() {
        var expression = (TextFunctionExpression<?>) _Author.name.lower();

        SoftAssertions.assertSoftly(soft -> soft.assertThat(expression.name()).isEqualTo(TextFunctionExpression.LOWER));
    }

    @Test
    @DisplayName("should create left function from TextAttribute")
    void shouldCreateLeftFunction() {
        var expression = (TextFunctionExpression<?>) _Author.name.left(5);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(expression.name()).isEqualTo(TextFunctionExpression.LEFT);
            soft.assertThat(expression.arguments()).hasSize(2);
            soft.assertThat(expression.arguments().get(0)).isEqualTo(_Author.name);
        });
    }

    @Test
    @DisplayName("should create right function from TextAttribute")
    void shouldCreateRightFunction() {
        var expression = (TextFunctionExpression<?>) _Author.name.right(3);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(expression.name()).isEqualTo(TextFunctionExpression.RIGHT);
            soft.assertThat(expression.arguments()).hasSize(2);
            soft.assertThat(expression.arguments().get(0)).isEqualTo(_Author.name);
        });
    }

    @Test
    @DisplayName("should create length function from TextAttribute")
    void shouldCreateLengthFunction() {
        NumericExpression<Author, Integer> expression = _Author.name.length();

        SoftAssertions.assertSoftly(soft -> soft.assertThat(expression).isInstanceOf(NumericExpression.class));
    }

    @Test
    @DisplayName("should chain left, right, upper and create a restriction")
    void shouldChainFunctionsAndCreateRestriction() {
        Restriction<Author> restriction = _Author.name.left(10).right(2).upper().equalTo("EE");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(restriction).isNotNull();
        });
    }

    @Test
    @DisplayName("should create prepend function from TextExpression")
    void shouldCreatePrependFunctionWithTextExpression() {
        var expression = (TextFunctionExpression<?>) _Author.name.prepend("Prefix");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(expression.name()).isEqualTo(TextFunctionExpression.CONCAT);
            soft.assertThat(expression.arguments()).hasSize(2);
        });
    }
}