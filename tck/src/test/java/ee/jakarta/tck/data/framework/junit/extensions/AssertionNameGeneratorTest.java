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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGenerator.Simple;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Core;
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
public class AssertionNameGeneratorTest {
    
    @Web
    public static class TestClass {
        
        @Assertion(id = "1.0")
        public void testMethod() {
            //Does nothing
        }
        
        public void testSimpleMethod() {
            //Does nothing
        }
        
        public static class NestedSimpleTestClass {
            public void testSimpleMethodInNestedClass() {
                //Does nothing
            }
        }
        
        @Core
        public static class NestedCoreTestClass {
            
            @Assertion(id = "2.0")
            public void testMethodInNestedClass() {
                //Does nothing
            }
        }
        
        @Standalone
        public static class NestedStandaloneTestClass {
            
            @Assertion(id = "3.0")
            public void TestMethodInNestedClass() {
                //Does nothing
            }
        }
    }
    
    @ParameterizedTest
    @ValueSource(strings =  { "standalone", "core", "web", "full", "something" })
    public void testGenerateDisplayNameForClassWithPlatform(String platform) {
        for(boolean isStandalone : Set.of(true, false)) {
            AssertionNameGenerator generator = createNewAssertionNameGenerator(platform, isStandalone);

            String expectedClassName = TestClass.class.getSimpleName() + "#" + platform;
            String actualClassName = generator.generateDisplayNameForClass(TestClass.class);
            assertEquals(expectedClassName, actualClassName);
        }
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    public void testGenerateDisplayNameForClassWithoutPlatform(String platform) {
        Simple simpleGenerator = new DisplayNameGenerator.Simple();
        AssertionNameGenerator generator;
        String expectedClassName, actualClassName;
        
        //On Server
        generator = createNewAssertionNameGenerator(platform, false);

        expectedClassName = simpleGenerator.generateDisplayNameForClass(TestClass.class);
        actualClassName = generator.generateDisplayNameForClass(TestClass.class);
        assertEquals(expectedClassName, actualClassName);
        
        expectedClassName = TestClass.NestedStandaloneTestClass.class.getSimpleName() + "#Deployed";
        actualClassName = generator.generateDisplayNameForClass(TestClass.NestedStandaloneTestClass.class);
        assertEquals(expectedClassName, actualClassName);
        
        //Standalone
        generator = createNewAssertionNameGenerator(platform, true);

        expectedClassName = simpleGenerator.generateDisplayNameForClass(TestClass.class);
        actualClassName = generator.generateDisplayNameForClass(TestClass.class);
        assertEquals(expectedClassName, actualClassName);
        
        expectedClassName = simpleGenerator.generateDisplayNameForClass(TestClass.NestedStandaloneTestClass.class);
        actualClassName = generator.generateDisplayNameForClass(TestClass.NestedStandaloneTestClass.class);
        assertEquals(expectedClassName, actualClassName);
    }
    
    @ParameterizedTest
    @ValueSource(strings =  { "standalone", "core", "web", "full", "something" })
    public void testGenerateDisplayNameForNestedClassWithPlatform(String platform) {
        for(boolean isStandalone : Set.of(true, false)) {
            AssertionNameGenerator generator = createNewAssertionNameGenerator(platform, isStandalone);

            String expectedClassName = TestClass.NestedCoreTestClass.class.getSimpleName() + "#" + platform;
            String actualClassName = generator.generateDisplayNameForNestedClass(TestClass.NestedCoreTestClass.class);
            assertEquals(expectedClassName, actualClassName);
        }
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    public void testGenerateDisplayNameForNestedClassWithoutPlatform(String platform) {
        Simple simpleGenerator = new DisplayNameGenerator.Simple();
        AssertionNameGenerator generator;
        String expectedClassName, actualClassName;
        
        //On Server
        generator = createNewAssertionNameGenerator(platform, false);

        expectedClassName = simpleGenerator.generateDisplayNameForNestedClass(TestClass.NestedCoreTestClass.class);
        actualClassName = generator.generateDisplayNameForNestedClass(TestClass.NestedCoreTestClass.class);
        assertEquals(expectedClassName, actualClassName);
        
        expectedClassName = TestClass.NestedStandaloneTestClass.class.getSimpleName() + "#Deployed";
        actualClassName = generator.generateDisplayNameForNestedClass(TestClass.NestedStandaloneTestClass.class);
        assertEquals(expectedClassName, actualClassName);
        
        //Standalone
        generator = createNewAssertionNameGenerator(platform, true);

        expectedClassName = simpleGenerator.generateDisplayNameForNestedClass(TestClass.NestedCoreTestClass.class);
        actualClassName = generator.generateDisplayNameForNestedClass(TestClass.NestedCoreTestClass.class);
        assertEquals(expectedClassName, actualClassName);
        
        expectedClassName = simpleGenerator.generateDisplayNameForNestedClass(TestClass.NestedStandaloneTestClass.class);
        actualClassName = generator.generateDisplayNameForNestedClass(TestClass.NestedStandaloneTestClass.class);
        assertEquals(expectedClassName, actualClassName);
    }
    
    @ParameterizedTest
    @ValueSource(strings =  { "standalone", "core", "web", "full", "something" })
    public void testGenerateDisplayNameForMethod(String platform) throws NoSuchMethodException, SecurityException {
        for(boolean isStandalone : Set.of(true, false)) {
            AssertionNameGenerator generator = createNewAssertionNameGenerator(platform, isStandalone);
            
            String expectedMethodName = "testMethod@Assertion.id:1.0";
            String actualMethodName = generator.generateDisplayNameForMethod(TestClass.class, TestClass.class.getMethod("testMethod"));
            assertEquals(expectedMethodName, actualMethodName);
            
            String expectedNestedMethodName = "testMethodInNestedClass@Assertion.id:2.0";
            String actualNestedMethodName = generator.generateDisplayNameForMethod(TestClass.NestedCoreTestClass.class, TestClass.NestedCoreTestClass.class.getMethod("testMethodInNestedClass"));
            assertEquals(expectedNestedMethodName, actualNestedMethodName);   
        }
    }
    
    private AssertionNameGenerator createNewAssertionNameGenerator(String platform, boolean isStandalone) {
        if (platform == null) {
            System.clearProperty(AssertionNameGenerator.platformProperty);
        } else {
            System.setProperty(AssertionNameGenerator.platformProperty, platform);    
        }
        
        System.setProperty(StandaloneExtension.isStandaloneProperty, Boolean.toString(isStandalone));
        return new AssertionNameGenerator();
    }
}
