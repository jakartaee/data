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
package ee.jakarta.tck.data.standalone.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.data.page.CursoredPage;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;

import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.ReadOnlyTest;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.framework.read.only._AsciiChar;
import ee.jakarta.tck.data.framework.read.only._AsciiCharacter;
import ee.jakarta.tck.data.framework.read.only._NaturalNumber;
import ee.jakarta.tck.data.framework.read.only.AsciiCharacter;
import ee.jakarta.tck.data.framework.read.only.AsciiCharacters;
import ee.jakarta.tck.data.framework.read.only.AsciiCharactersPopulator;
import ee.jakarta.tck.data.framework.read.only.CardinalNumber;
import ee.jakarta.tck.data.framework.read.only.CustomRepository;
import ee.jakarta.tck.data.framework.read.only.HexInfo;
import ee.jakarta.tck.data.framework.read.only.NaturalNumber;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbers;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbersPopulator;
import ee.jakarta.tck.data.framework.read.only.NumberInfo;
import ee.jakarta.tck.data.framework.read.only.PositiveIntegers;
import ee.jakarta.tck.data.framework.read.only.WholeNumber;
import ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType;
import ee.jakarta.tck.data.framework.utilities.DatabaseType;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import ee.jakarta.tck.data.framework.utilities.TestPropertyUtility;
import jakarta.data.Limit;
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.exceptions.EmptyResultException;
import jakarta.data.exceptions.NonUniqueResultException;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.page.PageRequest.Cursor;
import jakarta.inject.Inject;

/**
 * Execute a test with an entity that is dual annotated which means this test
 * can run against a provider that supports any Entity type.
 */
@Standalone
@AnyEntity
@ReadOnlyTest
public class EntityTests {

    public static final Logger log = Logger.getLogger(EntityTests.class.getCanonicalName());

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(EntityTests.class, Box.class, Boxes.class);
    }

    @Inject
    Boxes boxes;

    @Inject
    NaturalNumbers numbers;

    @Inject
    PositiveIntegers positives; // shares same read-only data with NaturalNumbers

    @Inject
    CustomRepository customRepo; // shares same read-only data with NaturalNumbers

    @Inject
    AsciiCharacters characters;

    @Inject
    MultipleEntityRepo shared;

    @BeforeEach
    //Inject doesn't happen until after BeforeClass so this is necessary before each test
    public void setup() {
        assertNotNull(numbers);
        NaturalNumbersPopulator.get().populate(numbers);

        assertNotNull(characters);
        AsciiCharactersPopulator.get().populate(characters);
    }

    private final DatabaseType type = TestProperty.databaseType.getDatabaseType();

    @Assertion(id = "136", strategy = "Ensures that the prepopulation step for readonly entities was successful")
    public void ensureNaturalNumberPrepopulation() {
        assertEquals(100L, numbers.countAll());
        assertTrue(numbers.findById(0L).isEmpty(), "Zero should not have been in the set of natural numbers.");
        assertFalse(numbers.findById(10L).get().isOdd());
    }

    @Assertion(id = "136", strategy = "Ensures that multiple readonly entities will be prepopulated before testing")
    public void ensureCharacterPrepopulation() {
        try {
            assertEquals(127L, characters.countByHexadecimalNotNull());
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of the Null comparison
            } else {
                throw x;
            }
        }

        assertEquals('0', characters.findByNumericValue(48).get().getThisCharacter());
        assertTrue(characters.findByNumericValue(1).get().isControl());
    }

    @Assertion(id = "133",
            strategy = "Use a repository that inherits from BasicRepository and adds some methods of its own. " +
                    "Use both built-in methods and the additional methods.")
    public void testBasicRepository() {

        // custom method from NaturalNumbers:
        try {
            Stream<NaturalNumber> found = numbers.findByIdBetweenOrderByNumTypeOrdinalAsc(
                    50L, 59L,
                    Order.by(Sort.asc("id")));
            List<Long> list = found
                    .map(NaturalNumber::getId)
                    .collect(Collectors.toList());
            assertEquals(List.of(53L, 59L, // first 2 must be primes
                            50L, 51L, 52L, 54L, 55L, 56L, 57L, 58L), // the remaining 8 are composite numbers
                    list);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                // Key-Value databases might not be capable of Between.
            } else {
                throw x;
            }
        }

        // built-in method from BasicRepository:
        assertEquals(60L, numbers.findById(60L).orElseThrow().getId());
    }

    @Assertion(id = "133",
            strategy = "Use a repository that inherits from BasicRepository and defines no additional methods of its own. " +
                    "Use all of the built-in methods.")
    public void testBasicRepositoryBuiltInMethods() {

        // BasicRepository.saveAll
        Iterable<Box> saved = boxes.saveAll(List.of(Box.of("TestBasicRepositoryMethods-01", 119, 120, 169),
                Box.of("TestBasicRepositoryMethods-02", 20, 21, 29),
                Box.of("TestBasicRepositoryMethods-03", 33, 56, 65),
                Box.of("TestBasicRepositoryMethods-04", 45, 28, 53)));
        Iterator<Box> savedIt = saved.iterator();
        assertTrue(savedIt.hasNext());
        Box box1 = savedIt.next();
        assertEquals("TestBasicRepositoryMethods-01", box1.boxIdentifier);
        assertEquals(119, box1.length);
        assertEquals(120, box1.width);
        assertEquals(169, box1.height);
        assertTrue(savedIt.hasNext());
        Box box2 = savedIt.next();
        assertEquals("TestBasicRepositoryMethods-02", box2.boxIdentifier);
        assertEquals(20, box2.length);
        assertEquals(21, box2.width);
        assertEquals(29, box2.height);
        assertTrue(savedIt.hasNext());
        Box box3 = savedIt.next();
        assertEquals("TestBasicRepositoryMethods-03", box3.boxIdentifier);
        assertEquals(33, box3.length);
        assertEquals(56, box3.width);
        assertEquals(65, box3.height);
        assertTrue(savedIt.hasNext());
        Box box4 = savedIt.next();
        assertEquals("TestBasicRepositoryMethods-04", box4.boxIdentifier);
        assertEquals(45, box4.length);
        assertEquals(28, box4.width);
        assertEquals(53, box4.height);
        assertFalse(savedIt.hasNext());

        TestPropertyUtility.waitForEventualConsistency();


        // BasicRepository.save
        box2.length = 21;
        box2.width = 20;
        box2 = boxes.save(box2);
        assertEquals("TestBasicRepositoryMethods-02", box2.boxIdentifier);
        assertEquals(21, box2.length);
        assertEquals(20, box2.width);
        assertEquals(29, box2.height);

        Box box5 = boxes.save(Box.of("TestBasicRepositoryMethods-05", 153, 104, 185));
        assertEquals("TestBasicRepositoryMethods-05", box5.boxIdentifier);
        assertEquals(153, box5.length);
        assertEquals(104, box5.width);
        assertEquals(185, box5.height);

        TestPropertyUtility.waitForEventualConsistency();

        // BasicRepository.deleteAll(Iterable)
        boxes.deleteAll(List.of(box1, box2));

        TestPropertyUtility.waitForEventualConsistency();

        assertEquals(3, boxes.findAll().count());


        // BasicRepository.delete
        boxes.delete(box4);

        TestPropertyUtility.waitForEventualConsistency();

        // BasicRepository.findAll
        Stream<Box> stream = boxes.findAll();
        List<Box> list = stream.sorted(Comparator.comparing(b -> b.boxIdentifier)).toList();
        assertEquals(2, list.size());
        box4 = list.get(0);
        assertEquals("TestBasicRepositoryMethods-03", box3.boxIdentifier);
        assertEquals(33, box3.length);
        assertEquals(56, box3.width);
        assertEquals(65, box3.height);
        box5 = list.get(1);
        assertEquals("TestBasicRepositoryMethods-05", box5.boxIdentifier);
        assertEquals(153, box5.length);
        assertEquals(104, box5.width);
        assertEquals(185, box5.height);

        // BasicRepository.deleteById
        boxes.deleteById("TestBasicRepositoryMethods-03");

        TestPropertyUtility.waitForEventualConsistency();

        // BasicRepository.findById
        assertFalse(boxes.findById("TestBasicRepositoryMethods-03").isPresent());
        box5 = boxes.findById("TestBasicRepositoryMethods-05").orElseThrow();
        assertEquals("TestBasicRepositoryMethods-05", box5.boxIdentifier);
        assertEquals(153, box5.length);
        assertEquals(104, box5.width);
        assertEquals(185, box5.height);

        // BasicRepository.deleteById
        boxes.deleteById("TestBasicRepositoryMethods-05");
        TestPropertyUtility.waitForEventualConsistency();

        assertEquals(0, boxes.findAll().count());
    }

    @Assertion(id = "133", strategy = "Use a repository that inherits from BasicRepository and defines no additional methods of its own. Use all of the built-in methods.")
    public void testBasicRepositoryMethods() {

        // BasicRepository.saveAll
        Iterable<Box> saved = boxes.saveAll(List.of(Box.of("TestBasicRepositoryMethods-01", 119, 120, 169),
                Box.of("TestBasicRepositoryMethods-02", 20, 21, 29),
                Box.of("TestBasicRepositoryMethods-03", 33, 56, 65),
                Box.of("TestBasicRepositoryMethods-04", 45, 28, 53)));
        Iterator<Box> savedIt = saved.iterator();
        assertTrue(savedIt.hasNext());
        Box box1 = savedIt.next();
        assertEquals("TestBasicRepositoryMethods-01", box1.boxIdentifier);
        assertEquals(119, box1.length);
        assertEquals(120, box1.width);
        assertEquals(169, box1.height);
        assertTrue(savedIt.hasNext());
        Box box2 = savedIt.next();
        assertEquals("TestBasicRepositoryMethods-02", box2.boxIdentifier);
        assertEquals(20, box2.length);
        assertEquals(21, box2.width);
        assertEquals(29, box2.height);
        assertTrue(savedIt.hasNext());
        Box box3 = savedIt.next();
        assertEquals("TestBasicRepositoryMethods-03", box3.boxIdentifier);
        assertEquals(33, box3.length);
        assertEquals(56, box3.width);
        assertEquals(65, box3.height);
        assertTrue(savedIt.hasNext());
        Box box4 = savedIt.next();
        assertEquals("TestBasicRepositoryMethods-04", box4.boxIdentifier);
        assertEquals(45, box4.length);
        assertEquals(28, box4.width);
        assertEquals(53, box4.height);
        assertFalse(savedIt.hasNext());

        TestPropertyUtility.waitForEventualConsistency();


        // BasicRepository.save
        box2.length = 21;
        box2.width = 20;
        box2 = boxes.save(box2);
        assertEquals("TestBasicRepositoryMethods-02", box2.boxIdentifier);
        assertEquals(21, box2.length);
        assertEquals(20, box2.width);
        assertEquals(29, box2.height);

        Box box5 = boxes.save(Box.of("TestBasicRepositoryMethods-05", 153, 104, 185));
        assertEquals("TestBasicRepositoryMethods-05", box5.boxIdentifier);
        assertEquals(153, box5.length);
        assertEquals(104, box5.width);
        assertEquals(185, box5.height);

        TestPropertyUtility.waitForEventualConsistency();

        // BasicRepository.deleteAll(Iterable)
        boxes.deleteAll(List.of(box1, box2));

        TestPropertyUtility.waitForEventualConsistency();

        assertEquals(3, boxes.findAll().count());


        // BasicRepository.delete
        boxes.delete(box4);

        TestPropertyUtility.waitForEventualConsistency();

        // BasicRepository.findAll
        Stream<Box> stream = boxes.findAll();
        List<Box> list = stream.sorted(Comparator.comparing(b -> b.boxIdentifier)).toList();
        assertEquals(2, list.size());
        box4 = list.get(0);
        assertEquals("TestBasicRepositoryMethods-03", box3.boxIdentifier);
        assertEquals(33, box3.length);
        assertEquals(56, box3.width);
        assertEquals(65, box3.height);
        box5 = list.get(1);
        assertEquals("TestBasicRepositoryMethods-05", box5.boxIdentifier);
        assertEquals(153, box5.length);
        assertEquals(104, box5.width);
        assertEquals(185, box5.height);

        // BasicRepository.deleteById
        boxes.deleteById("TestBasicRepositoryMethods-03");

        TestPropertyUtility.waitForEventualConsistency();

        // BasicRepository.findById
        assertFalse(boxes.findById("TestBasicRepositoryMethods-03").isPresent());
        box5 = boxes.findById("TestBasicRepositoryMethods-05").orElseThrow();
        assertEquals("TestBasicRepositoryMethods-05", box5.boxIdentifier);
        assertEquals(153, box5.length);
        assertEquals(104, box5.width);
        assertEquals(185, box5.height);

        // BasicRepository.deleteById
        boxes.deleteById("TestBasicRepositoryMethods-05");

        TestPropertyUtility.waitForEventualConsistency();

        assertEquals(0, boxes.findAll().count());
    }

    @Assertion(id = "133", strategy = "Request a Page higher than the final Page, expecting an empty Page with 0 results.")
    public void testBeyondFinalPage() {
        PageRequest sixth = PageRequest.ofPage(6).size(10);
        Page<AsciiCharacter> page;
        try {
            page = characters.findByNumericValueBetween(48, 90, sixth, Order.by(_AsciiCharacter.numericValue.asc()));
        } catch (UnsupportedOperationException x) {
            // Some NoSQL databases lack the ability to count the total results
            // and therefore cannot support a return type of Page.
            // Column and Key-Value databases might not be capable of sorting.
            // Key-Value databases might not be capable of Between.
            return;
        }
        assertEquals(0, page.numberOfElements());
        assertEquals(0, page.stream().count());
        assertFalse(page.hasContent());
        assertFalse(page.iterator().hasNext());
        try {
            assertEquals(43L, page.totalElements());
            assertEquals(5L, page.totalPages());
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // Some NoSQL databases lack the ability to count the total results
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "133", strategy = "Request a Slice higher than the final Slice, expecting an empty Slice with 0 results.")
    public void testBeyondFinalSlice() {
        PageRequest sixth = PageRequest.ofPage(6).size(5).withoutTotal();
        Page<NaturalNumber> page;
        try {
            page = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(
                    NumberType.PRIME,
                    8L,
                    sixth,
                    Sort.desc("id"));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                // Column and Key-Value databases might not be capable of And.
                // Key-Value databases might not be capable of LessThanEqual.
                return;
            } else {
                throw x;
            }
        }
        assertEquals(0, page.numberOfElements());
        assertEquals(0, page.stream().count());
        assertFalse(page.hasContent());
        assertFalse(page.iterator().hasNext());
    }

    @Assertion(id = "133", strategy = "Use a parameter-based find operation that uses the By annotation to identify the entity attribute names.")
    public void testBy() {
        AsciiCharacter ch = characters.find('L', "4c").orElseThrow();
        assertEquals('L', ch.getThisCharacter());
        assertEquals("4c", ch.getHexadecimal());
        assertEquals(76L, ch.getId());
        assertFalse(ch.isControl());

        assertTrue(characters.find('M', "4b").isEmpty());
    }

    @Assertion(id = "133", strategy = "Use a repository that inherits some if its methods from another interface.")
    public void testCommonInterfaceQueries() {

        try {
            assertEquals(4L, numbers.countByIdBetween(87L, 90L));

            assertEquals(5L, characters.countByIdBetween(86L, 90L));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // Key-Value databases are not capable of Between
            } else {
                throw x;
            }
        }

        assertTrue(numbers.existsById(73L));

        assertTrue(characters.existsById(74L));

        assertFalse(numbers.existsById(-1L));

        assertFalse(characters.existsById(-2L));

        try {
            assertEquals(
                    List.of(68L, 69L, 70L, 71L, 72L),
                    characters.withIdEqualOrAbove(68L, Limit.of(5)));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                return; // Key-Value databases are not capable of >= in JCQL
            } else {
                throw x;
            }
        }

        assertEquals(List.of(71L, 72L, 73L, 74L, 75L),
                numbers.withIdEqualOrAbove(71L, Limit.of(5)));
    }

    @Assertion(id = "133", strategy = "Use a repository method with Contains to query for a substring of a String attribute.")
    public void testContainsInString() {
        Collection<AsciiCharacter> found;
        try {
            found = characters.findByHexadecimalContainsAndIsControlNot("4", true);
        } catch (UnsupportedOperationException e) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of Contains.
                // Column and Key-Value databases might not be capable of And.
                return;
            } else {
                throw e;
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
            AsciiCharacter del = characters.findByIsControlTrueAndNumericValueBetween(33, 127);
            assertEquals(127, del.getNumericValue());
            assertEquals("7f", del.getHexadecimal());
            assertTrue(del.isControl());
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of And.
                // Key-Value databases might not be capable of Between.
                // Key-Value databases might not be capable of True/False comparison.
            } else {
                throw x;
            }
        }

        try {
            AsciiCharacter j = characters.findByHexadecimalIgnoreCase("6A");
            assertEquals("6a", j.getHexadecimal());
            assertEquals('j', j.getThisCharacter());
            assertEquals(106, j.getNumericValue());
            assertFalse(j.isControl());
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of IgnoreCase
            } else {
                throw x;
            }
        }

        AsciiCharacter d = characters.findByNumericValue(100).orElseThrow();
        assertEquals(100, d.getNumericValue());
        assertEquals('d', d.getThisCharacter());
        assertEquals("64", d.getHexadecimal());
        assertFalse(d.isControl());

        assertTrue(characters.existsByThisCharacter('D'));
    }

    @Assertion(id = "133", strategy = "Use a default method from a repository interface where the default method invokes other repository methods.")
    public void testDefaultMethod() {
        try {
            assertEquals(List.of('W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd'),
                    characters.retrieveAlphaNumericIn(87L, 100L)
                            .map(AsciiCharacter::getThisCharacter)
                            .collect(Collectors.toList()));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // Key-Value databases might not be capable of Between
                return;
            } else {
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
            stream = characters.findByIdBetween(
                    52L, 57L,
                    Sort.desc("id"));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                // Key-Value databases might not be capable of Between.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(Arrays.toString(new Character[]{'9', '8', '7', '6', '5', '4'}),
                Arrays.toString(stream.map(AsciiCharacter::getThisCharacter).toArray()));
    }

    @Assertion(id = "458", strategy = """
            Use a repository method with a JCQL query that has no clauses.
            """)
    public void testEmptyQuery() {

        try {
            assertEquals(List.of('a', 'b', 'c', 'd', 'e', 'f'),
                    characters.all(Limit.range(97, 102), Sort.asc("id"))
                            .map(AsciiCharacter::getThisCharacter)
                            .collect(Collectors.toList()));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                return;
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "133", strategy = "Use a repository method that returns a single entity value where no result is found. Expect EmptyResultException.")
    public void testEmptyResultException() {
        try {
            AsciiCharacter ch = characters.findByHexadecimalIgnoreCase("2g");
            fail("Unexpected result of findByHexadecimalIgnoreCase(2g): " + ch.getHexadecimal());
        } catch (EmptyResultException x) {
            log.info("testEmptyResultException expected to catch exception " + x + ". Printing its stack trace:");
            x.printStackTrace(System.out);
            // test passes
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of IgnoreCase
                return;
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "133", strategy = "Use a repository method with the False keyword.")
    public void testFalse() {
        List<NaturalNumber> even;
        try {
            even = positives.findByIsOddFalseAndIdBetween(50L, 60L);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of And.
                // Key-Value databases might not be capable of Between.
                // Key-Value databases might not be capable of True/False comparison.
                return;
            } else {
                throw x;
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
            page = characters.findByNumericValueBetween(48, 90, fifthPageRequest,
                    Order.by(_AsciiCharacter.numericValue.asc())); // 'X' to 'Z'
        } catch (UnsupportedOperationException x) {
            // Some NoSQL databases lack the ability to count the total results
            // and therefore cannot support a return type of Page.
            // Column and Key-Value databases might not be capable of sorting.
            // Key-Value databases might not be capable of Between.
            return;
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // Some NoSQL databases lack the ability to count the total results
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "133", strategy = "Request the last Slice of up to 5 results, expecting to find the final 2.")
    public void testFinalSliceOfUpTo5() {
        PageRequest fifth = PageRequest.ofPage(5).size(5).withoutTotal();
        Page<NaturalNumber> page;
        try {
            page = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(
                    NumberType.PRIME,
                    8L,
                    fifth,
                    Sort.desc("id"));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                // Column and Key-Value databases might not be capable of And.
                // Key-Value databases might not be capable of LessThanEqual.
                return;
            } else {
                throw x;
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
            strategy = "Use the findAll method of a repository that inherits from BasicRepository " +
                    "to request a Page 2 of size 12, specifying a PageRequest that requires a mixture of " +
                    "ascending and descending sort. Verify the page contains all 12 expected entities, " +
                    "sorted according to the mixture of ascending and descending sort orders specified.")
    public void testFindAllWithPagination() {
        PageRequest page2request = PageRequest.ofPage(2).size(12);
        Page<NaturalNumber> page2;
        try {
            page2 = positives.findAll(page2request,
                    Order.by(
                            Sort.asc("floorOfSquareRoot"),
                            Sort.desc("id")));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(12, page2.numberOfElements());
        assertEquals(2, page2.pageRequest().page());

        assertEquals(List.of(11L, 10L, 9L, // square root rounds down to 3
                        24L, 23L, 22L, 21L, 20L, 19L, 18L, 17L, 16L), // square root rounds down to 4
                page2.stream().map(NaturalNumber::getId).collect(Collectors.toList()));
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that uses the value of Find to identify the type
            of entity and retrieves a subset of entity's attributes, as defined by
            the names of record components. Results are returned in an array of record.
            """)
    public void testFindEntityAsRecordReturnArray() {

        HexInfo[] array = numbers.hexadecimalOfControlChars(true);
        Arrays.sort(array);

        assertEquals(List.of("1(1)", "2(2)", "3(3)", "4(4)", "5(5)",
                        "6(6)", "7(7)", "8(8)", "9(9)", "a(10)",
                        "b(11)", "c(12)", "d(13)", "e(14)", "f(15)",
                        "10(16)", "11(17)", "12(18)", "13(19)", "14(20)",
                        "15(21)", "16(22)", "17(23)", "18(24)", "19(25)",
                        "1a(26)", "1b(27)", "1c(28)", "1d(29)", "1e(30)",
                        "1f(31)", "7f(127)"),
                Arrays.stream(array)
                        .map(HexInfo::toString)
                        .collect(Collectors.toList()));
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that uses the value of Find to identify the type
            of entity and retrieves a subset of entity's attributes, as defined by
            the names of record components. The result is returned as an Optional.
            """)
    public void testFindEntityAsRecordReturnOptional() {
        HexInfo hex;
        hex = numbers.hexadecimalInfo(61).orElseThrow();
        assertEquals("3d", hex.hexadecimal());
        assertEquals(61, hex.numericValue());

        assertFalse(numbers.hexadecimalInfo(-5).isPresent());

        hex = numbers.hexadecimalInfo(95).orElseThrow();
        assertEquals("5f", hex.hexadecimal());
        assertEquals(95, hex.numericValue());
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that uses the value of Find to identify the type
            of entity and retrieves a subset of entity's attributes, as defined by
            the names of record components. Results are return as a Page of record.
            """)
    public void testFindEntityAsRecordReturnPage() {
        PageRequest page8Request = PageRequest.ofPage(8).size(10);

        @SuppressWarnings("unchecked")
        Sort<AsciiCharacter>[] numAscending =
                new Sort[]{Sort.asc(_AsciiCharacter.NUMERICVALUE)};

        Page<HexInfo> page8;
        try {
            page8 = numbers.hexadecimalPage(page8Request, numAscending);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(List.of("47(71)", "48(72)", "49(73)", "4a(74)", "4b(75)",
                        "4c(76)", "4d(77)", "4e(78)", "4f(79)", "50(80)"),
                page8.stream()
                        .map(HexInfo::toString)
                        .collect(Collectors.toList()));

        Page<HexInfo> page7 = numbers.hexadecimalPage(page8.previousPageRequest(),
                numAscending);

        assertEquals(List.of("3d(61)", "3e(62)", "3f(63)", "40(64)", "41(65)",
                        "42(66)", "43(67)", "44(68)", "45(69)", "46(70)"),
                page7.stream()
                        .map(HexInfo::toString)
                        .collect(Collectors.toList()));

        Page<HexInfo> page9 = numbers.hexadecimalPage(page8.nextPageRequest(),
                numAscending);

        assertEquals(List.of("51(81)", "52(82)", "53(83)", "54(84)", "55(85)",
                        "56(86)", "57(87)", "58(88)", "59(89)", "5a(90)"),
                page9.stream()
                        .map(HexInfo::toString)
                        .collect(Collectors.toList()));
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that uses the value of Find to identify the type
            of entity and retrieves a subset of entity's attributes, as defined by
            the Select annotation. Results are returned as a List of record.
            """)
    public void testFindEntitySelectAsRecordReturnList() {

        List<WholeNumber> found = characters.wholeNumbers((short) 4);

        assertEquals(List.of("8 COMPOSITE √8 >= 2",
                        "9 COMPOSITE √9 >= 3",
                        "10 COMPOSITE √10 >= 3",
                        "11 PRIME √11 >= 3",
                        "12 COMPOSITE √12 >= 3",
                        "13 PRIME √13 >= 3",
                        "14 COMPOSITE √14 >= 3",
                        "15 COMPOSITE √15 >= 3"),
                found.stream()
                        .sorted()
                        .map(WholeNumber::toString)
                        .collect(Collectors.toList()));
    }

    @Assertion(id = "133",
            strategy = "Use a repository method with findFirstBy that returns the first entity value " +
                    "where multiple results would otherwise be found.")
    public void testFindFirst() {
        Optional<AsciiCharacter> none;
        try {
            none = characters.findFirstByHexadecimalStartsWithAndIsControlOrderByIdAsc(
                    "h", false);
        } catch (UnsupportedOperationException e) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of StartsWith.
                // Column and Key-Value databases might not be capable of sorting.
                // Column and Key-Value databases might not be capable of And.
                return;
            } else {
                throw e;
            }
        }
        assertTrue(none.isEmpty());

        AsciiCharacter ch = characters.findFirstByHexadecimalStartsWithAndIsControlOrderByIdAsc("4", false)
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
            found = characters.findFirst3ByNumericValueGreaterThanEqualAndHexadecimalEndsWith(
                    40, "4", Sort.asc("numericValue"));
        } catch (UnsupportedOperationException e) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of EndsWith.
                // Column and Key-Value databases might not be capable of sorting.
                // Column and Key-Value databases might not be capable of And.
                return;
            } else {
                throw e;
            }
        }

        assertEquals(3, found.length);
        assertEquals('4', found[0].getThisCharacter());
        assertEquals('D', found[1].getThisCharacter());
        assertEquals('T', found[2].getThisCharacter());
    }

    @Assertion(id = "133",
            strategy = "Find a list of entities, querying by entity attributes with names that match the method parameter names," +
                    " with results capped by a Limit parameter and sorted according to a variable arguments Sort parameter.")
    public void testFindList() {
        List<NaturalNumber> oddCompositeNumbers;
        try {
            oddCompositeNumbers = positives.findOdd(
                    true,
                    NumberType.COMPOSITE,
                    Limit.of(10),
                    Order.by(
                            Sort.asc("floorOfSquareRoot"),
                            Sort.desc("numBitsRequired"),
                            Sort.asc("id")));


            assertEquals(List.of(9L, 15L,  // 3 <= sqrt < 4, 4 bits
                            21L,      // 4 <= sqrt < 5, 5 bits
                            33L, 35L, // 5 <= sqrt < 6, 6 bits
                            25L, 27L, // 5 <= sqrt < 6, 5 bits
                            39L, 45L, // 6 <= sqrt < 7, 6 bits
                            49L),     // 7 <= sqrt < 8, 6 bits
                    oddCompositeNumbers
                            .stream()
                            .map(NaturalNumber::getId)
                            .collect(Collectors.toList()));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
            } else {
                throw x;
            }
        }

        List<NaturalNumber> evenPrimeNumbers = positives.findOdd(false, NumberType.PRIME, Limit.of(9), Order.by());

        assertEquals(1, evenPrimeNumbers.size());
        NaturalNumber num = evenPrimeNumbers.get(0);
        assertEquals(2L, num.getId());
        assertEquals(1L, num.getFloorOfSquareRoot());
        assertEquals(Short.valueOf((short) 2), num.getNumBitsRequired());
        assertEquals(NumberType.PRIME, num.getNumType());
        assertFalse(num.isOdd());
    }

    @Assertion(id = "133",
            strategy = "Find a single entity, querying by entity attributes with names that match the method parameter names.")
    public void testFindOne() {
        AsciiCharacter j = characters.find('j');

        assertEquals("6a", j.getHexadecimal());
        assertEquals(106L, j.getId());
        assertEquals(106, j.getNumericValue());
        assertEquals('j', j.getThisCharacter());
    }

    @Assertion(id = "133",
            strategy = "Find a single entity that might or might not exist, querying by entity attributes" +
                    " with names that match the method parameter names.")
    public void testFindOptional() {
        NaturalNumber num = positives.findNumber(67L).orElseThrow();

        assertEquals(67L, num.getId());
        assertEquals(8L, num.getFloorOfSquareRoot());
        assertEquals(Short.valueOf((short) 7), num.getNumBitsRequired());
        assertEquals(NumberType.PRIME, num.getNumType());
        assertTrue(num.isOdd());

        Optional<NaturalNumber> opt = positives.findNumber(-40L);

        assertFalse(opt.isPresent());
    }

    @Assertion(id = "133",
            strategy = "Find a page of entities, with entity attributes identified by the parameter names and matching the parameter values.")
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(List.of(99L, 98L, 96L, 95L, 94L, 93L, 92L),
                page1.stream().map(NaturalNumber::getId).collect(Collectors.toList()));

        assertTrue(page1.hasNext());

        Page<NaturalNumber> page2 = positives.findMatching(9L, (short) 7, NumberType.COMPOSITE,
                page1.nextPageRequest(), Sort.desc("id"));

        assertEquals(List.of(91L, 90L, 88L, 87L, 86L, 85L, 84L),
                page2.stream().map(NaturalNumber::getId).collect(Collectors.toList()));

        assertTrue(page2.hasNext());

        Page<NaturalNumber> page3 = positives.findMatching(9L, (short) 7, NumberType.COMPOSITE,
                page2.nextPageRequest(), Sort.desc("id"));

        assertEquals(List.of(82L, 81L),
                page3.stream().map(NaturalNumber::getId).collect(Collectors.toList()));

        assertFalse(page3.hasNext());
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
            page = positives.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(4L, 33L, first8, order);
        } catch (UnsupportedOperationException x) {
            // Test passes: Jakarta Data providers must raise UnsupportedOperationException when the database
            // is not capable of cursor-based pagination.
            // Column and Key-Value databases might not be capable of sorting.
            // Column and Key-Value databases might not be capable of And.
            return;
        }

        assertEquals(8, page.numberOfElements());

        assertEquals(Arrays.toString(new Long[]{32L, 25L, 26L, 27L, 28L, 29L, 30L, 31L}),
                Arrays.toString(page.stream().map(NaturalNumber::getId).toArray()));

        try {
            page = positives.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(4L, 33L, page.nextPageRequest(), order);
        } catch (UnsupportedOperationException x) {
            // Test passes: Jakarta Data providers must raise UnsupportedOperationException when the database
            // is not capable of cursor-based pagination.
            return;
        }

        assertEquals(Arrays.toString(new Long[]{8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L}),
                Arrays.toString(page.stream().map(NaturalNumber::getId).toArray()));

        assertEquals(8, page.numberOfElements());

        try {
            page = positives.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(4L, 33L, page.nextPageRequest(), order);
        } catch (UnsupportedOperationException x) {
            // Test passes: Jakarta Data providers must raise UnsupportedOperationException when the database
            // is not capable of cursor-based pagination.
            return;
        }

        assertEquals(7, page.numberOfElements());

        assertEquals(Arrays.toString(new Long[]{4L, 5L, 6L, 7L, 2L, 3L, 1L}),
                Arrays.toString(page.stream().map(NaturalNumber::getId).toArray()));
    }

    @Assertion(id = "133",
            strategy = "Request the first CursoredPage of 6 results, expecting to find all 6, " +
                    "then request the next CursoredPage and the CursoredPage after that, " +
                    "expecting to find all results.")
    public void testFirstCursoredPageWithoutTotalOf6AndNextPages() {
        PageRequest first6 = PageRequest.ofSize(6).withoutTotal();
        CursoredPage<NaturalNumber> slice;

        try {
            slice = numbers.findByFloorOfSquareRootOrderByIdAsc(7L, first6);
        } catch (UnsupportedOperationException x) {
            // Test passes: Jakarta Data providers must raise UnsupportedOperationException when the database
            // is not capable of cursor-based pagination.
            // Column and Key-Value databases might not be capable of sorting.
            return;
        }

        assertEquals(Arrays.toString(new Long[]{49L, 50L, 51L, 52L, 53L, 54L}),
                Arrays.toString(slice.stream().map(NaturalNumber::getId).toArray()));

        assertEquals(6, slice.numberOfElements());

        try {
            slice = numbers.findByFloorOfSquareRootOrderByIdAsc(7L, slice.nextPageRequest());
        } catch (UnsupportedOperationException x) {
            // Test passes: Jakarta Data providers must raise UnsupportedOperationException when the database
            // is not capable of cursor-based pagination.
            return;
        }

        assertEquals(6, slice.numberOfElements());

        assertEquals(Arrays.toString(new Long[]{55L, 56L, 57L, 58L, 59L, 60L}),
                Arrays.toString(slice.stream().map(NaturalNumber::getId).toArray()));

        try {
            slice = numbers.findByFloorOfSquareRootOrderByIdAsc(7L, slice.nextPageRequest());
        } catch (UnsupportedOperationException x) {
            // Test passes: Jakarta Data providers must raise UnsupportedOperationException when the database
            // is not capable of cursor-based pagination.
            return;
        }

        assertEquals(Arrays.toString(new Long[]{61L, 62L, 63L}),
                Arrays.toString(slice.stream().map(NaturalNumber::getId).toArray()));

        assertEquals(3, slice.numberOfElements());
    }

    @Assertion(id = "133",
            strategy = "Request the first Page of 10 results, expecting to find all 10. " +
                    "From the Page, verify the totalElements and totalPages expected.")
    public void testFirstPageOf10() {
        PageRequest first10 = PageRequest.ofSize(10);
        Page<AsciiCharacter> page;
        try {
            page = characters.findByNumericValueBetween(48, 90, first10,
                    Order.by(_AsciiCharacter.numericValue.asc())); // '0' to 'Z'
        } catch (UnsupportedOperationException x) {
            // Some NoSQL databases lack the ability to count the total results
            // and therefore cannot support a return type of Page.
            // Column and Key-Value databases might not be capable of sorting.
            // Key-Value databases might not be capable of Between.
            return;
        }

        assertEquals(1, page.pageRequest().page());
        assertTrue(page.hasContent());
        assertEquals(10, page.numberOfElements());
        try {
            assertEquals(43L, page.totalElements());
            assertEquals(5L, page.totalPages());
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // Some NoSQL databases lack the ability to count the total results
            } else {
                throw x;
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
            page = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(
                    NumberType.PRIME,
                    8L,
                    first5,
                    Sort.desc("id"));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                // Column and Key-Value databases might not be capable of And.
                // Key-value databases might not be capable of LessThanEqual.
                return;
            } else {
                throw x;
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
            assertTrue(positives.existsByIdGreaterThan(0L));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                return; // Key-Value databases are not capable of GreaterThan
            } else {
                throw x;
            }
        }
        assertTrue(positives.existsByIdGreaterThan(99L));
        assertFalse(positives.existsByIdGreaterThan(100L)); // doesn't exist because the table only has 1 to 100
    }

    @Assertion(id = "133", strategy = "Use a repository method with the In keyword.")
    public void testIn() {
        Stream<NaturalNumber> nonPrimes;
        try {
            nonPrimes = positives.findByNumTypeInOrderByIdAsc(
                    Set.of(NumberType.COMPOSITE, NumberType.ONE),
                    Limit.of(9));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of In
                // when used with entity attributes other than the Id.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(List.of(1L, 4L, 6L, 8L, 9L, 10L, 12L, 14L, 15L),
                nonPrimes.map(NaturalNumber::getId).collect(Collectors.toList()));

        Stream<NaturalNumber> primes = positives.findByNumTypeInOrderByIdAsc(Collections.singleton(NumberType.PRIME),
                Limit.of(6));
        assertEquals(List.of(2L, 3L, 5L, 7L, 11L, 13L),
                primes.map(NaturalNumber::getId).collect(Collectors.toList()));
    }

    @Assertion(id = "133", strategy = "Use a repository method with the IgnoreCase keyword.")
    public void testIgnoreCase() {
        Stream<AsciiCharacter> found;
        try {
            found = characters.findByHexadecimalIgnoreCaseBetweenAndHexadecimalNotIn(
                    "4c", "5A", Set.of("5"),
                    Order.by(Sort.asc("hexadecimal")));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of IgnoreCase
                // Column and Key-Value databases might not be capable of And.
                // Key-Value databases might not be capable of Between.
                // Column and Key-Value databases might not be capable of In
                // when used with entity attributes other than the Id.
                // Column and Key-Value databases might not be capable of sorting.
                return;
            } else {
                throw x;
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
            page = positives.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(6L, 50L, middle7, order);
        } catch (UnsupportedOperationException x) {
            // Test passes: Jakarta Data providers must raise UnsupportedOperationException when the database
            // is not capable of cursor-based pagination.
            // Column and Key-Value databases might not be capable of sorting.
            // Column and Key-Value databases might not be capable of And.
            return;
        }

        assertEquals(Arrays.toString(new Long[]{25L, // 5 bits required, square root rounds down to 5
                        8L, // 4 bits required, square root rounds down to 2
                        15L, 14L, 13L, 12L, 11L // 4 bits required, square root rounds down to 3
                }),
                Arrays.toString(page.stream().map(NaturalNumber::getId).toArray()));

        assertEquals(7, page.numberOfElements());

        assertTrue(page.hasPrevious());

        CursoredPage<NaturalNumber> previousPage;
        try {
            previousPage = positives.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(6L, 50L,
                    page.previousPageRequest(),
                    order);
        } catch (UnsupportedOperationException x) {
            // Test passes: Jakarta Data providers must raise UnsupportedOperationException when the database
            // is not capable of cursor-based pagination.
            return;
        }

        assertEquals(Arrays.toString(new Long[]{16L, // 4 bits required, square root rounds down to 4
                        31L, 30L, 29L, 28L, 27L, 26L // 5 bits required, square root rounds down to 5
                }),
                Arrays.toString(previousPage.stream().map(NaturalNumber::getId).toArray()));

        assertEquals(7, previousPage.numberOfElements());

        CursoredPage<NaturalNumber> nextPage;
        try {
            nextPage = positives.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(6L, 50L,
                    page.nextPageRequest(),
                    order);
        } catch (UnsupportedOperationException x) {
            // Test passes: Jakarta Data providers must raise UnsupportedOperationException when the database
            // is not capable of cursor-based pagination.
            return;
        }

        assertEquals(Arrays.toString(new Long[]{10L, 9L, // 4 bits required, square root rounds down to 3
                        7L, 6L, 5L, 4L, // 3 bits required, square root rounds down to 2
                        3L // 2 bits required, square root rounds down to 1
                }),
                Arrays.toString(nextPage.stream().map(NaturalNumber::getId).toArray()));

        assertEquals(7, nextPage.numberOfElements());
    }

    @Assertion(id = "133", strategy = "Request a CursoredPage of results where none match the query, expecting an empty CursoredPage with 0 results.")
    public void testCursoredPageOfNothing() {

        CursoredPage<NaturalNumber> page;
        try {
            // There are no positive integers less than 4 which have a square root that rounds down to something other than 1.
            page = positives.findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(1L, 4L, PageRequest.ofPage(1L), Order.by());
        } catch (UnsupportedOperationException x) {
            // Test passes: Jakarta Data providers must raise UnsupportedOperationException when the database
            // is not capable of cursor-based pagination.
            // Column and Key-Value databases might not be capable of sorting.
            // Column and Key-Value databases might not be capable of And.
            return;
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
            slice = numbers.findByNumTypeAndNumBitsRequiredLessThan(NumberType.COMPOSITE, (short) 7, order, middle9);
        } catch (UnsupportedOperationException x) {
            // Test passes: Jakarta Data providers must raise UnsupportedOperationException when the database
            // is not capable of cursor-based pagination.
            // Column and Key-Value databases might not be capable of sorting.
            // Column and Key-Value databases might not be capable of And.
            return;
        }

        assertEquals(Arrays.toString(new Long[]{48L, 25L, 26L, 27L, 28L, 30L, 32L, 33L, 34L}),
                Arrays.toString(slice.stream().map(NaturalNumber::getId).toArray()));

        assertEquals(9, slice.numberOfElements());

        assertTrue(slice.hasPrevious());
        CursoredPage<NaturalNumber> previousSlice;
        try {
            previousSlice = numbers.findByNumTypeAndNumBitsRequiredLessThan(NumberType.COMPOSITE,
                    (short) 7,
                    order,
                    slice.previousPageRequest());
        } catch (UnsupportedOperationException x) {
            // Test passes: Jakarta Data providers must raise UnsupportedOperationException when the database
            // is not capable of cursor-based pagination.
            return;
        }

        assertEquals(Arrays.toString(new Long[]{63L, 36L, 38L, 39L, 40L, 42L, 44L, 45L, 46L}),
                Arrays.toString(previousSlice.stream().map(NaturalNumber::getId).toArray()));

        assertEquals(9, previousSlice.numberOfElements());

        CursoredPage<NaturalNumber> nextSlice;
        try {
            nextSlice = numbers.findByNumTypeAndNumBitsRequiredLessThan(NumberType.COMPOSITE,
                    (short) 7,
                    order,
                    slice.nextPageRequest());
        } catch (UnsupportedOperationException x) {
            // Test passes: Jakarta Data providers must raise UnsupportedOperationException when the database
            // is not capable of cursor-based pagination.
            return;
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
            slice = numbers.findByFloorOfSquareRootOrderByIdAsc(3L, pagination);
        } catch (UnsupportedOperationException x) {
            // Test passes: Jakarta Data providers must raise UnsupportedOperationException when the database
            // is not capable of cursor-based pagination.
            // Column and Key-Value databases might not be capable of sorting.
            return;
        }

        assertFalse(slice.hasContent());
        assertEquals(0, slice.content().size());
        assertEquals(0, slice.numberOfElements());
    }

    @Assertion(id = "133", strategy = "Use a repository method countByIdLessThan confirming the correct count is returned.")
    public void testLessThanWithCount() {
        try {
            assertEquals(91L, positives.countByIdLessThan(92L));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                return; // Key-Value databases are not capable of LessThan
            } else {
                throw x;
            }
        }

        assertEquals(0L, positives.countByIdLessThan(1L));
    }

    @Assertion(id = "133", strategy = "Use a repository method with both Sort and Limit, and verify that the Limit caps " +
            "the number of results and that results are ordered according to the sort criteria.")
    public void testLimit() {
        Collection<NaturalNumber> nums;
        try {
            nums = numbers.findByIdGreaterThanEqual(
                    60L,
                    Limit.of(10),
                    Order.by(
                            Sort.asc("floorOfSquareRoot"),
                            Sort.desc("id")));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                // Key-Value databases are not capable of GreaterThanEqual
                return;
            } else {
                throw x;
            }
        }

        assertEquals(Arrays.toString(new Long[]{63L, 62L, 61L, 60L, // square root rounds down to 7
                        80L, 79L, 78L, 77L, 76L, 75L}), // square root rounds down to 8
                Arrays.toString(nums.stream().map(NaturalNumber::getId).toArray()));
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
            nums = numbers.findByIdGreaterThanEqual(
                    40L,
                    Limit.range(6, 10),
                    Order.by(
                            Sort.asc("numTypeOrdinal"), // primes first
                            Sort.asc("id")));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                // Key-Value databases are not capable of GreaterThanEqual
                return;
            } else {
                throw x;
            }
        }

        assertEquals(Arrays.toString(new Long[]{61L, 67L, 71L, 73L, 79L}),
                Arrays.toString(nums.stream().map(NaturalNumber::getId).toArray()));
    }

    @Assertion(id = "133", strategy = "Use a repository method with Limit and verify that the Limit caps " +
            "the number of results to the amount that is specified.")
    public void testLimitToOneResult() {
        Collection<NaturalNumber> nums;
        try {
            nums = numbers.findByIdGreaterThanEqual(80L, Limit.of(1), Order.by());
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                return; // Key-Value databases are not capable of GreaterThanEqual
            } else {
                throw x;
            }
        }

        Iterator<NaturalNumber> it = nums.iterator();
        assertTrue(it.hasNext());

        NaturalNumber num = it.next();
        assertTrue(num.getId() >= 80L);

        assertFalse(it.hasNext());
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of JCQL AND
                // Key-Value databases might not be capable of JCQL TRUE/FALSE
                return;
            } else {
                throw x;
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Key-Value databases might not be capable of <= in JCQL.
                // Column and Key-Value databases might not be capable of JCQL AND.
            } else {
                throw x;
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
                            .collect(Collectors.toList()));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of JCQL AND.
                // Column and Key-Value databases might not be capable of JCQL IN
                // when used with entity attributes other than the Id.
            } else {
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of JCQL AND
                // Key-Value databases might not be capable of JCQL BETWEEN
                // Key-Value databases might not be capable of JCQL TRUE/FALSE
                return;
            } else {
                throw x;
            }
        }

        try {
            assertEquals(10L, page1.totalElements());
            assertEquals(2L, page1.totalPages());

        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // Some NoSQL databases lack the ability to count the total results
            } else {
                throw x;
            }
        }
        assertEquals(List.of(21L, 23L, 25L, 27L, 29L), page1.content());

        assertTrue(page1.hasNext());

        Page<Long> page2 = numbers.oddsFrom21To(40L, page1.nextPageRequest());

        assertEquals(List.of(31L, 33L, 35L, 37L, 39L), page2.content());

        if (page2.hasNext()) {
            Page<Long> page3 = numbers.oddsFrom21To(40L, page2.nextPageRequest());
            assertFalse(page3.hasContent());
            assertFalse(page3.hasNext());
        }
    }

    @Assertion(id = "133",
            strategy = "Use a repository method with two Sort parameters specifying a mixture of ascending and descending order, " +
                    "and verify all results are returned and are ordered according to the sort criteria.")
    public void testMixedSort() {
        NaturalNumber[] nums;
        try {
            nums = numbers.findByIdLessThan(
                    15L,
                    Sort.asc("numBitsRequired"),
                    Sort.desc("id"));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                // Key-Value databases might not be capable of LessThan.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(Arrays.toString(new Long[]{1L, // 1 bit
                        3L, 2L, // 2 bits
                        7L, 6L, 5L, 4L, // 3 bits
                        14L, 13L, 12L, 11L, 10L, 9L, 8L}), // 4 bits
                Arrays.toString(Stream.of(nums).map(NaturalNumber::getId).toArray()));
    }

    @Assertion(id = "133",
            strategy = "Use a repository method that ought to return a single entity value but where multiple results are found." +
                    "Expect NonUniqueResultException.")
    public void testNonUniqueResultException() {
        try {
            AsciiCharacter ch = characters.findByIsControlTrueAndNumericValueBetween(10, 15);
            fail("Unexpected result of findByIsControlTrueAndNumericValueBetween(10, 15): " + ch.getHexadecimal());
        } catch (NonUniqueResultException x) {
            log.info("testNonUniqueResultException expected to catch exception " + x + ". Printing its stack trace:");
            x.printStackTrace(System.out);
            // test passes
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of And.
                // Key-Value databases might not be capable of Between.
                // Key-Value databases might not be capable of True/False comparison.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "133", strategy = "Use a repository method with the Not keyword.")
    public void testNot() {
        NaturalNumber[] n;
        try {
            n = numbers.findByNumTypeNot(
                    NumberType.COMPOSITE,
                    Limit.of(8),
                    Order.by(Sort.asc("id")));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                return;
            } else {
                throw x;
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
            found = positives.findByNumTypeOrFloorOfSquareRoot(NumberType.ONE, 2L);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of Or.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(List.of(1L, 4L, 5L, 6L, 7L, 8L),
                found.map(NaturalNumber::getId).sorted().collect(Collectors.toList()));
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
            page = numbers.findByIdLessThanOrderByFloorOfSquareRootDesc(
                    25L, pagination, order);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                // Key-Value databases might not be capable of LessThan.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(Arrays.toString(new Long[]{23L, 19L, 17L, // square root rounds down to 4; prime
                        24L, 22L, 21L, 20L, 18L}), // square root rounds down to 4; composite
                Arrays.toString(page.stream().map(NaturalNumber::getId).toArray()));

        assertTrue(page.hasNext());
        pagination = page.nextPageRequest();
        page = numbers.findByIdLessThanOrderByFloorOfSquareRootDesc(25L, pagination, order);

        assertEquals(Arrays.toString(new Long[]{16L, // square root rounds down to 4; composite
                        13L, 11L, // square root rounds down to 3; prime
                        15L, 14L, 12L, 10L, 9L}), // square root rounds down to 3; composite
                Arrays.toString(page.stream().map(NaturalNumber::getId).toArray()));

        assertTrue(page.hasNext());
        pagination = page.nextPageRequest();
        page = numbers.findByIdLessThanOrderByFloorOfSquareRootDesc(25L, pagination, order);

        assertEquals(Arrays.toString(new Long[]{7L, 5L, // square root rounds down to 2; prime
                        8L, 6L, 4L, // square root rounds down to 2; composite
                        1L, // square root rounds down to 1; one
                        3L, 2L}), // square root rounds down to 1; prime
                Arrays.toString(page.stream().map(NaturalNumber::getId).toArray()));

        if (page.hasNext()) {
            pagination = page.nextPageRequest();
            page = numbers.findByIdLessThanOrderByFloorOfSquareRootDesc(25L, pagination, order);
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
            nums = numbers.findByIdBetweenOrderByNumTypeOrdinalAsc(
                    5L, 24L,
                    Order.by(Sort.desc("floorOfSquareRoot"), Sort.asc("id")));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                // Key-Value databases might not be capable of Between.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(Arrays.toString(new Long[]{17L, 19L, 23L, // prime; square root rounds down to 4
                        11L, 13L, // prime; square root rounds down to 3
                        5L, 7L, // prime; square root rounds down to 2
                        16L, 18L, 20L, 21L, 22L, 24L, // composite; square root rounds down to 4
                        9L, 10L, 12L, 14L, 15L, // composite; square root rounds down to 3
                        6L, 8L}), // composite; square root rounds down to 2
                Arrays.toString(nums.map(NaturalNumber::getId).toArray()));
    }

    @Assertion(id = "133", strategy = "Request a Page of results where none match the query, expecting an empty Page with 0 results.")
    public void testPageOfNothing() {
        PageRequest pagination = PageRequest.ofSize(6);
        Page<AsciiCharacter> page;
        try {
            page = characters.findByNumericValueBetween(150, 160, pagination,
                    Order.by(_AsciiCharacter.id.asc()));
        } catch (UnsupportedOperationException x) {
            // Some NoSQL databases lack the ability to count the total results
            // and therefore cannot support a return type of Page.
            // Column and Key-Value databases might not be capable of sorting.
            // Key-Value databases might not be capable of Between.
            return;
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // Some NoSQL databases lack the ability to count the total results
            } else {
                throw x;
            }
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
                        .collect(Collectors.toList()));
    }

    @Assertion(id = "458", strategy = """
            Use a repository method with a JCQL query that consists of only the
            SELECT and ORDER BY clauses.
            """)
    public void testPartialQuerySelectAndOrderBy() {

        Character[] chars = characters.reverseAlphabetic(Limit.range(6, 13));
        for (int i = 0; i < chars.length; i++) {
            assertEquals("zyxwvuts".charAt(i), chars[i]);
        }
    }

    @Assertion(id = "133", strategy = "Use count and exists methods where the primary entity class is inferred from the lifecycle methods.")
    public void testPrimaryEntityClassDeterminedByLifeCycleMethods() {
        assertEquals(4L, customRepo.countByIdIn(Set.of(2L, 15L, 37L, -5L, 60L)));

        assertTrue(customRepo.existsByIdIn(Set.of(17L, 14L, -1L)));

        assertFalse(customRepo.existsByIdIn(Set.of(-10L, -12L, -14L)));
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of Like
                // Column and Key-Value databases might not be capable of And.
                // Key-Value databases might not be capable of Between.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(6, abcdfo.length);
        for (int i = 0; i < abcdfo.length; i++) {
            assertEquals("ABCDFO".charAt(i), abcdfo[i]);
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of Contains.
                // Column and Key-Value databases might not be capable of And.
            } else {
                throw x;
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
            page1 = positives.withBitCountOrOfTypeAndBelow((short) 4,
                    NumberType.COMPOSITE, 20L,
                    Sort.desc("numBitsRequired"),
                    Sort.asc("id"),
                    page1Request);
        } catch (UnsupportedOperationException x) {
            // Test passes: Jakarta Data providers must raise UnsupportedOperationException when the database
            // is not capable of cursor-based pagination.
            // Column and Key-Value databases might not be capable of JCQL OR.
            // Column and Key-Value databases might not be capable of sorting.
            return;
        }

        assertEquals(List.of(16L, 18L, 8L, 9L),
                page1.stream()
                        .map(NaturalNumber::getId)
                        .collect(Collectors.toList()));

        assertTrue(page1.hasTotals());
        assertTrue(page1.hasNext());
        try {
            assertEquals(3L, page1.totalPages());
            assertEquals(12L, page1.totalElements());
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // Some NoSQL databases lack the ability to count the total results
            } else {
                throw x;
            }
        }

        CursoredPage<NaturalNumber> page2;

        try {
            page2 = positives.withBitCountOrOfTypeAndBelow((short) 4,
                    NumberType.COMPOSITE, 20L,
                    Sort.desc("numBitsRequired"),
                    Sort.asc("id"),
                    page1.nextPageRequest());
        } catch (UnsupportedOperationException x) {
            // Test passes: Jakarta Data providers must raise UnsupportedOperationException when the database
            // is not capable of cursor-based pagination.
            return;
        }

        assertEquals(List.of(10L, 11L, 12L, 13L),
                page2.stream()
                        .map(NaturalNumber::getId)
                        .collect(Collectors.toList()));

        assertTrue(page2.hasNext());

        CursoredPage<NaturalNumber> page3 = positives.withBitCountOrOfTypeAndBelow((short) 4,
                NumberType.COMPOSITE, 20L,
                Sort.desc("numBitsRequired"),
                Sort.asc("id"),
                page2.nextPageRequest());

        assertEquals(List.of(14L, 15L, 4L, 6L),
                page3.stream()
                        .map(NaturalNumber::getId)
                        .collect(Collectors.toList()));

        if (page3.hasNext()) {
            CursoredPage<NaturalNumber> page4 = positives.withBitCountOrOfTypeAndBelow((short) 4,
                    NumberType.COMPOSITE, 20L,
                    Sort.desc("numBitsRequired"),
                    Sort.asc("id"),
                    page3.nextPageRequest());
            assertFalse(page4.hasContent());
        }
    }

    @Assertion(id = "539", strategy = """
            Use a repository method with a Query that does not include a SELECT
            clause, instead relying on the record component names of the array of
            Java record result to determine the subset of entity attributes to
            retrieve from the database.
            """)
    public void testQueryWithoutSelectReturnsArrayOfRecord() {

        assertEquals(List.of("9 is ODD COMPOSITE and requires 4 bits",
                        "10 is EVEN COMPOSITE and requires 4 bits",
                        "11 is ODD PRIME and requires 4 bits",
                        "12 is EVEN COMPOSITE and requires 4 bits",
                        "13 is ODD PRIME and requires 4 bits",
                        "14 is EVEN COMPOSITE and requires 4 bits",
                        "15 is ODD COMPOSITE and requires 4 bits"),
                Arrays.stream(numbers.numberArray(3L))
                        .sorted(Comparator.comparing(NumberInfo::id))
                        .map(n -> n.id() +
                                " is " + (n.isOdd() ? "ODD" : "EVEN") +
                                " " + n.numType() +
                                " and requires " + n.numBitsRequired() + " bits")
                        .collect(Collectors.toList()));
    }

    @Assertion(id = "539", strategy = """
            Use a repository method with a Query that does not include a SELECT
            clause, instead relying on the record component names of the List of
            Java record result to determine the subset of entity attributes to
            retrieve from the database.
            """)
    public void testQueryWithoutSelectReturnsListOfRecord() {

        assertEquals(List.of("4 is EVEN COMPOSITE and requires 3 bits",
                        "5 is ODD PRIME and requires 3 bits",
                        "6 is EVEN COMPOSITE and requires 3 bits",
                        "7 is ODD PRIME and requires 3 bits"),
                numbers.numberList((short) 3)
                        .stream()
                        .map(n -> n.id() +
                                " is " + (n.isOdd() ? "ODD" : "EVEN") +
                                " " + n.numType() +
                                " and requires " + n.numBitsRequired() + " bits")
                        .sorted()
                        .collect(Collectors.toList()));
    }

    @Assertion(id = "539", strategy = """
            Use a repository method with a Query that does not include a SELECT
            clause, instead relying on the Select annotation of the Java record
            result to determine the subset of entity attributes to retrieve
            from the database.
            """)
    public void testQueryWithoutSelectReturnsRecordWithSelect() {

        CardinalNumber num = numbers.cardinalNumberOf(76L);

        assertEquals(76L, num.value());
        assertEquals(NumberType.COMPOSITE.ordinal(), num.numType());
        assertEquals(Short.valueOf((short) 7), num.numBitsRequired());
    }

    @Assertion(id = "539", strategy = """
            Use a repository method with a Query that does not include a SELECT
            clause, instead relying on the Select annotation of the Stream of
            Java record result to determine the subset of entity attributes to
            retrieve from the database.
            """)
    public void testQueryWithoutSelectReturnsStreamOfRecordWithSelect() {

        assertEquals(List.of("25 COMPOSITE (5 bits)",
                        "26 COMPOSITE (5 bits)",
                        "27 COMPOSITE (5 bits)",
                        "28 COMPOSITE (5 bits)",
                        "29 PRIME (5 bits)",
                        "30 COMPOSITE (5 bits)",
                        "31 PRIME (5 bits)",
                        "32 COMPOSITE (6 bits)",
                        "33 COMPOSITE (6 bits)",
                        "34 COMPOSITE (6 bits)",
                        "35 COMPOSITE (6 bits)"),
                numbers.cardinalNumberStream(5L)
                        .map(CardinalNumber::toString)
                        .sorted()
                        .collect(Collectors.toList()));
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.DOCUMENT)) {
                // Document, Column, and Key-Value databases might not be capable of parentheses.
                // Column and Key-Value databases might not be capable of JCQL OR.
                // Key-Value databases might not be capable of < in JCQL.
                // Column and Key-Value databases might not be capable of JCQL AND.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "539", strategy = """
            Use a repository method with a Query that includes a SELECT clause
            and returns an Optional Java record result.
            """)
    public void testQueryWithSelectReturnsOptionalOfRecord() {

        WholeNumber number = numbers.numberOptional(53L).orElseThrow();

        assertEquals(53L, number.value());
        assertEquals(NumberType.PRIME.ordinal(), number.numType());
        assertEquals(7L, number.sqrtFloor());
    }

    @Assertion(id = "539", strategy = """
            Use a repository method with a Query that includes a SELECT clause
            and returns a Page of Java record results.
            """)
    public void testQueryWithSelectReturnsPageOfRecord() {
        PageRequest page3Req = PageRequest.ofPage(3).size(6);
        Page<WholeNumber> page3;
        try {
            page3 = numbers.numberPage(page3Req);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(List.of("36 COMPOSITE √36 >= 6",
                        "31 PRIME √31 >= 5",
                        "30 COMPOSITE √30 >= 5",
                        "29 PRIME √29 >= 5",
                        "28 COMPOSITE √28 >= 5",
                        "27 COMPOSITE √27 >= 5"),
                page3.stream()
                        .map(WholeNumber::toString)
                        .collect(Collectors.toList()));

        try {
            assertEquals(21L, page3.totalElements());
            assertEquals(4L, page3.totalPages());
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // Some NoSQL databases lack the ability to count the total results
            } else {
                throw x;
            }
        }

        Page<WholeNumber> page4 = numbers.numberPage(page3.nextPageRequest());

        assertEquals(List.of("26 COMPOSITE √26 >= 5",
                        "25 COMPOSITE √25 >= 5",
                        "1 ONE √1 >= 1"),
                page4.stream()
                        .map(WholeNumber::toString)
                        .collect(Collectors.toList()));

        Page<WholeNumber> page2 = numbers.numberPage(page3.previousPageRequest());

        assertEquals(List.of("42 COMPOSITE √42 >= 6",
                        "41 PRIME √41 >= 6",
                        "40 COMPOSITE √40 >= 6",
                        "39 COMPOSITE √39 >= 6",
                        "38 COMPOSITE √38 >= 6",
                        "37 PRIME √37 >= 6"),
                page2.stream()
                        .map(WholeNumber::toString)
                        .collect(Collectors.toList()));
    }

    @Assertion(id = "539", strategy = """
            Use a repository method where record components define a subset of
            entity attributes to retrieve. The respository method must return an
            array of records.
            """)
    public void testRecordComponentsChooseAttributeReturnArray() {
        NumberInfo[] found = numbers.infoByNumBitsNeeded((short) 3);

        assertEquals(4, found.length); // for binary 100, 101, 110, 111

        Map<Long, NumberInfo> threeBitNums = new HashMap<>();
        for (NumberInfo info : found) {
            assertEquals(Short.valueOf((short) 3), info.numBitsRequired());
            assertNull(threeBitNums.put(info.id(), info)); // no duplicates
        }

        NumberInfo num4 = threeBitNums.get(4L);
        assertNotNull(num4);
        assertFalse(num4.isOdd());
        assertEquals(NumberType.COMPOSITE, num4.numType());

        NumberInfo num5 = threeBitNums.get(5L);
        assertNotNull(num5);
        assertTrue(num5.isOdd());
        assertEquals(NumberType.PRIME, num5.numType());

        NumberInfo num6 = threeBitNums.get(6L);
        assertNotNull(num6);
        assertFalse(num6.isOdd());
        assertEquals(NumberType.COMPOSITE, num6.numType());

        NumberInfo num7 = threeBitNums.get(7L);
        assertNotNull(num7);
        assertTrue(num7.isOdd());
        assertEquals(NumberType.PRIME, num7.numType());
    }

    @Assertion(id = "539", strategy = """
            Use a repository method where record components define a subset of
            entity attributes to retrieve. The respository method must return a
            List of records.
            """)
    public void testRecordComponentsChooseAttributeReturnList() {
        final boolean odd = true;
        List<NumberInfo> found = numbers.infoByParity(odd);

        assertEquals(50, found.size()); // half of numbers 1 to 100

        Map<Long, NumberInfo> odds = new HashMap<>();
        for (NumberInfo num : found) {
            assertTrue(num.isOdd());
            assertNull(odds.put(num.id(), num)); // no duplicates allowed
        }
    }

    @Assertion(id = "539", strategy = """
            Use a repository method where record components define a subset of
            entity attributes to retrieve. The respository method must return a
            single record.
            """)
    public void testRecordComponentsChooseAttributeReturnOne() {
        NumberInfo num12 = numbers.infoByIdentifier(12L);
        assertEquals(12L, num12.id());
        assertFalse(num12.isOdd());
        assertEquals(Short.valueOf((short) 4), num12.numBitsRequired());
        assertEquals(NumberType.COMPOSITE, num12.numType());

        try {
            NumberInfo negative2 = numbers.infoByIdentifier(-2L);
            fail("Must raise EmptyResultException when no result is found." +
                    "Instead: " + negative2);
        } catch (EmptyResultException x) {
            // expected
        }
    }

    @Assertion(id = "539", strategy = """
            Use a repository method where record components define a subset of
            entity attributes to retrieve. The respository method must return an
            Optional record.
            """)
    public void testRecordComponentsChooseAttributeReturnOptional() {
        NumberInfo num61 = numbers.infoIfFound(61L).orElseThrow();
        assertEquals(61L, num61.id());
        assertTrue(num61.isOdd());
        assertEquals(Short.valueOf((short) 6), num61.numBitsRequired());
        assertEquals(NumberType.PRIME, num61.numType());

        assertFalse(numbers.infoIfFound(-3L).isPresent());
    }

    @Assertion(id = "539", strategy = """
            Use a repository method where record components define a subset of
            entity attributes to retrieve. The respository method must return a
            Page of records.
            """)
    public void testRecordComponentsChooseAttributeReturnPage() {
        PageRequest page2Req = PageRequest.ofPage(2).size(5);
        Page<NumberInfo> page2;
        try {
            page2 = numbers.infoPaginated(true, page2Req);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(5, page2.numberOfElements());

        NumberInfo num;

        assertNotNull(num = page2.content().get(0));
        assertEquals(11L, num.id());
        assertEquals(Short.valueOf((short) 4), num.numBitsRequired());
        assertTrue(num.isOdd());
        assertEquals(NumberType.PRIME, num.numType());

        assertNotNull(num = page2.content().get(1));
        assertEquals(13L, num.id());
        assertEquals(Short.valueOf((short) 4), num.numBitsRequired());
        assertTrue(num.isOdd());
        assertEquals(NumberType.PRIME, num.numType());

        assertNotNull(num = page2.content().get(2));
        assertEquals(15L, num.id());
        assertEquals(Short.valueOf((short) 4), num.numBitsRequired());
        assertTrue(num.isOdd());
        assertEquals(NumberType.COMPOSITE, num.numType());

        assertNotNull(num = page2.content().get(3));
        assertEquals(17L, num.id());
        assertEquals(Short.valueOf((short) 5), num.numBitsRequired());
        assertTrue(num.isOdd());
        assertEquals(NumberType.PRIME, num.numType());

        assertNotNull(num = page2.content().get(4));
        assertEquals(19L, num.id());
        assertEquals(Short.valueOf((short) 5), num.numBitsRequired());
        assertTrue(num.isOdd());
        assertEquals(NumberType.PRIME, num.numType());

        Page<NumberInfo> page1 = numbers.infoPaginated(true, page2.previousPageRequest());
        assertEquals(5, page1.numberOfElements());
        assertTrue(page1.hasNext());

        assertNotNull(num = page1.content().get(0));
        assertEquals(1L, num.id());
        assertEquals(Short.valueOf((short) 1), num.numBitsRequired());
        assertTrue(num.isOdd());
        assertEquals(NumberType.ONE, num.numType());

        Page<NumberInfo> page3 = numbers.infoPaginated(true, page2.nextPageRequest());
        assertEquals(5, page3.numberOfElements());
        assertTrue(page3.hasNext());

        assertNotNull(num = page3.content().get(4));
        assertEquals(29L, num.id());
        assertEquals(Short.valueOf((short) 5), num.numBitsRequired());
        assertTrue(num.isOdd());
        assertEquals(NumberType.PRIME, num.numType());
    }

    @Assertion(id = "539", strategy = """
            Use a repository method where record components define a subset of
            entity attributes to retrieve. The respository method must return a
            Stream of records.
            """)
    public void testRecordComponentsChooseAttributeReturnStream() {
        Stream<NumberInfo> found = numbers.infoByOddness(false);

        Map<Long, NumberInfo> evens = new HashMap<>();
        found.forEach(num -> {
            assertFalse(num.isOdd());
            assertNull(evens.put(num.id(), num)); // no duplicates allowed
            if (num.id() == 2L) {
                assertEquals(NumberType.PRIME, num.numType());
            } else {
                assertEquals(NumberType.COMPOSITE, num.numType());
            }
        });

        assertEquals(50, evens.size()); // half of numbers 1 to 100
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that returns an array of a record type,
            where record components are annotated to select a subset of entity
            attributes.
            """)
    public void testReturnArrayOfRecordThatSelectsAttributes() {
        CardinalNumber[] found;

        found = numbers.cardinalNumbers((short) 5);

        assertEquals(List.of("16 COMPOSITE (5 bits)",
                        "17 PRIME (5 bits)",
                        "18 COMPOSITE (5 bits)",
                        "19 PRIME (5 bits)",
                        "20 COMPOSITE (5 bits)",
                        "21 COMPOSITE (5 bits)",
                        "22 COMPOSITE (5 bits)",
                        "23 PRIME (5 bits)",
                        "24 COMPOSITE (5 bits)",
                        "25 COMPOSITE (5 bits)",
                        "26 COMPOSITE (5 bits)",
                        "27 COMPOSITE (5 bits)",
                        "28 COMPOSITE (5 bits)",
                        "29 PRIME (5 bits)",
                        "30 COMPOSITE (5 bits)",
                        "31 PRIME (5 bits)"),
                Arrays.stream(found)
                        .map(CardinalNumber::toString)
                        .sorted()
                        .collect(Collectors.toList()));

        found = numbers.cardinalNumbers((short) 0);

        assertEquals(0, found.length);
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that returns an Optional of a record type,
            where record components are annotated to select a subset of entity
            attributes.
            """)
    public void testReturnOptionalOfRecordThatSelectsAttributes() {
        Optional<CardinalNumber> found;

        found = numbers.cardinalNumberOptional(79);

        assertTrue(found.isPresent());
        assertEquals(79L, found.get().value());
        assertEquals(Short.valueOf((short) 7), found.get().numBitsRequired());
        assertEquals(NumberType.PRIME.ordinal(), found.get().numType());

        found = numbers.cardinalNumberOptional(0); // database only has 1 to 100

        assertFalse(found.isPresent());
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that returns a Page of a record type,
            where record components are annotated to select a subset of entity
            attributes.
            """)
    public void testReturnPageOfRecordThatSelectsAttributes() {
        Page<CardinalNumber> page1;

        try {
            page1 = numbers.cardinalNumberPage(9L, PageRequest.ofSize(7));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(List.of("83 PRIME (7 bits)",
                        "89 PRIME (7 bits)",
                        "97 PRIME (7 bits)",
                        "81 COMPOSITE (7 bits)",
                        "82 COMPOSITE (7 bits)",
                        "84 COMPOSITE (7 bits)",
                        "85 COMPOSITE (7 bits)"),
                page1.stream()
                        .map(CardinalNumber::toString)
                        .collect(Collectors.toList()));

        Page<CardinalNumber> page2 = numbers
                .cardinalNumberPage(9L, page1.nextPageRequest());

        assertEquals(List.of("86 COMPOSITE (7 bits)",
                        "87 COMPOSITE (7 bits)",
                        "88 COMPOSITE (7 bits)",
                        "90 COMPOSITE (7 bits)",
                        "91 COMPOSITE (7 bits)",
                        "92 COMPOSITE (7 bits)",
                        "93 COMPOSITE (7 bits)"),
                page2.stream()
                        .map(CardinalNumber::toString)
                        .collect(Collectors.toList()));

        Page<CardinalNumber> page3 = numbers
                .cardinalNumberPage(9L, page2.nextPageRequest());

        assertEquals(List.of("94 COMPOSITE (7 bits)",
                        "95 COMPOSITE (7 bits)",
                        "96 COMPOSITE (7 bits)",
                        "98 COMPOSITE (7 bits)",
                        "99 COMPOSITE (7 bits)"),
                page3.stream()
                        .map(CardinalNumber::toString)
                        .collect(Collectors.toList()));
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that returns a record type, where the record
            components are annotated to select a subset of entity attributes.
            The Find annotation value is needed to identify the entity.
            """)
    public void testReturnRecordThatSelectsAttributesFindEntity() {

        CardinalNumber found = characters.cardinalNumberOf(59L);

        assertEquals(59L, found.value());
        assertEquals(Short.valueOf((short) 6), found.numBitsRequired());
        assertEquals(NumberType.PRIME.ordinal(), found.numType());

        try {
            found = characters.cardinalNumberOf(0); // database only has 1 to 100
        } catch (EmptyResultException x) {
            // expected
        }
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that returns a Stream of record type,
            where the record components are annotated to select a subset of entity
            attributes. The Find annotation value is needed to identify the entity.
            """)
    public void testReturnStreamOfRecordThatSelectsAttributesFindEntity() {

        Stream<CardinalNumber> stream = characters.cardinalNumberStream(3L);

        assertEquals(List.of("10 COMPOSITE (4 bits)",
                        "11 PRIME (4 bits)",
                        "12 COMPOSITE (4 bits)",
                        "13 PRIME (4 bits)",
                        "14 COMPOSITE (4 bits)",
                        "15 COMPOSITE (4 bits)",
                        "9 COMPOSITE (4 bits)"
                ),
                stream
                        .map(CardinalNumber::toString)
                        .sorted()
                        .collect(Collectors.toList()));
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that selects a single entity attribute as
            an array of long.
            """)
    public void testSelectEntityAttributeAsArrayOfLong() {

        long[] found = positives.requiringBits((short) 4);

        assertEquals(List.of(8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L),
                Arrays.stream(found)
                        .sorted()
                        .boxed()
                        .collect(Collectors.toList()));

        long[] notFound = positives.requiringBits((short) 0);

        assertEquals(0, notFound.length);
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that selects a single entity attribute as
            an List of long. The entity type is identified by the Find annotation.
            """)
    public void testSelectEntityAttributeAsListOfLong() {

        List<Long> found = shared.withTruncatedSqrt(7);

        assertEquals(List.of(49L, 50L, 51L, 52L, 53L, 54L, 55L, 56L,
                        57L, 58L, 59L, 60L, 61L, 62L, 63L),
                found.stream()
                        .sorted()
                        .collect(Collectors.toList()));

        assertEquals(List.of(), shared.withTruncatedSqrt(0));
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that selects a single entity attribute as
            an int value. The entity type is identified by the Find annotation.
            """)
    public void testSelectEntityAttributeAsOne() {

        assertEquals(30, shared.valueOf("1e"));

        assertEquals(42, shared.valueOf("2a"));

        try {
            int value = shared.valueOf("-4c");
            fail("Should not be able to find a value that is not in the database: " +
                    value);
        } catch (EmptyResultException x) {
            // expected
        }
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that selects a single entity attribute as
            an Optional.
            """)
    public void testSelectEntityAttributeAsOptional() {

        assertEquals(NumberType.COMPOSITE, positives.typeOfNumber(18).orElseThrow());

        assertEquals(NumberType.PRIME, positives.typeOfNumber(19).orElseThrow());

        assertEquals(NumberType.ONE, positives.typeOfNumber(1).orElseThrow());

        assertFalse(positives.typeOfNumber(0).isPresent());
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that selects a single entity attribute as a Page.
            """)
    public void testSelectEntityAttributeAsPage() {
        final boolean odd = true;

        PageRequest page4Request = PageRequest.ofPage(4).size(5);
        Page<Long> page4;
        try {
            page4 = positives.withParity(odd, page4Request);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(List.of(31L, 33L, 35L, 37L, 39L),
                page4.content());

        Page<Long> page3 = positives.withParity(odd, page4.previousPageRequest());

        assertEquals(List.of(21L, 23L, 25L, 27L, 29L),
                page3.content());

        Page<Long> page2 = positives.withParity(odd, page3.previousPageRequest());

        assertEquals(List.of(11L, 13L, 15L, 17L, 19L),
                page2.content());

        Page<Long> page5 = positives.withParity(odd, page4.nextPageRequest());

        assertEquals(List.of(41L, 43L, 45L, 47L, 49L),
                page5.content());
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that selects a single entity attribute as
            a Stream of long. The entity type is identified by the Find annotation.
            """)
    public void testSelectEntityAttributeAsStreamOfLong() {

        Stream<Long> found = shared.withBitRequirementOf((short) 5);

        assertEquals(List.of(16L, 17L, 18L, 19L, 20L, 21L, 22L, 23L,
                        24L, 25L, 26L, 27L, 28L, 29L, 30L, 31L),
                found
                        .sorted()
                        .collect(Collectors.toList()));

        assertEquals(0L,
                shared.withBitRequirementOf((short) -1).count());
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that selects a named subset of entity attributes
            to retrieve as an array of records.
            """)
    public void testSelectEntityAttributesAsArrayOfRecord() {
        Order<NaturalNumber> order;
        if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
            // Column and Key-Value databases might not be capable of sorting.
            order = Order.by();
        } else {
            order = Order.by(_NaturalNumber.id.desc());
        }

        WholeNumber[] found = numbers.wholeNumbers(2, order);

        // 5 numbers (4, 5, 6, 7, 8) have square roots that round down to 2
        assertEquals(5, found.length);

        Map<Long, Integer> valueToArrayIndex = new HashMap<>();
        if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
            // Column and Key-Value databases might not be capable of sorting.
            for (long expectedValue : new long[]{4L, 5L, 6L, 7L, 8L}) {
                int index = -1;
                for (int i = 0; i < found.length; i++) {
                    if (found[i].value() == expectedValue) {
                        index = i;
                        break;
                    }
                }
                if (index == -1) {
                    fail("Did not find WholeNumber with value of " + expectedValue);
                } else {
                    valueToArrayIndex.put(expectedValue, index);
                }
            }
        } else {
            // Sorted by id/value descending
            valueToArrayIndex.put(8L, 0);
            valueToArrayIndex.put(7L, 1);
            valueToArrayIndex.put(6L, 2);
            valueToArrayIndex.put(5L, 3);
            valueToArrayIndex.put(4L, 4);
        }

        WholeNumber num;
        num = found[valueToArrayIndex.get(4L)];
        assertEquals(4L, num.value());
        assertEquals(2L, num.sqrtFloor());
        assertEquals(NumberType.COMPOSITE.ordinal(), num.numType());

        num = found[valueToArrayIndex.get(5L)];
        assertEquals(5L, num.value());
        assertEquals(2L, num.sqrtFloor());
        assertEquals(NumberType.PRIME.ordinal(), num.numType());

        num = found[valueToArrayIndex.get(6L)];
        assertEquals(6L, num.value());
        assertEquals(2L, num.sqrtFloor());
        assertEquals(NumberType.COMPOSITE.ordinal(), num.numType());

        num = found[valueToArrayIndex.get(7L)];
        assertEquals(7L, num.value());
        assertEquals(2L, num.sqrtFloor());
        assertEquals(NumberType.PRIME.ordinal(), num.numType());

        num = found[valueToArrayIndex.get(8L)];
        assertEquals(8L, num.value());
        assertEquals(2L, num.sqrtFloor());
        assertEquals(NumberType.COMPOSITE.ordinal(), num.numType());
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that selects a named subset of entity attributes
            to retrieve as a List of records.
            """)
    public void testSelectEntityAttributesAsListOfRecord() {
        Order<NaturalNumber> order;
        if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
            // Column and Key-Value databases might not be capable of sorting.
            order = Order.by();
        } else {
            order = Order.by(_NaturalNumber.floorOfSquareRoot.asc(),
                    _NaturalNumber.id.desc());
        }

        List<WholeNumber> found = numbers.wholeNumberList(NumberType.PRIME.ordinal(),
                order);

        Map<Long, WholeNumber> primes = new HashMap<>();
        for (WholeNumber num : found) {
            assertEquals(NumberType.PRIME.ordinal(), num.numType());
            assertNull(primes.put(num.value(), num)); // no duplicates
        }

        assertEquals(25, primes.size()); // numbers 1 to 100 are in the database

        // results ordered by sqrtFloor ascending, then value descending
        // primes with sqrtFloor 1:  3  2
        // primes with sqrtFloor 2:  7  5
        // primes with sqrtFloor 3: 13 11
        // primes with sqrtFloor 4: 23 19 17
        // primes with sqrtFloor 5: 31 29
        // primes with sqrtFloor 6: 47 43 41 37
        // primes with sqrtFloor 7: 61 59 53
        // primes with sqrtFloor 8: 79 73 71 67
        // primes with sqrtFloor 9: 97 89 83

        WholeNumber num3 = type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)
                ? primes.get(3L)
                : found.get(0);
        assertNotNull(num3);
        assertEquals(1L, num3.sqrtFloor());

        WholeNumber num17 = type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)
                ? primes.get(17L)
                : found.get(8);
        assertNotNull(num17);
        assertEquals(4L, num17.sqrtFloor());

        WholeNumber num59 = type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)
                ? primes.get(59L)
                : found.get(16);
        assertNotNull(num59);
        assertEquals(7L, num59.sqrtFloor());

        WholeNumber num83 = type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)
                ? primes.get(83L)
                : found.get(24);
        assertNotNull(num83);
        assertEquals(9L, num83.sqrtFloor());
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that selects a named subset of entity attributes
            to retrieve as an Optional record.
            """)
    public void testSelectEntityAttributesAsOptionalOfRecord() {
        Optional<WholeNumber> found;

        found = numbers.wholeNumberOf(85);

        assertTrue(found.isPresent());
        assertEquals(85L, found.get().value());
        assertEquals(9L, found.get().sqrtFloor());
        assertEquals(NumberType.COMPOSITE.ordinal(), found.get().numType());

        found = numbers.wholeNumberOf(-150); // database only has 1 to 100

        assertFalse(found.isPresent());
    }

    @Assertion(id = "539", strategy = """
            Use a repository method that selects a named subset of entity attributes
            to retrieve as a Page of record.
            """)
    public void testSelectEntityAttributesAsPageOfRecord() {
        PageRequest page3Req = PageRequest.ofPage(3).size(4);
        Page<WholeNumber> page3;
        try {
            page3 = numbers.wholeNumberPage(NumberType.PRIME.ordinal(),
                    page3Req,
                    Order.by(Sort.asc(_NaturalNumber.ID)));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(4, page3.numberOfElements());

        WholeNumber num;

        assertNotNull(num = page3.content().get(0));
        assertEquals(23L, num.value());
        assertEquals(4L, num.sqrtFloor());
        assertEquals(NumberType.PRIME.ordinal(), num.numType());

        assertNotNull(num = page3.content().get(1));
        assertEquals(29L, num.value());
        assertEquals(5L, num.sqrtFloor());
        assertEquals(NumberType.PRIME.ordinal(), num.numType());

        assertNotNull(num = page3.content().get(2));
        assertEquals(31L, num.value());
        assertEquals(5L, num.sqrtFloor());
        assertEquals(NumberType.PRIME.ordinal(), num.numType());

        assertNotNull(num = page3.content().get(3));
        assertEquals(37L, num.value());
        assertEquals(6L, num.sqrtFloor());
        assertEquals(NumberType.PRIME.ordinal(), num.numType());

        Page<WholeNumber> page4;
        page4 = numbers.wholeNumberPage(NumberType.PRIME.ordinal(),
                page3.nextPageRequest(),
                Order.by(Sort.asc(_NaturalNumber.ID)));
        assertEquals(4, page4.numberOfElements());
        assertTrue(page4.hasPrevious());
        assertTrue(page4.hasNext());

        assertNotNull(num = page4.content().get(0));
        assertEquals(41L, num.value());
        assertEquals(6L, num.sqrtFloor());
        assertEquals(NumberType.PRIME.ordinal(), num.numType());

        Page<WholeNumber> page2;
        page2 = numbers.wholeNumberPage(NumberType.PRIME.ordinal(),
                page3.previousPageRequest(),
                Order.by(Sort.asc(_NaturalNumber.ID)));
        assertEquals(4, page2.numberOfElements());
        assertTrue(page2.hasNext());
        assertTrue(page2.hasPrevious());

        assertNotNull(num = page2.content().get(3));
        assertEquals(19L, num.value());
        assertEquals(4L, num.sqrtFloor());
        assertEquals(NumberType.PRIME.ordinal(), num.numType());
    }

    @Assertion(id = "133", strategy = "Use a repository method that returns a single entity value where a single result is found.")
    public void testSingleEntity() {
        AsciiCharacter ch;
        try {
            ch = characters.findByHexadecimalIgnoreCase("2B");
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                return; // NoSQL databases might not be capable of IgnoreCase
            } else {
                throw x;
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
            page = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(
                    NumberType.COMPOSITE, 1L, pagination, Sort.desc("id"));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                // Column and Key-Value databases might not be capable of And.
                // Key-Value databases might not be capable of LessThanEqual.
                return;
            } else {
                throw x;
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
            page1 = characters.findByNumericValueBetween(
                    68, 90, pageRequest,
                    Order.by(_AsciiChar.numericValue.asc()));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                // Key-Value databases might not be capable of Between.
                return;
            } else {
                throw x;
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
            page1 = characters.findByNumericValueBetween(
                    100, 122, pageRequest,
                    Order.by(_AsciiCharacter.numericValue.asc()));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                // Key-Value databases might not be capable of Between.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(List.of('d', 'e', 'f', 'g', 'h', 'i', 'j'),
                page1.stream()
                        .map(AsciiCharacter::getThisCharacter)
                        .collect(Collectors.toList()));
    }

    @Assertion(id = "133", strategy = "Use the StaticMetamodel to refer to entity attribute names in a type-safe manner.")
    public void testStaticMetamodelAttributeNames() {
        assertEquals(_AsciiChar.HEXADECIMAL, _AsciiChar.hexadecimal.name());
        assertEquals(_AsciiChar.ID, _AsciiChar.id.name());
        assertEquals("isControl", _AsciiChar.isControl.name());
        assertEquals(_AsciiChar.NUMERICVALUE, _AsciiChar.numericValue.name());
        assertEquals("thisCharacter", _AsciiChar.thisCharacter.name());
    }

    @Assertion(id = "133", strategy = "Use a pre-generated StaticMetamodel to refer to entity attribute names in a type-safe manner.")
    public void testStaticMetamodelAttributeNamesPreGenerated() {
        assertEquals(_AsciiCharacter.HEXADECIMAL, _AsciiCharacter.hexadecimal.name());
        assertEquals(_AsciiCharacter.ID, _AsciiCharacter.id.name());
        assertEquals("isControl", _AsciiCharacter.isControl.name());
        assertEquals(_AsciiChar.NUMERICVALUE, _AsciiCharacter.numericValue.name());
        assertEquals("thisCharacter", _AsciiCharacter.thisCharacter.name());
    }

    @Assertion(id = "133", strategy = "Use the StaticMetamodel to obtain descending Sorts for an entity attribute a type-safe manner.")
    public void testStaticMetamodelDescendingSorts() {
        assertEquals(Sort.desc(_AsciiChar.ID), _AsciiChar.id.desc());
        assertEquals(Sort.descIgnoreCase("hexadecimal"), _AsciiChar.hexadecimal.descIgnoreCase());
        assertEquals(Sort.descIgnoreCase("thisCharacter"), _AsciiChar.thisCharacter.descIgnoreCase());

        Sort<AsciiCharacter> sort = _AsciiChar.numericValue.desc();
        AsciiCharacter[] found;
        try {
            found = characters.findFirst3ByNumericValueGreaterThanEqualAndHexadecimalEndsWith(
                    30, "1", sort);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of EndsWith.
                // Column and Key-Value databases might not be capable of sorting.
                // Column and Key-Value databases might not be capable of And.
                // Key-Value databases might not be capable of GreaterThanEqual.
                return;
            } else {
                throw x;
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
            found = characters.findFirst3ByNumericValueGreaterThanEqualAndHexadecimalEndsWith(
                    30, "4", sort);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of EndsWith.
                // Column and Key-Value databases might not be capable of sorting.
                // Column and Key-Value databases might not be capable of And.
                // Key-Value databases might not be capable of GreaterThanEqual.
                return;
            } else {
                throw x;
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
            chars = characters.findByNumericValueLessThanEqualAndNumericValueGreaterThanEqual(
                    109, 101);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of And.
                // Key-Value databases might not be capable of GTE/LTE.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(Arrays.toString(new Character[]{'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm'}),
                Arrays.toString(chars.stream().map(AsciiCharacter::getThisCharacter).sorted().toArray()));

        assertEquals(101 + 102 + 103 + 104 + 105 + 106 + 107 + 108 + 109,
                chars.stream().mapToInt(AsciiCharacter::getNumericValue).sum());

        Set<String> sorted = new TreeSet<>();
        chars.forEach(ch -> sorted.add(ch.getHexadecimal()));
        assertEquals(new TreeSet<>(Set.of("65", "66", "67", "68", "69", "6a", "6b", "6c", "6d")),
                sorted);

        List<AsciiCharacter> empty = characters.findByNumericValueLessThanEqualAndNumericValueGreaterThanEqual(115, 120);
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
            page = characters.findByNumericValueBetween(48, 90, third10, order); // 'D' to 'M'
        } catch (UnsupportedOperationException x) {
            // Some NoSQL databases lack the ability to count the total results
            // and therefore cannot support a return type of Page.
            // Column and Key-Value databases might not be capable of sorting.
            // Key-Value databases might not be capable of Between.
            return;
        }

        assertEquals(3, page.pageRequest().page());
        assertTrue(page.hasContent());
        assertEquals(10, page.numberOfElements());
        try {
            assertEquals(43L, page.totalElements());
            assertEquals(5L, page.totalPages());
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // Some NoSQL databases lack the ability to count the total results
            } else {
                throw x;
            }
        }

        assertEquals("44:D;45:E;46:F;47:G;48:H;49:I;4a:J;4b:K;4c:L;4d:M;",
                page.stream()
                        .map(c -> c.getHexadecimal() + ':' + c.getThisCharacter() + ';')
                        .reduce("", String::concat));

        assertTrue(page.hasNext());
        PageRequest fourth10 = page.nextPageRequest();
        page = characters.findByNumericValueBetween(48, 90, fourth10, order); // 'N' to 'W'

        assertEquals(4, page.pageRequest().page());
        assertTrue(page.hasContent());
        assertEquals(10, page.numberOfElements());
        try {
            assertEquals(43L, page.totalElements());
            assertEquals(5L, page.totalPages());
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // Some NoSQL databases lack the ability to count the total results
            } else {
                throw x;
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
            page = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(
                    NumberType.PRIME, 8L, third5, sort);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                // Column and Key-Value databases might not be capable of And.
                // Key-Value databases might not be capable of LessThanEqual.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(3, page.pageRequest().page());
        assertEquals(5, page.numberOfElements());

        assertEquals(Arrays.toString(new Long[]{37L, 31L, 29L, 23L, 19L}),
                Arrays.toString(page.stream().map(NaturalNumber::getId).toArray()));

        assertTrue(page.hasNext());
        PageRequest fourth5 = page.nextPageRequest();

        page = numbers.findByNumTypeAndFloorOfSquareRootLessThanEqual(NumberType.PRIME, 8L, fourth5, sort);

        assertEquals(4, page.pageRequest().page());
        assertEquals(5, page.numberOfElements());

        assertEquals(Arrays.toString(new Long[]{17L, 13L, 11L, 7L, 5L}),
                Arrays.toString(page.stream().map(NaturalNumber::getId).toArray()));
    }

    @Assertion(id = "133", strategy = "Use a repository method with the True keyword.")
    public void testTrue() {
        Iterable<NaturalNumber> odd;
        try {
            odd = positives.findByIsOddTrueAndIdLessThanEqualOrderByIdDesc(10L);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of And.
                // Key-Value databases might not be capable of LessThanEqual.
                // Key-Value databases might not be capable of True/False comparison.
                return;
            } else {
                throw x;
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

    @Assertion(id = "458", strategy = """
            Use a repository method with a JCQL UPDATE query without a WHERE clause.
            This method also tests the addition, subtraction, and multiplication operators.
            """)
    public void testUpdateQueryWithoutWhereClause() {
        // Ensure there is no data left over from other tests:

        try {
            shared.removeAll();
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH) && TestProperty.delay.isSet()) {
                // NoSQL databases with eventual consistency might not be capable
                // of counting removed entities.
                // Use alternative approach for ensuring no data is present:
                boxes.deleteAll(boxes.findAll().toList());
            } else {
                throw x;
            }
        }

        TestPropertyUtility.waitForEventualConsistency();

        boxes.saveAll(List.of(Box.of("TestUpdateQueryWithoutWhereClause-01", 125, 117, 44),
                Box.of("TestUpdateQueryWithoutWhereClause-02", 173, 165, 52),
                Box.of("TestUpdateQueryWithoutWhereClause-03", 229, 221, 60)));

        TestPropertyUtility.waitForEventualConsistency();

        boolean resized;
        try {
            // increases length by 12, decreases width by 12, and doubles the height
            assertEquals(3L, shared.resizeAll(12, 2));
            resized = true;
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of arithmetic in updates.
                resized = false;
            } else {
                throw x;
            }
        }

        TestPropertyUtility.waitForEventualConsistency();

        if (resized) {
            Box b1 = boxes.findById("TestUpdateQueryWithoutWhereClause-01").orElseThrow();
            assertEquals(137, b1.length); // increased by 12
            assertEquals(105, b1.width); // decreased by 12
            assertEquals(88, b1.height); // increased by factor of 2

            Box b2 = boxes.findById("TestUpdateQueryWithoutWhereClause-02").orElseThrow();
            assertEquals(185, b2.length); // increased by 12
            assertEquals(153, b2.width); // decreased by 12
            assertEquals(104, b2.height); // increased by factor of 2

            Box b3 = boxes.findById("TestUpdateQueryWithoutWhereClause-03").orElseThrow();
            assertEquals(241, b3.length); // increased by 12
            assertEquals(209, b3.width); // decreased by 12
            assertEquals(120, b3.height); // increased by factor of 2
        }

        try {
            var removeAllResult = shared.removeAll();
            assertEquals(3, removeAllResult);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH) && TestProperty.delay.isSet()) {
                // NoSQL databases with eventual consistency might not be capable
                // of counting removed entities.
                // Use alternative approach for removing entities.
                boxes.deleteAll(boxes.findAll().toList());
            } else {
                throw x;
            }
        }

        TestPropertyUtility.waitForEventualConsistency();

        try {
            assertEquals(0L, shared.resizeAll(2, 1));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of arithmetic in updates.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "458", strategy ="""
            Use a repository method with a JCQL UPDATE query with a WHERE clause.
            This method also tests the assignment and division operators.
            """)
    public void testUpdateQueryWithWhereClause() {
        try {
            // Ensure there is no data left over from other tests:
            shared.deleteIfPositive();
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of And.
                return;
            } else if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH) && TestProperty.delay.isSet()) {
                // NoSQL databases with eventual consistency might not be capable
                // of counting removed entities.
                // Use alternative approach for ensuring no data is present:
                shared.deleteIfPositiveWithoutReturnRecords();
            } else {
                throw x;
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of arithmetic in updates.
                c1yExpected = 5.25f;
                c1xExpected = 1.41D;// no change
            } else {
                throw x;
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of And.
                return;
            } else if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH) && TestProperty.delay.isSet()) {
                // NoSQL databases with eventual consistency might not be capable
                // of counting removed entities.
                // Use alternative approach for ensuring no data is present:
                shared.deleteIfPositiveWithoutReturnRecords();
            } else {
                throw x;
            }
        }
        TestPropertyUtility.waitForEventualConsistency();

        assertFalse(shared.withUUID(id1).isPresent());
        assertFalse(shared.withUUID(id2).isPresent());
    }

    @Assertion(id = "133",
            strategy = "Use a repository method with varargs Sort... specifying a mixture of ascending and descending order, " +
                    "and verify all results are returned and are ordered according to the sort criteria.")
    public void testVarargsSort() {
        List<NaturalNumber> list;
        try {
            list = numbers.findByIdLessThanEqual(
                    12L,
                    Sort.asc("floorOfSquareRoot"),
                    Sort.desc("numBitsRequired"),
                    Sort.asc("id"));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
                // Key-Value databases might not be capable of LessThanEqual.
                return;
            } else {
                throw x;
            }
        }

        assertEquals(Arrays.toString(new Long[]{2L, 3L, // square root rounds down to 1; 2 bits
                        1L, // square root rounds down to 1; 1 bit
                        8L, // square root rounds down to 2; 4 bits
                        4L, 5L, 6L, 7L, // square root rounds down to 2; 3 bits
                        9L, 10L, 11L, 12L}), // square root rounds down to 3; 4 bits
                Arrays.toString(list.stream().map(NaturalNumber::getId).toArray()));
    }
}
