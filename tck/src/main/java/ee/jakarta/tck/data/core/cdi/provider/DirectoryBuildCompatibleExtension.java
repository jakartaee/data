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
package ee.jakarta.tck.data.core.cdi.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ee.jakarta.tck.data.common.cdi.Directory;
import jakarta.data.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.build.compatible.spi.BuildCompatibleExtension;
import jakarta.enterprise.inject.build.compatible.spi.Enhancement;
import jakarta.enterprise.inject.build.compatible.spi.Synthesis;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticBeanBuilder;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticComponents;
import jakarta.enterprise.inject.build.compatible.spi.Types;
import jakarta.enterprise.lang.model.AnnotationInfo;
import jakarta.enterprise.lang.model.AnnotationMember;
import jakarta.enterprise.lang.model.declarations.ClassInfo;

/**
 * A fake Jakarta Data provider extension that only produces a single repository class,
 * which is because it doesn't have a real implementation and is only for tests
 * that register a Jakarta Data provider as a CDI extension.
 */
public class DirectoryBuildCompatibleExtension implements BuildCompatibleExtension {
    
    private static final Logger log = Logger.getLogger(DirectoryBuildCompatibleExtension.class.getCanonicalName());
    
    // List of repository class names
    private final List<String> repositoryClassNames = new ArrayList<>();

    /**
     * Identify classes that are annotated with Repository
     * and determine which apply to this provider.
     */
    @Enhancement(withAnnotations = Repository.class, types = Object.class, withSubtypes = true)
    public void enhancement(ClassInfo repositoryClassInfo) {

        AnnotationInfo repositoryAnnotationInfo = repositoryClassInfo.annotation(Repository.class);

        // First, check for explicit configuration to use this provider:
        @SuppressWarnings({ "deprecation", "removal" }) // Work around bug where WELD lacks doPrivileged.
        AnnotationMember providerMember = java.security.AccessController.doPrivileged((java.security.PrivilegedAction<AnnotationMember>) () -> //
        repositoryAnnotationInfo.member("provider"));

        String provider = providerMember.asString();
        boolean providesRepository = Directory.PERSON_PROVIDER.equals(provider);
        
        log.info("During enhancement, found " + repositoryClassInfo + " with provider of " + provider);
        
        if(providesRepository) {
            log.info("This extension provides for the repository: " + repositoryClassInfo.name());
            repositoryClassNames.add(repositoryClassInfo.name());
        } else {
            log.info("This extension does not provides for the repository: " + repositoryClassInfo.name());
        }
    }

    /**
     * Register beans for repositories.
     */
    @Synthesis
    public void synthesis(Types types, SyntheticComponents synth) throws ClassNotFoundException {
        for (String repoClassName : repositoryClassNames) {
            @SuppressWarnings("unchecked")
            Class<Object> repoClass = (Class<Object>) Class.forName(repoClassName);
            @SuppressWarnings({ "deprecation", "removal" }) // Work around bug where WELD lacks doPrivileged.
            SyntheticBeanBuilder<Object> builder = java.security.AccessController.doPrivileged((java.security.PrivilegedAction<SyntheticBeanBuilder<Object>>) () -> //
            synth
                            .addBean(repoClass)
                            .name(repoClassName)
                            .type(types.ofClass(repoClassName))
                            .scope(ApplicationScoped.class)
                            .withParam("provider", Directory.PERSON_PROVIDER)
                            .createWith(PersonBeanCreator.class));
            
            log.info("Registered " + repoClassName + " bean with " + builder);
        }
    }

}
