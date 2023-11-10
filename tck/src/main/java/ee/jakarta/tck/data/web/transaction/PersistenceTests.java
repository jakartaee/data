/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.web.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Persistence;
import ee.jakarta.tck.data.framework.junit.anno.Transaction;
import ee.jakarta.tck.data.framework.junit.anno.Web;
import ee.jakarta.tck.data.standalone.persistence.Catalog;
import ee.jakarta.tck.data.standalone.persistence.Product;
import ee.jakarta.tck.data.standalone.persistence.Product.Department;
//import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;

/*
 * TODO would it be possible to use Transactions in a standalone environment?
 * If so how would we verify that transactions are available?
 */
@Web
@Persistence
@Transaction
public class PersistenceTests {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class).addClasses(Catalog.class, Product.class);
    }

    @Inject
    UserTransaction tran;

    @Inject
    Catalog catalog;

    @Assertion(id = "133", strategy = "Verify that data is persisted to repository when a transaction is committed")
    public void testCommit() throws Exception {
        catalog.deleteByProductNumLike("TEST-PROD-%");

        List<Product> yarns;

        tran.begin();

        catalog.save(Product.of("wool-yarn", 7.99, "TEST-PROD-100", Department.CRAFTS));
        catalog.save(Product.of("cotton-yarn", 5.99, "TEST-PROD-101", Department.CRAFTS));
        catalog.save(Product.of("acylic-yarn", 3.99, "TEST-PROD-102", Department.CRAFTS));

        yarns = catalog.findByProductNumLike("TEST-PROD-10_");
        assertTrue(yarns.isEmpty(), 
                "Save should not have persisted data prior to transaction commit, but instead found " + yarns.size() + " results");

        tran.commit();

        yarns = catalog.findByProductNumLike("TEST-PROD-10_");
        assertFalse(yarns.isEmpty(), "Commit should have persisted data");
        assertEquals(3, yarns.size());

        Product woolYarn = yarns.stream().filter(p -> p.getProductNum() == "TEST-PROD-100").findFirst().orElse(null);
        assertNotNull(woolYarn, "Result set did not contain the expected product");
        assertEquals("TEST-PROD-100", woolYarn.getProductNum());
    }

    @Assertion(id = "133", strategy = "Verify that data is not persisted to repository when a transaction is rolledback")
    public void testRollback() throws Exception {
        catalog.deleteByProductNumLike("TEST-PROD-%");

        List<Product> paints;

        tran.begin();

        catalog.save(Product.of("oil-paint", 12.55, "TEST-PROD-110", Department.CRAFTS));
        catalog.save(Product.of("acrylic-paint", 6.55, "TEST-PROD-111", Department.CRAFTS));
        catalog.save(Product.of("watercolor", 5.55, "TEST-PROD-112", Department.CRAFTS));

        paints = catalog.findByProductNumLike("TEST-PROD-11_");
        assertTrue(paints.isEmpty(), 
                "Save should not have persisted data prior to transaction commit, but instead found " + paints.size() + " results");

        tran.rollback();

        paints = catalog.findByProductNumLike("TEST-PROD-11_");
        assertTrue(paints.isEmpty(), "Save should not have persisted data after transaction rollback.");
    }

}