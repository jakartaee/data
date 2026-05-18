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
package ee.jakarta.tck.data.standalone.persistence;

import static jakarta.data.repository.By.ID;

import java.util.List;
import java.util.Optional;

import jakarta.data.repository.By;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Find;
import jakarta.data.repository.Repository;
import jakarta.data.repository.stateful.Detach;
import jakarta.data.repository.stateful.Merge;
import jakarta.data.repository.stateful.Persist;
import jakarta.data.repository.stateful.Refresh;
import jakarta.data.repository.stateful.Remove;
import jakarta.transaction.Transactional;

/**
 * A stateful repository for the Product entity.
 * This repository interface inherits from the built-in DataRepository
 * interface, which is compatible with stateful.
 */
@Repository
public interface Products extends DataRepository<Product, String> {

    @Find
    Optional<Product> byNumber(@By(ID) String productNumber);

    @Detach
    void detachAll(List<Product> products);

    @Merge
    Product[] mergeAll(Product... products);

    @Persist
    @Transactional
    void persistAll(Product... product);

    @Remove
    @Transactional
    void removeAll(Product... products);

    @Refresh
    void refreshAll(Product... products);
}
