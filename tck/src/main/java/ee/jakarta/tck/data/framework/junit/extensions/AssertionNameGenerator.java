/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package ee.jakarta.tck.data.framework.junit.extensions;

import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayNameGenerator;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;

/**
 * This custom JUnit name generator will append the {@code @Assertion.id}
 * if one exists to the name of the test at runtime.
 * 
 * @see ee.jakarta.tck.data.framework.junit.anno.Assertion
 */
public class AssertionNameGenerator extends DisplayNameGenerator.Standard implements DisplayNameGenerator {
    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        String standardDisplayName = super.generateDisplayNameForMethod(testClass, testMethod);
        Assertion instance = testMethod.getAnnotation(Assertion.class);
        if(instance != null) {
            return standardDisplayName + "@Assertion.id:" + instance.id();
        }
        return standardDisplayName;
    }
}
