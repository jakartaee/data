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
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.BeforeEach;

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
import jakarta.data.exceptions.MappingException;
import jakarta.data.repository.KeysetAwareSlice;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.Slice;
import jakarta.data.repository.Sort;
import jakarta.inject.Inject;

/**
 * Execute a test with an entity that is dual annotated which means this test
 * can run against a provider that supports any Entity type.
 */
@Standalone
@AnyEntity
@ReadOnlyTest
public class EntityTest {

    public static final Logger log = Logger.getLogger(EntityTest.class.getCanonicalName());

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class).addClasses(EntityTest.class);
    }

    @Inject
    NaturalNumbers numbers;

    @Inject
    AsciiCharacters characters;
    
    @BeforeEach //Inject doesn't happen until after BeforeClass so this is necessary before each test
    public void setup() {
        assertNotNull(numbers);
        NaturalNumbersPopulator.get().populate(numbers);
        
        assertNotNull(characters);
        AsciiCharactersPopulator.get().populate(characters);
    }

    @Assertion(id = "136", strategy = "Ensures that the prepopulation step for readonly entities was successful")
    public void ensureNaturalNumberPrepopulation() {
        assertEquals(100L, numbers.count());
        assertTrue(numbers.findById(0L).isEmpty(), "Zero should not have been in the set of natural numbers.");
        assertFalse(numbers.findById(10L).get().isOdd());
    }

    @Assertion(id = "136", strategy = "Ensures that multiple readonly entities will be prepopulated before testing")
    public void ensureCharacterPrepopulation() {
        assertEquals(128L, characters.count());
        assertEquals('0', characters.findByNumericValue(48).get().getThisCharacter());
        assertTrue(characters.findById(1L).get().isControl());
    }

    @Assertion(id = "133", strategy = "Request a Slice higher than the final Slice, expecting an empty Slice with 0 results.")
    public void testBeyondFinalSlice() {
        Pageable sixth = Pageable.ofSize(5).sortBy(Sort.desc("id")).page(6);
        Slice<NaturalNumber> slice = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(NumberType.PRIME, 8L,
                sixth);
        assertEquals(0, slice.numberOfElements());
        assertEquals(0, slice.stream().count());
        assertEquals(false, slice.hasContent());
        assertEquals(false, slice.iterator().hasNext());
    }

    @Assertion(id = "133",
               strategy = "Use a repository method with one Sort parameter specifying descending order, " +
                          "and verify all results are returned and are in descending order according to the sort criteria.")
    public void testDescendingSort() {
        Stream<NaturalNumber> stream = numbers.findByIdBetween(4, 10, Sort.desc("id"));

        assertEquals(Arrays.toString(new Long[] { 10L, 9L, 8L, 7L, 6L, 5L, 4L }),
                     Arrays.toString(stream.map(number -> number.getId()).toArray()));
    }

    @Assertion(id = "133", strategy = "Request the last Slice of up to 5 results, expecting to find the final 2.")
    public void testFinalSliceOfUpTo5() {
        Pageable fifth = Pageable.ofSize(5).page(5).sortBy(Sort.desc("id"));
        Slice<NaturalNumber> slice = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(NumberType.PRIME, 8L,
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

    @Assertion(id = "133",
               strategy = "Request the first KeysetAwareSlice of 6 results, expecting to find all 6, " +
                          "then request the next KeysetAwareSlice and the KeysetAwareSlice after that, " +
                          "expecting to find all results.")
    public void testFirstKeysetAwareSliceOf6AndNextSlices() {
        Pageable first6 = Pageable.ofSize(6);
        KeysetAwareSlice<NaturalNumber> slice;

        try {
            slice = numbers.findByFloorOfSquareRootOrderByIdAsc(7L, first6);
        } catch (MappingException x) {
            // Test passes: Jakarta Data providers must raise MappingException when the database
            // is not capable of keyset pagination.
            return;
        }

        assertEquals(Arrays.toString(new Long[] { 49L, 50L, 51L, 52L, 53L, 54L }),
                     Arrays.toString(slice.stream().map(number -> number.getId()).toArray()));

        assertEquals(6, slice.numberOfElements());

        try {
            slice = numbers.findByFloorOfSquareRootOrderByIdAsc(7L, slice.nextPageable());
        } catch (MappingException x) {
            // Test passes: Jakarta Data providers must raise MappingException when the database
            // is not capable of keyset pagination.
            return;
        }

        assertEquals(6, slice.numberOfElements());

        assertEquals(Arrays.toString(new Long[] { 55L, 56L, 57L, 58L, 59L, 60L }),
                     Arrays.toString(slice.stream().map(number -> number.getId()).toArray()));

        try {
            slice = numbers.findByFloorOfSquareRootOrderByIdAsc(7L, slice.nextPageable());
        } catch (MappingException x) {
            // Test passes: Jakarta Data providers must raise MappingException when the database
            // is not capable of keyset pagination.
            return;
        }

        assertEquals(Arrays.toString(new Long[] { 61L, 62L, 63L }),
                     Arrays.toString(slice.stream().map(number -> number.getId()).toArray()));

        assertEquals(3, slice.numberOfElements());
    }

    @Assertion(id = "133", strategy = "Request the first Slice of 5 results, expecting to find all 5.")
    public void testFirstSliceOf5() {
        Pageable first5 = Pageable.ofSize(5).sortBy(Sort.desc("id"));
        Slice<NaturalNumber> slice = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(NumberType.PRIME, 8L,
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

    @Assertion(id = "133",
            strategy = "Request a KeysetAwareSlice of 9 results after the keyset of the 20th result, expecting to find the next 9 results. " +
                       "Then request the KeysetAwareSlice before the keyset of the first entry of the slice, expecting to find the previous 9 results. " +
                       "Then request the KeysetAwareSlice after the last entry of the original slice, expecting to find the next 9.")
    public void testKeysetAwareSliceOf9FromCursor() {
        // The query for this test returns composite natural numbers under 64 in the following order:
        //
        // 49 50 51 52 54 55 56 57 58 60 62 63 36 38 39 40 42 44 45 46 48 25 26 27 28 30 32 33 34 35 16 18 20 21 22 24 09 10 12 14 15 04 06 08
        //                                                             ^^^^^^^^ slice 1 ^^^^^^^^^
        //                                  ^^^^^^^^ slice 2 ^^^^^^^^^
        //                                                                                        ^^^^^^^^ slice 3 ^^^^^^^^^

        Pageable middle9 = Pageable.ofSize(9)
                             .sortBy(Sort.desc("floorOfSquareRoot"), Sort.asc("id"))
                             .afterKeyset(6L, 46L); // 20th result is 46; its square root rounds down to 6.

        KeysetAwareSlice<NaturalNumber> slice;
        try {
            slice = numbers.findByNumTypeAndNumBitsRequiredLessThan(NumberType.COMPOSITE, (short) 7, middle9);
        } catch (MappingException x) {
            // Test passes: Jakarta Data providers must raise MappingException when the database
            // is not capable of keyset pagination.
            return;
        }

        assertEquals(Arrays.toString(new Long[] { 48L, 25L, 26L, 27L, 28L, 30L, 32L, 33L, 34L }),
                     Arrays.toString(slice.stream().map(number -> number.getId()).toArray()));

        assertEquals(9, slice.numberOfElements());

        KeysetAwareSlice<NaturalNumber> previousSlice;
        try {
            previousSlice = numbers.findByNumTypeAndNumBitsRequiredLessThan(NumberType.COMPOSITE,
                                                                            (short) 7,
                                                                            slice.previousPageable());
        } catch (MappingException x) {
            // Test passes: Jakarta Data providers must raise MappingException when the database
            // is not capable of keyset pagination.
            return;
        }

         assertEquals(Arrays.toString(new Long[] { 63L, 36L, 38L, 39L, 40L, 42L, 44L, 45L, 46L }),
                      Arrays.toString(previousSlice.stream().map(number -> number.getId()).toArray()));

         assertEquals(9, previousSlice.numberOfElements());

         KeysetAwareSlice<NaturalNumber> nextSlice;
         try {
             nextSlice = numbers.findByNumTypeAndNumBitsRequiredLessThan(NumberType.COMPOSITE,
                                                                         (short) 7,
                                                                         slice.nextPageable());
         } catch (MappingException x) {
             // Test passes: Jakarta Data providers must raise MappingException when the database
             // is not capable of keyset pagination.
             return;
         }

         assertEquals(Arrays.toString(new Long[] { 35L, 16L, 18L, 20L, 21L, 22L, 24L, 9L, 10L }),
                      Arrays.toString(nextSlice.stream().map(number -> number.getId()).toArray()));

         assertEquals(9, nextSlice.numberOfElements());
    }

    @Assertion(id = "133", strategy = "Request a KeysetAwareSlice of results where none match the query, expecting an empty KeysetAwareSlice with 0 results.")
    public void testKeysetAwareSliceOfNothing() {
        // There are no numbers larger than 30 which have a square root that rounds down to 3.
        Pageable pagination = Pageable.ofSize(33).afterKeyset(30L);

        KeysetAwareSlice<NaturalNumber> slice;
        try {
            slice = numbers.findByFloorOfSquareRootOrderByIdAsc(3L, pagination);
        } catch (MappingException x) {
            // Test passes: Jakarta Data providers must raise MappingException when the database
            // is not capable of keyset pagination.
            return;
        }

        assertEquals(false, slice.hasContent());
        assertEquals(0, slice.content().size());
        assertEquals(0, slice.numberOfElements());
    }

    @Assertion(id = "133",
               strategy = "Use a repository method with two Sort parameters specifying a mixture of ascending and descending order, " +
                          "and verify all results are returned and are ordered according to the sort criteria.")
    public void testMixedSort() {
        NaturalNumber[] nums = numbers.findByIdLessThan(15L, Sort.asc("numBitsRequired"), Sort.desc("id"));

        assertEquals(Arrays.toString(new Long[] { 1L, // 1 bit
                                                  3L, 2L, // 2 bits
                                                  7L, 6L, 5L, 4L, // 3 bits
                                                  14L, 13L, 12L, 11L, 10L, 9L, 8L }), // 4 bits
                     Arrays.toString(Stream.of(nums).map(number -> number.getId()).toArray()));
    }

    @Assertion(id = "133", strategy = "Request a Slice of results where none match the query, expecting an empty Slice with 0 results.")
    public void testSliceOfNothing() {
        Pageable pagination = Pageable.ofSize(5).sortBy(Sort.desc("id"));
        Slice<NaturalNumber> slice = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(NumberType.COMPOSITE, 1L,
                pagination);

        assertEquals(false, slice.hasContent());
        assertEquals(0, slice.content().size());
        assertEquals(0, slice.numberOfElements());
    }

    @Assertion(id = "133", 
               strategy = "Request the third Slice of 5 results, expecting to find all 5. "
                       +  "Request the next Slice via nextPageable, expecting page number 4 and another 5 results.")
    public void testThirdAndFourthSlicesOf5() {
        Pageable third5 = Pageable.ofPage(3).size(5).sortBy(Sort.desc("id"));
        Slice<NaturalNumber> slice = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(NumberType.PRIME, 8L,
                third5);

        assertEquals(3, slice.pageable().page());
        assertEquals(5, slice.numberOfElements());

        assertEquals(Arrays.toString(new Long[] { 37L, 31L, 29L, 23L, 19L }),
                Arrays.toString(slice.stream().map(number -> number.getId()).toArray()));

        Pageable fourth5 = third5.next();

        slice = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(NumberType.PRIME, 8L, fourth5);

        assertEquals(4, slice.pageable().page());
        assertEquals(5, slice.numberOfElements());

        assertEquals(Arrays.toString(new Long[] { 17L, 13L, 11L, 7L, 5L }),
                Arrays.toString(slice.stream().map(number -> number.getId()).toArray()));
    }

    @Assertion(id = "133",
               strategy = "Use a repository method with varargs Sort... specifying a mixture of ascending and descending order, " +
                          "and verify all results are returned and are ordered according to the sort criteria.")
    public void testVarargsSort() {
        List<NaturalNumber> list = numbers.findByIdLessThanEqual(12L,
                                                                 Sort.asc("floorOfSquareRoot"),
                                                                 Sort.desc("numBitsRequired"),
                                                                 Sort.asc("id"));

        assertEquals(Arrays.toString(new Long[] { 2L, 3L, // square root rounds down to 1; 2 bits
                                                  1L, // square root rounds down to 1; 1 bit
                                                  8L, // square root rounds down to 2; 4 bits
                                                  4L, 5L, 6L, 7L, // square root rounds down to 2; 3 bits
                                                  9L, 10L, 11L, 12L }), // square root rounds down to 3; 4 bits
                     Arrays.toString(list.stream().map(number -> number.getId()).toArray()));
    }
}
