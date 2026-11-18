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
package ee.jakarta.tck.data.standalone.persistence.fetching;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Persistence;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.standalone.persistence.Product;
import ee.jakarta.tck.data.standalone.persistence._Product;
import ee.jakarta.tck.data.standalone.persistence.stateful.Inventory;
import ee.jakarta.tck.data.standalone.persistence.stateful.Products;
import ee.jakarta.tck.data.standalone.persistence.stateless.Catalog;
import ee.jakarta.tck.data.standalone.persistence.Product.Department;
import jakarta.data.Order;
import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;

/**
 * Tests of eager fetching of entity attributes that Jakarta Persistence
 * would normally fetch lazily by default.
 */
@Standalone
@Persistence
public class FetchingTests {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                         .addClasses(Catalog.class,
                                     Inventory.class,
                                     Product.class,
                                     Products.class);
    }

    @Inject
    Catalog catalog; // stateless

    @Inject
    Inventory inventory; // stateful

    @Inject
    Products products; // stateful

    @Inject
    UserTransaction tran;

    @Assertion(id = "1185", strategy = """
            Use a stateful repository method annotated Fetching to eagerly
            obtain the departments collection of a Product entity.
            Detach the entities after fetching to confirm the departments
            were populated by the eager fetch, not by lazy loading through
            an active persistence context.
            """)
    public void testEagerFetchStateful() throws Exception {
        inventory.erase();

        Product p1 = Product.of("track jacket",
                                29.99,
                                "TEST-PROD-1022",
                                Department.CLOTHING,
                                Department.SPORTING_GOODS);
        Product p2 = Product.of("extension cord",
                                9.99,
                                "TEST-PROD-1023",
                                Department.ELECTRONICS,
                                Department.OFFICE);
        Product p3 = Product.of("antacid tablets",
                                5.29,
                                "TEST-PROD-1024",
                                Department.PHARMACY,
                                Department.GROCERY);

        tran.begin();
        inventory.persist(p1);
        inventory.persist(p2);
        inventory.persist(p3);
        tran.commit();

        products.detachAll(List.of(p1, p2, p3));

        List<Product> found = inventory.fetchWithDepartments(
                _Product.productNum.between("TEST-PROD-1022",
                                            "TEST-PROD-1023"),
                Order.by(_Product.productNum.asc()));

        assertEquals(2, found.size());

        products.detachAll(found);

        assertEquals("TEST-PROD-1022", found.get(0).getProductNum());
        assertEquals(Set.of(Department.CLOTHING,
                            Department.SPORTING_GOODS),
                     found.get(0).getDepartments());

        assertEquals("TEST-PROD-1023", found.get(1).getProductNum());
        assertEquals(Set.of(Department.ELECTRONICS,
                            Department.OFFICE),
                     found.get(1).getDepartments());

        tran.begin();
        List<Product> toRemove = inventory.fetchWithDepartments(
                _Product.productNum.between("TEST-PROD-1022",
                                            "TEST-PROD-1024"),
                Order.by(_Product.productNum.asc()));

        products.removeAll(toRemove.get(0),
                           toRemove.get(1),
                           toRemove.get(2));
        tran.commit();
    }

    @Assertion(id = "1185", strategy = """
            Use a repository method annotated Fetching to eagerly obtain
            the respective entity attribute.
            """)
    public void testEagerFetchStateless() {
        catalog.deleteByProductNumLike("TEST-PROD-%");

        catalog.addMultiple(Product.of("shoe laces", 2.89, "TEST-PROD-08",
                                       Department.CLOTHING,
                                       Department.SPORTING_GOODS),
                            Product.of("torque wrench", 34.99, "TEST-PROD-09",
                                       Department.AUTOMOTIVE,
                                       Department.TOOLS),
                            Product.of("multivitamin", 5.29, "TEST-PROD-10",
                                       Department.PHARMACY,
                                       Department.GROCERY));

        List<Product> found = catalog.matchesProductNumPattern("TEST-PROD-0_");

        assertEquals(List.of("TEST-PROD-08", "TEST-PROD-09"),
                     found.stream()
                          .map(Product::getProductNum)
                          .sorted()
                          .toList());

        Product prod08 = "TEST-PROD-08".equals(found.get(0).getProductNum())
                                        ? found.get(0)
                                        : found.get(1);
        Product prod09 = "TEST-PROD-09".equals(found.get(0).getProductNum())
                                        ? found.get(0)
                                        : found.get(1);

        assertEquals(Set.of(Department.CLOTHING,
                            Department.SPORTING_GOODS),
                     prod08.getDepartments());

        assertEquals(Set.of(Department.AUTOMOTIVE,
                            Department.TOOLS),
                     prod09.getDepartments());

        assertEquals(3L,
                     catalog.deleteByProductNumLike("TEST-PROD-%"));
    }
}
