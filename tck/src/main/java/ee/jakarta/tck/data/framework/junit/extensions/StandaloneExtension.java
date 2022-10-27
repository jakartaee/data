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
package ee.jakarta.tck.data.framework.junit.extensions;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

/**
 * This is a Junit5 extension class that extends ArquillianExtension
 * 
 * This extension will passthrough to the ArquillianExtension class when running
 * against a core/web/full profile Jakarta EE server, but will skip Arquillian
 * processing when running against a standalone implementation.
 * 
 * @see org.jboss.arquillian.junit5.ArquillianExtension
 *
 */
public class StandaloneExtension extends ArquillianExtension implements BeforeAllCallback, AfterAllCallback,
        BeforeEachCallback, AfterEachCallback, InvocationInterceptor, TestExecutionExceptionHandler {
    
    private static final Logger log = Logger.getLogger(StandaloneExtension.class.getCanonicalName());

    private static boolean isStandalone = Boolean.getBoolean("jakarta.standalone.test");

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (isStandalone) {
            log.info("Running tests in standalone mode, arquillian will not create or deploy archives for test class: " + context.getTestClass().get().getCanonicalName());
            return;
        }
        super.beforeAll(context);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (isStandalone) {
            return;
        }
        super.afterAll(context);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (isStandalone) {
            return;
        }
        super.beforeEach(context);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (isStandalone) {
            return;
        }
        super.afterEach(context);
    }

    @Override
    public void interceptTestTemplateMethod(Invocation<Void> invocation,
            ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        if (isStandalone) {
            invocation.proceed();
            return;
        }
        super.interceptTestTemplateMethod(invocation, invocationContext, extensionContext);
    }

    @Override
    public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
            ExtensionContext extensionContext) throws Throwable {
        if (isStandalone) {
            invocation.proceed();
            return;
        }
        super.interceptTestMethod(invocation, invocationContext, extensionContext);
    }

    @Override
    public void interceptBeforeEachMethod(Invocation<Void> invocation,
            ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        if (isStandalone) {
            invocation.proceed();
            return;
        }
        super.interceptBeforeEachMethod(invocation, invocationContext, extensionContext);
    }

    @Override
    public void interceptAfterEachMethod(Invocation<Void> invocation,
            ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        if (isStandalone) {
            invocation.proceed();
            return;
        }
        super.interceptAfterEachMethod(invocation, invocationContext, extensionContext);
    }

    @Override
    public void interceptBeforeAllMethod(Invocation<Void> invocation,
            ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        if (isStandalone) {
            invocation.proceed();
            return;
        }
        super.interceptBeforeAllMethod(invocation, invocationContext, extensionContext);
    }

    @Override
    public void interceptAfterAllMethod(Invocation<Void> invocation,
            ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        if (isStandalone) {
            invocation.proceed();
            return;
        }
        super.interceptAfterAllMethod(invocation, invocationContext, extensionContext);
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        if (isStandalone) {
            throw throwable;
        }
        super.handleTestExecutionException(context, throwable);
    }

}
