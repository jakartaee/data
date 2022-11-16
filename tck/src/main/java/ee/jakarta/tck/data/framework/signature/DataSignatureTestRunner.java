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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This class performs the interactions between the test client (EE Application
 * or Standalone Test class) and the signature test framework.
 */
public class DataSignatureTestRunner extends SigTestEE {

    private Logger log = Logger.getLogger(getClass().getCanonicalName());

    public static final String SIG_RESOURCE_PACKAGE = "ee.jakarta.tck.data.framework.signature";
    public static final String SIG_FILE_NAME = "jakarta.data.sig";
    public static final String SIG_MAP_NAME = "sig-test.map";
    public static final String SIG_PKG_NAME = "sig-test-pkg-list.txt";
    
    public static final String[] SIG_RESOURCES = {SIG_FILE_NAME, SIG_MAP_NAME, SIG_PKG_NAME};

    public DataSignatureTestRunner() {
        setup();
    }

    /**
     * Returns a list of strings where each string represents a package name. Each
     * package name will have it's signature tested by the signature test framework.
     * 
     * @return String[] The names of the packages whose signatures should be
     *         verified.
     */
    @Override
    protected String[] getPackages(String vehicleName) {
        return new String[] { "jakarta.data", "jakarta.data.repository" };
    }

    /**
     * Returns the classpath for the packages we are interested in.
     * @return the classpath as a colon (:) delimited string
     */
    protected String getClasspath() {
        final String defined = System.getProperty("signature.sigTestClasspath");
        if (defined != null && !defined.isBlank()) {
            return defined;
        }

        // The Jakarta artifacts we want added to our classpath
        String[] classes = new String[] { "jakarta.data.Entity", // For jakarta-data-api.jar
        };

        // The JDK modules we want added to our classpath
        String[] jdkModules = new String[] { "java.base", "java.rmi", "java.sql", "java.naming" };

        // Get Jakarta artifacts from application server
        Set<String> classPaths = new HashSet<String>();
        for (String c : classes) {
            try {
                Class<?> clazz = Class.forName(c);
                final CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
                final URL location = codeSource == null ? null : codeSource.getLocation();
                // Likely not null, but if so we cannot find the location of the JAR or class
                if (codeSource == null || location == null) {
                    log.warning(String.format("Could not resolve the the library for %s.", clazz.getName()));
                    continue;
                }
                String path = resolvePath(location);
                if (!classPaths.contains(path)) {
                    classPaths.add(path);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to load class " + c + " from application server.");
            }
        }

        // Get JDK modules from jimage
        // Add JDK classes to classpath
        File jimageOutput = new File(testInfo.getJImageDir());
        for (String module : jdkModules) {
            Path modulePath = Paths.get(jimageOutput.getAbsolutePath(), module);
            if (Files.isDirectory(modulePath)) {
                classPaths.add(modulePath.toString());
            } else {
                throw new RuntimeException("Unable to load JDK module " + module + " from jimage output "
                        + System.lineSeparator() + "Searched in directory: " + modulePath.toString());
            }
        }

        return String.join(":", classPaths);

    }

    protected File writeStreamToTempFile(InputStream inputStream, String tempFilePrefix, String tempFileSuffix)
            throws IOException {
        FileOutputStream outputStream = null;

        try {
            File file = File.createTempFile(tempFilePrefix, tempFileSuffix);
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            while (true) {
                int bytesRead = inputStream.read(buffer);
                if (bytesRead == -1) {
                    break;
                }
                outputStream.write(buffer, 0, bytesRead);
            }
            return file;
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    protected File writeStreamToSigFile(InputStream inputStream) throws IOException {
        FileOutputStream outputStream = null;
        String tmpdir = System.getProperty("java.io.tmpdir");
        try {
            File sigfile = new File(tmpdir + File.separator + SIG_FILE_NAME);
            if (sigfile.exists()) {
                sigfile.delete();
                log.info("Existing signature file deleted to create new one");
            }
            if (!sigfile.createNewFile()) {
                log.info("signature file is not created");
            }
            outputStream = new FileOutputStream(sigfile);
            byte[] buffer = new byte[1024];
            while (true) {
                int bytesRead = inputStream.read(buffer);
                if (bytesRead == -1) {
                    break;
                }
                outputStream.write(buffer, 0, bytesRead);
            }
            return sigfile;
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    /*****
     * Boilerplate Code
     * 
     * /* The following comments are specified in the base class that defines the
     * signature tests. This is done so the test finders will find the right class
     * to run. The implementation of these methods is inherited from the super class
     * which is part of the signature test framework.
     */

    // NOTE: If the API under test is not part of your testing runtime
    // environment, you may use the property sigTestClasspath to specify
    // where the API under test lives. This should almost never be used.
    // Normally the API under test should be specified in the classpath
    // of the VM running the signature tests. Use either the first
    // comment or the one below it depending on which properties your
    // signature tests need. Please do not use both comments.

    public void signatureTest() throws Fault {
        log.info("DataSignatureTestRunner.signatureTest() called");
        SigTestResult results = null;
        String mapFile = null;
        String packageListFile = null;
        String signatureRepositoryDir = null;
//        Properties mapFileAsProps = null;
        try {
            InputStream inStreamMapfile = DataSignatureTestRunner.class.getClassLoader()
                    .getResourceAsStream(SIG_RESOURCE_PACKAGE.replace(".", "/") + "/" + SIG_MAP_NAME);
            File mFile = writeStreamToTempFile(inStreamMapfile, "sig-test", ".map");
            mapFile = mFile.getCanonicalPath();
            log.info("mapFile location is :" + mapFile);

            InputStream inStreamPackageFile = DataSignatureTestRunner.class.getClassLoader()
                    .getResourceAsStream(SIG_RESOURCE_PACKAGE.replace(".", "/") + "/" + SIG_PKG_NAME);
            File pFile = writeStreamToTempFile(inStreamPackageFile, "sig-test-pkg-list", ".txt");
            packageListFile = pFile.getCanonicalPath();
            log.info("packageFile location is :" + packageListFile);

//            mapFileAsProps = getSigTestDriver().loadMapFile(mapFile);

            InputStream inStreamSigFile = DataSignatureTestRunner.class.getClassLoader()
                    .getResourceAsStream(SIG_RESOURCE_PACKAGE.replace(".", "/") + "/" + SIG_FILE_NAME);
            File sigFile = writeStreamToSigFile(inStreamSigFile);
            log.info("signature File location is :" + sigFile.getCanonicalPath());
            signatureRepositoryDir = System.getProperty("java.io.tmpdir");

        } catch (IOException ex) {
            log.info("Exception while creating temp files :" + ex);
        }

        String[] packagesUnderTest = getPackages(testInfo.getVehicle());
        String[] classesUnderTest = getClasses(testInfo.getVehicle());
        String optionalPkgToIgnore = testInfo.getOptionalTechPackagesToIgnore();

        // unlisted optional packages are technology packages for those optional
        // technologies (e.g. jsr-88) that might not have been specified by the
        // user.
        // We want to ensure there are no full or partial implementations of an
        // optional technology which were not declared
        ArrayList<String> unlistedTechnologyPkgs = getUnlistedOptionalPackages();

        // Need to extract java modules into classes
        String jimageDir = testInfo.getJImageDir();
        File f = new File(jimageDir);
        f.mkdirs();

        String javaHome = System.getProperty("java.home");
        log.info("Executing JImage");

        try {
            ProcessBuilder pb = new ProcessBuilder(javaHome + "/bin/jimage", "extract", "--dir=" + jimageDir,
                    javaHome + "/lib/modules");
            System.out
                    .println(javaHome + "/bin/jimage extract --dir=" + jimageDir + " " + javaHome + "/lib/modules");
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            BufferedReader out = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = out.readLine()) != null) {
                log.info(line);
            }

            int rc = proc.waitFor();
            log.info("JImage RC = " + rc);
            out.close();
        } catch (Exception e) {
            log.info("Exception while executing JImage!  Some tests may fail.");
            e.printStackTrace();
        }

        String classpath = getClasspath();

        try {
            results = getSigTestDriver().executeSigTest( //
                    packageListFile, // file containing the packages/classes that are to be verified
                    mapFile, // sig-test.map file
                    signatureRepositoryDir, // directory containing the recorded signatures
                    packagesUnderTest, // packages, defined by the test client, that should be tested
                    classesUnderTest, // classes, defined by the test client, that should be tested
                    classpath, // The location of the API being verified.
                    unlistedTechnologyPkgs, // packages that should not exist within the technology under test.
                    optionalPkgToIgnore); // packages that should be ignored if found.
            log.info(results.toString());
            if (!results.passed()) {
                log.info("results.passed() returned false");
                throw new Exception();
            }

            log.info("$$$ DataSignatureTestRunner.signatureTest() returning");
        } catch (Exception e) {
            if (results != null && !results.passed()) {
                throw new Fault("DataSignatureTestRunner.signatureTest() failed!, diffs found");
            } else {
                log.info("Unexpected exception " + e.getMessage());
                throw new Fault("DataSignatureTestRunner.signatureTest() failed with an unexpected exception", e);
            }
        }
    }

    /**
     * Ensures the test project to configured correctly to run signature tests
     * before attempting to run signature tests.
     * 
     * @param standalone - True if running on a standalone JVM, False if running on
     *                   a Jakarta EE server.
     */
    public static void assertProjectSetup(boolean standalone) {
        // Ensure that jimage directory is set.
        // This is where modules will be converted back to .class files for use in
        // signature testing
        assertNotNull(System.getProperty("jimage.dir"),
                "The system property jimage.dir must be set in order to run the Signature test.");

        if (standalone) {
            // Ensure that a test classpath is set
            // The signature test plugin runs tests on a forked JVM that uses a separate
            // classpath.
            assertNotNull(System.getProperty("signature.sigTestClasspath"),
                    "The system property signature.sigTestClasspath must be set in order to run the Signature test.");
        }

        // Ensure user is running on JDK 11 or higher, different JDKs produce different
        // signatures
        int javaSpecVersion = Integer.parseInt(System.getProperty("java.specification.version"));
        assertTrue(javaSpecVersion >= 11, "The signature tests must be run on a JVM using Java 11 or higher.");

        // Ensure user has the correct security/JDK settings to allow the plugin access
        // to internal JDK classes.
        Class<?> intf;
        try {
            // This class is just a known internal class, but we could have used any class.
            intf = Class.forName("jdk.internal.vm.annotation.Contended");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to load an internal JDK class", e);
        }

        if (!standalone) {
            for (Method m : intf.getDeclaredMethods()) {
                try {
                    m.setAccessible(true);
                } catch (InaccessibleObjectException ioe) {
                    // This means that this application (module) does not have access to JDK
                    // internals via reflection
                    String message = "Tried to call setAccessible on JDK internal method and received an InaccessibleObjectException from the JDK. "
                            + "Give this application (module) access to internal messages using the following JVM properties: "
                            + "--add-exports java.base/jdk.internal.vm.annotation=ALL-UNNAMED "
                            + "--add-opens java.base/jdk.internal.vm.annotation=ALL-UNNAMED";
                    fail(message, ioe);
                } catch (SecurityException se) {
                    // This means that this application was running under a security manager that
                    // did not allow the method call
                    String message = "Tried to call setAccessible on JDK internal method and received SecurityException from the security manager. "
                            + "Give this application permission to make this method call with the security manager using the following permissions:"
                            + "permission java.lang.RuntimePermission \"accessClassInPackage.jdk.internal\"; "
                            + "permission java.lang.RuntimePermission \"accessClassInPackage.jdk.internal.reflect\"; "
                            + "permission java.lang.RuntimePermission \"accessClassInPackage.jdk.internal.vm.annotation\";";
                    fail(message, se);
                }
            }
        }
    }

    private static String resolvePath(final URL resource) {
        if (resource == null) {
            return null;
        }
        final String path = resource.getPath();
        final String protocol = resource.getProtocol();

        // Possibly only specific to JBoss Modules. However, executing this will not be
        // an issue for non-jboss-modules
        // implementations.
        if ("jar".equals(protocol)) {
            // The last path segment before "!/" should be the JAR name
            final int sepIdx = path.lastIndexOf("!/");
            // We need to ignore jar:file: and use the real filesystem path
            final int start = path.startsWith("file:") ? 5 : 0;
            if (sepIdx != -1) {
                // hit!
                return path.substring(start, sepIdx);
            }
            return path.substring(start);
        }
        return path;
    }
}
