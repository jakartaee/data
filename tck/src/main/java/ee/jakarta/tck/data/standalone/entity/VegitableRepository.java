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
public interface VegitableRepository extends BasicRepository<Vegitable, String> {
	
    public static final List<Vegitable> VEGGIES = List.of(
    		vegitable("00000000-0000-0000-0000-000000000001", "Potato", 10L),
    		vegitable("00000000-0000-0000-0000-000000000002", "Carrot", 5L),
    		vegitable("00000000-0000-0000-0000-000000000003", "Onion", 10L),
    		vegitable("00000000-0000-0000-0000-000000000004", "Garlic", 7L),
    		vegitable("00000000-0000-0000-0000-000000000005", "Eggplant", 3L),
            vegitable("00000000-0000-0000-0000-000000000006", "Beet", 12L),
            vegitable("00000000-0000-0000-0000-000000000007", "Potato", 5L),
            vegitable("00000000-0000-0000-0000-000000000008", "Carrot", 10L),
            vegitable("00000000-0000-0000-0000-000000000009", "Onion", 7L),
            vegitable("00000000-0000-0000-0000-000000000010", "Garlic", 10L)
    );
    
    private static Vegitable vegitable(String id, String name, Long quantity) {
    	Vegitable vegitable = new Vegitable();
    	vegitable.setId(id);
    	vegitable.setName(name);
    	vegitable.setQuantity(quantity);
        return vegitable;
    }

    @Query("DELETE FROM Vegitable")
    void deleteAll();

    @Query("FROM Vegitable WHERE name = :name")
    List<Vegitable> findNameEquals(@Param("name") String name);

    @Query("FROM Vegitable WHERE name <> :name")
    List<Vegitable> findNameNotEquals(@Param("name") String name);

    @Query("FROM Vegitable WHERE quantity > :quantity")
    List<Vegitable> findQuantityGt(@Param("quantity") Long quantity);

    @Query("FROM Vegitable WHERE quantity >= :quantity")
    List<Vegitable> findQuantityGte(@Param("quantity") Long quantity);

    @Query("FROM Vegitable WHERE quantity < :quantity")
    List<Vegitable> findQuantityLt(@Param("quantity") Long quantity);

    @Query("FROM Vegitable WHERE quantity <= :quantity")
    List<Vegitable> findQuantityLte(@Param("quantity") Long quantity);

    @Query("FROM Vegitable WHERE name = :name AND quantity = :quantity")
    List<Vegitable> findNameEqualsAndQuantityEquals(@Param("name") String name, @Param("quantity") Long quantity);

    @Query("DELETE FROM Vegitable WHERE name = :name")
    void deleteByName(@Param("name") String name);

    @Query("DELETE FROM Vegitable WHERE name <> :name")
    void deleteByNotEqualsName(@Param("name") String name);

    @Query("DELETE FROM Vegitable WHERE quantity > :quantity")
    void deleteQuantityGreaterThan(@Param("quantity") Long quantity);

    @Query("DELETE FROM Vegitable WHERE quantity >= :quantity")
    void deleteQuantityGreaterThanEquals(@Param("quantity") Long quantity);

    @Query("DELETE FROM Vegitable WHERE quantity < :quantity")
    void deleteLesserThan(@Param("quantity") Long quantity);

    @Query("DELETE FROM Vegitable WHERE quantity <= :quantity")
    void deleteQuantityLesserThanEquals(@Param("quantity") Long quantity);

    @Query("DELETE FROM Vegitable WHERE name IN :names")
    void deleteByNameIn(@Param("names") List<String> names);

    @Query("DELETE FROM Vegitable WHERE name = :name AND quantity = :quantity")
    void deleteByNameAndQuantity(@Param("name") String name, @Param("quantity") Long quantity);

    @Query("DELETE FROM Vegitable WHERE name = :name OR quantity = :quantity")
    void deleteByNameOrQuantity(@Param("name") String name, @Param("quantity") Long quantity);

    @Query("UPDATE Vegitable SET name = :updated WHERE name = :name")
    void updateByNameEquals(@Param("updated") String updated,
                            @Param("name") String name);

    @Query("UPDATE Vegitable SET name = :updated WHERE name <> :name")
    void updateByNameNotEquals(@Param("updated") String updated,
                               @Param("name") String name);

    @Query("UPDATE Vegitable SET name = :updated WHERE quantity > :quantity")
    void updateByQuantityGreaterThan(@Param("updated") String updated,
                                     @Param("quantity") Long quantity);

    @Query("UPDATE Vegitable SET name = :updated WHERE quantity < :quantity")
    void updateByQuantityLessThan(@Param("updated") String updated,
                                  @Param("quantity") Long quantity);

    @Query("UPDATE Vegitable SET name = :updated WHERE quantity >= :quantity")
    void updateByQuantityGreaterThanEqual(@Param("updated") String updated,
                                          @Param("quantity") Long quantity);

    @Query("UPDATE Vegitable SET name = :updated WHERE quantity <= :quantity")
    void updateByQuantityLessThanEqual(@Param("updated") String updated,
                                       @Param("quantity") Long quantity);

    @Query("UPDATE Vegitable SET name = :updated WHERE quantity BETWEEN :start AND :end")
    void updateByQuantityBetween(@Param("updated") String updated,
                                 @Param("start") Long start,
                                 @Param("end") Long end);

    @Query("UPDATE Vegitable SET name = :updated WHERE name IN :names")
    void updateByNameIn(@Param("updated") String updated,
                        @Param("names") List<String> names);

    @Query("UPDATE Vegitable SET name = :updated WHERE name NOT IN :names")
    void updateByNameNotIn(@Param("updated") String updated,
                           @Param("names") List<String> names);

    @Query("UPDATE Vegitable SET name = :updated WHERE name = :name AND quantity = :quantity")
    void updateByNameAndQuantity(@Param("updated") String updated,
                                 @Param("name") String name,
                                 @Param("quantity") Long quantity);

    @Query("UPDATE Vegitable SET name = :updated WHERE name = :name OR quantity = :quantity")
    void updateByNameOrQuantity(@Param("updated") String updated,
                                @Param("name") String name,
                                @Param("quantity") Long quantity);

}
