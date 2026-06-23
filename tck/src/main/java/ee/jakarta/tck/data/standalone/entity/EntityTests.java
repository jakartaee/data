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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.data.page.CursoredPage;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;

import ee.jakarta.tck.data.framework.junit.anno.Annotated;
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
@Annotated
public class EntityTests {

    public static final Logger log = Logger.getLogger(EntityTests.class.getCanonicalName());

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(EntityTests.class,
                            Box.class,
                            Boxes.class,
                            Coordinate.class,
                            MultipleEntityRepo.class);
    }

    @Inject
    Boxes boxes;

    @Inject
    NaturalNumbers numbers;

    @Inject
    PositiveIntegers positives; // shares same read-only data with NaturalNumbers

    @Inject
    AsciiCharacters characters;

    @Inject
    MultipleEntityRepo shared;

    @BeforeEach
    //Inject doesn't happen until after BeforeClass so this is necessary before each test
    public void setup() {
        NaturalNumbersPopulator.get().populate(numbers);  // Uses annotation-based methods
        AsciiCharactersPopulator.get().populate(characters);  // Uses annotation-based methods
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
            assertEquals(127L, characters.countNonNullHex());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfNotNull()) {
                throw x;
            } else {
                return;
            }
        }
        assertEquals('0', characters.find('0').getThisCharacter());
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
            if (type.capableOfQueryWithoutWhere() &&
                type.capableOfSingleSort()) {
                throw x;
            }
        }
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
            if (type.capableOfMultipleSort() &&
                type.capableOfQueryWithoutWhere()) {
                throw x;
            } else {
                return;
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
            if (type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
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
                            .collect(Collectors.toList()));
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

        assertTrue(page1.hasNext());

        Page<Long> page2 = numbers.oddsFrom21To(40L, page1.nextPageRequest());

        assertEquals(List.of(31L, 33L, 35L, 37L, 39L), page2.content());

        if (page2.hasNext()) {
            Page<Long> page3 = numbers.oddsFrom21To(40L, page2.nextPageRequest());
            assertFalse(page3.hasContent());
            assertFalse(page3.hasNext());
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
            page1 = positives.withBitCountOrOfTypeAndBelow((short) 4,
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
                        .collect(Collectors.toList()));

        assertTrue(page1.hasTotals());
        assertTrue(page1.hasNext());
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
            page2 = positives.withBitCountOrOfTypeAndBelow((short) 4,
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
            if (type.capableOfAttributeVsAttributeComparison() &&
                type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
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
            if (type.capableOfCount()) {
                throw x;
            } else {
                return;
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
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
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
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfMultipleSort()) {
                throw x;
            } else {
                return;
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

        Stream<CardinalNumber> stream = characters.cardinalNumberStream(2L);

        assertEquals(List.of("4 COMPOSITE (3 bits)",
                             "5 PRIME (3 bits)",
                             "6 COMPOSITE (3 bits)",
                             "7 PRIME (3 bits)",
                             "8 COMPOSITE (4 bits)"),
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

        Long[] found = positives.requiringBits((short) 4);

        assertEquals(List.of(8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L),
                Arrays.stream(found)
                        .sorted()
                        .collect(Collectors.toList()));

        Long[] notFound = positives.requiringBits((short) 0);

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
        if (type.capableOfSingleSort()) {
            order = Order.by(_NaturalNumber.id.desc());
        } else {
            order = Order.by();
        }

        WholeNumber[] found = numbers.wholeNumbers(2, order);

        // 5 numbers (4, 5, 6, 7, 8) have square roots that round down to 2
        assertEquals(5, found.length);

        Map<Long, Integer> valueToArrayIndex = new HashMap<>();
        if (type.capableOfSingleSort() ) {
            // Sorted by id/value descending
            valueToArrayIndex.put(8L, 0);
            valueToArrayIndex.put(7L, 1);
            valueToArrayIndex.put(6L, 2);
            valueToArrayIndex.put(5L, 3);
            valueToArrayIndex.put(4L, 4);
        } else {
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
        if (type.capableOfMultipleSort()) {
            order = Order.by(_NaturalNumber.floorOfSquareRoot.asc(),
                             _NaturalNumber.id.desc());
        } else {
            order = Order.by();
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

        WholeNumber num3 = type.capableOfMultipleSort()
                ? found.get(0)
                : primes.get(3L);
        assertNotNull(num3);
        assertEquals(1L, num3.sqrtFloor());

        WholeNumber num17 = type.capableOfMultipleSort()
                ? found.get(8)
                : primes.get(17L);
        assertNotNull(num17);
        assertEquals(4L, num17.sqrtFloor());

        WholeNumber num59 = type.capableOfMultipleSort()
                ? found.get(16)
                : primes.get(59L);
        assertNotNull(num59);
        assertEquals(7L, num59.sqrtFloor());

        WholeNumber num83 = type.capableOfMultipleSort()
                ? found.get(24)
                : primes.get(83L);
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
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfCount() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
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

    @Assertion(id = "458", strategy = """
            Use a repository method with a JCQL UPDATE query without a WHERE clause.
            This method also tests the addition, subtraction, and multiplication operators.
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
            Use a repository method with a JCQL UPDATE query with a WHERE clause.
            This method also tests the assignment and division operators.
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

        assertFalse(shared.withUUID(id1).isPresent());
        assertFalse(shared.withUUID(id2).isPresent());
    }

}
