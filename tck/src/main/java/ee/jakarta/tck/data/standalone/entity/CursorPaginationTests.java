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
package ee.jakarta.tck.data.standalone.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;

import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.ReadOnlyTest;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.framework.read.only.NaturalNumber;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbers;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbersPopulator;
import ee.jakarta.tck.data.framework.read.only.PositiveIntegers;
import ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType;
import ee.jakarta.tck.data.framework.utilities.DatabaseType;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.page.CursoredPage;
import jakarta.data.page.PageRequest;
import jakarta.data.page.PageRequest.Cursor;
import jakarta.inject.Inject;

/**
 * Tests of cursor pagination.
 */
@AnyEntity
@ReadOnlyTest
@Standalone
public class CursorPaginationTests {

    public static final Logger log =
            Logger.getLogger(CursorPaginationTests.class.getCanonicalName());

    @Inject
    NaturalNumbers numbers;

    @Inject
    PositiveIntegers positives; // shares same read-only data with NaturalNumbers

    // Inject doesn't happen until after BeforeClass, so this is necessary
    // before each test
    @BeforeEach
    public void beforeEach() {
        NaturalNumbersPopulator.get().populate(numbers);
    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(CursorPaginationTests.class);
    }

    private final DatabaseType type = TestProperty.databaseType.getDatabaseType();

    @Assertion(id = "133", strategy = """
            Request a CursoredPage of 9 results after the cursor
            of the 20th result, expecting to find the next 9 results.
            Then request the CursoredPage before the cursor of the
            first entry of the page, expecting to find the previous
            9 results. Then request the CursoredPage after the last
            entry of the original page, expecting to find the next 9.
            """)
    public void testAfterCursorNoTotals() {
        // The query for this test returns composite natural numbers under 64
        // in the following order:
        //
        // 49 50 51 52 54 55 56 57 58 60 62 63 36 38 39 40 42 44 45 46 48 25 26 27 28 30 32 33 34 35 16 18 20 21 22 24 09 10 12 14 15 04 06 08
        //                                                             ^^^^^^^^^ page 1 ^^^^^^^^^
        //                                  ^^^^^^^^^ page 2 ^^^^^^^^^
        //                                                                                        ^^^^^^^^^ page 3 ^^^^^^^^^

        PageRequest middle9 = PageRequest.afterCursor(
                Cursor.forKey(6L,
                              46L), // 20th result is 46; its √ rounds down to 6
                4L, 9, false);
        Order<NaturalNumber> order = Order.by(Sort.desc("floorOfSquareRoot"),
                                              Sort.asc("id"));

        CursoredPage<NaturalNumber> page;
        try {
            page = numbers.findByNumTypeAndNumBitsRequiredLessThan(
                   NumberType.COMPOSITE,
                   (short) 7,
                   order,
                   middle9);
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

        assertEquals(List.of(48L, 25L, 26L, 27L, 28L, 30L, 32L, 33L, 34L),
                     page.stream()
                         .map(NaturalNumber::getId)
                         .toList());

        assertEquals(9, page.numberOfElements());

        assertTrue(page.hasPrevious());
        CursoredPage<NaturalNumber> previousPage;
        try {
            previousPage = numbers.findByNumTypeAndNumBitsRequiredLessThan(
                    NumberType.COMPOSITE,
                    (short) 7,
                    order,
                    page.previousPageRequest());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of(63L, 36L, 38L, 39L, 40L, 42L, 44L, 45L, 46L),
                     previousPage.stream()
                                 .map(NaturalNumber::getId)
                                 .toList());

        assertEquals(9, previousPage.numberOfElements());

        CursoredPage<NaturalNumber> nextPage;
        try {
            nextPage = numbers.findByNumTypeAndNumBitsRequiredLessThan(
                    NumberType.COMPOSITE,
                    (short) 7,
                    order,
                    page.nextPageRequest());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of(35L, 16L, 18L, 20L, 21L, 22L, 24L, 9L, 10L),
                     nextPage.stream()
                             .map(NaturalNumber::getId)
                             .toList());

        assertEquals(9, nextPage.numberOfElements());
    }

    @Assertion(id = "133", strategy = """
            Request a CursoredPage of 7 results after the
            cursor of the 20th result, expecting to find
            the next 7 results. Then request the CursoredPage
            before the cursor of the first entry of the page,
            expecting to find the previous 7 results.
            Then request the CursoredPage after the last entry
            of the original page, expecting to find the next 7.
            """)
    public void testAfterCursorWithTotals() {
        // The query for this test returns 1-35 and 49 in the following order:
        //
        // 35 34 33 32 49 24 23 22 21 20 19 18 17 16 31 30 29 28 27 26 25 08 15 14 13 12 11 10 09 07 06 05 04 03 02 01
        //                                                             ^^^^^^ page 1 ^^^^^^
        //                                        ^^^ previous page ^^
        //                                                                                  ^^^^^ next page ^^^^

        Order<NaturalNumber> order = Order.by(Sort.asc("floorOfSquareRoot"),
                                              Sort.desc("id"));
        PageRequest middle7 = PageRequest.afterCursor(
                Cursor.forKey((short) 5,
                              5L,
                              26L), // 20th result, requires 5 bits, √ rounds to 5
                4L, 7, true);

        CursoredPage<NaturalNumber> page;
        try {
            page = positives.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(
                    6L,
                    50L,
                    middle7,
                    order);
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

        assertEquals(List.of(
                        25L, // 5 bits required, square root rounds down to 5
                        8L, // 4 bits required, square root rounds down to 2
                        15L, 14L, 13L, 12L, 11L // 4 bits required,
                                                // square root rounds down to 3
                     ),
                     page.stream()
                         .map(NaturalNumber::getId)
                         .toList());

        assertEquals(7, page.numberOfElements());

        assertTrue(page.hasPrevious());

        CursoredPage<NaturalNumber> previousPage;
        try {
            previousPage = positives.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(
                    6L,
                    50L,
                    page.previousPageRequest(),
                    order);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of(
                        16L, // 4 bits required, square root rounds down to 4
                        31L, 30L, 29L, 28L, 27L, 26L // 5 bits required,
                                                     // square root rounds down to 5
                     ),
                     previousPage.stream()
                                 .map(NaturalNumber::getId)
                                 .toList());

        assertEquals(7, previousPage.numberOfElements());

        CursoredPage<NaturalNumber> nextPage;
        try {
            nextPage = positives.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(
                    6L,
                    50L,
                    page.nextPageRequest(),
                    order);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of(
                        10L, 9L, // 4 bits required, √ rounds down to 3
                        7L, 6L, 5L, 4L, // 3 bits required, √ rounds down to 2
                        3L // 2 bits required, √ rounds down to 1
                     ),
                     nextPage.stream()
                             .map(NaturalNumber::getId)
                             .toList());

        assertEquals(7, nextPage.numberOfElements());
    }

    @Assertion(id = "133", strategy = """
            Request a CursoredPage of results where none match
            the query, expecting an empty CursoredPage with
            0 results.
            """)
    public void testCursoredPageOfNothingNoTotals() {
        // There are no numbers larger than 30 which have a square root
        // that rounds down to 3.
        PageRequest pagination = PageRequest.ofSize(33)
                                            .afterCursor(Cursor.forKey(30L))
                                            .withoutTotal();

        CursoredPage<NaturalNumber> page;
        try {
            page = numbers.findByFloorOfSquareRootOrderByIdAsc(3L, pagination);
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

        assertFalse(page.hasContent());
        assertEquals(0, page.content().size());
        assertEquals(0, page.numberOfElements());
    }

    @Assertion(id = "133", strategy = """
            Request a CursoredPage of results where none
            match the query, expecting an empty CursoredPage
            with 0 results.
            """)
    public void testCursoredPageOfNothingWithTotals() {

        CursoredPage<NaturalNumber> page;
        try {
            // There are no positive integers less than 4 which have a
            // square root that rounds down to something other than 1.
            page = positives.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(
                    1L,
                    4L,
                    PageRequest.ofPage(1L),
                    Order.by());
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
            fail("nextPageRequest must raise NoSuchElementException when" +
                 " current page is empty.");
        } catch (NoSuchElementException x) {
            // expected
        }

        try {
            page.previousPageRequest();
            fail("previousPageRequest must raise NoSuchElementException when" +
                 " current page is empty.");
        } catch (NoSuchElementException x) {
            // expected
        }
    }

    @Assertion(id = "133", strategy = """
            Request the first CursoredPage of 6 results, expecting
            to find all 6, then request the next CursoredPage and
            the CursoredPage after that, expecting to find all results.
            """)
    public void testInitialPageOf6AndNextPagesNoTotals() {
        PageRequest first6 = PageRequest.ofSize(6).withoutTotal();
        CursoredPage<NaturalNumber> page;

        try {
            page = numbers.findByFloorOfSquareRootOrderByIdAsc(7L, first6);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of(49L, 50L, 51L, 52L, 53L, 54L),
                     page.stream()
                         .map(NaturalNumber::getId)
                         .toList());

        assertEquals(6, page.numberOfElements());

        try {
            page = numbers.findByFloorOfSquareRootOrderByIdAsc(
                   7L,
                   page.nextPageRequest());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(6, page.numberOfElements());

        assertEquals(List.of(55L, 56L, 57L, 58L, 59L, 60L),
                     page.stream()
                         .map(NaturalNumber::getId)
                         .toList());

        page = numbers.findByFloorOfSquareRootOrderByIdAsc(
                7L,
                page.nextPageRequest());


        assertEquals(List.of(61L, 62L, 63L),
                     page.stream()
                         .map(NaturalNumber::getId)
                         .toList());

        assertEquals(3, page.numberOfElements());
    }

    @Assertion(id = "133", strategy = """
            Request the first CursoredPage of 8 results, expecting
            to find all 8, then request the next CursoredPage and
            the CursoredPage after that, expecting to find all results.
            """)
    public void testInitialPageOf8AndNextPagesWithTotals() {
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
            page = positives.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(
                    4L,
                    33L,
                    first8,
                    order);
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

        assertEquals(List.of(32L, 25L, 26L, 27L, 28L, 29L, 30L, 31L),
                     page.stream()
                         .map(NaturalNumber::getId)
                         .toList());

        try {
            page = positives.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(
                    4L,
                    33L,
                    page.nextPageRequest(),
                    order);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of(8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L),
                     page.stream()
                         .map(NaturalNumber::getId)
                         .toList());

        assertEquals(8, page.numberOfElements());

        page = positives.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(
                4L,
                33L,
                page.nextPageRequest(),
                order);

        assertEquals(7, page.numberOfElements());

        assertEquals(List.of(4L, 5L, 6L, 7L, 2L, 3L, 1L),
                     page.stream()
                         .map(NaturalNumber::getId)
                         .toList());
    }

}
