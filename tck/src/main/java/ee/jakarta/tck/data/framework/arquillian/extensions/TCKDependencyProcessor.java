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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class TCKDependencyProcessor implements ApplicationArchiveProcessor {
    private static final Logger log = Logger.getLogger(TCKDependencyProcessor.class.getCanonicalName());

    // List of dependencies to be added to the application archive
    private static final List<String> TCK_DEPENDENCIES = List.of(
            "org.assertj:assertj-core:3.27.7");

    @Override
    public void process(Archive<?> applicationArchive, TestClass testClass) {
        if(! (applicationArchive instanceof LibraryContainer) ) {
            return;
        }

        String applicationName = applicationArchive.getName() == null
                ? applicationArchive.getId()
                : applicationArchive.getName();
        
        for(String dependency : TCK_DEPENDENCIES) {
            File[] resolvedDependencies = Maven.resolver()
                .resolve(dependency)
                .withTransitivity()
                .asFile();
            log.info("Application Archive [" + applicationName + "] is being appended with dependencies [" + Arrays.asList(resolvedDependencies) + "]");
            ((LibraryContainer) applicationArchive).addAsLibraries(resolvedDependencies);
        }
    }
}