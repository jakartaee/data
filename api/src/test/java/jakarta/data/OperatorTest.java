/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package jakarta.data;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OperatorTest {


    @ParameterizedTest(name = "Operator {0} should negate to {1}")
    @MethodSource("provideOperatorsAndNegations")
    void shouldNegateOperatorCorrectly(Operator operator, Operator expectedNegation) {
        var negated = operator.negate();

        assertThat(negated)
                .as("Negation of %s should be %s", operator, expectedNegation)
                .isEqualTo(expectedNegation);
    }

    private static Stream<Arguments> provideOperatorsAndNegations() {
        return Stream.of(
                Arguments.of(Operator.EQUAL, Operator.NOT_EQUAL),
                Arguments.of(Operator.NOT_EQUAL, Operator.EQUAL),
                Arguments.of(Operator.GREATER_THAN, Operator.LESS_THAN_EQUAL),
                Arguments.of(Operator.LESS_THAN, Operator.GREATER_THAN_EQUAL),
                Arguments.of(Operator.GREATER_THAN_EQUAL, Operator.LESS_THAN),
                Arguments.of(Operator.LESS_THAN_EQUAL, Operator.GREATER_THAN),
                Arguments.of(Operator.IN, Operator.NOT_IN),
                Arguments.of(Operator.NOT_IN, Operator.IN),
                Arguments.of(Operator.LIKE, Operator.NOT_LIKE),
                Arguments.of(Operator.NOT_LIKE, Operator.LIKE)
        );
    }
}