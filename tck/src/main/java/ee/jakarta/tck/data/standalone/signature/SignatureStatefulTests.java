/*
 * Copyright (c) 2022, 2026 Contributors to the Eclipse Foundation
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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Persistence;
import ee.jakarta.tck.data.framework.junit.anno.Signature;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.framework.signature.DataStatefulSignatureTestRunner;
import ee.jakarta.tck.data.framework.utilities.TestProperty;

@Standalone
// TODO currently runs against any persistence based implementation.
// does the spec allow a persistence based implementation to not implement
// the stateful API?
@Persistence
@Signature
public class SignatureStatefulTests {
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class);
    }

    @Assertion(id = "1539", strategy = """
        Uses the sigtest-maven-plugin to execute signature tests against the stateful API 
        on a Standalone JVM or on a Jakarta EE Server
        """)
    public void testSignatures() throws Exception {
        DataStatefulSignatureTestRunner.assertProjectSetup(TestProperty.skipDeployment.getBoolean());
        DataStatefulSignatureTestRunner runner = new DataStatefulSignatureTestRunner();
        runner.signatureTest();
    }
}
