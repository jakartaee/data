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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;

import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.ReadOnlyTest;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.framework.read.only._AsciiChar;
import ee.jakarta.tck.data.framework.read.only._AsciiCharacter;
import ee.jakarta.tck.data.framework.read.only.AsciiCharacter;
import ee.jakarta.tck.data.framework.read.only.AsciiCharacters;
import ee.jakarta.tck.data.framework.read.only.AsciiCharactersPopulator;
import ee.jakarta.tck.data.framework.read.only.CustomRepository;
import ee.jakarta.tck.data.framework.read.only.NaturalNumber;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbers;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbersPopulator;
import ee.jakarta.tck.data.framework.read.only.PositiveIntegers;
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
                .addClasses(EntityTests.class,
                            Box.class,
                            Boxes.class);
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

    @BeforeEach
    //Inject doesn't happen until after BeforeClass so this is necessary before each test
    public void setup() {
        NaturalNumbersPopulator.get().populate(numbers);
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
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfNotNull()) {
                throw x;
            } else {
                return;
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
            if (type.capableOfBetween() &&
                type.capableOfMultipleSort()) {
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
            if (type.capableOfBetween() &&
                type.capableOfCount()) {
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
            if (type.capableOfGreaterThanEqual()) {
                throw x;
            } else {
                return;
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
            AsciiCharacter del = characters.findByIsControlTrueAndNumericValueBetween(33, 127);
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
            AsciiCharacter j = characters.findByHexadecimalIgnoreCase("6A");
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

        assertEquals(Arrays.toString(new Character[]{'9', '8', '7', '6', '5', '4'}),
                Arrays.toString(stream.map(AsciiCharacter::getThisCharacter).toArray()));
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
                even.stream().map(NaturalNumber::getId).sorted().collect(Collectors.toList()));
    }

    @Assertion(id = "133",
            strategy = "Use a repository method with findFirstBy that returns the first entity value " +
                    "where multiple results would otherwise be found.")
    public void testFindFirst() {
        Optional<AsciiCharacter> none;
        try {
            none = characters.findFirstByHexadecimalStartsWithAndIsControlOrderByIdAsc(
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
                            .collect(Collectors.toList()));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfMultipleSort()) {
                throw x;
            } else {
                return;
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

    @Assertion(id = "133", strategy = "Use a repository method existsByIdGreaterThan confirming the correct boolean is returned.")
    public void testGreaterThanEqualExists() {
        try {
            assertTrue(positives.existsByIdGreaterThan(0L));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfGreaterThan()) {
                throw x;
            } else {
                return;
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

    @Assertion(id = "133", strategy = "Use a repository method countByIdLessThan confirming the correct count is returned.")
    public void testLessThanWithCount() {
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
            AsciiCharacter ch = characters.findByIsControlTrueAndNumericValueBetween(10, 15);
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

    @Assertion(id = "133", strategy = "Use a repository method with Or, expecting UnsupportedOperationException if the underlying database is not capable.")
    public void testOr() {
        Stream<NaturalNumber> found;
        try {
            found = positives.findByNumTypeOrFloorOfSquareRoot(NumberType.ONE, 2L);
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

        assertEquals(Arrays.toString(new Long[]{
                        23L, 19L, 17L, // square root rounds down to 4; prime
                        24L, 22L, 21L, 20L, 18L}), // square root rounds down to 4; composite
                     Arrays.toString(page.stream()
                                         .map(NaturalNumber::getId)
                                         .toArray()));

        assertTrue(page.hasNext());
        pagination = page.nextPageRequest();
        page = numbers.findByIdLessThanOrderByFloorOfSquareRootDesc(25L, pagination, order);

        assertEquals(Arrays.toString(new Long[]{
                        16L, // square root rounds down to 4; composite
                        13L, 11L, // square root rounds down to 3; prime
                        15L, 14L, 12L, 10L, 9L}), // square root rounds down to 3; composite
                     Arrays.toString(page.stream()
                                         .map(NaturalNumber::getId)
                                         .toArray()));

        assertTrue(page.hasNext());
        pagination = page.nextPageRequest();
        page = numbers.findByIdLessThanOrderByFloorOfSquareRootDesc(25L, pagination, order);

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

    @Assertion(id = "133", strategy = "Use count and exists methods where the primary entity class is inferred from the lifecycle methods.")
    public void testPrimaryEntityClassDeterminedByLifeCycleMethods() {
        assertEquals(4L, customRepo.countByIdIn(Set.of(2L, 15L, 37L, -5L, 60L)));

        assertTrue(customRepo.existsByIdIn(Set.of(17L, 14L, -1L)));

        assertFalse(customRepo.existsByIdIn(Set.of(-10L, -12L, -14L)));
    }

    @Assertion(id = "133", strategy = "Use a repository method that returns a single entity value where a single result is found.")
    public void testSingleEntity() {
        AsciiCharacter ch;
        try {
            ch = characters.findByHexadecimalIgnoreCase("2B");
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
            found = characters.findFirst3ByNumericValueGreaterThanEqualAndHexadecimalEndsWith(
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
            chars = characters.findByNumericValueLessThanEqualAndNumericValueGreaterThanEqual(
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

        List<AsciiCharacter> empty = characters.findByNumericValueLessThanEqualAndNumericValueGreaterThanEqual(115, 120);
        assertFalse(empty.iterator().hasNext());
        assertEquals(0L, empty.stream().count());
    }

    @Assertion(id = "133", strategy = "Use a repository method with the True keyword.")
    public void testTrue() {
        Iterable<NaturalNumber> odd;
        try {
            odd = positives.findByIsOddTrueAndIdLessThanEqualOrderByIdDesc(10L);
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
}
