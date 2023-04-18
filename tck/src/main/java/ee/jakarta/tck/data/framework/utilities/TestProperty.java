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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.container.ResourceContainer;

import ee.jakarta.tck.data.framework.arquillian.extensions.TCKFrameworkAppender;

/**
 * <p> This class represents the different test properties used within this TCK.
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
    profile     (true, "jakarta.tck.profile",        "The profile name which can be appended to the test name for easier reporting"),
    
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
    
    public String getDescription() {
        return description;
    }
    
    public boolean equals(String expectedValue) {
        return getValue().equalsIgnoreCase(expectedValue);
    }
    
    /**
     * Get the test property value. 
     * 
     * @return the property value
     * @throws IllegalStateException if required and no property was found
     */
    public String getValue() throws IllegalStateException {
        String value = null;
        log.fine("Searching for property: " + key);
        
        // Client: get property from system 
        if(value == null) {
            value = System.getProperty(key);
            log.fine("Value from system: " + value);
        }
        
        //Container: get property from properties file
        if(value == null) {
            loadProperties();
            value = foundProperites.getProperty(key);
            log.fine("Value from resource file: " + value);
        }
        
        //Default: get default property
        if(value == null) {
            value = defaultValue;
            log.fine("Defaulting to value: " + value);
        }
        
        if(required && value == null)
            throw new IllegalStateException("Could not obtain a value for system property: " + key);
        
        return value;
    }    

    // UTILITY METHODS
    
    private static final String PROP_FILE = "tck.properties";
    private static Properties foundProperites;
    
    /**
     * Container: Load properties from the property file, and cache them in the foundProperties object.
     * If any error occurs, log it, and create an empty foundProperties object.
     */
    private static void loadProperties() {
        if(foundProperites != null) {
            return;
        }
        
        //Try to load property file
        InputStream propsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROP_FILE);
        if(propsStream != null) {
            try {
                foundProperites.load(propsStream);
                return;
            } catch (Exception e) {
                log.info("Attempted to load properties from resource " + PROP_FILE + " but failed. Because: " + e.getLocalizedMessage());
            }
        }
        
        //Otherwise, default to an empty set of properties
        foundProperites = new Properties();
    }
    
    /**
     * Client: Store system properties from the client to a properties file 
     * as a resource on the archive sent to the container. 
     * 
     * @param archive - The archive going to the container
     * @return the archive with a resource file attached
     */
    public static Archive<?> storeProperties(Archive<?> archive) {
        if(! (archive instanceof ResourceContainer) ) {
            throw new RuntimeException("Could not store properties to archive, because it was not a ResourceConatiner. "
                    + "Please raise an issue with the maintainers of the Jakarta Data TCK.");
        }
        
        Properties filteredProps = new Properties();
        for(TestProperty prop : TestProperty.values()) {
            if(prop.getKey().startsWith("java.")) {
                continue;
            }
            filteredProps.put(prop.getKey(), prop.getValue());
        }
        
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            filteredProps.store(out, "System properties shared with Arquillian container");
            ((ResourceContainer<?>)archive).addAsResource(new StringAsset(out.toString()), PROP_FILE);
        } catch (Exception e) {
            throw new RuntimeException("Could not store properties file to archive", e);
        }
        
        return archive;
    }
}
