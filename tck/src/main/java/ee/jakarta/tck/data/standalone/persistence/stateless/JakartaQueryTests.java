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
package ee.jakarta.tck.data.standalone.persistence.stateless;

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

/**
 * Tests of repository methods annotated @JakartaQuery, which causes
 * the method to run a JPQL query.
 */
@Standalone
@Persistence
public class JakartaQueryTests {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                         .addClasses(Catalog.class,
                                     Product.class);
    }

    @Inject
    Catalog catalog;

    @Assertion(id = "1218", strategy = """
            Use a repository method annotated JakartaQuery to run a JPQL
            DELETE operation.
            """)
    public void testJPQLDelete() {
        catalog.discardAllMatching("TEST-PROD-%");

        // LENGTH(name) > 8: "car battery" (11), "floor mat" (9), "oil filter" (10)
        // LENGTH(name) <= 8: "lug nuts" (8) -- kept
        catalog.add(Product.of(
                "car battery", 89.99, "TEST-PROD-3010", Department.AUTOMOTIVE));
        catalog.add(Product.of(
                "lug nuts", 8.99, "TEST-PROD-3011", Department.AUTOMOTIVE));
        catalog.add(Product.of(
                "floor mat", 24.99, "TEST-PROD-3012", Department.AUTOMOTIVE));
        catalog.add(Product.of(
                "oil filter", 12.49, "TEST-PROD-3013", Department.AUTOMOTIVE));

        assertEquals(3,
                     catalog.deleteLongNamed("TEST-PROD-301%", 8));

        assertEquals(List.of("lug nuts"),
                     catalog.findByProductNumLike("TEST-PROD-301%")
                            .stream()
                            .map(Product::getName)
                            .toList());
    }

    @Assertion(id = "1218", strategy = """
            Use a repository method annotated JakartaQuery to run a JPQL
            SELECT query that returns an aggregate value.
            """)
    public void testJPQLSelectAggregate() {
        catalog.discardAllMatching("TEST-PROD-%");

        catalog.add(Product.of(
                "brake light", 3.99, "TEST-PROD-3000", Department.AUTOMOTIVE));
        catalog.add(Product.of(
                "printer paper", 7.99, "TEST-PROD-3001", Department.OFFICE));
        catalog.add(Product.of(
                "windshield wipers", 18.49, "TEST-PROD-3002", Department.AUTOMOTIVE));
        catalog.add(Product.of(
                "motor oil", 6.89, "TEST-PROD-3003", Department.AUTOMOTIVE));

        assertEquals(9.79,
                     catalog.averagePrice(Department.AUTOMOTIVE),
                     0.001);
    }

    @Assertion(id = "1218", strategy = """
            Use a repository method annotated JakartaQuery to run a JPQL
            SELECT query that uses GROUP BY and HAVING to return
            a list of non-entity values.
            """)
    public void testJPQLSelectValues() {
        catalog.discardAllMatching("TEST-PROD-%");

        // ELECTRONICS: avg = (249.99 + 34.99) / 2 = 142.49  --> above threshold
        catalog.add(Product.of(
                "tablet", 249.99, "TEST-PROD-3004", Department.ELECTRONICS));
        catalog.add(Product.of(
                "headphones", 34.99, "TEST-PROD-3005", Department.ELECTRONICS));

        // CRAFTS: avg = (8.99 + 12.99) / 2 = 10.99  --> below threshold
        catalog.add(Product.of(
                "fabric", 8.99, "TEST-PROD-3006", Department.CRAFTS));
        catalog.add(Product.of(
                "knitting needles", 12.99, "TEST-PROD-3007", Department.CRAFTS));

        // FURNITURE: avg = (189.99 + 74.99) / 2 = 132.49  --> above threshold
        catalog.add(Product.of(
                "armchair", 189.99, "TEST-PROD-3008", Department.FURNITURE));
        catalog.add(Product.of(
                "coffee table", 74.99, "TEST-PROD-3009", Department.FURNITURE));

        assertEquals(List.of(Department.ELECTRONICS,
                             Department.FURNITURE),
                     catalog.departmentsWithPriceAverageAbove(50.00));
    }
}
