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

import java.util.Arrays;
import java.util.Iterator;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.ReadOnlyTest;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.framework.read.only.AsciiCharacters;
import ee.jakarta.tck.data.framework.read.only.AsciiCharactersPopulator;
import ee.jakarta.tck.data.framework.read.only.NaturalNumber;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbers;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbersPopulator;
import ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.Slice;
import jakarta.data.repository.Sort;
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

    @Assertion(id = "136",
               strategy = "Request a Slice higher than the final Slice, expecting an empty Slice with 0 results.")
    public void testBeyondFinalSlice() {
         Pageable sixth = Pageable.ofSize(5).sortBy(Sort.desc("id")).page(6);
         Slice<NaturalNumber> slice = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(NumberType.PRIME,
                                                                                             8L,
                                                                                             sixth);
         assertEquals(0, slice.numberOfElements());
         assertEquals(0, slice.stream().count());
         assertEquals(false, slice.hasContent());
         assertEquals(false, slice.iterator().hasNext());
     }

     @Assertion(id = "136",
                strategy = "Request the last Slice of up to 5 results, expecting to find the final 2.")
     public void testFinalSliceOfUpTo5() {
         Pageable fifth = Pageable.ofSize(5).page(5).sortBy(Sort.desc("id"));
         Slice<NaturalNumber> slice = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(NumberType.PRIME,
                                                                                             8L,
                                                                                             fifth);
         assertEquals(true, slice.hasContent());
         assertEquals(5, slice.pageable().page());
         assertEquals(2, slice.numberOfElements());

         Iterator<NaturalNumber> it = slice.iterator();

         // first result
         assertEquals(true, it.hasNext());
         NaturalNumber number = it.next();
         assertEquals(3L, number.getId());
         assertEquals(NumberType.PRIME, number.getNumType());
         assertEquals(1L, number.getFloorOfSquareRoot());
         assertEquals(true, number.isOdd());
         assertEquals(Short.valueOf((short) 2), number.getNumBitsRequired());

         // second result
         assertEquals(true, it.hasNext());
         number = it.next();
         assertEquals(2L, number.getId());
         assertEquals(NumberType.PRIME, number.getNumType());
         assertEquals(1L, number.getFloorOfSquareRoot());
         assertEquals(false, number.isOdd());
         assertEquals(Short.valueOf((short) 2), number.getNumBitsRequired());

         assertEquals(false, it.hasNext());
     }

     @Assertion(id = "136",
                strategy = "Request the first Slice of 5 results, expecting to find all 5.")
     public void testFirstSliceOf5() {
         Pageable first5 = Pageable.ofSize(5).sortBy(Sort.desc("id"));
         Slice<NaturalNumber> slice = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(NumberType.PRIME,
                                                                                             8L,
                                                                                             first5);
         assertEquals(5, slice.numberOfElements());

         Iterator<NaturalNumber> it = slice.iterator();

         // first result
         assertEquals(true, it.hasNext());
         NaturalNumber number = it.next();
         assertEquals(79L, number.getId());
         assertEquals(NumberType.PRIME, number.getNumType());
         assertEquals(8L, number.getFloorOfSquareRoot());
         assertEquals(true, number.isOdd());
         assertEquals(Short.valueOf((short) 7), number.getNumBitsRequired());

         // second result
         assertEquals(true, it.hasNext());
         assertEquals(73L, it.next().getId());

         // third result
         assertEquals(true, it.hasNext());
         assertEquals(71L, it.next().getId());

         // fourth result
         assertEquals(true, it.hasNext());
         assertEquals(67L, it.next().getId());

         // fifth result
         assertEquals(true, it.hasNext());
         number = it.next();
         assertEquals(61L, number.getId());
         assertEquals(NumberType.PRIME, number.getNumType());
         assertEquals(7L, number.getFloorOfSquareRoot());
         assertEquals(true, number.isOdd());
         assertEquals(Short.valueOf((short) 6), number.getNumBitsRequired());

         assertEquals(false, it.hasNext());
     }

     @Assertion(id = "136",
                strategy = "Request a Slice of results where none match the query, expecting an empty Slice with 0 results.")
     public void testSliceOfNothing() {
         Pageable pagination = Pageable.ofSize(5).sortBy(Sort.desc("id"));
         Slice<NaturalNumber> slice = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(NumberType.COMPOSITE,
                                                                                             1L,
                                                                                             pagination);

         assertEquals(false, slice.hasContent());
         assertEquals(0, slice.content().size());
         assertEquals(0, slice.numberOfElements());
     }

     @Assertion(id = "136",
                strategy = "Request the third Slice of 5 results, expecting to find all 5. Request the next Slice via nextPageable, expecting page number 4 and another 5 results.")
     public void testThirdAndFourthSlicesOf5() {
         Pageable third5 = Pageable.ofPage(3).size(5).sortBy(Sort.desc("id"));
         Slice<NaturalNumber> slice = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(NumberType.PRIME,
                                                                                             8L,
                                                                                             third5);

         assertEquals(3, slice.pageable().page());
         assertEquals(5, slice.numberOfElements());

         assertEquals(Arrays.toString(new Long[] { 37L, 31L, 29L, 23L, 19L }),
                      Arrays.toString(slice.stream().map(number -> number.getId()).toArray()));

         Pageable fourth5 = third5.next();

         slice = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(NumberType.PRIME,
                                                                        8L,
                                                                        fourth5);

         assertEquals(4, slice.pageable().page());
         assertEquals(5, slice.numberOfElements());

         assertEquals(Arrays.toString(new Long[] { 17L, 13L, 11L, 7L, 5L }),
                      Arrays.toString(slice.stream().map(number -> number.getId()).toArray()));
     }
}
