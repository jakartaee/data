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
package ee.jakarta.tck.data.framework.junit.extensions;

import java.util.logging.Logger;

import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import jakarta.enterprise.inject.spi.CDI;

/**
 * Evaluates the availability of CDI to determine if a test class/method is enabled/disabled.
 * 
 * @see ee.jakarta.tck.data.framework.junit.anno.CDI
 */
public class CDIConditionExtension implements ExecutionCondition {

    private static final Logger log = Logger.getLogger(CDIConditionExtension.class.getCanonicalName());

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {

        //We are only verifying CDI when running on a platform so first verify that this condition is running on a platform
        if(Boolean.parseBoolean(context.getConfigurationParameter(ArquillianExtension.RUNNING_INSIDE_ARQUILLIAN).orElse("false"))) {
            log.info("Inside Jakarta EE Platform, testing CDI provider.");
        } else {
            log.info("Outside Jakarta EE Platform, waiting to test CDI provider until inside.");
            return ConditionEvaluationResult.enabled("Deploying test to container.");
        }

        try {
            CDI.current();
            return ConditionEvaluationResult.enabled("CDI provider is available");
        } catch (IllegalStateException e) {
            return ConditionEvaluationResult.disabled("CDI provider is not available");
        } catch (Throwable t) {
            return ConditionEvaluationResult.disabled("CDI provider available state unknown", t.getLocalizedMessage());
        }
    }

}