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
package ee.jakarta.tck.data.standalone.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.ReadOnlyTest;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.framework.read.only.AsciiCharacters;
import ee.jakarta.tck.data.framework.read.only.AsciiCharactersPopulator;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbers;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbersPopulator;
import jakarta.inject.Inject;

/**
 * Execute a test with an entity that is dual annotated which means
 * this test can run against a provider that supports any Entity type.
 */
@Standalone
@AnyEntity
@ReadOnlyTest(populator = NaturalNumbersPopulator.class, repository = NaturalNumbers.class)
@ReadOnlyTest(populator = AsciiCharactersPopulator.class, repository = AsciiCharacters.class)
public class EntityTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class).addClasses(EntityTest.class);
    }

    @Inject
    NaturalNumbers numbers;
    
    @Inject
    AsciiCharacters characters;
    
    @Assertion(id = "136", strategy = "Ensures that the prepopulation step for readonly entities was successful")
    public void ensureNaturalNumberPrepopulation() {
        assertNotNull(numbers);
        assertTrue(numbers.findById(0L).isEmpty(), "Zero should not have been in the set of natural numbers.");
        assertEquals(100L, numbers.count());
        assertFalse(numbers.findById(10L).get().isOdd());
    }
    
    @Assertion(id = "136", strategy = "Ensures that multiple readonly entities will be prepopulated before testing")
    public void ensureCharacterPrepopulation() {
        assertNotNull(characters);
        assertEquals('0', characters.findByNumericValue(48).get().getThisCharacter());
        assertTrue(characters.findByNumericValue(1).get().isControl());
    }
}
