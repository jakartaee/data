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
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.BeforeEach;

import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.ReadOnlyTest;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.framework.read.only.AsciiCharacter;
import ee.jakarta.tck.data.framework.read.only.AsciiCharacters;
import ee.jakarta.tck.data.framework.read.only.AsciiCharactersPopulator;
import ee.jakarta.tck.data.framework.read.only.NaturalNumber;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbers;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbersPopulator;
import ee.jakarta.tck.data.framework.read.only.PositiveIntegers;
import ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType;
import jakarta.data.exceptions.EmptyResultException;
import jakarta.data.exceptions.MappingException;
import jakarta.data.exceptions.NonUniqueResultException;
import jakarta.data.repository.KeysetAwareSlice;
import jakarta.data.repository.Limit;
import jakarta.data.repository.Page;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.Slice;
import jakarta.data.repository.Sort;
import jakarta.data.repository.Streamable;
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
    PositiveIntegers positives; // shares same read-only data with NaturalNumbers

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
        assertEquals(127, characters.countByHexadecimalNotNull());
        assertEquals('0', characters.findByNumericValue(48).get().getThisCharacter());
        assertTrue(characters.findByNumericValue(1).get().isControl());
    }

    @Assertion(id = "133", strategy = "Request a Page higher than the final Page, expecting an empty Page with 0 results.")
    public void testBeyondFinalPage() {
        Pageable sixth = Pageable.ofPage(6).sortBy(Sort.asc("numericValue")).size(10);
        Page<AsciiCharacter> page;
        try {
            page = characters.findByNumericValueBetween(48, 90, sixth);
        } catch (UnsupportedOperationException x) {
            // Some NoSQL databases lack the ability to count the total results
            // and therefore cannot support a return type of Page
            return;
        }
        assertEquals(0, page.numberOfElements());
        assertEquals(0, page.stream().count());
        assertEquals(false, page.hasContent());
        assertEquals(false, page.iterator().hasNext());
        assertEquals(43L, page.totalElements());
        assertEquals(5L, page.totalPages());
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
               strategy = "Use a repository that inherits from CrudRepository and adds some methods of its own. " +
                          "Use both built-in methods and the additional methods.")
    public void testCrudRepository() {
        assertEquals(false, numbers.existsById(0L));
        assertEquals(true, numbers.existsById(80L));

        Stream<NaturalNumber> found;
        found = numbers.findAllById(List.of(70L, 40L, -20L, 10L));
        assertEquals(List.of(10L, 40L, 70L),
                     found.map(NaturalNumber::getId).sorted().collect(Collectors.toList()));

        found = numbers.findByIdBetween(50L, 59L, Sort.asc("numType"));
        List<Long> list = found.map(NaturalNumber::getId).collect(Collectors.toList());
        assertEquals(Set.of(53L, 59L), // first 2 must be primes
                     new TreeSet<>(list.subList(0, 2)));
        assertEquals(Set.of(50L, 51L, 52L, 54L, 55L, 56L, 57L, 58L), // the remaining 8 are composite numbers
                     new TreeSet<>(list.subList(2, 10)));
    }

    @Assertion(id = "133", strategy = "Use a repository that inherits from DataRepository and defines all of its own methods.")
    public void testDataRepository() {
        AsciiCharacter del = characters.findByIsControlTrueAndNumericValueBetween(33, 127);
        assertEquals(127, del.getNumericValue());
        assertEquals("7f", del.getHexadecimal());
        assertEquals(true, del.isControl());

        AsciiCharacter j = characters.findByHexadecimalIgnoreCase("6A");
        assertEquals("6a", j.getHexadecimal());
        assertEquals('j', j.getThisCharacter());
        assertEquals(106, j.getNumericValue());
        assertEquals(false, j.isControl());

        AsciiCharacter d = characters.findByNumericValue(100).orElseThrow();
        assertEquals(100, d.getNumericValue());
        assertEquals('d', d.getThisCharacter());
        assertEquals("64", d.getHexadecimal());
        assertEquals(false, d.isControl());

        assertEquals(true, characters.existsByThisCharacter('D'));
    }

    @Assertion(id = "133",
               strategy = "Use a repository method with one Sort parameter specifying descending order, " +
                          "and verify all results are returned and are in descending order according to the sort criteria.")
    public void testDescendingSort() {
        Stream<NaturalNumber> stream = numbers.findByIdBetween(4, 10, Sort.desc("id"));

        assertEquals(Arrays.toString(new Long[] { 10L, 9L, 8L, 7L, 6L, 5L, 4L }),
                     Arrays.toString(stream.map(number -> number.getId()).toArray()));
    }

    @Assertion(id = "133", strategy = "Use a repository method that returns a single entity value where no result is found. Expect EmptyResultException.")
    public void testEmptyResultException() {
        try {
            AsciiCharacter ch = characters.findByHexadecimalIgnoreCase("2g");
            fail("Unexpected result of findByHexadecimalIgnoreCase(2g): " + ch.getHexadecimal());
        } catch (EmptyResultException x) {
            System.out.println("testEmptyResultException expected to catch exception " + x + ". Printing its stack trace:");
            x.printStackTrace(System.out);
            // test passes
        }
    }

    @Assertion(id = "133", strategy = "Request the last Page of up to 10 results, expecting to find the final 3.")
    public void testFinalPageOfUpTo10() {
        Pageable fifthPageRequest = Pageable.ofSize(10).page(5).sortBy(Sort.asc("numericValue"));
        Page<AsciiCharacter> page;
        try {
            page = characters.findByNumericValueBetween(48, 90, fifthPageRequest); // 'X' to 'Z'
        } catch (UnsupportedOperationException x) {
            // Some NoSQL databases lack the ability to count the total results
            // and therefore cannot support a return type of Page
            return;
        }

        Iterator<AsciiCharacter> it = page.iterator();

        // first result
        assertEquals(true, it.hasNext());
        AsciiCharacter ch = it.next();
        assertEquals('X', ch.getThisCharacter());
        assertEquals("58", ch.getHexadecimal());
        assertEquals(88L, ch.getId());
        assertEquals(88, ch.getNumericValue());
        assertEquals(false, ch.isControl());

        // second result
        ch = it.next();
        assertEquals('Y', ch.getThisCharacter());
        assertEquals("59", ch.getHexadecimal());
        assertEquals(89L, ch.getId());
        assertEquals(89, ch.getNumericValue());
        assertEquals(false, ch.isControl());

        // third result
        ch = it.next();
        assertEquals('Z', ch.getThisCharacter());
        assertEquals("5a", ch.getHexadecimal());
        assertEquals(90L, ch.getId());
        assertEquals(90, ch.getNumericValue());
        assertEquals(false, ch.isControl());

        assertEquals(false, it.hasNext());

        assertEquals(5, page.pageable().page());
        assertEquals(true, page.hasContent());
        assertEquals(3, page.numberOfElements());
        assertEquals(43L, page.totalElements());
        assertEquals(5L, page.totalPages());
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
               strategy = "Use the findAll method of a repository that inherits from PageableRepository " +
                          "to request a Page 2 of size 12, specifying a Pageable that requires a mixture of " +
                          "ascending and descending sort. Verify the page contains all 12 expected entities, " +
                          "sorted according to the mixture of ascending and descending sort orders specified.")
    public void testFindAllWithPagination() {
        Pageable page2request = Pageable.ofPage(2).size(12).sortBy(Sort.asc("floorOfSquareRoot"), Sort.desc("id"));
        Page<NaturalNumber> page2 = positives.findAll(page2request);

        assertEquals(12, page2.numberOfElements());
        assertEquals(2, page2.pageable().page());

        assertEquals(List.of(11L, 10L, 9L, // square root rounds down to 3
                             24L, 23L, 22L, 21L, 20L, 19L, 18L, 17L, 16L), // square root rounds down to 4
                     page2.stream().map(n -> n.getId()).collect(Collectors.toList()));
    }

    @Assertion(id = "133",
               strategy = "Use a repository method with findFirstBy that returns the first entity value " +
                          "where multiple results would otherwise be found.")
    public void testFindFirst() {
        Optional<AsciiCharacter> none = characters.findFirstByHexadecimalStartsWithAndIsControlOrderByIdAsc("h", false);
        assertEquals(true, none.isEmpty());

        AsciiCharacter ch = characters.findFirstByHexadecimalStartsWithAndIsControlOrderByIdAsc("4", false)
                        .orElseThrow();
        assertEquals('@', ch.getThisCharacter());
        assertEquals("40", ch.getHexadecimal());
        assertEquals(64, ch.getNumericValue());
    }

    @Assertion(id = "133",
               strategy = "Use a repository method with findFirst3By that returns the first 3 results.")
    public void testFindFirst3() {
        AsciiCharacter[] found = characters.findFirst3ByNumericValueGreaterThanEqualAndHexadecimalEndsWith(40, "4", Sort.asc("numericValue"));
        assertEquals(3, found.length);
        assertEquals('4', found[0].getThisCharacter());
        assertEquals('D', found[1].getThisCharacter());
        assertEquals('T', found[2].getThisCharacter());
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

    @Assertion(id = "133",
               strategy = "Request the first Page of 10 results, expecting to find all 10. " +
                          "From the Page, verify the totalElements and totalPages expected.")
    public void testFirstPageOf10() {
        Pageable first10 = Pageable.ofSize(10).sortBy(Sort.asc("numericValue"));
        Page<AsciiCharacter> page;
        try {
            page = characters.findByNumericValueBetween(48, 90, first10); // '0' to 'Z'
        } catch (UnsupportedOperationException x) {
            // Some NoSQL databases lack the ability to count the total results
            // and therefore cannot support a return type of Page
            return;
        }

        assertEquals(1, page.pageable().page());
        assertEquals(true, page.hasContent());
        assertEquals(10, page.numberOfElements());
        assertEquals(43L, page.totalElements());
        assertEquals(5L, page.totalPages());

        assertEquals("30:0;31:1;32:2;33:3;34:4;35:5;36:6;37:7;38:8;39:9;", // '0' to '9'
        page.stream()
                   .map(c -> c.getHexadecimal() + ':' + c.getThisCharacter() + ';')
                   .reduce("", String::concat));
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

    @Assertion(id = "133", strategy = "Use a repository method existsByIdGreaterThan confirming the correct boolean is returned.")
    public void testGreaterThanEqualExists() {
        assertEquals(true, positives.existsByIdGreaterThan(0L));
        assertEquals(true, positives.existsByIdGreaterThan(99L));
        assertEquals(false, positives.existsByIdGreaterThan(100L)); // doesn't exist because the table only has 1 to 100
    }

    @Assertion(id = "133", strategy = "Use a repository method with the In keyword.")
    public void testIn() {
        Stream<NaturalNumber> nonPrimes = positives.findByNumTypeInOrderByIdAsc(Set.of(NumberType.COMPOSITE, NumberType.ONE),
                                                                                Limit.of(9));
        assertEquals(List.of(1L, 4L, 6L, 8L, 9L, 10L, 12L, 14L, 15L),
                     nonPrimes.map(NaturalNumber::getId).collect(Collectors.toList()));

        Stream<NaturalNumber> primes = positives.findByNumTypeInOrderByIdAsc(Collections.singleton(NumberType.PRIME),
                                                                             Limit.of(6));
        assertEquals(List.of(2L, 3L, 5L, 7L, 11L, 13L),
                     primes.map(NaturalNumber::getId).collect(Collectors.toList()));
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

    @Assertion(id = "133", strategy = "Use a repository method countByIdLessThan confirming the correct count is returned.")
    public void testLessThanWithCount() {
        assertEquals(91L, positives.countByIdLessThan(92L));

        assertEquals(0L, positives.countByIdLessThan(1L));
    }

    @Assertion(id = "133", strategy = "Use a repository method with both Sort and Limit, and verify that the Limit caps " +
                                      "the number of results and that results are ordered according to the sort criteria.")
    public void testLimit() {
        Collection<NaturalNumber> nums = numbers.findByIdGreaterThanEqual(60L,
                                                        Limit.of(10),
                                                        Sort.asc("floorOfSquareRoot"),
                                                        Sort.desc("id"));

        assertEquals(Arrays.toString(new Long[] { 63L, 62L, 61L, 60L, // square root rounds down to 7
                                80L, 79L, 78L, 77L, 76L, 75L }), // square root rounds down to 8
        Arrays.toString(nums.stream().map(number -> number.getId()).toArray()));
    }

    @Assertion(id = "133", strategy = "Use a repository method with both Sort and Limit, where the Limit is a range, " +
                                      " and verify that the Limit range starts in the correct place, caps the number of results, " +
                                      " and that results are ordered according to the sort criteria.")
    public void testLimitedRange() {
        // Primes above 40 are:
        // 41, 43, 47, 53, 59,
        // 61, 67, 71, 73, 79,
        // 83, 89, ...

        Collection<NaturalNumber> nums = numbers.findByIdGreaterThanEqual(40L,
                                                        Limit.range(6, 10),
                                                        Sort.asc("numType"), // primes first
                                                        Sort.asc("id"));

        assertEquals(Arrays.toString(new Long[] { 61L, 67L, 71L, 73L, 79L }),
        Arrays.toString(nums.stream().map(number -> number.getId()).toArray()));
    }

    @Assertion(id = "133", strategy = "Use a repository method with Limit and verify that the Limit caps " +
                                      "the number of results to the amount that is specified.")
    public void testLimitToOneResult() {
        Collection<NaturalNumber> nums = numbers.findByIdGreaterThanEqual(80L, Limit.of(1));

        Iterator<NaturalNumber> it = nums.iterator();
        assertEquals(true, it.hasNext());

        NaturalNumber num = it.next();
        assertEquals(true, num.getId() >= 80L);

        assertEquals(false, it.hasNext());
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

    @Assertion(id = "133",
               strategy = "Use a repository method that ought to return a single entity value but where multiple results are found." +
                          "Expect NonUniqueResultException.")
    public void testNonUniqueResultException() {
        try {
            AsciiCharacter ch = characters.findByIsControlTrueAndNumericValueBetween(10, 15);
            fail("Unexpected result of findByIsControlTrueAndNumericValueBetween(10, 15): " + ch.getHexadecimal());
        } catch (NonUniqueResultException x) {
            System.out.println("testNonUniqueResultException expected to catch exception " + x + ". Printing its stack trace:");
            x.printStackTrace(System.out);
            // test passes
        }
    }

    @Assertion(id = "133", strategy = "Use a repository method with Or, expecting MappingException if the underlying database is not capable.")
    public void testOr() {
        Stream<NaturalNumber> found;
        try {
            found = positives.findByNumTypeOrFloorOfSquareRoot(NumberType.ONE, 2L);
        } catch (MappingException x) {
            // Test passes: Jakarta Data providers must raise MappingException when the database
            // is not capable of the OR operation.
            return;
        }

        assertEquals(List.of(1L, 4L, 5L, 6L, 7L, 8L),
                     found.map(NaturalNumber::getId).sorted().collect(Collectors.toList()));
    }

    @Assertion(id = "133",
               strategy = "Use a repository method with OrderBy (static) and a Sort parameter (dynamic), " +
                          "verfying that all results are returned and are ordered first by the static sort criteria, " +
                          "followed by the dynamic sort criteria when the value(s) being compared by the static criteria match.")
    public void testOrderByHasPrecedenceOverPageableSorts() {
        Pageable pagination = Pageable.ofSize(8).sortBy(Sort.asc("numType"), Sort.desc("id"));
        Slice<NaturalNumber> slice = numbers.findByIdLessThanOrderByFloorOfSquareRootDesc(25L, pagination);

        assertEquals(Arrays.toString(new Long[] { 23L, 19L, 17L, // square root rounds down to 4; prime
                                                  24L, 22L, 21L, 20L, 18L }), // square root rounds down to 4; composite
                     Arrays.toString(slice.stream().map(number -> number.getId()).toArray()));

        pagination = slice.nextPageable();
        slice = numbers.findByIdLessThanOrderByFloorOfSquareRootDesc(25L, pagination);

        assertEquals(Arrays.toString(new Long[] { 16L, // square root rounds down to 4; composite
                                                  13L, 11L, // square root rounds down to 3; prime
                                                  15L, 14L, 12L, 10L, 9L }), // square root rounds down to 3; composite
                     Arrays.toString(slice.stream().map(number -> number.getId()).toArray()));

        pagination = slice.nextPageable();
        slice = numbers.findByIdLessThanOrderByFloorOfSquareRootDesc(25L, pagination);

        assertEquals(Arrays.toString(new Long[] { 7L, 5L, // square root rounds down to 2; prime
                                                  8L, 6L, 4L, // square root rounds down to 2; composite
                                                  1L, // square root rounds down to 1; one
                                                  3L, 2L }), // square root rounds down to 1; prime
                     Arrays.toString(slice.stream().map(number -> number.getId()).toArray()));

        pagination = slice.nextPageable();
        if (pagination != null) {
            slice = numbers.findByIdLessThanOrderByFloorOfSquareRootDesc(25L, pagination);
            assertEquals(false, slice.hasContent());
        }
    }

    @Assertion(id = "133",
               strategy = "Use a repository method with OrderBy (static) and a Pageable with a Sort parameter (dynamic), " +
                          "verfying that all results are returned and are ordered first by the static sort criteria, " +
                          "followed by the dynamic sort criteria when the value(s) being compared by the static criteria match.")
    public void testOrderByHasPrecedenceOverSorts() {
        Stream<NaturalNumber> nums = numbers.findByIdBetweenOrderByNumTypeAsc(5L, 24L,
                                                                              Sort.desc("floorOfSquareRoot"),
                                                                              Sort.asc("id"));

        assertEquals(Arrays.toString(new Long[] { 17L, 19L, 23L, // prime; square root rounds down to 4
                                                  11L, 13L, // prime; square root rounds down to 3
                                                  5L, 7L, // prime; square root rounds down to 2
                                                  16L, 18L, 20L, 21L, 22L, 24L, // composite; square root rounds down to 4
                                                  9L, 10L, 12L, 14L, 15L, // composite; square root rounds down to 3
                                                  6L, 8L }), // composite; square root rounds down to 2
                     Arrays.toString(nums.map(number -> number.getId()).toArray()));
    }

    @Assertion(id = "133", strategy = "Request a Page of results where none match the query, expecting an empty Page with 0 results.")
    public void testPageOfNothing() {
        Pageable pagination = Pageable.ofSize(6).sortBy(Sort.asc("id"));
        Page<AsciiCharacter> page;
        try {
            page = characters.findByNumericValueBetween(150, 160, pagination);
        } catch (UnsupportedOperationException x) {
            // Some NoSQL databases lack the ability to count the total results
            // and therefore cannot support a return type of Page
            return;
        }

        assertEquals(0, page.numberOfElements());
        assertEquals(0, page.stream().count());
        assertEquals(0, page.content().size());
        assertEquals(false, page.hasContent());
        assertEquals(false, page.iterator().hasNext());
        assertEquals(0L, page.totalElements());
        assertEquals(0L, page.totalPages());
    }

    @Assertion(id = "133", strategy = "Use a repository method that returns a single entity value where a single result is found.")
    public void testSingleEntity() {
        AsciiCharacter ch = characters.findByHexadecimalIgnoreCase("2B");
        assertEquals('+', ch.getThisCharacter());
        assertEquals("2b", ch.getHexadecimal());
        assertEquals(43, ch.getNumericValue());
        assertEquals(false, ch.isControl());
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

    @Assertion(id = "133", strategy = "Use a repository method that returns Streamable and verify the results.")
    public void testStreamable() {
        Streamable<AsciiCharacter> chars = characters.findByNumericValueLessThanEqualAndNumericValueGreaterThanEqual(109, 101);

        assertEquals(Arrays.toString(new Character[] { Character.valueOf('e'),
                                                       Character.valueOf('f'),
                                                       Character.valueOf('g'),
                                                       Character.valueOf('h'),
                                                       Character.valueOf('i'),
                                                       Character.valueOf('j'),
                                                       Character.valueOf('k'),
                                                       Character.valueOf('l'),
                                                       Character.valueOf('m') }),
                     Arrays.toString(chars.stream().map(ch -> ch.getThisCharacter()).sorted().toArray()));

        assertEquals(101 + 102 + 103 + 104 + 105 + 106 + 107 + 108 + 109,
                     chars.stream().mapToInt(AsciiCharacter::getNumericValue).sum());

        Set<String> sorted = new TreeSet<>();
        chars.forEach(ch -> sorted.add(ch.getHexadecimal()));
        assertEquals(new TreeSet<>(Set.of("65", "66", "67", "68", "69", "6a", "6b", "6c", "6d")),
                     sorted);

        Streamable<AsciiCharacter> empty = characters.findByNumericValueLessThanEqualAndNumericValueGreaterThanEqual(115, 120);
        assertEquals(false, empty.iterator().hasNext());
        assertEquals(0L, empty.stream().count());
    }

    @Assertion(id = "133",
               strategy = "Request the third Page of 10 results, expecting to find all 10. " +
                          "Request the next Page via nextPageable, expecting page number 4 and another 10 results.")
    public void testThirdAndFourthPagesOf10() {
        Pageable third10 = Pageable.ofPage(3).size(10).sortBy(Sort.asc("numericValue"));
        Page<AsciiCharacter> page;
        try {
            page = characters.findByNumericValueBetween(48, 90, third10); // 'D' to 'M'
        } catch (UnsupportedOperationException x) {
            // Some NoSQL databases lack the ability to count the total results
            // and therefore cannot support a return type of Page
            return;
        }

        assertEquals(3, page.pageable().page());
        assertEquals(true, page.hasContent());
        assertEquals(10, page.numberOfElements());
        assertEquals(43L, page.totalElements());
        assertEquals(5L, page.totalPages());

        assertEquals("44:D;45:E;46:F;47:G;48:H;49:I;4a:J;4b:K;4c:L;4d:M;",
                     page.stream()
                                     .map(c -> c.getHexadecimal() + ':' + c.getThisCharacter() + ';')
                                     .reduce("", String::concat));

        Pageable fourth10 = third10.next();
        page = characters.findByNumericValueBetween(48, 90, fourth10); // 'N' to 'W'

        assertEquals(4, page.pageable().page());
        assertEquals(true, page.hasContent());
        assertEquals(10, page.numberOfElements());
        assertEquals(43L, page.totalElements());
        assertEquals(5L, page.totalPages());

        assertEquals("4e:N;4f:O;50:P;51:Q;52:R;53:S;54:T;55:U;56:V;57:W;",
                     page.stream()
                                     .map(c -> c.getHexadecimal() + ':' + c.getThisCharacter() + ';')
                                     .reduce("", String::concat));
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
