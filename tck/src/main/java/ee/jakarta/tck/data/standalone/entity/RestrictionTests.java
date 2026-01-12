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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
import jakarta.inject.Inject;

/**
 * Tests for repository methods with a Restriction parameter.
 */
@AnyEntity
@ReadOnlyTest
@Standalone
public class RestrictionTests {

    public static final Logger log =
            Logger.getLogger(RestrictionTests.class.getCanonicalName());

    @Inject
    Countries countries;

    private boolean initialized = false;

    // Inject doesn't happen until after BeforeClass, so this is necessary
    // before each test
    @BeforeEach
    public void beforeEach() {
        assertNotNull(countries);
        if (!initialized) {
            CountryPopulator.get().populate(countries);
            initialized = true;
        }
    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(RestrictionTests.class);
    }

    private final DatabaseType type = TestProperty.databaseType.getDatabaseType();

    @Assertion(id = "829", strategy = """
            Supply an equalTo Restriction to a repository find method.
            """)
    public void testEqualToRestriction() {

        List<Country> found = countries.filter(_Country.code.equalTo("PL"));

        assertEquals(1,
                     found.size());

        Country country = found.get(0);

        assertEquals("PL",
                     country.getCode());
        assertEquals("Poland",
                     country.getName());
        assertEquals(Region.EUROPE,
                     country.getRegion());
        assertEquals(304255L,
                     country.getArea());
        assertEquals(38364679L,
                     country.getPopulation());
        assertEquals(1628000000000L,
                     country.getGdp());
        assertEquals(823768000000L,
                     country.getDebt());
        assertEquals(LocalDate.of(2025, Month.MARCH, 30),
                     country.getDaylightTimeBegins());
        assertEquals(LocalDate.of(2025, Month.OCTOBER, 26),
                     country.getDaylightTimeEnds());
        assertEquals("Warsaw",
                     country.getCapital().getName());
        assertEquals(1863056,
                     country.getCapital().getPopulation());
    }

    @Assertion(id = "829", strategy = """
                    Supply a greaterThanEqual Restriction to a repository
                    find method.
                    """)
    public void testGreaterThanEqualRestriction() {
        try {
            List<String> found = countries
                    .filter(_Country.code.greaterThanEqual("ZA"))
                            .stream()
                            .map(c -> c.getCode() + ": " + c.getName())
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("ZA: South Africa",
                                 "ZM: Zambia",
                                 "ZW: Zimbabwe"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // Key-Value databases might not be capable of >=
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "829", strategy = """
            Supply a lessThan Restriction to a repository
            find method.
            """)
    public void testLessThanRestriction() {
        try {
            List<String> found = countries
                    .filter(_Country.code.lessThan("AO"))
                    .stream()
                    .map(c -> c.getCode() + ": " + c.getName())
                    .sorted()
                    .collect(Collectors.toList());
            assertEquals(List.of(
                    "AD: Andorra",
                    "AE: United Arab Emirates",
                    "AF: Afghanistan",
                    "AG: Antigua and Barbuda",
                    "AL: Albania",
                    "AM: Armenia"),
                    found);
            } catch (UnsupportedOperationException x) {
                if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                    // Key-Value databases might not be capable of <
                } else {
                    throw x;
                }
            }
    }

    @Assertion(id = "829", strategy = """
            Supply a notEqualTo Restriction to a repository
            find method.
            """)
    public void testNotEqualToRestriction() {
        try {
            List<String> found = countries
                    .filter(_Country.code.notEqualTo("US"))
                    .stream()
                    .map(Country::getName)
                    .collect(Collectors.toList());

            assertEquals(CountryPopulator.EXPECTED_TOTAL - 1,
                         found.size());

            assertEquals(false,
                         found.contains("United States of America"));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // Key-Value databases might not be capable of <>
            } else {
                throw x;
            }
        }
    }

}
