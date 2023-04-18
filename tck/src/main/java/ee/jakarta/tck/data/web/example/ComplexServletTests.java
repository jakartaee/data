/*
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInfo;

import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Web;
import ee.jakarta.tck.data.framework.servlet.TestClient;
import ee.jakarta.tck.data.framework.servlet.URLBuilder;

/**
 * Example test case where we want to run a test on the client side,
 * and make assertions in the container.
 * FIXME - This is an example test, remove before the 1.0 release.
 */
@Web
@AnyEntity
@RunAsClient //These tests will run on the client JVM and make calls to a servlet running on a web container.
public class ComplexServletTests extends TestClient {
    
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(ComplexServlet.class);
    } 
    
    @ArquillianResource
    URL baseURL;
    
    @Assertion(id = "26", strategy = "Run server side tests that will succeed and fail and assert the results.")
    public void testSuccessAndFailure(TestInfo testInfo) {
        URL requestURL = URLBuilder.fromURL(baseURL)
                .withPath(ComplexServlet.URL_PATTERN)
                .withQuery(ComplexServlet.TEST_METHOD_PARAM, "testServletSideSuccess")
                .build();
        
        super.runTest(requestURL);
        
        Assertions.assertThrows(AssertionError.class, () -> {
            URL requestURL2 = URLBuilder.fromURL(baseURL)
                    .withPath(ComplexServlet.URL_PATTERN)
                    .withQuery(ComplexServlet.TEST_METHOD_PARAM, "testServletSideFailure")
                    .build();
            
            super.runTest(requestURL2);
        });
    }
    
    @Assertion(id = "26", strategy = "Run server side test with a method name that matches the client side test.")
    public void testMatchServletSideMethodName(TestInfo testInfo) {
        URL requestURL = URLBuilder.fromURL(baseURL)
                .withPath(ComplexServlet.URL_PATTERN)
                .withQuery(ComplexServlet.TEST_METHOD_PARAM, testInfo.getTestMethod().get().getName())
                .build();
        
        super.runTest(requestURL);
    }
    
    @Assertion(id = "26", strategy = "Run server side test that append a unique string to the response body and make sure it is returned.")
    public void testServletSideCustomResponse(TestInfo testInfo) {
        URL requestURL = URLBuilder.fromURL(baseURL)
                .withPath(ComplexServlet.URL_PATTERN)
                .withQuery(ComplexServlet.TEST_METHOD_PARAM, testInfo.getTestMethod().get().getName())
                .build();
        
        super.runTest(requestURL, ComplexServlet.EXPECTED_RESPONSE);
    }
}