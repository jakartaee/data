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
import ee.jakarta.tck.data.framework.junit.anno.ReadOnlyTest;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.framework.utilities.DatabaseType;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.read.only.FruitPopulator;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

@Standalone
@AnyEntity
@ReadOnlyTest
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // Column and Key-Value databases might not be capable querying by attribute that is not a key.
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // Column and Key-Value databases might not be capable querying by attribute that is not a key.
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // Column and Key-Value databases might not be capable querying by attribute that is not a key.
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // Column and Key-Value databases might not be capable querying by attribute that is not a key.
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // Column and Key-Value databases might not be capable querying by attribute that is not a key.
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // Column and Key-Value databases might not be capable querying by attribute that is not a key.
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // Column and Key-Value databases might not be capable querying by attribute that is not a key.
            } else {
                throw exp;
            }
        }
    }
}