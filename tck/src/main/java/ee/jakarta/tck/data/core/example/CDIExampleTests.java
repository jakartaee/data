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
package ee.jakarta.tck.data.core.example;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Disabled;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Core;

/**
 * Example test case where we want to run a test within a CDI Bean
 */
@Core
public class CDIExampleTests {
    
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass(ExampleService.class);
    }    
    
    @Inject
    ExampleService service;
    
    @Assertion(id = "EXAMPLE", strategy = "Deployes a CDI bean to application server, and injects service to be tested")
    public void serviceAlwaysReturnsTrue() {
        assertTrue(service.getMessage(), "Message should have returned true");
    }
    
    @Disabled(value = "Challenge #1")
    @Assertion(id = "EXAMPLE", strategy = "Uses the disabled annotation to ensure test does not get executed")
    public void disabledTest() {
        
    }
}