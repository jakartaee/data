/*
 * Copyright (c) 2023, 2024 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.core.cdi;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.CDIRequired;
import ee.jakarta.tck.data.framework.junit.anno.Core;
import jakarta.enterprise.inject.spi.CDI;

@Core
@AnyEntity
@CDIRequired
public class CDITests {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackage(CDITests.class.getPackage());
    }

    @Assertion(id = "640", strategy = "Verifies that another Jakarta Data Provider does not attempt to "
            + "implement the Dictonary repository based on provider attribute.")
    public void testDataRepositoryHonorsProviderAttribute() {
        assertTrue(CDI.current().select(Directory.class).isUnsatisfied());
    }

    @Assertion(id = "640", strategy = "Verifies that another Jakarta Data Provider does not attempt to "
            + "implement the Address repository based on the EntityDefining annotation.")
    public void testDataRepositoryHonorsEntityDefiningAnnotation() {
        assertTrue(CDI.current().select(AddressBook.class).isUnsatisfied());
    }
}
