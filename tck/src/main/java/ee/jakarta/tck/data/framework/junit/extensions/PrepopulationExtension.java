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
package ee.jakarta.tck.data.framework.junit.extensions;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import ee.jakarta.tck.data.framework.junit.anno.ReadOnlyTest;
import ee.jakarta.tck.data.framework.read.only.Populator;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import jakarta.data.repository.CrudRepository;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.CDI;

/**
 * <p>This is an extension to Junit5 that has a beforeAll method that will run before all tests in a test class. </p>
 * 
 * <p> Standalone: Pre-population will happen on the same JVM as the test class. </p>
 * 
 * <p> Client: When running against a Jakarta EE profile, this class will the skipped for the client JVM. </p>
 * 
 * <p> Container: When running against a Jakarta EE profile, this class will run on the container JVM. </p>
 * 
 * @see ee.jakarta.tck.data.framework.read.only
 */
public class PrepopulationExtension implements BeforeAllCallback, AfterAllCallback {

    private static final Logger log = Logger.getLogger(PrepopulationExtension.class.getCanonicalName());

    private static final Predicate<ExtensionContext> IS_INSIDE_ARQUILLIAN = (context -> Boolean.parseBoolean(
            context.getConfigurationParameter(ArquillianExtension.RUNNING_INSIDE_ARQUILLIAN).orElse("false")));
    
    private static SeContainer container;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (TestProperty.isStandalone()) {
            log.info("Standalone: Pre-populating read-only entities");
            try {
                CDI.current();
            } catch (IllegalStateException e) {
                container = SeContainerInitializer.newInstance().initialize();
            }
            prepopulateAndVerify(context);
            return;
        }

        if (IS_INSIDE_ARQUILLIAN.test(context)) {
            log.info("Container: Pre-populating read-only entities");
            prepopulateAndVerify(context);
            return;
        }

        log.info("Client: waiting to pre-populate read-only entities");
    }
    
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (TestProperty.isStandalone() && container != null) {
            container.close();
        }
    }

    /**
     * Populates the repository, verifies repository was populated, and pauses execution for consistency.
     * 
     * @param context - the execution context from junit
     * @throws Exception - if any of the steps cannot be completed successfully
     */
    private void prepopulateAndVerify(ExtensionContext context) throws Exception {    
        // POPULATE
        Map<CrudRepository<?, Long>, Populator> unpopulatedRepos = findUnpopulatedRepositories(context);
        log.fine("Found " + unpopulatedRepos.size() + " repository(ies) that needs to be prepopulated");
        unpopulatedRepos.entrySet().stream().forEach(entry -> {
            Long start = System.currentTimeMillis();
            entry.getValue().populate();
            log.fine("Repository [ " + entry.getKey() + " ] pre-populated in " + (System.currentTimeMillis() - start) + " ms");
        });
        
        // VERIFY
        Long start = System.currentTimeMillis();
        while(unpopulatedRepos.size() > 0) {
            if(System.currentTimeMillis() - start >= Duration.ofSeconds(TestProperty.pollTimeout.getLong()).toMillis()) {
                throw new RuntimeException("Not all repositories were confirmed to be prepopulated: " + unpopulatedRepos);
            }
            
            log.fine("Repositories still needing verification: " + unpopulatedRepos);
            
            unpopulatedRepos = unpopulatedRepos.entrySet().stream()
                    .filter(Predicate.not(entry -> entry.getKey().existsById(01L)))
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
            
            Thread.sleep(Duration.ofSeconds(TestProperty.pollFrequency.getLong()).toMillis());
        }
        log.fine("Repository(ies) verified in " + (System.currentTimeMillis() - start) + " ms");
        
        //CONSISTENCY DELAY
        if(TestProperty.delay.isSet()) {
            Duration delay = Duration.ofSeconds(TestProperty.delay.getLong());
            log.info("Performing consistency delay for " + delay.getSeconds() + " seconds");
            Thread.sleep(delay.toMillis());
        }
    }
    
    /**
     * Searches the test class for the ReadOnlyTest annotation(s) and for each annotation we will create
     * a map between the repository object, and a populator object.
     * Then, we will check if the repository has already been populated, if not they are added to the map and returned.
     * 
     * @param context - The context of the extension
     * @return - A list of un-populated repositories
     */
    @SuppressWarnings("unchecked")
    private Map<CrudRepository<?,Long>, Populator> findUnpopulatedRepositories(ExtensionContext context) {
        Class<?> testClass = context.getTestClass().orElseThrow();
        return Stream.of(testClass.getAnnotationsByType(ReadOnlyTest.class))
                .flatMap(anno -> Collections.singletonMap(anno.repository(), anno.populator()).entrySet().stream())
                .flatMap(entry -> {
                    try {
                        CrudRepository<?,Long> repo = (CrudRepository<?,Long>) CDI.current().select(entry.getKey()).get();
                        Populator populator = (Populator) entry.getValue().getConstructor(CrudRepository.class).newInstance(repo);
                        return Collections.singletonMap(repo, populator).entrySet().stream();
                    } catch (Throwable t) {
                        log.warning("Unable to find the repository [ " + entry.getKey() + " ] or instantiate the populator [ " + entry.getValue() + " ]");
                        do {
                            log.warning("Reason: " + t.getLocalizedMessage());
                            t = t.getCause();
                        } while (t != null);
                        return null;
                    }
                })
                .filter(Predicate.not(entry -> entry == null))
                .filter(Predicate.not(entry -> entry.getKey().existsById(01L)))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
}
