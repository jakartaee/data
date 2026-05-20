/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.framework.arquillian.extensions;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.jboss.arquillian.container.test.spi.client.deployment.CachedAuxilliaryArchiveAppender;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;


/**
 * Creates and appends archive dependencies to TCK application deployments. 
 */
public class TCKDependencyProcessor {
    
    private static final Logger log = Logger.getLogger(TCKDependencyProcessor.class.getCanonicalName());

    /**
     * An archive appender that gets applied to ALL TCK application deployments.
     * This appender re-creates the assertj-core artifact with the classes/resources
     * on the classpath on the client at runtime.
     * 
     * Note: removed the use of Maven.resolver() because many enterprise industries use 
     * maven central mirrors (like Artifactory) and the resolver at runtime does not
     * have the maven context that started the test.
     * Therefore, Maven.resolver() will always pull directly from Maven Central and
     * can cause rate limiting.
     */
    public static class AssertJArchiveAppender extends CachedAuxilliaryArchiveAppender {
        @Override
        protected Archive<?> buildArchive() {
            final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "assertj-core.jar");
            archive.addPackages(true, "org.assertj.core");
            
            addManifestDirectoryResources(archive, "org.assertj.core.api.Assertions");
            
            log.info("Recreated assertj-core.jar archive with contents: " + archive.toString(true));
            
            return archive;
        }
    }
    
    /**
     * An archive appender that get's applied selectively in the 
     * {@link TCKArchiveProcessor#process(Archive, org.jboss.arquillian.test.spi.TestClass)} method.
     */
    public static class SigTestArchive {

        private static Archive<?> instance;
        
        private SigTestArchive() {
            // Only access via static method
        }
        
        private static Archive<?> buildArchive() {
            final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "sigtest-maven-plugin.jar");
            archive.addPackages(true, "com.sun.tdk");
            archive.addPackages(true, "org.netbeans.apitest");
            archive.addPackages(true, "jakarta.tck.sigtest_maven_plugin");
            
            addManifestDirectoryResources(archive, "com.sun.tdk.signaturetest.Version");
            
            log.info("Recreated sigtest-maven-plugin.jar archive with contents: " + archive.toString(true));
            
            return instance = archive;
        }
        
        public static Archive<?> get() {
            return Objects.requireNonNullElseGet(instance, () -> buildArchive());
        }
        
    }


    /**
     * Adds all META-INF directory resources from the JAR file containing the specified
     * reference class to the provided archive. This is used to preserve manifest and
     * service provider configuration files when recreating dependency archives.
     *
     * @param archive the JavaArchive to add META-INF resources to
     * @param refClass the fully qualified name of a class within the source JAR
     * @throws RuntimeException if the reference class cannot be found, is not loaded
     *         from a JAR file, or if an I/O error occurs while copying resources
     */
    private static void addManifestDirectoryResources(final JavaArchive archive, final String refClass) {

       // Get classloader to reference class
       ClassLoader artifactClassloader;
       try {
          artifactClassloader = Class.forName(refClass).getClassLoader();
       } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find reference class for assertj", e);
        }
       
       // Construct reference resource
       String refResrouce = refClass.replace('.', '/') + ".class";
       
        try {
           
           // Find artifact's location on the file system and verify it's a JAR
            URL classUrl = artifactClassloader.getResource(refResrouce);
            if (classUrl == null) {
                throw new RuntimeException("Could not locate reference class resource for " + refClass);
            }
            if (!"jar".equals(classUrl.getProtocol())) {
                throw new RuntimeException("Expected "+ archive.getName() + 
                      " classes to be loaded from a jar but found protocol: " + classUrl.getProtocol());
            }

            // Add all META-INF/ resources to the re-create archive
            JarURLConnection connection = (JarURLConnection) classUrl.openConnection();
            try (JarFile jarFile = connection.getJarFile()) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (!entry.isDirectory() && entry.getName().startsWith("META-INF/")) {
                        try (InputStream input = jarFile.getInputStream(entry)) {
                            archive.add(new ByteArrayAsset(input.readAllBytes()), entry.getName());
                        }
                    }
                }
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy META-INF resources into recreated archive", e);
        }
    }
}