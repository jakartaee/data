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
package ee.jakarta.tck.data.framework.arquillian.extensions;

import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.extensions.AssertionExtension;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import ee.jakarta.tck.data.framework.utilities.TestPropertyHandler;

/**
 * This extension will intercept all archives before they are deployed to the container and append 
 * a library with the following:
 * 
 * <p>ee.jakarta.tck.data.framework.junit.anno package</p>
 * <p>eee.jakarta.tck.data.framework.junit.extensions package</p>
 * <p>ee.jakarta.tck.data.framework.utilities package</p>
 * <p>A resource file with system properties from the client, that are needed on the container.</p>
 *
 */
public class TCKFrameworkAppender implements AuxiliaryArchiveAppender {
    
    private static final Package annoPackage = Assertion.class.getPackage();
    private static final Package extensionPackage = AssertionExtension.class.getPackage();
    private static final Package utilPackage = TestProperty.class.getPackage();

    @Override
    public Archive<?> createAuxiliaryArchive() {
        JavaArchive framework = ShrinkWrap.create(JavaArchive.class, "jakarta-data-framework.jar");
        framework.addPackages(false, annoPackage, extensionPackage, utilPackage);
        return TestPropertyHandler.storeProperties(framework);
    }

}
