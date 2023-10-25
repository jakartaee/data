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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

//TODO: Solve the module issue related to the usage of @DisplayNameGeneration(ReplaceCamelCaseAndUnderscoreAndNumber.class)
// module jakarta.data does not "exports jakarta.data.displaynamegeneration" to unnamed module
// and TestEngine with ID 'junit-jupiter' failed to discover tests

/**
 * This class implicitly tests {@linkplain ReplaceCamelCaseAndUnderscoreAndNumber } by checking if the generated display name matches the expected one.
 * */
@Disabled("Disabled until module issue has been fixed!")
//This next line is commented in order to avoid "TestEngine with ID 'junit-jupiter' failed to discover tests"
//@DisplayNameGeneration(ReplaceCamelCaseAndUnderscoreAndNumber.class)
class ClassAnnotatedWithReplaceCamelCaseAndUnderscoreAndNumberTest {

    private TestInfo testInfo;

    @BeforeEach
    void init(TestInfo testInfo) {
        this.testInfo = testInfo;
    }

    @Test
    void shouldReturnErrorWhen_maxResults_IsNegative() {
        assertSoftly(softly -> {
            softly.assertThat(testInfo.getDisplayName()).isEqualTo("Should return error when maxResults is negative");
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void shouldCreateLimitWithRange(String input) {
        methodNotAnnotatedWithTestOrParameterizedTest();
        assertSoftly(softly -> {
            softly.assertThat(testInfo.getDisplayName()).isEqualTo("Should create limit with range (String)");
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 23})
    void shouldReturn5Errors(int input) {
        assertSoftly(softly -> {
            softly.assertThat(testInfo.getDisplayName()).isEqualTo("Should return 5 errors (int)");
        });
    }

    @Test
    void shouldReturn5errors() {

        methodNotAnnotatedWithTestOrParameterizedTest();
        assertSoftly(softly -> {
            softly.assertThat(testInfo.getDisplayName()).isEqualTo("Should return 5errors");
        });
    }

    @Test
    void shouldReturn23Errors() {
        methodNotAnnotatedWithTestOrParameterizedTest();
        assertSoftly(softly -> {
            softly.assertThat(testInfo.getDisplayName()).isEqualTo("Should return 23 errors");
        });
    }

    @Test
    void shouldReturnTheValueOf_maxResults() {
        methodNotAnnotatedWithTestOrParameterizedTest();
        assertSoftly(softly -> {
            softly.assertThat(testInfo.getDisplayName()).isEqualTo("Should return the value of maxResults");
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void shouldReturnTheNumberOfErrorsAs_numberOfErrors_InferiorOrEqualTo5(String input) {
        assertSoftly(softly -> {
            softly.assertThat(testInfo.getDisplayName()).isEqualTo("Should return the number of errors as numberOfErrors inferior or equal to 5 (String)");
        });
    }

    @Test
    void shouldReturnTheNumberOfErrorsAs_numberOfErrors_InferiorOrEqualTo15() {
        assertSoftly(softly -> {
            softly.assertThat(testInfo.getDisplayName()).isEqualTo("Should return the number of errors as numberOfErrors inferior or equal to 15");
        });
    }

    private void methodNotAnnotatedWithTestOrParameterizedTest() {}
}