/*
 * Copyright (c) 2023,2026 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.standalone.entity.qbmn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.data.Limit;
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.exceptions.EmptyResultException;
import jakarta.data.exceptions.NonUniqueResultException;
import jakarta.data.page.CursoredPage;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.page.PageRequest.Cursor;
import jakarta.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;

import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.QueryByMethodName;
import ee.jakarta.tck.data.framework.junit.anno.ReadOnlyTest;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.framework.read.only.AsciiCharacter;
import ee.jakarta.tck.data.framework.read.only.AsciiCharacters;
import ee.jakarta.tck.data.framework.read.only.AsciiCharactersPopulator;
import ee.jakarta.tck.data.framework.read.only.qbmn.CustomRepository;
import ee.jakarta.tck.data.framework.read.only.NaturalNumber;
import ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbers;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbersPopulator;
import ee.jakarta.tck.data.framework.read.only._AsciiChar;
import ee.jakarta.tck.data.framework.read.only._AsciiCharacter;
import ee.jakarta.tck.data.framework.read.only.qbmn.AsciiCharactersByName;
import ee.jakarta.tck.data.framework.read.only.qbmn.NaturalNumbersByName;
import ee.jakarta.tck.data.framework.read.only.qbmn.PositiveIntegersByName;
import ee.jakarta.tck.data.framework.utilities.DatabaseType;
import ee.jakarta.tck.data.framework.utilities.TestProperty;

/**
 * Tests that use query-by-method-name repositories with entities that are
 * dual annotated, which means this test can run against a provider that
 * supports any Entity type.
 */
@Standalone
@AnyEntity
@ReadOnlyTest
@QueryByMethodName
public class EntityQueryByMethodNameTests {

    public static final Logger log = Logger.getLogger(EntityQueryByMethodNameTests.class.getCanonicalName());

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(EntityQueryByMethodNameTests.class);
    }

    @Inject
    NaturalNumbersByName numbersByName;

    @Inject
    AsciiCharactersByName charactersByName;

    @Inject
    PositiveIntegersByName positivesByName;

    @Inject
    CustomRepository customRepo;

    @Inject
    NaturalNumbers numbers; // for population only

    @Inject
    AsciiCharacters characters; // for population only

    private final DatabaseType type = TestProperty.databaseType.getDatabaseType();

    @BeforeEach
    @Assertion(id = "136", strategy = "Ensures that the prepopulation step for readonly entities was successful")
    public void ensureNaturalNumberPrepopulation() {
        NaturalNumbersPopulator.get().populate(numbers);  // Uses annotation-based methods
        assertEquals(100L, numbers.countAll());
        assertTrue(numbers.findById(0L).isEmpty(), "Zero should not have been in the set of natural numbers.");
        assertFalse(numbers.findById(10L).get().isOdd());
    }

    @BeforeEach
    @Assertion(id = "136", strategy = "Ensures that multiple readonly entities will be prepopulated before testing")
    public void ensureCharacterPrepopulation() {
        AsciiCharactersPopulator.get().populate(characters);  // Uses annotation-based methods
        try {
            assertEquals(127L, charactersByName.countByHexadecimalNotNull());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfNotNull()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals('0', charactersByName.findByNumericValue(48).get().getThisCharacter());
        assertTrue(charactersByName.findByNumericValue(1).get().isControl());
    }

    @Assertion(id = "133",
            strategy = "Use a repository that inherits from BasicRepository and adds some methods of its own. " +
                    "Use both built-in methods and the additional methods.")
    public void testBasicRepository() {

        // custom method from NaturalNumbers:
        try {
            Stream<NaturalNumber> found = numbersByName.findByIdBetweenOrderByNumTypeOrdinalAsc(
                    50L, 59L,
                    Order.by(Sort.asc("id")));
            List<Long> list = found
                    .map(NaturalNumber::getId)
                    .collect(Collectors.toList());
            assertEquals(List.of(53L, 59L, // first 2 must be primes
                            50L, 51L, 52L, 54L, 55L, 56L, 57L, 58L), // the remaining 8 are composite numbers
                    list);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfBetween() &&
                type.capableOfMultipleSort()) {
                throw x;
            }
        }

        // built-in method from BasicRepository:
        assertEquals(60L, numbers.findById(60L).orElseThrow().getId());
    }

    @Assertion(id = "133", strategy = "Request a Page higher than the final Page, expecting an empty Page with 0 results.")
    public void testBeyondFinalPage() {
        PageRequest sixth = PageRequest.ofPage(6).size(10);
        Page<AsciiCharacter> page;
        try {
            page = charactersByName.findByNumericValueBetween(48, 90, sixth, Order.by(_AsciiCharacter.numericValue.asc()));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfBetween() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }
        assertEquals(0, page.numberOfElements());
        assertEquals(0, page.stream().count());
        assertFalse(page.hasContent());
        assertFalse(page.iterator().hasNext());
        try {
            assertEquals(43L, page.totalElements());
            assertEquals(5L, page.totalPages());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfCount()) {
                throw x;
            } else {
                return;
            }
        }
    }

    @Assertion(id = "133", strategy = "Request a Slice higher than the final Slice, expecting an empty Slice with 0 results.")
    public void testBeyondFinalSlice() {
        PageRequest sixth = PageRequest.ofPage(6).size(5).withoutTotal();
        Page<NaturalNumber> page;
        try {
            page = numbersByName.findByNumTypeAndFloorOfSquareRootLessThanEqual(
                    NumberType.PRIME,
                    8L,
                    sixth,
                    Sort.desc("id"));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfLessThanEqual() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }
        assertEquals(0, page.numberOfElements());
        assertEquals(0, page.stream().count());
        assertFalse(page.hasContent());
        assertFalse(page.iterator().hasNext());
    }

    @Assertion(id = "133", strategy = "Use a repository method with Contains to query for a substring of a String attribute.")
    public void testContainsInString() {
        Collection<AsciiCharacter> found;
        try {
            found = charactersByName.findByHexadecimalContainsAndIsControlNot("4", true);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfLike()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("24", "34",
                        "40", "41", "42", "43",
                        "44", "45", "46", "47",
                        "48", "49", "4a", "4b",
                        "4c", "4d", "4e", "4f",
                        "54", "64", "74"),
                found.stream().map(AsciiCharacter::getHexadecimal).sorted().toList());
    }

    @Assertion(id = "133", strategy = "Use a repository that inherits from DataRepository and defines all of its own methods.")
    public void testDataRepository() {
        try {
            AsciiCharacter del = charactersByName.findByIsControlTrueAndNumericValueBetween(33, 127);
            assertEquals(127, del.getNumericValue());
            assertEquals("7f", del.getHexadecimal());
            assertTrue(del.isControl());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfBetween() &&
                type.capableOfConstraintsOnNonIdAttributes()) {
                throw x;
            }
        }

        try {
            AsciiCharacter j = charactersByName.findByHexadecimalIgnoreCase("6A");
            assertEquals("6a", j.getHexadecimal());
            assertEquals('j', j.getThisCharacter());
            assertEquals(106, j.getNumericValue());
            assertFalse(j.isControl());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfUpper()) {
                throw x;
            }
        }

        AsciiCharacter d = charactersByName.findByNumericValue(100).orElseThrow();
        assertEquals(100, d.getNumericValue());
        assertEquals('d', d.getThisCharacter());
        assertEquals("64", d.getHexadecimal());
        assertFalse(d.isControl());

        assertTrue(charactersByName.existsByThisCharacter('D'));
    }

    @Assertion(id = "133", strategy = "Use a default method from a repository interface where the default method invokes other repository methods.")
    public void testDefaultMethod() {
        try {
            assertEquals(List.of('W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd'),
                    charactersByName.retrieveAlphaNumericIn(87L, 100L)
                            .map(AsciiCharacter::getThisCharacter)
                            .collect(Collectors.toList()));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfBetween() &&
                type.capableOfSingleSort()) {
                throw x;
            }
        }
    }

    @Assertion(id = "133",
            strategy = "Use a repository method with one Sort parameter specifying descending order, " +
                    "and verify all results are returned and are in descending order according to the sort criteria.")
    public void testDescendingSort() {
        Stream<AsciiCharacter> stream;
        try {
            stream = charactersByName.findByIdBetween(
                    52L, 57L,
                    Sort.desc("id"));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfBetween() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(Arrays.toString(new Character[]{'9', '8', '7', '6', '5', '4'}),
                Arrays.toString(stream.map(AsciiCharacter::getThisCharacter).toArray()));
    }

    @Assertion(id = "133", strategy = "Use a repository method that returns a single entity value where no result is found. Expect EmptyResultException.")
    public void testEmptyResultException() {
        try {
            AsciiCharacter ch = charactersByName.findByHexadecimalIgnoreCase("2g");
            fail("Unexpected result of findByHexadecimalIgnoreCase(2g): " + ch.getHexadecimal());
        } catch (EmptyResultException x) {
            log.info("testEmptyResultException expected to catch exception " + x + ". Printing its stack trace:");
            x.printStackTrace(System.out);
            // test passes
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfUpper()) {
                throw x;
            }
        }
    }

    @Assertion(id = "133", strategy = "Use a repository method with the False keyword.")
    public void testFalse() {
        List<NaturalNumber> even;
        try {
            even = positivesByName.findByIsOddFalseAndIdBetween(50L, 60L);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfBetween() &&
                type.capableOfConstraintsOnNonIdAttributes()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(6L, even.stream().count());

        assertEquals(List.of(50L, 52L, 54L, 56L, 58L, 60L),
                even.stream().map(NaturalNumber::getId).sorted().collect(Collectors.toList()));
    }

    @Assertion(id = "133", strategy = "Request the last Page of up to 10 results, expecting to find the final 3.")
    public void testFinalPageOfUpTo10() {
        PageRequest fifthPageRequest = PageRequest.ofPage(5).size(10);
        Page<AsciiCharacter> page;
        try {
            page = charactersByName.findByNumericValueBetween(48, 90, fifthPageRequest,
                    Order.by(_AsciiCharacter.numericValue.asc())); // 'X' to 'Z'
        } catch (UnsupportedOperationException x) {
            if (type.capableOfBetween() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        Iterator<AsciiCharacter> it = page.iterator();

        // first result
        assertTrue(it.hasNext());
        AsciiCharacter ch = it.next();
        assertEquals('X', ch.getThisCharacter());
        assertEquals("58", ch.getHexadecimal());
        assertEquals(88L, ch.getId());
        assertEquals(88, ch.getNumericValue());
        assertFalse(ch.isControl());

        // second result
        ch = it.next();
        assertEquals('Y', ch.getThisCharacter());
        assertEquals("59", ch.getHexadecimal());
        assertEquals(89L, ch.getId());
        assertEquals(89, ch.getNumericValue());
        assertFalse(ch.isControl());

        // third result
        ch = it.next();
        assertEquals('Z', ch.getThisCharacter());
        assertEquals("5a", ch.getHexadecimal());
        assertEquals(90L, ch.getId());
        assertEquals(90, ch.getNumericValue());
        assertFalse(ch.isControl());

        assertFalse(it.hasNext());

        assertEquals(5, page.pageRequest().page());
        assertTrue(page.hasContent());
        assertEquals(3, page.numberOfElements());
        try {
            assertEquals(43L, page.totalElements());
            assertEquals(5L, page.totalPages());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfCount()) {
                throw x;
            }
        }
    }

    @Assertion(id = "133", strategy = "Request the last Slice of up to 5 results, expecting to find the final 2.")
    public void testFinalSliceOfUpTo5() {
        PageRequest fifth = PageRequest.ofPage(5).size(5).withoutTotal();
        Page<NaturalNumber> page;
        try {
            page = numbersByName.findByNumTypeAndFloorOfSquareRootLessThanEqual(
                    NumberType.PRIME,
                    8L,
                    fifth,
                    Sort.desc("id"));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfLessThanEqual() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }
        assertTrue(page.hasContent());
        assertEquals(5, page.pageRequest().page());
        assertEquals(2, page.numberOfElements());

        Iterator<NaturalNumber> it = page.iterator();

        // first result
        assertTrue(it.hasNext());
        NaturalNumber number = it.next();
        assertEquals(3L, number.getId());
        assertEquals(NumberType.PRIME, number.getNumType());
        assertEquals(1L, number.getFloorOfSquareRoot());
        assertTrue(number.isOdd());
        assertEquals(Short.valueOf((short) 2), number.getNumBitsRequired());

        // second result
        assertTrue(it.hasNext());
        number = it.next();
        assertEquals(2L, number.getId());
        assertEquals(NumberType.PRIME, number.getNumType());
        assertEquals(1L, number.getFloorOfSquareRoot());
        assertFalse(number.isOdd());
        assertEquals(Short.valueOf((short) 2), number.getNumBitsRequired());

        assertFalse(it.hasNext());
    }

    @Assertion(id = "133",
            strategy = "Use a repository method with findFirstBy that returns the first entity value " +
                    "where multiple results would otherwise be found.")
    public void testFindFirst() {
        Optional<AsciiCharacter> none;
        try {
            none = charactersByName.findFirstByHexadecimalStartsWithAndIsControlOrderByIdAsc(
                    "h", false);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfLike() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }
        assertTrue(none.isEmpty());

        AsciiCharacter ch = charactersByName.findFirstByHexadecimalStartsWithAndIsControlOrderByIdAsc("4", false)
                .orElseThrow();
        assertEquals('@', ch.getThisCharacter());
        assertEquals("40", ch.getHexadecimal());
        assertEquals(64, ch.getNumericValue());
    }

    @Assertion(id = "133",
            strategy = "Use a repository method with findFirst3By that returns the first 3 results.")
    public void testFindFirst3() {
        AsciiCharacter[] found;

        try {
            found = charactersByName.findFirst3ByNumericValueGreaterThanEqualAndHexadecimalEndsWith(
                    40, "4", Sort.asc("numericValue"));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfLike() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(3, found.length);
        assertEquals('4', found[0].getThisCharacter());
        assertEquals('D', found[1].getThisCharacter());
        assertEquals('T', found[2].getThisCharacter());
    }

    @Assertion(id = "133",
            strategy = "Request the first CursoredPage of 8 results, expecting to find all 8, " +
                    "then request the next CursoredPage and the CursoredPage after that, " +
                    "expecting to find all results.")
    public void testFirstCursoredPageOf8AndNextPages() {
        // The query for this test returns 1-15,25-32 in the following order:

        // 32 requires 6 bits
        // 25, 26, 27, 28, 29, 30, 31 requires 5 bits
        // 8, 9, 10, 11, 12, 13, 14, 15 requires 4 bits
        // 4, 5, 6, 7, 8 requires 3 bits
        // 2, 3 requires 2 bits
        // 1 requires 1 bit

        Order<NaturalNumber> order = Order.by(Sort.asc("id"));
        PageRequest first8 = PageRequest.ofSize(8);
        CursoredPage<NaturalNumber> page;

        try {
            page = positivesByName.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(4L, 33L, first8, order);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfLessThan() &&
                type.capableOfMultipleSort() &&
                type.capableOfNotEqual() &&
                type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(8, page.numberOfElements());

        assertEquals(Arrays.toString(new Long[]{32L, 25L, 26L, 27L, 28L, 29L, 30L, 31L}),
                     Arrays.toString(page.stream()
                                         .map(NaturalNumber::getId)
                                         .toArray()));

        try {
            page = positivesByName.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(4L, 33L, page.nextPageRequest(), order);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(Arrays.toString(new Long[]{8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L}),
                     Arrays.toString(page.stream()
                                         .map(NaturalNumber::getId)
                                         .toArray()));

        assertEquals(8, page.numberOfElements());

        page = positivesByName.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(
                4L, 33L, page.nextPageRequest(), order);

        assertEquals(7, page.numberOfElements());

        assertEquals(Arrays.toString(new Long[]{4L, 5L, 6L, 7L, 2L, 3L, 1L}),
                     Arrays.toString(page.stream()
                                         .map(NaturalNumber::getId)
                                         .toArray()));
    }

    @Assertion(id = "133",
            strategy = "Request the first CursoredPage of 6 results, expecting to find all 6, " +
                    "then request the next CursoredPage and the CursoredPage after that, " +
                    "expecting to find all results.")
    public void testFirstCursoredPageWithoutTotalOf6AndNextPages() {
        PageRequest first6 = PageRequest.ofSize(6).withoutTotal();
        CursoredPage<NaturalNumber> slice;

        try {
            slice = numbersByName.findByFloorOfSquareRootOrderByIdAsc(7L, first6);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(Arrays.toString(new Long[]{49L, 50L, 51L, 52L, 53L, 54L}),
                     Arrays.toString(slice.stream()
                                          .map(NaturalNumber::getId)
                                          .toArray()));

        assertEquals(6, slice.numberOfElements());

        try {
            slice = numbersByName.findByFloorOfSquareRootOrderByIdAsc(7L, slice.nextPageRequest());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(6, slice.numberOfElements());

        assertEquals(Arrays.toString(new Long[]{55L, 56L, 57L, 58L, 59L, 60L}),
                     Arrays.toString(slice.stream()
                                          .map(NaturalNumber::getId)
                                          .toArray()));

        slice = numbersByName.findByFloorOfSquareRootOrderByIdAsc(7L, slice.nextPageRequest());


        assertEquals(Arrays.toString(new Long[]{61L, 62L, 63L}),
                     Arrays.toString(slice.stream()
                                          .map(NaturalNumber::getId)
                                          .toArray()));

        assertEquals(3, slice.numberOfElements());
    }

    @Assertion(id = "133",
            strategy = "Request the first Page of 10 results, expecting to find all 10. " +
                    "From the Page, verify the totalElements and totalPages expected.")
    public void testFirstPageOf10() {
        PageRequest first10 = PageRequest.ofSize(10);
        Page<AsciiCharacter> page;
        try {
            page = charactersByName.findByNumericValueBetween(48, 90, first10,
                    Order.by(_AsciiCharacter.numericValue.asc())); // '0' to 'Z'
        } catch (UnsupportedOperationException x) {
            if (type.capableOfBetween() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(1, page.pageRequest().page());
        assertTrue(page.hasContent());
        assertEquals(10, page.numberOfElements());
        try {
            assertEquals(43L, page.totalElements());
            assertEquals(5L, page.totalPages());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfCount()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals("30:0;31:1;32:2;33:3;34:4;35:5;36:6;37:7;38:8;39:9;", // '0' to '9'
                page.stream()
                        .map(c -> c.getHexadecimal() + ':' + c.getThisCharacter() + ';')
                        .reduce("", String::concat));
    }

    @Assertion(id = "133", strategy = "Request the first Slice of 5 results, expecting to find all 5.")
    public void testFirstSliceOf5() {
        PageRequest first5 = PageRequest.ofSize(5).withoutTotal();
        Page<NaturalNumber> page;
        try {
            page = numbersByName.findByNumTypeAndFloorOfSquareRootLessThanEqual(
                    NumberType.PRIME,
                    8L,
                    first5,
                    Sort.desc("id"));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfLessThanEqual() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }
        assertEquals(5, page.numberOfElements());

        Iterator<NaturalNumber> it = page.iterator();

        // first result
        assertTrue(it.hasNext());
        NaturalNumber number = it.next();
        assertEquals(79L, number.getId());
        assertEquals(NumberType.PRIME, number.getNumType());
        assertEquals(8L, number.getFloorOfSquareRoot());
        assertTrue(number.isOdd());
        assertEquals(Short.valueOf((short) 7), number.getNumBitsRequired());

        // second result
        assertTrue(it.hasNext());
        assertEquals(73L, it.next().getId());

        // third result
        assertTrue(it.hasNext());
        assertEquals(71L, it.next().getId());

        // fourth result
        assertTrue(it.hasNext());
        assertEquals(67L, it.next().getId());

        // fifth result
        assertTrue(it.hasNext());
        number = it.next();
        assertEquals(61L, number.getId());
        assertEquals(NumberType.PRIME, number.getNumType());
        assertEquals(7L, number.getFloorOfSquareRoot());
        assertTrue(number.isOdd());
        assertEquals(Short.valueOf((short) 6), number.getNumBitsRequired());

        assertFalse(it.hasNext());
    }

    @Assertion(id = "133", strategy = "Use a repository method existsByIdGreaterThan confirming the correct boolean is returned.")
    public void testGreaterThanEqualExists() {
        try {
            assertTrue(positivesByName.existsByIdGreaterThan(0L));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfGreaterThan()) {
                throw x;
            } else {
                return;
            }
        }
        assertTrue(positivesByName.existsByIdGreaterThan(99L));
        assertFalse(positivesByName.existsByIdGreaterThan(100L)); // doesn't exist because the table only has 1 to 100
    }

    @Assertion(id = "133", strategy = "Use a repository method with the In keyword.")
    public void testIn() {
        Stream<NaturalNumber> nonPrimes;
        try {
            nonPrimes = positivesByName.findByNumTypeInOrderByIdAsc(
                    Set.of(NumberType.COMPOSITE, NumberType.ONE),
                    Limit.of(9));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfIn() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of(1L, 4L, 6L, 8L, 9L, 10L, 12L, 14L, 15L),
                nonPrimes.map(NaturalNumber::getId).collect(Collectors.toList()));

        Stream<NaturalNumber> primes = positivesByName.findByNumTypeInOrderByIdAsc(Collections.singleton(NumberType.PRIME),
                Limit.of(6));
        assertEquals(List.of(2L, 3L, 5L, 7L, 11L, 13L),
                primes.map(NaturalNumber::getId).collect(Collectors.toList()));
    }

    @Assertion(id = "133", strategy = "Use a repository method with the IgnoreCase keyword.")
    public void testIgnoreCase() {
        Stream<AsciiCharacter> found;
        try {
            found = charactersByName.findByHexadecimalIgnoreCaseBetweenAndHexadecimalNotIn(
                    "4c", "5A", Set.of("5"),
                    Order.by(Sort.asc("hexadecimal")));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfBetween() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfNotIn() &&
                type.capableOfSingleSort() &&
                type.capableOfUpper()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of('L', // 4c
                        'M', // 4d
                        'N', // 4e
                        'O', // 4f
                        'P', // 50
                        'Q', // 51
                        'R', // 52
                        'S', // 53
                        'T', // 54
                        'U', // 55
                        'V', // 56
                        'W', // 57
                        'X', // 58
                        'Y', // 59
                        'Z'), // 5a
                found.map(AsciiCharacter::getThisCharacter).collect(Collectors.toList()));
    }

    @Assertion(id = "133",
            strategy = "Request a CursoredPage of 7 results after the cursor of the 20th result, expecting to find the next 7 results. " +
                    "Then request the CursoredPage before the cursor of the first entry of the page, expecting to find the previous 7 results. " +
                    "Then request the CursoredPage after the last entry of the original slice, expecting to find the next 7.")
    public void testCursoredPageOf7FromCursor() {
        // The query for this test returns 1-35 and 49 in the following order:
        //
        // 35 34 33 32 49 24 23 22 21 20 19 18 17 16 31 30 29 28 27 26 25 08 15 14 13 12 11 10 09 07 06 05 04 03 02 01
        //                                                             ^^^^^^ page 1 ^^^^^^
        //                                        ^^^ previous page ^^
        //                                                                                  ^^^^^ next page ^^^^

        Order<NaturalNumber> order = Order.by(Sort.asc("floorOfSquareRoot"), Sort.desc("id"));
        PageRequest middle7 = PageRequest.afterCursor(
                Cursor.forKey((short) 5, 5L, 26L), // 20th result is 26; it requires 5 bits and its square root rounds down to 5.),
                4L, 7, true);

        CursoredPage<NaturalNumber> page;
        try {
            page = positivesByName.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(6L, 50L, middle7, order);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfLessThan() &&
                type.capableOfMultipleSort() &&
                type.capableOfNotEqual() &&
                type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(Arrays.toString(new Long[]{
                        25L, // 5 bits required, square root rounds down to 5
                        8L, // 4 bits required, square root rounds down to 2
                        15L, 14L, 13L, 12L, 11L // 4 bits required, square root rounds down to 3
                     }),
                     Arrays.toString(page.stream()
                                         .map(NaturalNumber::getId)
                                         .toArray()));

        assertEquals(7, page.numberOfElements());

        assertTrue(page.hasPrevious());

        CursoredPage<NaturalNumber> previousPage;
        try {
            previousPage = positivesByName.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(6L, 50L,
                    page.previousPageRequest(),
                    order);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(Arrays.toString(new Long[]{
                        16L, // 4 bits required, square root rounds down to 4
                        31L, 30L, 29L, 28L, 27L, 26L // 5 bits required, square root rounds down to 5
                     }),
                     Arrays.toString(previousPage.stream()
                                                 .map(NaturalNumber::getId)
                                                 .toArray()));

        assertEquals(7, previousPage.numberOfElements());

        CursoredPage<NaturalNumber> nextPage;
        try {
            nextPage = positivesByName.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(6L, 50L,
                    page.nextPageRequest(),
                    order);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(Arrays.toString(new Long[]{
                        10L, 9L, // 4 bits required, square root rounds down to 3
                        7L, 6L, 5L, 4L, // 3 bits required, square root rounds down to 2
                        3L // 2 bits required, square root rounds down to 1
                     }),
                     Arrays.toString(nextPage.stream()
                                             .map(NaturalNumber::getId)
                                             .toArray()));

        assertEquals(7, nextPage.numberOfElements());
    }

    @Assertion(id = "133", strategy = "Request a CursoredPage of results where none match the query, expecting an empty CursoredPage with 0 results.")
    public void testCursoredPageOfNothing() {

        CursoredPage<NaturalNumber> page;
        try {
            // There are no positive integers less than 4 which have a square root that rounds down to something other than 1.
            page = positivesByName.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(1L, 4L, PageRequest.ofPage(1L), Order.by());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfLessThan() &&
                type.capableOfMultipleSort() &&
                type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertFalse(page.hasContent());
        assertFalse(page.hasNext());
        assertFalse(page.hasPrevious());
        assertEquals(0, page.content().size());
        assertEquals(0, page.numberOfElements());

        try {
            page.nextPageRequest();
            fail("nextPageRequest must raise NoSuchElementException when current page is empty.");
        } catch (NoSuchElementException x) {
            // expected
        }

        try {
            page.previousPageRequest();
            fail("previousPageRequest must raise NoSuchElementException when current page is empty.");
        } catch (NoSuchElementException x) {
            // expected
        }
    }

    @Assertion(id = "133",
            strategy = "Request a CursoredPage of 9 results after the cursor of the 20th result, expecting to find the next 9 results. " +
                    "Then request the CursoredPage before the cursor of the first entry of the slice, expecting to find the previous 9 results. " +
                    "Then request the CursoredPage after the last entry of the original slice, expecting to find the next 9.")
    public void testCursoredPageWithoutTotalOf9FromCursor() {
        // The query for this test returns composite natural numbers under 64 in the following order:
        //
        // 49 50 51 52 54 55 56 57 58 60 62 63 36 38 39 40 42 44 45 46 48 25 26 27 28 30 32 33 34 35 16 18 20 21 22 24 09 10 12 14 15 04 06 08
        //                                                             ^^^^^^^^ slice 1 ^^^^^^^^^
        //                                  ^^^^^^^^ slice 2 ^^^^^^^^^
        //                                                                                        ^^^^^^^^ slice 3 ^^^^^^^^^

        PageRequest middle9 = PageRequest.afterCursor(
                Cursor.forKey(6L, 46L), // 20th result is 46; its square root rounds down to 6.
                4L, 9, false);
        Order<NaturalNumber> order = Order.by(Sort.desc("floorOfSquareRoot"), Sort.asc("id"));

        CursoredPage<NaturalNumber> slice;
        try {
            slice = numbersByName.findByNumTypeAndNumBitsRequiredLessThan(NumberType.COMPOSITE, (short) 7, order, middle9);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfLessThan() &&
                type.capableOfMultipleSort() &&
                type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(Arrays.toString(new Long[]{48L, 25L, 26L, 27L, 28L, 30L, 32L, 33L, 34L}),
                Arrays.toString(slice.stream().map(NaturalNumber::getId).toArray()));

        assertEquals(9, slice.numberOfElements());

        assertTrue(slice.hasPrevious());
        CursoredPage<NaturalNumber> previousSlice;
        try {
            previousSlice = numbersByName.findByNumTypeAndNumBitsRequiredLessThan(NumberType.COMPOSITE,
                    (short) 7,
                    order,
                    slice.previousPageRequest());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(Arrays.toString(new Long[]{63L, 36L, 38L, 39L, 40L, 42L, 44L, 45L, 46L}),
                Arrays.toString(previousSlice.stream().map(NaturalNumber::getId).toArray()));

        assertEquals(9, previousSlice.numberOfElements());

        CursoredPage<NaturalNumber> nextSlice;
        try {
            nextSlice = numbersByName.findByNumTypeAndNumBitsRequiredLessThan(NumberType.COMPOSITE,
                    (short) 7,
                    order,
                    slice.nextPageRequest());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(Arrays.toString(new Long[]{35L, 16L, 18L, 20L, 21L, 22L, 24L, 9L, 10L}),
                Arrays.toString(nextSlice.stream().map(NaturalNumber::getId).toArray()));

        assertEquals(9, nextSlice.numberOfElements());
    }

    @Assertion(id = "133", strategy = "Request a CursoredPage of results where none match the query, expecting an empty CursoredPage with 0 results.")
    public void testCursoredPageWithoutTotalOfNothing() {
        // There are no numbers larger than 30 which have a square root that rounds down to 3.
        PageRequest pagination = PageRequest.ofSize(33).afterCursor(Cursor.forKey(30L)).withoutTotal();

        CursoredPage<NaturalNumber> slice;
        try {
            slice = numbersByName.findByFloorOfSquareRootOrderByIdAsc(3L, pagination);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfOr() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertFalse(slice.hasContent());
        assertEquals(0, slice.content().size());
        assertEquals(0, slice.numberOfElements());
    }

    @Assertion(id = "133", strategy = "Use a repository method countByIdLessThan confirming the correct count is returned.")
    public void testLessThanWithCount() {
        try {
            assertEquals(91L, positivesByName.countByIdLessThan(92L));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfCount() &&
                type.capableOfLessThan()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(0L, positivesByName.countByIdLessThan(1L));
    }

    @Assertion(id = "133", strategy = "Use a repository method with both Sort and Limit, and verify that the Limit caps " +
            "the number of results and that results are ordered according to the sort criteria.")
    public void testLimit() {
        Collection<NaturalNumber> nums;
        try {
            nums = numbersByName.findByIdGreaterThanEqual(
                    60L,
                    Limit.of(10),
                    Order.by(
                            Sort.asc("floorOfSquareRoot"),
                            Sort.desc("id")));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfGreaterThanEqual() &&
                type.capableOfMultipleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(Arrays.toString(new Long[]{
                        63L, 62L, 61L, 60L, // square root rounds down to 7
                        80L, 79L, 78L, 77L, 76L, 75L}), // square root rounds down to 8
                     Arrays.toString(nums.stream()
                                         .map(NaturalNumber::getId)
                                         .toArray()));
    }

    @Assertion(id = "133", strategy = "Use a repository method with both Sort and Limit, where the Limit is a range, " +
            " and verify that the Limit range starts in the correct place, caps the number of results, " +
            " and that results are ordered according to the sort criteria.")
    public void testLimitedRange() {
        // Primes above 40 are:
        // 41, 43, 47, 53, 59,
        // 61, 67, 71, 73, 79,
        // 83, 89, ...

        Collection<NaturalNumber> nums;
        try {
            nums = numbersByName.findByIdGreaterThanEqual(
                    40L,
                    Limit.range(6, 10),
                    Order.by(
                            Sort.asc("numTypeOrdinal"), // primes first
                            Sort.asc("id")));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfGreaterThanEqual() &&
                type.capableOfMultipleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(Arrays.toString(new Long[]{61L, 67L, 71L, 73L, 79L}),
                     Arrays.toString(nums.stream()
                                         .map(NaturalNumber::getId)
                                         .toArray()));
    }

    @Assertion(id = "133", strategy = "Use a repository method with Limit and verify that the Limit caps " +
            "the number of results to the amount that is specified.")
    public void testLimitToOneResult() {
        Collection<NaturalNumber> nums;
        try {
            nums = numbersByName.findByIdGreaterThanEqual(80L, Limit.of(1), Order.by());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfGreaterThanEqual()) {
                throw x;
            } else {
                return;
            }
        }
        Iterator<NaturalNumber> it = nums.iterator();
        assertTrue(it.hasNext());

        NaturalNumber num = it.next();
        assertTrue(num.getId() >= 80L);

        assertFalse(it.hasNext());
    }

    @Assertion(id = "133",
            strategy = "Use a repository method with two Sort parameters specifying a mixture of ascending and descending order, " +
                    "and verify all results are returned and are ordered according to the sort criteria.")
    public void testMixedSort() {
        NaturalNumber[] nums;
        try {
            nums = numbersByName.findByIdLessThan(
                    15L,
                    Sort.asc("numBitsRequired"),
                    Sort.desc("id"));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfLessThan() &&
                type.capableOfMultipleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(Arrays.toString(new Long[]{
                        1L, // 1 bit
                        3L, 2L, // 2 bits
                        7L, 6L, 5L, 4L, // 3 bits
                        14L, 13L, 12L, 11L, 10L, 9L, 8L}), // 4 bits
                     Arrays.toString(Stream.of(nums)
                                           .map(NaturalNumber::getId)
                                           .toArray()));
    }

    @Assertion(id = "133",
            strategy = "Use a repository method that ought to return a single entity value but where multiple results are found." +
                    "Expect NonUniqueResultException.")
    public void testNonUniqueResultException() {
        try {
            AsciiCharacter ch = charactersByName.findByIsControlTrueAndNumericValueBetween(10, 15);
            fail("Unexpected result of findByIsControlTrueAndNumericValueBetween(10, 15): " + ch.getHexadecimal());
        } catch (NonUniqueResultException x) {
            log.info("testNonUniqueResultException expected to catch exception " + x + ". Printing its stack trace:");
            x.printStackTrace(System.out);
            // test passes
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfBetween() &&
                type.capableOfConstraintsOnNonIdAttributes()) {
                throw x;
            }
        }
    }

    @Assertion(id = "133", strategy = "Use a repository method with the Not keyword.")
    public void testNot() {
        NaturalNumber[] n;
        try {
            n = numbersByName.findByNumTypeNot(
                    NumberType.COMPOSITE,
                    Limit.of(8),
                    Order.by(Sort.asc("id")));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfNotEqual() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }
        assertEquals(8, n.length);
        assertEquals(1L, n[0].getId());
        assertEquals(2L, n[1].getId());
        assertEquals(3L, n[2].getId());
        assertEquals(5L, n[3].getId());
        assertEquals(7L, n[4].getId());
        assertEquals(11L, n[5].getId());
        assertEquals(13L, n[6].getId());
        assertEquals(17L, n[7].getId());
    }

    @Assertion(id = "133", strategy = "Use a repository method with Or, expecting UnsupportedOperationException if the underlying database is not capable.")
    public void testOr() {
        Stream<NaturalNumber> found;
        try {
            found = positivesByName.findByNumTypeOrFloorOfSquareRoot(NumberType.ONE, 2L);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of(1L, 4L, 5L, 6L, 7L, 8L),
                     found.map(NaturalNumber::getId).sorted().toList());
    }

    @Assertion(id = "133",
            strategy = "Use a repository method with OrderBy (static) and a Sort parameter (dynamic), " +
                    "verfying that all results are returned and are ordered first by the static sort criteria, " +
                    "followed by the dynamic sort criteria when the value(s) being compared by the static criteria match.")
    public void testOrderByHasPrecedenceOverPageRequestSorts() {
        PageRequest pagination = PageRequest.ofSize(8);
        Order<NaturalNumber> order = Order.by(Sort.asc("numTypeOrdinal"), Sort.desc("id"));

        Page<NaturalNumber> page;
        try {
            page = numbersByName.findByIdLessThanOrderByFloorOfSquareRootDesc(
                    25L, pagination, order);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfCount() &&
                type.capableOfLessThan() &&
                type.capableOfMultipleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(Arrays.toString(new Long[]{
                        23L, 19L, 17L, // square root rounds down to 4; prime
                        24L, 22L, 21L, 20L, 18L}), // square root rounds down to 4; composite
                     Arrays.toString(page.stream()
                                         .map(NaturalNumber::getId)
                                         .toArray()));

        assertTrue(page.hasNext());
        pagination = page.nextPageRequest();
        page = numbersByName.findByIdLessThanOrderByFloorOfSquareRootDesc(25L, pagination, order);

        assertEquals(Arrays.toString(new Long[]{
                        16L, // square root rounds down to 4; composite
                        13L, 11L, // square root rounds down to 3; prime
                        15L, 14L, 12L, 10L, 9L}), // square root rounds down to 3; composite
                     Arrays.toString(page.stream()
                                         .map(NaturalNumber::getId)
                                         .toArray()));

        assertTrue(page.hasNext());
        pagination = page.nextPageRequest();
        page = numbersByName.findByIdLessThanOrderByFloorOfSquareRootDesc(25L, pagination, order);

        assertEquals(Arrays.toString(new Long[]{
                        7L, 5L, // square root rounds down to 2; prime
                        8L, 6L, 4L, // square root rounds down to 2; composite
                        1L, // square root rounds down to 1; one
                        3L, 2L}), // square root rounds down to 1; prime
                     Arrays.toString(page.stream()
                                         .map(NaturalNumber::getId)
                                         .toArray()));

        if (page.hasNext()) {
            pagination = page.nextPageRequest();
            page = numbersByName.findByIdLessThanOrderByFloorOfSquareRootDesc(25L, pagination, order);
            assertFalse(page.hasContent());
        }
    }

    @Assertion(id = "133",
            strategy = "Use a repository method with OrderBy (static) and a PageRequest with a Sort parameter (dynamic), " +
                    "verfying that all results are returned and are ordered first by the static sort criteria, " +
                    "followed by the dynamic sort criteria when the value(s) being compared by the static criteria match.")
    public void testOrderByHasPrecedenceOverSorts() {
        Stream<NaturalNumber> nums;
        try {
            nums = numbersByName.findByIdBetweenOrderByNumTypeOrdinalAsc(
                    5L, 24L,
                    Order.by(Sort.desc("floorOfSquareRoot"), Sort.asc("id")));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfBetween() &&
                type.capableOfMultipleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(Arrays.toString(new Long[]{
                        17L, 19L, 23L, // prime; square root rounds down to 4
                        11L, 13L, // prime; square root rounds down to 3
                        5L, 7L, // prime; square root rounds down to 2
                        16L, 18L, 20L, 21L, 22L, 24L, // composite; square root rounds down to 4
                        9L, 10L, 12L, 14L, 15L, // composite; square root rounds down to 3
                        6L, 8L}), // composite; square root rounds down to 2
                     Arrays.toString(nums.map(NaturalNumber::getId)
                                         .toArray()));
    }

    @Assertion(id = "133", strategy = "Request a Page of results where none match the query, expecting an empty Page with 0 results.")
    public void testPageOfNothing() {
        PageRequest pagination = PageRequest.ofSize(6);
        Page<AsciiCharacter> page;
        try {
            page = charactersByName.findByNumericValueBetween(150, 160, pagination,
                    Order.by(_AsciiChar.id.asc()));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfBetween() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(0, page.numberOfElements());
        assertEquals(0, page.stream().count());
        assertEquals(0, page.content().size());
        assertFalse(page.hasContent());
        assertFalse(page.iterator().hasNext());
        try {
            assertEquals(0L, page.totalElements());
            assertEquals(0L, page.totalPages());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfCount()) {
                throw x;
            } else {
                return;
            }
        }
    }

    @Assertion(id = "133", strategy = "Use a repository method that returns a single entity value where a single result is found.")
    public void testSingleEntity() {
        AsciiCharacter ch;
        try {
            ch = charactersByName.findByHexadecimalIgnoreCase("2B");
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfLower() &&
                type.capableOfUpper()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals('+', ch.getThisCharacter());
        assertEquals("2b", ch.getHexadecimal());
        assertEquals(43, ch.getNumericValue());
        assertFalse(ch.isControl());
    }

    @Assertion(id = "133", strategy = "Request a Slice of results where none match the query, expecting an empty Slice with 0 results.")
    public void testSliceOfNothing() {
        PageRequest pagination = PageRequest.ofSize(5).withoutTotal();
        Page<NaturalNumber> page;
        try {
            page = numbersByName.findByNumTypeAndFloorOfSquareRootLessThanEqual(
                    NumberType.COMPOSITE, 1L, pagination, Sort.desc("id"));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfLessThanEqual() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertFalse(page.hasContent());
        assertEquals(0, page.content().size());
        assertEquals(0, page.numberOfElements());
    }

    @Assertion(id = "133", strategy = "Use the StaticMetamodel to obtain ascending Sorts for an entity attribute in a type-safe manner.")
    public void testStaticMetamodelAscendingSorts() {
        assertEquals(Sort.asc("id"), _AsciiChar.id.asc());
        assertEquals(Sort.ascIgnoreCase(_AsciiChar.HEXADECIMAL), _AsciiChar.hexadecimal.ascIgnoreCase());
        assertEquals(Sort.ascIgnoreCase("thisCharacter"), _AsciiChar.thisCharacter.ascIgnoreCase());

        PageRequest pageRequest = PageRequest.ofSize(6);
        Page<AsciiCharacter> page1;
        try {
            page1 = charactersByName.findByNumericValueBetween(
                    68, 90, pageRequest,
                    Order.by(_AsciiChar.numericValue.asc()));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfBetween() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of('D', 'E', 'F', 'G', 'H', 'I'),
                page1.stream()
                        .map(AsciiCharacter::getThisCharacter)
                        .collect(Collectors.toList()));
    }

    @Assertion(id = "133", strategy = "Use a pre-generated StaticMetamodel to obtain ascending Sorts for an entity attribute in a type-safe manner.")
    public void testStaticMetamodelAscendingSortsPreGenerated() {
        assertEquals(Sort.asc("id"), _AsciiCharacter.id.asc());
        assertEquals(Sort.asc("isControl"), _AsciiCharacter.isControl.asc());
        assertEquals(Sort.ascIgnoreCase(_AsciiCharacter.HEXADECIMAL), _AsciiCharacter.hexadecimal.ascIgnoreCase());
        assertEquals(Sort.ascIgnoreCase("thisCharacter"), _AsciiCharacter.thisCharacter.ascIgnoreCase());

        PageRequest pageRequest = PageRequest.ofSize(7);
        Page<AsciiCharacter> page1;
        try {
            page1 = charactersByName.findByNumericValueBetween(
                    100, 122, pageRequest,
                    Order.by(_AsciiCharacter.numericValue.asc()));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfBetween() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of('d', 'e', 'f', 'g', 'h', 'i', 'j'),
                page1.stream()
                        .map(AsciiCharacter::getThisCharacter)
                        .collect(Collectors.toList()));
    }

    @Assertion(id = "133", strategy = "Use the StaticMetamodel to obtain descending Sorts for an entity attribute a type-safe manner.")
    public void testStaticMetamodelDescendingSorts() {
        assertEquals(Sort.desc(_AsciiChar.ID), _AsciiChar.id.desc());
        assertEquals(Sort.descIgnoreCase("hexadecimal"), _AsciiChar.hexadecimal.descIgnoreCase());
        assertEquals(Sort.descIgnoreCase("thisCharacter"), _AsciiChar.thisCharacter.descIgnoreCase());

        Sort<AsciiCharacter> sort = _AsciiChar.numericValue.desc();
        AsciiCharacter[] found;
        try {
            found = charactersByName.findFirst3ByNumericValueGreaterThanEqualAndHexadecimalEndsWith(
                    30, "1", sort);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfGreaterThanEqual() &&
                type.capableOfLike() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }
        assertEquals(3, found.length);
        assertEquals('q', found[0].getThisCharacter());
        assertEquals('a', found[1].getThisCharacter());
        assertEquals('Q', found[2].getThisCharacter());
    }

    @Assertion(id = "133", strategy = "Use a pre-generated StaticMetamodel to obtain descending Sorts for an entity attribute a type-safe manner.")
    public void testStaticMetamodelDescendingSortsPreGenerated() {
        assertEquals(Sort.desc(_AsciiCharacter.ID), _AsciiCharacter.id.desc());
        assertEquals(Sort.desc("isControl"), _AsciiCharacter.isControl.desc());
        assertEquals(Sort.descIgnoreCase("hexadecimal"), _AsciiCharacter.hexadecimal.descIgnoreCase());
        assertEquals(Sort.descIgnoreCase("thisCharacter"), _AsciiCharacter.thisCharacter.descIgnoreCase());

        Sort<AsciiCharacter> sort = _AsciiCharacter.numericValue.desc();
        AsciiCharacter[] found;
        try {
            found = charactersByName.findFirst3ByNumericValueGreaterThanEqualAndHexadecimalEndsWith(
                    30, "4", sort);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfGreaterThanEqual() &&
                type.capableOfLike() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }
        assertEquals(3, found.length);
        assertEquals('t', found[0].getThisCharacter());
        assertEquals('d', found[1].getThisCharacter());
        assertEquals('T', found[2].getThisCharacter());
    }

    @Assertion(id = "133", strategy = "Obtain multiple streams from the same List result of a repository method.")
    public void testStreamsFromList() {
        List<AsciiCharacter> chars;
        try {
            chars = charactersByName.findByNumericValueLessThanEqualAndNumericValueGreaterThanEqual(
                    109, 101);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfGreaterThanEqual() &&
                type.capableOfLessThanEqual()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(Arrays.toString(new Character[]{'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm'}),
                     Arrays.toString(chars.stream()
                                          .map(AsciiCharacter::getThisCharacter)
                                          .sorted()
                                          .toArray()));

        assertEquals(101 + 102 + 103 + 104 + 105 + 106 + 107 + 108 + 109,
                     chars.stream()
                          .mapToInt(AsciiCharacter::getNumericValue)
                          .sum());

        Set<String> sorted = new TreeSet<>();
        chars.forEach(ch -> sorted.add(ch.getHexadecimal()));
        assertEquals(new TreeSet<>(Set.of("65", "66", "67", "68", "69", "6a", "6b", "6c", "6d")),
                     sorted);

        List<AsciiCharacter> empty = charactersByName.findByNumericValueLessThanEqualAndNumericValueGreaterThanEqual(115, 120);
        assertFalse(empty.iterator().hasNext());
        assertEquals(0L, empty.stream().count());
    }

    @Assertion(id = "133",
            strategy = "Request the third Page of 10 results, expecting to find all 10. " +
                    "Request the next Page via nextPageRequest, expecting page number 4 and another 10 results.")
    public void testThirdAndFourthPagesOf10() {
        Order<AsciiCharacter> order = Order.by(_AsciiCharacter.numericValue.asc());
        PageRequest third10 = PageRequest.ofPage(3).size(10);
        Page<AsciiCharacter> page;
        try {
            page = charactersByName.findByNumericValueBetween(48, 90, third10, order); // 'D' to 'M'
        } catch (UnsupportedOperationException x) {
            if (type.capableOfBetween() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(3, page.pageRequest().page());
        assertTrue(page.hasContent());
        assertEquals(10, page.numberOfElements());
        try {
            assertEquals(43L, page.totalElements());
            assertEquals(5L, page.totalPages());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfCount()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals("44:D;45:E;46:F;47:G;48:H;49:I;4a:J;4b:K;4c:L;4d:M;",
                page.stream()
                        .map(c -> c.getHexadecimal() + ':' + c.getThisCharacter() + ';')
                        .reduce("", String::concat));

        assertTrue(page.hasNext());
        PageRequest fourth10 = page.nextPageRequest();
        page = charactersByName.findByNumericValueBetween(48, 90, fourth10, order); // 'N' to 'W'

        assertEquals(4, page.pageRequest().page());
        assertTrue(page.hasContent());
        assertEquals(10, page.numberOfElements());
        try {
            assertEquals(43L, page.totalElements());
            assertEquals(5L, page.totalPages());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfCount()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals("4e:N;4f:O;50:P;51:Q;52:R;53:S;54:T;55:U;56:V;57:W;",
                page.stream()
                        .map(c -> c.getHexadecimal() + ':' + c.getThisCharacter() + ';')
                        .reduce("", String::concat));
    }

    @Assertion(id = "133",
            strategy = "Request the third Slice of 5 results, expecting to find all 5. "
                    + "Request the next Slice via nextPageRequest, expecting page number 4 and another 5 results.")
    public void testThirdAndFourthSlicesOf5() {
        PageRequest third5 = PageRequest.ofPage(3).size(5).withoutTotal();
        Sort<NaturalNumber> sort = Sort.desc("id");
        Page<NaturalNumber> page;
        try {
            page = numbersByName.findByNumTypeAndFloorOfSquareRootLessThanEqual(
                    NumberType.PRIME, 8L, third5, sort);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfLessThanEqual() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(3, page.pageRequest().page());
        assertEquals(5, page.numberOfElements());

        assertEquals(Arrays.toString(new Long[]{37L, 31L, 29L, 23L, 19L}),
                     Arrays.toString(page.stream()
                                         .map(NaturalNumber::getId)
                                         .toArray()));

        assertTrue(page.hasNext());
        PageRequest fourth5 = page.nextPageRequest();

        page = numbersByName.findByNumTypeAndFloorOfSquareRootLessThanEqual(NumberType.PRIME, 8L, fourth5, sort);

        assertEquals(4, page.pageRequest().page());
        assertEquals(5, page.numberOfElements());

        assertEquals(Arrays.toString(new Long[]{17L, 13L, 11L, 7L, 5L}),
                     Arrays.toString(page.stream()
                                         .map(NaturalNumber::getId)
                                         .toArray()));
    }

    @Assertion(id = "133", strategy = "Use a repository method with the True keyword.")
    public void testTrue() {
        Iterable<NaturalNumber> odd;
        try {
            odd = positivesByName.findByIsOddTrueAndIdLessThanEqualOrderByIdDesc(10L);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfLessThanEqual() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        Iterator<NaturalNumber> it = odd.iterator();

        assertTrue(it.hasNext());
        assertEquals(9L, it.next().getId());

        assertTrue(it.hasNext());
        assertEquals(7L, it.next().getId());

        assertTrue(it.hasNext());
        assertEquals(5L, it.next().getId());

        assertTrue(it.hasNext());
        assertEquals(3L, it.next().getId());

        assertTrue(it.hasNext());
        assertEquals(1L, it.next().getId());

        assertFalse(it.hasNext());
    }

    @Assertion(id = "133",
            strategy = "Use a repository method with varargs Sort... specifying a mixture of ascending and descending order, " +
                    "and verify all results are returned and are ordered according to the sort criteria.")
    public void testVarargsSort() {
        List<NaturalNumber> list;
        try {
            list = numbersByName.findByIdLessThanEqual(
                    12L,
                    Sort.asc("floorOfSquareRoot"),
                    Sort.desc("numBitsRequired"),
                    Sort.asc("id"));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfLessThanEqual() &&
                type.capableOfMultipleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(Arrays.toString(new Long[]{
                        2L, 3L, // square root rounds down to 1; 2 bits
                        1L, // square root rounds down to 1; 1 bit
                        8L, // square root rounds down to 2; 4 bits
                        4L, 5L, 6L, 7L, // square root rounds down to 2; 3 bits
                        9L, 10L, 11L, 12L}), // square root rounds down to 3; 4 bits
                     Arrays.toString(list.stream()
                                         .map(NaturalNumber::getId)
                                         .toArray()));
    }

    @Assertion(id = "133", strategy = "Use count and exists methods where the primary entity class is inferred from the lifecycle methods.")
    public void testPrimaryEntityClassDeterminedByLifeCycleMethods() {
        assertEquals(4L, customRepo.countByIdIn(Set.of(2L, 15L, 37L, -5L, 60L)));

        assertTrue(customRepo.existsByIdIn(Set.of(17L, 14L, -1L)));

        assertFalse(customRepo.existsByIdIn(Set.of(-10L, -12L, -14L)));
    }

    @Assertion(id = "133", strategy = "Use a repository that inherits some if its methods from another interface.")
    public void testCommonInterfaceQueries() {

        try {
            assertEquals(4L, numbersByName.countByIdBetween(87L, 90L));

            assertEquals(5L, charactersByName.countByIdBetween(86L, 90L));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfBetween() &&
                type.capableOfCount()) {
                throw x;
            }
        }

        assertTrue(numbersByName.existsById(73L));

        assertTrue(charactersByName.existsById(74L));

        assertFalse(numbersByName.existsById(-1L));

        assertFalse(charactersByName.existsById(-2L));
    }
}
