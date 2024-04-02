/**
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
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.page.CursoredPage;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType;

/**
 * This is a read only repository that represents the set of Natural Numbers from 1-100.
 * This repository will be pre-populated at test startup and verified prior to running tests.
 *
 * TODO figure out a way to make this a ReadOnlyRepository instead.
 */
@Repository
public interface NaturalNumbers extends BasicRepository<NaturalNumber, Long>, IdOperations {

    long countBy();

    CursoredPage<NaturalNumber> findByFloorOfSquareRootOrderByIdAsc(long sqrtFloor,
                                                                    PageRequest<NaturalNumber> pagination);

    Stream<NaturalNumber> findByIdBetweenOrderByNumTypeAsc(long minimum,
                                                           long maximum,
                                                           Order<NaturalNumber> sorts);

    List<NaturalNumber> findByIdGreaterThanEqual(long minimum,
                                                 Limit limit,
                                                 Order<NaturalNumber> sorts);

    NaturalNumber[] findByIdLessThan(long exclusiveMax, Sort<NaturalNumber> primarySort, Sort<NaturalNumber> secondarySort);

    List<NaturalNumber> findByIdLessThanEqual(long maximum, Sort<?>... sorts);

    Page<NaturalNumber> findByIdLessThanOrderByFloorOfSquareRootDesc(long exclusiveMax,
                                                                     PageRequest<NaturalNumber> pagination);

    CursoredPage<NaturalNumber> findByNumTypeAndNumBitsRequiredLessThan(NumberType type,
                                                                        short bitsUnder,
                                                                        PageRequest<NaturalNumber> pagination);

    NaturalNumber[] findByNumTypeNot(NumberType notThisType, Limit limit, Order<NaturalNumber> sorts);

    Page<NaturalNumber> findByNumTypeAndFloorOfSquareRootLessThanEqual(NumberType type,
                                                                       long maxSqrtFloor,
                                                                       PageRequest<NaturalNumber> pagination);

    @Query("SELECT id WHERE isOdd = true AND id BETWEEN 21 AND ?1 ORDER BY id ASC")
    Page<Long> oddsFrom21To(long max, PageRequest<NaturalNumber> pageRequest);

    @Query("WHERE isOdd = false AND numType = ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType.PRIME")
    Optional<NaturalNumber> two();
}
