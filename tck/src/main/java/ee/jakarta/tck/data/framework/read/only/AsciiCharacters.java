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
package ee.jakarta.tck.data.framework.read.only;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import jakarta.data.Sort;
import jakarta.data.Streamable;
import jakarta.data.page.Page;
import jakarta.data.page.Pageable;
import jakarta.data.repository.By;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;

/**
 * This is a read only repository that represents the set of AsciiCharacters from 0-256.
 * This repository will be pre-populated at test startup and verified prior to running tests.
 * This interface is required to inherit only from DataRepository in order to satisfy a TCK scenario.
 */
@Repository
public interface AsciiCharacters extends DataRepository<AsciiCharacter, Long>, IdOperations<AsciiCharacter> {

    int countByHexadecimalNotNull();

    boolean existsByThisCharacter(char ch);

    AsciiCharacter find(char thisCharacter);

    Optional<AsciiCharacter> find(@By("thisCharacter") char ch,
                                  @By("hexadecimal") String hex);

    Collection<AsciiCharacter> findByHexadecimalContainsAndIsControlNot(String substring, boolean isPrintable);

    Stream<AsciiCharacter> findByHexadecimalIgnoreCaseBetweenAndHexadecimalNotIn(String minHex,
                                                                                 String maxHex,
                                                                                 Set<String> excludeHex,
                                                                                 Sort... sorts);

    AsciiCharacter findByHexadecimalIgnoreCase(String hex);

    AsciiCharacter findByIsControlTrueAndNumericValueBetween(int min, int max);

    Optional<AsciiCharacter> findByNumericValue(int id);

    Page<AsciiCharacter> findByNumericValueBetween(int min, int max, Pageable pagination);

    Streamable<AsciiCharacter> findByNumericValueLessThanEqualAndNumericValueGreaterThanEqual(int max, int min);

    AsciiCharacter[] findFirst3ByNumericValueGreaterThanEqualAndHexadecimalEndsWith(long minValue, String lastHexDigit, Sort sort);

    Optional<AsciiCharacter> findFirstByHexadecimalStartsWithAndIsControlOrderByIdAsc(String firstHexDigit, boolean isControlChar);

    default Stream<AsciiCharacter> retrieveAlphaNumericIn(long minId, long maxId) {
        return findByIdBetween(minId, maxId, Sort.asc("id"))
                        .filter(c -> Character.isLetterOrDigit(c.getThisCharacter()));
    }

    @Save
    Iterable<AsciiCharacter> saveAll(Iterable<AsciiCharacter> characters);
}
