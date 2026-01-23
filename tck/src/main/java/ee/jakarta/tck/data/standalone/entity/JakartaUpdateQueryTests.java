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
@DisplayName("Jakarta Data integration with Jakarta Common Query Language for update operations")
public class JakartaUpdateQueryTests {

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

    private static final String UPDATED = "updated";


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

    @Test
    @DisplayName("should update entities using equals condition")
    @Assertion(id = "459", strategy = "update using equals condition")
    void shouldUpdateEq() {
        repository.updateByNameEquals(UPDATED, "Apple");
        TestPropertyUtility.waitForEventualConsistency();

        Assertions.assertThat(repository.findByName(UPDATED))
                .hasSize(2);
        Assertions.assertThat(repository.findByName("Apple"))
                .isEmpty();
    }

    @Test
    @DisplayName("should update entities using not equals condition")
    @Assertion(id = "459", strategy = "update using not equals condition")
    void shouldUpdateNeq() {
        repository.updateByNameNotEquals(UPDATED, "Apple");
        TestPropertyUtility.waitForEventualConsistency();

        Assertions.assertThat(repository.findByName(UPDATED))
                .hasSize(8);
        Assertions.assertThat(repository.findByName("Apple"))
                .hasSize(2);
    }

    @Test
    @DisplayName("should update entities using greater than condition")
    @Assertion(id = "459", strategy = "update using greater than condition")
    void shouldUpdateGt() {
        repository.updateByQuantityGreaterThan(UPDATED, 10L);
        TestPropertyUtility.waitForEventualConsistency();

        Assertions.assertThat(repository.findByName(UPDATED))
                .hasSize(1);
    }

    @Test
    @DisplayName("should update entities using less than condition")
    @Assertion(id = "459", strategy = "update using less than condition")
    void shouldUpdateLt() {
        repository.updateByQuantityLessThan(UPDATED, 5L);
        TestPropertyUtility.waitForEventualConsistency();

        Assertions.assertThat(repository.findByName(UPDATED))
                .hasSize(1);
    }

    @Test
    @DisplayName("should update entities using greater than or equals condition")
    @Assertion(id = "459", strategy = "update using greater than or equals condition")
    void shouldUpdateGte() {
        repository.updateByQuantityGreaterThanEqual(UPDATED, 10L);
        TestPropertyUtility.waitForEventualConsistency();

        Assertions.assertThat(repository.findByName(UPDATED))
                .hasSize(5);
    }

    @Test
    @DisplayName("should update entities using less than or equals condition")
    @Assertion(id = "459", strategy = "update using less than or equals condition")
    void shouldUpdateLte() {
        repository.updateByQuantityLessThanEqual(UPDATED, 5L);
        TestPropertyUtility.waitForEventualConsistency();

        Assertions.assertThat(repository.findByName(UPDATED))
                .hasSize(3);
    }

    @Test
    @DisplayName("should update entities using between condition")
    @Assertion(id = "459", strategy = "update using between condition")
    void shouldUpdateBetween() {
        repository.updateByQuantityBetween(UPDATED, 7L, 10L);
        TestPropertyUtility.waitForEventualConsistency();

        Assertions.assertThat(repository.findByName(UPDATED))
                .hasSize(6);
    }

    @Test
    @DisplayName("should update entities using IN condition")
    @Assertion(id = "459", strategy = "update using IN condition")
    void shouldUpdateIn() {
        repository.updateByNameIn(UPDATED, List.of("Apple", "Banana"));
        TestPropertyUtility.waitForEventualConsistency();

        Assertions.assertThat(repository.findByName(UPDATED))
                .hasSize(4);
    }

    @Test
    @DisplayName("should update entities using NOT IN condition")
    @Assertion(id = "459", strategy = "update using NOT IN condition")
    void shouldUpdateNotIn() {
        repository.updateByNameNotIn(UPDATED, List.of("Apple"));
        TestPropertyUtility.waitForEventualConsistency();

        Assertions.assertThat(repository.findByName(UPDATED))
                .hasSize(8);
        Assertions.assertThat(repository.findByName("Apple"))
                .hasSize(2);
    }

    @Test
    @DisplayName("should update entities using AND condition")
    @Assertion(id = "459", strategy = "update using AND condition")
    void shouldUpdateAnd() {
        repository.updateByNameAndQuantity(UPDATED, "Apple", 10L);
        TestPropertyUtility.waitForEventualConsistency();

        Assertions.assertThat(repository.findByName(UPDATED))
                .hasSize(1);
    }

    @Test
    @DisplayName("should update entities using OR condition")
    @Assertion(id = "459", strategy = "update using OR condition")
    void shouldUpdateOr() {
        repository.updateByNameOrQuantity(UPDATED, "Apple", 3L);
        TestPropertyUtility.waitForEventualConsistency();

        Assertions.assertThat(repository.findByName(UPDATED))
                .hasSize(3);
    }

}