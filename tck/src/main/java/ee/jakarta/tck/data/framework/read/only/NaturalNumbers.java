/**
 * Copyright (c) 2023,2025 Contributors to the Eclipse Foundation
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

import static jakarta.data.repository.By.ID;

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
import jakarta.data.repository.By;
import jakarta.data.repository.Find;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Select;
import ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType;

/**
 * This is a read only repository that represents the set of Natural Numbers from 1-100.
 * This repository will be pre-populated at test startup and verified prior to running tests.
 *
 * TODO figure out a way to make this a ReadOnlyRepository instead.
 */
@Repository
public interface NaturalNumbers extends BasicRepository<NaturalNumber, Long>, IdOperations {

    long countAll();

    CursoredPage<NaturalNumber> findByFloorOfSquareRootOrderByIdAsc(long sqrtFloor,
                                                                    PageRequest pagination);

    Stream<NaturalNumber> findByIdBetweenOrderByNumTypeOrdinalAsc(long minimum,
                                                                  long maximum,
                                                                  Order<NaturalNumber> sorts);

    List<NaturalNumber> findByIdGreaterThanEqual(long minimum,
                                                 Limit limit,
                                                 Order<NaturalNumber> sorts);

    NaturalNumber[] findByIdLessThan(long exclusiveMax, Sort<NaturalNumber> primarySort, Sort<NaturalNumber> secondarySort);

    List<NaturalNumber> findByIdLessThanEqual(long maximum, Sort<?>... sorts);

    Page<NaturalNumber> findByIdLessThanOrderByFloorOfSquareRootDesc(long exclusiveMax,
                                                                     PageRequest pagination,
                                                                     Order<NaturalNumber> order);

    CursoredPage<NaturalNumber> findByNumTypeAndNumBitsRequiredLessThan(NumberType type,
                                                                        short bitsUnder,
                                                                        Order<NaturalNumber> order,
                                                                        PageRequest pagination);

    NaturalNumber[] findByNumTypeNot(NumberType notThisType, Limit limit, Order<NaturalNumber> sorts);

    Page<NaturalNumber> findByNumTypeAndFloorOfSquareRootLessThanEqual(NumberType type,
                                                                       long maxSqrtFloor,
                                                                       PageRequest pagination,
                                                                       Sort<NaturalNumber> sort);

    @Find
    NumberInfo infoByIdentifier(@By(_NaturalNumber.ID) long id);

    @Find
    NumberInfo[] infoByNumBitsNeeded(@By(_NaturalNumber.NUMBITSREQUIRED) Short bits);

    @Find
    Stream<NumberInfo> infoByOddness(@By(_NaturalNumber.IS_ODD) boolean isOdd);

    @Find
    List<NumberInfo> infoByParity(@By(_NaturalNumber.IS_ODD) boolean isOdd);

    @Find
    Optional<NumberInfo> infoIfFound(@By(ID) long id);

    @Find
    @OrderBy(ID)
    Page<NumberInfo> infoPaginated(@By(_NaturalNumber.IS_ODD) boolean isOdd,
                                   PageRequest pageReq);

    @Query("SELECT id WHERE isOdd = true AND id BETWEEN 21 AND ?1 ORDER BY id ASC")
    Page<Long> oddsFrom21To(long max, PageRequest pageRequest);

    @Query("WHERE isOdd = false AND numType = ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType.PRIME")
    Optional<NaturalNumber> two();

    @Find
    @Select({ _NaturalNumber.NUMTYPEORDINAL,
              _NaturalNumber.FLOOROFSQUAREROOT,
              _NaturalNumber.ID })
    List<WholeNumber> wholeNumberList(@By(_NaturalNumber.NUMTYPEORDINAL) int numType,
                                      Order<NaturalNumber> order);

    @Find
    @Select({ _NaturalNumber.NUMTYPEORDINAL,
              _NaturalNumber.FLOOROFSQUAREROOT,
              _NaturalNumber.ID })
    Optional<WholeNumber> wholeNumberOf(@By(ID) int id);

    @Find
    @Select({ _NaturalNumber.NUMTYPEORDINAL,
              _NaturalNumber.FLOOROFSQUAREROOT,
              _NaturalNumber.ID })
    WholeNumber[] wholeNumbers(@By(_NaturalNumber.FLOOROFSQUAREROOT) int floorOfSquareRoot,
                               Order<NaturalNumber> order);

    @Find
    @Select({ _NaturalNumber.NUMTYPEORDINAL,
              _NaturalNumber.FLOOROFSQUAREROOT,
              _NaturalNumber.ID })
    Page<WholeNumber> wholeNumberPage(@By(_NaturalNumber.NUMTYPEORDINAL) int numType,
                                      PageRequest pageReq,
                                      Order<NaturalNumber> order);
}
