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
import ee.jakarta.tck.data.framework.read.only.FruitPopulator;

import java.util.List;
import java.util.logging.Logger;

@Standalone
@AnyEntity
@DisplayName("Jakarta Data integration with Jakarta Common Query Language for delete operations")
public class JakartaDeleteQueryTests {

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

    @AfterEach
    public void cleanup() {
        fruitRepository.deleteAll();
        TestPropertyUtility.waitForEventualConsistency();
    }

    @DisplayName("should delete all entities")
    @Assertion(id = "458",
            strategy = "Execute a deleteAll query, wait for eventual consistency and verify if all entities are deleted")
    void shouldDeleteAllEntities() {

        try {
            fruitRepository.deleteAll();
            TestPropertyUtility.waitForEventualConsistency();

            Assertions.assertThat(fruitRepository.findAll().toList()).isEmpty();
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of querying without a WHERE clause
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should delete entities using equals condition")
    @Assertion(id = "458",
            strategy = "delete by name equals, verify if entity is deleted")
    void shouldDeleteEq() {
        try {
            Fruit fruit = fruits.getFirst();
            fruitRepository.deleteByName(fruit.getName());
            TestPropertyUtility.waitForEventualConsistency();

            List<Fruit> result = fruitRepository.findNameEquals(fruit.getName());
            Assertions.assertThat(result)
                    .isEmpty();
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable deleting by attribute that is not a key.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should delete not equals condition")
    @Assertion(id = "458",
            strategy = "delete by name not equals, verify if entity is deleted")
    void shouldDeleteNeq() {
        try {
            Fruit fruit = fruits.getFirst();
            fruitRepository.deleteByNotEqualsName(fruit.getName());
            TestPropertyUtility.waitForEventualConsistency();

            List<Fruit> result1 = fruitRepository.findNameNotEquals(fruit.getName());
            Assertions.assertThat(result1)
                    .isEmpty();
            List<Fruit> result2 = fruitRepository.findNameEquals(fruit.getName());
            Assertions.assertThat(result2)
                    .isNotEmpty();
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable deleting by attribute that is not a key.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should delete greater than condition")
    @Assertion(id = "458",
            strategy = "delete by quantity greater than, verify if entity is deleted")
    void shouldGt() {
        try {
            Fruit fruit = fruits.getFirst();
            fruitRepository.deleteQuantityGreaterThan(fruit.getQuantity());
            TestPropertyUtility.waitForEventualConsistency();

            List<Fruit> result1 = fruitRepository.findQuantityGt(fruit.getQuantity());
            Assertions.assertThat(result1)
                    .isEmpty();
            List<Fruit> result2 = fruitRepository.findQuantityLte(fruit.getQuantity());
            Assertions.assertThat(result2)
                    .isNotEmpty();
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable deleting by attribute that is not a key.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should delete greater than equals condition")
    @Assertion(id = "458",
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
    @Assertion(id = "458",
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
    @Assertion(id = "458",
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
    @Assertion(id = "458",
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

    @DisplayName("should test AND")
    @Assertion(id = "458",
            strategy = "delete using AND condition")
    void shouldDeleteUsingAndCondition() {

        try {
            Fruit fruit = fruits.getFirst();
            fruitRepository.deleteByNameAndQuantity(fruit.getName(), fruit.getQuantity());
            TestPropertyUtility.waitForEventualConsistency();
            List<Fruit> result = fruitRepository.findAll().toList();
            Assertions.assertThat(result)
                    .allMatch(f -> !(f.getName().equals(fruit.getName())
                            && f.getQuantity().equals(fruit.getQuantity())));
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of AND.
                // Column and Key-Value databases might not be capable of querying by an attribute that is not the Id.
            } else {
                throw exp;
            }
        }
    }

    @DisplayName("should test OR")
    @Assertion(id = "458",
            strategy = "delete using OR condition")
    void shouldDeleteUsingOrCondition() {

        try {
            Fruit fruit = fruits.getFirst();
            fruitRepository.deleteByNameOrQuantity(fruit.getName(), fruit.getQuantity());
            TestPropertyUtility.waitForEventualConsistency();
            List<Fruit> result = fruitRepository.findAll().toList();

            Assertions.assertThat(result)
                    .allMatch(f -> !(f.getName().equals(fruit.getName())
                            || f.getQuantity().equals(fruit.getQuantity())));
        } catch (UnsupportedOperationException exp) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of OR.
                // Column and Key-Value databases might not be capable querying by an attribute that is not the Id.
            } else {
                throw exp;
            }
        }
    }
}