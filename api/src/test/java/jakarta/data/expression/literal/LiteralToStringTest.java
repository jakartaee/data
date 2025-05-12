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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.UUID;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LiteralToStringTest {

    @Test
    @DisplayName("ComparableLiteral.toString must output expected value")
    void testComparableLiteralToString() {
        final String TEST_UUID = "73d518c4-b7f6-4c3b-9f63-60a045a43bb8";
        ComparableLiteral<?, Boolean> booleanLiteral =
                ComparableLiteral.of(true);

        ComparableLiteral<?, Character> charLiteral =
                ComparableLiteral.of('D');

        ComparableLiteral<?, Character> singleQuoteLiteral =
                ComparableLiteral.of('\'');

        ComparableLiteral<?, Month> monthLiteral =
                ComparableLiteral.of(Month.MAY);

        ComparableLiteral<?, UUID> uuidLiteral =
                ComparableLiteral.of(UUID.fromString(TEST_UUID));

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(booleanLiteral.toString())
                    .isEqualTo("TRUE");

            soft.assertThat(charLiteral.toString())
                    .isEqualTo("'D'");

            soft.assertThat(singleQuoteLiteral.toString())
                    .isEqualTo("''''");

            soft.assertThat(monthLiteral.toString())
                    .isEqualTo("java.time.Month.MAY");

            soft.assertThat(uuidLiteral.toString())
                    .isEqualTo("{ComparableLiteral java.util.UUID '" +
                               TEST_UUID + "'}");
        });
    }

    @Test
    @DisplayName("NumericLiteral.toString must output expected value")
    void testNumericLiteralToString() {
        NumericLiteral<?, Integer> intLiteral = NumericLiteral.of(12);

        NumericLiteral<?, Long> longLiteral = NumericLiteral.of(123456789L);

        NumericLiteral<?, Byte> byteLiteral = NumericLiteral.of((byte) 68);

        NumericLiteral<?, BigInteger> bigIntegerLiteral =
                NumericLiteral.of(BigInteger.valueOf(10203040506L));

        NumericLiteral<?, BigDecimal> bigDecimalLiteral =
                NumericLiteral.of(BigDecimal.valueOf(123456789, 2));

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(intLiteral.toString())
                    .isEqualTo("12");

            soft.assertThat(longLiteral.toString())
                    .isEqualTo("123456789L");

            soft.assertThat(byteLiteral.toString())
                    .isEqualTo("{NumericLiteral java.lang.Byte '68'}");

            soft.assertThat(bigIntegerLiteral.toString())
                    .isEqualTo("10203040506BI");

            soft.assertThat(bigDecimalLiteral.toString())
                    .isEqualTo("1234567.89BD");
        });
    }

    @Test
    @DisplayName("Literal.toString must output expected value")
    void testObjectLiteralToString() {
        Literal<?, ZoneId> literal = Literal.of(ZoneId.of("America/Chicago"));

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(literal.toString())
                    .startsWith("{Literal ");
            soft.assertThat(literal.toString())
                    .endsWith(" 'America/Chicago'}");
        });
    }

    @Test
    @DisplayName("Stringiteral.toString must output expected value")
    void testStringLiteralToString() {
        StringLiteral<?> literal = StringLiteral.of("Hello!");
        StringLiteral<?> singleQuoteLiteral =
                StringLiteral.of("Jakarta Data's second release");

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(literal.toString())
                    .isEqualTo("'Hello!'");

            soft.assertThat(singleQuoteLiteral.toString())
                    .isEqualTo("'Jakarta Data''s second release'");
        });
    }

    @Test
    @DisplayName("TemporalLiteral.toString must output expected value")
    void testTemporalLiteralToString() {
        TemporalLiteral<?, Instant> instantLiteral =
                TemporalLiteral.of(Instant.ofEpochMilli(1746825482575L));

        TemporalLiteral<?, LocalDate> dateLiteral =
                TemporalLiteral.of(LocalDate.of(2025, 5, 8));

        TemporalLiteral<?, LocalDateTime> datetimeLiteral =
                TemporalLiteral.of(LocalDateTime.of(2025, 5, 9, 16, 15, 42));

        TemporalLiteral<?, LocalTime> timeLiteral =
                TemporalLiteral.of(LocalTime.of(18, 8, 30));

        SoftAssertions.assertSoftly(soft -> {
            soft.assertThat(instantLiteral.toString())
                    .isEqualTo("{ts '2025-05-09 21:18:02.575'}");

            soft.assertThat(dateLiteral.toString())
                    .isEqualTo("{d '2025-05-08'}");

            soft.assertThat(datetimeLiteral.toString())
                    .isEqualTo("{ts '2025-05-09 16:15:42'}");

            soft.assertThat(timeLiteral.toString())
                    .isEqualTo("{t '18:08:30'}");
        });
    }
}