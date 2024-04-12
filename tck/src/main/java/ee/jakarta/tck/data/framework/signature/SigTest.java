/*
 * Copyright (c) 2022, 2024 Contributors to the Eclipse Foundation
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

import java.io.PrintWriter;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class should be extended by TCK developers that wish to create a set of
 * signature tests that run outside of any Java EE container. Developers must
 * implement the getPackages method to specify which packages are to be tested
 * by the signature test framework.
 */
public abstract class SigTest {
    
    private static final Logger log = Logger.getLogger(SigTest.class.getCanonicalName());

    protected SignatureTestDriver driver;

    /**
     * <p>
     * Returns a {@link SignatureTestDriver} appropriate for the particular TCK
     * (using API check or the Signature Test Framework).
     * </p>
     *
     * <p>
     * The default implementation of this method will return a
     * {@link SignatureTestDriver} that will use API Check. TCK developers can
     * override this to return the desired {@link SignatureTestDriver} for their
     * TCK.
     * 
     * @return Object instance of SignatureTestDriver
     */
    protected SignatureTestDriver getSigTestDriver() {

        if (driver == null) {
            driver = SignatureTestDriverFactory.getInstance(SignatureTestDriverFactory.SIG_TEST);
        }

        return driver;

    } // END getSigTestDriver

    /**
     * Returns the location of the package list file. This file denotes the valid
     * sub-packages of any package being verified in the signature tests.
     *
     * Sub-classes are free to override this method if they use a different path or
     * filename for their package list file. Most users should be able to use this
     * default implementation.
     *
     * @return String The path and name of the package list file.
     */
    protected String getPackageFile() {
        return getSigTestDriver().getPackageFileImpl();
    }

    /**
     * Returns the path and name of the signature map file that this TCK uses when
     * conducting signature tests. The signature map file tells the signature test
     * framework which API versions of tested packages to use. To keep this code
     * platform independent, be sure to use the File.separator string (or the
     * File.separatorChar) to denote path separators.
     *
     * Sub-classes are free to override this method if they use a different path or
     * filename for their signature map file. Most users should be able to use this
     * default implementation.
     *
     * @return String The path and name of the signature map file.
     */
    protected String getMapFile() {
        return getSigTestDriver().getMapFileImpl();
    }

    /**
     * Returns the directory that contains the signature files.
     *
     * Sub-classes are free to override this method if they use a different
     * signature repository directory. Most users should be able to use this default
     * implementation.
     *
     * @return String The signature repository directory.
     */
    protected String getRepositoryDir() {
        return getSigTestDriver().getRepositoryDirImpl();
    }

    /**
     * <p>
     * Returns the list of Optional Packages which are not accounted for. By
     * 'unlisted optional' we mean the packages which are Optional to the technology
     * under test that the user did NOT specifically list for testing. For example,
     * with Java EE 7 implementation, a user could additionally opt to test a JSR-88
     * technology along with the Java EE technology. But if the user chooses NOT to
     * list this optional technology for testing (via ts.jte javaee.level prop) then
     * this method will return the packages for JSR-88 technology with this method
     * call.
     * </p>
     * 
     * <p>
     * This is useful for checking for a scenarios when a user may have forgotten to
     * identify a whole or partial technology implementation and in such cases, Java
     * EE platform still requires testing it.
     * </p>
     * 
     * <p>
     * Any partial or complete impl of an unlistedOptionalPackage sends up a red
     * flag indicating that the user must also pass tests for this optional
     * technology area.
     * </p>
     * 
     * <p>
     * Sub-classes are free to override this method if they use a different
     * signature repository directory. Most users should be able to use this default
     * implementation - which means that there was NO optional technology packages
     * that need to be tested.
     * </p>
     * 
     * @return List of unlisted packages
     */
    protected ArrayList<String> getUnlistedOptionalPackages() {
        return null;
    }

    /**
     * Returns the list of packages that must be tested by the siganture test
     * framework. TCK developers must implement this method in their signature test
     * sub-class.
     *
     * @return String A list of packages that the developer wishes to test using the
     *         signature test framework.
     */
    protected abstract String[] getPackages();

    /**
     * Returns an array of individual classes that must be tested by the signature
     * test framwork. TCK developers may override this method when this
     * functionality is needed. Most will only need package level granularity.
     *
     * @return an Array of Strings containing the individual classes the framework
     *         should test. The default implementation of this method returns a
     *         zero-length array.
     */
    protected String[] getClasses() {

        return new String[] {};

    } // END getClasses

    /**
     * Called by the test framework to initialize this test. The method simply
     * retrieves some state information that is necessary to run the test when when
     * the test framework invokes the run method (actually the test1 method).
     */
    public void setup() {
        try {
            log.info("$$$ SigTest.setup() called");
            log.info("$$$ SigTest.setup() complete");
        } catch (Exception e) {
            log.info("Unexpected exception " + e.getMessage());
            // throw new Fault("setup failed!", e);
        }
    }

    /**
     * Called by the test framework to cleanup any outstanding state. This method
     * simply passes the message through to the utility class so the implementation
     * can be used by both framework base classes.
     *
     * @throws Fault When an error occurs cleaning up the state of this test.
     */
    public void cleanup() throws Fault {
        log.info("$$$ SigTest.cleanup() called");
        try {
            getSigTestDriver().cleanupImpl();
            log.info("$$$ SigTest.cleanup() returning");
        } catch (Exception e) {
            throw new Fault("Cleanup failed!", e);
        }
    }

    public static class Fault extends Exception {
        private static final long serialVersionUID = -1574745208867827913L;

        public Throwable t;

        /**
         * creates a Fault with a message
         * @param msg - Error message
         */
        public Fault(String msg) {
            super(msg);
            log.info(msg);
        }

        /**
         * creates a Fault with a message.
         *
         * @param msg the message
         * @param t   prints this exception's stacktrace
         */
        public Fault(String msg, Throwable t) {
            super(msg);
            this.t = t;
            // log.info(msg, t);
        }

        /**
         * creates a Fault with a Throwable.
         *
         * @param t the Throwable
         */
        public Fault(Throwable t) {
            super(t);
            this.t = t;
        }

        /**
         * Prints this Throwable and its backtrace to the standard error stream.
         */
        public void printStackTrace() {
            if (this.t != null) {
                this.t.printStackTrace();
            } else {
                super.printStackTrace();
            }
        }

        /**
         * Prints this throwable and its backtrace to the specified print stream.
         */
        public void printStackTrace(PrintStream s) {
            if (this.t != null) {
                this.t.printStackTrace(s);
            } else {
                super.printStackTrace(s);
            }
        }

        /**
         * Prints this throwable and its backtrace to the specified print writer.
         */
        public void printStackTrace(PrintWriter s) {
            if (this.t != null) {
                this.t.printStackTrace(s);
            } else {
                super.printStackTrace(s);
            }
        }

        @Override
        public Throwable getCause() {
            return t;
        }

        @Override
        public synchronized Throwable initCause(Throwable cause) {
            if (t != null)
                throw new IllegalStateException("Can't overwrite cause");
            if (!Exception.class.isInstance(cause))
                throw new IllegalArgumentException("Cause not permitted");
            this.t = (Exception) cause;
            return this;
        }
    }

    /**
     * This exception is used only by EETest. Overrides 3 printStackTrace methods to
     * preserver the original stack trace. Using setStackTraceElement() would be
     * more elegant but it is not available prior to j2se 1.4.
     *
     * @author Kyle Grucci
     */
    public static class SetupException extends Exception {
        private static final long serialVersionUID = -7616313680616499158L;

        public Exception e;

        /**
         * creates a Fault with a message
         * @param msg - The error message
         */
        public SetupException(String msg) {
            super(msg);
        }

        /**
         * creates a SetupException with a message
         *
         * @param msg the message
         * @param e   prints this exception's stacktrace
         */
        public SetupException(String msg, Exception e) {
            super(msg);
            this.e = e;
        }

        /**
         * Prints this Throwable and its backtrace to the standard error stream.
         */
        public void printStackTrace() {
            if (this.e != null) {
                this.e.printStackTrace();
            } else {
                super.printStackTrace();
            }
        }

        /**
         * Prints this throwable and its backtrace to the specified print stream.
         */
        public void printStackTrace(PrintStream s) {
            if (this.e != null) {
                this.e.printStackTrace(s);
            } else {
                super.printStackTrace(s);
            }
        }

        /**
         * Prints this throwable and its backtrace to the specified print writer.
         */
        public void printStackTrace(PrintWriter s) {
            if (this.e != null) {
                this.e.printStackTrace(s);
            } else {
                super.printStackTrace(s);
            }
        }

        @Override
        public Throwable getCause() {
            return e;
        }

        @Override
        public synchronized Throwable initCause(Throwable cause) {
            if (e != null)
                throw new IllegalStateException("Can't overwrite cause");
            if (!Exception.class.isInstance(cause))
                throw new IllegalArgumentException("Cause not permitted");
            this.e = (Exception) cause;
            return this;
        }
    }

} // end class SigTest
