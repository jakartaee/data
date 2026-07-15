/*
 * Copyright (c) 2023,2026 Contributors to the Eclipse Foundation
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

import static jakarta.data.repository.By.ID;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import ee.jakarta.tck.data.standalone.persistence.Product;
import ee.jakarta.tck.data.standalone.persistence._Product;
import jakarta.data.Order;
import jakarta.data.constraint.Like;
import jakarta.data.repository.By;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Delete;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Is;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Param;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;
import jakarta.data.repository.Update;
import jakarta.persistence.query.NativeQuery;

@Repository
public interface Catalog extends DataRepository<Product, String> {

    @Insert
    Product add(Product product);

    @Insert
    Product[] addMultiple(Product... products);

    @NativeQuery("""
            DELETE FROM Product
             WHERE UPPER(name) = UPPER(?) AND versionNum = ?
            """)
    int deleteIfNamed(String productName, long version);

    @Delete
    long discardAllMatching(@By(ID) @Is(Like.class) String productIdPattern);

    @Find
    Optional<Product> get(String productNum);

    @Update
    Product modify(Product product);

    @Update
    Product[] modifyMultiple(Product... products);

    @Find
    @OrderBy(_Product.NAME)
    @OrderBy(_Product.PRODUCTNUM)
    List<Product> named(@By(_Product.NAME) Like namePattern);

    @NativeQuery("SELECT name FROM Product WHERE productNum = ?")
    Optional<String> nameOf(String prodNum);

    @NativeQuery("SELECT COUNT(*) FROM Product WHERE price > ?")
    long numPricedAbove(double minPrice);

    @NativeQuery("""
            SELECT * FROM Product
             WHERE price >= ? AND price <= ?
             ORDER BY price ASC
            """)
    List<Product> pricedWithin(double minPrice, double maxPrice);

    @Delete
    void remove(Product product);

    @Delete
    void removeMultiple(Product... products);

    @NativeQuery("""
            UPDATE Product
               SET price = ?
             WHERE productNum = ?
            """)
    int reprice(double newPrice, String productNumber);

    @Save
    void save(Product product);

    @Delete
    void deleteById(@By(ID) String productNum);

    long deleteByProductNumLike(String pattern);

    long countByPriceGreaterThanEqual(Double price);

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
