/**
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

import java.util.Set;
import java.util.stream.Stream;

import ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType;
import jakarta.data.Limit;
import jakarta.data.Streamable;
import jakarta.data.page.KeysetAwarePage;
import jakarta.data.page.Pageable;
import jakarta.data.repository.PageableRepository;
import jakarta.data.repository.Repository;

/**
 * This is a read only repository that shares the same data (and entity type)
 * as the NaturalNumbers repository: the positive integers 1-100.
 * This repository is pre-populated at test startup and verified prior to running tests.
 */
@Repository
public interface PositiveIntegers extends PageableRepository<NaturalNumber, Long> {
    long countByIdLessThan(long number);

    boolean existsByIdGreaterThan(Long number);

    KeysetAwarePage<NaturalNumber> findByFloorOfSquareRootNotAndIdLessThanOrderByBitsRequiredDesc(long excludeSqrt,
                                                                                                  long eclusiveMax,
                                                                                                  Pageable pagination);

    Iterable<NaturalNumber> findByIsOddTrueAndIdLessThanEqualOrderByIdDesc(long max);

    Streamable<NaturalNumber> findByIsOddFalseAndIdBetween(long min, long max);

    Stream<NaturalNumber> findByNumTypeInOrderByIdAsc(Set<NumberType> types, Limit limit);

    Stream<NaturalNumber> findByNumTypeOrFloorOfSquareRoot(NumberType type, long floor);
}