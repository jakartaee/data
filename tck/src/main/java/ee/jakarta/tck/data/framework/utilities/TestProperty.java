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

import java.util.logging.Logger;

import ee.jakarta.tck.data.framework.arquillian.extensions.TCKFrameworkAppender;

/**
 * <p> This enum represents the different test properties used within this TCK.
 * Each one is given a description and documentation will automatically be created in the TCK distribution. </p>
 * 
 * <p> When a test property is requested from the client, we expect these properties to be available from the system.</p>
 * 
 * <p> When a test property is requested from the container, we will attempt to load the property from a known property file.
 * {@link TCKFrameworkAppender}
 * </p>
 */
public enum TestProperty {    
    //Java properties that should always be set by the JVM
    javaHome    (true,  "java.home",                  "Path to the java executable used to create the current JVM"),
    javaSpecVer (true,  "java.specification.version", "Specification version of the java executable"),
    javaTempDir (true,  "java.io.tmpdir",             "The path to a temporary directory where a copy of the signature file will be created"),
    javaVer     (true,  "java.version",               "Full version of the java executable"),
    
    //TCK specific properties
    profile       (false,  "jakarta.tck.profile",          "Set to 'none' to run in standalone mode. "
            + "Optionally, set to 'core', 'web', or 'full' to differentiate between profile runs.", ""),
    pollFrequency (false, "jakarta.tck.poll.frequency",    "Time in seconds between polls of the repository to verify read-only data was successfully written. "
            + "Default: 1 second", "1"),
    pollTimeout   (false, "jakarta.tck.poll.timeout",      "Time in seconds when we will stop polling to verify read-only data was successfully written. "
            + "Default: 60 seconds", "60"),
    delay         (false, "jakarta.tck.consistency.delay", "Time in seconds after verifying read-only data was successfully written to respository "
            + "for repository to have consistency. Default: none", ""),
    
    //Signature testing properties
    signatureClasspath (false,  "signature.sigTestClasspath", "The path to the Jakarta Data API JAR used by your implementation. "
            + "Required for standalone testing, but optional when testing on a Jakarta EE profile.", ""),
    signatureImageDir  (true,   "jimage.dir",                 "The path to a directory that is readable and writable that "
            + "the signature test will cache Java SE modules as classes");
    
    private static final Logger log = Logger.getLogger(TestProperty.class.getCanonicalName());

    private boolean required;
    private String key;
    private String description;
    private String defaultValue;    
    
    // CONSTRUCTORS
    private TestProperty(boolean required, String key, String description) {
        this(required, key, description, null);
    }
    
    private TestProperty(boolean required, String key, String description, String defaultValue) {
        this.required = required;
        this.key = key;
        this.description = description;
        this.defaultValue = defaultValue;
    }
    
    // GETTERS
    public boolean isRequired() {
        return required;
    }
    
    public String getKey() {
        return key;
    }
    
    public String getDescription() {
        return description;
    }
    
    // COMPARISONS
    public boolean equals(String expectedValue) {
        return getValue().equalsIgnoreCase(expectedValue);
    }
    
    public boolean isSet() {
        String value = getValue(false);
        if(value == null)
            return false;
        if(value.isBlank() || value.isEmpty()) {
            return false;
        }
        return true;
    }
    
    // CONVERTERS
    public long getLong() throws IllegalStateException, NumberFormatException {
        return Long.parseLong(getValue());
    }
    
    public int getInt() throws IllegalStateException, NumberFormatException {
        return Integer.parseInt(getValue());
    }
    
    /**
     * Get the test property value. 
     * 
     * @return the property value
     * @throws IllegalStateException if required and no property was found
     */
    public String getValue() throws IllegalStateException {
        return getValue(required);
    }
    
    private String getValue(boolean verify) throws IllegalStateException {
        String value = null;
        log.fine("Searching for property: " + key);
        
        // Client: get property from system 
        if(value == null) {
            value = System.getProperty(key);
            log.fine("Value from system: " + value);
        }
        
        //Container: get property from properties file
        if(value == null) {
            value = TestPropertyHandler.loadProperties().getProperty(key);
            log.fine("Value from resource file: " + value);
        }
        
        //Default: get default property
        if(value == null) {
            value = defaultValue;
            log.fine("Defaulting to value: " + value);
        }
        
        if(verify && value == null)
            throw new IllegalStateException("Could not obtain a value for system property: " + key);
        
        return value;
    }
}
