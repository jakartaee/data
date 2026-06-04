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
package ee.jakarta.tck.data.framework.read.only;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.data.Limit;
import jakarta.data.Sort;
import jakarta.data.repository.By;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Find;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;
import jakarta.data.repository.Select;

/**
 * This is a read only repository that represents the set of AsciiCharacters
 * from 0-256. This repository will be pre-populated at test startup and
 * verified prior to running tests. This interface is required to inherit only
 * from DataRepository in order to satisfy a TCK scenario.
 * For query-by-method-name versions, see AsciiCharactersByName.
 */
@Repository
public interface AsciiCharacters extends DataRepository<AsciiCharacter, Long> {

    @Query("SELECT id WHERE id >= :inclusiveMin ORDER BY id ASC")
    List<Long> withIdEqualOrAbove(long inclusiveMin, Limit limit);

    @Query(" ")
        // it is valid to have a query with no clauses
    Stream<AsciiCharacter> all(Limit limit, Sort<?>... sort);

    @Query("ORDER BY id ASC")
    Stream<AsciiCharacter> alphabetic(Limit limit);

    @Find(NaturalNumber.class)
    CardinalNumber cardinalNumberOf(@By(_NaturalNumber.ID) long value);

    @Find(NaturalNumber.class)
    Stream<CardinalNumber> cardinalNumberStream(long floorOfSquareRoot);

    @Query("SELECT COUNT(THIS) WHERE hexadecimal IS NOT NULL")
    long countNonNullHex();

    @Find
    AsciiCharacter find(char thisCharacter);

    @Find
    Optional<AsciiCharacter> find(@By("thisCharacter") char ch,
                                  @By("hexadecimal") String hex);

    @Query("select thisCharacter where hexadecimal like '4_'" +
            " and hexadecimal not like '%0'" +
            " and thisCharacter not in ('E', 'G')" +
            " and id not between 72 and 78" +
            " order by id asc")
    Character[] getABCDFO();

    @Query("SELECT hexadecimal WHERE hexadecimal IS NOT NULL AND thisCharacter = ?1")
    Optional<String> hex(char ch);

    @Query("WHERE hexadecimal <> ' ORDER BY isn''t a keyword when inside a literal' AND hexadecimal IN ('4a', '4b', '4c', ?1)")
    Stream<AsciiCharacter> jklOr(String hex);

    @Query("SELECT thisCharacter ORDER BY id DESC")
    Character[] reverseAlphabetic(Limit limit);

    @Save
    List<AsciiCharacter> saveAll(List<AsciiCharacter> characters);

    @Query("SELECT COUNT(THIS) WHERE numericValue <= 97 AND numericValue >= 74")
    long twentyFour();

    @Find(NaturalNumber.class) // this is not the primary entity type
    @Select(_NaturalNumber.NUMTYPEORDINAL)
    @Select(_NaturalNumber.FLOOROFSQUAREROOT)
    @Select(_NaturalNumber.ID)
    List<WholeNumber> wholeNumbers(Short numBitsRequired);
}
