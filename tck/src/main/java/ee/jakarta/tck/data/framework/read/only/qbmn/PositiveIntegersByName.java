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
import java.util.Set;
import java.util.stream.Stream;

import ee.jakarta.tck.data.framework.read.only.NaturalNumber;
import ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType;
import jakarta.data.Limit;
import jakarta.data.Order;
import jakarta.data.page.CursoredPage;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.Repository;

/**
 * Query-by-method-name version of PositiveIntegers repository.
 * This repository uses method name derivation for query logic.
 */
@Repository
public interface PositiveIntegersByName extends BasicRepository<NaturalNumber, Long> {

    long countByIdLessThan(long number);

    boolean existsByIdGreaterThan(Long number);

    CursoredPage<NaturalNumber> findByFloorOfSquareRootNotAndIdLessThanOrderByNumBitsRequiredDesc(long excludeSqrt,
                                                                                                  long exclusiveMax,
                                                                                                  PageRequest pagination,
                                                                                                  Order<NaturalNumber> order);

    List<NaturalNumber> findByIsOddTrueAndIdLessThanEqualOrderByIdDesc(long max);

    List<NaturalNumber> findByIsOddFalseAndIdBetween(long min, long max);

    Stream<NaturalNumber> findByNumTypeInOrderByIdAsc(Set<NumberType> types, Limit limit);

    Stream<NaturalNumber> findByNumTypeOrFloorOfSquareRoot(NumberType type, long floor);
}
