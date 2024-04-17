/*
 * Copyright (c) 2023, 2024 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.standalone.nosql.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.NoSQL;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.framework.utilities.TestPropertyUtility;
import jakarta.data.exceptions.MappingException;
import jakarta.inject.Inject;

/**
 * Example test class:
 * Execute a test with an NoSQL specific entity with a repository that requires read and writes (AKA not read-only)
 */
@Standalone
@NoSQL
public class NoSQLEntityTests {
    
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class).addClasses(Product.class, Catalog.class);
    }
    
    @Inject
    Catalog catalog;
    
    @Assertion(id = "119", strategy = "Ensure that this test is only run when provider supports nosql entities")
    public void testNotRunOnPersistence() {
        List<Product> products = new ArrayList<>();
        products.add(Product.of(01L, "pen", 2.50, 3.50));
        products.add(Product.of(02L, "pencil", 1.25, 2.00));
        products.add(Product.of(03L, "marker", 3.00, 4.00));
        products.add(Product.of(04L, "calculator", 15.00, 20.00));
        products.add(Product.of(05L, "ruler", 2.00, 2.15));
        
        products.stream().forEach(product -> catalog.save(product));
        
        //IMPORTANT - all NoSQL tests need to wait for eventual consistency after a write action
        //because in a test environment we must be pessimistic and assume some wait is necessary for 
        //all nodes to get the latest update.
        TestPropertyUtility.waitForEventualConsistency(); 
        
        int countExpensive = catalog.countByPriceGreaterThanEqual(2.99);
        assertEquals(2, countExpensive, "Expected two products to be more than 3.00");
        
        Assertions.assertThrows(MappingException.class, () -> {
            catalog.countBySurgePriceGreaterThanEqual(2.99);
        });
        
    }
}
