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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;

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
import ee.jakarta.tck.data.framework.read.only._Country;
import ee.jakarta.tck.data.framework.utilities.DatabaseType;
import ee.jakarta.tck.data.framework.utilities.TestProperty;
import jakarta.data.Order;
import jakarta.inject.Inject;

/**
 * Tests for ordering by nullable entity attributes with null ordering control.
 */
@AnyEntity
@ReadOnlyTest
@Standalone
public class SortNullableTests {

    public static final Logger log =
            Logger.getLogger(SortNullableTests.class.getCanonicalName());

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
                .addClasses(SortNullableTests.class);
    }

    private final DatabaseType type =
            TestProperty.databaseType.getDatabaseType();

    /**
     * Deteriministically orders the portion of results that might otherwise
     * appear in any order because the getter method represented by the given
     * entity attribute accessor for primary sort returns the same value.
     * This method applies a secondary sort using the given entity attribute
     * accessor for secondary sort to ensure values.
     *
     * @param found     results found by test case.
     * @param primary   getter method of Country by which results are already
     *                      sorted.
     * @param secondary alternative getter method by which to sort when the
     *                      primary matches. The combination of the primary
     *                      and secondary must result in a consistent order.
     * @return deterministically sorted results.
     */
    List<Country> sortConsecutiveMatches(List<Country> found,
                                         Function<Country, Object> primary,
                                         Function<Country, String> secondary) {
        List<Country> sorted = new ArrayList<>(found.size());
        List<Country> consecutive = new ArrayList<>();
        Object mostRecentPrimary = null;

        for (Country country : found) {
            Object currentPrimary = primary.apply(country);
            if (!Objects.equals(currentPrimary, mostRecentPrimary)) {
                sorted.addAll(consecutive.stream()
                                         .sorted(Comparator.comparing(secondary))
                                         .toList());
                consecutive = new ArrayList<>();
            }
            consecutive.add(country);
            mostRecentPrimary = currentPrimary;
        }
        sorted.addAll(consecutive.stream()
                                 .sorted(Comparator.comparing(secondary))
                                 .toList());
        if (sorted.size() != found.size())
            throw new RuntimeException("Found " + found.stream().map(Country::getCode).toList() +
                    " but lost or gained switching to " + sorted.stream().map(Country::getCode).toList());
        return sorted;
    }

    @Assertion(id = "1416", strategy = """
            Sort by a nullable entity attribute in
            ascending order with null values ordered first. Verify that
            all null values appear before non-null values, and that
            non-null values are in ascending order.
            """)
    public void testAscendingNullsFirst() {
        List<Country> found;
        try {
            found = countries.findLargest(
                    50_000_000L,
                    Order.by(_Country.daylightTimeBegins.asc().nullsFirst()));
        } catch (IllegalArgumentException x) {
            if (type.capableOfSortingNulls()) {
                throw x;
            } else {
                return;
            }
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfGreaterThanEqual()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("BD: Bangladesh",
                             "BR: Brazil",
                             "CD: Congo",
                             "CN: China",
                             "ET: Ethiopia",
                             "ID: Indonesia",
                             "IN: India",
                             "IR: Iran",
                             "JP: Japan",
                             "KE: Kenya",
                             "KR: South Korea",
                             "MM: Burma",
                             "NG: Nigeria",
                             "PH: Philippines",
                             "PK: Pakistan",
                             "RU: Russia",
                             "SD: Sudan",
                             "TH: Thailand",
                             "TR: Turkey",
                             "TZ: Tanzania",
                             "UG: Uganda",
                             "VN: Vietnam",
                             "ZA: South Africa",
                             "MX: Mexico",
                             "US: United States of America",
                             "DE: Germany",
                             "FR: France",
                             "GB: United Kingdom",
                             "IT: Italy",
                             "EG: Egypt"),
                     sortConsecutiveMatches(found,
                                            Country::getDaylightTimeBegins,
                                            Country::getCode)
                             .stream()
                             .map(c -> c.getCode() + ": " + c.getName())
                             .toList());
    }

    @Assertion(id = "1416", strategy = """
            Sort by a nullable entity attribute in
            ascending order with null values ordered last. Verify that
            all non-null values appear before null values, and that
            non-null values are in ascending order.
            """)
    public void testAscendingNullsLast() {
        List<Country> found;
        try {
            found = countries.findLargest(
                    50_000_000L,
                    Order.by(_Country.daylightTimeBegins.asc().nullsLast()));
        } catch (IllegalArgumentException x) {
            if (type.capableOfSortingNulls()) {
                throw x;
            } else {
                return;
            }
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfGreaterThanEqual()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("MX: Mexico",
                             "US: United States of America",
                             "DE: Germany",
                             "FR: France",
                             "GB: United Kingdom",
                             "IT: Italy",
                             "EG: Egypt",
                             "BD: Bangladesh",
                             "BR: Brazil",
                             "CD: Congo",
                             "CN: China",
                             "ET: Ethiopia",
                             "ID: Indonesia",
                             "IN: India",
                             "IR: Iran",
                             "JP: Japan",
                             "KE: Kenya",
                             "KR: South Korea",
                             "MM: Burma",
                             "NG: Nigeria",
                             "PH: Philippines",
                             "PK: Pakistan",
                             "RU: Russia",
                             "SD: Sudan",
                             "TH: Thailand",
                             "TR: Turkey",
                             "TZ: Tanzania",
                             "UG: Uganda",
                             "VN: Vietnam",
                             "ZA: South Africa"),
                     sortConsecutiveMatches(found,
                                            Country::getDaylightTimeBegins,
                                            Country::getCode)
                             .stream()
                             .map(c -> c.getCode() + ": " + c.getName())
                             .toList());
    }

    @Assertion(id = "1416", strategy = """
            Sort by a nullable entity attribute in
            descending order with null values ordered first. Verify that
            all null values appear before non-null values, and that
            non-null values are in descending order.
            """)
    public void testDescendingNullsFirst() {
        List<Country> found;
        try {
            found = countries.findLargest(
                    50_000_000L,
                    Order.by(_Country.daylightTimeEnds.desc().nullsFirst()));
        } catch (IllegalArgumentException x) {
            if (type.capableOfSortingNulls()) {
                throw x;
            } else {
                return;
            }
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfGreaterThanEqual()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("BD: Bangladesh",
                             "BR: Brazil",
                             "CD: Congo",
                             "CN: China",
                             "ET: Ethiopia",
                             "ID: Indonesia",
                             "IN: India",
                             "IR: Iran",
                             "JP: Japan",
                             "KE: Kenya",
                             "KR: South Korea",
                             "MM: Burma",
                             "NG: Nigeria",
                             "PH: Philippines",
                             "PK: Pakistan",
                             "RU: Russia",
                             "SD: Sudan",
                             "TH: Thailand",
                             "TR: Turkey",
                             "TZ: Tanzania",
                             "UG: Uganda",
                             "VN: Vietnam",
                             "ZA: South Africa",
                             "MX: Mexico",
                             "US: United States of America",
                             "EG: Egypt",
                             "DE: Germany",
                             "FR: France",
                             "GB: United Kingdom",
                             "IT: Italy"),
                     sortConsecutiveMatches(found,
                                            Country::getDaylightTimeEnds,
                                            Country::getCode)
                             .stream()
                             .map(c -> c.getCode() + ": " + c.getName())
                             .toList());
    }

    @Assertion(id = "1416", strategy = """
            Sort by a nullable entity attribute in
            descending order with null values ordered last. Verify that
            all non-null values appear before null values, and that
            non-null values are in descending order.
            """)
    public void testDescendingNullsLast() {
        List<Country> found;
        try {
            found = countries.findLargest(
                    50_000_000L,
                    Order.by(_Country.daylightTimeEnds.desc().nullsLast()));
        } catch (IllegalArgumentException x) {
            if (type.capableOfSortingNulls()) {
                throw x;
            } else {
                return;
            }
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfGreaterThanEqual()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("MX: Mexico",
                             "US: United States of America",
                             "EG: Egypt",
                             "DE: Germany",
                             "FR: France",
                             "GB: United Kingdom",
                             "IT: Italy",
                             "BD: Bangladesh",
                             "BR: Brazil",
                             "CD: Congo",
                             "CN: China",
                             "ET: Ethiopia",
                             "ID: Indonesia",
                             "IN: India",
                             "IR: Iran",
                             "JP: Japan",
                             "KE: Kenya",
                             "KR: South Korea",
                             "MM: Burma",
                             "NG: Nigeria",
                             "PH: Philippines",
                             "PK: Pakistan",
                             "RU: Russia",
                             "SD: Sudan",
                             "TH: Thailand",
                             "TR: Turkey",
                             "TZ: Tanzania",
                             "UG: Uganda",
                             "VN: Vietnam",
                             "ZA: South Africa"),
                     sortConsecutiveMatches(found,
                                            Country::getDaylightTimeEnds,
                                            Country::getCode)
                             .stream()
                             .map(c -> c.getCode() + ": " + c.getName())
                             .toList());
    }

    @Assertion(id = "1416", strategy = """
            Sort by multiple attributes including a nullable
            attribute with null ordering specified, followed by non-nullable
            attributes. Verify that the primary sort (with null ordering)
            is applied correctly, and secondary sorts are applied within
            groups.
            """)
    public void testMultipleSortsWithNullOrdering() {
        List<Country> found;
        try {
            found = countries.findLargest(
                    50_000_000L,
                    Order.by(_Country.daylightTimeBegins.asc().nullsLast(),
                             _Country.population.desc(),
                             _Country.name.asc()));
        } catch (IllegalArgumentException x) {
            if (type.capableOfSortingNulls()) {
                throw x;
            } else {
                return;
            }
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfGreaterThanEqual() &&
                type.capableOfMultipleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("US: United States of America",
                             "MX: Mexico",
                             "DE: Germany",
                             "GB: United Kingdom",
                             "FR: France",
                             "IT: Italy",
                             "EG: Egypt",
                             "IN: India",
                             "CN: China",
                             "ID: Indonesia",
                             "PK: Pakistan",
                             "NG: Nigeria",
                             "BR: Brazil",
                             "BD: Bangladesh",
                             "RU: Russia",
                             "JP: Japan",
                             "ET: Ethiopia",
                             "PH: Philippines",
                             "CD: Congo",
                             "VN: Vietnam",
                             "IR: Iran",
                             "TR: Turkey",
                             "TH: Thailand",
                             "TZ: Tanzania",
                             "ZA: South Africa",
                             "MM: Burma",
                             "KE: Kenya",
                             "SD: Sudan",
                             "KR: South Korea",
                             "UG: Uganda"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .toList());
    }

    @Assertion(id = "1416", strategy = """
            Sort by a nullable attribute in ascending order with
            nulls first, combined with a secondary sort on a non-nullable
            attribute. Verify that null values are ordered first, followed
            by non-null values in ascending order, and that the secondary
            sort is applied correctly within each group.
            """)
    public void testNullsFirstWithSecondarySort() {
        List<Country> found;
        try {
            found = countries.findLargest(
                    50_000_000L,
                    Order.by(_Country.daylightTimeBegins.asc().nullsFirst(),
                             _Country.code.asc()));
        } catch (IllegalArgumentException x) {
            if (type.capableOfSortingNulls()) {
                throw x;
            } else {
                return;
            }
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfGreaterThanEqual() &&
                type.capableOfMultipleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("BD: Bangladesh",
                             "BR: Brazil",
                             "CD: Congo",
                             "CN: China",
                             "ET: Ethiopia",
                             "ID: Indonesia",
                             "IN: India",
                             "IR: Iran",
                             "JP: Japan",
                             "KE: Kenya",
                             "KR: South Korea",
                             "MM: Burma",
                             "NG: Nigeria",
                             "PH: Philippines",
                             "PK: Pakistan",
                             "RU: Russia",
                             "SD: Sudan",
                             "TH: Thailand",
                             "TR: Turkey",
                             "TZ: Tanzania",
                             "UG: Uganda",
                             "VN: Vietnam",
                             "ZA: South Africa",
                             "MX: Mexico",
                             "US: United States of America",
                             "DE: Germany",
                             "FR: France",
                             "GB: United Kingdom",
                             "IT: Italy",
                             "EG: Egypt"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .toList());
    }

    @Assertion(id = "1416", strategy = """
            Sort by a nullable attribute in descending order with
            nulls last, combined with a secondary sort on a non-nullable
            attribute. Verify that non-null values are ordered first in
            descending order, followed by null values, and that the
            secondary sort is applied correctly within each group.
            """)
    public void testNullsLastWithSecondarySort() {
        List<Country> found;
        try {
            found = countries.findLargest(
                    50_000_000L,
                    Order.by(_Country.daylightTimeEnds.desc().nullsLast(),
                             _Country.name.asc()));
        } catch (IllegalArgumentException x) {
            if (type.capableOfSortingNulls()) {
                throw x;
            } else {
                return;
            }
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfGreaterThanEqual() &&
                type.capableOfMultipleSort()) {
                throw x;
            } else {
                return;
            }
        }

        assertEquals(List.of("MX: Mexico",
                             "US: United States of America",
                             "EG: Egypt",
                             "FR: France",
                             "DE: Germany",
                             "IT: Italy",
                             "GB: United Kingdom",
                             "BD: Bangladesh",
                             "BR: Brazil",
                             "MM: Burma",
                             "CN: China",
                             "CD: Congo",
                             "ET: Ethiopia",
                             "IN: India",
                             "ID: Indonesia",
                             "IR: Iran",
                             "JP: Japan",
                             "KE: Kenya",
                             "NG: Nigeria",
                             "PK: Pakistan",
                             "PH: Philippines",
                             "RU: Russia",
                             "ZA: South Africa",
                             "KR: South Korea",
                             "SD: Sudan",
                             "TZ: Tanzania",
                             "TH: Thailand",
                             "TR: Turkey",
                             "UG: Uganda",
                             "VN: Vietnam"),
                     found.stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .toList());
    }

    @Assertion(id = "1416", strategy = """
            Sort by a nullable attribute without specifying
            null ordering (using UNSPECIFIED), allowing the data store
            to use its default behavior. Verify that the sort is applied
            and results are returned without error.
            """)
    public void testUnspecifiedNullOrdering() {
        List<Country> found;

        try {
            found = countries.findLargest(
                    50_000_000L,
                    Order.by(_Country.daylightTimeBegins.asc()));
            assertEquals(30, found.size());
        } catch (UnsupportedOperationException x) {
            if (type.capableOfConstraintsOnNonIdAttributes() &&
                type.capableOfGreaterThanEqual()) {
                throw x;
            } else {
                return;
            }
        }

        // When null ordering is unspecified, verify that non-null values
        // are in ascending order. The position of nulls is database-dependent.

        List<Country> withoutNulls = found
                .stream()
                .filter(c -> c.getDaylightTimeBegins() != null)
                .toList();

        assertEquals(List.of("MX: Mexico",
                             "US: United States of America",
                             "DE: Germany",
                             "FR: France",
                             "GB: United Kingdom",
                             "IT: Italy",
                             "EG: Egypt"),
                     sortConsecutiveMatches(withoutNulls,
                                  Country::getDaylightTimeBegins,
                                  Country::getCode)
                          .stream()
                          .map(c -> c.getCode() + ": " + c.getName())
                          .toList());
    }
}
