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
import java.util.stream.Stream;

import jakarta.data.Limit;
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.page.CursoredPage;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.Repository;
import ee.jakarta.tck.data.framework.read.only.NaturalNumber;
import ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType;

/**
 * Query-by-method-name version of NaturalNumbers repository.
 * This repository uses method name derivation for query logic.
 */
@Repository
public interface NaturalNumbersByName extends BasicRepository<NaturalNumber, Long> {

    long countAll();

    long countByIdBetween(long minimum, long maximum);

    boolean existsById(long id);

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
}
