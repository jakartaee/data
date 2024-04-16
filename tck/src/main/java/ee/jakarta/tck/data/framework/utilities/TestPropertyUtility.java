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
package ee.jakarta.tck.data.framework.utilities;

import java.time.Duration;
import java.util.logging.Logger;

/**
 * This is a generic utility class that has methods that use test properties
 * to perform repetitive and useful actions. 
 *
 */
public class TestPropertyUtility {
    
    private static final Logger log = Logger.getLogger(TestPropertyUtility.class.getCanonicalName());
    
    private TestPropertyUtility() {
        //UTILITY CLASS
    }
    
    /**
     * Checks the profile property and determine if it is standalone or not.
     * 
     * @return - true if TCK is configured in standalone mode, false otherwise. 
     */
    public static boolean isStandalone() {
        return TestProperty.profile.isSet() ? TestProperty.profile.equals("none") : false;
    }
    
    /**
     * If a delay was configured for eventual consistency then sleep this thread
     * for that amount of time. 
     * A warning is produced if the thread is interrupted during sleep. 
     */
    public static void waitForEventualConsistency() {
        if(!TestProperty.delay.isSet()) {
            return;
        }
        Duration delay = Duration.ofSeconds(TestProperty.delay.getLong());
        try {
            Thread.sleep(delay.toMillis());
        } catch (InterruptedException e) {
            log.warning("Did not wait full duration of " + delay.toMillis() 
                      + "ms for eventual consistency due to interruption: " + e.getLocalizedMessage());
        }
    }
}
