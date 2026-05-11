/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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
package ee.jakarta.tck.data.standalone.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;

import ee.jakarta.tck.data.framework.junit.anno.AnyEntity;
import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.ReadOnlyTest;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.framework.read.only.Countries;
import ee.jakarta.tck.data.framework.read.only.Country;
import ee.jakarta.tck.data.framework.read.only.CountryPopulator;
import ee.jakarta.tck.data.framework.read.only.Region;
import ee.jakarta.tck.data.framework.read.only._Country;
import ee.jakarta.tck.data.framework.utilities.DatabaseType;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import jakarta.data.Order;
import jakarta.data.constraint.Like;
import jakarta.inject.Inject;

/**
 * Tests for the First annotation on repository @Find and @Query methods.
 */
@AnyEntity
@ReadOnlyTest
@Standalone
public class FirstTests {

    public static final Logger log =
            Logger.getLogger(FirstTests.class.getCanonicalName());

    @Inject
    Countries countries;

    // Inject doesn't happen until after BeforeClass, so this is necessary
    // before each test
    @BeforeEach
    public void beforeEach() {
        CountryPopulator.get().populate(countries);
    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(FirstTests.class);
    }

    private final DatabaseType type =
            TestProperty.databaseType.getDatabaseType();

    @Assertion(id = "532", strategy = """
            Use @First annotation without a value (defaults to 1) on a @Find
            method with @OrderBy to retrieve the first result. Verify that
            exactly one result is returned and it is the first according to
            the sort order.
            """)
    public void testFindFirstDefaulted() {
        Country found;
        try {
            found = countries.mostPopulous();
        } catch (UnsupportedOperationException x) {
            if (type.capableOfQueryWithoutWhere() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals("IN", found.getCode());
        assertEquals("India", found.getName());
    }

    @Assertion(id = "532", strategy = """
            Use @First annotation defaulting to a value of 1 on a @Find method
            with a given Order to retrieve the first result as a List. Verify
            that the resulting List contains exactly one result and it is the
            first according to the sort order.
            """)
    public void testFindFirstEntityAsList() {
        List<Country> found;
        try {
            found = countries.firstOf(Order.by(_Country.region.asc(),
                                               _Country.area.desc()));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfQueryWithoutWhere() &&
                type.capableOfMultipleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("DZ: Algeria"),
        found.stream()
             .map(c -> c.getCode() + ": " + c.getName())
             .toList());
    }

    @Assertion(id = "532", strategy = """
            Use @First annotation with value 15 on a @Find method with Order
            and constraint parameters to retrieve the first 15 results from a
            filtered set. Verify that exactly 15 results are returned in the
            correct sort order from the filtered results.
            """)
    public void testFindFirstFifteenWithConstraint() {
        List<Country> found;
        try {
            found = countries.largest15Matching(Like.suffix("ia"),
                                                Order.by(_Country.region.asc(),
                                                         _Country.code.asc()));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfLike() &&
                type.capableOfMultipleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("DZ: Algeria",
                             "ET: Ethiopia",
                             "GM: Gambia",
                             "LR: Liberia",
                             "MR: Mauritania",
                             "NA: Namibia",
                             "NG: Nigeria",
                             "SO: Somalia",
                             "TN: Tunisia",
                             "TZ: Tanzania",
                             "ZM: Zambia",
                             "AM: Armenia",
                             "GE: Georgia",
                             "ID: Indonesia",
                             "IN: India"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .toList());
    }

    @Assertion(id = "532", strategy = """
            Use @First annotation with value 4 on a @Find method with an
            Is(In) constraint on the Id attribute. Verify that exactly
            4 results are returned in the correct sort order.
            """)
    public void testFindFirstFour() {
        List<Country> found;
        try {
            found = countries.fourMostPopulousOf(List.of("JO",
                                                         "GR",
                                                         "SS",
                                                         "ZW",
                                                         "HT",
                                                         "RO"));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfIn() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("RO: Romania",
                             "ZW: Zimbabwe",
                             "SS: South Sudan",
                             "HT: Haiti"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .toList());
    }

    @Assertion(id = "532", strategy = """
            Use @First annotation with value 1 on a @Find method with a given
            Sort to retrieve the first result. Verify that exactly one result
            is returned and it is the first according to the sort order.
            """)
    public void testFindFirstOne() {
        Country found;
        try {
            found = countries.leastBy(_Country.area.asc())
                             .orElseThrow();
        } catch (UnsupportedOperationException x) {
            if (type.capableOfQueryWithoutWhere() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals("VA", found.getCode());
        assertEquals("Vatican City State", found.getName());
    }

    @Assertion(id = "532", strategy = """
            Use @First annotation with value 3 on a @Find method with @OrderBy
            to retrieve the first 3 results. Verify that exactly 3 results are
            returned in the correct sort order.
            """)
    public void testFindFirstThree() {
        Country[] found;
        try {
            found = countries.alphabetizedAfter("Dominican Republic");
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfGreaterThan() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("TL: East Timor",
                             "EC: Ecuador",
                             "EG: Egypt"),
                     Stream.of(found)
                          .map(c -> c.getCode() + ": " + c.getName())
                          .toList());
    }

    @Assertion(id = "532", strategy = """
            Use @First annotation with value 4 on a @Find method, but
            where only 2 results match. Verify that exactly 2 results are
            returned in the correct sort order.
            """)
    public void testFindTwoOfFirstFourMax() {
        List<Country> found;
        try {
            found = countries.fourMostPopulousOf(List.of("PA",
                                                         "PH"));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfIn() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("PH: Philippines",
                             "PA: Panama"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .toList());
    }

    @Assertion(id = "532", strategy = """
            Use @First annotation on a @Query method with ORDER BY clause
            to retrieve the first result. Verify that exactly one result is
            returned and it is the first according to the sort order.
            """)
    public void testQueryFirst() {
        Country found;
        try {
            found = countries.largestByArea()
                             .orElseThrow();
        } catch (UnsupportedOperationException x) {
            if (type.capableOfNotNull() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals("RU", found.getCode());
        assertEquals("Russia", found.getName());
    }

    @Assertion(id = "532", strategy = """
            Use @First annotation with value 5 on a @Query method with ORDER BY
            to retrieve the first 5 results. Verify that exactly 5 results are
            returned in the correct sort order.
            """)
    public void testQueryFirstFive() {
        List<Country> found;
        try {
            found = countries.mostPopulousIn(Region.SOUTH_AMERICA);
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("BR: Brazil",
                             "CO: Colombia",
                             "AR: Argentina",
                             "PE: Peru",
                             "VE: Venezuela"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .toList());
    }

    @Assertion(id = "532", strategy = """
            Use @First annotation on a @Query method with  clause
            to retrieve the first result. Verify that exactly one result is
            returned and it is the first according to the sort order.
            """)
    public void testQueryFirstNoneFound() {
        Optional<String> found;
        try {
            found = countries.nextCode("ZW");
        } catch (UnsupportedOperationException x) {
            if (type.capableOfGreaterThan() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(true, found.isEmpty());
    }

}
