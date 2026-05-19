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
package ee.jakarta.tck.data.standalone.persistence.stateful;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Persistence;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.standalone.persistence.Product;
import ee.jakarta.tck.data.standalone.persistence._Product;
import ee.jakarta.tck.data.standalone.persistence.Product.Department;

import jakarta.data.Order;
import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;

/**
 * Tests for using stateful repositories to perform operations on
 * Jakarta Persistence entities.
 */
@Standalone
@Persistence
public class StatefulPersistenceEntityTests {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap
                .create(WebArchive.class)
                .addClasses(Inventory.class,
                            Product.class,
                            Products.class);
    }

    @Inject
    Inventory inventory;

    @Inject
    Products products;

    @Inject
    UserTransaction tran;

    @Assertion(id = "471", strategy = """
            Use the Detach annotation to detach entities from the persistence context.
            Verify that modifications to detached entities are not automatically
            persisted to the database.
            """)
    public void testDetach() throws Exception {
        inventory.erase();

        Product p1 = Product.of("drill",
                                39.99,
                                "TEST-PROD-1002",
                                Department.TOOLS);
        Product p2 = Product.of("screwdriver set",
                                24.99,
                                "TEST-PROD-1003",
                                Department.TOOLS);

        products.persistAll(p1, p2);

        tran.begin();
        List<Product> found = inventory.filter(_Product.price.between(20.00, 40.00),
                                               Order.by(_Product.price.desc()))
                                       .toList();
        products.detachAll(found);
        for (Product p : found) {
            p.setPrice(p.getPrice() + 1.00);
        }
        tran.commit();

        found = inventory.filter(_Product.price.between(20.00, 40.00),
                                 Order.by(_Product.price.desc()))
                         .toList();

        assertEquals(2,
                     found.size());

        assertEquals(39.99,
                     found.get(0).getPrice(),
                     0.01);

        assertEquals(24.99,
                     found.get(1).getPrice(),
                     0.01);

        products.removeAll(found.get(0), found.get(1));
    }

    @Assertion(id = "472", strategy = """
            Use the Merge annotation to merge detached entities back into
            the persistence context. Verify that modifications to detached
            entities are persisted after merging.
            """)
    public void testMerge() throws Exception {
        inventory.erase();

        Product p1 = Product.of("microwave",
                                149.99,
                                "TEST-PROD-1004",
                                Department.APPLIANCES);

        inventory.persist(p1);
        long v1 = p1.getVersionNum();

        p1.setName("stainless steel microwave");

        tran.begin();
        Product merged = inventory.merge(p1);
        tran.commit();

        assertEquals(v1 + 1,
                     merged.getVersionNum());
        assertEquals("stainless steel microwave",
                     merged.getName());
        assertEquals(149.99,
                     merged.getPrice(),
                     0.01);
        assertEquals(Set.of(Department.APPLIANCES),
                     merged.getDepartments());

        Product found = products.byNumber("TEST-PROD-1004")
                                .orElseThrow();

        assertEquals(v1 + 1,
                     found.getVersionNum());
        assertEquals("stainless steel microwave",
                     found.getName());
        assertEquals(149.99,
                     found.getPrice(),
                     0.01);
        assertEquals(Set.of(Department.APPLIANCES),
                     found.getDepartments());

        inventory.remove(found);
    }

    @Assertion(id = "473", strategy = """
            Use the Merge annotation with multiple entities to merge them
            back into the persistence context. Verify that all modifications
            are persisted correctly.
            """)
    public void testMergeMultiple() throws Exception {
        inventory.erase();

        Product p1 = Product.of("notebook",
                                1.99,
                                "TEST-PROD-1005",
                                Department.OFFICE);
        Product p2 = Product.of("stapler",
                                19.99,
                                "TEST-PROD-1006",
                                Department.OFFICE);

        products.persistAll(p1, p2);
     
        long v1 = p1.getVersionNum();
        long v2 = p2.getVersionNum();

        p1.setPrice(2.99);
        p2.setPrice(18.99);

        tran.begin();
        Product[] merged = products.mergeAll(p1, p2);
        tran.commit();

        assertEquals(2,
                     merged.length);

        assertEquals(2.99,
                     merged[0].getPrice(),
                     0.01);
        assertEquals(v1 + 1,
                     merged[0].getVersionNum());

        assertEquals(18.99,
                     merged[1].getPrice(),
                     0.01);
        assertEquals(v2 + 1,
                     merged[1].getVersionNum());

        List<Product> found = inventory.withDiscountedPriceUpTo(17.50, 10);

        assertEquals(2,
                     found.size());

        assertEquals(2.99,
                     found.get(1).getPrice(),
                     0.01);
        assertEquals(v1 + 1,
                     found.get(1).getVersionNum());

        assertEquals(18.99,
                     found.get(0).getPrice(),
                     0.01);
        assertEquals(v2 + 1,
                     found.get(0).getVersionNum());

        products.removeAll(found.get(0), found.get(1));
    }

    @Assertion(id = "470", strategy = """
            Persist an entity and then modify the entity instance.
            Query the database to confirm that the entity was persisted
            and the modifications were honored.
            """)
    public void testModifyPersistedEntity() throws Exception {
        inventory.erase();

        Product p1 = Product.of("tennis racket",
                                82.99,
                                "TEST-PROD-1001",
                                Department.SPORTING_GOODS);

        tran.begin();
        inventory.persist(p1);
        p1.setPrice(84.98);
        tran.commit();

        List<Product> found = inventory
                .filter(_Product.productNum.equalTo("TEST-PROD-1001"),
                        Order.by(_Product.productNum.asc()))
                .toList();

        assertEquals(List.of("TEST-PROD-1001"),
                     found.stream()
                          .map(Product::getProductNum)
                     .toList());

        assertEquals("tennis racket",
                     found.get(0).getName());

        assertEquals(84.98,
                     found.get(0).getPrice(),
                     0.01);

        assertEquals(Set.of(Department.SPORTING_GOODS),
                     found.get(0).getDepartments());

        inventory.remove(found.get(0));
    }

    @Assertion(id = "474", strategy = """
            Use the Persist annotation to persist new entities to the database.
            Verify that the entities are successfully stored and can be retrieved.
            """)
    public void testPersist() throws Exception {
        inventory.erase();

        Product p1 = Product.of("jeans",
                                39.99,
                                "TEST-PROD-1007",
                                Department.CLOTHING);

        tran.begin();
        inventory.persist(p1);
        tran.commit();

        Stream<Product> found = inventory
                .filter(_Product.productNum.equalTo("TEST-PROD-1007"),
                        Order.by(_Product.productNum.asc()));

        Product result = found.findFirst().orElseThrow();

        assertEquals("jeans",
                     result.getName());

        assertEquals(39.99,
                     result.getPrice(),
                     0.01);

        assertEquals("TEST-PROD-1007",
                     result.getProductNum());

        assertEquals(Set.of(Department.CLOTHING),
                     result.getDepartments());

        inventory.remove(result);
    }

    @Assertion(id = "475", strategy = """
            Use the Persist annotation with multiple entities to persist them
            to the database. Verify that all entities are successfully stored.
            """)
    public void testPersistMultiple() throws Exception {
        inventory.erase();

        Product p1 = Product.of("desk lamp",
                                19.99,
                                "TEST-PROD-1008",
                                Department.FURNITURE);
        Product p2 = Product.of("bookshelf",
                                164.99,
                                "TEST-PROD-1009",
                                Department.FURNITURE);
        Product p3 = Product.of("office chair",
                                289.99,
                                "TEST-PROD-1010",
                                Department.FURNITURE);

        products.persistAll(p1, p2, p3);

        Product found1 = products.byNumber("TEST-PROD-1008").orElseThrow();
        Product found2 = products.byNumber("TEST-PROD-1009").orElseThrow();
        Product found3 = products.byNumber("TEST-PROD-1010").orElseThrow();

        assertEquals("desk lamp",
                     found1.getName());

        assertEquals(19.99,
                     found1.getPrice(),
                     0.01);

        assertEquals("bookshelf",
                     found2.getName());

        assertEquals(164.99,
                     found2.getPrice(),
                     0.01);

        assertEquals("office chair",
                     found3.getName());

        assertEquals(289.99,
                     found3.getPrice(),
                     0.01);

        products.removeAll(found1, found2, found3);
    }

    @Assertion(id = "476", strategy = """
            Use the Refresh annotation to refresh entities from the database.
            Verify that changes made directly to the database are reflected
            in the refreshed entity instances.
            """)
    public void testRefresh() throws Exception {
        inventory.erase();

        Product p1 = Product.of("laptop",
                                1239.99,
                                "TEST-PROD-1011",
                                Department.ELECTRONICS);
        Product p2 = Product.of("wireless mouse",
                                74.99,
                                "TEST-PROD-1012",
                                Department.ELECTRONICS);

        products.persistAll(p1, p2);

        p1.setPrice(1444.99);
        p2.setPrice(79.99);

        products.refreshAll(p1, p2);

        assertEquals(1239.99,
                     p1.getPrice(),
                     0.01);

        assertEquals(74.99,
                     p2.getPrice(),
                     0.01);

        products.removeAll(p1, p2);
    }

    @Assertion(id = "477", strategy = """
            Use the Remove annotation to remove an entity from the database.
            Verify that the entity is successfully deleted and cannot be retrieved.
            """)
    public void testRemove() throws Exception {
        inventory.erase();

        Product p1 = Product.of("garden hose",
                                34.99,
                                "TEST-PROD-1013",
                                Department.GARDEN);

        tran.begin();
        inventory.persist(p1);
        tran.commit();

        Stream<Product> found = inventory
                .filter(_Product.productNum.equalTo("TEST-PROD-1013"),
                        Order.by(_Product.productNum.asc()));

        assertEquals(1L,
                     found.count());

        found = inventory
                .filter(_Product.productNum.equalTo("TEST-PROD-1013"),
                        Order.by(_Product.productNum.asc()));

        Product result = found.findFirst().orElseThrow();

        tran.begin();
        inventory.remove(result);
        tran.commit();

        found = inventory
                .filter(_Product.productNum.equalTo("TEST-PROD-1013"),
                        Order.by(_Product.productNum.asc()));

        assertEquals(0L,
                     found.count());
    }

    @Assertion(id = "478", strategy = """
            Use the Remove annotation with multiple entities to remove them
            from the database. Verify that all entities are successfully deleted.
            """)
    public void testRemoveMultiple() throws Exception {
        inventory.erase();

        Product p1 = Product.of("aspirin",
                                8.99,
                                "TEST-PROD-1014",
                                Department.PHARMACY);
        Product p2 = Product.of("bandages",
                                2.99,
                                "TEST-PROD-1015",
                                Department.PHARMACY);
        Product p3 = Product.of("vitamins",
                                7.99,
                                "TEST-PROD-1016",
                                Department.PHARMACY);

        products.persistAll(p1, p2, p3);

        assertEquals(true,
                     products.byNumber("TEST-PROD-1014").isPresent());

        assertEquals(true,
                     products.byNumber("TEST-PROD-1015").isPresent());

        assertEquals(true,
                     products.byNumber("TEST-PROD-1016").isPresent());

        products.removeAll(p1, p3);

        assertEquals(false,
                     products.byNumber("TEST-PROD-1014").isPresent());

        assertEquals(true,
                     products.byNumber("TEST-PROD-1015").isPresent());

        assertEquals(false,
                     products.byNumber("TEST-PROD-1016").isPresent());

        products.removeAll(p2);

        assertEquals(false,
                     products.byNumber("TEST-PROD-1015").isPresent());
    }
}
