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
package jakarta.data.displaynamegeneration;

import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;

public class ReplaceCamelCaseAndUnderscore extends DisplayNameGenerator.Standard {

    public ReplaceCamelCaseAndUnderscore() {
        super();
    }

    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        return replaceCamelCaseAndUnderscore(testMethod.getName()) + DisplayNameGenerator.parameterTypesAsString(testMethod);
    }

    public String replaceCamelCaseAndUnderscore(String input) {
        StringBuilder result = new StringBuilder();
        /*
         * Each method name starts with "should" then the displayed name starts with "Should"
         * */
        result.append(Character.toUpperCase(input.charAt(0)));

        /*
         * There are 2 groups of method name: with and without underscore
         * */
        if (input.contains("_")) {
            boolean insideUnderscores = false;
            for (int i = 1; i < input.length(); i++) {
                char currentChar = input.charAt(i);
                if (currentChar == '_') {
                    result.append(' ');
                    /*
                     * If the current char is an underscore and insideUnderscores is true,
                     * it means there is an opening underscore and this one is the closing one
                     * then we set insideUnderscores to false.
                     * */
                    /*
                     * If the current char is an underscore and insideUnderscores is false,
                     * it means there is not an opening underscore and this one is the opening one
                     * then we set insideUnderscores to true.
                     * */
                    insideUnderscores = !insideUnderscores;
                } else {
                    /*
                     * If the character is inside underscores, we append the character as it is.
                     * */
                    if (insideUnderscores) {
                        result.append(currentChar);
                    } else {
                        //CamelCase handling for method name containing "_"
                        if (Character.isUpperCase(currentChar)) {
                            //We already replace "_" with " ". If the previous character is "_", we will not add extra space
                            if (!(input.charAt(i - 1) == '_')) {
                                result.append(' ');
                            }
                            result.append(Character.toLowerCase(currentChar));
                        } else {
                            result.append(currentChar);
                        }
                    }
                }
            }
        } else {
            //CamelCase handling for method name not containing "_"
            for (int i = 1; i < input.length(); i++) {
                if (Character.isUpperCase(input.charAt(i))) {
                    result.append(' ');
                    result.append(Character.toLowerCase(input.charAt(i)));
                } else {
                    result.append(input.charAt(i));
                }
            }
        }
        return result.toString().replaceAll("(\\d+)", " $1");
    }
}