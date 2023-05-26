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

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.KeysetAwareSlice;
import jakarta.data.repository.Limit;
import jakarta.data.repository.Pageable;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Slice;
import jakarta.data.repository.Sort;

import ee.jakarta.tck.data.framework.read.only.NaturalNumber.NumberType;

/**
 * This is a read only repository that represents the set of Natural Numbers from 1-100.
 * This repository will be pre-populated at test startup and verified prior to running tests.
 *
 * TODO figure out a way to make this a ReadOnlyRepository instead.
 */
@Repository
public interface NaturalNumbers extends CrudRepository<NaturalNumber, Long> {

    KeysetAwareSlice<NaturalNumber> findByFloorOfSquareRootOrderByIdAsc(long sqrtFloor,
                                                                        Pageable pagination);

    Stream<NaturalNumber> findByIdBetween(long minimum, long maximum, Sort sort);

    Stream<NaturalNumber> findByIdBetweenOrderByNumTypeAsc(long minimum,
                                                           long maximum,
                                                           Sort... sorts);

    Collection<NaturalNumber> findByIdGreaterThanEqual(long minimum,
                                                       Limit limit,
                                                       Sort... sorts);

    NaturalNumber[] findByIdLessThan(long exclusiveMax, Sort primarySort, Sort secondarySort);

    ArrayList<NaturalNumber> findByIdLessThanEqual(long maximum, Sort... sorts);

    Slice<NaturalNumber> findByIdLessThanOrderByFloorOfSquareRootDesc(long exclusiveMax,
                                                                      Pageable pagination);

    KeysetAwareSlice<NaturalNumber> findByNumTypeAndNumBitsRequiredLessThan(NumberType type,
                                                                            short bitsUnder,
                                                                            Pageable pagination);

    Slice<NaturalNumber> findByNumTypeAndFloorOfSquareRootLessThanEqual(NumberType type,
                                                                        long maxSqrtFloor,
                                                                        Pageable pagination);

}
