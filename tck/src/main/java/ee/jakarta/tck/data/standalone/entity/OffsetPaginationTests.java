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

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
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
import ee.jakarta.tck.data.framework.read.only._AsciiChar;
import ee.jakarta.tck.data.framework.read.only._AsciiCharacter;
import ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType;
import ee.jakarta.tck.data.framework.utilities.DatabaseType;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.inject.Inject;

/**
 * Tests of offset pagination.
 */
@AnyEntity
@ReadOnlyTest
@Standalone
public class OffsetPaginationTests {

    public static final Logger log =
            Logger.getLogger(OffsetPaginationTests.class.getCanonicalName());

    @Inject
    NaturalNumbers numbers;

    @Inject
    PositiveIntegers positives; // shares same read-only data with NaturalNumbers

    @Inject
    AsciiCharacters characters;

    // Inject doesn't happen until after BeforeClass, so this is necessary
    // before each test
    @BeforeEach
    public void beforeEach() {
        NaturalNumbersPopulator.get().populate(numbers);
        AsciiCharactersPopulator.get().populate(characters);
    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(OffsetPaginationTests.class);
    }

    private final DatabaseType type = TestProperty.databaseType.getDatabaseType();

    @Assertion(id = "133", strategy = """
            Request a Page higher than the final Page,
            expecting an empty Page with 0 results.
            """)
    public void testBeyondFinalPage() {
        PageRequest sixth = PageRequest.ofPage(6).size(10);
        Page<AsciiCharacter> page;
        try {
            page = characters.findByNumericValueBetween(
                    48,
                    90,
                    sixth,
                    Order.by(_AsciiCharacter.numericValue.asc()));
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

    @Assertion(id = "133", strategy = """
            Request a page without a total count of results,
            requesting a page number higher than the final page,
            and expecting an empty page with 0 results.
            """)
    public void testBeyondFinalPageWithoutTotal() {
        PageRequest sixth = PageRequest.ofPage(6).size(5).withoutTotal();
        Page<NaturalNumber> page;
        try {
            page = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(
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

    @Assertion(id = "133", strategy = """
            Request the last Page of up to 10 results,
            expecting to find the final 3.
            """)
    public void testFinalPageOfUpTo10() {
        PageRequest fifthPageRequest = PageRequest.ofPage(5).size(10);
        Page<AsciiCharacter> page;
        try {
            page = characters.findByNumericValueBetween(48, 90, fifthPageRequest,
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

        assertEquals(5, page.pageRequest().pageNumber());
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

    @Assertion(id = "133", strategy = """
            Request the last page of up to 5 results,
            expecting to find the final 2. Request the page
            without requesting a total count of results.
            """)
    public void testFinalPageOfUpTo5WithoutTotal() {
        PageRequest fifth = PageRequest.ofPage(5).size(5).withoutTotal();
        Page<NaturalNumber> page;
        try {
            page = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(
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
        assertEquals(5, page.pageRequest().pageNumber());
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

    @Assertion(id = "133", strategy = """
            Use the findAll method of a repository that inherits
            from BasicRepository to request a Page 2 of size 12,
            specifying a PageRequest that requires a mixture of
            ascending and descending sort. Verify the page contains
            all 12 expected entities, sorted according to the mixture
            of ascending and descending sort orders specified.
            """)
    public void testFindAllWithPagination() {
        PageRequest page2request = PageRequest.ofPage(2).size(12);
        Page<NaturalNumber> page2;
        try {
            page2 = positives.findAll(page2request,
                    Order.by(
                            Sort.asc("floorOfSquareRoot"),
                            Sort.desc("id")));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfMultipleSort() &&
                type.capableOfQueryWithoutWhere()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(12, page2.numberOfElements());
        assertEquals(2, page2.pageRequest().pageNumber());

        assertEquals(List.of(11L, 10L, 9L, // square root rounds down to 3
                             24L, 23L, 22L, 21L, 20L,
                             19L, 18L, 17L, 16L), // square root rounds down to 4
                     page2.stream()
                          .map(NaturalNumber::getId)
                          .collect(Collectors.toList()));
    }

    @Assertion(id = "133", strategy = """
            Find a page of entities, with entity attributes
            identified by the parameter names and matching
            the parameter values.
            """)
    public void testFindPage() {
        PageRequest page1Request = PageRequest.ofSize(7);

        Page<NaturalNumber> page1;
        try {
            page1 = positives.findMatching(
                    9L,
                    (short) 7,
                    NumberType.COMPOSITE,
                    page1Request,
                    Sort.desc("id"));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of(99L, 98L, 96L, 95L, 94L, 93L, 92L),
                page1.stream()
                     .map(NaturalNumber::getId)
                     .collect(Collectors.toList()));

        assertTrue(page1.hasNext());

        Page<NaturalNumber> page2 = positives.findMatching(
                9L,
                (short) 7,
                NumberType.COMPOSITE,
                page1.nextPageRequest(),
                Sort.desc("id"));

        assertEquals(List.of(91L, 90L, 88L, 87L, 86L, 85L, 84L),
                page2.stream()
                     .map(NaturalNumber::getId)
                     .collect(Collectors.toList()));

        assertTrue(page2.hasNext());

        Page<NaturalNumber> page3 = positives.findMatching(
                9L, (short)
                7,
                NumberType.COMPOSITE,
                page2.nextPageRequest(),
                Sort.desc("id"));

        assertEquals(List.of(82L, 81L),
                page3.stream()
                     .map(NaturalNumber::getId)
                     .collect(Collectors.toList()));

        assertFalse(page3.hasNext());
    }

    @Assertion(id = "133", strategy = """
            Request the first Page of 10 results, expecting to find all 10.
            From the Page, verify the totalElements and totalPages expected.
            """)
    public void testFirstPageOf10() {
        PageRequest first10 = PageRequest.ofSize(10);
        Page<AsciiCharacter> page;
        try {
            page = characters.findByNumericValueBetween(48, 90, first10,
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

        assertEquals(1, page.pageRequest().pageNumber());
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

    @Assertion(id = "133", strategy = """
            Request the first page of 5 results,
            expecting to find all 5. Request the page
            without requesting a total count of results.
            """)
    public void testFirstPageOf5WithoutTotal() {
        PageRequest first5 = PageRequest.ofSize(5).withoutTotal();
        Page<NaturalNumber> page;
        try {
            page = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(
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

    @Assertion(id = "133", strategy = """
            Request a Page of results where none match the query,
            expecting an empty Page with 0 results.
            """)
    public void testPageOfNothing() {
        PageRequest pagination = PageRequest.ofSize(6);
        Page<AsciiCharacter> page;
        try {
            page = characters.findByNumericValueBetween(150, 160, pagination,
                    Order.by(_AsciiCharacter.id.asc()));
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

    @Assertion(id = "133", strategy = """
            Request a page of results without a total count
            of results, where none match the query,
            expecting an empty page with 0 results.
            """)
    public void testPageOfNothingWithoutTotal() {
        PageRequest pagination = PageRequest.ofSize(5).withoutTotal();
        Page<NaturalNumber> page;
        try {
            page = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(
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

    @Assertion(id = "133", strategy = """
            Use the StaticMetamodel to obtain ascending Sorts
            for an entity attribute in a type-safe manner.
            """)
    public void testStaticMetamodelAscendingSorts() {
        assertEquals(Sort.asc("id"),
                     _AsciiChar.id.asc());

        assertEquals(Sort.ascIgnoreCase(_AsciiChar.HEXADECIMAL),
                     _AsciiChar.hexadecimal.ascIgnoreCase());

        assertEquals(Sort.ascIgnoreCase("thisCharacter"),
                     _AsciiChar.thisCharacter.ascIgnoreCase());

        PageRequest pageRequest = PageRequest.ofSize(6);
        Page<AsciiCharacter> page1;
        try {
            page1 = characters.findByNumericValueBetween(
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

    @Assertion(id = "133", strategy = """
            Use a pre-generated StaticMetamodel to obtain
            ascending Sorts for an entity attribute in a
            type-safe manner.
            """)
    public void testStaticMetamodelAscendingSortsPreGenerated() {
        assertEquals(Sort.asc("id"),
                     _AsciiCharacter.id.asc());

        assertEquals(Sort.asc("isControl"),
                     _AsciiCharacter.isControl.asc());

        assertEquals(Sort.ascIgnoreCase(_AsciiCharacter.HEXADECIMAL),
                     _AsciiCharacter.hexadecimal.ascIgnoreCase());

        assertEquals(Sort.ascIgnoreCase("thisCharacter"),
                     _AsciiCharacter.thisCharacter.ascIgnoreCase());

        PageRequest pageRequest = PageRequest.ofSize(7);
        Page<AsciiCharacter> page1;
        try {
            page1 = characters.findByNumericValueBetween(
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

    @Assertion(id = "133", strategy = """
            Request the third Page of 10 results, expecting to
            find all 10. Request the next Page via nextPageRequest,
            expecting page number 4 and another 10 results.
            """)
    public void testThirdAndFourthPagesOf10() {
        Order<AsciiCharacter> order =
                Order.by(_AsciiCharacter.numericValue.asc());
        PageRequest third10 = PageRequest.ofPage(3).size(10);
        Page<AsciiCharacter> page;
        try {
            page = characters.findByNumericValueBetween(
                    48,
                    90,
                    third10,
                    order); // 'D' to 'M'
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

        assertEquals(3, page.pageRequest().pageNumber());
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
        page = characters.findByNumericValueBetween(
                48,
                90,
                fourth10,
                order); // 'N' to 'W'

        assertEquals(4, page.pageRequest().pageNumber());
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

    @Assertion(id = "133", strategy = """
            Request the third page of 5 results, expecting to
            find all 5. Request the next page via nextPageRequest,
            expecting page number 4 and another 5 results.
            Request the pages without requesting a total count
            of results.
            """)
    public void testThirdAndFourthPageOf5WithoutTotal() {
        PageRequest third5 = PageRequest.ofPage(3).size(5).withoutTotal();
        Sort<NaturalNumber> sort = Sort.desc("id");
        Page<NaturalNumber> page;
        try {
            page = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(
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

        assertEquals(3, page.pageRequest().pageNumber());
        assertEquals(5, page.numberOfElements());

        assertEquals(List.of(37L, 31L, 29L, 23L, 19L),
                     page.stream()
                         .map(NaturalNumber::getId)
                         .toList());

        assertTrue(page.hasNext());
        PageRequest fourth5 = page.nextPageRequest();

        page = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(
                NumberType.PRIME,
                8L,
                fourth5,
                sort);

        assertEquals(4, page.pageRequest().pageNumber());
        assertEquals(5, page.numberOfElements());

        assertEquals(List.of(17L, 13L, 11L, 7L, 5L),
                     page.stream()
                         .map(NaturalNumber::getId)
                         .toList());
    }

}
