/**
 * Copyright (c) 2025, 2026 Contributors to the Eclipse Foundation
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
public interface VegetableRepository extends BasicRepository<Vegetable, String> {
    
    public static final List<Vegetable> VEGGIES = List.of(
            vegetable("00000000-0000-0000-0000-000000000001", "Potato", 10L),
            vegetable("00000000-0000-0000-0000-000000000002", "Carrot", 5L),
            vegetable("00000000-0000-0000-0000-000000000003", "Onion", 10L),
            vegetable("00000000-0000-0000-0000-000000000004", "Garlic", 7L),
            vegetable("00000000-0000-0000-0000-000000000005", "Eggplant", 3L),
            vegetable("00000000-0000-0000-0000-000000000006", "Beet", 12L),
            vegetable("00000000-0000-0000-0000-000000000007", "Potato", 5L),
            vegetable("00000000-0000-0000-0000-000000000008", "Carrot", 10L),
            vegetable("00000000-0000-0000-0000-000000000009", "Onion", 7L),
            vegetable("00000000-0000-0000-0000-000000000010", "Garlic", 10L)
    );
    
    private static Vegetable vegetable(String id, String name, Long quantity) {
        Vegetable vegetable = new Vegetable();
        vegetable.setId(id);
        vegetable.setName(name);
        vegetable.setQuantity(quantity);
        return vegetable;
    }

    @Query("DELETE FROM Vegetable")
    void deleteAll();

    @Query("FROM Vegetable WHERE name = :name")
    List<Vegetable> findNameEquals(@Param("name") String name);

    @Query("FROM Vegetable WHERE name <> :name")
    List<Vegetable> findNameNotEquals(@Param("name") String name);

    @Query("FROM Vegetable WHERE quantity > :quantity")
    List<Vegetable> findQuantityGt(@Param("quantity") Long quantity);

    @Query("FROM Vegetable WHERE quantity >= :quantity")
    List<Vegetable> findQuantityGte(@Param("quantity") Long quantity);

    @Query("FROM Vegetable WHERE quantity < :quantity")
    List<Vegetable> findQuantityLt(@Param("quantity") Long quantity);

    @Query("FROM Vegetable WHERE quantity <= :quantity")
    List<Vegetable> findQuantityLte(@Param("quantity") Long quantity);

    @Query("FROM Vegetable WHERE name = :name AND quantity = :quantity")
    List<Vegetable> findNameEqualsAndQuantityEquals(@Param("name") String name, @Param("quantity") Long quantity);

    @Query("DELETE FROM Vegetable WHERE name = :name")
    void deleteByName(@Param("name") String name);

    @Query("DELETE FROM Vegetable WHERE name <> :name")
    void deleteByNotEqualsName(@Param("name") String name);

    @Query("DELETE FROM Vegetable WHERE quantity > :quantity")
    void deleteQuantityGreaterThan(@Param("quantity") Long quantity);

    @Query("DELETE FROM Vegetable WHERE quantity >= :quantity")
    void deleteQuantityGreaterThanEquals(@Param("quantity") Long quantity);

    @Query("DELETE FROM Vegetable WHERE quantity < :quantity")
    void deleteLesserThan(@Param("quantity") Long quantity);

    @Query("DELETE FROM Vegetable WHERE quantity <= :quantity")
    void deleteQuantityLesserThanEquals(@Param("quantity") Long quantity);

    @Query("DELETE FROM Vegetable WHERE name IN :names")
    void deleteByNameIn(@Param("names") List<String> names);

    @Query("DELETE FROM Vegetable WHERE name = :name AND quantity = :quantity")
    void deleteByNameAndQuantity(@Param("name") String name, @Param("quantity") Long quantity);

    @Query("DELETE FROM Vegetable WHERE name = :name OR quantity = :quantity")
    void deleteByNameOrQuantity(@Param("name") String name, @Param("quantity") Long quantity);

    @Query("UPDATE Vegetable SET name = :updated WHERE name = :name")
    void updateByNameEquals(@Param("updated") String updated,
                            @Param("name") String name);

    @Query("UPDATE Vegetable SET name = :updated WHERE name <> :name")
    void updateByNameNotEquals(@Param("updated") String updated,
                               @Param("name") String name);

    @Query("UPDATE Vegetable SET name = :updated WHERE quantity > :quantity")
    void updateByQuantityGreaterThan(@Param("updated") String updated,
                                     @Param("quantity") Long quantity);

    @Query("UPDATE Vegetable SET name = :updated WHERE quantity < :quantity")
    void updateByQuantityLessThan(@Param("updated") String updated,
                                  @Param("quantity") Long quantity);

    @Query("UPDATE Vegetable SET name = :updated WHERE quantity >= :quantity")
    void updateByQuantityGreaterThanEqual(@Param("updated") String updated,
                                          @Param("quantity") Long quantity);

    @Query("UPDATE Vegetable SET name = :updated WHERE quantity <= :quantity")
    void updateByQuantityLessThanEqual(@Param("updated") String updated,
                                       @Param("quantity") Long quantity);

    @Query("UPDATE Vegetable SET name = :updated WHERE quantity BETWEEN :start AND :end")
    void updateByQuantityBetween(@Param("updated") String updated,
                                 @Param("start") Long start,
                                 @Param("end") Long end);

    @Query("UPDATE Vegetable SET name = :updated WHERE name IN :names")
    void updateByNameIn(@Param("updated") String updated,
                        @Param("names") List<String> names);

    @Query("UPDATE Vegetable SET name = :updated WHERE name NOT IN :names")
    void updateByNameNotIn(@Param("updated") String updated,
                           @Param("names") List<String> names);

    @Query("UPDATE Vegetable SET name = :updated WHERE name = :name AND quantity = :quantity")
    void updateByNameAndQuantity(@Param("updated") String updated,
                                 @Param("name") String name,
                                 @Param("quantity") Long quantity);

    @Query("UPDATE Vegetable SET name = :updated WHERE name = :name OR quantity = :quantity")
    void updateByNameOrQuantity(@Param("updated") String updated,
                                @Param("name") String name,
                                @Param("quantity") Long quantity);

}
