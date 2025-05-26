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

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.data.expression.ComparableExpression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Stream;

class ComparableLiteralTest {

    static class Dummy {}

    static Stream<Object> supportedLiterals() {
        return Stream.of(
                "Jakarta",
                42,
                42L,
                3.14f,
                2.718,
                (short) 7,
                (byte) 1,
                new BigDecimal("123.45"),
                new BigInteger("987654321"),
                LocalDate.of(2024, 4, 23),
                LocalTime.of(14, 30)
                // LocalDateTime and Instant are excluded because their
                // literals match the query language syntax rather than
                // following the LocalDateTime.toString and Instant.toString
                // output exactly.
        );
    }

    @DisplayName("should return ComparableLiteral from primitive numeric types")
    @ParameterizedTest(name = "value = {0}")
    @ValueSource(ints = {1, 42, 999})
    void shouldReturnNumericLiteralFromInt(int value) {
        var literal = ComparableLiteral.of(value);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(literal).isInstanceOf(ComparableLiteral.class);
            soft.assertThat(literal.value()).isEqualTo(value);
        });
    }

    @DisplayName("should support BigDecimal, BigInteger, and temporal literals")
    @org.junit.jupiter.api.Test
    void shouldSupportBigAndTemporalLiterals() {
        var now = Instant.now();
        var date = LocalDate.now();
        var time = LocalTime.now();
        var bigDecimal = new BigDecimal("123.45");
        var bigInteger = new BigInteger("999999");

        var inst = ComparableLiteral.of(now);
        var localDate = ComparableLiteral.of(date);
        var localTime = ComparableLiteral.of(time);
        var dec = ComparableLiteral.of(bigDecimal);
        var bigint = ComparableLiteral.of(bigInteger);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(inst.value()).isEqualTo(now);
            soft.assertThat(localDate.value()).isEqualTo(date);
            soft.assertThat(localTime.value()).isEqualTo(time);
            soft.assertThat(dec.value()).isEqualTo(bigDecimal);
            soft.assertThat(bigint.value()).isEqualTo(bigInteger);
        });
    }

    @DisplayName("should support StringLiteral via of")
    @org.junit.jupiter.api.Test
    void shouldReturnStringLiteral() {
        var literal = ComparableLiteral.of("Jakarta");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(literal).isInstanceOf(ComparableLiteral.class);
            soft.assertThat(literal.value()).isEqualTo("Jakarta");
        });
    }

    @DisplayName("should return ComparableLiteral from long value")
    @ParameterizedTest
    @ValueSource(longs = {42L, 99L})
    void shouldReturnNumericLiteralFromLong(long value) {
        var literal = ComparableLiteral.of(value);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(literal).isInstanceOf(ComparableLiteral.class);
            soft.assertThat(literal.value()).isEqualTo(value);
        });
    }

    @DisplayName("should return ComparableLiteral from float value")
    @ParameterizedTest
    @ValueSource(floats = {3.14f, 2.71f})
    void shouldReturnNumericLiteralFromFloat(float value) {
        var literal = ComparableLiteral.of(value);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(literal).isInstanceOf(ComparableLiteral.class);
            soft.assertThat(literal.value()).isEqualTo(value);
        });
    }

    @DisplayName("should return ComparableLiteral from double value")
    @ParameterizedTest
    @ValueSource(doubles = {1.618, 2.718})
    void shouldReturnNumericLiteralFromDouble(double value) {
        var literal = ComparableLiteral.of(value);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(literal).isInstanceOf(ComparableLiteral.class);
            soft.assertThat(literal.value()).isEqualTo(value);
        });
    }

    @DisplayName("should create a ComparableLiteral for all supported types")
    @ParameterizedTest(name = "[{index}] value = {0}")
    @MethodSource("supportedLiterals")
    void shouldCreateLiteralsFromAllSupportedTypes(Object input) {
        @SuppressWarnings("unchecked")
        var literal = ComparableLiteral.of((Comparable<Object>) input);

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(literal).isInstanceOf(ComparableLiteral.class);
            soft.assertThat(literal.value()).isEqualTo(input);
            soft.assertThat(literal.toString()).contains(input.toString());
        });
    }

    @DisplayName("should support StringLiteral explicitly")
    @Test
    void shouldSupportStringLiteral() {
        var literal = ComparableLiteral.of("Jakarta");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(literal.value()).isEqualTo("Jakarta");
            soft.assertThat(literal).isInstanceOf(ComparableExpression.class);
        });
    }
}