/*
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
package ee.jakarta.tck.data.standalone.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;
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
import jakarta.data.Limit;
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.constraint.Between;
import jakarta.inject.Inject;

/**
 * Execute a test with an entity that is dual annotated which means
 * this test can run against a provider that supports any Entity type.
 */
@AnyEntity
@ReadOnlyTest
@Standalone
public class ConstraintTests {

    public static final Logger log = Logger.getLogger(ConstraintTests.class.getCanonicalName());

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
                .addClasses(ConstraintTests.class);
    }

    private final DatabaseType type = TestProperty.databaseType.getDatabaseType();

    @Assertion(id = "965", strategy = """
                    Use a repository find method that has a constraint
                    parameter of type Between.
                    """)
    public void testBetweenConstraint() {

        try {
            List<String> found = countries.populated(Between.bounds(80000000L,
                                                                    90000000L))
                            .map(Country::getName)
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("Germany",
                                 "Iran",
                                 "Turkey"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // Key-Value databases might not be capable of Between.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
                    Use a repository find method that has a parameter
                    annotated with Is, specifying the AtLeast constraint.
                    """)
    public void testIsAtLeast() {

        try {
            List<String> found = countries.asLargeOrBigger(9326410L)
                            .stream()
                            .map(Country::getName)
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("China",
                                 "Russia"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // Key-Value databases might not be capable of >=
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
                    Use a repository find method that has a parameter
                    annotated with Is, specifying the AtMost constraint.
                    """)
    public void testIsAtMost() {

        try {
            List<String> found = countries.namedUpTo("Algeria")
                            .stream()
                            .map(Country::getName)
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("Afghanistan",
                                 "Albania",
                                 "Algeria"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // Key-Value databases might not be capable of <=
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
                    Use a repository find method that has a parameter
                    annotated with Is, but not specifying any constraint,
                    which must default to the EqualTo constraint.
                    """)
    public void testIsDefaultConstraint() {

        List<String> found = countries.inRegion(Region.SOUTH_AMERICA)
                        .map(Country::getName)
                        .sorted()
                        .collect(Collectors.toList());
        assertEquals(List.of("Argentina",
                             "Bolivia",
                             "Brazil",
                             "Chile",
                             "Colombia",
                             "Ecuador",
                             "Guyana",
                             "Paraguay",
                             "Peru",
                             "Suriname",
                             "Uruguay",
                             "Venezuela"),
                     found);
    }

    @Assertion(id = "965", strategy = """
                    Use a repository find method that has a parameter
                    annotated with Is, specifying the EqualTo constraint.
                    """)
    public void testIsEqualTo() {

        LocalDate endDate = LocalDate.of(2025, Month.NOVEMBER, 2);
        try {
            List<String> found = countries.whereDaylightTimeEndsOn(endDate)
                            .stream()
                            .map(Country::getName)
                            .collect(Collectors.toList());
            assertEquals(List.of("Bahamas",
                                 "Bermuda",
                                 "Canada",
                                 "Cuba",
                                 "Greenland",
                                 "Haiti",
                                 "Mexico",
                                 "Saint Pierre and Miquelon",
                                 "Turks and Caicos Islands",
                                 "United States of America"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
                    Use a repository find method that has a parameter
                    annotated with Is, specifying the GreaterThan constraint.
                    """)
    public void testIsGreaterThan() {

        try {
            List<String> found = countries.withNameAfter("Venezuela")
                            .stream()
                            .map(Country::getName)
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("Vietnam",
                                 "Yemen",
                                 "Zambia",
                                 "Zimbabwe"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // Key-Value databases might not be capable of >
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
                    Use a repository find method that has a parameter
                    annotated with Is, specifying the In constraint.
                    """)
    public void testIsIn() {

        List<String> found = countries.byCountryCodes(Set.of("BW",
                                                             "HR",
                                                             "IQ",
                                                             "KR",
                                                             "TZ",
                                                             "UA"))
                        .stream()
                        .map(Country::getName)
                        .sorted()
                        .collect(Collectors.toList());
        assertEquals(List.of("Botswana",
                             "Croatia",
                             "Iraq",
                             "South Korea",
                             "Tanzania",
                             "Ukraine"),
                     found);
    }

    @Assertion(id = "965", strategy = """
                    Use a repository find method that has a parameter
                    annotated with Is, specifying the LessThan constraint.
                    """)
    public void testIsLessThan() {

        try {
            List<String> found = countries.smallerThan(180L)
                            .map(Country::getName)
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("Bermuda",
                                 "Guernsey",
                                 "Jersey",
                                 "Liechtenstein",
                                 "Macau",
                                 "Monaco",
                                 "Nauru",
                                 "San Marino",
                                 "Tuvalu",
                                 "Vatican City State"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // Key-Value databases might not be capable of <
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
                    Use a repository find method that has a parameter
                    annotated with Is, specifying the Like constraint.
                    """)
    public void testIsLike() {

        try {
            List<String> found = countries.namedLike("I_eland")
                            .stream()
                            .map(Country::getName)
                            .collect(Collectors.toList());
            assertEquals(List.of("Ireland", // country code IE
                                 "Iceland"), // country code IS
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of Like.
                // Column and Key-Value databases might not be capable of sorting.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
                    Use a repository find method that has a parameter
                    annotated with Is, specifying the NotEqualTo constraint.
                    """)
    public void testIsNotEqualTo() {

        Order<Country> sorts = Order.by(Sort.desc(_Country.AREA),
                                        Sort.asc(_Country.CODE));
        List<String> found;
        try {
            found = countries.outsideOfRegion(Region.NORTH_AMERICA,
                                              sorts,
                                              Limit.of(15))
                            .stream()
                            .map(Country::getName)
                            .collect(Collectors.toList());
            assertEquals(List.of("Russia",
                                 "China",
                                 "Brazil",
                                 "Australia",
                                 "India",
                                 "Argentina",
                                 "Kazakhstan",
                                 "Algeria",
                                 "Congo",
                                 "Saudi Arabia",
                                 "Sudan",
                                 "Indonesia",
                                 "Libya",
                                 "Mongolia",
                                 "Iran"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.COLUMN)) {
                // Column and Key-Value databases might not be capable of sorting.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
                    Use a repository find method that has a parameter
                    annotated with Is, specifying the NotIn constraint.
                    """)
    public void testIsNotIn() {

        List<String> found;
        found = countries.outsideOfRegions(List.of(Region.AFRICA,
                                                   Region.ASIA,
                                                   Region.CARIBBEAN,
                                                   Region.EUROPE,
                                                   Region.NORTH_AMERICA,
                                                   Region.SOUTH_AMERICA))
                        .stream()
                        .map(Country::getName)
                        .sorted()
                        .collect(Collectors.toList());
        assertEquals(List.of("American Samoa",
                             "Australia",
                             "Fiji",
                             "Guam",
                             "Kiribati",
                             "Marshall Islands",
                             "Micronesia",
                             "Nauru",
                             "New Caledonia",
                             "New Zealand",
                             "Northern Mariana Islands",
                             "Samoa",
                             "Solomon Islands",
                             "Tonga",
                             "Tuvalu",
                             "Vanuatu"),
                     found);
    }

    @Assertion(id = "965", strategy = """
                    Use a repository find method that has a parameter
                    annotated with Is, specifying the NotLike constraint.
                    """)
    public void testIsNotLike() {

        try {
            List<String> found = countries.namedUnlike("%a%",
                                                       "%i%",
                                                       "%n%")
                            .map(Country::getName)
                            .collect(Collectors.toList());
            assertEquals(List.of("Comoros",
                                 "Cyprus",
                                 "Egypt",
                                 "Greece",
                                 "Jersey",
                                 "Kosovo",
                                 "Lesotho",
                                 "Luxembourg",
                                 "Morocco",
                                 "Peru",
                                 "Seychelles",
                                 "Togo",
                                 "Turkey"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of Like.
                // Column and Key-Value databases might not be capable of And.
                // Column and Key-Value databases might not be capable of sorting.
            } else {
                throw x;
            }
        }
    }

}
