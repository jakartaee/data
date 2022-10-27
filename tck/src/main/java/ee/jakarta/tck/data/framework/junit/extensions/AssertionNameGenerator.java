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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayNameGenerator;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Core;
import ee.jakarta.tck.data.framework.junit.anno.Full;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.framework.junit.anno.Web;

/**
 * This custom JUnit name generator will append the {@code @Assertion.id}
 * if one exists to the name of the test at runtime.
 * 
 * We will also append the platform the test ran on by using a combination of
 * annotations and system properties. 
 * 
 * @see ee.jakarta.tck.data.framework.junit.anno.Assertion
 * @see ee.jakarta.tck.data.framework.junit.anno
 */
public class AssertionNameGenerator extends DisplayNameGenerator.Standard implements DisplayNameGenerator {
    
    /**
     * Optional property that can be provided by test runners to append the platform to the method name.
     * This is useful if an test aggregator is being used to run multiple platforms. 
     * Having the platform name appended to the test will help distinguish the multiple runs.
     */
    private static final String platform = System.getProperty("jakarta.tck.platform");
    
    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {  
        //If user provided a platform, then use that
        if(platform != null && !platform.isEmpty()) {
            return testClass.getSimpleName() + "#" + platform.replace("#", "");
        }
        
        //Otherwise, use the annotation on the class
        return testClass.getSimpleName() + getPlatform(testClass);
    }
    
    @Override
    public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
        //If user provided a platform, then use that
        if(platform != null && !platform.isEmpty()) {
            return nestedClass.getSimpleName() + "#" + platform.replace("#", "");
        }
        
        //Otherwise, use the annotation on the class
        return nestedClass.getSimpleName() + getPlatform(nestedClass);
    }
    
    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        String standardDisplayName = super.generateDisplayNameForMethod(testClass, testMethod);
        Assertion instance = testMethod.getAnnotation(Assertion.class);
        if(instance != null) {
            return testMethod.getName() + "@Assertion.id:" + instance.id();
        }
        return standardDisplayName;
    }
    
    private String getPlatform(Class<?> testClass) {
        //If the test is annotated with Standalone, but we are going to deploy the test on a server.
        if (testClass.isAnnotationPresent(Standalone.class) && !Boolean.getBoolean(StandaloneExtension.isStandaloneProperty)) {
            return "#DeployedStandalone";
        }
        
        //Otherwise, use the annoation from the class
        for(Class<? extends Annotation> annotation : Arrays.asList(Standalone.class, Core.class, Web.class, Full.class)) {
            if(testClass.isAnnotationPresent(annotation)) {
                return "#" + annotation.getSimpleName();
            }
        }
        
        return ""; //Otherwise, ignore the platform
    }
}
