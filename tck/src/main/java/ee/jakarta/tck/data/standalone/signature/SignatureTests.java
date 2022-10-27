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
package ee.jakarta.tck.data.standalone.signature;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.logging.Logger;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Signature;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.framework.junit.extensions.StandaloneExtension;
import ee.jakarta.tck.data.framework.signature.DataSignatureTestRunner;
import ee.jakarta.tck.data.framework.signature.SigTestEE.Fault;
import jakarta.inject.Inject;

@Standalone
@Signature
public class SignatureTests {
    private static final Logger log = Logger.getLogger(SignatureTests.class.getCanonicalName());

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(SignatureTestBean.class);
    }

    @Inject
    SignatureTestBean testBean;

    @Test
    @Assertion(id = "SIGNATURES", strategy = "Uses the sigtest-maven-plugin to execute signature tests on a Standalone JVM or on a Jakarta EE Server")
    public void testSignaturesStandalone() throws Exception {

        try {
            if (testBean == null && Boolean.getBoolean(StandaloneExtension.isStandaloneProperty)) {
                log.info("Signature test running in standalone mode");
                DataSignatureTestRunner.assertProjectSetup(true);
                DataSignatureTestRunner runner = new DataSignatureTestRunner();
                runner.signatureTest();
                return;
            }

            log.info("Signature test running on Jakarta EE Server");
            testBean.testSignatures(); 
        } catch (Fault f) {
            fail("Signature test failed", f);
        }

    }
}
