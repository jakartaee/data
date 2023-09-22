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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

//@DisplayNameGeneration(ReplaceCamelCaseAndUnderscoreAndNumber.class)

/**
 * This class is used to simulate the usage of the custom generator {@linkplain ReplaceCamelCaseAndUnderscoreAndNumber }.
 */
class ClassAnnotatedWithReplaceCamelCaseAndUnderscoreAndNumberTest {

    @Test
    void shouldReturnErrorWhen_maxResults_IsNegative() {
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void shouldCreateLimitWithRange() {
        doNothing();
    }

    @Test
    void shouldReturn5Errors() {
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 23})
    void shouldReturn5errors() {
        doNothing();
    }

    @Test
    void shouldReturn23Errors() {
    }

    @Test
    void shouldReturnTheValueOf_maxResults() {
        doNothing();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void shouldReturnTheNumberOfErrorsAs_numberOfErrors_InferiorOrEqualTo5() {
    }

    @Test
    void shouldReturnTheNumberOfErrorsAs_numberOfErrors_InferiorOrEqualTo15() {
    }

    private void doNothing() {
    }
}
