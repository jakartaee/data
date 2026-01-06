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

import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

@Standalone
@AnyEntity
@ReadOnlyTest
public class JakartaQueryTests {

    public static final Logger log = Logger.getLogger(EntityTests.class.getCanonicalName());

    private final DatabaseType type = TestProperty.databaseType.getDatabaseType();

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(JakartaQueryTests.class, Vehicle.class, VehicleRepository.class);
    }

    @Inject
    protected VehicleRepository vehicleRepository;

    @Inject
    protected FruitRepository fruitRepository;


    @BeforeEach
    public void setup() {
        vehicleRepository.deleteAll();
        fruitRepository.deleteAll();
    }

    @DisplayName("should find all entities as stream")
    @ParameterizedTest
    @ArgumentsSource(VehicleListSupplier.class)
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
    @ArgumentsSource(VehicleListSupplier.class)
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
    @ArgumentsSource(VehicleListSupplier.class)
    @ArgumentsSource(VehicleListSupplier.class)
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
    @ArgumentsSource(VehicleListSupplier.class)
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

    @ParameterizedTest
    @DisplayName("should test eq")
    @ParametizedAssertion(id = "404",
            strategy = "Persist Fruit entities and execute an equality comparison on the name attribute, asserting that all " +
                    "returned entities have a name equal to the provided value, or accept UnsupportedOperationException if unsupported.")
    @ArgumentsSource(FruitListSupplier.class)
    void shouldEq(List<Fruit> fruits) {
        try {
            fruitRepository.saveAll(fruits);
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findNameEquals(sample.getName());

            AssertionsForInterfaceTypes.assertThat(result)
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

    @ParameterizedTest
    @DisplayName("should test neq")
    @ArgumentsSource(FruitListSupplier.class)
    @ParametizedAssertion(id = "405",
            strategy = "Persist Fruit entities and execute a not-equal comparison on the name attribute, asserting that all " +
                    "returned entities have a different name, or accept UnsupportedOperationException if unsupported.")
    void shouldNEq(List<Fruit> fruits) {
        try {
            fruitRepository.saveAll(fruits);
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findNameNotEquals(sample.getName());

            AssertionsForInterfaceTypes.assertThat(result)
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

    @ParameterizedTest
    @DisplayName("should test gt")
    @ParametizedAssertion(id = "406",
            strategy = "Persist Fruit entities and execute a greater-than comparison on the quantity attribute, asserting that " +
                    "all returned entities have a quantity greater than the provided value, or accept UnsupportedOperationException.")
    @ArgumentsSource(FruitListSupplier.class)
    void shouldGt(List<Fruit> fruits) {
        try {
            fruitRepository.saveAll(fruits);
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findQuantityGt(sample.getQuantity());

            AssertionsForInterfaceTypes.assertThat(result)
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

    @ParameterizedTest
    @DisplayName("should test gte")
    @ParametizedAssertion(id = "407",
            strategy = "Persist Fruit entities and execute a greater-than-or-equal comparison on the quantity attribute, " +
                    "asserting compliant results or accepting UnsupportedOperationException if unsupported.")
    @ArgumentsSource(FruitListSupplier.class)
    void shouldGte(List<Fruit> fruits) {
        try {
            fruitRepository.saveAll(fruits);
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findQuantityGte(sample.getQuantity());

            AssertionsForInterfaceTypes.assertThat(result)
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

    @ParameterizedTest
    @DisplayName("should test lt")
    @ParametizedAssertion(id = "408",
            strategy = "Persist Fruit entities and execute a less-than comparison on the quantity attribute, asserting that " +
                    "all returned entities have a smaller quantity, or accept UnsupportedOperationException.")
    @ArgumentsSource(FruitListSupplier.class)
    void shouldLt(List<Fruit> fruits) {
        try {
            fruitRepository.saveAll(fruits);
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findQuantityLt(sample.getQuantity());

            AssertionsForInterfaceTypes.assertThat(result)
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

    @ParameterizedTest
    @DisplayName("should test lte")
    @ArgumentsSource(FruitListSupplier.class)
    @ParametizedAssertion(id = "409",
            strategy = "Persist Fruit entities and execute a less-than-or-equal comparison on the quantity attribute, " +
                    "asserting compliant results or accepting UnsupportedOperationException if unsupported.")
    void shouldLte(List<Fruit> fruits) {
        try {
            fruitRepository.saveAll(fruits);
            Fruit sample = fruits.getFirst();
            List<Fruit> result = fruitRepository.findQuantityLte(sample.getQuantity());
            AssertionsForInterfaceTypes.assertThat(result)
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

    @ParameterizedTest
    @DisplayName("should test in")
    @ParametizedAssertion(id = "410",
            strategy = "Persist Fruit entities and execute an IN comparison on the name attribute with multiple values, " +
                    "asserting membership in the provided set or accepting UnsupportedOperationException.")
    @ArgumentsSource(FruitListSupplier.class)
    void shouldIn(List<Fruit> fruits) {
        try {
            fruitRepository.saveAll(fruits);
            var sample1 = fruits.getFirst();
            var sample2 = fruits.get(1);
            List<Fruit> result = fruitRepository.findNameIn(sample1.getName(), sample2.getName());

            AssertionsForInterfaceTypes.assertThat(result)
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