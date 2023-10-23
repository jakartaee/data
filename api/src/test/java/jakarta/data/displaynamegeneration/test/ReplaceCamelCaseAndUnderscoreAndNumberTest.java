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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

//TODO: Solve the module issue related to the usage of @DisplayNameGeneration(ReplaceCamelCaseAndUnderscoreAndNumber.class)
// and test the display names inside ClassAnnotatedWithReplaceCamelCaseAndUnderscoreAndNumberTest
// without explicitly calling the method generateDisplayNameForMethod
// by finding a way to programmatically get these names from JUnit then compare to expected results.
class ReplaceCamelCaseAndUnderscoreAndNumberTest {

    private static Stream<Arguments> provideMethodNameAndExpectedDisplayName() {
        return Stream.of(
                Arguments.of("shouldReturnErrorWhen_maxResults_IsNegative", "Should return error when maxResults is negative"),
                Arguments.of("shouldCreateLimitWithRange", "Should create limit with range (String)"),
                Arguments.of("shouldReturn5Errors", "Should return 5 errors (int)"),
                Arguments.of("shouldReturn5errors", "Should return 5errors"),
                Arguments.of("shouldReturn23Errors", "Should return 23 errors"),
                Arguments.of("shouldReturnTheValueOf_maxResults", "Should return the value of maxResults"),
                Arguments.of("shouldReturnTheNumberOfErrorsAs_numberOfErrors_InferiorOrEqualTo5", "Should return the number of errors as numberOfErrors inferior or equal to 5 (String)"),
                Arguments.of("shouldReturnTheNumberOfErrorsAs_numberOfErrors_InferiorOrEqualTo15", "Should return the number of errors as numberOfErrors inferior or equal to 15"),
                Arguments.of("methodNotAnnotatedWithTestOrParameterizedTest", null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideMethodNameAndExpectedDisplayName")
    void shouldReturnMethodDisplayNamesForCamelCaseAndUnderscoreAndNumber(final String methodName, final String expectedDisplayName) {
        List<String> methodsWithParameters = List.of("shouldCreateLimitWithRange", "shouldReturn5Errors", "shouldReturnTheNumberOfErrorsAs_numberOfErrors_InferiorOrEqualTo5");

        try {
            Method method = null;
            if (!methodsWithParameters.contains(methodName)) {
                method = ClassAnnotatedWithReplaceCamelCaseAndUnderscoreAndNumberTest.class.getDeclaredMethod(methodName);
            }
            if (List.of("shouldCreateLimitWithRange", "shouldReturnTheNumberOfErrorsAs_numberOfErrors_InferiorOrEqualTo5").contains(methodName)) {
                method = ClassAnnotatedWithReplaceCamelCaseAndUnderscoreAndNumberTest.class.getDeclaredMethod(methodName, String.class);
            }
            if ("shouldReturn5Errors".equals(methodName)) {
                method = ClassAnnotatedWithReplaceCamelCaseAndUnderscoreAndNumberTest.class.getDeclaredMethod(methodName, int.class);
            }
            assert method != null;

            if (method.isAnnotationPresent(Test.class) || method.isAnnotationPresent(ParameterizedTest.class)) {
                Method finalMethod = method;
                assertSoftly(softly -> {
                    softly.assertThat(ReplaceCamelCaseAndUnderscoreAndNumber.INSTANCE.generateDisplayNameForMethod(ClassAnnotatedWithReplaceCamelCaseAndUnderscoreAndNumberTest.class, finalMethod)).isEqualTo(expectedDisplayName);
                });
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}