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
import ee.jakarta.tck.data.framework.junit.anno.Assertion;

import java.util.List;
import java.util.logging.Logger;

@Standalone
@AnyEntity
@DisplayName("Jakarta Data integration with Jakarta Common Query Language for delete operations")
public class JakartaQueryDeleteTests {

    public static final Logger log =
            Logger.getLogger(JakartaQueryDeleteTests.class.getCanonicalName());

    protected final DatabaseType type = TestProperty.databaseType.getDatabaseType();

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(JakartaQueryDeleteTests.class,
                            Vegetable.class,
                            VegetableRepository.class);
    }

    @Inject
    protected VegetableRepository vegetableRepository;

    protected List<Vegetable> vegetables = VegetableRepository.VEGGIES;

    @BeforeEach
    public void setup() {
        vegetableRepository.saveAll(vegetables);
    }

    @AfterEach
    public void cleanup() {
        vegetableRepository.deleteAll();
        TestPropertyUtility.waitForEventualConsistency();
    }

    @DisplayName("should delete all entities")
    @Assertion(id = "458",
            strategy = "Execute a deleteAll query, wait for eventual consistency and verify if all entities are deleted")
    void shouldDeleteAllEntities() {

        try {
            vegetableRepository.deleteAll();
            TestPropertyUtility.waitForEventualConsistency();

            Assertions.assertThat(vegetableRepository.findAll().toList()).isEmpty();
        } catch (UnsupportedOperationException exp) {
            if (type.capableOfQueryWithoutWhere()) {
                throw exp;
            }
        }
    }

    @DisplayName("should delete entities using equals condition")
    @Assertion(id = "458",
            strategy = "delete by name equals, verify if entity is deleted")
    void shouldDeleteEq() {
        try {
            Vegetable vegetable = vegetables.get(0);
            vegetableRepository.deleteByName(vegetable.getName());
            TestPropertyUtility.waitForEventualConsistency();

            List<Vegetable> result = vegetableRepository.findNameEquals(vegetable.getName());
            Assertions.assertThat(result)
                    .isEmpty();
        } catch (UnsupportedOperationException exp) {
            if (type.capableOfConstraintsOnNonIdAttributes()) {
                throw exp;
            }
        }
    }

    @DisplayName("should delete not equals condition")
    @Assertion(id = "458",
            strategy = "delete by name not equals, verify if entity is deleted")
    void shouldDeleteNeq() {
        try {
            Vegetable vegetable = vegetables.get(0);
            vegetableRepository.deleteByNotEqualsName(vegetable.getName());
            TestPropertyUtility.waitForEventualConsistency();

            List<Vegetable> result1 = vegetableRepository.findNameNotEquals(vegetable.getName());
            Assertions.assertThat(result1)
                    .isEmpty();
            List<Vegetable> result2 = vegetableRepository.findNameEquals(vegetable.getName());
            Assertions.assertThat(result2)
                    .isNotEmpty();
        } catch (UnsupportedOperationException exp) {
            if (type.capableOfConstraintsOnNonIdAttributes()) {
                throw exp;
            }
        }
    }

    @DisplayName("should delete greater than condition")
    @Assertion(id = "458",
            strategy = "delete by quantity greater than, verify if entity is deleted")
    void shouldGt() {
        try {
            Vegetable vegetable = vegetables.get(0);
            vegetableRepository.deleteQuantityGreaterThan(vegetable.getQuantity());
            TestPropertyUtility.waitForEventualConsistency();

            List<Vegetable> result1 = vegetableRepository.findQuantityGt(vegetable.getQuantity());
            Assertions.assertThat(result1)
                    .isEmpty();
            List<Vegetable> result2 = vegetableRepository.findQuantityLte(vegetable.getQuantity());
            Assertions.assertThat(result2)
                    .isNotEmpty();
        } catch (UnsupportedOperationException exp) {
            if (type.capableOfConstraintsOnNonIdAttributes()) {
                throw exp;
            }
        }
    }

    @DisplayName("should delete greater than equals condition")
    @Assertion(id = "458",
            strategy = "delete by quantity greater than equals, verify if entity is deleted")
    void shouldGte() {
        try {
            Vegetable vegetable = vegetables.get(0);
            vegetableRepository.deleteQuantityGreaterThanEquals(vegetable.getQuantity());
            TestPropertyUtility.waitForEventualConsistency();

            List<Vegetable> result1 = vegetableRepository.findQuantityGte(vegetable.getQuantity());
            Assertions.assertThat(result1)
                    .isEmpty();
            List<Vegetable> result2 = vegetableRepository.findQuantityLt(vegetable.getQuantity());
            Assertions.assertThat(result2)
                    .isNotEmpty();
        } catch (UnsupportedOperationException exp) {
            if (type.capableOfConstraintsOnNonIdAttributes()) {
                throw exp;
            }
        }
    }

    @DisplayName("should delete lesser condition")
    @Assertion(id = "458",
            strategy = "delete by quantity lesser, verify if entity is deleted")
    void shouldLt() {
        try {
            Vegetable vegetable = vegetables.get(0);
            vegetableRepository.deleteLesserThan(vegetable.getQuantity());
            TestPropertyUtility.waitForEventualConsistency();

            List<Vegetable> result1 = vegetableRepository.findQuantityLt(vegetable.getQuantity());
            Assertions.assertThat(result1)
                    .isEmpty();
            List<Vegetable> result2 = vegetableRepository.findQuantityGte(vegetable.getQuantity());
            Assertions.assertThat(result2)
                    .isNotEmpty();
        } catch (UnsupportedOperationException exp) {
            if (type.capableOfConstraintsOnNonIdAttributes()) {
                throw exp;
            }
        }
    }

    @DisplayName("should delete lesser than equals condition")
    @Assertion(id = "458",
            strategy = "delete by quantity lesser than equals, verify if entity is deleted")
    void shouldLte() {
        try {
            Vegetable vegetable = vegetables.get(0);
            vegetableRepository.deleteQuantityLesserThanEquals(vegetable.getQuantity());
            TestPropertyUtility.waitForEventualConsistency();

            List<Vegetable> result1 = vegetableRepository.findQuantityLte(vegetable.getQuantity());
            Assertions.assertThat(result1)
                    .isEmpty();
            List<Vegetable> result2 = vegetableRepository.findQuantityGt(vegetable.getQuantity());
            Assertions.assertThat(result2)
                    .isNotEmpty();
        } catch (UnsupportedOperationException exp) {
            if (type.capableOfConstraintsOnNonIdAttributes()) {
                throw exp;
            }
        }
    }

    @DisplayName("should delete using IN condition")
    @Assertion(id = "458",
            strategy = "delete by name in, verify if entity is deleted")
    void shouldIn() {
        try {
            Vegetable vegetable = vegetables.get(0);
            vegetableRepository.deleteByNameIn(List.of(vegetable.getName(), vegetables.get(1).getName()));
            TestPropertyUtility.waitForEventualConsistency();

            List<Vegetable> result1 = vegetableRepository.findNameNotEquals(vegetable.getName());
            Assertions.assertThat(result1).isNotEmpty();
            List<Vegetable> result2 = vegetableRepository.findNameEquals(vegetables.get(1).getName());
            Assertions.assertThat(result2)
                    .isEmpty();
        } catch (UnsupportedOperationException exp) {
            if (type.capableOfConstraintsOnNonIdAttributes()) {
                throw exp;
            }
        }
    }

    @DisplayName("should test AND")
    @Assertion(id = "458",
            strategy = "delete using AND condition")
    void shouldDeleteUsingAndCondition() {

        try {
            Vegetable vegetable = vegetables.get(0);
            vegetableRepository.deleteByNameAndQuantity(vegetable.getName(), vegetable.getQuantity());
            TestPropertyUtility.waitForEventualConsistency();
            List<Vegetable> result = vegetableRepository.findNameEqualsAndQuantityEquals(
                    vegetable.getName(),
                    vegetable.getQuantity());
            Assertions.assertThat(result)
                    .isEmpty();
        } catch (UnsupportedOperationException exp) {
            if (type.capableOfAnd() &&
                type.capableOfConstraintsOnNonIdAttributes()) {
                throw exp;
            }
        }
    }

    @DisplayName("should test OR")
    @Assertion(id = "458",
            strategy = "delete using OR condition")
    void shouldDeleteUsingOrCondition() {

        try {
            Vegetable vegetable = vegetables.get(0);
            vegetableRepository.deleteByNameOrQuantity(vegetable.getName(), vegetable.getQuantity());
            TestPropertyUtility.waitForEventualConsistency();

            List<Vegetable> result1 = vegetableRepository.findNameEquals(vegetable.getName());
            Assertions.assertThat(result1)
                    .isEmpty();
            List<Vegetable> result2 = vegetableRepository.findQuantityGt(vegetable.getQuantity());
            Assertions.assertThat(result2)
                    .isNotEmpty();
        } catch (UnsupportedOperationException exp) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfOr()) {
                throw exp;
            }
        }
    }
}