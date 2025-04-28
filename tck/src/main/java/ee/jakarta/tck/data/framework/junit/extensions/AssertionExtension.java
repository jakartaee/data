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
package ee.jakarta.tck.data.framework.junit.extensions;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.logging.Logger;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;

/**
 * If a test fails or is disabled we can warn the vendor of the assertion ID As
 * well as provide test strategy information to aid in resolving the issue. This
 * data is obtained by the {@code @Assertion} annotation.
 *
 * @see ee.jakarta.tck.data.framework.junit.anno.Assertion
 */
public class AssertionExtension implements TestWatcher, BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private static final Logger log = Logger.getLogger(AssertionExtension.class.getCanonicalName());
    private static final String nl = System.lineSeparator();

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        Method testMethod = context.getRequiredTestMethod();
        Assertion instance = testMethod.getAnnotation(Assertion.class);
        if (instance != null) {
            log.warning(testMethod.getName() + " failed " + nl
                    + " @Assertion.id: #" + instance.id() + nl
                    + " @Assertion.strategy: " + instance.strategy() + nl
                    + " Throwable.cause: " + cause.getLocalizedMessage());
        }
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        Method testMethod = context.getRequiredTestMethod();
        Assertion instance = testMethod.getAnnotation(Assertion.class);
        if (instance != null) {
            log.warning(testMethod.getName() + " was aborted " + nl
                    + " @Assertion.id: #" + instance.id() + nl
                    + " @Assertion.strategy: " + instance.strategy() + nl
                    + " Throwable.cause: " + cause.getLocalizedMessage());
        }
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        Method testMethod = context.getRequiredTestMethod();
        Assertion instance = testMethod.getAnnotation(Assertion.class);
        if (instance != null) {
            log.warning(testMethod.getName() + " is disabled" + nl
                    + " @Assertion.id: #" + instance.id() + nl
                    + " @Assertion.strategy: " + instance.strategy() + nl
                    + " @Disabled.reason:" + reason.get());
        }
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        log.info(">>> Begin test: " + context.getDisplayName());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        log.info("<<< End test: " + context.getDisplayName());
    }

}
