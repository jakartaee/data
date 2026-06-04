/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.framework.read.only.qbmn;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Repository;
import ee.jakarta.tck.data.framework.read.only.AsciiCharacter;

/**
 * Query-by-method-name version of AsciiCharacters repository.
 * This repository uses method name derivation for query logic.
 */
@Repository
public interface AsciiCharactersByName extends DataRepository<AsciiCharacter, Long> {

    long countByIdBetween(long minimum, long maximum);

    long countByHexadecimalNotNull();

    boolean existsById(long id);

    boolean existsByThisCharacter(char ch);

    List<AsciiCharacter> findByHexadecimalContainsAndIsControlNot(String substring, boolean isPrintable);

    Stream<AsciiCharacter> findByHexadecimalIgnoreCaseBetweenAndHexadecimalNotIn(String minHex,
                                                                                 String maxHex,
                                                                                 Set<String> excludeHex,
                                                                                 Order<AsciiCharacter> sorts);

    AsciiCharacter findByHexadecimalIgnoreCase(String hex);

    Stream<AsciiCharacter> findByIdBetween(long minimum, long maximum, Sort<AsciiCharacter> sort);

    AsciiCharacter findByIsControlTrueAndNumericValueBetween(int min, int max);

    Optional<AsciiCharacter> findByNumericValue(int id);

    Page<AsciiCharacter> findByNumericValueBetween(int min, int max, PageRequest pagination, Order<AsciiCharacter> order);

    List<AsciiCharacter> findByNumericValueLessThanEqualAndNumericValueGreaterThanEqual(int max, int min);

    AsciiCharacter[] findFirst3ByNumericValueGreaterThanEqualAndHexadecimalEndsWith(int minValue, String lastHexDigit, Sort<AsciiCharacter> sort);

    Optional<AsciiCharacter> findFirstByHexadecimalStartsWithAndIsControlOrderByIdAsc(String firstHexDigit, boolean isControlChar);

    default Stream<AsciiCharacter> retrieveAlphaNumericIn(long minId, long maxId) {
        return findByIdBetween(minId, maxId, Sort.asc("id"))
                .filter(c -> Character.isLetterOrDigit(c.getThisCharacter()));
    }
}
