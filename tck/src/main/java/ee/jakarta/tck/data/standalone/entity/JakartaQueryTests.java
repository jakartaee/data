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
import org.junit.jupiter.params.provider.ArgumentsSource;
import ee.jakarta.tck.data.framework.junit.anno.ParametizedAssertion;
import org.junit.jupiter.params.ParameterizedTest;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;

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
                            Vehicle.class, VehicleRepository.class,
                            Fruit.class, FruitRepository.class);
    }

    @Inject
    protected VehicleRepository vehicleRepository;

    @Inject
    protected FruitRepository fruitRepository;

    protected List<Fruit> fruits = FruitPopulator.FRUITS;

    @BeforeEach
    public void setup() {
        var populator = new FruitPopulator();
        populator.populate(fruitRepository);
    }

    @DisplayName("should find all entities as stream")
    @ParameterizedTest
    @ArgumentsSource(VehicleSupplier.class)
    @ParametizedAssertion(id = "400",
            strategy = "Persist a known collection of Vehicle entities and execute a repository query using the " +
                    "'FROM Vehicle' clause that returns Stream<Vehicle>, asserting that the stream yields all " +
                    "persisted Vehicle instances.")
    void shouldFindAllEntities(List<Vehicle> vehicles) {
        try {
            vehicleRepository.saveAll(vehicles);
            var result = vehicleRepository.findAllQuery();

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .hasSize(vehicles.size())
                    .containsAll(vehicles);
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                log.warning("database does not support keyword 'FROM' type: " + type);
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should find all entities as stream")
    @ParameterizedTest
    @ArgumentsSource(VehicleSupplier.class)
    @ParametizedAssertion(id = "401",
            strategy = "Persist a known collection of Vehicle entities and execute a repository query that orders " +
                    "results by the Vehicle color attribute in ascending order, asserting that the returned list " +
                    "matches the natural ascending order of the persisted values.")
    void shouldOrderByAsc(List<Vehicle> vehicles) {
        try {
            vehicleRepository.saveAll(vehicles);
            List<Vehicle> result = vehicleRepository.findAllAsc();

            var expectedColor = vehicles.stream()
                    .map(Vehicle::getColor)
                    .sorted()
                    .toList();

            var colors = result.stream()
                    .map(Vehicle::getColor)
                    .toList();

            Assertions.assertThat(expectedColor)
                    .isNotEmpty()
                    .hasSize(vehicles.size())
                    .containsExactly(colors.toArray(new String[0]));
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                log.warning("database does not support keyword 'FROM' type: " + type);
            } else {
                throw exp;
            }
        }
    }

    @ParameterizedTest
    @DisplayName("should order by descending")
    @ArgumentsSource(VehicleSupplier.class)
    @ParametizedAssertion(id = "402",
            strategy = "Persist a known collection of Vehicle entities and execute a repository query that orders " +
                    "results by the Vehicle color attribute in descending order, asserting that the returned list " +
                    "matches the reverse natural order of the persisted values.")
    void shouldOrderByDesc(List<Vehicle> vehicles) {
        try {
            vehicleRepository.saveAll(vehicles);
            List<Vehicle> result = vehicleRepository.findAllDesc();
            var colors = result.stream()
                    .map(Vehicle::getColor)
                    .toList();

            var expectedColor = vehicles.stream()
                    .map(Vehicle::getColor)
                    .sorted(Comparator.reverseOrder())
                    .toList();

            Assertions.assertThat(expectedColor)
                    .isNotEmpty()
                    .hasSize(vehicles.size())
                    .containsExactly(colors.toArray(new String[0]));
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                log.warning("database does not support keyword 'FROM' type: " + type);
            } else {
                throw exp;
            }
        }
    }

    @ParameterizedTest
    @DisplayName("should find all by projection")
    @ArgumentsSource(VehicleSupplier.class)
    @ParametizedAssertion(id = "403",
            strategy = "Persist a known collection of Vehicle entities and execute a repository query that returns " +
                    "a projection type, asserting that each result corresponds to a projection derived from the " +
                    "persisted Vehicle entities.")
    void shouldFindAllByProjection(List<Vehicle> vehicles) {
        try {
            vehicleRepository.saveAll(vehicles);
            var result = vehicleRepository.findAllWithProjection();

            var expected = vehicles.stream()
                    .map(VehicleSummary::of)
                    .toList();

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .hasSize(vehicles.size())
                    .containsAll(expected);
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                log.warning("database does not support keyword 'FROM' type: " + type);
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test eq")
    @Assertion(id = "404",
            strategy = "Persist Fruit entities and execute an equality comparison on the name attribute, asserting that all " +
                    "returned entities have a name equal to the provided value, or accept UnsupportedOperationException if unsupported.")
    void shouldEq() {
        try {
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findNameEquals(sample.getName());

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .allMatch(fruit -> fruit.getName().equals(sample.getName()));
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                log.warning("database does not support keyword 'FROM' type: " + type);
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test neq")
    @Assertion(id = "405",
            strategy = "Persist Fruit entities and execute a not-equal comparison on the name attribute, asserting that all " +
                    "returned entities have a different name, or accept UnsupportedOperationException if unsupported.")
    void shouldNEq() {
        try {
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findNameNotEquals(sample.getName());

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .allMatch(fruit -> !fruit.getName().equals(sample.getName()));
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                log.warning("database does not support keyword 'FROM' type: " + type);
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test gt")
    @Assertion(id = "406",
            strategy = "Persist Fruit entities and execute a greater-than comparison on the quantity attribute, asserting that " +
                    "all returned entities have a quantity greater than the provided value, or accept UnsupportedOperationException.")
    void shouldGt() {
        try {
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findQuantityGt(sample.getQuantity());

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .allMatch(fruit -> fruit.getQuantity() > sample.getQuantity());
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                log.warning("database does not support keyword 'FROM' type: " + type);
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test gte")
    @Assertion(id = "407",
            strategy = "Persist Fruit entities and execute a greater-than-or-equal comparison on the quantity attribute, " +
                    "asserting compliant results or accepting UnsupportedOperationException if unsupported.")
    void shouldGte() {
        try {
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findQuantityGte(sample.getQuantity());

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .allMatch(fruit -> fruit.getQuantity() >= sample.getQuantity());
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                log.warning("database does not support keyword 'FROM' type: " + type);
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test lt")
    @Assertion(id = "408",
            strategy = "Persist Fruit entities and execute a less-than comparison on the quantity attribute, asserting that " +
                    "all returned entities have a smaller quantity, or accept UnsupportedOperationException.")
    void shouldLt() {
        try {
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findQuantityLt(sample.getQuantity());

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .allMatch(fruit -> fruit.getQuantity() < sample.getQuantity());
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                log.warning("database does not support keyword 'FROM' type: " + type);
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test lte")
    @Assertion(id = "409",
            strategy = "Persist Fruit entities and execute a less-than-or-equal comparison on the quantity attribute, " +
                    "asserting compliant results or accepting UnsupportedOperationException if unsupported.")
    void shouldLte() {
        try {
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findQuantityLte(sample.getQuantity());
            Assertions.assertThat(result)
                    .isNotEmpty()
                    .allMatch(fruit -> fruit.getQuantity() <= sample.getQuantity());
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                log.warning("database does not support keyword 'FROM' type: " + type);
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test in")
    @Assertion(id = "410",
            strategy = "Persist Fruit entities and execute an IN comparison on the name attribute with multiple values, " +
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                log.warning("database does not support keyword 'FROM' type: " + type);
            } else {
                throw exp;
            }
        }
    }


    @DisplayName("should test AND")
    @Assertion(id = "411",
            strategy = "Persist Fruit entities and execute a query combining two predicates with AND (name equals and quantity equals), " +
                    "asserting every returned entity satisfies both predicates or accepting UnsupportedOperationException if unsupported.")
    void shouldAnd() {

        try {
            fruitRepository.saveAll(fruits);
            Fruit sample = fruits.getFirst();

            List<Fruit> result = fruitRepository.findNameEqualsAndQuantitEquals(sample.getName(), sample.getQuantity());

            Assertions.assertThat(result)
                    .isNotEmpty()
                    .allMatch(fruit -> fruit.getName().equals(sample.getName())
                            && fruit.getQuantity().equals(sample.getQuantity()));
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                log.warning("database does not support keyword 'FROM' type: " + type);
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test OR")
    @Assertion(id = "412",
            strategy = "Persist Fruit entities and execute a query combining predicates with OR (name equals either value), " +
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
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                log.warning("database does not support keyword 'FROM' type: " + type);
            } else {
                throw exp;
            }
        }
    }


}