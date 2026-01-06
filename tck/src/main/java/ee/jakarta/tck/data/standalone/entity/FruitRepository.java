/**
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 * <p>
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package ee.jakarta.tck.data.standalone.entity;

import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Param;
import jakarta.data.repository.Query;

import java.util.List;

@Repository
public interface FruitRepository extends BasicRepository<Fruit, String> {

    @Query("DELETE FROM Fruit")
    void deleteAll();

    @Query("FROM Fruit WHERE name = :name")
    List<Fruit> findNameEquals(@Param("name") String name);

    @Query("FROM Fruit WHERE name <> :name")
    List<Fruit> findNameNotEquals(@Param("name") String name);

    @Query("FROM Fruit WHERE quantity > :quantity")
    List<Fruit> findQuantityGt(@Param("quantity") Long quantity);

    @Query("FROM Fruit WHERE quantity >= :quantity")
    List<Fruit> findQuantityGte(@Param("quantity") Long quantity);

    @Query("FROM Fruit WHERE quantity < :quantity")
    List<Fruit> findQuantityLt(@Param("quantity") Long quantity);

    @Query("FROM Fruit WHERE quantity <= :quantity")
    List<Fruit> findQuantityLte(@Param("quantity") Long quantity);

    @Query("FROM Fruit WHERE name IN (:name1, :name2)")
    List<Fruit> findNameIn(@Param("name1") String name1, @Param("name2")  String name2);

    @Query("FROM Fruit WHERE name = :name AND quantity = :quantity")
    List<Fruit> findNameEqualsAndQuantityGte(String name, Long quantity);

    @Query("FROM Fruit WHERE name = :name OR quantity = :quantity")
    List<Fruit> findNameEqualsORQuantityGte(String name, Long quantity);
}
