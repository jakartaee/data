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
package ee.jakarta.tck.data.web.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Web;
import jakarta.ejb.EJB;

@Web
public class EJBExampleTests {
    
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(EJBExampleInterface.class, EJBExampleBean.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }    
    
    @EJB
    EJBExampleInterface testBean;
    
    @Test
    @Assertion(id = "EXAMPLE", strategy = "Deploy an EJB to the server, and make sure we can inject and test it.")
    public void serviceAlwaysReturnsTrue() {
        assertNotNull(testBean, "TestBean was not correctly injected.");
        assertEquals(EJBExampleBean.EXPECTED_MESSAGE, testBean.getMessage(), "Got wrong message from testBean.");
    }
}
