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
package ee.jakarta.tck.data.framework.signature;

/**
 * This class holds the data passed to a signature test invocation during the
 * setup phase. This allows us to keep the passed data separate and reuse the
 * data between the signature test framework base classes.
 */
public final class SigTestData {
    
    private SigTestData() {
        //Utility Class
    }

    public static String getVehicle() {
        return "";
    }

    public static String getBinDir() {
        return "";
    }

    public static String getTSHome() {
        return "";
    }

    public static String getJavaeeLevel() {
        return "";
    }

    public static String getCurrentKeywords() {
        return "";
    }

    public static String getProperty(String prop) {
        return System.getProperty(prop);
    }

    public static String getOptionalTechPackagesToIgnore() {
        return "";
    }

    public static String getJtaJarClasspath() {
        return "";
    }
} // end class SigTestData
