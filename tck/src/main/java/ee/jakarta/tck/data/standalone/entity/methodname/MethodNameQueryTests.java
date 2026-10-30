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
package ee.jakarta.tck.data.standalone.entity.methodname;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

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
import ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType;
import ee.jakarta.tck.data.framework.utilities.DatabaseType;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import jakarta.data.Limit;
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.inject.Inject;

/**
 * Tests for the Query by Method Name pattern.
 */
@AnyEntity
@ReadOnlyTest
@Standalone
public class MethodNameQueryTests {

    public static final Logger log =
            Logger.getLogger(MethodNameQueryTests.class.getCanonicalName());

    private final DatabaseType type =
            TestProperty.databaseType.getDatabaseType();

    @Inject
    AsciiCharacters characters;

    @Inject
    NaturalNumbers numbers;

    @Inject
    PositiveIntegers positives; // shares read-only data with NaturalNumbers

    // Inject doesn't happen until after BeforeClass, so this is necessary
    // before each test
    @BeforeEach
    public void beforeEach() {
        AsciiCharactersPopulator.get().populate(characters);
        NaturalNumbersPopulator.get().populate(numbers);
    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                         .addClasses(MethodNameQueryTests.class);
    }

    @Assertion(id = "133", strategy = """
            Use a repository method that includes the Contains keyword in
            its method name to query for a substring of a String attribute.
            """)
    public void testContainsKeyword() {
        Collection<AsciiCharacter> found;
        try {
            found = characters.findByHexadecimalContainsAndIsControlNot(
                    "4",
                    true);
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
                found.stream()
                     .map(AsciiCharacter::getHexadecimal)
                     .sorted()
                     .toList());
    }

    @Assertion(id = "133", strategy = """
            Use a repository method that includes the False keyword in
            its method name.
            """)
    public void testFalseKeyword() {
        List<NaturalNumber> even;
        try {
            even = positives.findByIsOddFalseAndIdBetween(50L, 60L);
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
                     even.stream()
                         .map(NaturalNumber::getId)
                         .sorted()
                         .toList());
    }

    @Assertion(id = "133", strategy = """
            Use a repository method with findFirstBy that returns the
            first entity value where multiple results would otherwise
            be found.
            """)
    public void testFindFirst() {
        Optional<AsciiCharacter> none;
        try {
            none = characters
                    .findFirstByHexadecimalStartsWithAndIsControlOrderByIdAsc(
                            "h",
                            false);
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
        assertEquals(true, none.isEmpty());

        AsciiCharacter ch = characters
                .findFirstByHexadecimalStartsWithAndIsControlOrderByIdAsc(
                        "4",
                        false)
                .orElseThrow();
        assertEquals('@', ch.getThisCharacter());
        assertEquals("40", ch.getHexadecimal());
        assertEquals(64, ch.getNumericValue());
    }

    @Assertion(id = "133", strategy = """
            Use a repository method with findFirst3By that returns the
            first 3 results.
            """)
    public void testFindFirst3() {
        AsciiCharacter[] found;

        try {
            found = characters
               .findFirst3ByNumericValueGreaterThanEqualAndHexadecimalEndsWith(
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

    @Assertion(id = "133", strategy = """
            Use a repository method existsByIdGreaterThan confirming the
            correct boolean is returned.
            """)
    public void testGreaterThanKeywordOnExistsMethod() {
        try {
            assertEquals(true, positives.existsByIdGreaterThan(0L));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfGreaterThan()) {
                throw x;
            } else {
                return;
            }
        }
        assertEquals(true, positives.existsByIdGreaterThan(99L));

        // doesn't exist because the table only has 1 to 100
        assertEquals(false, positives.existsByIdGreaterThan(100L));
    }

    @Assertion(id = "133", strategy = """
            Use a repository method with the In keyword.
            """)
    public void testInKeyword() {
        Stream<NaturalNumber> nonPrimes;
        try {
            nonPrimes = positives.findByNumTypeInOrderByIdAsc(
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
                nonPrimes.map(NaturalNumber::getId).toList());

        Stream<NaturalNumber> primes = positives.findByNumTypeInOrderByIdAsc(
                Collections.singleton(NumberType.PRIME),
                Limit.of(6));
        assertEquals(List.of(2L, 3L, 5L, 7L, 11L, 13L),
                primes.map(NaturalNumber::getId).toList());
    }

    @Assertion(id = "133", strategy = """
            Use a repository method with the IgnoreCase keyword.
            """)
    public void testIgnoreCaseKeyword() {
        Stream<AsciiCharacter> found;
        try {
            found = characters
                    .findByHexadecimalIgnoreCaseBetweenAndHexadecimalNotIn(
                            "4c",
                            "5A",
                            Set.of("5"),
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
                found.map(AsciiCharacter::getThisCharacter)
                     .toList());
    }

    @Assertion(id = "133", strategy = """
            Use a repository method countByIdLessThan confirming the correct
            count is returned.
            """)
    public void testLessThanKeywordOnCountMethod() {
        try {
            assertEquals(91L, positives.countByIdLessThan(92L));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfCount() &&
                type.capableOfLessThan()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(0L, positives.countByIdLessThan(1L));
    }

    @Assertion(id = "133", strategy = """
            Use a repository method with the Not keyword.
            """)
    public void testNotKeyword() {
        NaturalNumber[] n;
        try {
            n = numbers.findByNumTypeNot(
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

    @Assertion(id = "133", strategy = """
            Use a repository method with Or, expecting
            UnsupportedOperationException if the underlying database
            is not capable.
            """)
    public void testOrKeyword() {
        Stream<NaturalNumber> found;
        try {
            found = positives.findByNumTypeOrFloorOfSquareRoot(NumberType.ONE,
                                                               2L);
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

    @Assertion(id = "133", strategy = """
            Use a repository method with OrderBy (static) and a Sort parameter
            (dynamic), verfying that all results are returned and are ordered
            first by the static sort criteria, followed by the dynamic sort
            criteria when the value(s) being compared by the static criteria
            match.
            """)
    public void testOrderByKeywordHasPrecedenceOverSorts() {
        PageRequest pagination = PageRequest.ofSize(8);
        Order<NaturalNumber> order = Order.by(Sort.asc("numTypeOrdinal"),
                                              Sort.desc("id"));

        Page<NaturalNumber> page;
        try {
            page = numbers.findByIdLessThanOrderByFloorOfSquareRootDesc(
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

        assertEquals(List.of(23L, 19L, 17L, // √ rounds down to 4; prime
                             24L, 22L, 21L, 20L, 18L), // √ -> 4; composite
                     page.stream()
                         .map(NaturalNumber::getId)
                         .toList());

        assertEquals(true, page.hasNext());
        pagination = page.nextPageRequest();
        page = numbers.findByIdLessThanOrderByFloorOfSquareRootDesc(
                25L,
                pagination,
                order);

        assertEquals(List.of(16L, // √ rounds down to 4; composite
                             13L, 11L, // √ rounds down to 3; prime
                             15L, 14L, 12L, 10L, 9L), // √ -> 3; composite
                     page.stream()
                         .map(NaturalNumber::getId)
                         .toList());

        assertEquals(true, page.hasNext());
        pagination = page.nextPageRequest();
        page = numbers.findByIdLessThanOrderByFloorOfSquareRootDesc(
                25L,
                pagination,
                order);

        assertEquals(List.of(7L, 5L, // √ rounds down to 2; prime
                             8L, 6L, 4L, // √ rounds down to 2; composite
                             1L, // √ rounds down to 1; one
                             3L, 2L), // √ rounds down to 1; prime
                     page.stream()
                         .map(NaturalNumber::getId)
                         .toList());

        if (page.hasNext()) {
            pagination = page.nextPageRequest();
            page = numbers.findByIdLessThanOrderByFloorOfSquareRootDesc(
                    25L,
                    pagination,
                    order);
            assertEquals(false, page.hasContent());
        }
    }

    @Assertion(id = "133", strategy = """
            Use a repository method with the True keyword.
            """)
    public void testTrueKeyword() {
        Iterable<NaturalNumber> odd;
        try {
            odd = positives.findByIsOddTrueAndIdLessThanEqualOrderByIdDesc(
                    10L);
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

        assertEquals(true, it.hasNext());
        assertEquals(9L, it.next().getId());

        assertEquals(true, it.hasNext());
        assertEquals(7L, it.next().getId());

        assertEquals(true, it.hasNext());
        assertEquals(5L, it.next().getId());

        assertEquals(true, it.hasNext());
        assertEquals(3L, it.next().getId());

        assertEquals(true, it.hasNext());
        assertEquals(1L, it.next().getId());

        assertEquals(false, it.hasNext());
    }

}
