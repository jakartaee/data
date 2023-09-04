/*
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
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
package jakarta.data.displaynamegeneration.test;

import jakarta.data.displaynamegeneration.ReplaceCamelCaseAndUnderscoreAndNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayNameGeneration(ReplaceCamelCaseAndUnderscoreAndNumber.class)
class ReplaceCamelCaseAndUnderscoreAndNumberTest {
    private ReplaceCamelCaseAndUnderscoreAndNumber replaceCamelCaseAndUnderscoreAndNumber;

    @BeforeEach
    void setUp() {
        replaceCamelCaseAndUnderscoreAndNumber = new ReplaceCamelCaseAndUnderscoreAndNumber();
    }

    private static Stream<Arguments> provideInputAndExpectedResult() {
        return Stream.of(
                Arguments.of("shouldReturnErrorWhen_maxResults_IsNegative", "Should return error when maxResults is negative"),
                Arguments.of("shouldCreateLimitWithRange", "Should create limit with range"),
                Arguments.of("shouldReturn5Errors", "Should return 5 errors"),
                Arguments.of("shouldReturn5errors", "Should return 5errors"),
                Arguments.of("shouldReturn23Errors", "Should return 23 errors"),
                Arguments.of("shouldReturnTheValueOf_maxResults", "Should return the value of maxResults"),
                Arguments.of("shouldReturnTheNumberOfErrorsAs_numberOfErrors_InferiorOrEqualTo5", "Should return the number of errors as numberOfErrors inferior or equal to 5"),
                Arguments.of("shouldReturnTheNumberOfErrorsAs_numberOfErrors_InferiorOrEqualTo15", "Should return the number of errors as numberOfErrors inferior or equal to 15")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInputAndExpectedResult")
    void shouldReturnMethodDisplayNamesForCamelCaseAndUnderscoreAndNumber(final String input, final String expectedResult) {
        assertSoftly(softly -> {
            softly.assertThat(replaceCamelCaseAndUnderscoreAndNumber.replaceCamelCaseAndUnderscoreAndNumber(input)).isEqualTo(expectedResult);
        });
    }
}