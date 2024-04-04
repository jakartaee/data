/*
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
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
import java.util.stream.Stream;

import jakarta.data.Order;
import jakarta.data.repository.By;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Param;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;
import jakarta.data.repository.Update;

@Repository
public interface Catalog extends DataRepository<Product, String> {

    @Insert
    Product add(Product product);

    @Insert
    Product[] addMultiple(Product... products);

    @Find
    Optional<Product> get(String productNum);

    @Update
    Product modify(Product product);

    @Update
    Product[] modifyMultiple(Product... products);

    @Delete
    void remove(Product product);

    @Delete
    void removeMultiple(Product... products);

    @Save
    void save(Product product);

    @Delete
    void deleteById(@By(ID) String productNum);

    long deleteByProductNumLike(String pattern);

    int countByPriceGreaterThanEqual(Double price);

    @Query("WHERE LENGTH(name) = ?1 AND price < ?2 ORDER BY name")
    List<Product> findByNameLengthAndPriceBelow(int nameLength, double maxPrice);

    List<Product> findByNameLike(String name);

    @OrderBy(value = "price", descending = true)
    Stream<Product> findByPriceNotNullAndPriceLessThanEqual(double maxPrice);

    List<Product> findByPriceNull();

    List<Product> findByProductNumBetween(String first, String last, Order<Product> sorts);

    List<Product> findByProductNumLike(String productNum);

//    EntityManager getEntityManager();
//
//    default double sumPrices(Department... departments) {
//        StringBuilder jpql = new StringBuilder("SELECT SUM(o.price) FROM Product o");
//        for (int d = 1; d <= departments.length; d++) {
//            jpql.append(d == 1 ? " WHERE " : " OR ");
//            jpql.append('?').append(d).append(" MEMBER OF o.departments");
//        }
//
//        EntityManager em = getEntityManager();
//        TypedQuery<Double> query = em.createQuery(jpql.toString(), Double.class);
//        for (int d = 1; d <= departments.length; d++) {
//            query.setParameter(d, departments[d - 1]);
//        }
//        return query.getSingleResult();
//    }

    @Query("FROM Product WHERE (:rate * price <= :max AND :rate * price >= :min) ORDER BY name")
    Stream<Product> withTaxBetween(@Param("min") double mininunTaxAmount,
                                   @Param("max") double maximumTaxAmount,
                                   @Param("rate") double taxRate);
}
