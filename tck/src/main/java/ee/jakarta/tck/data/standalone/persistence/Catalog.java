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
package ee.jakarta.tck.data.standalone.persistence;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.data.repository.DataRepository;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Sort;

import ee.jakarta.tck.data.standalone.persistence.Product.Department;

@Repository
public interface Catalog extends DataRepository<Product, String> {

    void save(Product product);

    void deleteById(String productNum);

    long deleteByProductNumLike(String pattern);

    int countByPriceGreaterThanEqual(Double price);

    int countBySurgePriceGreaterThanEqual(Double price);

    @OrderBy("name")
    Product[] findByDepartmentsContains(Department department);

    LinkedList<Product> findByDepartmentsEmpty();

    Iterable<Product> findByIdBetween(String first, String last, Sort... sorts);

    List<Product> findByNameLike(String name);

    @OrderBy(value = "price", descending = true)
    Stream<Product> findByPriceNotNullAndPriceLessThanEqual(double maxPrice);

    Collection<Product> findByPriceNull();
}
