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

import java.util.List;
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
import jakarta.data.constraint.Between;
import jakarta.inject.Inject;

/**
 * Tests for Sort supplied in various ways to repository methods.
 */
@AnyEntity
@ReadOnlyTest
@Standalone
public class SortTests {

    public static final Logger log =
            Logger.getLogger(SortTests.class.getCanonicalName());

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
                         .addClasses(SortTests.class);
    }

    @Assertion(id = "133", strategy = """
            Use a repository method with one Sort parameter specifying
            descending order, and verify all results are returned
            and are in descending order according to the sort criteria.
            """)
    public void testDescendingSort() {
        Stream<AsciiCharacter> stream;
        try {
            stream = characters.findByIdBetween(
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

        assertEquals(List.of('9', '8', '7', '6', '5', '4'),
                     stream.map(AsciiCharacter::getThisCharacter)
                           .toList());
    }

    @Assertion(id = "133", strategy = """
            Use a repository method with two Sort parameters specifying a
            mixture of ascending and descending order, and verify all results
            are returned and are ordered according to the sort criteria.
            """)
    public void testMultipleSort() {
        NaturalNumber[] nums;
        try {
            nums = numbers.findByIdLessThan(
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

        assertEquals(List.of(1L, // 1 bit
                             3L, 2L, // 2 bits
                             7L, 6L, 5L, 4L, // 3 bits
                             14L, 13L, 12L, 11L, 10L, 9L, 8L), // 4 bits
                     Stream.of(nums)
                           .map(NaturalNumber::getId)
                           .toList());
    }

    // This test switched to @OrderBy because we already had 2 tests
    // covering this precedence scenario for the OrderBy keyword
    @Assertion(id = "133", strategy = """
            Use a repository method annotated OrderBy (static) and an Order
            parameter with additional Sort instances (dynamic), verfying that
            all results are returned and are ordered first by the static sort
            criteria, followed by the dynamic sort criteria when the value(s)
            being compared by the static criteria match.
            """)
    public void testOrderAnnoByHasPrecedenceOverSorts() {
        Stream<NaturalNumber> nums;
        try {
            nums = numbers.findBetween(
                    Between.bounds(5L, 24L),
                    Order.by(Sort.desc("floorOfSquareRoot"), Sort.asc("id")));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfBetween() &&
                type.capableOfMultipleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of(17L, 19L, 23L, // prime; √ rounds to ⌊4⌋
                             11L, 13L, // prime; √ rounds to ⌊3⌋
                             5L, 7L, // prime; √ rounds to ⌊2⌋
                             16L, 18L, 20L, 21L, 22L, 24L, // composite; √ ⌊4⌋
                             9L, 10L, 12L, 14L, 15L, // composite; √ ⌊3⌋
                             6L, 8L), // composite; √ rounds to ⌊2⌋
                     nums.map(NaturalNumber::getId)
                         .toList());
    }

    @Assertion(id = "133", strategy = """
            Find a list of entities, querying by entity attributes with names
            that match the method parameter names, with results capped by a
            Limit parameter and sorted according to an Order parameter with
            variable arguments Sort.
            """)
    public void testSortWithLimit() {
        List<NaturalNumber> oddCompositeNumbers;
        try {
            oddCompositeNumbers = positives.findOdd(
                    true,
                    NumberType.COMPOSITE,
                    Limit.of(10),
                    Order.by(Sort.asc("floorOfSquareRoot"),
                             Sort.desc("numBitsRequired"),
                             Sort.asc("id")));


            assertEquals(List.of(
                            9L, 15L,  // 3 <= sqrt < 4, 4 bits
                            21L,      // 4 <= sqrt < 5, 5 bits
                            33L, 35L, // 5 <= sqrt < 6, 6 bits
                            25L, 27L, // 5 <= sqrt < 6, 5 bits
                            39L, 45L, // 6 <= sqrt < 7, 6 bits
                            49L),     // 7 <= sqrt < 8, 6 bits
                         oddCompositeNumbers
                            .stream()
                            .map(NaturalNumber::getId)
                            .toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfMultipleSort()) {
                throw x;
            } else {
                return;
            }
        }

        List<NaturalNumber> evenPrimeNumbers = positives.findOdd(
                false,
                NumberType.PRIME,
                Limit.of(9),
                Order.by());

        assertEquals(1, evenPrimeNumbers.size());
        NaturalNumber num = evenPrimeNumbers.get(0);
        assertEquals(2L, num.getId());
        assertEquals(1L, num.getFloorOfSquareRoot());
        assertEquals(Short.valueOf((short) 2), num.getNumBitsRequired());
        assertEquals(NumberType.PRIME, num.getNumType());
        assertEquals(false, num.isOdd());
    }

    @Assertion(id = "133", strategy = """
            Use a repository method with varargs Sort... specifying a
            mixture of ascending and descending sorts, and verify all results
            are returned and are ordered according to the sort criteria.
            """)
    public void testVarargsSort() {
        List<NaturalNumber> list;
        try {
            list = numbers.findByIdLessThanEqual(
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

        assertEquals(List.of(2L, 3L, // √ rounds to ⌊1⌋; 2 bits
                             1L, // √ rounds to ⌊1⌋; 1 bit
                             8L, // √ rounds to ⌊2⌋; 4 bits
                             4L, 5L, 6L, 7L, // √ rounds to ⌊2⌋; 3 bits
                             9L, 10L, 11L, 12L), // √ rounds to ⌊3⌋; 4 bits
                     list.stream()
                         .map(NaturalNumber::getId)
                         .toList());
    }

}
