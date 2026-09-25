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
package ee.jakarta.tck.data.standalone.persistence.resourceaccess;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Persistence;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.standalone.persistence.Product;
import ee.jakarta.tck.data.standalone.persistence.Product.Department;
import jakarta.inject.Inject;
import jakarta.persistence.EntityAgent;
import jakarta.persistence.TypedQuery;

/**
 * Tests of resource accessor methods on a repository.
 */
@Standalone
@Persistence
public class ResourceAccessTests {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                         .addClasses(Warehouse.class,
                                     Product.class);
    }

    @Inject
    Warehouse warehouse;

    @Assertion(id = "1589", strategy = """
            Use a resource accessor method that returns EntityAgent.
            Use the EntityAgent to access the database.
            """)
    public void testEntityAgent() {
        warehouse.clear();

        // AUTOMOTIVE: floor jack is the highest-priced item
        warehouse.insert(Product.of(
                "floor jack", 89.99, "TEST-PROD-5000", Department.AUTOMOTIVE));
        warehouse.insert(Product.of(
                "jumper cables", 22.49, "TEST-PROD-5001", Department.AUTOMOTIVE));
        warehouse.insert(Product.of(
                "tire inflator", 34.99, "TEST-PROD-5002", Department.AUTOMOTIVE));

        // ELECTRONICS: smart speaker is the highest-priced item
        warehouse.insert(Product.of(
                "smart speaker", 79.99, "TEST-PROD-5003", Department.ELECTRONICS));
        warehouse.insert(Product.of(
                "surge protector", 27.99, "TEST-PROD-5004", Department.ELECTRONICS));

        // FURNITURE: standing desk is the highest-priced item
        warehouse.insert(Product.of(
                "standing desk", 349.99, "TEST-PROD-5005", Department.FURNITURE));
        warehouse.insert(Product.of(
                "TV stand", 94.99, "TEST-PROD-5006", Department.FURNITURE));
        warehouse.insert(Product.of(
                "bookcase", 119.99, "TEST-PROD-5007", Department.FURNITURE));

        // TOOLS: power drill is the highest-priced item
        warehouse.insert(Product.of(
                "power drill", 54.99, "TEST-PROD-5008", Department.TOOLS));
        warehouse.insert(Product.of(
                "socket set", 39.99, "TEST-PROD-5009", Department.TOOLS));
        warehouse.insert(Product.of(
                "work gloves", 12.99, "TEST-PROD-5010", Department.TOOLS));

        // For each department, retrieve the product with the highest price,
        // sorted by department name (enum ordinal/name ascending).
        List<Product> expensive;
        try (EntityAgent agent = warehouse.agent()) {
            assertEquals(true,
                         agent.isOpen());

            TypedQuery<Product> query = agent.createQuery("""
                    SELECT p FROM Product p
                      JOIN p.departments d
                     WHERE p.productNum LIKE 'TEST-PROD-5%'
                       AND p.price = (SELECT MAX(p2.price)
                                        FROM Product p2
                                        JOIN p2.departments d2
                                       WHERE d2 = d)
                     ORDER BY d ASC
                    """, Product.class);
            expensive = query.getResultList();
        }

        // Results must be one product per department, in ascending department
        // name order: AUTOMOTIVE, ELECTRONICS, FURNITURE, TOOLS
        assertEquals(List.of("floor jack",
                             "smart speaker",
                             "standing desk",
                             "power drill"),
                expensive.stream()
                         .map(Product::getName)
                         .toList());

        warehouse.clear();
    }
}
