/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.framework.utilities.DatabaseType;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import ee.jakarta.tck.data.framework.utilities.TestPropertyUtility;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.read.only.FruitPopulator;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Standalone
@AnyEntity
@DisplayName("the Jakarta Query Tests integration")
public class JakartaQueryTests {

    public static final Logger log = Logger.getLogger(JakartaQueryTests.class.getCanonicalName());

    protected final DatabaseType type = TestProperty.databaseType.getDatabaseType();

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(JakartaQueryTests.class,
                            Fruit.class, FruitRepository.class);
    }

    @Inject
    protected FruitRepository fruitRepository;

    protected List<Fruit> fruits = FruitPopulator.FRUITS;

    @BeforeEach
    public void setup() {
        var populator = new FruitPopulator();
        populator.populate(fruitRepository);
    }

    @Nested
    @DisplayName("When execute the select statement")
    class WhendSelectQuery {
    @DisplayName("should find all entities as stream")
    @Assertion(id = "1318",
            strategy = "Execute a repository query using the " +
                    "'FROM Fruit' clause that returns Stream<Fruit>, asserting that the stream yields all " +
                    "persisted Fruit instances.")
    void shouldFindAllEntities() {
        try {
            var result = fruitRepository.findAllQuery();

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .hasSize(fruits.size())
                    .containsAll(fruits);
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of find all elements.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should find all entities as stream")
    @Assertion(id = "1318",
            strategy = "Execute a repository query that orders " +
                    "results by the Fruit name attribute in ascending order, asserting that the returned list " +
                    "matches the natural ascending order of the persisted values.")
    void shouldOrderByAsc() {
        try {
            var result = fruitRepository.findAllAsc();

            var expectedColor = fruits.stream()
                    .map(Fruit::getName)
                    .sorted()
                    .toList();

            var names = result.stream()
                    .map(Fruit::getName)
                    .toList();

            Assertions.assertThat(expectedColor)
                    .isNotEmpty()
                    .hasSize(fruits.size())
                    .containsExactly(names.toArray(new String[0]));
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should order by descending")
    @Assertion(id = "1318",
            strategy = "Execute a repository query that orders " +
                    "results by the Fruit name attribute in descending order, asserting that the returned list " +
                    "matches the reverse natural order of the persisted values.")
    void shouldOrderByDesc() {
        try {
            var result = fruitRepository.findAllDesc();
            var names = result.stream()
                    .map(Fruit::getName)
                    .toList();

            var expectedNames = fruits.stream()
                    .map(Fruit::getName)
                    .sorted(Comparator.reverseOrder())
                    .toList();

            Assertions.assertThat(expectedNames)
                    .isNotEmpty()
                    .hasSize(fruits.size())
                    .containsExactly(names.toArray(new String[0]));
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should find all by projection")
    @Assertion(id = "1318",
            strategy = "Execute a repository query that returns " +
                    "a projection type, asserting that each result corresponds to a projection derived from the " +
                    "persisted Vehicle entities.")
    void shouldFindAllByProjection() {
        try {
            var result = fruitRepository.findAllWithProjection();

            var expected = fruits.stream()
                    .map(FruitSummary::of)
                    .toList();

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .hasSize(fruits.size())
                    .containsAll(expected);
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of find all elements.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test eq")
    @Assertion(id = "1318",
            strategy = "Execute an equality comparison on the name attribute, asserting that all " +
                    "returned entities have a name equal to the provided value, or accept UnsupportedOperationException if unsupported.")
    void shouldEq() {
        try {
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findNameEquals(sample.getName());

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .allMatch(fruit -> fruit.getName().equals(sample.getName()));
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable querying by attribute that is not a key.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test neq")
    @Assertion(id = "1318",
            strategy = "Execute a not-equal comparison on the name attribute, asserting that all " +
                    "returned entities have a different name, or accept UnsupportedOperationException if unsupported.")
    void shouldNEq() {
        try {
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findNameNotEquals(sample.getName());

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .allMatch(fruit -> !fruit.getName().equals(sample.getName()));
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable querying by attribute that is not a key.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test gt")
    @Assertion(id = "1318",
            strategy = "Execute a greater-than comparison on the quantity attribute, asserting that " +
                    "all returned entities have a quantity greater than the provided value, or accept UnsupportedOperationException.")
    void shouldGt() {
        try {
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findQuantityGt(sample.getQuantity());

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .allMatch(fruit -> fruit.getQuantity() > sample.getQuantity());
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Key-Value databases might not be capable of > and
                // Column databases might not be capable querying by attribute that is not a key.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test gte")
    @Assertion(id = "1318",
            strategy = "Execute a greater-than-or-equal comparison on the quantity attribute, " +
                    "asserting compliant results or accepting UnsupportedOperationException if unsupported.")
    void shouldGte() {
        try {
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findQuantityGte(sample.getQuantity());

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .allMatch(fruit -> fruit.getQuantity() >= sample.getQuantity());
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Key-Value databases might not be capable of >= and
                // Column databases might not be capable querying by attribute that is not a key.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test lt")
    @Assertion(id = "1318",
            strategy = "Execute a less-than comparison on the quantity attribute, asserting that " +
                    "all returned entities have a smaller quantity, or accept UnsupportedOperationException.")
    void shouldLt() {
        try {
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findQuantityLt(sample.getQuantity());

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .allMatch(fruit -> fruit.getQuantity() < sample.getQuantity());
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Key-Value databases might not be capable of < and
                // Column databases might not be capable querying by attribute that is not a key.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test lte")
    @Assertion(id = "1318",
            strategy = "Execute a less-than-or-equal comparison on the quantity attribute, " +
                    "asserting compliant results or accepting UnsupportedOperationException if unsupported.")
    void shouldLte() {
        try {
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findQuantityLte(sample.getQuantity());
            Assertions.assertThat(result)
                    .isNotEmpty()
                    .allMatch(fruit -> fruit.getQuantity() <= sample.getQuantity());
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Key-Value databases might not be capable of <= and
                // Column databases might not be capable querying by attribute that is not a key.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test in")
    @Assertion(id = "1318",
            strategy = "Execute an IN comparison on the name attribute with multiple values, " +
                    "asserting membership in the provided set or accepting UnsupportedOperationException.")
    void shouldIn() {
        try {
            var sample1 = fruits.getFirst();
            var sample2 = fruits.get(1);
            List<Fruit> result = fruitRepository.findNameIn(sample1.getName(), sample2.getName());

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .allMatch(fruit -> fruit.getName().equals(sample1.getName())
                            || fruit.getName().equals(sample2.getName()));

        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Key-Value databases might not be capable of in and
                // Column databases might not be capable querying by attribute that is not a key.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test in")
    @Assertion(id = "458",
            strategy = "Execute a query that performs an IN comparison of the name " +
                    "attribute against a Set of multiple values, asserting membership " +
                    "in the provided set or accepting UnsupportedOperationException.")
    void shouldInUsingParameterCollection() {
        try {
            var sample1 = fruits.getFirst();
            var sample2 = fruits.get(1);
            List<Fruit> result = fruitRepository.findNameIn(Set.of(sample1.getName(), sample2.getName()));

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .allMatch(fruit -> fruit.getName().equals(sample1.getName())
                            || fruit.getName().equals(sample2.getName()));

        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Key-Value databases might not be capable of in and
                // Column databases might not be capable querying by attribute that is not a key.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test AND")
    @Assertion(id = "1318",
            strategy = "Execute a query combining two predicates with AND (name equals and quantity equals), " +
                    "asserting every returned entity satisfies both predicates or accepting UnsupportedOperationException if unsupported.")
    void shouldAnd() {

        try {
            fruitRepository.saveAll(fruits);
            Fruit sample = fruits.getFirst();

            List<Fruit> result = fruitRepository.findNameEqualsAndQuantityEquals(sample.getName(), sample.getQuantity());

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .allMatch(fruit -> fruit.getName().equals(sample.getName())
                            && fruit.getQuantity().equals(sample.getQuantity()));
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of AND.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test OR")
    @Assertion(id = "1318",
            strategy = "Execute a query combining predicates with OR (name equals either value), " +
                    "asserting every returned entity matches at least one predicate or accepting UnsupportedOperationException.")
    void shouldOr() {

        try {
            Fruit sample1 = fruits.get(0);
            Fruit sample2 = fruits.get(1);
            List<Fruit> result = fruitRepository.findNameEqualsORNameEquals(sample1.getName(), sample2.getName());

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .allMatch(fruit -> fruit.getName().equals(sample1.getName())
                            || fruit.getName().equals(sample2.getName()));

        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of OR.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test count")
    @Assertion(id = "458",
            strategy = "Execute a query that counts all entities in the database, " +
                    "asserting the count matches the total number of entities " +
                    "inserted or accepting UnsupportedOperationException.")
    void shouldCount() {

        try {

            long result = fruitRepository.countAll();

            Assertions.assertThat(result).isEqualTo(fruits.size());

        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of count.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should count all Fruits using id function")
    @Assertion(id = "1318",
            strategy = "Execute a query that performs an equality comparison on the " +
                    "id attribute using the id function. Because it is an id attribute, all " +
                    "databases should support it")
    void shouldFindByIdUsingIdFunction() {
        var fruit = fruits.getFirst();
        var result = fruitRepository.findByIdUsingIdFunction(fruit.getId());

        Assertions.assertThat(result).isNotEmpty().get().isEqualTo(fruit);
    }

    @DisplayName("should return only name attribute order by quantity")
    @Assertion(id = "458",
            strategy = "Execute the query returning only the name attribute order by quantity")
    void shouldReturnName() {

        try {
            var names = fruits.stream()
                    .sorted(Comparator.comparingLong(Fruit::getQuantity))
                    .map(Fruit::getName)
                    .toArray(String[]::new);
            var result = fruitRepository.findAllOnlyNameOrderByQuantity();

            Assertions.assertThat(result).isNotEmpty().containsExactly(names);
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should return only name and quantity attributes order by quantity")
    @Assertion(id = "458",
            strategy = "Execute the query returning only the name and quantity attributes order by quantity")
    void shouldReturnNameAndQuantity() {

        try {
            var tuples = fruits.stream()
                    .sorted(Comparator.comparing(Fruit::getName))
                    .map(f -> new FruitTuple(f.getName(), f.getQuantity()))
                    .toArray(FruitTuple[]::new);
            var result = fruitRepository.findAllNameAndQuantityOrderByQuantity();

            Assertions.assertThat(result.stream().map(FruitTuple::of))
                    .isNotEmpty().containsExactly(tuples);
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should return id using id function order by id")
    @Assertion(id = "458",
            strategy = "Execute the query returning only the id attribute order by id")
    void shouldReturnIdUsingIdFunctionOrderById() {

        try {

            var fruit = fruits.getFirst();
            var result = fruitRepository.findByIdUsingIdFunctionOrderById(fruit.getId());

            Assertions.assertThat(result).get().isEqualTo(fruit.getId());
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should return empty result when condition does not match any entity")
    @Assertion(id = "458",
            strategy = "Execute a SELECT query with a valid predicate that does not match any persisted entity, " +
                    "asserting that the query executes successfully and returns an empty result.")
    void shouldReturnEmptyWhenConditionDoesNotMatch() {

        try {
            var result = fruitRepository.findByName("non-existing-fruit");

            Assertions.assertThat(result)
                    .isNotNull()
                    .isEmpty();

        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable querying by non-key attributes.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should apply where condition and order by together")
    @Assertion(id = "458",
            strategy = "Execute a SELECT query combining a WHERE predicate and ORDER BY clause, " +
                    "asserting that the result is filtered by the predicate and ordered correctly.")
    void shouldFilterAndOrder() {

        try {
            var threshold = fruits.getFirst().getQuantity();

            var expected = fruits.stream()
                    .filter(f -> f.getQuantity() > threshold)
                    .map(Fruit::getName)
                    .sorted()
                    .toList();

            var result = fruitRepository
                    .findByQuantityGreaterThanOrderByNameAsc(threshold);

            var names = result.stream()
                    .map(Fruit::getName)
                    .toList();

            Assertions.assertThat(names)
                    .isNotNull()
                    .containsExactlyElementsOf(expected);

        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of filtering + sorting on non-key attributes.
            } else {
                throw exp;
            }
        }
    }
    }

    @Nested
    @DisplayName("When execute delete query")
    class WhenDeleteQuery {


        @AfterEach
        public void cleanup() {
            fruitRepository.deleteAll();
            TestPropertyUtility.waitForEventualConsistency();
        }

        @DisplayName("should delete all entities")
        @Assertion(id = "1318",
                strategy = "Execute the delete all queries, wait for eventual consistency and verify if all entities are deleted")
        void shouldDeleteAllEntities() {

            try {
                fruitRepository.deleteAll();
                TestPropertyUtility.waitForEventualConsistency();

                Assertions.assertThat(fruitRepository.findAll().toList()).isEmpty();
            } catch (UnsupportedOperationException exp) {
                if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                    // Column and Key-Value databases might not be capable of delete all the entities
                } else {
                    throw exp;
                }
            }
        }

        @DisplayName("should delete entities using equals condition")
        @Assertion(id = "1318",
                strategy = "delete by name equals, verify if entity is deleted")
        void shouldDeleteEq() {
            try {
                Fruit fruit = fruits.getFirst();
                fruitRepository.deleteByName(fruit.getName());
                TestPropertyUtility.waitForEventualConsistency();

                List<Fruit> result = fruitRepository.findAll().toList();
                Assertions.assertThat(result)
                        .allMatch(f -> !fruit.getName().equals(f.getName()));
            } catch (UnsupportedOperationException exp) {
                if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                    // Column and Key-Value databases might not be capable deleting by attribute that is not a key.
                } else {
                    throw exp;
                }
            }
        }

        @DisplayName("should delete not equals condition")
        @Assertion(id = "1318",
                strategy = "delete by name not equals, verify if entity is deleted")
        void shouldDeleteNeq() {
            try {
                Fruit fruit = fruits.getFirst();
                fruitRepository.deleteByNotEqualsName(fruit.getName());
                TestPropertyUtility.waitForEventualConsistency();

                List<Fruit> result = fruitRepository.findAll().toList();
                Assertions.assertThat(result)
                        .allMatch(f -> fruit.getName().equals(f.getName()));
            } catch (UnsupportedOperationException exp) {
                if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                    // Column and Key-Value databases might not be capable deleting by attribute that is not a key.
                } else {
                    throw exp;
                }
            }
        }

        @DisplayName("should delete greater than condition")
        @Assertion(id = "1318",
                strategy = "delete by quantity greater than, verify if entity is deleted")
        void shouldGt() {
            try {
                Fruit fruit = fruits.getFirst();
                fruitRepository.deleteQuantityGreaterThan(fruit.getQuantity());
                TestPropertyUtility.waitForEventualConsistency();

                List<Fruit> result = fruitRepository.findAll().toList();
                Assertions.assertThat(result)
                        .allMatch(f -> f.getQuantity() <= fruit.getQuantity());
            } catch (UnsupportedOperationException exp) {
                if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                    // Column and Key-Value databases might not be capable deleting by attribute that is not a key.
                } else {
                    throw exp;
                }
            }
        }

        @DisplayName("should delete greater than equals condition")
        @Assertion(id = "1318",
                strategy = "delete by quantity greater than equals, verify if entity is deleted")
        void shouldGte() {
            try {
                Fruit fruit = fruits.getFirst();
                fruitRepository.deleteQuantityGreaterThanEquals(fruit.getQuantity());
                TestPropertyUtility.waitForEventualConsistency();

                List<Fruit> result = fruitRepository.findAll().toList();
                Assertions.assertThat(result)
                        .allMatch(f -> f.getQuantity() < fruit.getQuantity());
            } catch (UnsupportedOperationException exp) {
                if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                    // Column and Key-Value databases might not be capable deleting by attribute that is not a key.
                } else {
                    throw exp;
                }
            }
        }

        @DisplayName("should delete lesser condition")
        @Assertion(id = "1318",
                strategy = "delete by quantity lesser, verify if entity is deleted")
        void shouldLt() {
            try {
                Fruit fruit = fruits.getFirst();
                fruitRepository.deleteLesserThan(fruit.getQuantity());
                TestPropertyUtility.waitForEventualConsistency();

                List<Fruit> result = fruitRepository.findAll().toList();
                Assertions.assertThat(result)
                        .allMatch(f -> f.getQuantity() >= fruit.getQuantity());
            } catch (UnsupportedOperationException exp) {
                if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                    // Column and Key-Value databases might not be capable deleting by attribute that is not a key.
                } else {
                    throw exp;
                }
            }
        }

        @DisplayName("should delete lesser than equals condition")
        @Assertion(id = "1318",
                strategy = "delete by quantity lesser than equals, verify if entity is deleted")
        void shouldLte() {
            try {
                Fruit fruit = fruits.getFirst();
                fruitRepository.deleteQuantityLesserThanEquals(fruit.getQuantity());
                TestPropertyUtility.waitForEventualConsistency();

                List<Fruit> result = fruitRepository.findAll().toList();
                Assertions.assertThat(result)
                        .allMatch(f -> f.getQuantity() > fruit.getQuantity());
            } catch (UnsupportedOperationException exp) {
                if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                    // Column and Key-Value databases might not be capable deleting by attribute that is not a key.
                } else {
                    throw exp;
                }
            }
        }

        @DisplayName("should delete using IN condition")
        @Assertion(id = "1318",
                strategy = "delete by name in, verify if entity is deleted")
        void shouldIn() {
            try {
                Fruit fruit = fruits.getFirst();
                fruitRepository.deleteByNameIn(List.of(fruit.getName(), fruits.get(1).getName()));
                TestPropertyUtility.waitForEventualConsistency();

                List<Fruit> result = fruitRepository.findAll().toList();
                Assertions.assertThat(result)
                        .allMatch(f -> !f.getName().equals(fruit.getName())
                                ||
                                !f.getName().equals(fruits.get(1).getName()));
            } catch (UnsupportedOperationException exp) {
                if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                    // Column and Key-Value databases might not be capable deleting by attribute that is not a key.
                } else {
                    throw exp;
                }
            }
        }
    }

    private record FruitTuple(String name, Object quantity) {
        static FruitTuple of(Object[] values) {
            return new FruitTuple((String) values[0], values[1]);
        }
    }

}