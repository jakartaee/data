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
package ee.jakarta.tck.data.framework.arquillian.extensions;

import java.util.Arrays;
import java.util.logging.Logger;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.container.ClassContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Full;
import ee.jakarta.tck.data.framework.junit.anno.Signature;
import ee.jakarta.tck.data.framework.junit.anno.Web;
import ee.jakarta.tck.data.framework.junit.extensions.AssertionExtension;
import ee.jakarta.tck.data.framework.servlet.TestServlet;
import ee.jakarta.tck.data.framework.signature.DataSignatureTestRunner;

/**
 * The ee.jakarta.tck.data.framework.junit.anno and ee.jakarta.tck.data.framework.junit.extensions
 * packages need to be available for all application that are deployed to the server.
 * 
 * The web/full profile tests require the ee.jakarta.tck.data.framework.servlet package on the server.
 * 
 * The signature tests require the ee.jakarta.tck.data.framework.signature package on the server.
 * 
 * This processor service will automatically include these packages in the
 * archives based on their requirements before they are deployed to the server.
 *
 */
public class TCKArchiveProcessor implements ApplicationArchiveProcessor {
    private static final Logger log = Logger.getLogger(TCKArchiveProcessor.class.getCanonicalName());

    private static final Package annoPackage = Assertion.class.getPackage();
    private static final Package extensionPackage = AssertionExtension.class.getPackage();
    private static final Package servletPackage = TestServlet.class.getPackage();
    private static final Package signaturePackage = DataSignatureTestRunner.class.getPackage();

    @Override
    public void process(Archive<?> applicationArchive, TestClass testClass) {
        String applicationName = applicationArchive.getName() == null ? applicationArchive.getId() : applicationArchive.getName();
        
        // NOTE: ClassContainer is a superclass of ResourceContainer
        if (applicationArchive instanceof ClassContainer) { 
            
            // Add annotation and extension packages to all archives
            log.info("Application Archive [" + applicationName + "] is being appended with packages [" + annoPackage + " ," + extensionPackage + "]");
            ((ClassContainer<?>) applicationArchive).addPackages(false, annoPackage, extensionPackage);

            // Add servlet packages to web/full profile tests
            if(testClass.isAnnotationPresent(Web.class) || testClass.isAnnotationPresent(Full.class)) {
                log.info("Application Archive [" + applicationName + "] is being appended with packages [" + servletPackage +"]");
                ((ClassContainer<?>) applicationArchive).addPackage(servletPackage);
            }
            
            // Add signature packages to signature tests
            if (testClass.isAnnotationPresent(Signature.class)) {
                log.info("Application Archive [" + applicationName + "] is being appended with packages [" + signaturePackage +"]");
                log.info("Application Archive [" + applicationName + "] is being appended with resources " + Arrays.asList(DataSignatureTestRunner.SIG_RESOURCES));
                ((ClassContainer<?>) applicationArchive).addPackage(signaturePackage);
                ((ResourceContainer<?>) applicationArchive).addAsResources(signaturePackage,
                        DataSignatureTestRunner.SIG_RESOURCES);
            }
        }
    }
}
