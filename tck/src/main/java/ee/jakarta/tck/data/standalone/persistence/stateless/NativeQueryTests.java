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
 * Tests of NativeQuery methods on a repository.
 */
@Standalone
@Persistence
public class NativeQueryTests {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                         .addClasses(Catalog.class,
                                     Product.class);
    }

    @Inject
    Catalog catalog;

    @Assertion(id = "1218", strategy = """
            Use a repository method annotated NativeQuery to run a SQL
            SELECT query that returns a count value.
            """)
    public void testSQLSelectCount() {
        catalog.discardAllMatching("TEST-PROD-%");

        catalog.add(Product.of(
                "air fryer", 53.99, "TEST-PROD-2000", Department.APPLIANCES));
        catalog.add(Product.of(
                "golf clubs", 599.99, "TEST-PROD-2001", Department.SPORTING_GOODS));
        catalog.add(Product.of(
                "rain jacket", 44.99, "TEST-PROD-2002", Department.CLOTHING));

        assertEquals(2L,
                     catalog.numPricedAbove(50.0));
    }

    @Assertion(id = "1218", strategy = """
            Use a repository method annotated NativeQuery to run a SQL
            SELECT query that retrieves entities.
            """)
    public void testSQLSelectEntities() {
        catalog.discardAllMatching("TEST-PROD-%");

        catalog.add(Product.of(
                "refrigerator", 859.98, "TEST-PROD-2003", Department.APPLIANCES));
        catalog.add(Product.of(
                "kayak", 748.99, "TEST-PROD-2004", Department.SPORTING_GOODS));
        catalog.add(Product.of(
                "winter coat", 229.99, "TEST-PROD-2005", Department.CLOTHING));
        catalog.add(Product.of(
                "tennis balls", 9.98, "TEST-PROD-2006", Department.SPORTING_GOODS));
        catalog.add(Product.of(
                "flour", 2.19, "TEST-PROD-2007", Department.GROCERY));

        assertEquals(List.of("winter coat",
                             "kayak"),
                     catalog.pricedWithin(100.0, 750.0)
                            .stream()
                            .map(Product::getName)
                            .toList());
    }

    @Assertion(id = "1218", strategy = """
            Use a repository method annotated NativeQuery to run a SQL
            SELECT query that retrieves an entity attribute.
            """)
    public void testSQLSelectEntityAttribute() {
        catalog.discardAllMatching("TEST-PROD-%");

        catalog.add(Product.of(
                "camp stove", 89.99, "TEST-PROD-2008", Department.SPORTING_GOODS));
        catalog.add(Product.of(
                "t-shirt", 3.99, "TEST-PROD-2009", Department.CLOTHING));
        catalog.add(Product.of(
                "hockey stick", 79.99, "TEST-PROD-2010", Department.SPORTING_GOODS));

        assertEquals("t-shirt",
                     catalog.nameOf("TEST-PROD-2009")
                            .orElseThrow());
    }

    @Assertion(id = "1218", strategy = """
            Use a repository method annotated NativeQuery to run a SQL
            UPDATE statement.
            """)
    public void testSQLUpdate() {
        catalog.discardAllMatching("TEST-PROD-%");

        catalog.add(Product.of(
                "smartphone", 799.99, "TEST-PROD-2011", Department.ELECTRONICS));
        catalog.add(Product.of(
                "running shoes", 129.99, "TEST-PROD-2012", Department.SPORTING_GOODS));
        catalog.add(Product.of(
                "backpack", 39.99, "TEST-PROD-2013", Department.CLOTHING));

        int updated = catalog.reprice(749.99, "TEST-PROD-2011");
        assertEquals(1,
                     updated);

        Product smartphone = catalog.get("TEST-PROD-2011").orElseThrow();
        assertEquals(749.99,
                     smartphone.getPrice(),
                     0.001);

        Product shoes = catalog.get("TEST-PROD-2012").orElseThrow();
        assertEquals(129.99,
                     shoes.getPrice(),
                     0.001);

        Product backpack = catalog.get("TEST-PROD-2013").orElseThrow();
        assertEquals(39.99,
                     backpack.getPrice(),
                     0.001);
    }
}
