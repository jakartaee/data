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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
import ee.jakarta.tck.data.framework.read.only.NaturalNumber;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbers;
import ee.jakarta.tck.data.framework.read.only.NaturalNumbersPopulator;
import ee.jakarta.tck.data.framework.read.only._NaturalNumber;
import ee.jakarta.tck.data.framework.utilities.DatabaseType;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import jakarta.data.Limit;
import jakarta.data.Order;
import jakarta.inject.Inject;

/**
 * Tests for the Limit special parameter on repository @Find and @Query
 * methods.
 */
@AnyEntity
@ReadOnlyTest
@Standalone
public class LimitTests {

    public static final Logger log =
            Logger.getLogger(LimitTests.class.getCanonicalName());

    @Inject
    Countries countries;

    @Inject
    NaturalNumbers numbers;

    // Inject doesn't happen until after BeforeClass, so this is necessary
    // before each test
    @BeforeEach
    public void beforeEach() {
        CountryPopulator.get().populate(countries);
        NaturalNumbersPopulator.get().populate(numbers);
    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                         .addClasses(LimitTests.class);
    }

    private final DatabaseType type =
            TestProperty.databaseType.getDatabaseType();

    @Assertion(id = "133", strategy = """
            Use a repository Find method with both Sort and Limit,
            and verify that the Limit caps the number of results
            and that results are ordered according to the sort
            criteria.
            """)
    public void testLimit() {
        Collection<NaturalNumber> nums;
        try {
            nums = numbers.atLeast(
                    60L,
                    Limit.of(10),
                    Order.by(
                            _NaturalNumber.floorOfSquareRoot.asc(),
                            _NaturalNumber.id.desc()));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfGreaterThanEqual() &&
                type.capableOfMultipleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of(
                        63L, 62L, 61L, 60L, // √ rounds down to 7
                        80L, 79L, 78L, 77L, 76L, 75L // √ rounds down to 8
                     ),
                     nums.stream()
                         .map(NaturalNumber::getId)
                         .toList());
    }

    @Assertion(id = "133", strategy = """
            Create a Limit that is relative to a 0-based offset.
            Supply the Limit to a repository method and verify
            that the Limit caps the number of results, starting
            from the offset, and orders according to the sort
            criteria.
            """)
    public void testLimitFromOffset() {
        Collection<NaturalNumber> nums;
        try {
            nums = numbers.atLeast(
                    20L,
                    Limit.of(8, 24),
                    Order.by(_NaturalNumber.id.asc()));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfGreaterThanEqual() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of(44L, 45L, 46L, 47L, 48L, 49L, 50L, 51L),
                     nums.stream()
                         .map(NaturalNumber::getId)
                         .toList());
    }

    @Assertion(id = "133", strategy = """
            Create a Limit that is relative an offset of zero.
            Supply the Limit to a repository method and verify
            that the Limit caps the number of results, starting
            from the beginning, and orders according to the sort
            criteria.
            """)
    public void testLimitFromZeroOffset() {
        Collection<NaturalNumber> nums;
        try {
            nums = numbers.atLeast(
                    10L,
                    Limit.of(6, 0),
                    Order.by(_NaturalNumber.id.asc()));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfGreaterThanEqual() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of(10L, 11L, 12L, 13L, 14L, 15L),
                     nums.stream()
                         .map(NaturalNumber::getId)
                         .toList());
    }

    @Assertion(id = "133", strategy = """
            Use a repository Find method with both Sort and Limit,
            where the Limit is a range, and verify that the
            Limit range starts in the correct place, caps the
            number of results, and that results are ordered
            according to the sort criteria.
            """)
    public void testLimitedRange() {
        // Primes above 40 are:
        // 41, 43, 47, 53, 59,
        // 61, 67, 71, 73, 79,
        // 83, 89, ...

        Collection<NaturalNumber> nums;
        try {
            nums = numbers.atLeast(
                    40L,
                    Limit.range(6, 10),
                    Order.by(
                            _NaturalNumber.numTypeOrdinal.asc(), // primes first
                            _NaturalNumber.id.asc()));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfGreaterThanEqual() &&
                type.capableOfMultipleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of(61L, 67L, 71L, 73L, 79L),
                     nums.stream()
                         .map(NaturalNumber::getId)
                         .toList());
    }

    @Assertion(id = "133", strategy = """
            Use a repository Find method with Limit and verify
            that the Limit caps the number of results
            to the amount (1) that is specified.""")
    public void testLimitToOneResult() {
        Collection<NaturalNumber> nums;
        try {
            nums = numbers.atLeast(80L, Limit.of(1), Order.by());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfGreaterThanEqual()) {
                throw x;
            } else {
                return;
            }
        }
        Iterator<NaturalNumber> it = nums.iterator();
        assertEquals(true, it.hasNext());

        NaturalNumber num = it.next();
        assertEquals(true, num.getId() >= 80L);

        assertEquals(false, it.hasNext());
    }

    @Assertion(id = "133", strategy = """
            Create a Limit that is relative an offset of zero.
            Supply the Limit to a repository Query method and
            verify that the Limit caps the number of results,
            starting from the beginning, and orders according
            to the sort criteria.
            """)
    public void testQueryWithLimitFromOffset() {
        Stream<Country> found;
        try {
            found = countries.excludingCode("VC",
                                            Limit.of(6, 7));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfNotIn() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("VU: Vanuatu",
                             "VN: Vietnam",
                             "VE: Venezuela",
                             "VA: Vatican City State",
                             "UZ: Uzbekistan",
                             "UY: Uruguay"),
                     found
                             .map(c -> c.getCode() + ": " + c.getName())
                             .toList());
    }

    @Assertion(id = "133", strategy = """
            Create a Limit that is relative an offset of zero.
            Supply the Limit to a repository Query method and
            verify that the Limit caps the number of results,
            starting from the beginning, and orders according
            to the sort criteria.
            """)
    public void testQueryWithLimitFromZeroOffset() {
        Stream<Country> found;
        try {
            found = countries.excludingCode("ZA",
                                            Limit.of(3, 0));
        } catch (UnsupportedOperationException x) {
            if (type.capableOfNotIn() &&
                type.capableOfSingleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("ZW: Zimbabwe",
                             "ZM: Zambia",
                             "YE: Yemen"),
                     found
                             .map(c -> c.getCode() + ": " + c.getName())
                             .toList());
    }
}
