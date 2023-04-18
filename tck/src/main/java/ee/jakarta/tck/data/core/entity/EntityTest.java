/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.core.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Core;
import jakarta.data.exceptions.MappingException;
import jakarta.inject.Inject;

/**
 * Execute a test with an entity that is dual annotated which means
 * this test can run against a provider that supports any Entity type.
 */
@Core
@AnyEntity
public class EntityTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class).addClasses(Student.class, StudentDirectory.class);
    }

    //FIXME - This test could be a standalone test, but we would need some sort of provider.
    @Inject
    StudentDirectory directory;

    @Assertion(id = "119", strategy = "Ensures that an entity is created and used based on provider")
    public void ensureEntityPersistedData() {
        List<Student> testData = new ArrayList<>();
        testData.add(new Student(1L, "Jimothy", 4));
        testData.add(new Student(2L, "Karn", 9));
        testData.add(new Student(3L, "Kyle", 32));
        testData.add(new Student(4L, "Brent", 45));
        testData.add(new Student(5L, "Lee", 92));
        
        testData.stream().forEach(student -> directory.save(student));
        
        int countAdults = directory.countByAgeGreaterThanEqual(18);
        assertEquals(3, countAdults, "Got unexpected count of adults");
        
        try {
            int countName = directory.countByNameIgnoreCase("kyle");
            fail("Name was transient should not have been able to count but was, " + countName);
        } catch (Exception e) {
            assertTrue(e instanceof MappingException, "Expected MappingException but got " + e.getClass().getCanonicalName());
        }
    }
}
