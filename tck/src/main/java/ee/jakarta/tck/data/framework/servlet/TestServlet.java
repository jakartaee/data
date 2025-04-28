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
package ee.jakarta.tck.data.framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>Standard superclass for test servlets that accepts a `testMethod`
 * parameter
 * to the doGet / doPost methods that will attempt to run that method on the</p>
 * subclass.
 *
 * <p>The doGet / doPost methods will append `SUCCESS` to the response if the
 * test
 * is successfully. Otherwise, SUCCESS will not be appended to the
 * response.</p>
 */
public class TestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(TestServlet.class.getCanonicalName());

    private boolean runBeforeClass = true;

    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";
    public static final String TEST_METHOD_PARAM = "testMethod";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String method = request.getParameter(TEST_METHOD_PARAM);

        String requestParams = request.getParameterMap().keySet().stream()
                .map(key -> key + "=" + Arrays.asList(request.getParameterMap().get(key)))
                .collect(Collectors.joining(", ", "{", "}"));

        log.info("Received request to run test " + method + " with parameters " + requestParams);

        if (runBeforeClass) {
            try {
                beforeClass();
                runBeforeClass = false;
            } catch (Exception e) {
                throw new RuntimeException("Caught exception trying to run beforeClass method.", e);
            }
        }

        PrintWriter writer = response.getWriter();
        if (method != null && method.length() > 0) {
            try {
                before();
                // Use reflection to try invoking various test method signatures:
                // 1) method(HttpServletRequest request, HttpServletResponse response)
                // 2) method()
                // 3) use custom method invocation by calling invokeTest(method, request,
                // response)
                try {
                    Method mthd = getClass().getMethod(method, HttpServletRequest.class, HttpServletResponse.class);
                    mthd.invoke(this, request, response);
                } catch (NoSuchMethodException nsme) {
                    try {
                        Method mthd = getClass().getMethod(method, (Class<?>[]) null);
                        mthd.invoke(this);
                    } catch (NoSuchMethodException nsme1) {
                        invokeTest(method, request, response);
                    }
                } finally {
                    after();
                }

                writer.println(SUCCESS);
            } catch (Throwable t) {
                if (t instanceof InvocationTargetException) {
                    t = t.getCause();
                }
                writer.println(FAILURE);
                String message = "Caught exception attempting to call test method " + method + " on servlet "
                        + getClass().getName();
                writer.println(message);
                t.printStackTrace(writer);
            }
        } else {
            writer.println("ERROR: expected testMethod parameter");
        }

        writer.flush();
        writer.close();
    }

    /**
     * Override to mimic JUnit's {@code @BeforeClass} annotation.
     *
     * @throws Exception - unable to setup
     */
    protected void beforeClass() throws Exception {
    }

    /**
     * Override to mimic JUnit's {@code @Before} annotation.
     *
     * @throws Exception - unable to setup
     */
    protected void before() throws Exception {
    }

    /**
     * Override to mimic JUnit's {@code @After} annotation.
     *
     * @throws Exception - Unable to cleanup
     */
    protected void after() throws Exception {
    }


    /**
     * Implement this method for custom test invocation, such as specific test
     * method signatures
     *
     * @param method   - The method name
     * @param request  - HTTPServlet Request
     * @param response - HTTPServlet Response
     * @throws Exception - Unable to invoke test
     */
    protected void invokeTest(String method, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        throw new NoSuchMethodException("No such method '" + method + "' found on class " + getClass()
                + " with any of the following signatures:   " + method + "(HttpServletRequest, HttpServletResponse)   "
                + method + "()");
    }
}
