/**
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import jakarta.data.Limit;
import jakarta.data.Order;
import jakarta.data.constraint.AtLeast;
import jakarta.data.constraint.AtMost;
import jakarta.data.constraint.Between;
import jakarta.data.constraint.EqualTo;
import jakarta.data.constraint.GreaterThan;
import jakarta.data.constraint.In;
import jakarta.data.constraint.LessThan;
import jakarta.data.constraint.Like;
import jakarta.data.constraint.NotEqualTo;
import jakarta.data.constraint.NotIn;
import jakarta.data.constraint.NotLike;
import jakarta.data.repository.By;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Find;
import jakarta.data.repository.Is;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Repository;

/**
 * This is a read only repository with statistics about countries.
 * This repository is pre-populated at test startup and verified prior
 * to running tests, which must not alter the data.
 */
@Repository
public interface Countries extends CrudRepository<Country, String> {

    long count();

    @Find
    List<Country> asLargeOrBigger(
            @By(_Country.AREA) @Is(AtLeast.class) long minSquareKM);

    @Find
    List<Country> byCountryCodes(
            @By(_Country.CODE) @Is(In.class) Collection<String> codes);

    @Find
    Stream<Country> inRegion(
            @By(_Country.REGION) @Is Region regionOfWorld);

    @Find
    @OrderBy(_Country.CODE)
    List<Country> namedLike(
            @By(_Country.NAME) @Is(Like.class) String pattern);

    @Find
    @OrderBy(_Country.NAME)
    Stream<Country> namedUnlike(
            @By(_Country.NAME) @Is(NotLike.class) String pattern1,
            @By(_Country.NAME) @Is(NotLike.class) String pattern2,
            @By(_Country.NAME) @Is(NotLike.class) String pattern3);

    @Find
    List<Country> namedUpTo(
            @Is(AtMost.class) String name);

    @Find
    List<Country> outsideOfRegion(
            @Is(NotEqualTo.class) Region region,
            Order<Country> order,
            Limit limit);

    @Find
    List<Country> outsideOfRegions(
            @By(_Country.REGION) @Is(NotIn.class) Collection<Region> excluded);

    @Find
    Stream<Country> populated(
            @By(_Country.POPULATION) Between<Long> range);

    @Find
    Stream<Country> smallerThan(
            @By(_Country.AREA) @Is(LessThan.class) long squareKM);

    @Find
    @OrderBy(_Country.NAME)
    List<Country> whereDaylightTimeEndsOn(
            @Is(EqualTo.class) LocalDate daylightTimeEnds);

    @Find
    List<Country> withNameAfter(
            @Is(GreaterThan.class) String name);

}