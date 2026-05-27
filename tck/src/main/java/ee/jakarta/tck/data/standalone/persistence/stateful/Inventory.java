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

import java.util.List;
import java.util.stream.Stream;

import ee.jakarta.tck.data.standalone.persistence.Product;
import jakarta.data.Order;
import jakarta.data.repository.Find;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.stateful.Merge;
import jakarta.data.repository.stateful.Persist;
import jakarta.data.repository.stateful.Remove;
import jakarta.data.restrict.Restriction;
import jakarta.transaction.Transactional;

/**
 * A stateful repository for the Product entity.
 */
@Repository
public interface Inventory {

    @Query("DELETE FROM Product")
    void erase();

    @Find
    Stream<Product> filter(Restriction<Product> restriction,
                           Order<Product> sortBy);

    @Merge
    Product merge(Product product);

    @Persist
    @Transactional
    void persist(Product product);

    @Remove
    @Transactional
    void remove(Product product);

    @Query("""
            FROM Product
            WHERE price * (1.0 - :percentOff / 100.0) < :max
            ORDER BY price DESC, name ASC, productNum DESC
            """)
    List<Product> withDiscountedPriceUpTo(double max,
                                          double percentOff);
}
