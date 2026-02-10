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

import java.util.Optional;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Repository
public interface FruitRepository extends BasicRepository<Fruit, String> {

    @Query("FROM Fruit")
    List<FruitSummary> findAllWithProjection();

    @Query("FROM Fruit")
    Stream<Fruit> findAllQuery();

    @Query("FROM Fruit ORDER BY name ASC")
    List<Fruit> findAllAsc();

    @Query("FROM Fruit ORDER BY name DESC")
    List<Fruit> findAllDesc();

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

    @Query("FROM Fruit WHERE name IN (:names)")
    List<Fruit> findNameIn(@Param("names") Set<String> names);

    @Query("FROM Fruit WHERE name = :name AND quantity = :quantity")
    List<Fruit> findNameEqualsAndQuantityEquals(@Param("name") String name, @Param("quantity") Long quantity);

    @Query("FROM Fruit WHERE name = :name1 OR name = :name2")
    List<Fruit> findNameEqualsORNameEquals(@Param("name1") String name1, @Param("name2")  String name2);

    @Query("SELECT count(this) FROM Fruit")
    long countAll();

    @Query("SELECT name FROM Fruit ORDER BY name ASC")
    List<String> findAllOnlyNameOrderByName();

    @Query("SELECT name, quantity FROM Fruit ORDER BY name ASC")
    List<Object[]> findAllNameAndQuantityOrderByQuantity();

    @Query("WHERE id(this) = :id")
    Optional<Fruit> findByIdUsingIdFunction(@Param("id") String id);

    @Query("SELECT id(this) FROM Fruit WHERE id(this) = :id ORDER BY id(this)")
    Optional<String> findByIdUsingIdFunctionOrderById(@Param("id")  String id);

    @Query("FROM Fruit WHERE name = :name")
    List<Fruit> findByName(String name);

    @Query("FROM Fruit WHERE quantity > :quantity ORDER BY name ASC")
    List<Fruit> findByQuantityGreaterThanOrderByNameAsc(@Param("quantity") Long quantity);

    @Query("DELETE FROM Fruit WHERE name = :name")
    void deleteByName(@Param("name") String name);

    @Query("DELETE FROM Fruit WHERE name <> :name")
    void deleteByNotEqualsName(@Param("name") String name);

    @Query("DELETE FROM Fruit WHERE quantity > :quantity")
    void deleteQuantityGreaterThan(@Param("quantity") Long quantity);

    @Query("DELETE FROM Fruit WHERE quantity >= :quantity")
    void deleteQuantityGreaterThanEquals(@Param("quantity") Long quantity);

    @Query("DELETE FROM Fruit WHERE quantity < :quantity")
    void deleteLesserThan(@Param("quantity") Long quantity);

    @Query("DELETE FROM Fruit WHERE quantity <= :quantity")
    void deleteQuantityLesserThanEquals(@Param("quantity") Long quantity);

    @Query("DELETE FROM Fruit WHERE name IN (:names)")
    void deleteByNameIn(@Param("names") List<String> names);

    @Query("DELETE FROM Fruit WHERE name = :name AND quantity = :quantity")
    void deleteByNameAndQuantity(@Param("name") String name, @Param("quantity") Long quantity);

    @Query("DELETE FROM Fruit WHERE name = :name OR quantity = :quantity")
    void deleteByNameOrQuantity(@Param("name") String name, @Param("quantity") Long quantity);

    @Query("UPDATE Fruit SET name = :updated WHERE name = :name")
    void updateByNameEquals(@Param("updated") String updated,
                            @Param("name") String name);

    @Query("UPDATE Fruit SET name = :updated WHERE name <> :name")
    void updateByNameNotEquals(@Param("updated") String updated,
                               @Param("name") String name);

    @Query("UPDATE Fruit SET name = :updated WHERE quantity > :quantity")
    void updateByQuantityGreaterThan(@Param("updated") String updated,
                                     @Param("quantity") Long quantity);

    @Query("UPDATE Fruit SET name = :updated WHERE quantity < :quantity")
    void updateByQuantityLessThan(@Param("updated") String updated,
                                  @Param("quantity") Long quantity);

    @Query("UPDATE Fruit SET name = :updated WHERE quantity >= :quantity")
    void updateByQuantityGreaterThanEqual(@Param("updated") String updated,
                                          @Param("quantity") Long quantity);

    @Query("UPDATE Fruit SET name = :updated WHERE quantity <= :quantity")
    void updateByQuantityLessThanEqual(@Param("updated") String updated,
                                       @Param("quantity") Long quantity);

    @Query("UPDATE Fruit SET name = :updated WHERE quantity BETWEEN :start AND :end")
    void updateByQuantityBetween(@Param("updated") String updated,
                                 @Param("start") Long start,
                                 @Param("end") Long end);

    @Query("UPDATE Fruit SET name = :updated WHERE name IN (:names)")
    void updateByNameIn(@Param("updated") String updated,
                        @Param("names") List<String> names);

    @Query("UPDATE Fruit SET name = :updated WHERE name NOT IN (:names)")
    void updateByNameNotIn(@Param("updated") String updated,
                           @Param("names") List<String> names);

    @Query("UPDATE Fruit SET name = :updated WHERE name = :name AND quantity = :quantity")
    void updateByNameAndQuantity(@Param("updated") String updated,
                                 @Param("name") String name,
                                 @Param("quantity") Long quantity);

    @Query("UPDATE Fruit SET name = :updated WHERE name = :name OR quantity = :quantity")
    void updateByNameOrQuantity(@Param("updated") String updated,
                                @Param("name") String name,
                                @Param("quantity") Long quantity);

}
