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

import java.lang.reflect.Constructor;
import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import ee.jakarta.tck.data.framework.read.only.Populator;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import jakarta.data.repository.CrudRepository;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

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
public class PrepopulationExtension implements BeforeAllCallback {

    private static final Logger log = Logger.getLogger(PrepopulationExtension.class.getCanonicalName());

    private static final Predicate<ExtensionContext> IS_INSIDE_ARQUILLIAN = (context -> Boolean.parseBoolean(
            context.getConfigurationParameter(ArquillianExtension.RUNNING_INSIDE_ARQUILLIAN).orElse("false")));
    
    /**
     * A simple test to verify that the data repository is populated or not
     */
    public static final Predicate<CrudRepository<?,Long>> isPopulated = (repo -> repo.existsById(01L));

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (TestProperty.profile.equals("none")) {
            log.info("Standalone: Pre-populating read-only entities");
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

    /**
     * Populates the repository, verifies repository was populated, and pauses execution for consistency.
     * 
     * @param context - the execution context from junit
     * @throws Exception - if any of the steps cannot be completed successfully
     */
    private void prepopulateAndVerify(ExtensionContext context) throws Exception {    
        // POPULATE
        Long start = System.currentTimeMillis();
        
        List<CrudRepository<?,Long>> unpopulatedRepos = findUnpopulatedRepositories(context);
        log.info("Unpopulated repositories: " + unpopulatedRepos);
        
        List<Populator> populators = findRepositoryPopulators(unpopulatedRepos);
        log.info("Repository populators: " + populators);
        
        populators.stream().forEach(populator -> populator.populate());
        log.fine("Repositories pre-populated in " + (System.currentTimeMillis() - start) + " ms");
        
        // VERIFY
        start = System.currentTimeMillis();
        while(!unpopulatedRepos.isEmpty()) {
            if(System.currentTimeMillis() - start >= Duration.ofSeconds(Long.valueOf(TestProperty.pollTimeout.getValue())).toMillis()) {
                throw new RuntimeException("Not all repositories were confirmed to be prepopulated: " + unpopulatedRepos);
            }
            
            log.fine("Repositories still needing verification: " + unpopulatedRepos);
            
            unpopulatedRepos = unpopulatedRepos.stream()
                .filter(Predicate.not(isPopulated))
                .collect(Collectors.toList());
            
            Thread.sleep(Duration.ofSeconds(Long.valueOf(TestProperty.pollFrequency.getValue())).toMillis());
        }
        log.fine("Repository verified in " + (System.currentTimeMillis() - start) + " ms");
        
        //CONSISTENCY DELAY
        if(TestProperty.delay.isSet()) {
            Duration delay = Duration.ofSeconds(Long.valueOf(TestProperty.delay.getValue()));
            log.info("Performing consistency delay for " + delay.getSeconds() + " seconds");
            Thread.sleep(delay.toMillis());
        }
    }
    
    /**
     * <p>Searches repository class for a constant field named <b>populator</b> which 
     * contains the class name for that repositories populator.</p>
     * 
     * <p>NOTE: This is so complicated because the repository instance we get from CDI
     * is a proxy class which is stripped of all useful reflective data such as annotations, and nested classes.</p>
     * 
     * @param repos - The repositories that need to be populated
     * @return - A list of populators.
     */
    private List<Populator> findRepositoryPopulators(List<CrudRepository<?,Long>> repos) {
            return repos.stream()
            .map(repo -> {
                try {
                    Class<?> populatorType = (Class<?>) repo.getClass().getField("populator").get(repo);
                    Constructor<?> struct = populatorType.getConstructor(CrudRepository.class);
                    Populator instance = (Populator) struct.newInstance(repo);
                    return instance;
                } catch (Exception e) {
                    log.warning("Failed to construct instance of populator for repository: " + repo.getClass().getCanonicalName());
                    log.warning("Reason: " + e.toString());
                    return null;
                }
            })
            .filter(populator -> populator != null)
            .collect(Collectors.toList());
    }
    
    /**
     * Searches test class for Injected fields that are of type {@code CrudRepository<?,Long> }
     * Then, checks if they have been populated, if not they are added to the list and returned.
     * 
     * @param context - The context of the extension
     * @return - A list of unpopulated repositories
     */
    private List<CrudRepository<?,Long>> findUnpopulatedRepositories(ExtensionContext context) {
        Class<?> testClass = context.getTestClass().orElseThrow();
        return Stream.of(testClass.getDeclaredFields())
                .filter(field -> CrudRepository.class.isAssignableFrom(field.getType()))
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .map(field -> field.getType())
                .map(type -> (CrudRepository<?,Long>) CDI.current().select(type).get())
                .filter(Predicate.not(isPopulated))
                .collect(Collectors.toList());
    }

}
