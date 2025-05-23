/**
 * Copyright (c) 2024,2025 Contributors to the Eclipse Foundation
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import ee.jakarta.tck.data.framework.read.only.AsciiCharacter;
import ee.jakarta.tck.data.framework.read.only.NaturalNumber;
import ee.jakarta.tck.data.framework.read.only._AsciiCharacter;
import ee.jakarta.tck.data.framework.read.only._NaturalNumber;
import jakarta.data.repository.By;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Select;

/**
 * A repository that performs operations on different types of entities.
 */
@Repository
public interface MultipleEntityRepo { // Do not add a primary entity type.

    // Methods for Box entity:

    @Insert
    Box[] addAll(Box... boxes);

    @Query("DELETE FROM Box")
    long removeAll();

    @Query("UPDATE Box SET length = length + ?1, width = width - ?1, height = height * ?2")
    long resizeAll(int lengthIncrementWidthDecrement, int heightFactor);

    // Methods for Coordinate entity:

    @Insert
    Coordinate create(Coordinate c);

    @Query("DELETE FROM Coordinate WHERE x > 0.0d AND y > 0.0f")
    long deleteIfPositive();

    @Query("DELETE FROM Coordinate WHERE x > 0.0d AND y > 0.0f")
    void deleteIfPositiveWithoutReturnRecords();

    @Query("UPDATE Coordinate SET x = :newX, y = y / :yDivisor WHERE id = :id")
    int move(UUID id, double newX, float yDivisor);

    @Find(AsciiCharacter.class)
    @Select(_AsciiCharacter.NUMERICVALUE)
    int valueOf(String hexadecimal);

    @Find(NaturalNumber.class)
    @Select("id")
    Stream<Long> withBitRequirementOf(Short numBitsRequired);

    @Find(NaturalNumber.class)
    @Select("id")
    List<Long> withTruncatedSqrt(@By(_NaturalNumber.FLOOROFSQUAREROOT) long floor);

    @Query("WHERE id = ?1")
    Optional<Coordinate> withUUID(UUID id);
}