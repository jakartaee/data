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

import static ee.jakarta.tck.data.framework.read.only.Region.AFRICA;
import static ee.jakarta.tck.data.framework.read.only.Region.ASIA;
import static ee.jakarta.tck.data.framework.read.only.Region.EUROPE;
import static ee.jakarta.tck.data.framework.read.only.Region.OCEANIA;
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
import ee.jakarta.tck.data.framework.read.only.City;
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
import jakarta.data.constraint.AtLeast;
import jakarta.data.constraint.AtMost;
import jakarta.data.constraint.Between;
import jakarta.data.constraint.EqualTo;
import jakarta.data.constraint.GreaterThan;
import jakarta.data.constraint.In;
import jakarta.data.constraint.LessThan;
import jakarta.data.constraint.Like;
import jakarta.data.constraint.NotBetween;
import jakarta.data.constraint.NotEqualTo;
import jakarta.data.constraint.NotIn;
import jakarta.data.constraint.NotLike;
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
                    parameter of type AtLeast.
                    """)
    public void testAtLeastConstraint() {
        try {
            List<String> found = countries.whereDaylightTimeStartsOnOrAfter(
                    AtLeast.min(LocalDate.of(2025, Month.APRIL, 25)))
                            .stream()
                            .map(c -> c.getName() +
                                      " @" + c.getDaylightTimeBegins())
                            .collect(Collectors.toList());
            assertEquals(List.of("Australia @2025-10-05",
                                 "Chile @2025-09-06",
                                 "Egypt @2025-04-25",
                                 "New Zealand @2025-09-28"),
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
            Use a repository find method that has a constraint
            parameter of type AtMost.
            """)
    public void testAtMostConstraint() {
        try {
            List<String> found = countries.countryCodesUpTo(AtMost.max("AM"))
                            .stream()
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("AD",
                                 "AE",
                                 "AF",
                                 "AG",
                                 "AL",
                                 "AM"),
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
            Use a repository find method that has constraint
            parameters of type EqualTo.
            """)
    public void testEqualToConstraint() {

        Country canada = countries.withCountryCode(
                EqualTo.value("CA"))
                .orElseThrow();

        assertEquals("CA",
                     canada.getCode());
        assertEquals("Canada",
                     canada.getName());
        assertEquals(Region.NORTH_AMERICA,
                     canada.getRegion());
        assertEquals(9093507L,
                     canada.getArea());
        assertEquals(39187155L,
                     canada.getPopulation());
        assertEquals(2242000000000L,
                     canada.getGdp());
        assertEquals(1394524000000L,
                     canada.getDebt());
        assertEquals(LocalDate.of(2025, Month.MARCH, 9),
                     canada.getDaylightTimeBegins());
        assertEquals(LocalDate.of(2025, Month.NOVEMBER, 2),
                     canada.getDaylightTimeEnds());
        assertEquals("Ottawa",
                     canada.getCapital().getName());
        assertEquals(1017449,
                     canada.getCapital().getPopulation());
    }

    @Assertion(id = "965", strategy = """
                    Use a repository find method that has a constraint
                    parameter of type GreaterThan.
               """)
    public void testGreaterThanConstraint() {

        try {
            List<String> found = countries
                    .withCapitalBiggerThan(GreaterThan.bound(1000000))
                            .stream()
                            .map(Country::getCapital)
                            .map(capital -> capital.getName() +
                                            " population " +
                                            capital.getPopulation())
                            .collect(Collectors.toList());
            assertEquals(List.of("Ankara population 5747325",
                                 "Singapore population 5917600",
                                 "Santiago population 6310000",
                                 "Bogota population 7181469",
                                 "Hong Kong population 7534200",
                                 "Riyadh population 7676654",
                                 "Baghdad population 7682136",
                                 "Hanoi population 8053663",
                                 "Bangkok population 8305218",
                                 "Tehran population 8693706",
                                 "Dhaka population 8906039",
                                 "London population 9002488",
                                 "Mexico City population 9209944",
                                 "Seoul population 9508451",
                                 "Cairo population 10107125",
                                 "Lima population 10151000",
                                 "Jakarta population 10562088",
                                 "Moscow population 13104177",
                                 "Tokyo population 14094034",
                                 "Beijing population 21858000"),
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
            Use a repository find method that has a constraint
            parameter of type In.
            """)
    public void testInConstraint() {

        List<String> found;
        found = countries.namedAnyOf(
                In.values(Set.of("Chile",
                                 "Cyprus",
                                 "Croatia")))
                        .stream()
                        .map(Country::getCode)
                        .sorted()
                        .collect(Collectors.toList());
        assertEquals(List.of("CL",
                             "CY",
                             "HR"),
                     found);

        found = countries.namedAnyOf(
                In.values("Sierra Leone",
                          "Senegal",
                          "San Marino"))
                        .stream()
                        .map(Country::getCode)
                        .sorted()
                        .collect(Collectors.toList());
        assertEquals(List.of("SL",
                             "SM",
                             "SN"),
                     found);
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

    @Assertion(id = "965", strategy = """
            Use a repository find method that has a constraint
            parameter of type LessThan.
            """)
    public void testLessThanConstraint() {
        try {
            List<String> found = countries.lessPopulousThan(
                    LessThan.bound(10000L))
                            .stream()
                            .map(c -> c.getName() +
                                      " population " + c.getPopulation())
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("Nauru population 9930",
                                 "Saint Pierre and Miquelon population 5070",
                                 "Vatican City State population 764"),
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
            Use a repository find method that has a constraint
            parameter of type Like. Supply a pattern that has
            custom wildcard characters.
            """)
    public void testLikeConstraintCustomWildcards() {

        try {
            List<String> found = countries.withCapitalNamed(
                    Like.pattern("1akar#", '1', '#'))
                            .stream()
                            .map(Country::getCapital)
                            .map(City::getName)
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("Dakar",
                                 "Jakarta"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of Like.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
            Use a repository find method that has a constraint
            parameter of type Like. Supply a pattern that has
            custom wildcard characters and an escape character.
            """)
    public void testLikeConstraintCustomWildcardsAndEscape() {

        try {
            List<String> found = countries.withCapitalNamed(
                    Like.pattern("Port$--!$", '!', '$', '-'))
                            .stream()
                            .map(Country::getCapital)
                            .map(City::getName)
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("Port-au-Prince",
                                 "Porto-Novo"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of Like.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
            Use a repository find method that has a constraint
            parameter of type Like. Supply a literal.
            """)
    public void testLikeConstraintLiteral() {

        try {
            List<String> found = countries.withCapitalNamed(
                    Like.literal("Warsaw"))
                            .stream()
                            .map(Country::getName)
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("Poland"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of Like.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
            Use a repository find method that has a constraint
            parameter of type Like. Supply a pattern that uses
            the standard _ and % wildcard characters.
            """)
    public void testLikeConstraintPattern() {

        try {
            List<String> found = countries.withCapitalNamed(
                    Like.pattern("_all%"))
                            .stream()
                            .map(Country::getCapital)
                            .map(City::getName)
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("Tallinn",
                                 "Valletta"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of Like.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
            Use a repository find method that has a constraint
            parameter of type Like. Supply a prefix.
            """)
    public void testLikeConstraintPrefix() {

        try {
            List<String> found = countries.withCapitalNamed(
                    Like.prefix("San"))
                            .stream()
                            .map(Country::getCapital)
                            .map(City::getName)
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("San Jose",
                                 "San Juan",
                                 "San Salvador",
                                 "Sanaa",
                                 "Santiago",
                                 "Santo Domingo"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of Like.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
            Use a repository find method that has a constraint
            parameter of type Like. Supply a substring.
            """)
    public void testLikeConstraintSubstring() {

        try {
            List<String> found = countries.withCapitalNamed(
                    Like.substring("ey"))
                            .stream()
                            .map(Country::getCapital)
                            .map(City::getName)
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("Niamey",
                                 "Reykjavík"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of Like.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
            Use a repository find method that has a constraint
            parameter of type Like. Supply a suffix.
            """)
    public void testLikeConstraintSuffix() {

        try {
            List<String> found = countries.withCapitalNamed(
                    Like.suffix("ila"))
                            .stream()
                            .map(Country::getCapital)
                            .map(City::getName)
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("Manila",
                                 "Port Vila"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of Like.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
            Use a repository find method that has a constraint
            parameter of type NotBetween.
            """)
    public void testNotBetweenConstraint() {

        try {
            List<String> found = countries.excludingCountryCodeRange(
                    NotBetween.bounds("AG", "UY"))
                            .stream()
                            .map(Country::getCode)
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("AD",
                                 "AE",
                                 "AF",
                                 "UZ",
                                 "VA",
                                 "VC",
                                 "VE",
                                 "VN",
                                 "VU",
                                 "WS",
                                 "XK",
                                 "XW",
                                 "YE",
                                 "ZA",
                                 "ZM",
                                 "ZW"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.KEY_VALUE)) {
                // Key-Value databases might not be capable of NotBetween.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
            Use a repository find method that has constraint
            parameters of type NotEqualTo.
            """)
    public void testNotEqualToConstraint() {

        try {
            List<String> found = countries.outsideOfTheseRegions(
                    NotEqualTo.value(Region.AFRICA),
                    NotEqualTo.value(Region.ASIA),
                    NotEqualTo.value(Region.EUROPE),
                    NotEqualTo.value(Region.NORTH_AMERICA),
                    NotEqualTo.value(Region.OCEANIA),
                    NotEqualTo.value(Region.SOUTH_AMERICA))
                            .stream()
                            .map(Country::getName)
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("Antigua and Barbuda",
                                 "Aruba",
                                 "Bahamas",
                                 "Barbados",
                                 "Cuba",
                                 "Dominica",
                                 "Dominican Republic",
                                 "Grenada",
                                 "Haiti",
                                 "Jamaica",
                                 "Puerto Rico",
                                 "Saint Kitts and Nevis",
                                 "Saint Lucia",
                                 "Saint Vincent and the Grenadines",
                                 "Trinidad and Tobago",
                                 "Turks and Caicos Islands"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // Column and Key-Value databases might not be capable of And.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
            Use a repository find method that has constraint
            parameters of type NotIn.
            """)
    public void testNotInConstraint() {

        List<String> found;
        found = countries.notInRegions(
                NotIn.values(Set.of(Region.AFRICA,
                                    Region.ASIA,
                                    Region.CARIBBEAN,
                                    Region.EUROPE,
                                    Region.OCEANIA,
                                    Region.SOUTH_AMERICA)))
                        .stream()
                        .map(Country::getName)
                        .sorted()
                        .collect(Collectors.toList());
        assertEquals(List.of("Belize",
                             "Bermuda",
                             "Canada",
                             "Costa Rica",
                             "El Salvador",
                             "Greenland",
                             "Guatemala",
                             "Honduras",
                             "Mexico",
                             "Nicaragua",
                             "Panama",
                             "Saint Pierre and Miquelon",
                             "United States of America"),
                     found);

        found = countries.notInRegions(
                NotIn.values(Region.ASIA,
                             Region.CARIBBEAN,
                             Region.EUROPE,
                             Region.NORTH_AMERICA,
                             Region.OCEANIA,
                             Region.SOUTH_AMERICA))
                        .stream()
                        .map(Country::getName)
                        .sorted()
                        .collect(Collectors.toList());
        assertEquals(List.of("Algeria",
                             "Angola",
                             "Benin",
                             "Botswana",
                             "Burkina Faso",
                             "Burundi",
                             "Cameroon",
                             "Cape Verde",
                             "Central African Republic",
                             "Chad",
                             "Comoros",
                             "Congo",
                             "Djibouti",
                             "Egypt",
                             "Equatorial Guinea",
                             "Eritrea",
                             "Eswatini",
                             "Ethiopia",
                             "Gabon",
                             "Gambia",
                             "Ghana",
                             "Guinea",
                             "Guinea-Bissau",
                             "Ivory Coast",
                             "Kenya",
                             "Lesotho",
                             "Liberia",
                             "Libya",
                             "Madagascar",
                             "Malawi",
                             "Mali",
                             "Mauritania",
                             "Mauritius",
                             "Morocco",
                             "Mozambique",
                             "Namibia",
                             "Niger",
                             "Nigeria",
                             "Rwanda",
                             "Sao Tome and Principe",
                             "Senegal",
                             "Seychelles",
                             "Sierra Leone",
                             "Somalia",
                             "South Africa",
                             "South Sudan",
                             "Sudan",
                             "Tanzania",
                             "Togo",
                             "Tunisia",
                             "Uganda",
                             "Zambia",
                             "Zimbabwe"),
                     found);
    }

    @Assertion(id = "965", strategy = """
            Use a repository find method that has constraint
            parameters of type NotLike. Supply a pattern that has
            custom wildcard characters.
            """)
    public void testNotLikeConstraintCustomWildcards() {

        try {
            List<String> found = countries.excludingNames(
                    // exclude names with 5 or more characters
                    NotLike.pattern("*-----*", '-', '*'))
                            .stream()
                            .map(Country::getName)
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("Chad",
                                 "Cuba",
                                 "Fiji",
                                 "Guam",
                                 "Iran",
                                 "Iraq",
                                 "Laos",
                                 "Mali",
                                 "Oman",
                                 "Peru",
                                 "Togo"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of NotLike.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
            Use a repository find method that has constraint
            parameters of type NotLike. Supply a pattern that has
            custom wildcard characters and an escape character.
            """)
    public void testNotLikeConstraintCustomWildcardsAndEscape() {

        try {
            List<String> found = countries.excludingNames(
                    // intentionally reversed so that % is the single character
                    // wildcard and _ is the multiple character wildcard.
                    NotLike.pattern("_%aa_\"", '%', '_', 'a'))
                            .stream()
                            .map(Country::getName)
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of("Belgium",
                                 "Belize",
                                 "Benin",
                                 "Brunei",
                                 "Burundi",
                                 "Chile",
                                 "Comoros",
                                 "Congo",
                                 "Cyprus",
                                 "Czech Republic",
                                 "Djibouti",
                                 "Egypt",
                                 "Fiji",
                                 "Greece",
                                 "Guernsey",
                                 "Hong Kong",
                                 "Jersey",
                                 "Kosovo",
                                 "Lesotho",
                                 "Liechtenstein",
                                 "Luxembourg",
                                 "Mexico",
                                 "Montenegro",
                                 "Morocco",
                                 "Niger",
                                 "Peru",
                                 "Philippines",
                                 "Puerto Rico",
                                 "Seychelles",
                                 "Sweden",
                                 "Togo",
                                 "Turkey",
                                 "United Kingdom",
                                 "Yemen"),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of NotLike.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
            Use a repository find method that has constraint
            parameters of type NotLike. Supply a literal.
            """)
    public void testNotLikeConstraintLiteral() {

        try {
            List<String> found = countries.excludingNames(
                    NotLike.literal("Luxembourg"))
                    .stream()
                    .map(Country::getName)
                    .collect(Collectors.toList());

            assertEquals(CountryPopulator.EXPECTED_TOTAL - 1,
                         found.size());
            assertEquals(false, found.contains("Luxembourg"));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of NotLike.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
            Use a repository find method that has constraint
            parameters of type NotLike. Supply a pattern that uses
            the standard _ and % wildcard characters.
            """)
    public void testNotLikeConstraintPattern() {

        try {
            List<String> found = countries.excludingNames(
                    // exclude names with 4 or more characters
                    NotLike.pattern("%____%"))
                            .stream()
                            .map(Country::getName)
                            .sorted()
                            .collect(Collectors.toList());
            assertEquals(List.of(),
                         found);
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of NotLike.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
            Use a repository find method that has constraint
            parameters of type NotLike. Supply a prefix.
            """)
    public void testNotLikeConstraintPrefix() {

        try {
            List<String> found = countries.excludingNames(
                    NotLike.prefix("Ma"))
                            .stream()
                            .map(Country::getName)
                            .collect(Collectors.toList());
            assertEquals(CountryPopulator.EXPECTED_TOTAL - 10,
                         found.size());
            assertEquals(false, found.contains("Macau"));
            assertEquals(false, found.contains("Madagascar"));
            assertEquals(false, found.contains("Malawi"));
            assertEquals(false, found.contains("Malaysia"));
            assertEquals(false, found.contains("Maldives"));
            assertEquals(false, found.contains("Mali"));
            assertEquals(false, found.contains("Malta"));
            assertEquals(false, found.contains("Marshall Islands"));
            assertEquals(false, found.contains("Mauritania"));
            assertEquals(false, found.contains("Mauritius"));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of NotLike.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
            Use a repository find method that has constraint
            parameters of type NotLike. Supply a substring.
            """)
    public void testNotLikeConstraintSubstring() {

        try {
            List<String> found = countries.excludingNames(
                    NotLike.substring("an"))
                    .stream()
                    .map(Country::getName)
                    .collect(Collectors.toList());

            assertEquals(CountryPopulator.EXPECTED_TOTAL - 60,
                         found.size());
            assertEquals(false, found.contains("Rwanda"));
            assertEquals(false, found.contains("South Sudan"));
            assertEquals(false, found.contains("Tanzania"));
            assertEquals(false, found.contains("Uganda"));
            assertEquals(false, found.contains("Vanuatu"));
            // ... 55 more
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of NotLike.
            } else {
                throw x;
            }
        }
    }

    @Assertion(id = "965", strategy = """
            Use a repository find method that has constraint
            parameters of type NotLike. Supply a suffix.
            """)
    public void testNotLikeConstraintSuffix() {

        try {
            List<String> found = countries.excludingNames(
                    NotLike.suffix("land"))
                            .stream()
                            .map(Country::getName)
                            .collect(Collectors.toList());
            assertEquals(CountryPopulator.EXPECTED_TOTAL - 8,
                         found.size());
            assertEquals(false, found.contains("Finland"));
            assertEquals(false, found.contains("Greenland"));
            assertEquals(false, found.contains("Icland"));
            assertEquals(false, found.contains("Ireland"));
            assertEquals(false, found.contains("New Zealand"));
            assertEquals(false, found.contains("Poland"));
            assertEquals(false, found.contains("Switzerland"));
            assertEquals(false, found.contains("Thailand"));
        } catch (UnsupportedOperationException x) {
            if (type.isKeywordSupportAtOrBelow(DatabaseType.GRAPH)) {
                // NoSQL databases might not be capable of NotLike.
            } else {
                throw x;
            }
        }
    }

}
