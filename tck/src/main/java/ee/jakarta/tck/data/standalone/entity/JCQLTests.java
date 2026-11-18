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
import java.util.UUID;

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
import ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbers;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbersPopulator;
import ee.jakarta.tck.data.framework.read.only.PositiveIntegers;
import ee.jakarta.tck.data.framework.utilities.DatabaseType;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import ee.jakarta.tck.data.framework.utilities.TestPropertyUtility;
import jakarta.data.Limit;
import jakarta.data.Sort;
import jakarta.data.page.CursoredPage;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.inject.Inject;

/**
 * Tests for repository methods that directly supply a
 * Jakarta Common Query Language (JCQL) query to execute,
 * typically via the Query annotation.
 */
@AnyEntity
@ReadOnlyTest
@Standalone
public class JCQLTests {

    private final DatabaseType type =
            TestProperty.databaseType.getDatabaseType();

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(JCQLTests.class,
                            Box.class,
                            Boxes.class,
                            Coordinate.class,
                            MultipleEntityRepo.class);
    }

    @Inject
    AsciiCharacters characters;

    @Inject
    Boxes boxes;

    @Inject
    NaturalNumbers numbers;

    @Inject
    PositiveIntegers positives; // shares read-only data with NaturalNumbers

    @Inject
    MultipleEntityRepo shared;

    // Inject happens after BeforeClass so this is needed before each test
    @BeforeEach
    public void setup() {
        NaturalNumbersPopulator.get().populate(numbers);
        AsciiCharactersPopulator.get().populate(characters);
    }

    @Assertion(id = "458", strategy = """
            Use a repository method with a JCQL query that has no clauses.
            """)
    public void testEmptyQuery() {

        try {
            assertEquals(List.of('a', 'b', 'c', 'd', 'e', 'f'),
                         characters.all(Limit.range(97, 102), Sort.asc("id"))
                             .map(AsciiCharacter::getThisCharacter)
                             .toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfQueryWithoutWhere() &&
                type.capableOfSingleSort()) {
                throw x;
            }
        }
    }

    @Assertion(id = "458", strategy = """
            Use a repository method with a JCQL Query that specifies an
            enum literal and a boolean false literal.
            """)
    public void testLiteralEnumAndLiteralFalse() {

        NaturalNumber two;
        try {
            two = numbers.two().orElseThrow();
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(2L, two.getId());
        assertEquals(NumberType.PRIME, two.getNumType());
        assertEquals(Short.valueOf((short) 2), two.getNumBitsRequired());
    }

    @Assertion(id = "458", strategy = """
            Use a repository method with a JCQL Query that specifies
            literal Integer values.
            """)
    public void testLiteralInteger() {

        try {
            assertEquals(24, characters.twentyFour());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfGreaterThanEqual() &&
                type.capableOfLessThanEqual()) {
                throw x;
            } else {
                return;
            }
        }
    }

    @Assertion(id = "458", strategy = """
            Use a repository method with a JCQL Query that specifies
            literal String values.
            """)
    public void testLiteralString() {

        try {
            assertEquals(List.of('J', 'K', 'L', 'M'),
                         characters.jklOr("4d")
                             .map(AsciiCharacter::getThisCharacter)
                             .sorted()
                             .toList());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfIn() &&
                type.capableOfNotEqual()) {
                throw x;
            }
        }
    }

    @Assertion(id = "458", strategy = """
            Use a repository method with a JDQL Query that specifies a
            boolean true literal.
            """)
    public void testLiteralTrue() {
        Page<Long> page1;
        try {
            page1 = numbers.oddsFrom21To(40L, PageRequest.ofSize(5));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfBetween() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        try {
            assertEquals(10L, page1.totalElements());
            assertEquals(2L, page1.totalPages());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfCount()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of(21L, 23L, 25L, 27L, 29L), page1.content());

        assertEquals(true, page1.hasNext());

        Page<Long> page2 =
                numbers.oddsFrom21To(40L, page1.nextPageRequest());

        assertEquals(List.of(31L, 33L, 35L, 37L, 39L), page2.content());

        if (page2.hasNext()) {
            Page<Long> page3 =
                    numbers.oddsFrom21To(40L, page2.nextPageRequest());
            assertEquals(false, page3.hasContent());
            assertEquals(false, page3.hasNext());
        }
    }

    @Assertion(id = "458", strategy = """
            Use a repository method with a JCQL query that consists of only an
            ORDER BY clause.
            """)
    public void testPartialQueryOrderBy() {

        assertEquals(List.of('A', 'B', 'C', 'D', 'E', 'F'),
                     characters.alphabetic(Limit.range(65, 70))
                         .map(AsciiCharacter::getThisCharacter)
                         .toList());
    }

    @Assertion(id = "458", strategy = """
            Use a repository method with a JCQL query that consists of only the
            SELECT and ORDER BY clauses.
            """)
    public void testPartialQuerySelectAndOrderBy() {

        Character[] chars = characters.reverseAlphabetic(Limit.range(6, 13));
        for (int i = 0; i < chars.length; i++) {
            assertEquals(Character.valueOf("zyxwvuts".charAt(i)), chars[i]);
        }
    }

    @Assertion(id = "458", strategy = """
            Use a repository method with a JCQL query that uses the
            NOT operator with LIKE, IN, and BETWEEN.
            """)
    public void testQueryWithNot() {

        // 'NOT LIKE' excludes '@'
        // 'NOT IN' excludes 'E' and 'G'
        // 'NOT BETWEEN' excludes 'H' through 'N'.
        Character[] abcdfo;
        try {
            abcdfo = characters.getABCDFO();
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfNotBetween() &&
                type.capableOfNotIn() &&
                type.capableOfNotLike() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(6, abcdfo.length);
        for (int i = 0; i < abcdfo.length; i++) {
            assertEquals(Character.valueOf("ABCDFO".charAt(i)), abcdfo[i]);
        }
    }

    @Assertion(id = "458", strategy = """
            Use a repository method with a JCQL query that uses the
            NULL keyword.
            """)
    public void testQueryWithNull() {
        try {
            assertEquals("4a", characters.hex('J').orElseThrow());
            assertEquals("44", characters.hex('D').orElseThrow());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfNotNull()) {
                throw x;
            } else {
                return;
            }
        }
    }

    @Assertion(id = "458", strategy = """
            Use a repository method with a JCQL query that relies on the
            OR operator.
            """)
    public void testQueryWithOr() {
        PageRequest page1Request = PageRequest.ofSize(4);
        CursoredPage<NaturalNumber> page1;

        try {
            page1 = positives.withBitCountOrOfTypeAndBelow(
                    (short) 4,
                    NumberType.COMPOSITE, 20L,
                    Sort.desc("numBitsRequired"),
                    Sort.asc("id"),
                    page1Request);
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

        assertEquals(List.of(16L, 18L, 8L, 9L),
                     page1.stream()
                          .map(NaturalNumber::getId)
                          .toList());

        assertEquals(true, page1.hasTotals());
        assertEquals(true, page1.hasNext());
        try {
            assertEquals(3L, page1.totalPages());
            assertEquals(12L, page1.totalElements());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfCount()) {
                throw x;
            } else {
                return;
            }
        }

        CursoredPage<NaturalNumber> page2;

        try {
            page2 = positives.withBitCountOrOfTypeAndBelow(
                    (short) 4,
                    NumberType.COMPOSITE, 20L,
                    Sort.desc("numBitsRequired"),
                    Sort.asc("id"),
                    page1.nextPageRequest());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfOr()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of(10L, 11L, 12L, 13L),
                     page2.stream()
                          .map(NaturalNumber::getId)
                          .toList());

        assertEquals(true, page2.hasNext());

        CursoredPage<NaturalNumber> page3 =
                positives.withBitCountOrOfTypeAndBelow(
                        (short) 4,
                        NumberType.COMPOSITE, 20L,
                        Sort.desc("numBitsRequired"),
                        Sort.asc("id"),
                        page2.nextPageRequest());

        assertEquals(List.of(14L, 15L, 4L, 6L),
                     page3.stream()
                          .map(NaturalNumber::getId)
                          .toList());

        if (page3.hasNext()) {
            CursoredPage<NaturalNumber> page4 =
                    positives.withBitCountOrOfTypeAndBelow(
                            (short) 4,
                            NumberType.COMPOSITE, 20L,
                            Sort.desc("numBitsRequired"),
                            Sort.asc("id"),
                            page3.nextPageRequest());
            assertEquals(false, page4.hasContent());
        }
    }

    @Assertion(id = "458", strategy = """
            Use a repository method with a JCQL query that uses parenthesis
            to make OR be evaluated before AND.
            """)
    public void testQueryWithParenthesis() {

        try {
            assertEquals(
                    List.of(15L, 7L, 5L, 3L, 1L),
                    positives.oddAndEqualToOrBelow(15L, 9L));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfLessThan() &&
                type.capableOfMultipleSort() &&
                type.capableOfOr() &&
                type.capableOfParentheses()) {
                throw x;
            } else {
                return;
            }
        }
    }

    @Assertion(id = "458", strategy = """
            Use a repository method with a JCQL UPDATE query without a WHERE
            clause. This method also tests the addition, subtraction, and
            multiplication operators.
            """)
    public void testUpdateQueryWithoutWhereClause() {
        // Ensure there is no data left over from other tests:

        try {
            shared.removeAll();
        } catch (UnsupportedOperationException x) {
            if (type.capableOfCountingDeletes()) {
                throw x;
            } else if (type.capableOfQueryWithoutWhere()) {
                boxes.deleteAll(boxes.findAll().toList());
            } else {
                return;
            }
        }

        TestPropertyUtility.waitForEventualConsistency();

        boxes.saveAll(List.of(
                Box.of("TestUpdateQueryWithoutWhereClause-01", 125, 117, 44),
                Box.of("TestUpdateQueryWithoutWhereClause-02", 173, 165, 52),
                Box.of("TestUpdateQueryWithoutWhereClause-03", 229, 221, 60)));

        TestPropertyUtility.waitForEventualConsistency();

        boolean resized;
        try {
            // increases length by 12, decreases width by 12, and doubles the
            // height
            assertEquals(3L, shared.resizeAll(12, 2));
            resized = true;
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAssignmentToExpression() &&
                type.capableOfCountingUpdates() &&
                type.capableOfQueryWithoutWhere()) {
                throw x;
            } else {
                return;
            }
        }

        TestPropertyUtility.waitForEventualConsistency();

        if (resized) {
            Box b1 = boxes.findById("TestUpdateQueryWithoutWhereClause-01")
                    .orElseThrow();
            assertEquals(137, b1.length); // increased by 12
            assertEquals(105, b1.width); // decreased by 12
            assertEquals(88, b1.height); // increased by factor of 2

            Box b2 = boxes.findById("TestUpdateQueryWithoutWhereClause-02")
                    .orElseThrow();
            assertEquals(185, b2.length); // increased by 12
            assertEquals(153, b2.width); // decreased by 12
            assertEquals(104, b2.height); // increased by factor of 2

            Box b3 = boxes.findById("TestUpdateQueryWithoutWhereClause-03")
                    .orElseThrow();
            assertEquals(241, b3.length); // increased by 12
            assertEquals(209, b3.width); // decreased by 12
            assertEquals(120, b3.height); // increased by factor of 2
        }

        try {
            long removeAllResult = shared.removeAll();
            assertEquals(3, removeAllResult);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfCountingDeletes()) {
                throw x;
            } else if (type.capableOfQueryWithoutWhere()) {
                boxes.deleteAll(boxes.findAll().toList());
            } else {
                return;
            }
        }

        TestPropertyUtility.waitForEventualConsistency();

        try {
            assertEquals(0L, shared.resizeAll(2, 1));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAssignmentToExpression() &&
                type.capableOfCountingUpdates() &&
                type.capableOfQueryWithoutWhere()) {
                throw x;
            } else {
                return;
            }
        }
    }

    @Assertion(id = "458", strategy ="""
            Use a repository method with a JCQL UPDATE query with a WHERE
            clause. This method also tests the assignment and division
            operators.
            """)
    public void testUpdateQueryWithWhereClause() {
        try {
            // Ensure there is no data left over from other tests:
            shared.deleteIfPositive();
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCountingDeletes()) {
                throw x;
            } else {
                try {
                    shared.deleteIfPositiveWithoutReturnRecords();
                } catch (UnsupportedOperationException u) {
                    if (type.capableOfConditionalDelete() &&
                        type.capableOfConstraintsOnNonIdAttributes()) {
                        throw x;
                    } else {
                        return;
                    }
                }
            }
        }

        UUID id1 = shared.create(Coordinate.of("first", 1.41d, 5.25f)).id;
        UUID id2 = shared.create(Coordinate.of("second", 2.2d, 2.34f)).id;

        TestPropertyUtility.waitForEventualConsistency();

        float c1yExpected;
        double c1xExpected;
        try {
            assertEquals(1, shared.move(id1, 1.23d, 1.5f));
            c1yExpected = 3.5f; // 5.25 / 1.5 = 3.5
            c1xExpected = 1.23D;
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAssignmentToExpression() &&
                type.capableOfCountingUpdates()) {
                throw x;
            } else {
                c1yExpected = 5.25f;
                c1xExpected = 1.41D;// no change
            }
        }

        TestPropertyUtility.waitForEventualConsistency();

        Coordinate c1 = shared.withUUID(id1).orElseThrow();
        assertEquals(c1xExpected, c1.x, 0.001d);
        assertEquals(c1yExpected, c1.y, 0.001f);

        Coordinate c2 = shared.withUUID(id2).orElseThrow();
        assertEquals(2.2d, c2.x, 0.001d);
        assertEquals(2.34f, c2.y, 0.001f);

        try {
            assertEquals(2, shared.deleteIfPositive());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                    type.capableOfConstraintsOnNonIdAttributes() &&
                    type.capableOfCountingDeletes()) {
                    throw x;
            } else {
                try {
                    shared.deleteIfPositiveWithoutReturnRecords();
                } catch (UnsupportedOperationException u) {
                    if (type.capableOfConditionalDelete() &&
                        type.capableOfConstraintsOnNonIdAttributes()) {
                        throw x;
                    } else {
                        return;
                    }
                }
            }
        }
        TestPropertyUtility.waitForEventualConsistency();

        assertEquals(false, shared.withUUID(id1).isPresent());
        assertEquals(false, shared.withUUID(id2).isPresent());
    }
}
