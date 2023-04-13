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
package ee.jakarta.tck.data.framework.utilities;

import java.util.logging.Logger;

/**
 * This class represents the different system properties used within this TCK.
 * Each one is given a description and documentation will automatically be created in the TCK distribution.
 */
public enum TestProperty {    
    //Java properties that should always be set by the JVM
    javaHome    (true,  "java.home",                  "Path to the java executable used to create the current JVM"),
    javaSpecVer (true,  "java.specification.version", "Specification version of the java executable"),
    javaTempDir (true,  "java.io.tmpdir",             "The path to a temporary directory where a copy of the signature file will be created"),
    javaVer     (true,  "java.version",               "Full version of the java executable"),
    
    //TCK specific properties
    platform    (false, "jakarta.tck.platform",        "The platform name which can be appended to the test name for easier reporting"),
    standalone  (true,  "jakarta.tck.standalone.test", "Set to true if running in standalone mode, false otherwise"),
    entity      (true,  "jakarta.tck.entity",          "A list of common seperated entity annotations supported by the vendor. Valid values [nosql, persistence]"),
    
    //Signature testing properties
    signatureClasspath (true,  "signature.sigTestClasspath", "The path to the Jakarta Data API JAR used by your implementation"),
    signatureImageDir  (true,  "jimage.dir",                 "The path to a directory that is readable and writable that the signature test will cache Java SE modules as classes");
    
    private static final Logger log = Logger.getLogger(TestProperty.class.getCanonicalName());

    private boolean required;
    private String key;
    private String value;
    private String description;
    private String defaultValue;
    
    private boolean fetched = false;
    
    private TestProperty(boolean required, String key, String description) {
        this(required, key, description, null);
    }
    
    private TestProperty(boolean required, String key, String description, String defaultValue) {
        this.required = required;
        this.key = key;
        this.description = description;
        this.defaultValue = defaultValue;
    }
    
    
    public boolean isRequired() {
        return required;
    }
    
    public String getKey() {
        return key;
    }
    
    /**
     * Get the system property value. 
     * 
     * @return the property value
     * @throws IllegalStateException if required and no system property was returned
     */
    public String getValue() throws IllegalStateException {
        if(fetched)
            return value;

        value = System.getProperty(key, defaultValue);
        fetched = true;
        
        if(required && value == null)
            throw new IllegalStateException("Could not obtain a value for system property: " + key);
        
        log.config(toString());
        
        return value;
    }
    
    /**
     * Get the system property value as boolean. 
     * 
     * @return the boolean represented by the system property value
     * @throws IllegalStateException if required and no system property was returned
     */
    public boolean getBoolean() throws IllegalStateException {
        return Boolean.parseBoolean(getValue());
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
    
    @Override
    public String toString() {
        return """
               TestProperty[ key: %s, value: %s, required: %b, defaultValue: %s ]  isFetched? %b
               Description: %s
               """.formatted(key, value, required, defaultValue, fetched, description);        
    }    
}
